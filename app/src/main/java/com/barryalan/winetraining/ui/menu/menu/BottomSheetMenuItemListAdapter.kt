package com.barryalan.winetraining.ui.menu.menu


import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.model.menu.MenuItem
import com.barryalan.winetraining.model.menu.Price
import com.barryalan.winetraining.util.getProgressDrawable
import com.barryalan.winetraining.util.loadImage
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import java.util.*
import kotlin.collections.ArrayList

class BottomSheetMenuItemListAdapter(
    private val menuItemList: ArrayList<MenuItem>,
    private val addItemCallback: BottomSheetMenuItemCallback,
    private val menuItemsInMenu: ArrayList<MenuItem>,
    private val menuItemsPricesInMenu: ArrayList<Price>
) :
    RecyclerView.Adapter<BottomSheetMenuItemListAdapter.BottomSheetMenuItemViewHolder>() {

    class BottomSheetMenuItemViewHolder(var view: View) : RecyclerView.ViewHolder(view)

    override fun getItemCount() = menuItemList.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BottomSheetMenuItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_menu_item_bottom_sheet_menu_items, parent, false)
        return BottomSheetMenuItemViewHolder(
            view
        )
    }

    fun updateMenuItemsList(
        newMenuItemsList: List<MenuItem>?,
        newItemsInMenu: List<MenuItem>?,
        newPricesInMenu: List<Price>?
    ) {

        if (newMenuItemsList != null) {
            menuItemList.clear()
            menuItemList.addAll(newMenuItemsList)
        }
        if (newItemsInMenu != null) {
            menuItemsInMenu.clear()
            menuItemsInMenu.addAll(newItemsInMenu)
        }
        if (newPricesInMenu != null) {
            menuItemsPricesInMenu.clear()
            menuItemsPricesInMenu.addAll(newPricesInMenu)
        }


        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: BottomSheetMenuItemViewHolder, position: Int) {

        val image = holder.view.findViewById<ImageView>(R.id.img_menu_item_bottom_sheet_menu_item)
        val tvName = holder.view.findViewById<MaterialTextView>(R.id.tv_menu_item_bottom_sheet_menu_item)
        val etPrice =
            holder.view.findViewById<EditText>(R.id.et_recipe_amount_bottom_sheet_menu_item)
        val btnAdd = holder.view.findViewById<MaterialButton>(R.id.btn_bottom_sheet_menu_item)


        menuItemList[holder.adapterPosition].image?.let {
            image.loadImage(
                Uri.parse(it),
                getProgressDrawable(holder.view.context)
            )
        } ?: run {
            image.setImageResource(R.drawable.ic_error_outline_black_24dp)
        }

        tvName.text = menuItemList[holder.adapterPosition].name.capitalize(Locale.ROOT)


        val itemIndex = menuItemsInMenu.indexOfFirst { it == menuItemList[holder.adapterPosition] }
        if (itemIndex != -1) {
            etPrice.setText(menuItemsPricesInMenu[itemIndex].price.toString())
            etPrice.isEnabled = false
            btnAdd.text = "-"
        } else {
            etPrice.text.clear()
            etPrice.isEnabled = true
            btnAdd.text = "+"
        }

        btnAdd.setOnClickListener {
            if (btnAdd.text == "+") {
                etPrice.isEnabled = false
                btnAdd.text = "-"
                addItemCallback.addMenuItemWithPrice(
                    menuItemList[holder.adapterPosition],
                    etPrice.text.toString()
                )
            } else {
                etPrice.isEnabled = true
                btnAdd.text = "+"
                addItemCallback.removeMenuItemWithPrice(
                    menuItemList[holder.adapterPosition],
                    etPrice.text.toString()
                )
            }

        }
    }

}
