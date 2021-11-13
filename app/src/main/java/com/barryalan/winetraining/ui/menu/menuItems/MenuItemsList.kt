package com.barryalan.winetraining.ui.menu.menuItems

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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.model.menu.MenuItem
import com.barryalan.winetraining.ui.menu.ingredients.IngredientAdapter
import com.barryalan.winetraining.ui.menu.ingredients.IngredientCallback
import com.barryalan.winetraining.ui.shared.BaseFragment
import com.barryalan.winetraining.util.addNewItem
import com.barryalan.winetraining.util.removeItem
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textview.MaterialTextView
import java.util.*


class MenuItemsList : BaseFragment(), IngredientCallback {

    private val menuItemListAdapter = MenuItemListAdapter(arrayListOf())
    private val ingredientListAdapter = IngredientAdapter(arrayListOf(), this)
    private lateinit var viewModel: MenuItemsListViewModel


    private lateinit var rvMenuItemList: RecyclerView
    private lateinit var menuItemSearch: SearchView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var tvListError: MaterialTextView
    private lateinit var progressBar: ProgressBar
    private lateinit var ibtnAdvancedSearch: ImageView
    private lateinit var abNewMenuItem: ExtendedFloatingActionButton

    private lateinit var chipGroupSort: ChipGroup
    private lateinit var chipGroupFilter: ChipGroup


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu_items_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvMenuItemList = requireView().findViewById(R.id.rv_list_menu_item_list)
        menuItemSearch = requireView().findViewById(R.id.search_menu_menu_item_list)
        refreshLayout = requireView().findViewById(R.id.refreshLayout_menu_item)
        tvListError = requireView().findViewById(R.id.list_error_menu_item_list)
        progressBar = requireView().findViewById(R.id.loading_view_menu_item_list)
        ibtnAdvancedSearch = view.findViewById(R.id.ibtn_advanced_search_menu_item_list)
        chipGroupSort = view.findViewById(R.id.chip_group_sort_menu_item_list)
        chipGroupFilter = view.findViewById(R.id.chip_group_filter_menu_item_list)
        abNewMenuItem = view.findViewById(R.id.action_button_new_menu_item_list)

        viewModel = ViewModelProvider(this).get(MenuItemsListViewModel::class.java)

        viewModel.refresh()

        initRecyclerView()
        initRefreshLayout()
        subscribeObservers()

        ibtnAdvancedSearch.setOnClickListener { openAdvancedSearch() }


        chipGroupSort.setOnCheckedChangeListener { group, checkedId ->
            viewModel.searchSortByLiveData.value = checkedId
        }


