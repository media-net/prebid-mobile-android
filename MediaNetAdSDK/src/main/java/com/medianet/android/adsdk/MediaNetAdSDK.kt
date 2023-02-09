package com.medianet.android.adsdk

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.app.analytics.AnalyticsSDK
import com.app.analytics.SamplingMap
import com.app.analytics.providers.AnalyticsProviderFactory
import com.app.network.RetryPolicy
import com.app.network.wrapper.safeApiCall
import com.medianet.android.adsdk.events.EventManager
import com.medianet.android.adsdk.model.ConfigResponse
import com.medianet.android.adsdk.network.NetworkComponentFactory
import com.medianet.android.adsdk.network.ServerApiService
import kotlinx.coroutines.*
import com.app.logger.CustomLogger
import com.medianet.android.adsdk.model.SdkConfiguration
import com.medianet.android.adsdk.model.StoredConfigs.StoredSdkConfig
import org.prebid.mobile.Host
import org.prebid.mobile.LogUtil
import org.prebid.mobile.PrebidMobile
import org.prebid.mobile.TargetingParams
import org.prebid.mobile.api.exceptions.InitError
import org.prebid.mobile.rendering.listeners.SdkInitializationListener
import com.medianet.android.adsdk.utils.Constants.KEY_CC
import com.medianet.android.adsdk.utils.Constants.KEY_DN
import com.medianet.android.adsdk.utils.Constants.KEY_UGD
import com.medianet.android.adsdk.utils.Constants.VALUE_MOBILE
import com.medianet.android.adsdk.utils.Constants.VALUE_US
import com.medianet.android.adsdk.utils.Util
import kotlinx.coroutines.flow.collectLatest

object MediaNetAdSDK {

    const val TAG = "MediaNetAdSDK"
    const val TEMP_ACCOUNT_ID = "0689a263-318d-448b-a3d4-b02e8a709d9d" //TODO - should store in preference ?
    private const val HOST_URL = "https://prebid-server-test-j.prebid.org/openrtb2/auction" //TODO - should store in preference ?
    private const val CONFIG_BASE_URL = "http://ems-adserving-stage-1.traefik.internal.media.net/" //TODO - should store in preference ?
    private const val CID = "8CU15Y85L" // Temp account Id for config call
    private var sdkOnVacation: Boolean = false
    val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    const val DATA_STORE_FILE_NAME = "sdk_config.pb"
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
    private var serverApiService: ServerApiService? = NetworkComponentFactory.getServerApiService(CONFIG_BASE_URL)
    private var config: SdkConfiguration? = null

    private val Context.configDataStore: DataStore<StoredSdkConfig> by dataStore(
        fileName = DATA_STORE_FILE_NAME,
        serializer = ConfigSerializer
    )

    private var configRepository: ConfigRepository? = null

    fun initPrebidSDK(
        applicationContext : Context,
        accountId: String,
        sdkInitListener: MSdkInitListener? = null
    ) {
        coroutineScope.launch {
            LogUtil.setBaseTag(TAG)
            configRepository = ConfigRepository(applicationContext.configDataStore)
            initObservers()
            val configFromServer = getConfigFromServer(CID) //TODO - replace it with account ID provided by publisher
            storeConfigToDataStore(configFromServer)

            //Disable SDK if kill switch is onn
            if (config == null || config?.shouldKillSDK == true) {
                sdkOnVacation = true
                return@launch
            }
            config?.let {
                updateSDKConfigDependencies(applicationContext, it)
            }
            publisherSdkInitListener = sdkInitListener
            PrebidMobile.initializeSdk(applicationContext, prebidSdkInitializationListener)
            //TODO - that need to be come from customer
            TargetingParams.setSubjectToGDPR(true)
        }
    }

    private suspend fun storeConfigToDataStore(configFromServer: ConfigResponse?) {
        val sdkConfig = getSDKConfig(configFromServer)
        sdkConfig?.let {
            configRepository?.updateSdkConfig(it)
        }
    }

    private fun initObservers() {
        initStoredSdkConfigObserver()
    }

    private fun initStoredSdkConfigObserver() {
        coroutineScope.launch {
            configRepository?.getConfigFlow()?.collectLatest { storedConfig ->
                config = Util.storedConfigToSdkConfig(storedConfig)
                Log.e("Nikhil", "$config")
            }
        }
    }

    private fun getSDKConfig(data: ConfigResponse?): SdkConfiguration? {
        data?.let {
            val crIdMap = mutableMapOf<String, String>()
            it.crIds.map { item ->
                crIdMap.put(item.dfpAdUnitId, item.crId)
            }
            return SdkConfiguration(
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
                dummyCCrId = data.dummyCrId.crId
            )
        }
        return null
    }

    private suspend fun updateSDKConfigDependencies(applicationContext: Context, config: SdkConfiguration) {
        PrebidMobile.setPrebidServerAccountId(config.customerId)
        PrebidMobile.setPrebidServerHost(Host.createCustomHost(config.bidRequestUrl)) //PrebidMobile.setPrebidServerHost(Host.createCustomHost(HOST_URL))
//        PrebidMobile.setPrebidServerAccountId("0689a263-318d-448b-a3d4-b02e8a709d9d")
//        PrebidMobile.setPrebidServerHost(Host.createCustomHost("https://prebid-server-test-j.prebid.org/openrtb2/auction"))
        PrebidMobile.setTimeoutMillis(config.auctionTimeOutMillis.toInt())
        //Initialising Analytics
        initAnalytics(applicationContext, config)
    }

    private suspend fun getConfigFromServer(accountId: String): ConfigResponse? {
        val configParams = mapOf(
            KEY_CC to VALUE_US,
            KEY_DN to BuildConfig.LIBRARY_PACKAGE_NAME,
            KEY_UGD to VALUE_MOBILE
        )
        val configResult = safeApiCall(
            apiCall = {
                serverApiService?.getSdkConfig(accountId, configParams)
            },
            successTransform = {
                it
            },
            retryPolicy = RetryPolicy(maxTries = 0)
        )
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

    private fun initAnalytics(applicationContext: Context, sdkConfig: SdkConfiguration) {
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
        serverApiService = null
        EventManager.clear()
        configRepository = null
    }

    fun isSubjectToGDPR(): Boolean? {
        return TargetingParams.isSubjectToGDPR()
    }

    fun setGDPRConsentString(consentString: String?) = apply {
        TargetingParams.setGDPRConsentString(consentString)
    }
}
