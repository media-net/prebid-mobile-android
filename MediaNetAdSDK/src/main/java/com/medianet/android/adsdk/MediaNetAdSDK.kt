package com.medianet.android.adsdk

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.app.analytics.AnalyticsSDK
import com.app.analytics.SamplingMap
import com.app.analytics.providers.AnalyticsProviderFactory
import com.app.logger.CustomLogger
import com.medianet.android.adsdk.base.MLogLevel
import com.medianet.android.adsdk.base.listeners.MSdkInitListener
import com.medianet.android.adsdk.events.EventManager
import com.medianet.android.adsdk.events.LoggingEvents
import com.medianet.android.adsdk.model.StoredConfigs
import com.medianet.android.adsdk.model.sdkconfig.SdkConfiguration
import com.medianet.android.adsdk.network.ApiConstants.CONFIG_BASE_URL
import com.medianet.android.adsdk.network.NetworkComponentFactory
import com.medianet.android.adsdk.network.SDKConfigSyncWorker
import com.medianet.android.adsdk.network.ServerApiService
import com.medianet.android.adsdk.network.repository.ConfigRepoImpl
import com.medianet.android.adsdk.network.repository.IConfigRepo
import com.medianet.android.adsdk.utils.ConfigSerializer
import com.medianet.android.adsdk.utils.Constants
import com.medianet.android.adsdk.utils.Constants.DATA_STORE_FILE_NAME
import com.medianet.android.adsdk.utils.MapperUtils.mapLogLevelToPrebidLogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.prebid.mobile.Host
import org.prebid.mobile.LogUtil
import org.prebid.mobile.PrebidMobile
import org.prebid.mobile.TargetingParams
import org.prebid.mobile.api.exceptions.InitError
import org.prebid.mobile.rendering.listeners.SdkInitializationListener

/**
 * point of contact for initializing the sdk
 * and setting various parameters while working with it
 */
object MediaNetAdSDK {

    private const val TAG = "MediaNetAdSDK"

    private var sdkOnVacation: Boolean = false
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var logLevel: MLogLevel = MLogLevel.INFO
    private var accountId = ""

    private var publisherSdkInitListener: MSdkInitListener? = null
    private var prebidSdkInitializationListener: SdkInitializationListener =
        object : SdkInitializationListener {
            override fun onSdkInit() {
                CustomLogger.debug(TAG, "SDK initialized successfully!")
                // If we need to send event for SDK initialisation we can do here
                publisherSdkInitListener?.onInitSuccess()
            }

            override fun onSdkFailedToInit(error: InitError?) {
                CustomLogger.error(TAG, "SDK initialization error: " + error?.error)
                val sdkInitError = com.medianet.android.adsdk.base.Error.SDK_INIT_ERROR.apply {
                    errorMessage = error?.error.toString()
                }
                publisherSdkInitListener?.onInitFailed(sdkInitError)
            }

        }
    private val serverApiService: ServerApiService by lazy {
        NetworkComponentFactory.getServerApiService(
            CONFIG_BASE_URL
        )
    }
    private var config: SdkConfiguration? = null
    private val Context.configDataStore: DataStore<StoredConfigs.StoredSdkConfig> by dataStore(
        fileName = DATA_STORE_FILE_NAME,
        serializer = ConfigSerializer
    )
    private var configRepo: IConfigRepo? = null

    /**
     * initializes the SDK along with the fetch and validation of SDKConfig
     * @param applicationContext is the context of application where the sdk has been integrated
     * @param accountId is the publisher account id
     * @param sdkInitListener listens to the sdk initialization result
     */
    fun init(
        applicationContext: Context,
        accountId: String,
        sdkInitListener: MSdkInitListener? = null
    ) {
        configRepo = ConfigRepoImpl(serverApiService, applicationContext.configDataStore)
        coroutineScope.launch {
            LogUtil.setBaseTag(TAG)
            MediaNetAdSDK.accountId = accountId
            initialiseSdkConfig(applicationContext)
            // Removing this code as was only for tersting , in future we will take is as task which provide test feature
            /*if (accountId == Constants.TEST_CUSTOMER_ID) {
                PrebidMobile.setUserAgentParam(Constants.FORCE_BID_PARAM)
            }*/
            PrebidMobile.initializeSdk(applicationContext, prebidSdkInitializationListener)
            publisherSdkInitListener = sdkInitListener
        }
    }

