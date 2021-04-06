package com.barryalan.winetraining.shared

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.barryalan.winetraining.R


class MainMenu : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_menu, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnPlay = requireView().findViewById<Button>(R.id.btn_play)
        val btnHighScores = requireView().findViewById<Button>(R.id.btn_high_scores)
        val btnWineList = requireView().findViewById<Button>(R.id.btn_wine)
        val btnQuestionList = requireView().findViewById<Button>(R.id.btn_questions)


        btnPlay.setOnClickListener {
            view.findNavController().navigate(R.id.action_mainMenu_to_play)
        }

        btnHighScores.setOnClickListener {
            view.findNavController().navigate(R.id.action_mainMenu_to_highScores)
        }

        btnWineList.setOnClickListener {
            view.findNavController().navigate(R.id.action_mainMenu_to_wineList)
        }

        btnQuestionList.setOnClickListener {
            view.findNavController().navigate(R.id.action_mainMenu_to_questionList)
        }

        btnPlay.animation = AnimationUtils.loadAnimation(requireContext(), R.anim.anim_btn_play)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                exitDialog()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    //Dialogs
    private fun exitDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
        val dialogView: View = layoutInflater.inflate(R.layout.dialog_are_you_sure, null)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.show()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val tvQuestion = alertDialog.findViewById<TextView>(R.id.tv_are_you_sure_dialog_question)
        val btnYes = alertDialog.findViewById<Button>(R.id.btn_yes)
        val btnCancel = alertDialog.findViewById<Button>(R.id.btn_cancel)
        tvQuestion!!.text = resources.getString(R.string.MainActivity_tv_closing_app)

        btnYes!!.setOnClickListener {

            alertDialog.dismiss()
            requireActivity().finish()

        }
        btnCancel!!.setOnClickListener {
            alertDialog.dismiss()
        }
    }
}