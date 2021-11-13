package com.barryalan.winetraining.ui.manager.floor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.ui.menu.menu.MenuDetailDirections
import com.barryalan.winetraining.ui.shared.BaseFragment
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import java.util.*

class ManagerFloorList : BaseFragment(), FloorCallback {

    private lateinit var viewModel: ManagerFloorListViewModel
    private val floorAdapter = FloorAdapter(arrayListOf(),this)
    private lateinit var eabtn: ExtendedFloatingActionButton
    private lateinit var rvFloor: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_manager_floor_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eabtn = requireView().findViewById(R.id.eabtn_new_floor_list)
        rvFloor = requireView().findViewById(R.id.rv_floor_plans_floor_list)

        viewModel = ViewModelProvider(this).get(ManagerFloorListViewModel::class.java)
        viewModel.refresh()
        initFloorRecycler()
        subscribeObservers()


        eabtn.setOnClickListener {
            view.findNavController().navigate(R.id.action_managerFloorList_to_managerFloorNew)
        }
    }

    private fun initFloorRecycler() {
        rvFloor.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = floorAdapter

        }
    }

    private fun subscribeObservers() {
        viewModel.floorsLiveData.observe(viewLifecycleOwner, {
            it?.let {
                floorAdapter.updateFloorList(it)
            }
        })
    }

    override fun onEditFloor(selectedFloorID: Long) {
        val action = ManagerFloorListDirections.actionManagerFloorListToManagerFloorNew(
            selectedFloorID
        )
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun onArrangeTables(selectedFloorID: Long) {
        val action = ManagerFloorListDirections.actionManagerFloorListToManagerFloorArrangeTables(
            selectedFloorID
        )
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun onEditSections(selectedFloorID: Long) {
        val action = ManagerFloorListDirections.actionManagerFloorListToManagerSectionEditor(
            selectedFloorID
        )
        Navigation.findNavController(requireView()).navigate(action)
    }

}