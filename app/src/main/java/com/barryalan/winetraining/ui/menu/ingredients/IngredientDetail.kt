package com.barryalan.winetraining.ui.menu.ingredients

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.barryalan.winetraining.R
import com.barryalan.winetraining.model.menu.Ingredient
import com.barryalan.winetraining.ui.shared.BaseFragment
import com.barryalan.winetraining.ui.shared.CameraActivity
import com.barryalan.winetraining.ui.shared.util.AreYouSureCallBack
import com.barryalan.winetraining.ui.shared.util.UIMessage
import com.barryalan.winetraining.ui.shared.util.UIMessageType
import com.barryalan.winetraining.util.getProgressDrawable
import com.barryalan.winetraining.util.loadImage
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class IngredientDetail : BaseFragment() {

    private lateinit var viewModel: IngredientDetailViewModel
    private var mIngredientImageURIString: String? = null

    private lateinit var btnCancel: MaterialButton
    private lateinit var btnSaveIngredient: MaterialButton
    private lateinit var etIngredientName: EditText
    private lateinit var etIngredientCalories: EditText
    private lateinit var imgIngredient: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ingredient_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        btnCancel = view.findViewById(R.id.btn_cancel_ingredient_detail)
        btnSaveIngredient = view.findViewById(R.id.btn_save_ingredient_ingredient_detail)
        etIngredientName = view.findViewById(R.id.et_ingredient_name_ingredient_detail)
        etIngredientCalories = view.findViewById(R.id.et_ingredient_calories_ingredient_detail)
        imgIngredient = view.findViewById(R.id.ingredient_img_ingredient_detail)


        viewModel = ViewModelProvider(this).get(IngredientDetailViewModel::class.java)

        arguments?.let {
            val selectedRecipeID = IngredientDetailArgs.fromBundle(it).ingredientUID
            viewModel.fetch(selectedRecipeID)
        }
        subscribeObservers()

        btnCancel.setOnClickListener { v ->
            confirmBackNavigation(v)
        }

        btnSaveIngredient.setOnClickListener {
            when {
                etIngredientName.text.isEmpty() -> {
                    uiCommunicationListener.onUIMessageReceived(
                        UIMessage(
                            "Ingredient must have a name",
                            UIMessageType.ErrorDialog()
                        )
                    )
                }
                else -> {
                    uiCommunicationListener.onUIMessageReceived(
                        UIMessage(
                            "Updated",
                            UIMessageType.Toast()
                        )
                    )

                    saveIngredient(it)
                }
            }
        }

        imgIngredient.setOnClickListener {
            val intent = Intent(activity, CameraActivity::class.java)

            startActivityForResult(intent, 2)
        }
    }

    private fun confirmBackNavigation(view: View) {
        val callback: AreYouSureCallBack = object :
            AreYouSureCallBack {
            override fun proceed() {
                Navigation.findNavController(view).navigate(
                    IngredientDetailDirections.actionIngredientDetailFragmentToIngredientListFragment()
                )
            }

            override fun cancel() {}
        }

        uiCommunicationListener.onUIMessageReceived(
            UIMessage(
                "Are you sure you want to go back? Any information that was not saved on this page will be forever lost",
                UIMessageType.AreYouSureDialog(callback)
            )
        )
    }

    private fun saveIngredient(view: View) {
        lifecycleScope.launch(Dispatchers.Main) {

            //create new ingredient object from UI fields
            val updatedIngredient =
                Ingredient(
                    etIngredientName.text.toString().trim(),
                    mIngredientImageURIString,
                    etIngredientCalories.text.toString().toInt()
                )

            //update ingredient in the database
            val updateJob = viewModel.updateIngredient(updatedIngredient)

            //block the current co-routine until the job is completed
            updateJob.join()

            //navigate back to ingredientList fragment
            Navigation.findNavController(view)
                .navigate(IngredientDetailDirections.actionIngredientDetailFragmentToIngredientListFragment())

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                2 -> {
                    data?.let { intent ->
                        mIngredientImageURIString = intent.extras?.get("photoURI").toString()

                        imgIngredient.loadImage(
                            intent.extras?.get("photoURI") as Uri?,
                            getProgressDrawable(requireContext())
                        )
                    }
                }
            }
        }
    }

    private fun subscribeObservers() {
        viewModel.selectedIngredientLiveData.observe(viewLifecycleOwner, { ingredient ->
            ingredient?.let { nnIngredient ->
                etIngredientName.setText(ingredient.name.capitalize(Locale.ROOT))

                if (nnIngredient.calories != null) {
                    etIngredientCalories.setText(nnIngredient.calories.toString())
                } else {
                    etIngredientCalories.setText("")
                }

                nnIngredient.image?.let { imageURI ->
                    mIngredientImageURIString = imageURI
                    imgIngredient.loadImage(
                        Uri.parse(imageURI),
                        getProgressDrawable(requireContext())
                    )
                }
            }
        })
    }

}
//todo figure out what to do with items that have multiple versions like a meat or cheese option and their prices
//todo figure out how to calculate calories based on amounts
//todo add timestamp to all items
