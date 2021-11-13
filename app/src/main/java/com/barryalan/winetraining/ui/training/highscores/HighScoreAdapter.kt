package com.barryalan.winetraining.ui.training.highscores

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.barryalan.winetraining.R
import com.barryalan.winetraining.model.menu.Score
import java.util.*

open class HighScoreAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Score>() {

        override fun areItemsTheSame(oldItem: Score, newItem: Score): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Score, newItem: Score): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return HighScoreViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_score,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HighScoreViewHolder -> {


                holder.bind(differ.currentList[position])

            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Score>) {
        differ.submitList(list)
    }

    class HighScoreViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Score) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }


            itemView.findViewById<TextView>(R.id.tv_score_item_score)?.text =
                item.score.toString()
            itemView.findViewById<TextView>(R.id.tv_player_name_item_score)?.text =
                item.name.capitalize(
                    Locale.ROOT
                )
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Score)
    }
}
