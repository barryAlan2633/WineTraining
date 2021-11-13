package com.barryalan.winetraining.ui.manager.employee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat.getColorStateList
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.model.employee.Employee
import com.barryalan.winetraining.model.employee.Job
import com.barryalan.winetraining.model.employee.with.EmployeeWithJobs
import com.barryalan.winetraining.ui.shared.BaseFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ManagerEmployee : BaseFragment(), EmployeeCallback {

    private var employeeAdapter = EmployeeWithJobsAdapter(arrayListOf(), this)
    private lateinit var viewModel: ManagerEmployeeViewModel

    private lateinit var btnNewEmployee: MaterialButton
    private lateinit var rvEmployees: RecyclerView
    private lateinit var tvGuide: MaterialTextView
    private lateinit var employeeSearch: SearchView
    private lateinit var filterChipGroup: ChipGroup
    private lateinit var sortChipGroup: ChipGroup
    private lateinit var ivAdvancedSearch: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_manager_employee, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnNewEmployee = view.findViewById(R.id.btn_new_employee_manager_employee)
        rvEmployees = view.findViewById(R.id.rv_employee_manager_employee)
        tvGuide = view.findViewById(R.id.tv_guide_manager_employee)
        employeeSearch = view.findViewById(R.id.employee_search_manager_employee)
        sortChipGroup = view.findViewById(R.id.chip_group_sort_manager_employee)
        ivAdvancedSearch = view.findViewById(R.id.iv_advanced_search_manager_employee)

        viewModel = ViewModelProvider(this).get(ManagerEmployeeViewModel::class.java)
        viewModel.refresh()
        subscribeObservers()

        initEmployeeRecycler()

        btnNewEmployee.setOnClickListener { openEmployeeBottomSheet(null) }

        sortChipGroup.setOnCheckedChangeListener { group, checkedId ->
            viewModel.searchSortByLiveData.value = checkedId
        }


        ivAdvancedSearch.setOnClickListener { openAdvancedSearch() }

    }

    private fun initEmployeeRecycler() {
        //setup rv and search
        rvEmployees.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = employeeAdapter
        }

        employeeSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                employeeAdapter.filter.filter(newText)
                return false
            }

        })
    }

    private fun openEmployeeBottomSheet(employeeToEdit: EmployeeWithJobs?) {
        MaterialDialog(requireContext(), BottomSheet()).show {
            this.noAutoDismiss()
            cancelOnTouchOutside(false)

            //setup bottom sheet
            customView(R.layout.bottom_sheet_new_employee)
            val etEmployeeName =
                this.view.findViewById<EditText>(R.id.et_name_new_employee)
            val etEmployeeClockInID =
                this.view.findViewById<EditText>(R.id.et_clock_in_id_new_employee)
            val btnManager =
                this.view.findViewById<MaterialButton>(R.id.btn_manager_new_employee)
            val btnServer =
                this.view.findViewById<MaterialButton>(R.id.btn_server_new_employee)
            val btnHostess =
                this.view.findViewById<MaterialButton>(R.id.btn_hostess_new_employee)
            val btnCook =
                this.view.findViewById<MaterialButton>(R.id.btn_cook_new_employee)
            val btnBusser =
                this.view.findViewById<MaterialButton>(R.id.btn_busser_new_employee)
            val btnDishWasher =
                this.view.findViewById<MaterialButton>(R.id.btn_dish_washer_new_employee)
            val btnFoodRunner =
                this.view.findViewById<MaterialButton>(R.id.btn_food_runner_new_employee)
            val btnBarTender =
                this.view.findViewById<MaterialButton>(R.id.btn_bar_tender_new_employee)
            val tilName =
                this.view.findViewById<TextInputLayout>(R.id.textInputLayout_name_new_employee)
            val tilClockInID =
                this.view.findViewById<TextInputLayout>(R.id.textInputLayout_clock_in_id_new_employee)

            title(R.string.dialog_title_new_employee)
            negativeButton(R.string.dialog_button_negative) { dismiss() }
            positiveButton(R.string.dialog_button_positive) {
                tilName.error = null
                tilClockInID.error = null
                when {
                    etEmployeeName.text.isEmpty() -> {
                        tilName.error = "Employee must have a name"
                    }
                    etEmployeeClockInID.text.isEmpty() -> {
                        tilClockInID.error = "Employee must have a clock in ID"
                    }
                    else -> {
                        val newEmployeeJobs = mutableListOf<Job>()
                        if (btnManager.isActivated) {
                            newEmployeeJobs.add(Job("manager"))
                        }
                        if (btnServer.isActivated) {
                            newEmployeeJobs.add(Job("server"))
                        }
                        if (btnHostess.isActivated) {
                            newEmployeeJobs.add(Job("hostess"))
                        }
                        if (btnCook.isActivated) {
                            newEmployeeJobs.add(Job("cook"))
                        }
                        if (btnBusser.isActivated) {
                            newEmployeeJobs.add(Job("busser"))
                        }
                        if (btnDishWasher.isActivated) {
                            newEmployeeJobs.add(Job("dishwasher"))
                        }
                        if (btnFoodRunner.isActivated) {
                            newEmployeeJobs.add(Job("foodrunner"))
                        }
                        if (btnBarTender.isActivated) {
                            newEmployeeJobs.add(Job("bartender"))
                        }

                        if (etEmployeeName.text.toString() == "alan") {
                            CoroutineScope(IO).launch {

                                withContext(IO) {
                                    viewModel.initGloriasServers()

                                }

                                withContext(Dispatchers.Main) {
                                    dismiss()
                                }
                            }
                        } else {
                            val newEmployee = EmployeeWithJobs(
                                Employee(
                                    0,
                                    etEmployeeClockInID.text.toString().toLong(),
                                    etEmployeeName.text.toString()
                                ),
                                newEmployeeJobs
                            )

                            CoroutineScope(IO).launch {

                                withContext(IO) {
                                    if (employeeToEdit != null) {
                                        newEmployee.employee.id = employeeToEdit.employee.id
                                        viewModel.updateEmployee(newEmployee, employeeToEdit)
                                    } else {
                                        viewModel.createNewEmployee(newEmployee)
                                    }
                                }

                                withContext(Dispatchers.Main) {
                                    dismiss()
                                }
                            }
                        }


                    }
                }


            }
            btnManager.setOnClickListener { switchViewActivation(it) }
            btnServer.setOnClickListener { switchViewActivation(it) }
            btnHostess.setOnClickListener { switchViewActivation(it) }
            btnCook.setOnClickListener { switchViewActivation(it) }
            btnBusser.setOnClickListener { switchViewActivation(it) }
            btnDishWasher.setOnClickListener { switchViewActivation(it) }
            btnFoodRunner.setOnClickListener { switchViewActivation(it) }
            btnBarTender.setOnClickListener { switchViewActivation(it) }


            //handle data
            if (employeeToEdit != null) {
                etEmployeeName.setText(employeeToEdit.employee.name)
                etEmployeeClockInID.setText(employeeToEdit.employee.clockInID.toString())

                employeeToEdit.jobs.map {
                    when (it.name) {
                        "manager" -> {
                            btnManager.isActivated = true
                        }
                        "server" -> {
                            btnServer.isActivated = true
                        }
                        "hostess" -> {
                            btnHostess.isActivated = true
                        }
                        "cook" -> {
                            btnCook.isActivated = true
                        }
                        "busser" -> {
                            btnBusser.isActivated = true
                        }
                        "dishwasher" -> {
                            btnDishWasher.isActivated = true
                        }
                        "foodrunner" -> {
                            btnFoodRunner.isActivated = true
                        }
                        "bartender" -> {
                            btnBarTender.isActivated = true
                        }
                    }
                }
            }

        }
    }

    private fun switchViewActivation(v: View) {
        v.isActivated = !v.isActivated
    }

    private fun openAdvancedSearch() {
        MaterialDialog(requireContext(), BottomSheet()).show {
            cancelOnTouchOutside(false)


            //setup bottom sheet
            customView(R.layout.bottom_sheet_job_filter)
            val chipGroupJobsWanted =
                this.view.findViewById<ChipGroup>(R.id.chip_group_job_job_filter)

            title(R.string.advanced_search_options)
            positiveButton(R.string.apply) {
                viewModel.refresh()
            }


            viewModel.jobsLiveData.observe(viewLifecycleOwner, { filerList ->


                filerList.map { job ->
                    val chip = Chip(requireContext())
                    chip.text = job.name.capitalize(Locale.ROOT)
                    chip.chipBackgroundColor =
                        getColorStateList(requireContext(), R.color.color_chip_background)
                    chip.setOnClickListener {
                        chipGroupJobsWanted.check(it.id)
                        (it as Chip).isChecked = true
                    }
                    chipGroupJobsWanted.addView(chip)


                }

            })


        }
    }


    override fun employeeClicked(employee: EmployeeWithJobs) {
        openEmployeeBottomSheet(employee)
    }

    private fun subscribeObservers() {
        viewModel.employeesLiveData.observe(viewLifecycleOwner, {
            it?.let { employeesWithJobs ->
                if (employeesWithJobs.isNotEmpty()) {
                    tvGuide.visibility = GONE
                } else {
                    tvGuide.visibility = VISIBLE
                }
                employeeAdapter.updateEmployeeList(employeesWithJobs as MutableList<EmployeeWithJobs>)
            }
        })

        viewModel.searchSortByLiveData.observe(viewLifecycleOwner, {
            it?.let { sortID ->
                if (it == -1) {
                    sortChipGroup.check(R.id.chip_name_asc_manager_employee)
                } else {
                    sortChipGroup.check(it)
                }
                employeeAdapter.updateSort(sortID)
            }
        })

    }


}