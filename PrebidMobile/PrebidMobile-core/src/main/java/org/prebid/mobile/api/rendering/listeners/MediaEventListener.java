package org.prebid.mobile.api.rendering.listeners;

import org.prebid.mobile.rendering.bidding.data.bid.BidResponse;

public interface MediaEventListener {
    void onBidRequest();
    void onBidRequestTimeout();
    void onRequestSentToGam(BidResponse bidResponse);
    void onAdLoaded();
}
