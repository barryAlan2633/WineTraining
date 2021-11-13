package com.barryalan.winetraining.ui.manager.floor

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.customViews.TableView
import com.barryalan.winetraining.model.floor.Section
import com.barryalan.winetraining.model.floor.SectionColor
import com.barryalan.winetraining.model.floor.Table
import com.barryalan.winetraining.model.floor.with.SectionWithTables
import com.barryalan.winetraining.ui.shared.BaseFragment
import com.barryalan.winetraining.ui.shared.util.UIMessage
import com.barryalan.winetraining.ui.shared.util.UIMessageType
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.listeners.ColorListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.floor


class ManagerSectionEditor : BaseFragment(), AdapterView.OnItemSelectedListener, SectionCallback {

    private lateinit var numberOfServersAdapter: ArrayAdapter<String>
    private lateinit var sectionAdapter: SectionAdapter
    private lateinit var viewModel: ManagerSectionEditorViewModel

    private lateinit var metrics: DisplayMetrics

    private lateinit var sbZoom: SeekBar
    private lateinit var lytFloor: ConstraintLayout
    private lateinit var rvSections: RecyclerView
    private lateinit var spNumberOfSections: Spinner
    private lateinit var btnSectionColorMenu: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_manager_section_editor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sbZoom = view.findViewById(R.id.sb_zoom_manager_section_editor)
        lytFloor = view.findViewById(R.id.con_lyt_floor_manager_floor_section_editor)
        spNumberOfSections =
            view.findViewById(R.id.sp_number_of_sections_manager_floor_section_editor)
        btnSectionColorMenu =
            view.findViewById(R.id.btn_section_color_menu)
        rvSections = view.findViewById(R.id.rv_sections_manager_floor_section_editor)
        metrics = resources.displayMetrics

        viewModel = ViewModelProvider(this).get(ManagerSectionEditorViewModel::class.java)

        arguments?.let {
            viewModel.retrieveFloorFromDB(ManagerSectionEditorArgs.fromBundle(it).floorUID)
        }
        viewModel.retrieveServersFromDB()
        viewModel.retrieveSectionColorsFromDB()

        subscribeObservers()
        initSectionRecycler()


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


