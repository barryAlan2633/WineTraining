package com.barryalan.winetraining.ui.highscores

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.model.Score
import java.util.*
import kotlin.collections.ArrayList

class HighScoreAdapter2(
    val scoreList: MutableList<Score>
) : RecyclerView.Adapter<HighScoreAdapter2.ScoreViewHolder>(), Filterable {

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
        holder.view.findViewById<TextView>(R.id.tv_score_item_score)?.text =
            scoreList[position].score.toString()
        holder.view.findViewById<TextView>(R.id.tv_player_name_item_score)?.text =
            scoreList[position].name.capitalize(
                Locale.ROOT
            )
//                "yyyy-MM-dd HH:mm:ss.SSSSSS"

        val date =
            scoreList[position].timeStamp.subSequence(0, scoreList[position].timeStamp.indexOf(";"))


        holder.view.findViewById<TextView>(R.id.tv_date_item_score)?.text = date.toString()


        Log.e("debug", "position:$position, ${scoreList[position]} ")


        val conLytScoreBackground =
            holder.view.findViewById<ConstraintLayout>(R.id.score_item_item_background)

        when (position) {
            0 -> {
                conLytScoreBackground.background =
                    ResourcesCompat.getDrawable(
                        holder.view.context.resources,
                        R.drawable.background_button_navigation_gold,
                        null
                    )
            }
            1 -> {
                conLytScoreBackground.background =
                    ResourcesCompat.getDrawable(
                        holder.view.context.resources,
                        R.drawable.background_button_navigation_silver,
                        null
                    )
            }
            2 -> {
                conLytScoreBackground.background =
                    ResourcesCompat.getDrawable(
                        holder.view.context.resources,
                        R.drawable.background_button_navigation_bronze,
                        null
                    )
            }
            else -> {
                conLytScoreBackground.background =
                    ResourcesCompat.getDrawable(
                        holder.view.context.resources,
                        R.drawable.background_button_navigation,
                        null
                    )
            }
        }
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