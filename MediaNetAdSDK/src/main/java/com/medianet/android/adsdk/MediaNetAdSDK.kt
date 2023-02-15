package com.medianet.android.adsdk

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.app.analytics.AnalyticsSDK
import com.app.analytics.SamplingMap
import com.app.analytics.providers.AnalyticsProviderFactory
import com.app.logger.CustomLogger
import com.medianet.android.adsdk.events.EventManager
import com.medianet.android.adsdk.model.SdkConfiguration
import com.medianet.android.adsdk.model.StoredConfigs
import com.medianet.android.adsdk.network.*
import com.medianet.android.adsdk.utils.Util
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import org.prebid.mobile.Host
import org.prebid.mobile.LogUtil
import org.prebid.mobile.PrebidMobile
import org.prebid.mobile.TargetingParams
import org.prebid.mobile.api.exceptions.InitError
import org.prebid.mobile.rendering.listeners.SdkInitializationListener

/**
 * Point Of Contact for initializing the SDK
 * and setting various parameters while working with it
 */
object MediaNetAdSDK {

    const val TAG = "MediaNetAdSDK"
    const val TEMP_ACCOUNT_ID = "0689a263-318d-448b-a3d4-b02e8a709d9d" //TODO - should store in preference ?
    private const val HOST_URL = "https://prebid-server-test-j.prebid.org/openrtb2/auction" //TODO - should store in preference ?
    private const val CONFIG_BASE_URL = "http://ems-adserving-stage-1.traefik.internal.media.net/" //TODO - should store in preference ?
    private const val CID = "8CU5Z4D53" // Temp account Id for config call
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
    private val serverApiService: ServerApiService by lazy { NetworkComponentFactory.getServerApiService(CONFIG_BASE_URL) }
    private var config: SdkConfiguration? = null
    private val Context.configDataStore: DataStore<StoredConfigs.StoredSdkConfig> by dataStore(
        fileName = DATA_STORE_FILE_NAME,
        serializer = ConfigSerializer
    )
    private var configRepo: IConfigRepo? = null

    /**
     * initializes the SDK along with the fetch and validation of SDKConfig
     * @param applicationContext is the context of application where the SDK has been integrated
     * @param accountId is the publisher account ID
     * @param sdkInitListener listens to the SDK Initialization Result
     */
    fun init(
        applicationContext : Context,
        accountId: String,
        sdkInitListener: MSdkInitListener? = null
    ) {
        configRepo = ConfigRepoImpl(serverApiService, applicationContext.configDataStore)
        coroutineScope.launch {
            LogUtil.setBaseTag(TAG)
            PrebidMobile.initializeSdk(applicationContext, prebidSdkInitializationListener)
            //TODO - that need to be come from customer
            setSubjectToGDPR(true)
            publisherSdkInitListener = sdkInitListener
            initialiseSdkConfig(applicationContext)
        }
    }

    /**
     * fetches sdk config for account ID
     * @param applicationContext is the context of application where the SDK has been integrated
      */
    internal suspend fun initialiseSdkConfig(applicationContext: Context) {
        configRepo?.getSDKConfigFlow()?.collectLatest { sdkConfig ->
            config = sdkConfig

            // We get null config when no config is stored in data store, so scheduling the fetch config from server
            if (config == null || config?.isConfigExpired() == true) {
                CustomLogger.debug(TAG, "fetching fresh config from server")
                fetchConfigFromServer(applicationContext)
            }

            //Disable SDK if kill switch is onn
            if (config?.shouldKillSDK == true) {
                CustomLogger.debug(TAG, "config kill switch is onn so disabling SDK functionality")
                sdkOnVacation = true
                return@collectLatest
            }
            config?.let {
                updateSDKConfigDependencies(applicationContext, it)
                initConfigExpiryTimer(applicationContext, it.configExpiryMillis)
            }
        }
    }

    /**
     * fetches config from server
     * @param context is the context of application where the SDK has been integrated
     */
    fun fetchConfigFromServer(context: Context) {
        coroutineScope.launch {
            configRepo?.refreshSdkConfig(CID, context)
        }
    }

