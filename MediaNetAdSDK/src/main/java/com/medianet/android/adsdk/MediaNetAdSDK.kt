package com.medianet.android.adsdk

import android.content.Context
import com.app.analytics.AnalyticsSDK
import com.app.analytics.SamplingMap
import com.app.analytics.providers.AnalyticsProviderFactory
import com.medianet.android.adsdk.events.EventManager
import com.medianet.android.adsdk.model.ConfigResponse
import kotlinx.coroutines.*
import com.app.logger.CustomLogger
import com.medianet.android.adsdk.network.*
import org.prebid.mobile.Host
import org.prebid.mobile.LogUtil
import org.prebid.mobile.PrebidMobile
import org.prebid.mobile.TargetingParams
import org.prebid.mobile.api.exceptions.InitError
import org.prebid.mobile.rendering.listeners.SdkInitializationListener
import com.medianet.android.adsdk.utils.Util

object MediaNetAdSDK {

    const val TAG = "MediaNetAdSDK"
    const val TEMP_ACCOUNT_ID = "0689a263-318d-448b-a3d4-b02e8a709d9d" //TODO - should store in preference ?
    private const val HOST_URL = "https://prebid-server-test-j.prebid.org/openrtb2/auction" //TODO - should store in preference ?
    private const val CONFIG_BASE_URL = "http://ems-adserving-stage-1.traefik.internal.media.net/" //TODO - should store in preference ?
    private const val CID = "8CU5Z4D53" // Temp account Id for config call
    private var sdkOnVacation: Boolean = false
    val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var logLevel: MLogLevel = MLogLevel.INFO
    // TODO - For some action we can check if test mode is on or not
    private var isTestMode: Boolean = false

    private var publisherSdkInitListener: MSdkInitListener? = null
    private var prebidSdkInitializationListener: SdkInitializationListener = object : SdkInitializationListener  {
        override fun onSdkInit() {
            CustomLogger.debug(TAG, "SDK initialized successfully!")
            // If we need to send event for SDK initialisation we can do here
            publisherSdkInitListener?.onInitSuccess()
        }

        override fun onSdkFailedToInit(error: InitError?) {
            CustomLogger.error(TAG, "SDK initialization error: " + error?.error)
            val sdkInitError = Error.SDK_INIT_ERROR.apply {
                errorMessage = error?.error.toString()
            }
            publisherSdkInitListener?.onInitFailed(sdkInitError)
        }

    }
    private val serverApiService: ServerApiService by lazy { NetworkComponentFactory.getServerApiService(CONFIG_BASE_URL) }
    private val configRepo: IConfigRepo = ConfigRepoImpl(serverApiService)
    private var config: Configuration? = null

    fun initPrebidSDK(
        applicationContext : Context,
        accountId: String,
        sdkInitListener: MSdkInitListener? = null
    ) {
        coroutineScope.launch {
            LogUtil.setBaseTag(TAG)
            initialiseSdkConfig(applicationContext)
            publisherSdkInitListener = sdkInitListener
            PrebidMobile.initializeSdk(applicationContext, prebidSdkInitializationListener)
            //TODO - that need to be come from customer
            TargetingParams.setSubjectToGDPR(true)
        }
    }

    internal suspend fun initialiseSdkConfig(applicationContext: Context) {
        CustomLogger.debug(TAG, "fetching config from server")
        val configFromServer = getConfigFromServer(CID) //TODO - replace it with account ID provided by publisher
        config = getSDKConfig(configFromServer) ?: config

        //Stopping the SDK initialisation process if config is null (can be due to API Failure)
        // scheduling the fetch config
        if(config == null) {
            initConfigExpiryTimer(applicationContext, 120L)
            return
        }

        //Disable SDK if kill switch is onn
        if (config?.shouldKillSDK == true) {
            sdkOnVacation = true
            return
        }
        config?.let {
            updateSDKConfigDependencies(applicationContext, it)
            initConfigExpiryTimer(applicationContext, it.configExpiry)
        }
    }

    private suspend fun initConfigExpiryTimer(applicationContext: Context, expiry: Long?) = withContext(Dispatchers.IO) {
        expiry?.let { expiryInSeconds ->
            CustomLogger.debug(TAG, "refreshing config after $expiryInSeconds seconds")
            SDKConfigSyncWorker.scheduleConfigFetch(applicationContext, expiryInSeconds)
        }
    }

    private fun getSDKConfig(data: ConfigResponse?): MediaNetAdSDK.Configuration? {
        data?.let {
            val crIdMap = mutableMapOf<String, String>()
            it.crIds.map { item ->
                crIdMap.put(item.dfpAdUnitId, item.crId)
            }
            return Configuration(
                customerId = data.pub.cId,
                partnerId = data.pub.partnerId,
                domainName = data.targeting.domainName,
                countryCode = data.targeting.countryCode,
                auctionTimeOutMillis = it.timeout.auctionTimeout.toLong(),
                projectEventPercentage = it.logPercentage.projectEvent.toInt(),
                opportunityEventPercentage = it.logPercentage.opportunityEvent.toInt(),
                shouldKillSDK = it.publisherConfig.killSwitch,
                bidRequestUrl = it.urls.auctionLayerUrl,
                projectEventUrl = it.urls.projectEventUrl,
                opportunityEventUrl = it.urls.opportunityEventUrl,
                dpfToCrIdMap = crIdMap,
                dummyCCrId = data.dummyCrId.crId,
                configExpiry = data.globalConfig.configExpiryInSec
            )
        }
        return null
    }