    /**
     * fetches sdk config for account id from data store if present and not expired
     * if not, fetches it from server remotely
     * @param applicationContext is the context of application where the sdk has been integrated
     */
    private fun initialiseSdkConfig(applicationContext: Context) {
        coroutineScope.launch {
            configRepo?.getSDKConfigFlow()?.collectLatest { sdkConfig ->
                config = sdkConfig

                // We get null config when no config is stored in data store, so scheduling the fetch config from server
                if (config == null || config?.isConfigExpired() == true) {
                    CustomLogger.debug(TAG, "fetching fresh config from server")
                    fetchConfigFromServer(applicationContext)
                }

                //Disable sdk if kill switch is on
                if (config?.shouldKillSDK == true) {
                    CustomLogger.debug(
                        TAG,
                        "config kill switch is on so disabling SDK functionality"
                    )
                    sdkOnVacation = true
                    return@collectLatest
                }
                config?.let {
                    updateSDKConfigDependencies(applicationContext, it)
                    initConfigExpiryTimer(applicationContext, it.configExpiryMillis)
                }
            }
        }
    }

    /**
     * fetches config from server
     * @param context is the context of application where the sdk has been integrated
     */
    internal fun fetchConfigFromServer(context: Context) {
        coroutineScope.launch {
            configRepo?.refreshSdkConfig(accountId, context)
        }
    }

    /**
     * schedules config fetch after config store expires
     * @param applicationContext is the context of application where the sdk has been integrated
     * @param expiry is the time in seconds after which config will be expired and config should be fetched from server
     */
    private fun initConfigExpiryTimer(applicationContext: Context, expiry: Long?) {
        expiry?.let { expiryInSeconds ->
            CustomLogger.debug(TAG, "refreshing config after $expiryInSeconds seconds")
            SDKConfigSyncWorker.scheduleConfigFetch(applicationContext, expiryInSeconds)
        }
    }

    /**
     * initializes dependencies like server host, account id, bid request url, connection timeout time, analytics etc.  from config fetched from server
     * @param applicationContext is the context of application where the sdk has been integrated
     * @param config is the sdk config which is mapped from SDK Config Response
     */
    private fun updateSDKConfigDependencies(applicationContext: Context, config: SdkConfiguration) {
        PrebidMobile.setPrebidServerAccountId(config.customerId)
        PrebidMobile.setPrebidServerHost(Host.createCustomHost(config.bidRequestUrl))
        PrebidMobile.setTimeoutMillis(config.auctionTimeOutMillis.toInt())

        //Initialising Analytics
        initAnalytics(applicationContext, config)
    }

    fun getAccountId() = PrebidMobile.getPrebidServerAccountId()

    /**
     * sets in milliseconds, will return control to the ad server sdk to fetch an ad once the expiration period is achieved.
     * because MediaNetSdk sdk solicits bids from server in one payload, setting timeout too low can stymie all demand resulting in a potential negative revenue impact.
     */
    fun setTimeoutMillis(timeoutMillis: Long) =
        apply { PrebidMobile.setTimeoutMillis(timeoutMillis.toInt()) }

    fun getTimeOutMillis() = PrebidMobile.getTimeoutMillis()

    fun enableTestMode() = apply {
        PrebidMobile.setPbsDebug(true)
    }

    fun disableTestMode() = apply {
        PrebidMobile.setPbsDebug(false)
    }

    fun isDebugMode(): Boolean {
        return PrebidMobile.getPbsDebug()
    }

    /**
     * set as type string, stored auction responses signal server to respond with a static response matching the storedAuctionResponse found in the server database,
     * useful for debugging and integration testing.
     * no bid requests will be sent to any bidders when a matching storedAuctionResponse is found
     * @param storedAuctionResponse
     */
    fun setStoredAuctionResponse(storedAuctionResponse: String? = null) = apply {
        storedAuctionResponse?.let { storedResponse ->
            PrebidMobile.setStoredAuctionResponse(storedResponse)
        }
    }

