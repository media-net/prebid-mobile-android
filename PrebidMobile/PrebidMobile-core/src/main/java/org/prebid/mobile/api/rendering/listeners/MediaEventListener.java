package org.prebid.mobile.api.rendering.listeners;

public interface MediaEventListener {
    void onBidRequest();
    void onBidRequestTimeout();
    void onRequestSentToGam();
    void onAdLoaded();
}
