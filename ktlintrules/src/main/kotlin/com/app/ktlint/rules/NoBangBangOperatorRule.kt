package com.app.ktlint.rules

import com.pinterest.ktlint.core.Rule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes

class NoBangBangOperatorRule : Rule("no-bang-bang-operator") {
    companion object {
        private const val BANG_BANG = "!!"
        private const val BANG_BANG_LEN = BANG_BANG.length
    }

    override fun visit(
        node: ASTNode,
        autoCorrect: Boolean,
        emit: (
            offset: Int,
            errorMessage: String,
            canBeAutoCorrected: Boolean
        ) -> Unit
    ) {
        if (node.elementType == KtStubElementTypes.FILE) {
            val text = node.text
            var offset = 0
            var index = text.indexOf(BANG_BANG, offset)
            while (index > 0) {
                emit(index, "!! operator is not allowed", false)
                offset = index + BANG_BANG_LEN
                index = text.indexOf(BANG_BANG, offset)
            }
        }
    }
}
