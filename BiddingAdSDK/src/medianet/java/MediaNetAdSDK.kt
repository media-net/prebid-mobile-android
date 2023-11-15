
import android.content.Context
import com.android.adsdk.AdSDKManager
import com.android.adsdk.AdSdk
import com.android.adsdk.base.MLogLevel
import com.android.adsdk.base.listeners.MSdkInitListener

/**
 * point of contact for initializing the sdk
 * and setting various parameters while working with it
 */
object MediaNetAdSDK : AdSdk {
    override fun init(
        applicationContext: Context,
        accountId: String,
        sdkInitListener: MSdkInitListener?
    ) {
        AdSDKManager.init(applicationContext, accountId, sdkInitListener)
    }
    override fun getAccountId(): String {
        return AdSDKManager.getAccountId()
    }

    override fun setTimeoutMillis(timeoutMillis: Long) {
        AdSDKManager.setTimeoutMillis(timeoutMillis)
    }

    override fun getTimeOutMillis(): Int {
        return AdSDKManager.getTimeOutMillis()
    }

    override fun setStoredAuctionResponse(storedAuctionResponse: String?) {
        AdSDKManager.setStoredAuctionResponse(storedAuctionResponse)
    }

    override fun addStoredBidResponse(bidder: String, responseId: String) {
        AdSDKManager.addStoredBidResponse(bidder, responseId)
    }

    override fun getStoredBidResponses(): Map<String, String> {
        return AdSDKManager.getStoredBidResponses()
    }

    override fun setLogLevel(level: MLogLevel) {
         AdSDKManager.setLogLevel(level)
    }

    override fun isCompatibleWithGoogleMobileAds(version: String) {
        AdSDKManager.isCompatibleWithGoogleMobileAds(version)
    }

    override fun shareGeoLocation(share: Boolean) {
        AdSDKManager.shouldShareGeoLocation(share)
    }

    override fun isSharingGeoLocation(): Boolean {
        return AdSDKManager.isSharingGeoLocation()
    }

    override fun setSubjectToGDPR(enable: Boolean) {
        AdSDKManager.setSubjectToGDPR(enable)
    }

    override fun isSubjectToGDPR(): Boolean {
        return AdSDKManager.isSubjectToGDPR()
    }

    override fun setGDPRConsentString(consentString: String?) {
        AdSDKManager.setGDPRConsentString(consentString)
    }
}
