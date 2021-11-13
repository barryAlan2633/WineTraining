package com.barryalan.winetraining.ui.manager.floor

import com.barryalan.winetraining.R


import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.barryalan.winetraining.customViews.TableView
import com.barryalan.winetraining.model.floor.Table
import com.barryalan.winetraining.util.StaticIntegers.Companion._ROTATION
import com.barryalan.winetraining.util.StaticIntegers.Companion._X
import com.barryalan.winetraining.util.StaticIntegers.Companion._Y
import com.google.android.material.textview.MaterialTextView
import kotlin.collections.ArrayList


class SelectedTableAdapter(
    private val selectedTableList: MutableList<Table>,
    private val metrics: DisplayMetrics,
    private val tableCallback: TableCallback
) : RecyclerView.Adapter<SelectedTableAdapter.SelectedTableViewHolder>(), Filterable {

    private var filteredSelectedTableList = selectedTableList

    class SelectedTableViewHolder(var view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectedTableViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_selected_table, parent, false)
        return SelectedTableViewHolder(view)
    }

    override fun getItemCount() = filteredSelectedTableList.size

    override fun onBindViewHolder(holder: SelectedTableViewHolder, position: Int) {

        val tableSelected = holder.view.findViewById<EditText>(R.id.et_table_item_table_pressed)
        val sbSelectedTableItemRotation =
            holder.view.findViewById<SeekBar>(R.id.sb_rotation_item_table_pressed)
        val etSelectedTableItemRotation =
            holder.view.findViewById<EditText>(R.id.et_rotation_item_table_pressed)

        tableSelected.updateLayoutParams {
            this.width =
                (filteredSelectedTableList[position].length * metrics.scaledDensity).toInt()
            this.height =
                (filteredSelectedTableList[position].height * metrics.scaledDensity).toInt()
        }


        tableSelected.setText(filteredSelectedTableList[holder.adapterPosition].number.toString())


        tableSelected.rotation = filteredSelectedTableList[holder.adapterPosition].rotation

        sbSelectedTableItemRotation.progress =
            filteredSelectedTableList[holder.adapterPosition].rotation.toInt()

        etSelectedTableItemRotation.setText(
            selectedTableList[holder.adapterPosition].rotation.toInt().toString()
        )


        tableSelected.addTextChangedListener(object : TextWatcher {


            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val newNumber: Int

                if (s.isNullOrBlank()) {
                    newNumber = 0
                } else {
                    newNumber = s.toString().trim().toInt()
                }

                tableCallback.changeTableNumber(holder.adapterPosition, newNumber)
            }

            override fun afterTextChanged(editText: Editable?) {

            }
        })

        etSelectedTableItemRotation.setOnFocusChangeListener { v, hasFocus ->

            if (!hasFocus) {
                val etRotationText = etSelectedTableItemRotation.text.toString()

                if (etRotationText != "") {
                    sbSelectedTableItemRotation.progress =
                        when {
                            etRotationText.toInt() < 0 -> {
                                0
                            }
                            etRotationText.toInt() > 359 -> {
                                359
                            }
                            else -> {
                                etRotationText.toInt()
                            }
                        }
                } else {
                    sbSelectedTableItemRotation.progress = 0
                }
            }

        }


        sbSelectedTableItemRotation.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {

                tableSelected.rotation = (progress.toFloat())

                etSelectedTableItemRotation.setText(progress.toString())

                tableCallback.changeTableRotation(
                    holder.adapterPosition,
                    progress.toFloat()
                )


            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        holder.view.findViewById<MaterialTextView>(R.id.btn_align_x_item_table_pressed).setOnClickListener {
            tableCallback.alignTable(holder.adapterPosition, _X)
        }

        holder.view.findViewById<MaterialTextView>(R.id.btn_align_y_item_table_pressed).setOnClickListener {


            tableCallback.alignTable(holder.adapterPosition, _Y)
        }

        holder.view.findViewById<MaterialTextView>(R.id.btn_align_rotation_item_table_pressed)
            .setOnClickListener {
                tableCallback.alignTable(holder.adapterPosition, _ROTATION)

                selectedTableList.map {
                    it.rotation = selectedTableList[holder.adapterPosition].rotation
                }
                notifyDataSetChanged()
            }

    }

    fun updateList(updatedSelectedTableList: MutableList<Table>) {
        selectedTableList.clear()
        selectedTableList.addAll(updatedSelectedTableList)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val charSearch = constraint.toString()
                filteredSelectedTableList = if (charSearch.isEmpty()) {
                    selectedTableList
                } else {
                    val resultList = ArrayList<Table>()
                    for (table in selectedTableList) {
                        if (table.number.toString().contains(charSearch)) {
                            resultList.add(table)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredSelectedTableList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredSelectedTableList = results?.values as ArrayList<Table>
                notifyDataSetChanged()
            }

        }
    }
}


