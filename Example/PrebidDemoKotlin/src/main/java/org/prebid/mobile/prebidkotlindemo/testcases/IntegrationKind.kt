package org.prebid.mobile.prebidkotlindemo.testcases

enum class IntegrationKind(
    val adServer: String
) {

    GAM_ORIGINAL("GAM (Original API)"),
    GAM_RENDERING("GAM (Rendering API)")
}