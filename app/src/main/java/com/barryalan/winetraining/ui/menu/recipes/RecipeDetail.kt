package com.barryalan.winetraining.ui.menu.recipes

import com.barryalan.winetraining.ui.menu.ingredients.IngredientWithAmountsAdapter
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.ui.shared.BaseFragment
import com.barryalan.winetraining.ui.shared.util.AreYouSureCallBack
import com.barryalan.winetraining.ui.shared.util.UIMessage
import com.barryalan.winetraining.ui.shared.util.UIMessageType
import com.barryalan.winetraining.util.getProgressDrawable
import com.barryalan.winetraining.util.loadImage
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView
import java.util.*


class RecipeDetail : BaseFragment() {


    private lateinit var imgRecipe: ImageView
    private lateinit var tvRecipeName: MaterialTextView
    private lateinit var tvRecipeType: MaterialTextView
    private lateinit var tvRecipeCalories: MaterialTextView
    private lateinit var abEditRecipe: FloatingActionButton
    private lateinit var abDeleteRecipe: FloatingActionButton
    private lateinit var rvIngredientList: RecyclerView
    private lateinit var rvInstructionList: RecyclerView


    private val ingredientListAdapter = IngredientWithAmountsAdapter(arrayListOf())
    private val instructionListAdapter = RecipeInstructionsListAdapter(arrayListOf())
    private lateinit var viewModel: RecipeDetailViewModel

    private var mSelectedRecipeID: Long = 0L


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipe_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgRecipe = view.findViewById(R.id.img_recipe_recipe_detail)
        tvRecipeName = view.findViewById(R.id.tv_recipe_name_recipe_detail)
        tvRecipeType = view.findViewById(R.id.tv_recipe_type_recipe_detail)
        tvRecipeCalories = view.findViewById(R.id.tv_recipe_calories_recipe_detail)
        abEditRecipe = view.findViewById(R.id.ab_edit_recipe_recipe_detail)
        abDeleteRecipe = view.findViewById(R.id.ab_delete_recipe_recipe_detail)
        rvIngredientList = view.findViewById(R.id.rv_ingredient_list_recipe_detail)
        rvInstructionList = view.findViewById(R.id.rv_instruction_list_recipe_detail)

        viewModel = ViewModelProvider(this).get(RecipeDetailViewModel::class.java)

        arguments?.let {
            mSelectedRecipeID = RecipeDetailArgs.fromBundle(it).recipeUID
            viewModel.fetch(mSelectedRecipeID)

        }

        initIngredientRecyclerView()
        initInstructionRecyclerView()
        subscribeObservers()

        abEditRecipe.setOnClickListener {
            if (mSelectedRecipeID != 0L) {
                val action =
                    RecipeDetailDirections.actionRecipeDetailToRecipeNewEdit(mSelectedRecipeID)
                Navigation.findNavController(view).navigate(action)

            } else {
                Toast.makeText(
                    context,
                    "invalid action id: $mSelectedRecipeID, this object does not exist",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

        abDeleteRecipe.setOnClickListener {
            val callback: AreYouSureCallBack = object :
                AreYouSureCallBack {
                override fun proceed() {
                    viewModel.deleteRecipeWithIngredients(mSelectedRecipeID)
                    Navigation.findNavController(it)
                        .navigate(R.id.action_recipeDetail_to_recipeList)
                }

                override fun cancel() {
                }

            }
            uiCommunicationListener.onUIMessageReceived(
                UIMessage(
                    "Are you sure you want to delete this recipe? This action cannot be un-done",
                    UIMessageType.AreYouSureDialog(callback)
                )
            )
        }
    }

    private fun initIngredientRecyclerView() {
        rvIngredientList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = ingredientListAdapter
        }
    }

    private fun initInstructionRecyclerView() {
        rvInstructionList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = instructionListAdapter
        }
    }

    private fun subscribeObservers() {
        viewModel.recipeWithIngredientsLiveData.observe(
            viewLifecycleOwner, { recipeWithIngredients ->
                recipeWithIngredients?.let { nnRecipeWithIngredients ->
                    tvRecipeName.text = nnRecipeWithIngredients.recipe.name.capitalize(Locale.ROOT)
                    tvRecipeType.text = nnRecipeWithIngredients.recipe.type.capitalize(Locale.ROOT)


                    if (nnRecipeWithIngredients.recipe.calories != null) {
                        tvRecipeCalories.text = nnRecipeWithIngredients.recipe.calories.toString()
                    }else{
                        tvRecipeCalories.text = getString(R.string.ingredient_calories_needed)
                    }

                    nnRecipeWithIngredients.recipe.image?.let { imageURI ->
                        imgRecipe.loadImage(
                            Uri.parse(imageURI),
                            getProgressDrawable(requireContext())
                        )
                    }
                    ingredientListAdapter.updateIngredientList(
                        nnRecipeWithIngredients.ingredients,
                        nnRecipeWithIngredients.amounts
                    )
                    instructionListAdapter.updateInstructionList(nnRecipeWithIngredients.instructions.sortedBy { it.instructionNumber })

                }
            })
    }

}