        btnSectionColorMenu.setOnClickListener {
            CoroutineScope(IO).launch {

                async {
                    viewModel.retrieveSectionColorsFromDB()
                }.await()

                withContext(Main) {
                    openSectionColorMenu()
                }

            }
        }
    }

    private fun initSelectedNumberOfServersSpinner(numberOfServersList: IntArray) {
        val list: MutableList<String> = mutableListOf()

        numberOfServersList.map {
            list.add(it.toString())
        }

        numberOfServersAdapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_small,
            list
        ).also { adapter ->

            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

            spNumberOfSections.adapter = adapter
            spNumberOfSections.onItemSelectedListener = this

        }

        spNumberOfSections.setSelection(
            numberOfServersAdapter.getPosition(viewModel.selectedNumberOfSectionsLiveData.value!!.toString())
        )

    }

    private fun initSectionRecycler() {

        sectionAdapter = SectionAdapter(mutableListOf(), this, mutableListOf())

        rvSections.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = sectionAdapter
        }
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
                if (sectionAdapter.selectedSectionID != -1L) {

                    viewModel.saveOrDeleteSectionTableRef(
                        tableToDraw.id,
                        sectionAdapter.selectedSectionID
                    )

                }

            }

            lytFloor.addView(tbNewTable)

        }

    }

    private fun paintTables(sectionsToPaint: List<SectionWithTables>) {

        if (viewModel.floorWithTablesAndSectionsLiveData.value != null) {
            viewModel.floorWithTablesAndSectionsLiveData.value!!.allTables.map {
                requireView().findViewById<TableView>(it.id.toInt())?.setTableColor(Color.GRAY)
            }
        }

        if (!viewModel.sectionColorsLiveData.value.isNullOrEmpty()) {

            sectionsToPaint.map { section ->

                viewModel.sectionColorsLiveData.value!!.map { sectionColor ->

                    if (sectionColor.number == section.section.number) {
                        section.tables.map {
                            requireView().findViewById<TableView>(it.id.toInt())
                                ?.setTableColor(sectionColor.color)
                        }
                    }
                }

            }
        }
    }

    private fun openSectionColorMenu() {
        MaterialDialog(requireContext(), BottomSheet()).show {

            cancelOnTouchOutside(false)
            customView(R.layout.bottom_sheet_section_color_menu)


            //Make this shit stick to the top for good :D
            val behavior: BottomSheetBehavior<*> = from(view.parent as View)
            behavior.state = STATE_EXPANDED
            behavior.addBottomSheetCallback(object : BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        STATE_HIDDEN -> {

                        }
                        else -> {
                            behavior.state = STATE_EXPANDED
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })


            val chipGroup = this.view.findViewById<ChipGroup>(R.id.chip_group_section_color_menu)
            val colorPicker = this.view.findViewById<ColorPickerView>(R.id.col_section_color_menu)

            var selectedChipID = -1

            title(R.string.section_color_menu)
            positiveButton(R.string.save) {

                val sectionColors = arrayListOf<SectionColor>()
                for (index in 0..chipGroup.size) {
                    if (chipGroup.getChildAt(index) != null) {
                        val chip = chipGroup.getChildAt(index) as Chip

                        sectionColors.add(
                            SectionColor(
                                number = chip.text.toString().toInt(),
                                color = chip.chipBackgroundColor?.defaultColor ?: Color.WHITE
                            )
                        )


                    }
                }

                viewModel.saveAllSectionColors(sectionColors)

                viewModel.retrieveSectionColorsFromDB()
            }

            for (number in 1..viewModel.serversLiveData.value!!.size) {

                val newChip = Chip(this.context)


                newChip.text = number.toString()
                newChip.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                newChip.textSize = 20f
                newChip.width = 400
                newChip.height = 200
                newChip.chipStrokeWidth = 5f
                newChip.chipStrokeColor = ColorStateList.valueOf(Color.BLACK)
                newChip.id = View.generateViewId()
                newChip.setOnClickListener {
                    selectedChipID = it.id
                    newChip.chipBackgroundColor?.let { it1 -> colorPicker.setInitialColor(it1.defaultColor) }
                }


                viewModel.sectionColorsLiveData.value?.let {

                    it.map { sectionColor ->
                        if (sectionColor.number == newChip.text.toString().toInt()) {
                            newChip.chipBackgroundColor =
                                ColorStateList.valueOf(sectionColor.color)
                        }

                    }
                }

                chipGroup.addView(newChip)


            }

            colorPicker.setColorListener(ColorListener { color, fromUser ->
                this.view.findViewById<Chip>(selectedChipID)?.chipBackgroundColor =
                    ColorStateList.valueOf(color)
            })


        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {
            when (parent.id) {
                R.id.sp_number_of_sections_manager_floor_section_editor -> {
                    viewModel.selectedNumberOfSectionsLiveData.value =
                        parent.selectedItem.toString().toInt()
                }
                else -> {

                }
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onSectionSelected(sectionId: Long) {
        viewModel.selectedSectionIdLiveData.value = sectionId
    }

    private fun subscribeObservers() {
        viewModel.floorWithTablesAndSectionsLiveData.observe(viewLifecycleOwner, {
            it?.let { floorWithTablesAndSections ->
                drawTables(floorWithTablesAndSections.allTables)
            }
        })

        viewModel.selectedNumberOfSectionsLiveData.observe(viewLifecycleOwner, {
            it?.let { nSelectedNumberOfSections ->
                //get all sections that have this many people
                viewModel.getRelevantSections(nSelectedNumberOfSections)
            }
        })

        viewModel.serversLiveData.observe(viewLifecycleOwner,
            {

                it.let { servers ->
                    initSelectedNumberOfServersSpinner(IntArray(servers.size) { number -> number + 1 })


                    val sections = mutableListOf<Section>()
                    //Saves the initial section objects to db
                    (1..servers.size).map { numberOfServers ->

                        (1..numberOfServers).map { sectionNumber ->
                            sections.add(Section(sectionNumber, numberOfServers))
                        }
                    }

                    viewModel.initAllSections(sections)
                }

                if (it.isNullOrEmpty()) {
                    uiCommunicationListener.onUIMessageReceived(
                        UIMessage(
                            "You need to first add at least one server on the employee section",
                            UIMessageType.ErrorDialog()
                        )
                    )
                }
            })

        viewModel.sectionColorsLiveData.observe(viewLifecycleOwner,
            {
                it?.let { sectionColors ->
                    sectionAdapter.updateSectionColors(sectionColors)
                }
            })

        viewModel.relevantSectionsLiveData.observe(viewLifecycleOwner,
            {

                it?.let { relevantSections ->
                    paintTables(relevantSections)
                    sectionAdapter.updateSectionList(relevantSections)
                }
            })

        viewModel.selectedSectionIdLiveData.observe(viewLifecycleOwner,
            { selectedSectionId ->

                sectionAdapter.updateSelectedSection(selectedSectionId)
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
    }

}