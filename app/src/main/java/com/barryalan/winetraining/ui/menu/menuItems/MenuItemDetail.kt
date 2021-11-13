package com.barryalan.winetraining.ui.menu.menuItems

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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.model.menu.Instruction
import com.barryalan.winetraining.ui.menu.recipes.RecipeInstructionsListAdapter
import com.barryalan.winetraining.ui.menu.recipes.RecipeListWithAmountsAdapter
import com.barryalan.winetraining.ui.shared.BaseFragment
import com.barryalan.winetraining.ui.shared.util.AreYouSureCallBack
import com.barryalan.winetraining.ui.shared.util.UIMessage
import com.barryalan.winetraining.ui.shared.util.UIMessageType
import com.barryalan.winetraining.util.getProgressDrawable
import com.barryalan.winetraining.util.loadImage
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView
import java.util.*


class MenuItemDetail : BaseFragment() {


    private lateinit var imgMenuItem: ImageView
    private lateinit var tvMenuItemName: MaterialTextView
    private lateinit var tvMenuItemType: MaterialTextView
    private lateinit var tvMenuItemCalories: MaterialTextView
    private lateinit var abEditMenuItem: FloatingActionButton
    private lateinit var abDeleteMenuItem: FloatingActionButton
    private lateinit var rvRecipeList: RecyclerView
    private lateinit var rvInstructionList: RecyclerView

    private val instructionListAdapter = RecipeInstructionsListAdapter(arrayListOf())
    private val recipeListAdapter = RecipeListWithAmountsAdapter(arrayListOf())
    private lateinit var viewModel: MenuItemDetailViewModel

    private var mSelectedMenuItemID: Long = 0L


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu_item_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgMenuItem = view.findViewById(R.id.img_menu_item_menu_item_detail)
        tvMenuItemName = view.findViewById(R.id.tv_menu_item_name_menu_item_detail)
        tvMenuItemType = view.findViewById(R.id.tv_menu_item_type_menu_item_detail)
        tvMenuItemCalories = view.findViewById(R.id.tv_menu_item_calories_menu_item_detail)
        abEditMenuItem = view.findViewById(R.id.ab_edit_menu_item_menu_item_detail)
        abDeleteMenuItem = view.findViewById(R.id.ab_delete_menu_item_menu_item_detail)
        rvRecipeList = view.findViewById(R.id.rv_recipe_list_menu_item_detail)
        rvInstructionList = view.findViewById(R.id.rv_instruction_list_menu_item_detail)

        viewModel = ViewModelProvider(this).get(MenuItemDetailViewModel::class.java)

        arguments?.let {
            mSelectedMenuItemID = MenuItemDetailArgs.fromBundle(it).menuItemUID
            viewModel.fetch(mSelectedMenuItemID)
        }


        initRecipeRecyclerView()
        initInstructionRecyclerView()
        subscribeObservers()

        abEditMenuItem.setOnClickListener {
            if (mSelectedMenuItemID != 0L) {
                val action = MenuItemDetailDirections.actionMenuItemDetailToMenuItemNewEdit(
                    mSelectedMenuItemID
                )
                Navigation.findNavController(view).navigate(action)

            } else {
                Toast.makeText(
                    context,
                    "invalid action id: $mSelectedMenuItemID",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

        abDeleteMenuItem.setOnClickListener {
            val callback: AreYouSureCallBack = object :
                AreYouSureCallBack {
                override fun proceed() {
                    viewModel.deleteMenuItemAndAssociations(mSelectedMenuItemID)
                    Navigation.findNavController(it)
                        .navigate(R.id.action_menuItemDetail_to_menuItemsList)
                }

                override fun cancel() {
                }

            }
            uiCommunicationListener.onUIMessageReceived(
                UIMessage(
                    "Are you sure you want to delete this menu item? This action cannot be un-done",
                    UIMessageType.AreYouSureDialog(callback)
                )
            )
        }
    }

    private fun initRecipeRecyclerView() {
        rvRecipeList.apply {
            layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
            adapter = recipeListAdapter
        }
    }

    private fun initInstructionRecyclerView() {

        val itemTouchHelperCallback = object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                // Specify the directions of movement
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                return makeMovementFlags(dragFlags, 0)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // Notify your adapter that an item is moved from x position to y position

                val removedInstruction: Instruction =
                    instructionListAdapter.getInstructionList().removeAt(viewHolder.adapterPosition)

                instructionListAdapter.getInstructionList()
                    .add(target.adapterPosition, removedInstruction)

                instructionListAdapter.notifyItemMoved(
                    viewHolder.adapterPosition,
                    target.adapterPosition
                )

                return true
            }

            override fun isLongPressDragEnabled(): Boolean {
                // true: if you want to start dragging on long press
                // false: if you want to handle it yourself
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                // Handle action state changes
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                // Called by the ItemTouchHelper when the user interaction with an element is over and it also completed its animation
                // This is a good place to send update to your backend about changes
                instructionListAdapter.notifyDataSetChanged()

            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)

        rvInstructionList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = instructionListAdapter
            itemTouchHelper.attachToRecyclerView(this)
        }
    }

    private fun subscribeObservers() {
        viewModel.menuItemWithRecipesLiveData.observe(viewLifecycleOwner, { menuItemWithRecipes ->
            menuItemWithRecipes?.let { nnMenuItemWithRecipes ->

                tvMenuItemName.text = nnMenuItemWithRecipes.menuItem.name.capitalize(Locale.ROOT)

                tvMenuItemType.text = nnMenuItemWithRecipes.menuItem.type.capitalize(Locale.ROOT)

                nnMenuItemWithRecipes.menuItem.image?.let { imageURI ->
                    imgMenuItem.loadImage(
                        Uri.parse(imageURI),
                        getProgressDrawable(requireContext())
                    )
                }

                if (nnMenuItemWithRecipes.menuItem.calories != null) {
                    tvMenuItemCalories.text = nnMenuItemWithRecipes.menuItem.calories.toString()
                }else{
                    tvMenuItemCalories.text = getString(R.string.ingredient_calories_needed)
                }

                recipeListAdapter.updateRecipeList(nnMenuItemWithRecipes.recipes, nnMenuItemWithRecipes.amounts)
                instructionListAdapter.updateInstructionList(nnMenuItemWithRecipes.instructions)
            }
        })
    }

}