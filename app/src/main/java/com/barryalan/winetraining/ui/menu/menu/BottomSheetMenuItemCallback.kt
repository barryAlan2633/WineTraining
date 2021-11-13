package com.barryalan.winetraining.ui.menu.menu

import com.barryalan.winetraining.model.menu.MenuItem

interface BottomSheetMenuItemCallback {
    fun addMenuItemWithPrice(menuItem: MenuItem, price: String)
    fun removeMenuItemWithPrice(menuItem: MenuItem, price: String)
}