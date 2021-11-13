package com.barryalan.winetraining.ui.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import com.barryalan.winetraining.R
import com.google.android.material.button.MaterialButton


class MenuMainMenu : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu_main_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnRecipeList = requireView().findViewById<MaterialButton>(R.id.btn_recipes_menu_main_menu)
        val btnIngredients = requireView().findViewById<MaterialButton>(R.id.btn_ingredients_menu_main_menu)
        val btnMenuItems = requireView().findViewById<MaterialButton>(R.id.btn_menu_items_menu_main_menu)
        val btnMenus = requireView().findViewById<MaterialButton>(R.id.btn_menus_menu_main_menu)

        btnIngredients.setOnClickListener {
            requireView().findNavController().navigate(R.id.action_menuMainMenu_to_ingredientListFragment)
        }

        btnRecipeList.setOnClickListener {
            requireView().findNavController().navigate(R.id.action_menuMainMenu_to_recipeList)
        }

        btnMenuItems.setOnClickListener {
            requireView().findNavController().navigate(R.id.action_menuMainMenu_to_menuItemsList)
        }

        btnMenus.setOnClickListener {
            requireView().findNavController().navigate(R.id.action_menuMainMenu_to_menuList)
        }
    }


}