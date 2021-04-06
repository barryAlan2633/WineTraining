package com.barryalan.winetraining.highscores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.shared.Score
import java.util.*
import kotlin.collections.ArrayList

class HighScoreAdapter(
    val scoreList: MutableList<Score>
    ) : RecyclerView.Adapter<HighScoreAdapter.ScoreViewHolder>(), Filterable {

    private var filteredScoreList = scoreList

    class ScoreViewHolder(var view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ScoreViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item_score, parent, false)
        return ScoreViewHolder(view)
    }

    override fun getItemCount() = filteredScoreList.size

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        holder.view.findViewById<TextView>(R.id.tv_score_item_score)?.text = scoreList[position].score.toString()
        holder.view.findViewById<TextView>(R.id.tv_player_name_item_score)?.text = scoreList[position].name.capitalize(
            Locale.ROOT)
    }

    fun updateList(updatedScoreList: MutableList<Score>) {
        scoreList.clear()
        scoreList.addAll(updatedScoreList)
        notifyDataSetChanged()
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val charSearch = constraint.toString()
                filteredScoreList = if (charSearch.isEmpty()) {
                    scoreList
                } else {
                    val resultList = ArrayList<Score>()
//                    for (employee in taskList) {
//                        if (employee.name.toLowerCase(Locale.ROOT).contains(
//                                charSearch.toLowerCase(
//                                    Locale.ROOT
//                                )
//                            )
//                        ) {
//                            resultList.add(employee)
//                        }
//                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredScoreList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredScoreList = results?.values as ArrayList<Score>
                notifyDataSetChanged()
            }

        }
    }
}