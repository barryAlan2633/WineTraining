package com.barryalan.winetraining.ui.manager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.barryalan.winetraining.R
import com.barryalan.winetraining.ui.shared.BaseFragment
import com.google.android.material.button.MaterialButton

class ManagerMainMenu : BaseFragment() {

    private lateinit var btnEmployee: MaterialButton
    private lateinit var btnFloorDesigner: MaterialButton
    private lateinit var btnMenu: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_manager_main_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnEmployee = view.findViewById(R.id.btn_employee_manager_main_menu)
        btnFloorDesigner = view.findViewById(R.id.btn_floor_designer_manager_main_menu)
        btnMenu = view.findViewById(R.id.btn_menu_manager_main_menu)

        btnEmployee.setOnClickListener {
            view.findNavController().navigate(R.id.action_managerMainMenu_to_managerEmployee)
        }

        btnFloorDesigner.setOnClickListener {
            view.findNavController().navigate(R.id.action_managerMainMenu_to_managerFloorDesigner)
        }
        btnMenu.setOnClickListener {
            view.findNavController().navigate(R.id.action_managerMainMenu_to_menuMainMenu)
        }

    }


}