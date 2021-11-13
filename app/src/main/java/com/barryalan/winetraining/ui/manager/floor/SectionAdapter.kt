package com.barryalan.winetraining.ui.manager.floor


import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.model.floor.SectionColor
import com.barryalan.winetraining.model.floor.with.SectionWithTables
import com.google.android.material.textview.MaterialTextView

class SectionAdapter(
    private val sectionList: MutableList<SectionWithTables>,
    private val sectionCallback: SectionCallback,
    private val sectionColors: MutableList<SectionColor>
) : RecyclerView.Adapter<SectionAdapter.SectionViewHolder>() {
    class SectionViewHolder(var view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_section, parent, false)
        return SectionViewHolder(view)
    }

    override fun getItemCount() = sectionList.size


    var selectedSectionID = -1L


    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {

        val tvSectionNumber =
            holder.view.findViewById<MaterialTextView>(R.id.tv_section_number_item_section)
        val tvAssignedTables =
            holder.view.findViewById<MaterialTextView>(R.id.tv_assigned_tables_item_section)
        val tvSectionColor = holder.view.findViewById<MaterialTextView>(R.id.tv_section_color_item_section)
        val cvItemSection = holder.view.findViewById<CardView>(R.id.cv_item_selected_section)



        tvSectionNumber.text = sectionList[holder.adapterPosition].section.number.toString()

        var tableList = ""

        sectionList[holder.adapterPosition].tables.map {
            tableList = "$tableList ${it.number}"
        }

        tvAssignedTables.text = tableList

        if (!sectionColors.isNullOrEmpty()) {
            if (sectionColors.size >= holder.adapterPosition + 1) {
                tvSectionColor.setBackgroundColor(sectionColors[holder.adapterPosition].color)
            }
        }


        if (sectionList[holder.adapterPosition].section.id == selectedSectionID) {

            cvItemSection.animation = AnimationUtils.loadAnimation(holder.view.context, R.anim.anim_selected_item)
        } else {
            cvItemSection.setCardBackgroundColor(ColorStateList.valueOf(Color.WHITE))
        }

        cvItemSection.setOnClickListener {
            if (sectionList[holder.adapterPosition].section.id == selectedSectionID) {
                sectionCallback.onSectionSelected(-1L)
            } else {
                sectionCallback.onSectionSelected(sectionList[holder.adapterPosition].section.id)
            }
        }

    }


    fun updateSectionList(updatedSectionList: List<SectionWithTables>) {
        sectionList.clear()
        sectionList.addAll(updatedSectionList)


        notifyDataSetChanged()
    }

    fun updateSelectedSection(selectedSectionID: Long) {
        this.selectedSectionID = selectedSectionID

        notifyDataSetChanged()
    }

    fun updateSectionColors(updatedSectionColors: List<SectionColor>) {
        sectionColors.clear()
        sectionColors.addAll(updatedSectionColors)
        notifyDataSetChanged()
    }


}