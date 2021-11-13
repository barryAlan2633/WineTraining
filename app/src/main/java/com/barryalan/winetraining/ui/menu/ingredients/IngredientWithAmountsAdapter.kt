package com.barryalan.winetraining.ui.menu.ingredients

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
import com.google.android.material.textview.MaterialTextView
import java.util.*
import kotlin.collections.ArrayList

class IngredientWithAmountsAdapter(private val ingredientList: ArrayList<Ingredient>) :
    RecyclerView.Adapter<IngredientWithAmountsAdapter.IngredientViewHolder>(), Filterable {

    private var filteredIngredientList = ingredientList
    private var amounts: MutableList<Amount> = mutableListOf()

    fun getIngredientList(): ArrayList<Ingredient> {
        return ingredientList
    }

    fun updateIngredientList(newIngredientList: List<Ingredient>, newAmounts: List<Amount>) {
        ingredientList.clear()
        ingredientList.addAll(newIngredientList)

        amounts.clear()
        amounts.addAll(newAmounts)

        notifyDataSetChanged()
    }

    fun addIngredientItem(newIngredient: Ingredient, newAmount: Amount) {
        ingredientList.add(newIngredient)
        amounts.add(newAmount)
        notifyItemInserted(itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_ingredient_with_amount, parent, false)
        return IngredientViewHolder(
            view
        )
    }

    override fun getItemCount() = filteredIngredientList.size

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.view.findViewById<MaterialTextView>(R.id.et_name_item_ingredient_with_amount).text =
            filteredIngredientList[holder.adapterPosition].name.capitalize(Locale.ROOT)
        val tvCalories = holder.view.findViewById<MaterialTextView>(R.id.tv_calories_item_ingredient_with_amount)



        if( filteredIngredientList[holder.adapterPosition].calories != null){
            "${filteredIngredientList[holder.adapterPosition].calories} calories".also { tvCalories.text = it }
        }else{
            tvCalories.text =  "Calories Unknown"
        }

        if (amounts.isNotEmpty()) {
            //todo these amounts are wrong they should correspond to the filtered list not the regular list
            holder.view.findViewById<MaterialTextView>(R.id.tv_amount_item_ingredient_with_amount).text =
                amounts[holder.adapterPosition].amount.toString()
            holder.view.findViewById<MaterialTextView>(R.id.tv_amount_unit_item_ingredient_with_amount).text =
                amounts[holder.adapterPosition].unit
        }


        filteredIngredientList[holder.adapterPosition].image?.let {
            holder.view.findViewById<ImageView>(R.id.img_ingredient_item_ingredient_with_amount).loadImage(
                Uri.parse(it),
                getProgressDrawable(holder.view.context)
            )
        } ?: run {
            holder.view.findViewById<ImageView>(R.id.img_ingredient_item_ingredient_with_amount)
                .setImageResource(R.drawable.ic_error_outline_black_24dp)
        }

    }

    fun getAmountsList() = amounts


    class IngredientViewHolder(var view: View) : RecyclerView.ViewHolder(view)

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

    fun removeIngredientWithAmount(ingredient: Ingredient) {
        val ingredientIndex = ingredientList.indexOf(ingredient)
        if (ingredientIndex != -1) {
            amounts.removeAt(ingredientIndex)
            ingredientList.remove(ingredient)
            notifyItemRemoved(ingredientIndex)
        }
    }
}