    /**
     * Adds a Stored Bid Response. Stored Bid Responses are similar to Stored Auction Responses in that they signal to server to
     * respond with a static pre-defined response,
     * except Stored Bid Responses is done at the bidder level,
     * with bid requests sent out for any bidders not specified in the bidder parameter.
     * @param bidder
     * @param responseId
     */
    fun addStoredBidResponse(bidder: String, responseId: String) {
        PrebidMobile.addStoredBidResponse(bidder, responseId)
    }

    /**
     * Returns a map of all Stored Bid Responses.
     */
    fun getStoredBidResponses(): Map<String, String> {
        return PrebidMobile.getStoredBidResponses()
    }

    /**
     * Clears all the Stored Bid Responses
     */
    fun clearStoredBidResponses() {
        PrebidMobile.clearStoredBidResponses()
    }

    fun setLogLevel(level: MLogLevel) = apply {
        logLevel = level
        PrebidMobile.setLogLevel(level.mapLogLevelToPrebidLogLevel())
    }

    fun getLogLevel() = logLevel

    /**
     * checks whether the google play service ads library version used in your application
     * matches with the version of google play service ads used in our sdk
     * @param version - MobileAds.getVersion().toString()
     */
    fun isCompatibleWithGoogleMobileAds(version: String): Boolean =
        PrebidMobile.checkGoogleMobileAdsCompatibility(version)

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
     * sets subject to GDPR for MediaNetAdSdk. It uses custom static field, not IAB. <br><br>
     * @param enable allows the ability to provide consent
     * must be called only after MediaNetAdSdk.init(applicationContext, accountId, sdkInitListener).
     */
    fun setSubjectToGDPR(enable: Boolean) = apply { TargetingParams.setSubjectToGDPR(enable) }

    /**
     * initializes analytics sdk to be used across the MediaNetAdSDK
     * @param applicationContext is the context of application where the sdk has been integrated
     * @param sdkConfig is the sdk config which is mapped from SDK Config Response
     */
    private fun initAnalytics(applicationContext: Context, sdkConfig: SdkConfiguration) {
        EventManager.init(sdkConfig)
        val samplingMap = SamplingMap()
        samplingMap.put(LoggingEvents.PROJECT.type, sdkConfig.projectEventPercentage)
        samplingMap.put(LoggingEvents.OPPORTUNITY.type, sdkConfig.opportunityEventPercentage)

        val configuration = AnalyticsSDK.Configuration.Builder()
            .setAnalyticsUrl("") // There are multiple type of events with different base url so setting base url for SDK as empty, we will send url in event itself
            .enableEventSampling(samplingMap)
            .build()

        //TODO we get interval minute from config, need to update code for this use case, currently we only sync immediately
        val analyticsProvider = AnalyticsProviderFactory.getCachedProvider(
            applicationContext,
            sdkConfig.projectEventUrl,
            0
        )
        AnalyticsSDK.init(applicationContext, configuration, providers = listOf(analyticsProvider))
    }

    /**
     * indicates the working/availability of sdk.
     * if true then sdk will not function from there on.
     */
    fun isSdkOnVacation() = sdkOnVacation

    /**
     * indicates the successful fetch of sdk config.
     * if true then sdk will not function from there on.
     */
    internal fun isConfigEmpty() = config == null

    //TODO - when to call this
    internal fun clear() {
        AnalyticsSDK.clear()
        coroutineScope.coroutineContext.cancelChildren()
        config = null
        SDKConfigSyncWorker.cancelSDKConfigSync(PrebidMobile.getApplicationContext())
        EventManager.clear()
    }

    /**
     * gets any given subject to GDPR in that order. <br>
     * 1) MediaNetAdSdk subject to GDPR custom value, if present. <br>
     * 2) IAB subject to GDPR TCF 2.0. <br>
     * Otherwise, null.
     *
     * must be called only after MediaNetAdSdk.init(applicationContext, accountId, sdkInitListener)
     */
    fun isSubjectToGDPR(): Boolean? {
        return TargetingParams.isSubjectToGDPR()
    }

    /**
     * sets GDPR consent for MediaNetAdSdk. It uses custom static field, not IAB.
     * @param consentString for GDPR
     * must be called only after MediaNetAdSdk.init(applicationContext, accountId, sdkInitListener)
     */
    fun setGDPRConsentString(consentString: String?) = apply {
        TargetingParams.setGDPRConsentString(consentString)
    }
}
