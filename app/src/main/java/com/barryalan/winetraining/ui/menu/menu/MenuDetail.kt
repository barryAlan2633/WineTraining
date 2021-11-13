package com.barryalan.winetraining.ui.menu.menu

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
import androidx.recyclerview.widget.RecyclerView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.ui.menu.menuItems.MenuItemWithPricesListAdapter
import com.barryalan.winetraining.ui.shared.BaseFragment
import com.barryalan.winetraining.ui.shared.util.AreYouSureCallBack
import com.barryalan.winetraining.ui.shared.util.UIMessage
import com.barryalan.winetraining.ui.shared.util.UIMessageType
import com.barryalan.winetraining.util.getProgressDrawable
import com.barryalan.winetraining.util.loadImage
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView
import java.util.*

class MenuDetail : BaseFragment() {

    private lateinit var imgMenu: ImageView
    private lateinit var tvMenuName: MaterialTextView
    private lateinit var tvMenuType: MaterialTextView
    private lateinit var abEditMenu: FloatingActionButton
    private lateinit var abDeleteMenu: FloatingActionButton
    private lateinit var rvMenuItemList: RecyclerView


    private val menuListAdapter = MenuItemWithPricesListAdapter(arrayListOf())
    private lateinit var viewModel: MenuDetailViewModel

    private var mSelectedMenuID: Long = 0L


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu_detail, container, false)
    }

    @ExperimentalStdlibApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgMenu = view.findViewById(R.id.img_menu_menu_detail)
        tvMenuName = view.findViewById(R.id.tv_menu_name_menu_detail)
        tvMenuType = view.findViewById(R.id.tv_menu_item_type_menu_detail)
        abEditMenu = view.findViewById(R.id.ab_edit_menu_menu_detail)
        abDeleteMenu = view.findViewById(R.id.ab_delete_menu_menu_detail)
        rvMenuItemList = view.findViewById(R.id.rv_menu_item_list_menu_detail)

        viewModel = ViewModelProvider(this).get(MenuDetailViewModel::class.java)

        arguments?.let {
            mSelectedMenuID = MenuDetailArgs.fromBundle(it).menuUID
            viewModel.fetch(mSelectedMenuID)
        }


        initRecyclerView()
        subscribeObservers()

        abEditMenu.setOnClickListener {
            if (mSelectedMenuID != 0L) {
                val action = MenuDetailDirections.actionMenuDetailToMenuNewEdit(
                    mSelectedMenuID
                )
                Navigation.findNavController(view).navigate(action)

            } else {
                Toast.makeText(
                    context,
                    "invalid action id: $mSelectedMenuID",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

        abDeleteMenu.setOnClickListener {
            val callback: AreYouSureCallBack = object :
                AreYouSureCallBack {
                override fun proceed() {
                    viewModel.deleteMenuAndAssociations(mSelectedMenuID)
                    Navigation.findNavController(it)
                        .navigate(R.id.action_menuDetail_to_menuList)
                }

                override fun cancel() {
                }

            }
            uiCommunicationListener.onUIMessageReceived(
                UIMessage(
                    "Are you sure you want to delete this menu? This action cannot be un-done",
                    UIMessageType.AreYouSureDialog(callback)
                )
            )
        }
    }

    private fun initRecyclerView() {
        rvMenuItemList.apply {
            layoutManager = GridLayoutManager(context, 3,GridLayoutManager.VERTICAL, false)
            adapter = menuListAdapter
        }
    }

    private fun subscribeObservers() {
        viewModel.menuWithMenuItemsLiveData.observe(viewLifecycleOwner, { menuWithMenuItems ->
            menuWithMenuItems?.let {

                tvMenuName.text = it.menu.name.capitalize(Locale.ROOT)

                tvMenuType.text = it.menu.type.capitalize(Locale.ROOT)

                it.menu.image?.let { imageURI ->
                    imgMenu.loadImage(
                        Uri.parse(imageURI),
                        getProgressDrawable(requireContext())
                    )
                }

                menuListAdapter.updateMenuItemsList(it.menuItems,it.menuItemsPrices)
            }
        })
    }

}