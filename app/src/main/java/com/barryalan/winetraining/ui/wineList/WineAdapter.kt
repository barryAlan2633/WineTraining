package com.barryalan.winetraining.ui.wineList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.model.menu.Wine
import com.barryalan.winetraining.ui.shared.OnItemClickListener
import java.util.*
import kotlin.collections.ArrayList

class WineAdapter(
    val wineList: MutableList<Wine>,
    private val onItemClickListener: OnItemClickListener,

    ) : RecyclerView.Adapter<WineAdapter.WineViewHolder>(), Filterable {

    private var filteredWineList = wineList

    class WineViewHolder(var view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WineViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item_wine, parent, false)
        return WineViewHolder(view)
    }

    override fun getItemCount() = filteredWineList.size

    override fun onBindViewHolder(holder: WineViewHolder, position: Int) {
        holder.view.findViewById<TextView>(R.id.tv_player_name_item_score)?.text =
            filteredWineList[holder.adapterPosition].name
        holder.view.findViewById<TextView>(R.id.tv_category_item_score)?.text =
            filteredWineList[holder.adapterPosition].category
        holder.view.findViewById<TextView>(R.id.tv_score_item_score)?.text =
            filteredWineList[holder.adapterPosition].glassPrice.toString()
        holder.view.findViewById<TextView>(R.id.tv_bottlePrice)?.text =
            filteredWineList[holder.adapterPosition].bottlePrice.toString()
        holder.view.findViewById<CardView>(R.id.card_item_wine)
            .setOnClickListener { onItemClickListener.onItemClick(holder.adapterPosition) }
    }

    fun updateListAdd(updatedWineList: MutableList<Wine>) {
        wineList.clear()
        wineList.addAll(updatedWineList)
        notifyDataSetChanged()
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val charSearch = constraint.toString()
                filteredWineList = if (charSearch.isEmpty()) {
                    wineList
                } else {
                    val resultList = ArrayList<Wine>()
                    for (wine in wineList) {
                        if (wine.name.toLowerCase(Locale.ROOT).trim().contains(
                                charSearch.toLowerCase(Locale.ROOT).trim())) {
                            resultList.add(wine)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredWineList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredWineList = results?.values as ArrayList<Wine>
                notifyDataSetChanged()
            }

        }
    }
}