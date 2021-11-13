package com.barryalan.winetraining.ui.menu.menu

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.model.menu.Menu
import com.barryalan.winetraining.model.menu.MenuItem
import com.barryalan.winetraining.model.menu.Price
import com.barryalan.winetraining.ui.menu.menuItems.MenuItemWithPricesListAdapter
import com.barryalan.winetraining.ui.shared.BaseFragment
import com.barryalan.winetraining.ui.shared.CameraActivity
import com.barryalan.winetraining.ui.shared.util.AreYouSureCallBack
import com.barryalan.winetraining.ui.shared.util.UIMessage
import com.barryalan.winetraining.ui.shared.util.UIMessageType
import com.barryalan.winetraining.util.getProgressDrawable
import com.barryalan.winetraining.util.loadImage
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MenuNewEdit : BaseFragment(), BottomSheetMenuItemCallback {

    private lateinit var imgMenu: ImageView
    private lateinit var etMenuName: EditText
    private lateinit var rvMenuItemList: RecyclerView
    private lateinit var btnUpdateSaveMenu: MaterialButton
    private lateinit var btnCancel: MaterialButton
    private lateinit var btnEditMenuItems: MaterialButton
    private lateinit var chipGroup: ChipGroup

    private val menuItemListAdapter = MenuItemWithPricesListAdapter(arrayListOf())
    private val bottomSheetMenuItemListAdapter = BottomSheetMenuItemListAdapter(
        arrayListOf(),
        this,
        arrayListOf(),
        arrayListOf()
    )
    private lateinit var viewModel: MenuNewEditViewModel

    private var mMenuToEditUID: Long = -1L
    private var mMenuImageURIString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            confirmBackNavigation()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        return inflater.inflate(R.layout.fragment_menu_new_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgMenu = view.findViewById(R.id.img_menu_menu_new_edit)
        etMenuName = view.findViewById(R.id.et_menu_name_menu_new_edit)
        rvMenuItemList = view.findViewById(R.id.rv_menu_item_list_menu_new_edit)
        btnUpdateSaveMenu = view.findViewById(R.id.btn_update_save_menu_menu_new_edit)
        btnCancel = view.findViewById(R.id.btn_cancel_menu_new_edit)
        btnEditMenuItems = view.findViewById(R.id.btn_edit_menu_items)
        chipGroup = view.findViewById(R.id.chip_group_menu_new_edit)

        viewModel = ViewModelProvider(this).get(MenuNewEditViewModel::class.java)
        viewModel.fetchMenuItemList()
        arguments?.let {
            mMenuToEditUID = MenuNewEditArgs.fromBundle(it).menuUID
            viewModel.fetchSelectedMenuWithMenuItems(mMenuToEditUID)
        }

        initRecyclerView()
        subscribeObservers()

        btnEditMenuItems.setOnClickListener {
            openMenuItemsBottomSheet()
        }

        btnCancel.setOnClickListener {
            confirmBackNavigation()
        }

        btnUpdateSaveMenu.setOnClickListener {

            //Check to make sure there is a name on the menu
            if (etMenuName.text.isEmpty()) {
                uiCommunicationListener.onUIMessageReceived(
                    UIMessage(
                        "Menu must have a name",
                        UIMessageType.ErrorDialog()
                    )
                )
            } else if (chipGroup.checkedChipId == View.NO_ID) {
                uiCommunicationListener.onUIMessageReceived(
                    UIMessage(
                        "Menu must have a type. (Drink or Food)",
                        UIMessageType.ErrorDialog()
                    )
                )
            } else {
                //User is in this fragment to make a new menu
                if (mMenuToEditUID == -1L) {
                    if (etMenuName.text.toString() == "menu") {
                        viewModel.initGloriasMenus()
                    } else if (etMenuName.text.toString() == "clear menu") {
                        viewModel.nukeMenus()
                    } else {
                        saveNewMenuItem()
                    }
                } else {//User is in this fragment to edit an existing recipe
                    updateMenuItem()
                }
            }

        }

        imgMenu.setOnClickListener {
            val intent = Intent(activity, CameraActivity::class.java)

            startActivityForResult(intent, 1)
        }

    }

    private fun initRecyclerView() {
        rvMenuItemList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = menuItemListAdapter
        }
    }

    private fun saveNewMenuItem() {
        uiCommunicationListener.onUIMessageReceived(
            UIMessage(
                "Saved",
                UIMessageType.Toast()
            )
        )

        val menuType = when (chipGroup.checkedChipId) {
            R.id.chip_drink_menu_new_edit -> {
                "drink"
            }
            R.id.chip_food_menu_new_edit -> {
                "food"
            }
            else -> null
        }

        val newMenu = Menu(
            etMenuName.text.toString().trim(),
            mMenuImageURIString,
            menuType!!
        )


        lifecycleScope.launch(Dispatchers.Main) {
            //save item to database
            val savingJob = viewModel.saveMenuWithMenuItems(
                newMenu,
                menuItemListAdapter.getMenuItemList(),
                menuItemListAdapter.getMenuItemPricesList()
            )

            //block the current co-routine until the job is completed
            savingJob.join()

            //navigate to back to menu item list
            Navigation.findNavController(requireView())
                .navigate(R.id.action_menuNewEdit_to_menuList)
        }
    }

    private fun updateMenuItem() {
        uiCommunicationListener.onUIMessageReceived(
            UIMessage(
                "Updated",
                UIMessageType.Toast()
            )
        )

        val menuType = when (chipGroup.checkedChipId) {
            R.id.chip_drink_menu_new_edit -> {
                "drink"
            }
            R.id.chip_food_menu_new_edit -> {
                "food"
            }
            else -> null
        }

        val updatedMenu = Menu(
            etMenuName.text.toString().trim(),
            mMenuImageURIString,
            menuType!!
        )

        updatedMenu.id = mMenuToEditUID

        lifecycleScope.launch(Dispatchers.Main) {
            //update item in database
            val updatingJob = viewModel.updateMenuWithMenuItems(
                updatedMenu,
                menuItemListAdapter.getMenuItemList(),
                menuItemListAdapter.getMenuItemPricesList()
            )

            //block the current co-routine until the job is completed
            updatingJob.join()

            //navigate to back to menu item list
            val action =
                MenuNewEditDirections.actionMenuNewEditToMenuDetail(mMenuToEditUID)
            Navigation.findNavController(requireView()).navigate(action)
        }
    }

    private fun openMenuItemsBottomSheet() {
        MaterialDialog(requireContext(), BottomSheet()).show {
            cancelOnTouchOutside(false)

            customView(R.layout.bottom_sheet_with_rv)
            val rvMenuItems = this.view.findViewById<RecyclerView>(R.id.rv_bottom_with_rv)

            rvMenuItems.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = bottomSheetMenuItemListAdapter
            }

            title(R.string.select_menu_items)
            positiveButton(R.string.close)
        }

        bottomSheetMenuItemListAdapter.updateMenuItemsList(
            null,
            menuItemListAdapter.getMenuItemList(),
            menuItemListAdapter.getMenuItemPricesList()
        )


    }


    //handle result of image taken
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                1 -> {
                    data?.let { intent ->
                        mMenuImageURIString = intent.extras?.get("photoURI").toString()

                        imgMenu.loadImage(
                            intent.extras?.get("photoURI") as Uri?,
                            getProgressDrawable(requireContext())
                        )
                    }
                }
            }
        }
    }

    private fun confirmBackNavigation() {
        val callback: AreYouSureCallBack = object :
            AreYouSureCallBack {
            override fun proceed() {

                //User is in this fragment to make a new menu item
                if (mMenuToEditUID == -1L) {

                    Navigation.findNavController(requireView())
                        .navigate(R.id.action_menuNewEdit_to_menuList)
                } else {//User is in this fragment to edit an existing menu item

                    val action =
                        MenuNewEditDirections.actionMenuNewEditToMenuDetail(
                            mMenuToEditUID
                        )
                    Navigation.findNavController(requireView()).navigate(action)
                }
            }

            override fun cancel() {
                //Do nothing
            }
        }

        uiCommunicationListener.onUIMessageReceived(
            UIMessage(
                "Are you sure you want to go back? Any information that was not saved on this page will be forever lost",
                UIMessageType.AreYouSureDialog(callback)
            )
        )
    }

    override fun addMenuItemWithPrice(menuItem: MenuItem, price: String) {
        when (price) {
            "" -> {
                uiCommunicationListener.onUIMessageReceived(
                    UIMessage(
                        "You must input a price for this menu item if you wish to add it to this menu",
                        UIMessageType.ErrorDialog()
                    )
                )
            }
            else -> {
                menuItemListAdapter.addMenuItem(menuItem, Price(price.toFloat()))
                uiCommunicationListener.onUIMessageReceived(
                    UIMessage(
                        "You have added ${menuItem.name.capitalize(Locale.ROOT)}",
                        UIMessageType.Toast()
                    )
                )
            }
        }
    }

    override fun removeMenuItemWithPrice(menuItem: MenuItem, price: String) {
        when (price) {
            "" -> {
                uiCommunicationListener.onUIMessageReceived(
                    UIMessage(
                        "This is what you call a bug, *ahem* I mean a #feature#",
                        UIMessageType.ErrorDialog()
                    )
                )
            }
            else -> {
                menuItemListAdapter.removeMenuItem(menuItem)
                uiCommunicationListener.onUIMessageReceived(
                    UIMessage(
                        "You have removed ${menuItem.name.capitalize(Locale.ROOT)}",
                        UIMessageType.Toast()
                    )
                )
            }
        }


    }


    private fun subscribeObservers() {
        viewModel.menuToUpdateLiveData.observe(
            viewLifecycleOwner, { menuItemWithRecipes ->
                menuItemWithRecipes?.let {
                    etMenuName.setText(menuItemWithRecipes.menu.name.capitalize(Locale.ROOT))

                    if (menuItemWithRecipes.menu.type.decapitalize(Locale.ROOT) == "drink") {
                        chipGroup.check(R.id.chip_drink_menu_new_edit)
                    } else {
                        chipGroup.check(R.id.chip_food_menu_new_edit)
                    }

                    menuItemWithRecipes.menu.image?.let {
                        imgMenu.loadImage(
                            Uri.parse(it),
                            getProgressDrawable(requireContext())
                        )
                        mMenuImageURIString = it
                    }


                    menuItemListAdapter.updateMenuItemsList(
                        menuItemWithRecipes.menuItems,
                        menuItemWithRecipes.menuItemsPrices
                    )
                }
            })



        viewModel.menuItemListLiveData.observe(viewLifecycleOwner, { menuItems ->
            menuItems?.let {
                bottomSheetMenuItemListAdapter.updateMenuItemsList(
                    menuItems.sortedBy { it.name },
                    menuItemListAdapter.getMenuItemList(),
                    menuItemListAdapter.getMenuItemPricesList()
                )
            }
        })
    }


}


//todo replace two list with a pair that contains the menuitem and the price

