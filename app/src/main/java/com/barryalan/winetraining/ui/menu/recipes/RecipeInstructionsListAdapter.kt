package com.barryalan.winetraining.ui.menu.recipes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.model.menu.Instruction
import com.google.android.material.textview.MaterialTextView
import java.util.*
import kotlin.collections.ArrayList

class RecipeInstructionsListAdapter(private val recipeInstructionList: ArrayList<Instruction>) :
    RecyclerView.Adapter<RecipeInstructionsListAdapter.RecipeInstructionsViewHolder>(), Filterable {

    class RecipeInstructionsViewHolder(var view: View) : RecyclerView.ViewHolder(view)

    private var filteredRecipeInstructionList = recipeInstructionList

    fun getInstructionList(): ArrayList<Instruction> = recipeInstructionList


    fun updateInstructionList(newInstructionList: List<Instruction>) {
        recipeInstructionList.clear()

        recipeInstructionList.addAll(newInstructionList)
        notifyDataSetChanged()
    }

    fun addInstruction(instruction: Instruction) {
        recipeInstructionList.add(instruction)
        notifyItemInserted(itemCount)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecipeInstructionsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_instruction, parent, false)
        return RecipeInstructionsViewHolder(
            view
        )
    }

    override fun getItemCount() = filteredRecipeInstructionList.size

    override fun onBindViewHolder(holder: RecipeInstructionsViewHolder, position: Int) {

        val tvInstructionText =
            holder.view.findViewById<MaterialTextView>(R.id.tv_instruction_text_item_instruction)
        val tvInstructionNumber =
            holder.view.findViewById<MaterialTextView>(R.id.tv_instruction_number_item_instruction)

        tvInstructionText.text =
            filteredRecipeInstructionList[holder.adapterPosition].instructionText.capitalize(Locale.ROOT)
        tvInstructionNumber.text = (holder.adapterPosition + 1).toString()

        //update correct instruction number for when the item is moved
        filteredRecipeInstructionList[holder.adapterPosition].instructionNumber =
            holder.adapterPosition + 1


    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                filteredRecipeInstructionList = if (charSearch.isEmpty()) {
                    recipeInstructionList
                } else {
                    val resultList = ArrayList<Instruction>()
                    for (instruction in recipeInstructionList) {
                        if (instruction.instructionText.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(instruction)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredRecipeInstructionList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredRecipeInstructionList = results?.values as ArrayList<Instruction>
                notifyDataSetChanged()
            }

        }
    }

}
