package com.barryalan.winetraining.ui.menu.menuItems

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.model.menu.MenuItem
import com.barryalan.winetraining.util.getProgressDrawable
import com.barryalan.winetraining.util.loadImage
import com.google.android.material.textview.MaterialTextView
import java.util.*
import kotlin.collections.ArrayList

class MenuItemListAdapter(private val menuItemsList: ArrayList<MenuItem>) :
    RecyclerView.Adapter<MenuItemListAdapter.MenuItemViewHolder>(), Filterable {
    class MenuItemViewHolder(var view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_menu_item, parent, false)
        return MenuItemViewHolder(
            view
        )
    }

    override fun getItemCount() = filteredMenuItemsList.size
    fun getMenuItemsList() = menuItemsList

    private var filteredMenuItemsList = menuItemsList
    private var menuItemSort: Int = R.id.chip_name_asc_menu_item_list

    fun updateMenuItemsList(newMenuItemsList: List<MenuItem>) {
        menuItemsList.clear()

        menuItemsList.addAll(newMenuItemsList)
        notifyDataSetChanged()
    }

    fun updateSort(sortID: Int) {
        if (sortID != -1) {
            menuItemSort = sortID
            filteredMenuItemsList = ArrayList(sortMenuItems(filteredMenuItemsList))
            notifyDataSetChanged()
        }
    }

    private fun sortMenuItems(menuItemList: List<MenuItem>): List<MenuItem> {
        return when (menuItemSort) {
            R.id.chip_name_asc_menu_item_list -> {
                menuItemList.sortedBy { it.name }
            }
            R.id.chip_name_desc_menu_item_list -> {
                menuItemList.sortedByDescending { it.name }
            }
            R.id.chip_calories_asc_menu_item_list -> {
                menuItemList.sortedBy { it.calories }
            }
            R.id.chip_calories_desc_menu_item_list -> {
                menuItemList.sortedByDescending { it.calories }
            }
            else -> {
                menuItemList
            }
        }
    }

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {

        val tvMenuItemImage = holder.view.findViewById<ImageView>(R.id.img_menu_item_menu_item)
        val tvMenuItemCalories = holder.view.findViewById<MaterialTextView>(R.id.tv_calories_menu_item)

        holder.view.findViewById<MaterialTextView>(R.id.tv_menu_item_name_menu_item).text =
            filteredMenuItemsList[holder.adapterPosition].name.capitalize(Locale.ROOT)


        filteredMenuItemsList[holder.adapterPosition].image?.let {
            tvMenuItemImage.loadImage(
                Uri.parse(it),
                getProgressDrawable(holder.view.context)
            )
        } ?: run {
            tvMenuItemImage.setImageResource(R.drawable.ic_error_outline_black_24dp)
        }

        if (filteredMenuItemsList[holder.adapterPosition].calories != null) {
            "${filteredMenuItemsList[holder.adapterPosition].calories} calories".also {
                tvMenuItemCalories.text = it
            }
        } else {
            "Calories Unknown".also { tvMenuItemCalories.text = it }
        }



        holder.view.setOnClickListener {


            val action = MenuItemsListDirections.actionMenuItemsListToMenuItemDetail(
                filteredMenuItemsList[holder.adapterPosition].id
            )
            Navigation.findNavController(holder.view).navigate(action)


        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                filteredMenuItemsList = if (charSearch.isEmpty()) {
                    menuItemsList
                } else {
                    val resultList = ArrayList<MenuItem>()
                    for (menuItem in menuItemsList) {
                        if (menuItem.name.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(menuItem)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredMenuItemsList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredMenuItemsList = results?.values as ArrayList<MenuItem>
                notifyDataSetChanged()
            }

        }
    }

}
