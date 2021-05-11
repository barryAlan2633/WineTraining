package com.barryalan.winetraining.ui.highscores

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.barryalan.winetraining.R
import com.barryalan.winetraining.model.Score
import com.barryalan.winetraining.ui.shared.Util


class HighScores : Fragment() {

    private lateinit var rvScores: RecyclerView
    private lateinit var viewModel: HighScoresViewModel
    private lateinit var scoresAdapter: HighScoreAdapter2

    private lateinit var rgGameType: RadioGroup
    private lateinit var rbCategories: RadioButton
    private lateinit var rbPrices: RadioButton
    private lateinit var rbAll: RadioButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_high_scores, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvScores = requireView().findViewById(R.id.rv_scores)
        rgGameType = requireView().findViewById(R.id.rg_game_type)
        rbCategories = requireView().findViewById(R.id.rb_categories_high_scores)
        rbPrices = requireView().findViewById(R.id.rb_prices_high_scores)
        rbAll = requireView().findViewById(R.id.rb_all_high_scores)


        viewModel = ViewModelProvider(this).get(HighScoresViewModel::class.java)
        viewModel.getScores()

        initScoresRecycler()
        subscribeObservers()


        rgGameType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_categories_high_scores -> {
                    viewModel.gameTypeLiveData.value = Util.CATEGORIES
                }
                R.id.rb_prices_high_scores -> {
                    viewModel.gameTypeLiveData.value = Util.PRICES
                }
                R.id.rb_all_high_scores -> {
                    viewModel.gameTypeLiveData.value = Util.ALL
                }
            }
        }
    }

    private fun initScoresRecycler() {
        scoresAdapter = HighScoreAdapter2(mutableListOf())
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.reverseLayout = false
        linearLayoutManager.stackFromEnd = false
        rvScores.layoutManager = linearLayoutManager
        rvScores.setHasFixedSize(true)
        rvScores.adapter = scoresAdapter
    }

    private fun subscribeObservers() {

        viewModel.gameTypeLiveData.observe(viewLifecycleOwner, { currentGameType ->
            if (currentGameType != -1) {

                if (viewModel.scoresLiveData.value != null) {
                    val scoresToDisplay = mutableListOf<Score>()

                    viewModel.scoresLiveData.value!!.map { score ->
                        if (score.type == currentGameType) {
                            scoresToDisplay.add(score)
                        }
                    }
                    scoresAdapter.updateList(scoresToDisplay)
                }

            }
        })

        viewModel.scoresLiveData.observe(viewLifecycleOwner, { scores ->

            val scoresToDisplay = mutableListOf<Score>()
            scores.map {
                when (viewModel.gameTypeLiveData.value) {
                    Util.CATEGORIES -> {
                        if (it.type == Util.CATEGORIES) {
                            scoresToDisplay.add(it)
                        }
                    }
                    Util.ALL -> {
                        if (it.type == Util.ALL) {
                            scoresToDisplay.add(it)
                        }
                    }
                    Util.PRICES -> {
                        if (it.type == Util.PRICES) {
                            scoresToDisplay.add(it)
                        }
                    }

                }
            }


            scoresAdapter.updateList(scoresToDisplay)


        })
    }
}
