package com.barryalan.winetraining.ui.manager.floor


import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.customViews.TableView
import com.barryalan.winetraining.model.floor.TableType
import com.google.android.material.textview.MaterialTextView

class TableTypeAdapter(
    private val tableTypeList: MutableList<TableType>,
    private val tableTypeCallback: TableTypeCallback,
    private val metrics: DisplayMetrics,
) : RecyclerView.Adapter<TableTypeAdapter.TableTypeViewHolder>() {
    class TableTypeViewHolder(var view: View) : RecyclerView.ViewHolder(view)

    override fun getItemCount() = tableTypeList.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableTypeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_table_type, parent, false)
        return TableTypeViewHolder(view)
    }

    fun getTableTypeList() = tableTypeList

    override fun onBindViewHolder(holder: TableTypeViewHolder, position: Int) {
        val cvItemTableType: CardView = holder.view.findViewById(R.id.cv_item_table_type)
        val tvTableType: TableView = holder.view.findViewById(R.id.table_item_table_type)
        val tvTableTypeDimensions: TextView =
            holder.view.findViewById(R.id.tv_dimensions_item_table_type)


        tvTableType.updateLayoutParams {
            this.height =
                (tableTypeList[holder.adapterPosition].height * metrics.scaledDensity).toInt()
            this.width =
                (tableTypeList[holder.adapterPosition].length * metrics.scaledDensity).toInt()
        }

        "${tableTypeList[holder.adapterPosition].length} x ${tableTypeList[holder.adapterPosition].height}".also {
            tvTableTypeDimensions.text = it
        }

        when (tableTypeList[holder.adapterPosition].boothOrTable) {
            0 -> {
                "Booth for ${tableTypeList[holder.adapterPosition].maxNumberOfSeats}".also {
                    holder.view.findViewById<MaterialTextView>(
                        R.id.tv_description_item_table_type
                    ).text = it
                }
            }
            else -> {
                "Table for ${tableTypeList[holder.adapterPosition].maxNumberOfSeats}".also {
                    holder.view.findViewById<MaterialTextView>(
                        R.id.tv_description_item_table_type
                    ).text = it
                }
            }
        }

        cvItemTableType.setOnClickListener {
            tableTypeCallback.onTableTypePressed(tableTypeList[holder.adapterPosition])
        }

//        holder.view.findViewById<MaterialButton>(R.id.btn_delete_item_table_type).setOnClickListener {
//            onDeleteTableType.onDeleteTableType(holder.adapterPosition)
//        }


    }

    fun updateTableTypeList(updatedTableTypeList: MutableList<TableType>) {
        tableTypeList.clear()
        tableTypeList.addAll(updatedTableTypeList)
        notifyDataSetChanged()
    }

    fun addTableType(tableType: TableType) {
        tableTypeList.add(tableType)
        notifyItemInserted(itemCount)
    }


}