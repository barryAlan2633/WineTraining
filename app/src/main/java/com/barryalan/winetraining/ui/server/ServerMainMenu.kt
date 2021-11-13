package com.barryalan.winetraining.ui.server

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.barryalan.winetraining.R
import com.barryalan.winetraining.ui.menu.recipes.RecipeDetailArgs
import com.barryalan.winetraining.ui.shared.BaseFragment
import com.barryalan.winetraining.ui.shared.LoginDirections
import com.google.android.material.button.MaterialButton

class ServerMainMenu : BaseFragment() {

    private lateinit var btnFloor: MaterialButton

    private var mServerUID = -1L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_server_main_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            mServerUID = ServerMainMenuArgs.fromBundle(it).serverUID
        }

        btnFloor = view.findViewById(R.id.btn_floor_server_main_menu)

        btnFloor.setOnClickListener {

            val action = ServerMainMenuDirections.actionServerMainMenuToServerMain(mServerUID)

            requireView().findNavController().navigate(action)
        }
    }




}