package com.barryalan.winetraining.ui.training

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.navigation.findNavController
import com.barryalan.winetraining.R
import com.barryalan.winetraining.ui.shared.BaseFragment


class TrainingMainMenu : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_training_main_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnHighScores = requireView().findViewById<Button>(R.id.btn_high_scores_training_main_menu)
        val btnQuestions = requireView().findViewById<Button>(R.id.btn_questions_training_main_menu)
        val btnPlay = requireView().findViewById<Button>(R.id.btn_play_training_main_menu)

        btnHighScores.setOnClickListener {
            requireView().findNavController()
                .navigate(R.id.action_trainingMainMenu_to_highScores)
        }

        btnQuestions.setOnClickListener {
            requireView().findNavController().navigate(R.id.action_trainingMainMenu_to_questionList)
        }

        btnPlay.setOnClickListener {
            requireView().findNavController().navigate(R.id.action_trainingMainMenu_to_playMenu)
        }

        btnPlay.animation = AnimationUtils.loadAnimation(requireContext(), R.anim.anim_btn_play)

    }


}