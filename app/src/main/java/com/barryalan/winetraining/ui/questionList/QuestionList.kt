package com.barryalan.winetraining.ui.questionList

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
import com.barryalan.winetraining.model.Question
import com.barryalan.winetraining.ui.shared.OnItemClickListener
import com.google.android.material.floatingactionbutton.FloatingActionButton


class QuestionList : Fragment(), OnItemClickListener {

    private lateinit var viewModel: QuestionListViewModel
    private lateinit var questionAdapter: QuestionAdapter
    private lateinit var rvQuestions: RecyclerView

    private  var isNewItem = false
    private var editItemId = 0

    private lateinit var actionButtonNew: FloatingActionButton
    private lateinit var actionButtonCancel: FloatingActionButton
    private lateinit var actionButtonSave: FloatingActionButton
    private lateinit var tvQuestion: TextView
    private lateinit var tvType: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_question_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        rvQuestions = requireView().findViewById(R.id.rv_question)

        actionButtonNew = requireView().findViewById(R.id.action_button_new_question)
        actionButtonCancel = requireView().findViewById(R.id.action_button_cancel_question)
        actionButtonNew = requireView().findViewById(R.id.action_button_new_question)
        actionButtonSave = requireView().findViewById(R.id.action_button_save_question)
        tvQuestion = requireView().findViewById(R.id.tv_question_edit)
        tvType = requireView().findViewById(R.id.tv_type_edit)

        viewModel = ViewModelProvider(this).get(QuestionListViewModel::class.java)
        viewModel.getQuestions()
//        viewModel.initQuestions()

        initRecyclerView()
        initActionButtons()
        subscribeObservers()
        requireView().findViewById<FloatingActionButton>(R.id.action_button_prePopulateQuestions).setOnClickListener {
            viewModel.prePopulateQuestions()
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
                questionAdapter.filter.filter(newText)
                return false
            }

        })
    }

    private fun initRecyclerView() {
        questionAdapter = QuestionAdapter(mutableListOf(), this)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        rvQuestions.layoutManager = linearLayoutManager
        rvQuestions.setHasFixedSize(true)
        rvQuestions.adapter = questionAdapter


        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewModel.deleteQuestion(questionAdapter.questionList[viewHolder.adapterPosition])
            }
        }).attachToRecyclerView(rvQuestions)
    }


    private fun initActionButtons() {
        actionButtonNew.setOnClickListener {
            isNewItem = true
            openNewItemInterface()
            actionButtonNew.visibility = View.GONE
            tvQuestion.requestFocus()

            val inputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            inputMethodManager.toggleSoftInputFromWindow(
                tvQuestion.applicationWindowToken,
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
        rvQuestions.alpha = 1f
        rvQuestions.isClickable = true
        actionButtonNew.visibility = View.VISIBLE

        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inputMethodManager.toggleSoftInputFromWindow(
            actionButtonNew.applicationWindowToken,
            InputMethodManager.HIDE_NOT_ALWAYS, 0
        )
        tvQuestion.text = ""
        tvType.text = ""
    }


    private fun openNewItemInterface() {
        val cardViewLayout = requireView().findViewById<CardView>(R.id.cardView_layout)
        cardViewLayout.visibility = View.VISIBLE
        rvQuestions.alpha = 0.5.toFloat()
        rvQuestions.isClickable = false
    }

    private fun saveInfoToDatabase(): Boolean {
        return if (tvQuestion.text.toString() == "" || tvType.text.toString() == "") {
            false
        } else {
            val newQuestion = Question(
                tvType.text.toString(),
                tvQuestion.text.toString()
            )
            if (!isNewItem) {
                newQuestion.id = editItemId
            }
            viewModel.saveQuestion(newQuestion)
            true
        }
    }

    private fun setOldItemInfo(question: Question) {
        editItemId = question.id
        tvQuestion.text = question.question
        tvType.text = question.type
    }

    override fun onItemClick(position: Int) {
        if (rvQuestions.isClickable) {
            isNewItem = false
            openNewItemInterface()
            setOldItemInfo(questionAdapter.questionList[position])
        }
    }

    private fun subscribeObservers() {
        viewModel.questionsLiveData.observe(viewLifecycleOwner, {
            questionAdapter.updateListAdd(it)
        })
    }
}