package org.prebid.mobile.api.rendering.listeners;

import org.prebid.mobile.api.exceptions.AdException;
import org.prebid.mobile.rendering.bidding.data.bid.BidResponse;

public interface LoggingEventListener {
    void onBidRequest();
    void onBidRequestTimeout();
    void onRequestSentToGam(BidResponse bidResponse, AdException exception);
    void onAdLoaded();
}
