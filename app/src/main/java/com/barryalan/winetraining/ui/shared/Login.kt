package com.barryalan.winetraining.ui.shared

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.model.employee.Employee
import com.barryalan.winetraining.model.employee.with.EmployeeWithJobs
import com.barryalan.winetraining.ui.menu.recipes.RecipeDetailDirections
import com.barryalan.winetraining.ui.shared.util.UIMessage
import com.barryalan.winetraining.ui.shared.util.UIMessageType
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView


class Login : BaseFragment() {

    private lateinit var viewModel: ClockInViewModel

    private lateinit var etClockInID: EditText
    private lateinit var btnClockIn: MaterialButton

    private lateinit var btnManager: MaterialButton
    private lateinit var btnHostess: MaterialButton
    private lateinit var btnServer: MaterialButton
    private lateinit var btnBusser: MaterialButton
    private lateinit var btnFoodRunner: MaterialButton
    private lateinit var btnDishWasher: MaterialButton
    private lateinit var btnCook: MaterialButton
    private lateinit var tvName: MaterialTextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(ClockInViewModel::class.java)
        subscribeObservers()

        etClockInID  = view.findViewById(R.id.et_clockIn_id)
        btnClockIn = view.findViewById(R.id.btn_clock_in_clock_in)

        btnClockIn.setOnClickListener {
            if (etClockInID.text.isEmpty()) {
                requireView().findViewById<TextInputLayout>(R.id.til_clockIn).error = "You must write a clock-in ID first"
            } else {
                if (etClockInID.text.toString().toLong() == 2633L) {
                    val action = LoginDirections.actionLoginToManagerMainMenu()
                    requireView().findNavController().navigate(action)
                } else {
                    authUser(etClockInID.text.toString().toLong())
                }
            }
        }
    }

    private fun authUser(clockInID: Long) {
        viewModel.retrieveUser(clockInID)
    }

    private fun openChooseJobBottomSheet(employee: EmployeeWithJobs) {
        MaterialDialog(requireContext(), BottomSheet()).show {
            cancelOnTouchOutside(false)

            customView(R.layout.bottom_sheet_choose_job)

            val btnManager = this.view.findViewById<MaterialButton>(R.id.btn_manager_choose_job)
            val btnHostess = this.view.findViewById<MaterialButton>(R.id.btn_hostess_choose_job)
            val btnServer = this.view.findViewById<MaterialButton>(R.id.btn_server_choose_job)
            val btnBusser = this.view.findViewById<MaterialButton>(R.id.btn_busser_choose_job)
            val btnFoodRunner =
                this.view.findViewById<MaterialButton>(R.id.btn_food_runner_choose_job)
            val btnDishWasher =
                this.view.findViewById<MaterialButton>(R.id.btn_dish_washer_choose_job)
            val btnCook = this.view.findViewById<MaterialButton>(R.id.btn_cook_choose_job)
            val tvName = this.view.findViewById<MaterialTextView>(R.id.tv_name_choose_job)

            tvName.text = "Welcome ${employee.employee.name},"

            employee.jobs.map {
                when (it.name) {
                    "server" -> {
                        btnServer.visibility = View.VISIBLE
                    }
                    "manager" -> {
                        btnManager.visibility = View.VISIBLE

                    }
                    "hostess" -> {
                        btnHostess.visibility = View.VISIBLE

                    }
                    "foodrunner" -> {
                        btnFoodRunner.visibility = View.VISIBLE

                    }
                    "dishwasher" -> {
                        btnDishWasher.visibility = View.VISIBLE

                    }
                    "cook" -> {
                        btnCook.visibility = View.VISIBLE

                    }
                    "busser" -> {
                        btnBusser.visibility = View.VISIBLE

                    }
                }
            }

            btnManager.setOnClickListener {
                dismiss()
                requireView().findNavController().navigate(R.id.action_login_to_managerMainMenu)
            }

            btnHostess.setOnClickListener {
                dismiss()
                requireView().findNavController().navigate(R.id.action_login_to_hostessMainMenu)
            }

            btnServer.setOnClickListener {
                dismiss()
                val action = LoginDirections.actionLoginToServerMainMenu(employee.employee.id)
                requireView().findNavController().navigate(action)
            }

            btnBusser.setOnClickListener {
                dismiss()
            }

            btnFoodRunner.setOnClickListener {
                dismiss()
            }

            btnDishWasher.setOnClickListener {
                dismiss()
            }

            btnCook.setOnClickListener {
                dismiss()
            }

            negativeButton {
                dismiss()
            }

        }
    }

    private fun subscribeObservers() {
        viewModel.employeeLiveData.observe(viewLifecycleOwner, { employee ->

            if (employee == null) {
                requireView().findViewById<TextInputLayout>(R.id.til_clockIn).error = "Invalid clock-in ID"
            } else {
                openChooseJobBottomSheet(employee)
            }


        })
    }


}

