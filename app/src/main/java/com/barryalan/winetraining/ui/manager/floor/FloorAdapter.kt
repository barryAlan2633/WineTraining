package com.barryalan.winetraining.ui.manager.floor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.model.floor.Floor
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import java.util.*

class FloorAdapter(
    private val floorList: ArrayList<Floor>,
    private val floorCallback: FloorCallback
) : RecyclerView.Adapter<FloorAdapter.FloorViewHolder>() {
    class FloorViewHolder(var view: View) : RecyclerView.ViewHolder(view)

    override fun getItemCount() = floorList.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FloorViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_floor, parent, false)
        return FloorViewHolder(view)
    }

    fun getFloorList() = floorList

    override fun onBindViewHolder(holder: FloorViewHolder, position: Int) {
        val tvFloorName: MaterialTextView = holder.view.findViewById(R.id.name_item_floor)
        val btnEditFloor: MaterialButton = holder.view.findViewById(R.id.btn_edit_floor_item_floor)
        val btnArrangeTables: MaterialButton =
            holder.view.findViewById(R.id.btn_arrange_tables_item_floor)
        val btnEditSections: MaterialButton =
            holder.view.findViewById(R.id.btn_section_editor_item_floor)

        tvFloorName.text = floorList[holder.adapterPosition].name.capitalize(Locale.ROOT)

        btnEditFloor.setOnClickListener {
            floorCallback.onEditFloor(floorList[holder.adapterPosition].id)
        }

        btnArrangeTables.setOnClickListener {
            floorCallback.onArrangeTables(floorList[holder.adapterPosition].id)
        }

        btnEditSections.setOnClickListener {
            floorCallback.onEditSections(floorList[holder.adapterPosition].id)
        }
    }

    fun updateFloorList(updatedFloorList: List<Floor>) {
        floorList.clear()
        floorList.addAll(updatedFloorList)
        notifyDataSetChanged()
    }

    fun addTableType(floor: Floor) {
        floorList.add(floor)
        notifyItemInserted(itemCount)
    }


}