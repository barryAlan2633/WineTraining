package com.barryalan.winetraining.ui.manager.floor

import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.customViews.TableView
import com.barryalan.winetraining.model.floor.Table
import com.barryalan.winetraining.model.floor.TableType
import com.barryalan.winetraining.ui.shared.BaseFragment
import com.barryalan.winetraining.util.StaticIntegers.Companion.MOVING_OFF
import com.barryalan.winetraining.util.StaticIntegers.Companion.MOVING_ON
import com.barryalan.winetraining.util.StaticIntegers.Companion.MOVING_TOGETHER_ON
import com.barryalan.winetraining.util.StaticIntegers.Companion._ROTATION
import com.barryalan.winetraining.util.StaticIntegers.Companion._X
import com.barryalan.winetraining.util.StaticIntegers.Companion._Y
import com.barryalan.winetraining.util.addNewItem
import com.barryalan.winetraining.util.removeItem
import com.google.android.material.button.MaterialButton
import kotlin.math.floor


class ManagerFloorArrangeTables : BaseFragment(), TableTypeCallback, View.OnTouchListener,
    TableCallback {

    private lateinit var viewModel: ManagerFloorArrangeTablesViewModel
    private lateinit var tableTypeAdapter: TableTypeAdapter
    private lateinit var selectedTablesAdapter: SelectedTableAdapter
    private lateinit var metrics: DisplayMetrics

    private lateinit var tvOriginalCoordinates: TextView
    private lateinit var tvMovingCoordinates: TextView
    private lateinit var tvSavingCoordinates: TextView

    private lateinit var sbZoom: SeekBar
    private lateinit var btnMenu: MaterialButton
    private lateinit var lytFloor: ConstraintLayout
    private lateinit var lytTableMenu: LinearLayout
    private lateinit var lytSelectedTableMenu: LinearLayout
    private lateinit var lytTableTypeMenu: LinearLayout
    private lateinit var rvTableType: RecyclerView
    private lateinit var rvSelectedTables: RecyclerView
    private lateinit var btnTableTypeMenu: ImageButton
    private lateinit var btnMove: ImageButton
    private lateinit var btnDeleteSelectedTables: ImageButton
    private lateinit var btnSelectAll: ImageButton
    private lateinit var btnDuplicateAll: ImageButton
    private lateinit var btnSelectedTablesMenu: ImageButton

    private lateinit var xyDifferences: ArrayList<FloatArray>
    private var originalX: Float = 0.0f
    private var originalY: Float = 0.0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_manager_floor_arrange_tables, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sbZoom = view.findViewById(R.id.sb_zoom_manager_floor_arrange_tables)

        tvOriginalCoordinates = view.findViewById(R.id.original_coordinates)
        tvMovingCoordinates = view.findViewById(R.id.moving_coordinates)
        tvSavingCoordinates = view.findViewById(R.id.saving_coordinates)

        btnMenu = view.findViewById(R.id.btn_menu_manager_floor_arrange_tables)
        lytFloor = view.findViewById(R.id.con_lyt_floor_manager_floor_arrange_tables)
        lytTableMenu = view.findViewById(R.id.lin_lyt_bottom_menu_manager_floor_arrange_tables)
        lytSelectedTableMenu =
            view.findViewById(R.id.lin_lyt_selected_tables_manager_floor_arrange_tables)
        lytTableTypeMenu = view.findViewById(R.id.lin_lyt_table_type_manager_floor_arrange_tables)
        rvTableType = view.findViewById(R.id.rv_table_types_manager_floor_arrange_tables)
        rvSelectedTables = view.findViewById(R.id.rv_selected_tables_manager_floor_arrange_tables)
        btnSelectedTablesMenu =
            view.findViewById(R.id.btn_selected_tables_menu_manager_floor_arrange_tables)

        btnTableTypeMenu =
            requireView().findViewById(R.id.btn_table_type_menu_manager_floor_arrange_tables)
        btnMove =
            requireView().findViewById(R.id.btn_move_selected_manager_floor_arrange_tables)
        btnDeleteSelectedTables =
            requireView().findViewById(R.id.btn_delete_selected_manager_floor_arrange_tables)
        btnSelectAll = requireView().findViewById(R.id.btn_select_manager_floor_arrange_tables)
        btnDuplicateAll =
            requireView().findViewById(R.id.btn_duplicate_all_manager_floor_arrange_tables)

        metrics = resources.displayMetrics

        Log.d("debug",metrics.density.toString())
        tableTypeAdapter = TableTypeAdapter(arrayListOf(), this, metrics)
        selectedTablesAdapter = SelectedTableAdapter(arrayListOf(), metrics, this)
        viewModel = ViewModelProvider(this).get(ManagerFloorArrangeTablesViewModel::class.java)

        arguments?.let {
            viewModel.retrieveChosenFloorFromDB(ManagerFloorNewEditArgs.fromBundle(it).floorUID)
        }

        subscribeObservers()
        initTableTypeRecycler()
        initSelectedTablesRecycler()

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

        btnMenu.setOnClickListener {
            openBottomSheetTableMenu()
        }

        btnMove.setOnClickListener {
            viewModel.movingModeLiveData.value = when (viewModel.movingModeLiveData.value) {
                MOVING_OFF -> {
                    btnMove.setImageResource(R.drawable.ic_baseline_move_24)
                    MOVING_ON
                }
                MOVING_ON -> {
                    btnMove.setImageResource(R.drawable.ic_baseline_move_together_24)
                    MOVING_TOGETHER_ON
                }

                MOVING_TOGETHER_ON -> {
                    btnMove.setImageResource(R.drawable.ic_baseline_touch_app_24)
                    MOVING_OFF
                }
                else -> MOVING_OFF
            }
        }

        btnSelectAll.setOnClickListener {
            when (btnSelectAll.tag) {
                "R.drawable.ic_baseline_not_move_24" -> {
                    btnSelectAll.tag = "R.drawable.ic_baseline_touch_app_24"
                    btnSelectAll.setImageResource(R.drawable.ic_baseline_touch_app_24)
                    viewModel.selectedTablesLiveData.value = arrayListOf()

                }
                "R.drawable.ic_baseline_touch_app_24" -> {
                    btnSelectAll.tag = "R.drawable.ic_baseline_not_move_24"
                    btnSelectAll.setImageResource(R.drawable.ic_baseline_not_move_24)
                    viewModel.selectedTablesLiveData.value =
                        viewModel.chosenFloorLiveData.value!!.allTables
                }
            }

        }

        btnDuplicateAll.setOnClickListener { viewModel.duplicateSelectedTablesAndRefresh() }

        btnDeleteSelectedTables.setOnClickListener { viewModel.deleteSelectedTablesAndRefsAndRefresh() }

        btnSelectedTablesMenu.setOnClickListener {
            if (lytSelectedTableMenu.visibility == GONE) {
                lytSelectedTableMenu.visibility = VISIBLE
            } else {
                lytSelectedTableMenu.visibility = GONE
            }
        }

        btnTableTypeMenu.setOnClickListener {
            if (lytTableTypeMenu.visibility == GONE) {
                lytTableTypeMenu.visibility = VISIBLE
            } else {
                lytTableTypeMenu.visibility = GONE
            }
        }


    }

    private fun openBottomSheetTableMenu() {
        MaterialDialog(requireContext(), BottomSheet()).show {
            cancelOnTouchOutside(false)


            //setup bottom sheet
            customView(R.layout.bottom_sheet_table_menu)

            title(R.string.advanced_search_options)
            positiveButton(R.string.apply) {}


        }

    }

    private fun initTableTypeRecycler() {
        rvTableType.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = tableTypeAdapter
        }
    }

    private fun initSelectedTablesRecycler() {
        rvSelectedTables.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = selectedTablesAdapter
        }
    }

    override fun onTableTypePressed(tableType: TableType) {
        if (tableType.height == 0 && tableType.length == 0) {
            viewModel.initGloriaFloor()
        } else {

            viewModel.saveTableAndRefresh(
                Table(
                    0,
                    tableType.height,
                    tableType.length,
                    0F,
                    10,
                    10,
                    tableType.boothOrTable,
                    tableType.maxNumberOfSeats
                )
            )
        }

    }

    private fun drawTables(listOfTablesToDraw: MutableList<Table>) {
        lytFloor.removeAllViews()

        listOfTablesToDraw.map {
            val tbNewTable = TableView(requireContext())

            //layout params
            tbNewTable.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

            tbNewTable.layoutParams = ConstraintLayout.LayoutParams(
                floor(it.length  * metrics.scaledDensity * viewModel.zoomLiveData.value!! / 100F).toInt(),
                floor(it.height  * metrics.scaledDensity * viewModel.zoomLiveData.value!! / 100F).toInt()
            )


            tbNewTable.x = it._X!!.toFloat() * viewModel.zoomLiveData.value!! / 100F
            tbNewTable.y = it._Y!!.toFloat() * viewModel.zoomLiveData.value!! / 100F
            tbNewTable.id = it.id.toInt()
            tbNewTable.setBoothOrTable(it.boothOrTable)


            //view specific params
            tbNewTable.rotation = it.rotation
            tbNewTable.setTableRotation(it.rotation)
            tbNewTable.setTableNumber(it.number.toString())


            tbNewTable.setOnTouchListener(this)

            viewModel.selectedTablesLiveData.value!!.map { selectedTable ->
                if (tbNewTable.id.toLong() == selectedTable.id) {
                    tbNewTable.setTableColor(Color.YELLOW)
                }
            }


            lytFloor.addView(tbNewTable)

        }

    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val view = (v as TableView)

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {

                xyDifferences = arrayListOf(FloatArray(2))
                xyDifferences.clear()


                if (viewModel.movingModeLiveData.value == MOVING_OFF) {
                    //todo concurrent modification here for some weird reason 8-7-21
                    viewModel.chosenFloorLiveData.value!!.allTables.map { tableInFloor ->

                        if (tableInFloor.id.toInt() == view.id) {
                            if (viewModel.selectedTablesLiveData.value!!.contains(tableInFloor)) {
                                view.setTableColor(Color.GRAY)
                                viewModel.selectedTablesLiveData.removeItem(tableInFloor)
                            } else {
                                view.setTableColor(Color.YELLOW)
                                viewModel.selectedTablesLiveData.addNewItem(tableInFloor)
                            }
                        }

                    }
                } else {
                    viewModel.selectedTablesLiveData.value!!.map { selectedTable ->

                        if (selectedTable.id.toInt() == view.id) {
                            originalX = view.x - event.rawX
                            originalY = view.y - event.rawY
                            "X=${event.rawX + originalX}, Y=${event.rawY + originalY}".also {
                                tvOriginalCoordinates.text = it
                            }
                        } else {
                            if (viewModel.movingModeLiveData.value == MOVING_TOGETHER_ON) {
                                xyDifferences.add(
                                    floatArrayOf(
                                        (selectedTable._X!! * viewModel.zoomLiveData.value!! / 100F - view.x),
                                        (selectedTable._Y!! * viewModel.zoomLiveData.value!! / 100F - view.y)
                                    )
                                )
                            } else {
                            }
                        }
                    }

                }


                return true
            }

            MotionEvent.ACTION_MOVE -> {

                if (viewModel.movingModeLiveData.value != MOVING_OFF) {

                    var index = 0
                    viewModel.selectedTablesLiveData.value!!.map { selectedTable ->
                        if (selectedTable.id.toInt() == view.id) {
                            view.animate()
                                .x(event.rawX + originalX)
                                .y(event.rawY + originalY)
                                .setDuration(0)
                                .start()

                            "X=${event.rawX + originalX}, Y=${event.rawY + originalY}".also {
                                tvMovingCoordinates.text = it
                            }
                        } else {
                            if (viewModel.movingModeLiveData.value == MOVING_TOGETHER_ON) {
                                val tableView =
                                    requireView().findViewById<TableView>(selectedTable.id.toInt())

                                tableView.animate()
                                    .x(view.x + xyDifferences[index][0])
                                    .y(view.y + xyDifferences[index][1])
                                    .setDuration(0)
                                    .start()
                                index++
                            } else {

                            }
                        }
                    }
                }

                return true
            }

            MotionEvent.ACTION_UP -> {
                view.performClick()
                //go through all the selected tables and change them all
                //if the pressed table is not in the selected tables don't do this


                var index = 0
                viewModel.selectedTablesLiveData.value!!.map { selectedTable ->

                    if (viewModel.movingModeLiveData.value != MOVING_OFF) {

                        if (selectedTable.id.toInt() == view.id) {
                            selectedTable._X =
                                floor((event.rawX + originalX) * 100F / viewModel.zoomLiveData.value!!).toInt()
                            selectedTable._Y =
                                floor((event.rawY + originalY) * 100F / viewModel.zoomLiveData.value!!).toInt()
                            "X=${event.rawX + originalX}, Y=${event.rawY + originalY}".also {
                                tvSavingCoordinates.text = it
                            }

                            viewModel.updateTableAndRefresh(selectedTable)
                        } else {
                            if (viewModel.movingModeLiveData.value == MOVING_TOGETHER_ON) {
                                selectedTable._X =
                                    floor((view.x + xyDifferences[index][0]) * 100F / viewModel.zoomLiveData.value!!).toInt()
                                selectedTable._Y =
                                    floor((view.y + xyDifferences[index][1]) * 100F / viewModel.zoomLiveData.value!!).toInt()
                                index++
                                viewModel.updateTableAndRefresh(selectedTable)
                            }
                        }

                    }

                }


                return true
            }

            else -> {
                return false
            }
        }
    }

    override fun alignTable(position: Int, type: Int) {

        viewModel.selectedTablesLiveData.value!!.map {
            when (type) {
                _X -> {
                    it._X =
                        viewModel.selectedTablesLiveData.value!![position]._X!! * viewModel.zoomLiveData.value!! / 100
                }
                _Y -> {
                    it._Y =
                        viewModel.selectedTablesLiveData.value!![position]._Y!! * viewModel.zoomLiveData.value!! / 100
                }
                _ROTATION -> {
                    it.rotation = viewModel.selectedTablesLiveData.value!![position].rotation
                }
            }
        }

        viewModel.updateSelectedTables()
    }

    override fun changeTableRotation(position: Int, rotation: Float) {
        val tableToChange = viewModel.selectedTablesLiveData.value!![position]
        tableToChange.rotation = rotation

        viewModel.updateTableAndRefresh(tableToChange)
    }

    override fun changeTableNumber(position: Int, newNumber: Int) {
        val tableToChange = viewModel.selectedTablesLiveData.value!![position]
        tableToChange.number = newNumber

        viewModel.updateTableAndRefresh(tableToChange)
    }

    private fun subscribeObservers() {
        viewModel.chosenFloorLiveData.observe(viewLifecycleOwner, {
            it?.let { floor ->
                tableTypeAdapter.updateTableTypeList(floor.allTableTypes)

                drawTables(floor.allTables)
            }
        })

        viewModel.selectedTablesLiveData.observe(viewLifecycleOwner, {
            it?.let { selectedTables ->

                selectedTablesAdapter.updateList(selectedTables)

                viewModel.chosenFloorLiveData.value?.let { floor -> drawTables(floor.allTables) }
            }
        })

        viewModel.zoomLiveData.observe(viewLifecycleOwner,
            {
                viewModel.chosenFloorLiveData.value?.let { chosenFloor ->
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
    }
}

//todo change 10F to a static var of value 100F in all other fragments