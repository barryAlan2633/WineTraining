package com.barryalan.winetraining.ui.questionList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.model.Question
import com.barryalan.winetraining.ui.shared.OnItemClickListener
import java.util.*
import kotlin.collections.ArrayList

class QuestionAdapter(
    val questionList: MutableList<Question>,
    private val onItemClickListener: OnItemClickListener,

    ) : RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>(), Filterable {

    private var filteredQuestionList = questionList

    class QuestionViewHolder(var view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QuestionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item_question, parent, false)
        return QuestionViewHolder(view)
    }

    override fun getItemCount() = filteredQuestionList.size

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.view.findViewById<TextView>(R.id.tv_question_question)?.text =
            filteredQuestionList[position].question
        holder.view.findViewById<TextView>(R.id.tv_question_type)?.text =
            filteredQuestionList[position].type
        holder.view.findViewById<CardView>(R.id.card_item_question)?.setOnClickListener {
            onItemClickListener.onItemClick(position)
        }

    }

    fun updateListAdd(updatedQuestionList: MutableList<Question>) {
        questionList.clear()
        questionList.addAll(updatedQuestionList)
        notifyDataSetChanged()
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val charSearch = constraint.toString()
                filteredQuestionList = if (charSearch.isEmpty()) {
                    questionList
                } else {
                    val resultList = ArrayList<Question>()
                    for (question in questionList) {
                        if (question.type.toLowerCase(Locale.ROOT).trim().contains(
                                charSearch.toLowerCase(Locale.ROOT).trim()
                            )
                        ) {
                            resultList.add(question)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredQuestionList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredQuestionList = results?.values as ArrayList<Question>
                notifyDataSetChanged()
            }

        }
    }

}