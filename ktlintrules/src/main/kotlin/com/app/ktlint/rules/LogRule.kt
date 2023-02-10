package com.app.ktlint.rules

import com.pinterest.ktlint.core.Rule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes

class LogRule : Rule("no-log-usage") {

    companion object {
        private const val CUSTOM_LOG_OBJECT = "object CustomLogger"
        private const val LOG_PREFIX = "Log."
        private const val CUSTOM_LOG_PREFIX = "CustomLogger."
    }

    private var isCustomLogObject = false

    override fun visit(
        node: ASTNode,
        autoCorrect: Boolean,
        emit: (
            offset: Int,
            errorMessage: String,
            canBeAutoCorrected: Boolean
        ) -> Unit
    ) {
        when (node.elementType) {
            KtStubElementTypes.FILE -> isCustomLogObject = node.text.contains(CUSTOM_LOG_OBJECT)
            KtStubElementTypes.DOT_QUALIFIED_EXPRESSION -> {
                if (!isCustomLogObject && node.text.contains(LOG_PREFIX) && !node.text.contains(
                        CUSTOM_LOG_PREFIX
                    )) {
                    emit(node.startOffset + node.text.indexOf(LOG_PREFIX),
                            "Log not allowed - use CustomLogger",
                            false)
                }
            }
            else -> {}
        }
    }
}