    /**
     * schedules config fetch after config store expires
     * @param applicationContext is the context of application where the SDK has been integrated
     * @param expiry is the time in seconds after which config will be expired and config should be fetched from server
      */
    private fun initConfigExpiryTimer(applicationContext: Context, expiry: Long?) {
        expiry?.let { expiryInSeconds ->
            CustomLogger.debug(TAG, "refreshing config after $expiryInSeconds seconds")
            SDKConfigSyncWorker.scheduleConfigFetch(applicationContext, expiryInSeconds)
        }
    }

    /**
     * extracts data from config that will be required for auction call
     * @param applicationContext is the context of application where the SDK has been integrated
     * @param config is the sdk config which is mapped from SDK Config Response
      */

    private fun updateSDKConfigDependencies(applicationContext: Context, config: SdkConfiguration) {
        PrebidMobile.setPrebidServerAccountId(config.customerId)
        PrebidMobile.setPrebidServerHost(Host.createCustomHost(config.bidRequestUrl)) //PrebidMobile.setPrebidServerHost(Host.createCustomHost(HOST_URL))
//        PrebidMobile.setPrebidServerAccountId("0689a263-318d-448b-a3d4-b02e8a709d9d")
//        PrebidMobile.setPrebidServerHost(Host.createCustomHost("https://prebid-server-test-j.prebid.org/openrtb2/auction"))
        PrebidMobile.setTimeoutMillis(config.auctionTimeOutMillis.toInt())
        //Initialising Analytics
        initAnalytics(applicationContext, config)
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

    /**
     * sets stored auction response
     * @param storedAuctionResponse
     */
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

    /**
     * checks whether the google play service ads library version used in your application
     * matches with the version of google play service ads used in our SDK
     * @param version - MobileAds.getVersion().toString()
     */
    fun isCompatibleWithGoogleMobileAds(version: String): Boolean = PrebidMobile.checkGoogleMobileAdsCompatibility(version)

    /**
     * will share the users geo location in the ad request input if true is passed and vice versa
     * @param share is the boolean to decide whether geo location should be shared or not
     */
    fun shouldShareGeoLocation(share: Boolean) = apply { PrebidMobile.setShareGeoLocation(share) }

    /**
     * tells whether geo location of the user is being shared in the ad request input
     */
    fun isSharingGeoLocation() = PrebidMobile.isShareGeoLocation()

    /**
     * Sets subject to GDPR for MediaNetAdSdk. It uses custom static field, not IAB. <br><br>
     * @param enable allows the ability to provide consent
     * Must be called only after MediaNetAdSdk.init(applicationContext, accountId, sdkInitListener).
     */
    fun setSubjectToGDPR(enable: Boolean) = apply { TargetingParams.setSubjectToGDPR(enable) }

    /**
     * initializes Analytics SDK to be used across the MediaNetAdSDK
     * @param applicationContext is the context of application where the SDK has been integrated
     * @param sdkConfig is the sdk config which is mapped from SDK Config Response
      */
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

    /**
     * This method is being used by every Ad class before any functioning
      */
    fun isSdkOnVacation() = sdkOnVacation

    //TODO - when to call this
    fun clear() {
        AnalyticsSDK.clear()
        coroutineScope.coroutineContext.cancelChildren()
        config = null
        SDKConfigSyncWorker.cancelSDKConfigSync(PrebidMobile.getApplicationContext())
        EventManager.clear()
    }

    /**
     * Gets any given subject to GDPR in that order. <br>
     * 1) MediaNetAdSdk subject to GDPR custom value, if present. <br>
     * 2) IAB subject to GDPR TCF 2.0. <br>
     * Otherwise, null.
     *
     * Must be called only after MediaNetAdSdk.init(applicationContext, accountId, sdkInitListener)
     */
    fun isSubjectToGDPR(): Boolean? {
        return TargetingParams.isSubjectToGDPR()
    }

    /**
     * Sets GDPR consent for MediaNetAdSdk. It uses custom static field, not IAB.
     * @param consentString for GDPR
     * Must be called only after MediaNetAdSdk.init(applicationContext, accountId, sdkInitListener)
     */
    fun setGDPRConsentString(consentString: String?) = apply {
        TargetingParams.setGDPRConsentString(consentString)
    }
}
