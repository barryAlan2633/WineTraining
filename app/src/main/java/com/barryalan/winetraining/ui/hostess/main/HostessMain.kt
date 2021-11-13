package com.barryalan.winetraining.ui.hostess.main

import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.afollestad.date.dayOfMonth
import com.afollestad.date.month
import com.afollestad.date.year
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.datetime.dateTimePicker
import com.barryalan.winetraining.R
import com.barryalan.winetraining.customViews.TableView
import com.barryalan.winetraining.model.customer.Customer
import com.barryalan.winetraining.model.employee.with.ServerWithSection
import com.barryalan.winetraining.model.floor.Table
import com.barryalan.winetraining.ui.shared.BaseFragment
import com.barryalan.winetraining.ui.shared.util.UIMessage
import com.barryalan.winetraining.ui.shared.util.UIMessageType
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import kotlin.math.floor

class HostessMain : BaseFragment(), AdapterView.OnItemSelectedListener {
    private lateinit var viewModel: HostessMainViewModel
    private lateinit var serverChoiceAdapter: ArrayAdapter<String>

    private lateinit var metrics: DisplayMetrics

    private lateinit var sbZoom: SeekBar
    private lateinit var lytFloor: ConstraintLayout
    private lateinit var lytOptions: LinearLayout
    private lateinit var lytNewGuest: LinearLayout
    private lateinit var btnNewGuest: MaterialButton
    private lateinit var btnSubmit: MaterialButton
    private lateinit var btnCancel: MaterialButton
    private lateinit var cbHighChair: CheckBox
    private lateinit var tipHighChair: TextInputLayout
    private lateinit var tvTableNumber: MaterialTextView
    private lateinit var cgTableBooth: ChipGroup
    private lateinit var etPartySize: EditText
    private lateinit var spServerChoice: Spinner
    private lateinit var cbReservation: CheckBox
    private lateinit var tvReservationDateTime: MaterialTextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hostess_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sbZoom = view.findViewById(R.id.sb_zoom_hostess_main)
        lytFloor = view.findViewById(R.id.con_lyt_floor_hostess_main)
        lytOptions = view.findViewById(R.id.lin_lyt_options_hostess_main)
        lytNewGuest = view.findViewById(R.id.lin_lyt_new_guest_hostess_main)
        btnNewGuest = view.findViewById(R.id.btn_new_guest_hostess_main)
        btnSubmit = view.findViewById(R.id.btn_submit_hostess_main)
        btnCancel = view.findViewById(R.id.btn_cancel_hostess_main)
        cbHighChair = view.findViewById(R.id.cb_high_chair_hostess_main)
        tipHighChair = view.findViewById(R.id.et_high_chair_amount_wrapper_hostess_main)
        tvTableNumber = view.findViewById(R.id.tv_selected_table_number_hostess_main)
        cgTableBooth = view.findViewById(R.id.cg_booth_or_table_hostess_main)
        etPartySize = view.findViewById(R.id.et_party_size_hostess_main)
        spServerChoice = view.findViewById(R.id.sp_server_choice_hostess_main)
        cbReservation = view.findViewById(R.id.cb_reservation_hostess_main)
        tvReservationDateTime = view.findViewById(R.id.tv_reservation_date_time_hostess_main)

        metrics = resources.displayMetrics

        viewModel = ViewModelProvider(this).get(HostessMainViewModel::class.java)
        viewModel.retrieveFloorFromDB(1)
        viewModel.retrieveServersWithSectionFromDB()
        subscribeObservers()

        sbZoom.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                viewModel.zoomLiveData.value = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnNewGuest.setOnClickListener {
            lytOptions.visibility = GONE
            lytNewGuest.visibility = VISIBLE
        }

        btnCancel.setOnClickListener {
            resetNewCustomerForm()
            lytNewGuest.visibility = GONE
            lytOptions.visibility = VISIBLE
            paintTables(viewModel.customerLiveData.value!!, "")
        }

