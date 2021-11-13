package com.barryalan.winetraining.ui.hostess.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.barryalan.winetraining.R
import com.barryalan.winetraining.ui.shared.BaseFragment
import com.google.android.material.button.MaterialButton


class HostessMainMenu : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hostess_main_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSectionAssigner = requireView().findViewById<MaterialButton>(R.id.btn_section_assigner_hostess_main_menu)
        val btnMain = requireView().findViewById<MaterialButton>(R.id.btn_main_hostess_main_menu)


        btnSectionAssigner.setOnClickListener {
            requireView().findNavController().navigate(R.id.action_hostessMainMenu_to_hostessSectionAssigner)
        }


        btnMain.setOnClickListener {
            requireView().findNavController().navigate(R.id.action_hostessMainMenu_to_hostessMain)
        }

    }


}