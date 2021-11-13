package com.barryalan.winetraining.ui.shared

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.addCallback
import androidx.navigation.findNavController
import com.barryalan.winetraining.R
import com.barryalan.winetraining.ui.shared.util.AreYouSureCallBack
import com.barryalan.winetraining.ui.shared.util.UIMessage
import com.barryalan.winetraining.ui.shared.util.UIMessageType
import com.google.android.material.button.MaterialButton


class MainMenu : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            exitAppDialog()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_menu, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnMenus = requireView().findViewById<MaterialButton>(R.id.btn_menus_main_menu)
        val btnTraining = requireView().findViewById<MaterialButton>(R.id.btn_training_main_menu)
        val btnManager = requireView().findViewById<MaterialButton>(R.id.btn_manager_main_menu)
        val btnHostess = requireView().findViewById<MaterialButton>(R.id.btn_hostess_main_menu)


//        btnMenus.setOnClickListener {
//            requireView().findNavController().navigate(R.id.action_mainMenu_to_menuMainMenu)
//        }
//
//
//        btnTraining.setOnClickListener {
//            requireView().findNavController().navigate(R.id.action_mainMenu_to_trainingMainMenu)
//        }
//
//        btnManager.setOnClickListener {
//            requireView().findNavController().navigate(R.id.action_mainMenu_to_managerMainMenu)
//        }
//
//        btnHostess.setOnClickListener {
//            requireView().findNavController().navigate(R.id.action_mainMenu_to_hostessMainMenu)
//        }

    }

    private fun exitAppDialog() {
        val callback: AreYouSureCallBack = object :
            AreYouSureCallBack {
            override fun proceed() {
                requireActivity().finish()
            }

            override fun cancel() {}
        }

        uiCommunicationListener.onUIMessageReceived(
            UIMessage(
                "Are you sure you want to exit the app? Don't be afraid of success!",
                UIMessageType.AreYouSureDialog(callback)
            )
        )

    }
}