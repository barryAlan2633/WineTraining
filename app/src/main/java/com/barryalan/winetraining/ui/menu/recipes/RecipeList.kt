package com.barryalan.winetraining.ui.menu.recipes

import android.content.res.Configuration.*
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import com.barryalan.winetraining.ui.menu.ingredients.IngredientAdapter
import com.barryalan.winetraining.ui.menu.ingredients.IngredientCallback
import com.barryalan.winetraining.ui.shared.BaseFragment
import com.barryalan.winetraining.util.addNewItem
import com.barryalan.winetraining.util.removeItem
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textview.MaterialTextView
import java.util.*


class RecipeList : BaseFragment(), IngredientCallback {

    private val recipeListAdapter = RecipeListAdapter(arrayListOf())
    private val ingredientListAdapter = IngredientAdapter(arrayListOf(), this)
    private lateinit var viewModel: RecipeListViewModel


    private lateinit var rvRecipeList: RecyclerView
    private lateinit var recipeSearch: SearchView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var tvListError: MaterialTextView
    private lateinit var progressBar: ProgressBar
    private lateinit var ibtnAdvancedSearch: ImageView
    private lateinit var chipGroupSort: ChipGroup
    private lateinit var chipGroupFilter: ChipGroup
    private lateinit var abNewRecipe: ExtendedFloatingActionButton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipe_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvRecipeList = requireView().findViewById(R.id.rv_recipe_list_recipe_list)
        recipeSearch = requireView().findViewById(R.id.search_recipe_recipe_list)
        refreshLayout = requireView().findViewById(R.id.refreshLayout_recipe_list)
        tvListError = requireView().findViewById(R.id.list_error_recipe_list)
        progressBar = requireView().findViewById(R.id.loading_view_recipe_list)
        ibtnAdvancedSearch = view.findViewById(R.id.ibtn_advanced_search_recipe_list)
        chipGroupSort = view.findViewById(R.id.chip_group_sort_recipe_list)
        chipGroupFilter = view.findViewById(R.id.chip_group_filter_recipe_list)
        abNewRecipe = view.findViewById(R.id.action_button_new_recipe_list)

        viewModel = ViewModelProvider(this).get(RecipeListViewModel::class.java)
        viewModel.refresh()

        initRecipeRecyclerView()
        initRefreshLayout()
        subscribeObservers()

        ibtnAdvancedSearch.setOnClickListener { openAdvancedSearch() }

        abNewRecipe.setOnClickListener {
            view.findNavController().navigate(R.id.action_recipeList_to_recipeNewEdit)
        }

        chipGroupSort.setOnCheckedChangeListener { group, checkedId ->
            viewModel.searchSortByLiveData.value = checkedId
        }
    }

    private fun initRecipeRecyclerView() {
        rvRecipeList.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = recipeListAdapter
        }

        recipeSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                recipeListAdapter.filter.filter(newText)
                return false
            }

        })

        val searchIcon = recipeSearch.findViewById<ImageView>(R.id.search_mag_icon)
        val cancelIcon = recipeSearch.findViewById<ImageView>(R.id.search_close_btn)
        val textView = recipeSearch.findViewById<TextView>(R.id.search_src_text)

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
            rvRecipeList.visibility = View.GONE
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
            chipNot.setOnCheckedChangeListener { buttonView, isChecked ->
                ingredientListAdapter.notWanted = isChecked
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

        viewModel.recipesFilteredLiveData.observe(viewLifecycleOwner, { recipes ->
            recipes?.let {
                rvRecipeList.visibility = View.VISIBLE
                recipeListAdapter.updateRecipeList(recipes)
            }
        })

        viewModel.recipeLoadError.observe(viewLifecycleOwner, { isError ->
            isError?.let {
                tvListError.visibility = if (it) View.VISIBLE else View.GONE
            }
        })

        viewModel.loading.observe(viewLifecycleOwner, { isLoading ->
            isLoading?.let {
                if (it) {
                    progressBar.visibility = View.VISIBLE
                    rvRecipeList.visibility = View.GONE
                    tvListError.visibility = View.GONE
                } else progressBar.visibility = View.GONE
            }
        })

        viewModel.searchSortByLiveData.observe(viewLifecycleOwner, {
            if (it == -1) {
                chipGroupSort.check(R.id.chip_name_asc_recipe_list)
            } else {
                chipGroupSort.check(it)
            }
            recipeListAdapter.updateSort(it)
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