package com.barryalan.winetraining.ui.menu.ingredients

import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.model.menu.Ingredient
import com.barryalan.winetraining.util.getProgressDrawable
import com.barryalan.winetraining.util.loadImage
import com.google.android.material.textview.MaterialTextView
import java.util.*
import kotlin.collections.ArrayList

class IngredientAdapter(
    private val ingredientList: ArrayList<Ingredient>,
    private val ingredientCallback: IngredientCallback
) :
    RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>(), Filterable {
    class IngredientViewHolder(var view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_ingredient, parent, false)
        return IngredientViewHolder(
            view
        )
    }

    override fun getItemCount() = filteredIngredientList.size
    fun getIngredientList() = ingredientList


    private var filteredIngredientList = ingredientList
    var notWanted = false

    fun updateIngredientList(newIngredientList: List<Ingredient>) {
        ingredientList.clear()
        ingredientList.addAll(newIngredientList)
        notifyDataSetChanged()
    }

    fun addIngredientItem(newIngredient: Ingredient) {
        ingredientList.add(newIngredient)
        notifyItemInserted(itemCount)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {

        val tvName = holder.view.findViewById<MaterialTextView>(R.id.tv_name_item_ingredient)
        val cardView = holder.view.findViewById<CardView>(R.id.cv_item_ingredient)

        cardView.setCardBackgroundColor(Color.WHITE)

        tvName.text = filteredIngredientList[holder.adapterPosition].name.capitalize(Locale.ROOT)

        filteredIngredientList[holder.adapterPosition].image?.let {
            holder.view.findViewById<ImageView>(R.id.img_ingredient_item_ingredient).loadImage(
                Uri.parse(it),
                getProgressDrawable(holder.view.context)
            )
        } ?: run {
            holder.view.findViewById<ImageView>(R.id.img_ingredient_item_ingredient)
                .setImageResource(R.drawable.ic_error_outline_black_24dp)
        }

        holder.view.setOnClickListener {
            if (notWanted) {
                ingredientCallback.onIngredientClicked(
                    "not ${tvName.text.toString().capitalize(Locale.ROOT)}"
                )

            } else {
                ingredientCallback.onIngredientClicked(
                    tvName.text.toString().capitalize(Locale.ROOT)
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