package com.app.ktlint.rules

import com.pinterest.ktlint.core.RuleSet
import com.pinterest.ktlint.core.RuleSetProvider

class RuleSetProvider : RuleSetProvider {
    override fun get() = RuleSet("ktlint-rules",
            NoBangBangOperatorRule(),
            LogRule()
    )
}
