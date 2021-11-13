package com.barryalan.winetraining.ui.server

import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.allViews
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.customViews.TableView
import com.barryalan.winetraining.model.customer.Customer
import com.barryalan.winetraining.model.employee.with.ServerWithSection
import com.barryalan.winetraining.model.floor.Table
import com.barryalan.winetraining.ui.shared.BaseFragment
 import com.google.android.material.chip.ChipGroup
import kotlin.math.floor

class ServerMain : BaseFragment() {

    private lateinit var viewModel: ServerMainViewModel
    private lateinit var metrics: DisplayMetrics
    private lateinit var lytFloor: ConstraintLayout
    private lateinit var sbZoom: SeekBar
    private lateinit var ivFloorSettings: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_server_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lytFloor = view.findViewById(R.id.con_lyt_floor_server_main)
        sbZoom = view.findViewById(R.id.sb_zoom_server_main)
        ivFloorSettings = view.findViewById(R.id.iv_floor_settings_server_main)

        viewModel = ViewModelProvider(this).get(ServerMainViewModel::class.java)
        metrics = resources.displayMetrics

        arguments?.let {
            viewModel.loggedServerIDLiveData.value = ServerMainArgs.fromBundle(it).serverUID
        }

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

        ivFloorSettings.setOnClickListener {
            openFloorSettingsBottomSheet()
        }

    }

    private fun paintTables(customersToPaint: List<Customer>, selectedServerID: Long) {

        var selectedServer: ServerWithSection? = null

        viewModel.serversWithSectionLiveData.value?.map {
            if (it.employee.id == selectedServerID) {
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


    }

    private fun drawTables(allTables: List<Table>, floorSettings: Int) {
        val metrics: DisplayMetrics = resources.displayMetrics

        lytFloor.removeAllViews()


        val tablesToDraw = mutableListOf<Table>()

        when (floorSettings) {
            R.id.chip_my_section_bottom_sheet_server_floor_settings -> {

                viewModel.serversWithSectionLiveData.value!!.map { server ->
                    if (server.employee.id == viewModel.loggedServerIDLiveData.value) {
                        allTables.map { table ->
                            if (server.section?.tables?.contains(table) == true) {
                                tablesToDraw.add(table)
                            }
                        }
                    }
                }

            }
            R.id.chip_coworkers_section_bottom_sheet_server_floor_settings -> {
                tablesToDraw.addAll(allTables)
            }
            R.id.chip_coworkes_active_tables_bottom_sheet_server_floor_settings -> {

                viewModel.customerLiveData.value!!.map { customer ->
                    allTables.map { table ->
                        if (customer.tableID == table.id) {
                            tablesToDraw.add(table)
                        }
                    }
                }
            }
            R.id.chip_my_active_tables_bottom_sheet_server_floor_settings -> {

                viewModel.customerLiveData.value!!.map { customer ->
                    allTables.map { table ->
                        if (customer.tableID == table.id) {
                            viewModel.serversWithSectionLiveData.value!!.map { server ->
                                if (server.employee.id == viewModel.loggedServerIDLiveData.value) {
                                    if (server.section?.tables?.contains(table) == true) {
                                        tablesToDraw.add(table)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


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


                val customer: Customer? =
                    viewModel.customerLiveData.value?.firstOrNull { customer ->
                        customer.tableID.toInt() == tbNewTable.id && customer.serverID == viewModel.loggedServerIDLiveData.value
                    }

                if (customer != null) {
                    openMenuBottomSheet(customer)
                }


            }

            lytFloor.addView(tbNewTable)

        }


    }

    private fun openMenuBottomSheet(customer: Customer) {
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            cancelOnTouchOutside(false)


            customView(R.layout.bottom_sheet_customer_order)

            val conLytCustomer =
                this.view.findViewById<ConstraintLayout>(R.id.con_lyt_customer_customer_order)

            conLytCustomer.removeAllViews()

            val tablesToDraw = viewModel.getTablesByID(listOf(customer.tableID))


            tablesToDraw.map { tableToDraw ->
                val customerTable = TableView(requireContext())

                //layout params
                customerTable.layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                customerTable.layoutParams = ConstraintLayout.LayoutParams(
                    floor(tableToDraw.length * metrics.scaledDensity).toInt(),
                    floor(tableToDraw.height * metrics.scaledDensity).toInt()
                )


                customerTable.x = 0f
                customerTable.y = 50f
                customerTable.id = tableToDraw.id.toInt()
                customerTable.setBoothOrTable(tableToDraw.boothOrTable)

                //view specific params
                customerTable.rotation = tableToDraw.rotation
                customerTable.setTableRotation(tableToDraw.rotation)
                customerTable.setTableNumber(tableToDraw.number.toString())

                conLytCustomer.addView(customerTable)

            }

            negativeButton { }

        }

    }

    private fun openFloorSettingsBottomSheet() {
        MaterialDialog(requireContext(), BottomSheet()).show {
            cancelOnTouchOutside(false)

            customView(R.layout.bottom_sheet_server_floor_settings)

            this.view.findViewById<ChipGroup>(R.id.chipGroup_server_floor_settings)
                .setOnCheckedChangeListener { group, checkedId ->
                    viewModel.floorSettingsLiveData.value = checkedId
                }

            this.view.findViewById<ChipGroup>(R.id.chipGroup_server_status)
                .setOnCheckedChangeListener { group, checkedId ->
                    viewModel.serverStateLiveData.value = checkedId
                }

        }
    }

    private fun subscribeObservers() {
        viewModel.floorWithTablesAndSectionsLiveData.observe(viewLifecycleOwner, {
            it?.let { floorWithTablesAndSections ->
                drawTables(
                    floorWithTablesAndSections.allTables,
                    viewModel.floorSettingsLiveData.value!!
                )
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
            paintTables(customers, viewModel.loggedServerIDLiveData.value!!)
        })

        viewModel.floorSettingsLiveData.observe(viewLifecycleOwner, {
            viewModel.floorWithTablesAndSectionsLiveData.value?.let { it1 ->
                drawTables(
                    it1.allTables,
                    it
                )
            }

        })

        viewModel.serverStateLiveData.observe(viewLifecycleOwner, {

        })


    }


}