    private fun updateSDKConfigDependencies(applicationContext: Context, config: Configuration) {
        PrebidMobile.setPrebidServerAccountId(config.customerId)
        PrebidMobile.setPrebidServerHost(Host.createCustomHost(config.bidRequestUrl)) //PrebidMobile.setPrebidServerHost(Host.createCustomHost(HOST_URL))
        //PrebidMobile.setPrebidServerAccountId("0689a263-318d-448b-a3d4-b02e8a709d9d")
        //PrebidMobile.setPrebidServerHost(Host.createCustomHost("https://prebid-server-test-j.prebid.org/openrtb2/auction"))
        PrebidMobile.setTimeoutMillis(config.auctionTimeOutMillis.toInt())
        //Initialising Analytics
        initAnalytics(applicationContext, config)
    }

    private suspend fun getConfigFromServer(accountId: String): ConfigResponse? {
        val configResult = configRepo.getSDKConfig(accountId)
        return if (configResult.isSuccess) {
            configResult.successValue()
        } else {
            CustomLogger.error(TAG, "config call fails: ${configResult.errorValue()?.errorModel?.errorMessage}")
            null
        }
    }

    // TODO - publisherAccountId is better name?
    fun getAccountId() = PrebidMobile.getPrebidServerAccountId()
    fun setAccountId(accountId: String) = apply { PrebidMobile.setPrebidServerAccountId(accountId) }

    fun getPrebidServerHost() = HOST_URL

    fun setTimeoutMillis(timeoutMillis: Long) = apply { PrebidMobile.setTimeoutMillis(timeoutMillis.toInt()) }
    fun getTimeOutMillis() = PrebidMobile.getTimeoutMillis()

    //TODO - should expose PrebidMobile.setCustomHeaders()

    //TODO - should expose PrebidMobile.getApplicationContext()
    fun enableTestMode() = apply {
        isTestMode = true
        PrebidMobile.setPbsDebug(true)
    }
    fun disableTestMode() = apply {
        isTestMode = false
        PrebidMobile.setPbsDebug(false)
    }
    fun isDebugMode(): Boolean {
        //return isTestMode
        return PrebidMobile.getPbsDebug()
    }

    fun setStoredAuctionResponse(storedAuctionResponse: String? = null) = apply {
        storedAuctionResponse?.let { storedResponse ->
            PrebidMobile.setStoredAuctionResponse(storedResponse)
        }
    }

    //TODO - should expose PrebidMobile.addStoredBidResponse()

    fun setLogLevel(level: MLogLevel) = apply {
        logLevel = level
        PrebidMobile.setLogLevel(Util.mapLogLevelToPrebidLogLevel(level))
    }

    fun getLogLevel() = logLevel

    fun isCompatibleWithGoogleMobileAds(version: String) = PrebidMobile.checkGoogleMobileAdsCompatibility(version)

    fun shouldShareGeoLocation(share: Boolean) = apply { PrebidMobile.setShareGeoLocation(share) }
    fun isSharingGeoLocation() = PrebidMobile.isShareGeoLocation()

    fun setSubjectToGDPR(enable: Boolean) = apply { TargetingParams.setSubjectToGDPR(enable) }

    private fun initAnalytics(applicationContext: Context, sdkConfig: Configuration) {
        EventManager.init(sdkConfig)
        val samplingMap = SamplingMap()
        samplingMap.put(LoggingEvents.PROJECT.type, sdkConfig.projectEventPercentage)
        samplingMap.put(LoggingEvents.OPPORTUNITY.type, sdkConfig.opportunityEventPercentage)

        val configuration = AnalyticsSDK.Configuration.Builder()
            .setAnalyticsUrl("") // There are multiple type of events with different base url so setting base url for SDK as empty, we will send url in event itself
            .enableEventSampling(false, samplingMap)
            .build()

        //TODO we get interval minute from config, need to update code for this use case, currently we only sync immediately
        val analyticsProvider = AnalyticsProviderFactory.getCachedProvider(applicationContext, sdkConfig.projectEventUrl, 0)
        AnalyticsSDK.init(applicationContext, configuration, providers = listOf(analyticsProvider))
    }

    // This method is being used by every Ad class before any functioning
    fun isSdkOnVacation() = sdkOnVacation

    //TODO - when to call this
    fun clear() {
        AnalyticsSDK.clear()
        coroutineScope.coroutineContext.cancelChildren()
        config = null
        SDKConfigSyncWorker.cancelSDKConfigSync(PrebidMobile.getApplicationContext())
        EventManager.clear()
    }

    fun isSubjectToGDPR(): Boolean? {
        return TargetingParams.isSubjectToGDPR()
    }

    fun setGDPRConsentString(consentString: String?) = apply {
        TargetingParams.setGDPRConsentString(consentString)
    }

    data class Configuration(
        val customerId: String,
        val partnerId: String,
        val domainName: String,
        val countryCode: String,
        val auctionTimeOutMillis: Long,
        val dpfToCrIdMap: MutableMap<String, String>,
        val dummyCCrId: String,
        val eventsBufferInterval: Long = 0,
        val projectEventPercentage: Int,
        val opportunityEventPercentage: Int,
        val shouldKillSDK: Boolean,
        val bidRequestUrl: String,
        val projectEventUrl: String,
        val opportunityEventUrl: String,
        val sdkVersion: String = BuildConfig.VERSION_NAME,
        val configExpiry: Long? = null
    ) {

        fun getCrId(dfpAdId: String): String {
            val id =  dpfToCrIdMap[dfpAdId] ?: dummyCCrId
            return id
        }
    }
}
