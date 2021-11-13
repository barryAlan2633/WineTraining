package com.barryalan.winetraining.ui.menu.menu


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
import com.barryalan.winetraining.model.menu.Menu
import com.barryalan.winetraining.util.getProgressDrawable
import com.barryalan.winetraining.util.loadImage
import com.google.android.material.textview.MaterialTextView
import java.util.*
import kotlin.collections.ArrayList

class MenuListAdapter(private val menuList: ArrayList<Menu>) :

    RecyclerView.Adapter<MenuListAdapter.MenuViewHolder>(), Filterable {

    class MenuViewHolder(var view: View) : RecyclerView.ViewHolder(view)

    override fun getItemCount() = filteredMenuList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_menu_item, parent, false)
        return MenuViewHolder(
            view
        )
    }


    private var filteredMenuList = menuList

    fun updateMenuList(newMenuList: List<Menu>) {
        menuList.clear()

        //add the addNewMenuItem card
        menuList.add(Menu("Add New Menu", null, "Food"))

        //add the rest of the Menu Items
        menuList.addAll(newMenuList)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {

        val tvMenuItemImage = holder.view.findViewById<ImageView>(R.id.img_menu_item_menu_item)

        holder.view.findViewById<MaterialTextView>(R.id.tv_menu_item_name_menu_item).text =
            filteredMenuList[holder.adapterPosition].name.capitalize(Locale.ROOT)

        //If this is the AddNewMenuItem card
        if (filteredMenuList[holder.adapterPosition].name == "Add New Menu") {
            tvMenuItemImage.setImageDrawable(

                ResourcesCompat.getDrawable(
                    holder.view.context.resources,
                    R.drawable.ic_add_circle_outline_black_24dp,
                    null
                )

            )
        } else {
            filteredMenuList[holder.adapterPosition].image?.let {
                tvMenuItemImage.loadImage(
                    Uri.parse(it),
                    getProgressDrawable(holder.view.context)
                )
            } ?: run {
                tvMenuItemImage.setImageResource(R.drawable.ic_error_outline_black_24dp)
            }
        }


        holder.view.setOnClickListener {


            //if the id has not been initialized aka you pressed the AddNewMenuItem card
            if (filteredMenuList[holder.adapterPosition].name == "Add New Menu") {

                holder.view.findNavController()
                    .navigate(R.id.action_menuList_to_menuNewEdit)
            } else {
                val action = MenuListDirections.actionMenuListToMenuDetail(
                    filteredMenuList[holder.adapterPosition].id
                )
                Navigation.findNavController(holder.view).navigate(action)
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                filteredMenuList = if (charSearch.isEmpty()) {
                    menuList
                } else {
                    val resultList = ArrayList<Menu>()
                    for (menu in menuList) {
                        if (menu.name.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(menu)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredMenuList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredMenuList = results?.values as ArrayList<Menu>
                notifyDataSetChanged()
            }

        }
    }

}
