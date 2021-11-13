package com.barryalan.winetraining.ui.manager.floor

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.customViews.TableView
import com.barryalan.winetraining.model.floor.Floor
import com.barryalan.winetraining.model.floor.TableType
import com.barryalan.winetraining.model.floor.with.FloorWithTableTypes
import com.barryalan.winetraining.ui.shared.BaseFragment
import com.barryalan.winetraining.ui.shared.util.AreYouSureCallBack
import com.barryalan.winetraining.ui.shared.util.UIMessage
import com.barryalan.winetraining.ui.shared.util.UIMessageType
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout
import java.util.*


class ManagerFloorNewEdit : BaseFragment(), TableTypeCallback {

    private lateinit var btnNewTableType: MaterialButton
    private lateinit var btnSaveFloorPlan: MaterialButton
    private lateinit var btnDeleteFloorPlan: MaterialButton
    private lateinit var rvTableType: RecyclerView
    private lateinit var etFloorName: EditText

    private lateinit var metrics: DisplayMetrics

    private lateinit var tableTypeAdapter: TableTypeAdapter
    private lateinit var viewModel: ManagerFloorNewEditViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_manager_floor_new_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnNewTableType = view.findViewById(R.id.btn_new_table_type)
        btnSaveFloorPlan = view.findViewById(R.id.btn_save_floor_plan)
        btnDeleteFloorPlan = view.findViewById(R.id.btn_delete_floor_plan)
        rvTableType = view.findViewById(R.id.rv_table_type_new_table_menu)
        etFloorName = view.findViewById(R.id.et_name_manager_floor_new_edit)

        viewModel = ViewModelProvider(this).get(ManagerFloorNewEditViewModel::class.java)
        metrics = resources.displayMetrics
        tableTypeAdapter = TableTypeAdapter(arrayListOf(), this, metrics)

        arguments?.let {
            viewModel.retrieveChosenFloorFromDB(ManagerFloorNewEditArgs.fromBundle(it).floorUID)

            btnDeleteFloorPlan.visibility = VISIBLE
        }

        initTableTypeRecycler()
        subscribeObservers()

        btnNewTableType.setOnClickListener { openTableBottomSheet() }

        btnSaveFloorPlan.setOnClickListener {


            if (etFloorName.text.toString().isBlank()) {
                requireView().findViewById<TextInputLayout>(R.id.textInputLayout_manager_floor_new_edit).error = "Your floor plan must have a name"
            } else {
                viewModel.upsertChosenFloor(
                    FloorWithTableTypes(
                        Floor(etFloorName.text.toString()),
                        tableTypeAdapter.getTableTypeList()
                    )
                )

                view.findNavController().popBackStack()
            }

        }

