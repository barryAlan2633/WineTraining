package com.barryalan.winetraining.ui.menu.recipes

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.model.menu.Amount
import com.barryalan.winetraining.model.menu.Recipe
import com.barryalan.winetraining.util.getProgressDrawable
import com.barryalan.winetraining.util.loadImage
import com.google.android.material.textview.MaterialTextView
import java.util.*
import kotlin.collections.ArrayList

class RecipeListWithAmountsAdapter(private val recipeList: ArrayList<Recipe>) :
    RecyclerView.Adapter<RecipeListWithAmountsAdapter.RecipeViewHolder>() {

    private var amounts: MutableList<Amount> = mutableListOf()

    class RecipeViewHolder(var view: View) : RecyclerView.ViewHolder(view)

    override fun getItemCount() = recipeList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_recipe_with_amounts, parent, false)
        return RecipeViewHolder(
            view
        )
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {

        val tvRecipeImage =
            holder.view.findViewById<ImageView>(R.id.img_recipe_item_recipe_with_amounts)
        val tvCalories =
            holder.view.findViewById<MaterialTextView>(R.id.tv_recipe_calories_item_recipe_with_amounts)

        holder.view.findViewById<MaterialTextView>(R.id.tv_recipe_name_item_recipe_with_amounts).text =
            recipeList[holder.adapterPosition].name.capitalize(Locale.ROOT)

        "${amounts[holder.adapterPosition].amount} ${amounts[holder.adapterPosition].unit}".also {
            holder.view.findViewById<MaterialTextView>(
                R.id.tv_recipe_amount_item_recipe_with_amounts
            ).text = it
        }


        if (recipeList[holder.adapterPosition].calories != null) {
            "${recipeList[holder.adapterPosition].calories} calories".also { tvCalories.text = it }
        } else {
            "Calories Unknown".also { tvCalories.text = it }
        }

        //If this is the AddNewRecipe card
        if (recipeList[holder.adapterPosition].name == "Add New Menu Item") {
            tvRecipeImage.setImageDrawable(

                ResourcesCompat.getDrawable(
                    holder.view.context.resources,
                    R.drawable.ic_add_circle_outline_black_24dp,
                    null
                )

            )
        } else {
            recipeList[holder.adapterPosition].image?.let {
                tvRecipeImage.loadImage(
                    Uri.parse(it),
                    getProgressDrawable(holder.view.context)
                )
            } ?: run {
                tvRecipeImage.setImageResource(R.drawable.ic_error_outline_black_24dp)
            }
        }


    }


    fun getRecipeList() = recipeList


    fun getAmountsList() = amounts



    fun updateRecipeList(newRecipeList: List<Recipe>, newAmounts: List<Amount>) {
        recipeList.clear()
        amounts.clear()

        recipeList.addAll(newRecipeList)
        amounts.addAll(newAmounts)
        notifyDataSetChanged()
    }

    fun addRecipeItem(newRecipe: Recipe, newAmount: Amount) {
        recipeList.add(newRecipe)
        amounts.add(newAmount)
        notifyItemInserted(itemCount)
    }

    fun removeRecipeWithAmount(recipe: Recipe) {
        val recipeIndex = recipeList.indexOf(recipe)
        if (recipeIndex != -1) {
            amounts.removeAt(recipeIndex)
            recipeList.remove(recipe)
            notifyItemRemoved(recipeIndex)
        }

    }


}
