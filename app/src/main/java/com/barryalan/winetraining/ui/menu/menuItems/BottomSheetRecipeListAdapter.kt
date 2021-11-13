package com.barryalan.winetraining.ui.menu.menuItems

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.model.menu.Amount
import com.barryalan.winetraining.model.menu.Ingredient
import com.barryalan.winetraining.model.menu.Recipe
import com.barryalan.winetraining.util.getProgressDrawable
import com.barryalan.winetraining.util.loadImage
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import java.util.*
import kotlin.collections.ArrayList

class BottomSheetRecipeListAdapter(
    private val recipeList: ArrayList<Recipe>,
    private val addItemCallback: BottomSheetRecipeCallback,
    private val recipesInMenu: ArrayList<Recipe>,
    private val recipesAmountsInMenu: ArrayList<Amount>,
) :
    RecyclerView.Adapter<BottomSheetRecipeListAdapter.BottomSheetMenuItemViewHolder>(),Filterable {
    class BottomSheetMenuItemViewHolder(var view: View) : RecyclerView.ViewHolder(view)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BottomSheetMenuItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_recipe_bottom_sheet_recipes, parent, false)
        return BottomSheetMenuItemViewHolder(
            view
        )
    }
    private var filteredRecipeList = recipeList
    override fun getItemCount() = recipeList.size

    fun updateMenuItemsList(
        newRecipesList: List<Recipe>?,
        newRecipesInMenu: List<Recipe>?,
        newAmountsInMenu: List<Amount>?
    ) {
        if (newRecipesList != null) {
            recipeList.clear()
            recipeList.addAll(newRecipesList)
        }
        if (newRecipesInMenu != null) {
            recipesInMenu.clear()
            recipesInMenu.addAll(newRecipesInMenu)
        }
        if (newAmountsInMenu != null) {
            recipesAmountsInMenu.clear()
            recipesAmountsInMenu.addAll(newAmountsInMenu)
        }

        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: BottomSheetMenuItemViewHolder, position: Int) {

        val image = holder.view.findViewById<ImageView>(R.id.img_recipe_bottom_sheet_recipe)
        val tvName = holder.view.findViewById<MaterialTextView>(R.id.tv_recipe_name_bottom_sheet_recipe)
        val etAmount =
            holder.view.findViewById<EditText>(R.id.et_recipe_amount_bottom_sheet_menu_item)
        val spUnit =
            holder.view.findViewById<Spinner>(R.id.sp_recipe_unit_bottom_sheet_recipe)
        val btnAdd = holder.view.findViewById<MaterialButton>(R.id.btn_bottom_sheet_recipe)


        filteredRecipeList[holder.adapterPosition].image?.let {
            image.loadImage(
                Uri.parse(it),
                getProgressDrawable(holder.view.context)
            )
        } ?: run {
            image.setImageResource(R.drawable.ic_error_outline_black_24dp)
        }

        tvName.text = filteredRecipeList[holder.adapterPosition].name.capitalize(Locale.ROOT)


        val recipeIndex = recipesInMenu.indexOfFirst {
            it.name.equals(
                filteredRecipeList[holder.adapterPosition].name,
                ignoreCase = true
            )
        }
        if (recipeIndex != -1) {
            etAmount.setText(recipesAmountsInMenu[recipeIndex].amount.toString())
            etAmount.isEnabled = false
            btnAdd.text = "-"
        } else {
            etAmount.text.clear()
            etAmount.isEnabled = true
            btnAdd.text = "+"
        }

        ArrayAdapter.createFromResource(
            holder.view.context,
            R.array.units,
            android.R.layout.simple_spinner_item
        ).also { adapter ->

            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Apply the adapter to the spinner
            spUnit.adapter = adapter


            if (recipeIndex != -1) {
                spUnit.setSelection(
                    adapter.getPosition(
                        recipesAmountsInMenu[recipeIndex].unit
                    )
                )
            }

        }


        btnAdd.setOnClickListener {
            if (btnAdd.text == "+") {

                if (etAmount.text.isNotBlank()) {
                    etAmount.isEnabled = false
                    spUnit.isEnabled = false
                    btnAdd.text = "-"
                }
                addItemCallback.addRecipeWithAmount(
                    filteredRecipeList[holder.adapterPosition],
                    etAmount.text.toString(), spUnit.selectedItem.toString()
                )
            } else {
                etAmount.isEnabled = true
                spUnit.isEnabled = true
                btnAdd.text = "+"
                addItemCallback.removeRecipeWithAmount(
                    filteredRecipeList[holder.adapterPosition]
                )
            }

        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                filteredRecipeList = if (charSearch.isEmpty()) {
                    recipeList
                } else {
                    val resultList = ArrayList<Recipe>()
                    for (recipe in recipeList) {
                        if (recipe.name.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(recipe)
                        }
                    }

                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredRecipeList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredRecipeList = results?.values as ArrayList<Recipe>
                notifyDataSetChanged()
            }
        }
    }


}
