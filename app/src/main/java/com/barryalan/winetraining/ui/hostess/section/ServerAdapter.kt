package com.barryalan.winetraining.ui.hostess.section

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.model.employee.Employee
import com.barryalan.winetraining.model.employee.with.ServerWithSection
import com.barryalan.winetraining.model.floor.SectionColor
import com.google.android.material.textview.MaterialTextView
import java.util.*


class ServerAdapter(
    private val serverList: MutableList<ServerWithSection>,
    private val serverCallback: ServerCallback
) : RecyclerView.Adapter<ServerAdapter.SectionViewHolder>() {

    class SectionViewHolder(var view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_server, parent, false)
        return SectionViewHolder(view)
    }

    override fun getItemCount() = serverList.size

    private val sectionColors = mutableListOf<SectionColor>()

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val tvAssignedSectionColor =
            holder.view.findViewById<MaterialTextView>(R.id.tv_sectionColor_item_server)
        val tvAssignedSection =
            holder.view.findViewById<MaterialTextView>(R.id.tv_assignedSection_item_server)
        val cbCount = holder.view.findViewById<CheckBox>(R.id.cb_count_item_server)


        holder.view.findViewById<MaterialTextView>(R.id.tv_name_item_server).text =
            serverList[holder.adapterPosition].employee.name.capitalize(Locale.ROOT)



        if (serverList[holder.adapterPosition].section != null) {
            ("Section #${serverList[holder.adapterPosition].section?.section?.number}:\n " +
                    "${serverList[holder.adapterPosition].section?.tables?.map { it.number }}").also {
                tvAssignedSection.text = it
            }

            cbCount.isChecked = true


            sectionColors.map {
                if (it.number == serverList[holder.adapterPosition].section!!.section.number) {
                    tvAssignedSectionColor.setBackgroundColor(it.color)
                }
            }


        }else{
            tvAssignedSection.text = ""
            tvAssignedSectionColor.setBackgroundColor(Color.WHITE)
            cbCount.isChecked = false
        }


        cbCount.setOnClickListener {
            serverCallback.onServerChecked(serverList[holder.adapterPosition])
        }
    }

    fun updateList(updatedList: List<ServerWithSection>) {
        serverList.clear()
        serverList.addAll(updatedList)
        notifyDataSetChanged()
    }

    fun updateSectionColors(sectionColors: List<SectionColor>) {
        this.sectionColors.clear()
        this.sectionColors.addAll(sectionColors)
        notifyDataSetChanged()
    }

//    fun updateCheckedServers(checkedServers: List<Employee>) {
//        this.checkedServers.clear()
//        this.checkedServers.addAll(checkedServers)
//
//        notifyDataSetChanged()
//    }

//    override fun onViewRecycled(holder: SectionViewHolder) {
//        holder.view.findViewById<CheckBox>(R.id.cb_count_item_server).isChecked = false
//        holder.view.findViewById<TextView>(R.id.tv_sectionColor_item_server)
//            .setBackgroundColor(Color.WHITE)
//
//        super.onViewRecycled(holder)
//    }


}