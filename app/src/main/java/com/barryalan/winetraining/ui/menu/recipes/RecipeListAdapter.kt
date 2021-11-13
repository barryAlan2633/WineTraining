package com.barryalan.winetraining.ui.menu.recipes

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
import com.barryalan.winetraining.model.menu.Recipe
import com.barryalan.winetraining.util.getProgressDrawable
import com.barryalan.winetraining.util.loadImage
import com.google.android.material.textview.MaterialTextView
import java.util.*
import kotlin.collections.ArrayList

class RecipeListAdapter(private val recipeList: ArrayList<Recipe>) :
    RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder>(), Filterable {
    class RecipeViewHolder(var view: View) : RecyclerView.ViewHolder(view)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(
            view
        )
    }
    override fun getItemCount() = filteredRecipeList.size
    fun getRecipeList() = recipeList


    private var filteredRecipeList = recipeList
    private var recipeSort: Int = R.id.chip_name_asc_recipe_list

    fun updateRecipeList(newRecipeList: List<Recipe>) {
        recipeList.clear()
        recipeList.addAll(sortRecipes(newRecipeList))
        notifyDataSetChanged()
    }

    fun updateSort(sortID: Int) {
        if (sortID != -1) {
            recipeSort = sortID
            filteredRecipeList = ArrayList(sortRecipes(filteredRecipeList))
            notifyDataSetChanged()
        }
    }

    private fun sortRecipes(recipeList: List<Recipe>): List<Recipe> {
        return when (recipeSort) {
            R.id.chip_name_asc_recipe_list -> {
                recipeList.sortedBy { it.name }
            }
            R.id.chip_name_desc_recipe_list -> {
                recipeList.sortedByDescending { it.name }
            }
            R.id.chip_calories_asc_recipe_list -> {
                recipeList.sortedBy { it.calories }
            }
            R.id.chip_calories_desc_recipe_list -> {
                recipeList.sortedByDescending { it.calories }
            }
            else -> {
                recipeList
            }
        }
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {

        val tvRecipeImage = holder.view.findViewById<ImageView>(R.id.img_item_recipe)
        val tvRecipeCalories = holder.view.findViewById<MaterialTextView>(R.id.tv_calories_item_recipe)

        holder.view.findViewById<MaterialTextView>(R.id.tv_name_item_recipe).text =
            filteredRecipeList[holder.adapterPosition].name.capitalize(Locale.ROOT)

        filteredRecipeList[holder.adapterPosition].image?.let {
            tvRecipeImage.loadImage(
                Uri.parse(it),
                getProgressDrawable(holder.view.context)
            )
        } ?: run {
            tvRecipeImage.setImageResource(R.drawable.ic_error_outline_black_24dp)
        }

        if (filteredRecipeList[holder.adapterPosition].calories != null) {
            "${filteredRecipeList[holder.adapterPosition].calories} calories".also { tvRecipeCalories.text = it }
        } else {
            "Calories Unknown".also { tvRecipeCalories.text = it }
        }

        holder.view.setOnClickListener {
            val action =
                RecipeListDirections.actionRecipeListToRecipeDetail(filteredRecipeList[holder.adapterPosition].id)
            Navigation.findNavController(holder.view).navigate(action)
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
                filterResults.values = ArrayList(sortRecipes(filteredRecipeList))
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