        cgTableBooth.setOnCheckedChangeListener { group, checkedId ->

            when (checkedId) {
                R.id.chip_booth_hostess_main -> {
                    viewModel.floorWithTablesAndSectionsLiveData.value?.let {
                        it.allTables.map { table ->
                            var isTaken = false
                            viewModel.customerLiveData.value?.map { customer ->
                                if (customer.tableID == table.id) {
                                    isTaken = true
                                }
                            }
                            if (isTaken) {
                                requireView().findViewById<TableView>(table.id.toInt())
                                    ?.setTableColor(Color.RED)
                            } else {
                                if (table.boothOrTable == 0) {
                                    requireView().findViewById<TableView>(table.id.toInt())
                                        ?.setTableColor(Color.CYAN)

                                } else {
                                    requireView().findViewById<TableView>(table.id.toInt())
                                        ?.setTableColor(Color.GRAY)
                                }

                            }
                        }
                    }
                }

                R.id.chip_table_hostess_main -> {
                    viewModel.floorWithTablesAndSectionsLiveData.value?.let {
                        it.allTables.map { table ->


                            var isTaken = false
                            viewModel.customerLiveData.value?.map { customer ->
                                if (customer.tableID == table.id) {
                                    isTaken = true
                                }
                            }
                            if (isTaken) {
                                requireView().findViewById<TableView>(table.id.toInt())
                                    ?.setTableColor(Color.RED)
                            } else {
                                if (table.boothOrTable == 1) {
                                    requireView().findViewById<TableView>(table.id.toInt())
                                        ?.setTableColor(Color.CYAN)

                                } else {
                                    requireView().findViewById<TableView>(table.id.toInt())
                                        ?.setTableColor(Color.GRAY)
                                }

                            }
                        }
                    }
                }

                else -> {
                    paintTables(viewModel.customerLiveData.value!!, "")
                }
            }
        }