        btnDeleteFloorPlan.setOnClickListener {
            val callback: AreYouSureCallBack = object : AreYouSureCallBack {
                override fun proceed() {
                    viewModel.deleteChosenFloor()
                    requireView().findNavController().navigateUp()
                }

                override fun cancel() {}
            }

            uiCommunicationListener.onUIMessageReceived(
                uiMessage = UIMessage(
                    "Are you sure you want to delete this floor plan? You will lose all data associated with it forever!",
                    UIMessageType.AreYouSureDialog(
                        callback
                    )
                )
            )
        }
    }

    private fun initTableTypeRecycler() {
        rvTableType.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = tableTypeAdapter
        }
    }

    private fun openTableBottomSheet() {
        MaterialDialog(requireContext(), BottomSheet()).show {
            cancelOnTouchOutside(false)
            noAutoDismiss()

            //setup bottom sheet
            customView(R.layout.bottom_sheet_table_type_menu)

            val btnDraw =
                this.view.findViewById<MaterialButton>(R.id.btn_draw_new_table_type_new_table_menu)
            val btnCreate =
                this.view.findViewById<MaterialButton>(R.id.btn_create_new_table_type_new_table_menu)
            val tableView =
                this.view.findViewById<TableView>(R.id.rv_table_type_new_table_menu)
            val etLength = this.view.findViewById<EditText>(R.id.et_length_new_table_menu)
            val etHeight = this.view.findViewById<EditText>(R.id.et_height_new_table_menu)
            val chipGroupTableBooth =
                this.view.findViewById<ChipGroup>(R.id.chip_group_table_or_booth_new_table_menu)
            val etNumberOfSeats =
                this.view.findViewById<EditText>(R.id.et_max_number_of_seats_new_table_menu)

            btnDraw.setOnClickListener {

                if (validateNewTableTypeForm(this)) {

                    tableView.apply {
                        updateLayoutParams {
                            this.height = (etHeight.text.toString()
                                .toInt() * metrics.scaledDensity).toInt()
                            this.width = (etLength.text.toString()
                                .toInt() * metrics.scaledDensity).toInt()
                        }
                        this.visibility = VISIBLE
                    }
                    btnCreate.visibility = VISIBLE
                } else {
                    btnCreate.visibility = GONE
                }

            }

            btnCreate.setOnClickListener {
                if (validateNewTableTypeForm(this)) {
                    val boothOrTable = when (chipGroupTableBooth.checkedChipId) {
                        R.id.chip_booth_new_table_menu -> {
                            0
                        }
                        R.id.chip_table_new_table_menu -> {
                            1
                        }
                        else -> {
                            -1
                        }
                    }

                    val newTableType = TableType(
                        etHeight.text.toString().toInt(),
                        etLength.text.toString().toInt(),
                        0f,
                        boothOrTable,
                        etNumberOfSeats.text.toString().toInt()
                    )

                    tableTypeAdapter.addTableType(newTableType)

                    dismiss()
                }
            }



            title(R.string.table_menu)
            negativeButton(R.string.cancel) {

                val callback: AreYouSureCallBack = object : AreYouSureCallBack {
                    override fun proceed() {
                        dismiss()
                    }

                    override fun cancel() {}

                }



                uiCommunicationListener.onUIMessageReceived(
                    uiMessage = UIMessage(
                        "Are you sure you are finished? all info that is not saved will be forever lost!",
                        UIMessageType.AreYouSureDialog(
                            callback
                        )
                    )
                )
            }


        }
    }

    private fun validateNewTableTypeForm(dialog: MaterialDialog): Boolean {
        val etNumberOfSeats =
            dialog.view.findViewById<EditText>(R.id.et_max_number_of_seats_new_table_menu)
        val etLength = dialog.view.findViewById<EditText>(R.id.et_length_new_table_menu)
        val etHeight = dialog.view.findViewById<EditText>(R.id.et_height_new_table_menu)
        val chipGroupTableBooth =
            dialog.view.findViewById<ChipGroup>(R.id.chip_group_table_or_booth_new_table_menu)

        when {
            chipGroupTableBooth.checkedChipId == NO_ID -> {
                uiCommunicationListener.onUIMessageReceived(
                    uiMessage = UIMessage(
                        "Is this a booth or table?",
                        UIMessageType.ErrorDialog()
                    )
                )
            }
            etNumberOfSeats.text.isEmpty() -> {
                etNumberOfSeats.error =
                    "What is the maximum number of people that you are willing to sit here"
            }
            etLength.text.isEmpty() -> {
                etLength.error = "What is the length of this table?"

            }
            etHeight.text.isEmpty() -> {
                etHeight.error = "What is the height of this table?"
            }

            else -> {
                return true
            }
        }
        return false
    }

    private fun subscribeObservers() {
        viewModel.chosenFloorLiveData.observe(viewLifecycleOwner, {
            it?.let {
                etFloorName.setText(it.floor.name.capitalize(Locale.ROOT))
                tableTypeAdapter.updateTableTypeList(it.allTableTypes)
            }
        })
    }

    override fun onTableTypePressed(tableType: TableType) {}
}