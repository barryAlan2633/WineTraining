package com.barryalan.winetraining.ui.menu.menuItems


import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.Navigation
import androidx.navigation.findNavController
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

class MenuItemWithPricesListAdapter(
    private val menuItemList: ArrayList<MenuItem>
) :
    RecyclerView.Adapter<MenuItemWithPricesListAdapter.MenuItemWithPricesViewHolder>(), Filterable {
    class MenuItemWithPricesViewHolder(var view: View) : RecyclerView.ViewHolder(view)

    override fun getItemCount() = filteredMenuItemWithPricesList.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuItemWithPricesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_menu_item_with_prices, parent, false)
        return MenuItemWithPricesViewHolder(
            view
        )
    }

    private var filteredMenuItemWithPricesList = menuItemList
    private val menuItemPrices = arrayListOf<Price>()

    fun updateMenuItemsList(newMenuItemsList: List<MenuItem>, newPricesList: List<Price>) {
        menuItemList.clear()
        menuItemPrices.clear()

        //add the rest of the Menu Items
        menuItemList.addAll(newMenuItemsList)
        menuItemPrices.addAll(newPricesList)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MenuItemWithPricesViewHolder, position: Int) {

        val tvMenuItemImage =
            holder.view.findViewById<ImageView>(R.id.img_menu_item_menu_item_with_prices)

        holder.view.findViewById<MaterialTextView>(R.id.tv_menu_item_name_menu_item_with_prices).text =
            filteredMenuItemWithPricesList[holder.adapterPosition].name.capitalize(Locale.ROOT)

        //todo got to make filtered list for prices too otherwise this will cause bug when the list is filtered
        "$${menuItemPrices[holder.adapterPosition].price}".also {
            holder.view.findViewById<MaterialTextView>(R.id.tv_menu_item_price_menu_item_with_prices).text =
                it
        }

        //If this is the AddNewMenuItem card
        if (filteredMenuItemWithPricesList[holder.adapterPosition].name == "Add New Menu Item") {
            tvMenuItemImage.setImageDrawable(

                ResourcesCompat.getDrawable(
                    holder.view.context.resources,
                    R.drawable.ic_add_circle_outline_black_24dp,
                    null
                )

            )
        } else {
            filteredMenuItemWithPricesList[holder.adapterPosition].image?.let {
                tvMenuItemImage.loadImage(
                    Uri.parse(it),
                    getProgressDrawable(holder.view.context)
                )
            } ?: run {
                tvMenuItemImage.setImageResource(R.drawable.ic_error_outline_black_24dp)
            }
        }


        holder.view.setOnClickListener {

            if (filteredMenuItemWithPricesList[holder.adapterPosition].name == "Add New Menu Item") {

                holder.view.findNavController()
                    .navigate(R.id.action_menuItemsList_to_menuItemNewEdit)
            } else {
                if (menuItemList[0].name == "Add New Menu Item") {
                    val action = MenuItemsListDirections.actionMenuItemsListToMenuItemDetail(
                        filteredMenuItemWithPricesList[holder.adapterPosition].id
                    )
                    Navigation.findNavController(holder.view).navigate(action)
                }
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                filteredMenuItemWithPricesList = if (charSearch.isEmpty()) {
                    menuItemList
                } else {
                    val resultList = ArrayList<MenuItem>()
                    for (menuItem in menuItemList) {
                        if (menuItem.name.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(menuItem)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredMenuItemWithPricesList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredMenuItemWithPricesList = results?.values as ArrayList<MenuItem>
                notifyDataSetChanged()
            }

        }
    }

    fun addMenuItem(menuItemToAdd: MenuItem, pricesToAdd: Price) {
        menuItemList.add(menuItemToAdd)
        menuItemPrices.add(pricesToAdd)
        notifyItemInserted(itemCount)
    }

    fun getMenuItemList(): ArrayList<MenuItem> = menuItemList
    fun getMenuItemPricesList(): ArrayList<Price> = menuItemPrices

    fun removeMenuItem(menuItem: MenuItem) {
        val indexToDelete = menuItemList.indexOf(menuItem)
        menuItemList.removeAt(indexToDelete)
        menuItemPrices.removeAt(indexToDelete)
        notifyItemRemoved(indexToDelete)
    }

}
