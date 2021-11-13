package com.barryalan.winetraining.ui.menu.ingredients

import android.content.res.Configuration
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.barryalan.winetraining.R
import com.barryalan.winetraining.ui.shared.BaseFragment
import com.barryalan.winetraining.ui.shared.util.AreYouSureCallBack
import com.barryalan.winetraining.ui.shared.util.UIMessage
import com.barryalan.winetraining.ui.shared.util.UIMessageType
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textview.MaterialTextView


class IngredientList : BaseFragment(), IngredientWithRecipesCallback {

    private lateinit var ingredientListAdapter: IngredientWithRecipesListAdapter
    private lateinit var viewModel: IngredientListViewModel

    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var searchIngredientWithRecipes: SearchView
    private lateinit var rvIngredientList: RecyclerView
    private lateinit var tvListError: MaterialTextView
    private lateinit var pbLoadingView: ProgressBar
    private lateinit var chipGroupSort: ChipGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ingredient_list, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshLayout = view.findViewById(R.id.refreshLayout_ingredient_list)
        searchIngredientWithRecipes =
            view.findViewById(R.id.search_ingredient_with_recipes_ingredient_list)
        rvIngredientList = view.findViewById(R.id.rv_ingredient_list_ingredient_list)
        tvListError = view.findViewById(R.id.tv_list_error_ingredient_list)
        pbLoadingView = view.findViewById(R.id.pb_loading_view_ingredient_list)
        chipGroupSort = view.findViewById(R.id.chip_group_sort_ingredient_list)


        viewModel = ViewModelProvider(this).get(IngredientListViewModel::class.java)
        viewModel.refresh()
        initRecyclerView()
        initRefreshLayout()
        subscribeObservers()

        chipGroupSort.setOnCheckedChangeListener { group, checkedId ->
            viewModel.searchSortByLiveData.value = checkedId
        }



    }

    private fun initRecyclerView() {
        rvIngredientList.apply {
            layoutManager = GridLayoutManager(context, 3)
            ingredientListAdapter =
                IngredientWithRecipesListAdapter(ArrayList(), this@IngredientList)
            adapter = ingredientListAdapter
        }

        searchIngredientWithRecipes.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                ingredientListAdapter.filter.filter(newText)
                return false
            }

        })
        val searchIcon = searchIngredientWithRecipes.findViewById<ImageView>(R.id.search_mag_icon)
        val cancelIcon = searchIngredientWithRecipes.findViewById<ImageView>(R.id.search_close_btn)
        val textView = searchIngredientWithRecipes.findViewById<TextView>(R.id.search_src_text)

        when (resources.configuration.uiMode) {
            Configuration.UI_MODE_NIGHT_NO -> {
                searchIcon.setColorFilter(Color.WHITE)

                cancelIcon.setColorFilter(Color.WHITE)

                textView.setTextColor(Color.WHITE)
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                searchIcon.setColorFilter(Color.BLACK)

                cancelIcon.setColorFilter(Color.BLACK)

                textView.setTextColor(Color.BLACK)
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                searchIcon.setColorFilter(Color.WHITE)

                cancelIcon.setColorFilter(Color.WHITE)

                textView.setTextColor(Color.WHITE)
            }
        }
    }

    private fun initRefreshLayout() {
        refreshLayout.setOnRefreshListener {
            rvIngredientList.visibility = View.GONE
            tvListError.visibility = View.GONE
            pbLoadingView.visibility = View.VISIBLE
            viewModel.refresh()
            refreshLayout.isRefreshing = false
        }
    }

    override fun onDeleteIngredient(ingredientID: Long) {
        val callback: AreYouSureCallBack = object :
            AreYouSureCallBack {
            override fun proceed() {
                viewModel.deleteIngredient(ingredientID)
                viewModel.refresh()
            }

            override fun cancel() {}
        }

        uiCommunicationListener.onUIMessageReceived(
            UIMessage(
                "Are you sure you wish to delete this ingredient? This action cannot be un-done",
                UIMessageType.AreYouSureDialog(callback)
            )
        )


    }

    private fun subscribeObservers() {
        viewModel.ingredientWithRecipesListLiveData.observe(
            viewLifecycleOwner, { ingredients ->
                ingredients?.let {
                    rvIngredientList.visibility = View.VISIBLE
                    ingredientListAdapter.updateIngredientList(ingredients.sortedBy { it.ingredient.name })
                }
            })
        viewModel.ingredientLoadError.observe(viewLifecycleOwner, { isError ->
            isError?.let {
                tvListError.visibility = if (it) View.VISIBLE else View.GONE
            }
        })

        viewModel.loading.observe(viewLifecycleOwner, { isLoading ->
            isLoading?.let {
                if (it) {
                    pbLoadingView.visibility = View.VISIBLE
                    rvIngredientList.visibility = View.GONE
                    tvListError.visibility = View.GONE
                } else pbLoadingView.visibility = View.GONE
            }
        })


        viewModel.searchSortByLiveData.observe(viewLifecycleOwner, {
            if (it == -1) {
                chipGroupSort.check(R.id.chip_name_asc_ingredient_list)
            } else {
                chipGroupSort.check(it)
            }
            ingredientListAdapter.updateSort(it)
        })
    }

}
