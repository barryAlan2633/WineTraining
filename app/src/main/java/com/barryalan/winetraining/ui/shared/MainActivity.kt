package com.barryalan.winetraining.ui.shared

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.barryalan.winetraining.R
import com.barryalan.winetraining.ui.shared.util.*

class MainActivity : AppCompatActivity(),
    UICommunicationListener {
    private val _tag: String = "AppDebug"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }


    override fun onUIMessageReceived(uiMessage: UIMessage) {
        when (uiMessage.uiMessageType) {
            is UIMessageType.AreYouSureDialog -> {
                areYouSureDialog(
                    uiMessage.message,
                    uiMessage.uiMessageType.callback
                )
            }
            is UIMessageType.Toast -> {
                displayToast(uiMessage.message)
            }
            is UIMessageType.Dialog -> {
                displayInfoDialog(uiMessage.message)
            }

            is UIMessageType.ErrorDialog -> {
                displayErrorDialog(uiMessage.message)
            }

            is UIMessageType.None -> {
                Log.i(_tag, "onUIMessageReceived: ${uiMessage.message}")
            }
        }
    }
}