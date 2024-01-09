
import android.content.Context
import com.android.adsdk.AdSDKManager
import com.android.adsdk.AdSdk
import com.android.adsdk.base.LoggingLevel
import com.android.adsdk.base.listeners.MSdkInitListener

/**
 * point of contact for initializing the sdk
 * and setting various parameters while working with it
 */
object AdTechSDK : AdSdk {

    override fun init(
        applicationContext: Context,
        accountId: String,
        sdkInitListener: MSdkInitListener?
    ) {
        com.android.adsdk.AdSDKManager.init(applicationContext, accountId, sdkInitListener)
    }
    override fun getAccountId(): String {
        return com.android.adsdk.AdSDKManager.getAccountId()
    }

    override fun setTimeoutMillis(timeoutMillis: Long) {
        com.android.adsdk.AdSDKManager.setTimeoutMillis(timeoutMillis)
    }

    override fun getTimeOutMillis(): Int {
        return com.android.adsdk.AdSDKManager.getTimeOutMillis()
    }

    override fun setStoredAuctionResponse(storedAuctionResponse: String?) {
        com.android.adsdk.AdSDKManager.setStoredAuctionResponse(storedAuctionResponse)
    }

    override fun addStoredBidResponse(bidder: String, responseId: String) {
        com.android.adsdk.AdSDKManager.addStoredBidResponse(bidder, responseId)
    }

    override fun getStoredBidResponses(): Map<String, String> {
        return com.android.adsdk.AdSDKManager.getStoredBidResponses()
    }

    override fun setLogLevel(level: LoggingLevel) {
        com.android.adsdk.AdSDKManager.setLogLevel(level)
    }

    override fun isCompatibleWithGoogleMobileAds(version: String) {
        com.android.adsdk.AdSDKManager.isCompatibleWithGoogleMobileAds(version)
    }

    override fun shareGeoLocation(share: Boolean) {
        com.android.adsdk.AdSDKManager.shouldShareGeoLocation(share)
    }

    override fun isSharingGeoLocation(): Boolean {
        return com.android.adsdk.AdSDKManager.isSharingGeoLocation()
    }

    override fun setSubjectToGDPR(enable: Boolean) {
        com.android.adsdk.AdSDKManager.setSubjectToGDPR(enable)
    }

    override fun isSubjectToGDPR(): Boolean {
        return com.android.adsdk.AdSDKManager.isSubjectToGDPR()
    }

    override fun setGDPRConsentString(consentString: String?) {
        com.android.adsdk.AdSDKManager.setGDPRConsentString(consentString)
    }

    override fun setStoreUrl(storeUrl: String) {
        AdSDKManager.setStoreUrl(storeUrl)
    }

    override fun setDomain(domain: String) {
        AdSDKManager.setDomain(domain)
    }

    override fun setDebug(enable: Boolean) {
        AdSDKManager.enableDebug(enable)
    }
}
