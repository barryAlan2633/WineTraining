package com.barryalan.winetraining.ui.hostess.section

import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.customViews.TableView
import com.barryalan.winetraining.model.employee.with.ServerWithSection
import com.barryalan.winetraining.model.floor.Table
import com.barryalan.winetraining.model.floor.with.SectionWithTables
import com.barryalan.winetraining.ui.shared.BaseFragment
import com.barryalan.winetraining.util.addNewItem
import com.barryalan.winetraining.util.forceRefresh
import com.barryalan.winetraining.util.removeItem
import com.google.android.material.button.MaterialButton
import kotlin.math.floor


class HostessSectionAssigner : BaseFragment(), ServerCallback {

    private val serverAdapter = ServerAdapter(arrayListOf(), this)

    private lateinit var viewModel: HostessSectionAssignerViewModel

    private lateinit var metrics: DisplayMetrics

    private lateinit var sbZoom: SeekBar
    private lateinit var lytFloor: ConstraintLayout
    private lateinit var rvServers: RecyclerView
    private lateinit var btnAssignSections: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hostess_section_assigner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sbZoom = view.findViewById(R.id.sb_zoom_hostess_section_assigner)
        lytFloor = view.findViewById(R.id.con_lyt_floor_hostess_section_assigner)
        rvServers = view.findViewById(R.id.rv_servers_hostess_section_assigner)
        btnAssignSections = view.findViewById(R.id.btn_assign_sections_hostess_section_assigner)
        metrics = resources.displayMetrics

        viewModel = ViewModelProvider(this).get(HostessSectionAssignerViewModel::class.java)

        arguments?.let {
            viewModel.retrieveFloorFromDB(1)
            //todo
        }
        viewModel.retrieveFloorFromDB(1)

        viewModel.retrieveServersFromDB()
        viewModel.retrieveSectionColorsFromDB()

        subscribeObservers()
        initServerRecycler()


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

        btnAssignSections.setOnClickListener {
            viewModel.assignSections(viewModel.checkedServers.value!!)

        }
    }

    private fun initServerRecycler() {

        rvServers.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = serverAdapter
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

    override fun onServerChecked(server: ServerWithSection) {

        var add = true
        var index = -1

        viewModel.checkedServers.value!!.mapIndexed {i,s->
            if (s.employee.id == server.employee.id) {
                //meaning checked servers contains this item // DO NOT use CONTAINS()
                add = false
                index = i
            }
        }

        if (add) {
            viewModel.checkedServers.addNewItem(server)
        }else{
            //DON'T USE RemoveItem extension function, doesn't check deep enough to get it right
            viewModel.checkedServers.value!!.removeAt(index)
        }

    }

    private fun subscribeObservers() {
        viewModel.floorWithTablesAndSectionsLiveData.observe(viewLifecycleOwner, {
            it?.let { floorWithTablesAndSections ->
                drawTables(floorWithTablesAndSections.allTables)
            }
        })

        viewModel.serversWithSectionLiveData.observe(viewLifecycleOwner, {
            it?.let { servers ->

                viewModel.checkedServers.value!!.clear()

                val sectionsToPaint = arrayListOf<SectionWithTables>()

                servers.map{server->
                    if(server.section != null){
                        viewModel.checkedServers.addNewItem(server)
                        sectionsToPaint.add(server.section)
                    }
                }

                paintTables(sectionsToPaint)

                 serverAdapter.updateList(servers)
            }
        })

        viewModel.sectionColorsLiveData.observe(viewLifecycleOwner, {
            it?.let { sectionColors ->
                serverAdapter.updateSectionColors(sectionColors)
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


    }


}