package com.barryalan.winetraining.wineList

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.shared.Wine
import com.google.android.material.floatingactionbutton.FloatingActionButton


class WineList : Fragment(), com.barryalan.winetraining.shared.OnItemClickListener {

    private lateinit var viewModel: WineListViewModel
    private lateinit var wineAdapter: WineAdapter

    private var isNewItem = false
    private var editItemId = 0

    private lateinit var actionButtonNew: FloatingActionButton
    private lateinit var actionButtonCancel: FloatingActionButton
    private lateinit var actionButtonSave: FloatingActionButton
    lateinit var name: TextView
    lateinit var category: TextView
    private lateinit var glassPrice: TextView
    private lateinit var bottlePrice: TextView
    private lateinit var rvWines: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wine_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        name = requireView().findViewById(R.id.name)
        category = requireView().findViewById(R.id.category)
        glassPrice = requireView().findViewById(R.id.glassPrice)
        bottlePrice = requireView().findViewById(R.id.bottlePrice)
        actionButtonNew = requireView().findViewById(R.id.action_button)
        actionButtonCancel = requireView().findViewById(R.id.action_button_cancel)
        actionButtonSave = requireView().findViewById(R.id.action_button_save)
        rvWines = requireView().findViewById(R.id.rv_wines)

        viewModel = ViewModelProvider(this).get(WineListViewModel::class.java)
        viewModel.getWines()

        initRecyclerView()
        initActionButtons()
        subscribeObservers()
        requireView().findViewById<FloatingActionButton>(R.id.action_button_prePopulateWines).setOnClickListener {
            viewModel.prePopulateWine()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val menuInflater: MenuInflater = requireActivity().menuInflater
        menuInflater.inflate(R.menu.menu_search, menu)

        val myActionMenuItem = menu.findItem(R.id.appar_search)
        val searchView = myActionMenuItem.actionView as SearchView

        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                wineAdapter.filter.filter(newText)
                return false
            }

        })


    }

    private fun initRecyclerView() {
        wineAdapter = WineAdapter(mutableListOf(), this)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        rvWines.layoutManager = linearLayoutManager
        rvWines.setHasFixedSize(true)
        rvWines.adapter = wineAdapter


        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewModel.deleteWine(wineAdapter.wineList[viewHolder.adapterPosition])
            }
        }).attachToRecyclerView(rvWines)

    }


    private fun initActionButtons() {
        actionButtonNew.setOnClickListener {
            isNewItem = true
            openNewItemInterface()
            actionButtonNew.visibility = View.GONE
            name.requestFocus()

            val inputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager



            inputMethodManager.toggleSoftInputFromWindow(
                name.applicationWindowToken,
                InputMethodManager.SHOW_IMPLICIT, 0
            )
        }

        actionButtonCancel.setOnClickListener { closeNewInterface() }
        actionButtonSave.setOnClickListener { //If the info was saved correctly then reset the database otherwise do not reset it
            if (saveInfoToDatabase()) {
                closeNewInterface()
            }
        }
    }

    private fun closeNewInterface() {
        val cardViewLayout = requireView().findViewById<CardView>(R.id.cardView_layout)
        cardViewLayout.visibility = View.GONE
        rvWines.alpha = 1f
        rvWines.isClickable = true
        actionButtonNew.visibility = View.VISIBLE

        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inputMethodManager.toggleSoftInputFromWindow(
            actionButtonNew.applicationWindowToken,
            InputMethodManager.HIDE_NOT_ALWAYS, 0
        )
        name.text = ""
        category.text = ""
        glassPrice.text = ""
        bottlePrice.text = ""
    }


    private fun openNewItemInterface() {
        val cardViewLayout = requireView().findViewById<CardView>(R.id.cardView_layout)
        cardViewLayout.visibility = View.VISIBLE
        rvWines.alpha = 0.5.toFloat()
        rvWines.isClickable = false
    }

    private fun saveInfoToDatabase(): Boolean {
        return if (name.text.toString() == "" || category.text.toString() == "" || glassPrice.text.toString() == "" || bottlePrice.text.toString() == "") {
            false
        } else {
            val newWine = Wine(
                name.text.toString(),
                category.text.toString(),
                glassPrice.text.toString().toFloat(),
                bottlePrice.text.toString().toFloat()
            )
            if (isNewItem) {
                viewModel.saveWine(newWine)
            } else {
                newWine.id = editItemId
                viewModel.saveWine(newWine)
            }
            true
        }
    }

    private fun setOldItemInfo(wine: Wine) {
        editItemId = wine.id
        name.text = wine.name
        category.text = wine.category
        glassPrice.text = wine.glassPrice.toString()
        bottlePrice.text = wine.bottlePrice.toString()
    }

    override fun onItemClick(position: Int) {
        if (rvWines.isClickable) {
            isNewItem = false
            openNewItemInterface()
            setOldItemInfo(wineAdapter.wineList[position])
        }
    }

    private fun subscribeObservers() {
        viewModel.winesLiveData.observe(viewLifecycleOwner, {
            wineAdapter.updateListAdd(it)
        })
    }
}