        cbHighChair.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                tipHighChair.visibility = VISIBLE
            } else {
                tipHighChair.visibility = GONE
            }
        }

        cbReservation.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                MaterialDialog(requireContext()).show {
                    cancelOnTouchOutside(false)
                    dateTimePicker(
                        requireFutureDateTime = true,
                        currentDateTime = java.util.Calendar.getInstance(),
                        minDateTime = java.util.Calendar.getInstance()
                    ) { _, dateTime ->
                        tvReservationDateTime.visibility = VISIBLE
                        tvReservationDateTime.text = dateTime.time.toString()
                    }

                    negativeButton {
                        cbReservation.isChecked = false
                    }

                    positiveButton {
                        cbReservation.isChecked = true
                    }

                }
            } else {
                tvReservationDateTime.visibility = GONE
                tvReservationDateTime.text = ""
            }
        }

        btnSubmit.setOnClickListener {

            if (etPartySize.text.isBlank()) {
                requireView().findViewById<TextInputLayout>(R.id.til_party_size_hostess_main).error =
                    "You must write number of guests"
            } else if (tvTableNumber.text == "") {
                uiCommunicationListener.onUIMessageReceived(
                    UIMessage(
                        "You must select a table on the floor first",
                        UIMessageType.Toast()
                    )
                )
            } else {

                if (spServerChoice.selectedItem.toString() == "No Selection") {

                    //-1 because we have to discount the no selection choice
                    viewModel.retrieveServerIDFromTableID(
                        tvTableNumber.text.toString().substring(
                            5, tvTableNumber.text.toString().indexOf("\n")
                        ).toLong(),
                        serverChoiceAdapter.count - 1
                    )
                } else {
                    viewModel.retrieveServerIDFromServerName(
                        spServerChoice.selectedItem.toString().substring(
                            spServerChoice.selectedItem.toString().indexOf(". ") + 2
                        )
                    )
                }
            }
        }
    }

    private fun initServerChoiceSpinner(servers: MutableList<String>) {

        servers.add(0, "No Selection")
        serverChoiceAdapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_small,
            servers
        ).also { adapter ->

            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

            // Apply the adapter to the spinner
            spServerChoice.adapter = adapter
            spServerChoice.onItemSelectedListener = this

        }
    }

    private fun resetNewCustomerForm() {
        etPartySize.text.clear()
        cgTableBooth.clearCheck()
        tvTableNumber.text = ""
    }

    private fun drawTables(tablesToDraw: List<Table>) {
        val metrics: DisplayMetrics = resources.displayMetrics

        lytFloor.removeAllViews()


        tablesToDraw.map { tableToDraw ->
            val tbNewTable = TableView(requireContext())

            //layout params
            tbNewTable.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

            tbNewTable.layoutParams = ConstraintLayout.LayoutParams(
                floor(tableToDraw.length * metrics.scaledDensity * viewModel.zoomLiveData.value!! / 100F).toInt(),
                floor(tableToDraw.height * metrics.scaledDensity * viewModel.zoomLiveData.value!! / 100F).toInt()
            )


            tbNewTable.x = tableToDraw._X!!.toFloat() * viewModel.zoomLiveData.value!! / 100F
            tbNewTable.y = tableToDraw._Y!!.toFloat() * viewModel.zoomLiveData.value!! / 100F
            tbNewTable.id = tableToDraw.id.toInt()
            tbNewTable.setBoothOrTable(tableToDraw.boothOrTable)


            //view specific params
            tbNewTable.rotation = tableToDraw.rotation
            tbNewTable.setTableRotation(tableToDraw.rotation)
            tbNewTable.setTableNumber(tableToDraw.number.toString())

            tbNewTable.setOnClickListener {
                (it as TableView)


                if (lytNewGuest.visibility == VISIBLE) {

                    if (it.getTableColor() == Color.RED) {
                        uiCommunicationListener.onUIMessageReceived(
                            UIMessage(
                                "Table ${it.getTableNumber()} is already taken!",
                                UIMessageType.Toast()
                            )
                        )
                    } else {

                        if (tvTableNumber.text == "ID = ${tbNewTable.id}\nNumber = ${tbNewTable.getTableNumber()}") {
                            tvTableNumber.text = ""
                            it.setTableColor(Color.GRAY)
                        } else {

                            if (tvTableNumber.text != "") {
                                requireView().findViewById<TableView>(
                                    tvTableNumber.text.toString().substring(
                                        5,
                                        tvTableNumber.text.toString().indexOf("\n")
                                    ).toInt()
                                )?.setTableColor(Color.GRAY)
                            }

                            "ID = ${tbNewTable.id}\nNumber = ${tbNewTable.getTableNumber()}".also { string ->
                                tvTableNumber.text = string
                            }
                            it.setTableColor(Color.YELLOW)

                        }
                    }
                } else {
                    openSummonBottomSheet(it.id.toLong())
                }
            }



            lytFloor.addView(tbNewTable)

        }


    }

    private fun openSummonBottomSheet(tableID: Long) {
        MaterialDialog(requireContext(), BottomSheet()).show {
            cancelOnTouchOutside(false)

            customView(R.layout.bottom_sheet_summon)

            val btnManager =
                this.view.findViewById<Button>(R.id.btn_manager_summon_bottom_sheet)
            val btnServer =
                this.view.findViewById<Button>(R.id.btn_server_summon_bottom_sheet)
            val btnHostess =
                this.view.findViewById<Button>(R.id.btn_hostess_summon_bottom_sheet)
            val btnBusser =
                this.view.findViewById<Button>(R.id.btn_busser_summon_bottom_sheet)
            val btnFoodRunner =
                this.view.findViewById<Button>(R.id.btn_food_runner_summon_bottom_sheet)
            val btnBarTender =
                this.view.findViewById<Button>(R.id.btn_bar_tender_summon_bottom_sheet)


            title(R.string.summon)

            negativeButton(R.string.dialog_button_negative) {
            }


            positiveButton(R.string.dialog_button_positive) {
//                if (btnBusser.isActivated) {
//                    viewModel.floorPlanWithTablesAndSectionsLiveData.value?.allTables?.map {
//                        if (it.id == tableID) {
//                            it.summon = 1
//
//                            viewModel.saveTable(it)
//                        }
//                    }
//
//                }

            }

            btnManager.setOnClickListener { switchViewActivation(it) }
            btnServer.setOnClickListener { switchViewActivation(it) }
            btnHostess.setOnClickListener { switchViewActivation(it) }
            btnBusser.setOnClickListener { switchViewActivation(it) }
            btnFoodRunner.setOnClickListener { switchViewActivation(it) }
            btnBarTender.setOnClickListener { switchViewActivation(it) }
        }

    }

    private fun switchViewActivation(v: View) {
        v.isActivated = !v.isActivated
    }

    private fun paintTables(customersToPaint: List<Customer>, selectedServerName: String) {

        var selectedServer: ServerWithSection? = null

        viewModel.serversWithSectionLiveData.value?.map {
            if (it.employee.name == selectedServerName) {
                selectedServer = it
            }
        }


        if (viewModel.floorWithTablesAndSectionsLiveData.value != null) {
            viewModel.floorWithTablesAndSectionsLiveData.value!!.allTables.map {
                requireView().findViewById<TableView>(it.id.toInt())
                    ?.setTableColor(Color.GRAY)
            }
        }

        if (selectedServer != null) {
            selectedServer!!.section?.tables?.map {
                requireView().findViewById<TableView>(it.id.toInt())
                    ?.setTableColor(Color.GREEN)
            }
        }


        customersToPaint.map { customer ->
            requireView().findViewById<TableView>(customer.tableID.toInt())
                ?.setTableColor(Color.RED)
        }


        if (tvTableNumber.text != "") {
            requireView().findViewById<TableView>(
                tvTableNumber.text.substring(
                    5,
                    tvTableNumber.text.indexOf("\n")
                ).toInt()
            )
                ?.setTableColor(Color.YELLOW)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        paintTables(
            viewModel.customerLiveData.value!!,
            serverChoiceAdapter.getItem(position)!!.substring(
                serverChoiceAdapter.getItem(position)!!.indexOf(". ") + 2
            )
        )
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    private fun subscribeObservers() {
        viewModel.floorWithTablesAndSectionsLiveData.observe(viewLifecycleOwner, {
            it?.let { floorWithTablesAndSections ->
                drawTables(floorWithTablesAndSections.allTables)
                viewModel.retrieveCustomersFromDB()

            }
        })

        viewModel.zoomLiveData.observe(viewLifecycleOwner, {
            viewModel.floorWithTablesAndSectionsLiveData.value?.let { chosenFloor ->
                chosenFloor.allTables.map { selectedTable ->

                    val tableView: TableView? =
                        requireView().findViewById(selectedTable.id.toInt())

                    tableView?.let {

                        //length and width
                        tableView.layoutParams = ConstraintLayout.LayoutParams(
                            floor(selectedTable.length * metrics.scaledDensity * viewModel.zoomLiveData.value!! / 100F).toInt(),
                            floor(selectedTable.height * metrics.scaledDensity * viewModel.zoomLiveData.value!! / 100F).toInt()
                        )

                        //coordinates
                        tableView.y =
                            selectedTable._Y!! * viewModel.zoomLiveData.value!! / 100F
                        tableView.x =
                            selectedTable._X!! * viewModel.zoomLiveData.value!! / 100F
                    }

                }


            }

        })

        viewModel.customerLiveData.observe(viewLifecycleOwner, { customers ->
            paintTables(customers, "")
        })

        viewModel.serversWithSectionLiveData.observe(viewLifecycleOwner, { serversWithSection ->

            val serverList = mutableListOf<String>()

            serversWithSection.sortedBy { it.section?.section?.number }.map {
                if (it.section != null) {
                    serverList.add("${it.section.section.number}. ${it.employee.name}")
                }
            }

            initServerChoiceSpinner(serverList)

        })

        viewModel.serverIDToSitLiveData.observe(viewLifecycleOwner, {

            if (etPartySize.text.isNotBlank() && tvTableNumber.text != "") {
                val newCustomer: Customer

                if (it == -1L) {
                    //todo save this customer give it to next person on rotation
                    newCustomer = Customer(
                        tableID = tvTableNumber.text.toString()
                            .substring(5, tvTableNumber.text.toString().indexOf("\n")).toLong(),
                        tableNumber = tvTableNumber.text.toString()
                            .substring(tvTableNumber.text.toString().indexOf("Number = ") + 9)
                            .toInt(),
                        partySize = etPartySize.text.toString().toInt(),
                        serverID = it
                    )
                } else {
                    newCustomer = Customer(
                        tableID = tvTableNumber.text.toString()
                            .substring(5, tvTableNumber.text.toString().indexOf("\n")).toLong(),
                        tableNumber = tvTableNumber.text.toString()
                            .substring(tvTableNumber.text.toString().indexOf("Number = ") + 9)
                            .toInt(),
                        partySize = etPartySize.text.toString().toInt(),
                        serverID = it
                    )
                }



                viewModel.saveNewCustomer(newCustomer)
                resetNewCustomerForm()


                lytNewGuest.visibility = GONE
                lytOptions.visibility = VISIBLE

            }


        })

    }
}


//todo select multiple tables for parties
//todo put together selected tables
//todo assign tables to multiple servers
//todo see sections while holding a button