package com.barryalan.winetraining.ui.menu.ingredients

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.model.menu.with.IngredientWithRecipes
import com.barryalan.winetraining.util.getProgressDrawable
import com.barryalan.winetraining.util.loadImage
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView
import java.util.*
import kotlin.collections.ArrayList

class IngredientWithRecipesListAdapter(private val ingredientList: ArrayList<IngredientWithRecipes>, private val clickListener:IngredientWithRecipesCallback) :
    RecyclerView.Adapter<IngredientWithRecipesListAdapter.IngredientWithRecipesViewHolder>() , Filterable {
    class IngredientWithRecipesViewHolder(var view: View) : RecyclerView.ViewHolder(view)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IngredientWithRecipesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_ingredient_with_recipes, parent, false)
        return IngredientWithRecipesViewHolder(
            view
        )
    }


    private var filteredIngredientList = ingredientList
    private var ingredientSort: Int = R.id.chip_name_asc_ingredient_list

    fun updateIngredientList(newIngredientList: List<IngredientWithRecipes>) {
        ingredientList.clear()
        ingredientList.addAll(newIngredientList)
        notifyDataSetChanged()
    }

    fun updateSort(sortID: Int) {
        if (sortID != -1) {
            ingredientSort = sortID
            filteredIngredientList = ArrayList(sortIngredients(filteredIngredientList))
            notifyDataSetChanged()
        }
    }

    private fun sortIngredients(ingredients: List<IngredientWithRecipes>): List<IngredientWithRecipes> {
        return when (ingredientSort) {
            R.id.chip_name_asc_ingredient_list -> {
                ingredients.sortedBy { it.ingredient.name }
            }
            R.id.chip_name_desc_ingredient_list -> {
                ingredients.sortedByDescending { it.ingredient.name }
            }
            R.id.chip_calories_asc_ingredient_list -> {
                ingredients.sortedBy { it.ingredient.calories }
            }
            R.id.chip_calories_desc_ingredient_list -> {
                ingredients.sortedByDescending { it.ingredient.calories }
            }
            else -> {
                ingredients
            }
        }
    }

    fun getIngredientList() = ingredientList

    override fun getItemCount() = filteredIngredientList.size

    override fun onBindViewHolder(holder: IngredientWithRecipesViewHolder, position: Int) {

        val abDeleteIngredient = holder.view.findViewById<FloatingActionButton>(R.id.ab_delete_item_ingredient_with_recipes)
        val imgIngredientWR =  holder.view.findViewById<ImageView>(R.id.img_item_ingredient_with_recipes)


        holder.view.findViewById<MaterialTextView>(R.id.tv_name_item_ingredient_with_recipes).text =
            filteredIngredientList[holder.adapterPosition].ingredient.name.capitalize(Locale.ROOT)
        holder.view.findViewById<MaterialTextView>(R.id.tv_amount_of_recipes_item_ingredient_with_recipes).text = filteredIngredientList[holder.adapterPosition].recipes.size.toString()

        val tvCalories = holder.view.findViewById<MaterialTextView>(R.id.tv_calories_item_ingredient_with_recipes)
        if(filteredIngredientList[holder.adapterPosition].ingredient.calories != null){
            "${filteredIngredientList[holder.adapterPosition].ingredient.calories.toString()} calories".also { tvCalories.text = it }
        }else{
            "Calories Unknown".also { tvCalories.text = it }
        }

        if (filteredIngredientList[holder.adapterPosition].recipes.isEmpty()) {

            abDeleteIngredient.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    clickListener.onDeleteIngredient(filteredIngredientList[holder.adapterPosition].ingredient.id)
                }
            }
        } else {
            abDeleteIngredient.visibility = View.GONE
        }

        filteredIngredientList[holder.adapterPosition].ingredient.image?.let {
            imgIngredientWR.loadImage(
                Uri.parse(filteredIngredientList[holder.adapterPosition].ingredient.image),
                getProgressDrawable(holder.view.context)
            )
        }?: run{
            imgIngredientWR.setImageResource(R.drawable.ic_error_outline_black_24dp)
        }

        holder.view.setOnClickListener {

            val action =
                IngredientListDirections.actionIngredientListFragmentToIngredientDetailFragment(
                    filteredIngredientList[holder.adapterPosition].ingredient.id
                )

            Navigation.findNavController(holder.view).navigate(action)

        }

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                filteredIngredientList = if (charSearch.isEmpty()) {
                    ingredientList
                } else {
                    val resultList = ArrayList<IngredientWithRecipes>()
                    for (ingredient in ingredientList) {
                        if (ingredient.ingredient.name.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
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
                filteredIngredientList = results?.values as ArrayList<IngredientWithRecipes>
                notifyDataSetChanged()
            }

        }
    }
}