        abNewMenuItem.setOnClickListener {
            view.findNavController().navigate(R.id.action_menuItemsList_to_menuItemNewEdit)
        }

    }


    private fun initRecyclerView() {
        rvMenuItemList.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = menuItemListAdapter
        }

        menuItemSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                menuItemListAdapter.filter.filter(newText)
                return false
            }

        })
        val searchIcon = menuItemSearch.findViewById<ImageView>(R.id.search_mag_icon)
        val cancelIcon = menuItemSearch.findViewById<ImageView>(R.id.search_close_btn)
        val textView = menuItemSearch.findViewById<TextView>(R.id.search_src_text)

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
            rvMenuItemList.visibility = View.GONE
            tvListError.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            viewModel.refresh()
            refreshLayout.isRefreshing = false
        }
    }

    private fun openAdvancedSearch() {
        MaterialDialog(requireContext(), BottomSheet()).show {
            cancelOnTouchOutside(false)


            //setup bottom sheet
            customView(R.layout.bottom_sheet_advanced_search)
            val chipGroupIngredientsWanted =
                this.view.findViewById<ChipGroup>(R.id.chip_group_ingredients_advanced_search)
            val rvIngredients =
                this.view.findViewById<RecyclerView>(R.id.rv_ingredient_advanced_search)
            val ingredientSearch = this.view.findViewById<SearchView>(R.id.search_advanced_search)
            val chipNot = this.view.findViewById<Chip>(R.id.chip_not_advanced_search)
            title(R.string.advanced_search_options)
            positiveButton(R.string.apply) {
                ingredientListAdapter.notWanted = false
                viewModel.refresh()
            }
            chipNot.setOnCheckedChangeListener { buttonView, isChecked ->
                ingredientListAdapter.notWanted = isChecked
            }

            //setup rv and search
            rvIngredients.apply {
                layoutManager = GridLayoutManager(context, 3)
                adapter = ingredientListAdapter
            }
            ingredientSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    ingredientListAdapter.filter.filter(newText)
                    return false
                }

            })
            val searchIcon = ingredientSearch.findViewById<ImageView>(R.id.search_mag_icon)
            val cancelIcon = ingredientSearch.findViewById<ImageView>(R.id.search_close_btn)
            val textView = ingredientSearch.findViewById<TextView>(R.id.search_src_text)
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


            //handle data
            viewModel.ingredientsLiveData.value?.let { ingredient ->
                ingredientListAdapter.updateIngredientList(
                    ingredient.sortedBy { it.name })
            }

            viewModel.filterLiveData.observe(viewLifecycleOwner, { filerList ->

                chipGroupIngredientsWanted.removeAllViews()

                filerList.map { chipName ->
                    val chip = Chip(requireContext())
                    chip.text = chipName.capitalize(Locale.ROOT)
                    chip.isCloseIconVisible = true
                    chipGroupIngredientsWanted.addView(chip)

                    chip.setOnClickListener {
                        viewModel.filterLiveData.removeItem(chipName.toLowerCase(Locale.ROOT))

                        chipGroupIngredientsWanted.removeView(chip)
                    }
                }
            })


        }
    }

    override fun onIngredientClicked(ingredientName: String) {
        viewModel.filterLiveData.addNewItem(ingredientName.toLowerCase(Locale.ROOT))
    }

    private fun subscribeObservers() {
        viewModel.menuItemsFilteredLiveData.observe(viewLifecycleOwner, { menuItems ->
            menuItems?.let { nnMenuItem ->
                rvMenuItemList.visibility = View.VISIBLE

                val itemsList = mutableListOf<MenuItem>()
                itemsList.addAll(nnMenuItem)

                menuItemListAdapter.updateMenuItemsList(itemsList.sortedBy { it.name })
            }
        })
        viewModel.menuItemsLoadError.observe(viewLifecycleOwner, { isError ->
            isError?.let {
                tvListError.visibility = if (it) View.VISIBLE else View.GONE
            }
        })

        viewModel.loading.observe(viewLifecycleOwner, { isLoading ->
            isLoading?.let {
                if (it) {
                    progressBar.visibility = View.VISIBLE
                    rvMenuItemList.visibility = View.GONE
                    tvListError.visibility = View.GONE
                } else progressBar.visibility = View.GONE
            }

        })


        viewModel.searchSortByLiveData.observe(viewLifecycleOwner, {
            if (it == -1) {
                chipGroupSort.check(R.id.chip_name_asc_menu_item_list)
            } else {
                chipGroupSort.check(it)
            }
            menuItemListAdapter.updateSort(it)
        })

        viewModel.filterLiveData.observe(viewLifecycleOwner, { filterList ->


            chipGroupFilter.removeAllViews()

            filterList.map { chipName ->
                val chip = Chip(requireContext())
                chip.text = chipName.capitalize(Locale.ROOT)
                chip.isCloseIconVisible = true
                chipGroupFilter.addView(chip)

                chip.setOnClickListener {
                    viewModel.filterLiveData.removeItem(chipName.toLowerCase(Locale.ROOT))

                    chipGroupFilter.removeView(chip)
                    viewModel.refresh()
                }
            }
        })

    }


}