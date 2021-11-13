package com.barryalan.winetraining.model.menu.with

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.barryalan.winetraining.model.menu.Menu
import com.barryalan.winetraining.model.menu.MenuItem
import com.barryalan.winetraining.model.menu.Price
import com.barryalan.winetraining.model.menu.reference.MenuMenuItemRef

data class MenuWithMenuItems(

    @Embedded
    val menu: Menu,

    @Relation(
        parentColumn = "menuID",
        entity = MenuItem::class,
        entityColumn = "menuItemID",
        associateBy = Junction(
            value = MenuMenuItemRef::class,
            parentColumn = "menuID",
            entityColumn = "menuItemID"
        )
    )
    val menuItems: List<MenuItem>,

    @Relation(
        parentColumn = "menuID",
        entity = Price::class,
        entityColumn = "priceID",
        associateBy = Junction(
            value = MenuMenuItemRef::class,
            parentColumn = "menuID",
            entityColumn = "priceID"
        )
    )
    val menuItemsPrices: List<Price>
)
