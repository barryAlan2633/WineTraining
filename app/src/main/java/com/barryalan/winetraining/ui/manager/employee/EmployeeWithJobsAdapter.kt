package com.barryalan.winetraining.ui.manager.employee


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.model.employee.with.EmployeeWithJobs
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textview.MaterialTextView
import java.util.*
import kotlin.collections.ArrayList


class EmployeeWithJobsAdapter(
    private val employeeList: MutableList<EmployeeWithJobs>,
    private val onEmployeeClickListener: EmployeeCallback,

    ) : RecyclerView.Adapter<EmployeeWithJobsAdapter.EmployeeWithJobsViewHolder>(), Filterable {

    private var filteredEmployeeList = employeeList
    private var employeeSort: Int = R.id.chip_name_asc_manager_employee

    class EmployeeWithJobsViewHolder(var view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EmployeeWithJobsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_employee, parent, false)
        return EmployeeWithJobsViewHolder(view)
    }

    override fun getItemCount() = filteredEmployeeList.size

    override fun onBindViewHolder(holder: EmployeeWithJobsViewHolder, position: Int) {

        holder.view.findViewById<MaterialTextView>(R.id.tv_name_item_employee).text =
            filteredEmployeeList[holder.adapterPosition].employee.name.capitalize(Locale.ROOT)
        holder.view.findViewById<MaterialTextView>(R.id.tv_clock_in_id_item_employee).text =
            filteredEmployeeList[holder.adapterPosition].employee.clockInID.toString()
        val jobsChipGroup = holder.view.findViewById<ChipGroup>(R.id.chip_group_jobs_item_employee)

        jobsChipGroup.removeAllViews()
        filteredEmployeeList[holder.adapterPosition].jobs.map {
            val chip = Chip(holder.view.context)
            chip.text = it.name.capitalize(Locale.ROOT)
            jobsChipGroup.addView(chip)
        }

        holder.view.setOnClickListener {
            onEmployeeClickListener.employeeClicked(filteredEmployeeList[holder.adapterPosition])
        }
    }

    fun updateEmployeeList(updatedEmployeeList: MutableList<EmployeeWithJobs>) {
        employeeList.clear()
        employeeList.addAll(sortEmployees(updatedEmployeeList))
        notifyDataSetChanged()
    }

    fun updateSort(sortID: Int) {
        if (sortID != -1) {
            employeeSort = sortID
            filteredEmployeeList = ArrayList(sortEmployees(filteredEmployeeList))
            notifyDataSetChanged()
        }
    }

    private fun sortEmployees(employeeList: List<EmployeeWithJobs>): List<EmployeeWithJobs> {
        return when (employeeSort) {
            R.id.chip_name_asc_manager_employee -> {
                employeeList.sortedBy { it.employee.name }
            }
            R.id.chip_name_desc_manager_employee -> {
                employeeList.sortedByDescending { it.employee.name }
            }
            else -> {
                employeeList
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val charSearch = constraint.toString()
                filteredEmployeeList = if (charSearch.isEmpty()) {
                    employeeList
                } else {
                    val resultList = ArrayList<EmployeeWithJobs>()
                    for (employee in employeeList) {
                        if (employee.employee.name.toLowerCase(Locale.ROOT).contains(
                                charSearch.toLowerCase(
                                    Locale.ROOT
                                )
                            )
                        ) {
                            resultList.add(employee)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredEmployeeList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredEmployeeList = results?.values as ArrayList<EmployeeWithJobs>
                notifyDataSetChanged()
            }

        }
    }
}