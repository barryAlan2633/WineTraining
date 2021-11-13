package com.barryalan.winetraining.ui.menu.menu

import android.content.res.Configuration.*
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.barryalan.winetraining.R
import com.barryalan.winetraining.ui.shared.BaseFragment
import com.google.android.material.textview.MaterialTextView


class MenuList : BaseFragment() {

    private val menuItemListAdapter = MenuListAdapter(arrayListOf())
    private lateinit var viewModel: MenuListViewModel


    private lateinit var rvMenuList: RecyclerView
    private lateinit var menuSearch: SearchView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var tvListError: MaterialTextView
    private lateinit var progressBar: ProgressBar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvMenuList = requireView().findViewById(R.id.rv_list_menu_list)
        menuSearch = requireView().findViewById(R.id.search_menu_menu_list)
        refreshLayout = requireView().findViewById(R.id.refreshLayout_menu_list)
        tvListError = requireView().findViewById(R.id.list_error_menu_list)
        progressBar = requireView().findViewById(R.id.loading_view_menu_list)

        viewModel = ViewModelProvider(this).get(MenuListViewModel::class.java)

        viewModel.refresh()

        initRecyclerView()
        initRefreshLayout()

        subscribeObservers()

    }


    private fun initRecyclerView() {
        rvMenuList.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = menuItemListAdapter
        }

        menuSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                menuItemListAdapter.filter.filter(newText)
                return false
            }

        })
        val searchIcon = menuSearch.findViewById<ImageView>(R.id.search_mag_icon)
        val cancelIcon = menuSearch.findViewById<ImageView>(R.id.search_close_btn)
        val textView = menuSearch.findViewById<TextView>(R.id.search_src_text)

        when (resources.configuration.uiMode) {
            UI_MODE_NIGHT_NO -> {
                searchIcon.setColorFilter(Color.WHITE)

                cancelIcon.setColorFilter(Color.WHITE)

                textView.setTextColor(Color.WHITE)
            }
            UI_MODE_NIGHT_YES -> {
                searchIcon.setColorFilter(Color.BLACK)

                cancelIcon.setColorFilter(Color.BLACK)

                textView.setTextColor(Color.BLACK)
            }
            UI_MODE_NIGHT_UNDEFINED -> {
                searchIcon.setColorFilter(Color.WHITE)

                cancelIcon.setColorFilter(Color.WHITE)

                textView.setTextColor(Color.WHITE)
            }
        }


    }

    private fun initRefreshLayout() {
        refreshLayout.setOnRefreshListener {
            rvMenuList.visibility = View.GONE
            tvListError.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            viewModel.refresh()
            refreshLayout.isRefreshing = false
        }
    }

    private fun subscribeObservers() {
        viewModel.menusLiveData.observe(viewLifecycleOwner, { menus ->
            menus?.let { nnMenus ->
                rvMenuList.visibility = View.VISIBLE
                menuItemListAdapter.updateMenuList(nnMenus.sortedBy { it.name })
            }
        })
        viewModel.menusLoadError.observe(viewLifecycleOwner, { isError ->
            isError?.let {
                tvListError.visibility = if (it) View.VISIBLE else View.GONE
            }
        })

        viewModel.loading.observe(viewLifecycleOwner, { isLoading ->
            isLoading?.let {
                if (it) {
                    progressBar.visibility = View.VISIBLE
                    rvMenuList.visibility = View.GONE
                    tvListError.visibility = View.GONE
                } else progressBar.visibility = View.GONE
            }

        })
    }
}