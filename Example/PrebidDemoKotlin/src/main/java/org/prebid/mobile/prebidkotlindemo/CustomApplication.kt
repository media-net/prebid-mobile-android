/*
 *    Copyright 2018-2019 Prebid.org, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.prebid.mobile.prebidkotlindemo

import AdTechSDK
import android.app.Application
import org.prebid.mobile.prebidkotlindemo.utils.Settings

class CustomApplication : Application() {

    companion object {
        private const val TEMP_ACCOUNT_ID = "8CURM5854"
        private const val YAHOO_TEMP_ACCOUNT_ID = "8YHBCQD82"
    }

    override fun onCreate() {
        super.onCreate()
        AdTechSDK.init(this, TEMP_ACCOUNT_ID)
        Settings.init(this)
    }
}
