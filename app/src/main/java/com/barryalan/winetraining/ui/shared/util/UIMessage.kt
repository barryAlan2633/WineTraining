package com.barryalan.winetraining.ui.shared.util

data class UIMessage(
    val message: String,
    val uiMessageType: UIMessageType
)

sealed class UIMessageType {
    class Toast : UIMessageType()

    class Dialog : UIMessageType()

    class AreYouSureDialog(
        val callback: AreYouSureCallBack
    ) : UIMessageType()

    class ErrorDialog : UIMessageType()

    class None : UIMessageType()
}