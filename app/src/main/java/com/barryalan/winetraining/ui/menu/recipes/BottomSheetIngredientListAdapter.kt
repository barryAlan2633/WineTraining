package com.barryalan.winetraining.ui.menu.recipes

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.model.menu.Amount
import com.barryalan.winetraining.model.menu.Ingredient
import com.barryalan.winetraining.util.getProgressDrawable
import com.barryalan.winetraining.util.loadImage
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import java.util.*
import kotlin.collections.ArrayList

class BottomSheetIngredientListAdapter(
    private val ingredientList: ArrayList<Ingredient>,
    private val addItemCallback: BottomSheetIngredientCallback,
    private val ingredientsInRecipe: ArrayList<Ingredient>,
    private val amountsInRecipe: ArrayList<Amount>
) : RecyclerView.Adapter<BottomSheetIngredientListAdapter.BottomSheetIngredientViewHolder>(),Filterable {
    class BottomSheetIngredientViewHolder(var view: View) : RecyclerView.ViewHolder(view)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BottomSheetIngredientViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_recipe_bottom_sheet_recipes, parent, false)
        return BottomSheetIngredientViewHolder(
            view
        )
    }

    private var filteredIngredientList = ingredientList
    override fun getItemCount() = filteredIngredientList.size
    fun getIngredientList() = filteredIngredientList

    fun updateIngredientsList(
        newIngredientList: List<Ingredient>?,
        newIngredientsInRecipe: List<Ingredient>?,
        newAmountsInRecipe: List<Amount>?
    ) {
        if (newIngredientList != null) {
            ingredientList.clear()
            ingredientList.addAll(newIngredientList)
        }
        if (newIngredientsInRecipe != null) {
            ingredientsInRecipe.clear()
            ingredientsInRecipe.addAll(newIngredientsInRecipe)
        }
        if (newAmountsInRecipe != null) {
            amountsInRecipe.clear()
            amountsInRecipe.addAll(newAmountsInRecipe)
        }

        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: BottomSheetIngredientViewHolder, position: Int) {

        val image = holder.view.findViewById<ImageView>(R.id.img_recipe_bottom_sheet_recipe)
        val tvName = holder.view.findViewById<MaterialTextView>(R.id.tv_recipe_name_bottom_sheet_recipe)
        val etAmount =
            holder.view.findViewById<EditText>(R.id.et_recipe_amount_bottom_sheet_menu_item)
        val spUnit =
            holder.view.findViewById<Spinner>(R.id.sp_recipe_unit_bottom_sheet_recipe)
        val btnAdd = holder.view.findViewById<MaterialButton>(R.id.btn_bottom_sheet_recipe)


        filteredIngredientList[holder.adapterPosition].image?.let {
            image.loadImage(
                Uri.parse(it),
                getProgressDrawable(holder.view.context)
            )
        } ?: run {
            image.setImageResource(R.drawable.ic_error_outline_black_24dp)
        }

        tvName.text = filteredIngredientList[holder.adapterPosition].name.capitalize(Locale.ROOT)


        val ingredientIndex = ingredientsInRecipe.indexOfFirst {
            it.name.toLowerCase(Locale.ROOT).equals(
                filteredIngredientList[holder.adapterPosition].name.toLowerCase(Locale.ROOT),
                ignoreCase = true
            )
        }
        if (ingredientIndex != -1) {
            etAmount.setText(amountsInRecipe[ingredientIndex].amount.toString())
            etAmount.isEnabled = false
            spUnit.isEnabled = false
            btnAdd.text = "-"
        } else {
            etAmount.text.clear()
            etAmount.isEnabled = true
            spUnit.isEnabled = true
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


            if (ingredientIndex != -1) {
                spUnit.setSelection(
                    adapter.getPosition(
                        amountsInRecipe[ingredientIndex].unit
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
                addItemCallback.addIngredientWithAmount(
                    filteredIngredientList[holder.adapterPosition],
                    etAmount.text.toString(), spUnit.selectedItem.toString()
                )
            } else {
                etAmount.isEnabled = true
                spUnit.isEnabled = true
                btnAdd.text = "+"
                addItemCallback.removeIngredientWithAmount(
                    filteredIngredientList[holder.adapterPosition]
                )
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                filteredIngredientList = if (charSearch.isEmpty()) {
                    ingredientList
                } else {
                    val resultList = ArrayList<Ingredient>()
                    for (ingredient in ingredientList) {
                        if (ingredient.name.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(ingredient)
                        }
                    }

                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredIngredientList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredIngredientList = results?.values as ArrayList<Ingredient>
                notifyDataSetChanged()
            }
        }
    }
}