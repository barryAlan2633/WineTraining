package com.barryalan.winetraining.ui.play

import android.content.Context
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.barryalan.winetraining.R
import com.barryalan.winetraining.ui.customViews.TimerView
import com.barryalan.winetraining.ui.shared.Util
import java.util.concurrent.TimeUnit


class Play : Fragment() {

    lateinit var viewModel: PlayViewModel

    private lateinit var btnCategories: Button
    private lateinit var btnPrices: Button
    private lateinit var btnAll: Button
    private lateinit var btnAnswer1: Button
    private lateinit var btnAnswer2: Button
    private lateinit var btnAnswer3: Button
    private lateinit var tvTimer: TimerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_play, container, false)

    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnCategories = requireView().findViewById(R.id.btn_play_categories)
        btnPrices = requireView().findViewById(R.id.btn_play_prices)
        btnAll = requireView().findViewById(R.id.btn_play_all)
        btnAnswer1 = requireView().findViewById(R.id.btn_play_answer1)
        btnAnswer2 = requireView().findViewById(R.id.btn_play_answer2)
        btnAnswer3 = requireView().findViewById(R.id.btn_play_answer3)
        tvTimer = requireView().findViewById(R.id.tv_timer)

        viewModel = ViewModelProvider(this).get(PlayViewModel::class.java)

        viewModel.getQuestions()
        viewModel.getWines()
        initTimer()
        subscribeObservers()

        //If the rotation was changed and the play activity was re-started
        if (viewModel.userState == Util.PLAYING) {
            setGameLayout()
            setQuestionLayout()
            setAnswersLayout()
            setScoreLayout()
            setButtonBackgrounds()
            setLives(viewModel.livesLeft)
            if (viewModel.leavingDialogShow) {
                exitDialog()
            }
            if (viewModel.scoreDialogShow) {
                scoreAlertDialog()
            }
        }

        btnCategories.setOnClickListener {
            if (viewModel.winesLiveData.value?.isNotEmpty() == true && viewModel.questionsLiveData.value?.isNotEmpty() == true) {
                viewModel.userState = Util.PLAYING
                viewModel.chosenType = Util.CATEGORIES
                viewModel.generateQuestion()
                viewModel.generateAnswerAndChoices()
                setGameLayout()
                setQuestionLayout()
                setAnswersLayout()
                setLives(viewModel.livesLeft)
                viewModel.setCountDownTimer()
            } else {
                Toast.makeText(
                    requireContext(),
                    "You need to have wines and questions before playing!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btnPrices.setOnClickListener {
            if (viewModel.winesLiveData.value?.isNotEmpty() == true && viewModel.questionsLiveData.value?.isNotEmpty() == true) {
                viewModel.userState = Util.PLAYING
                viewModel.chosenType = Util.PRICES
                viewModel.generateQuestion()
                viewModel.generateAnswerAndChoices()
                setGameLayout()
                setQuestionLayout()
                setAnswersLayout()
                setLives(viewModel.livesLeft)
                viewModel.setCountDownTimer()
            } else {
                Toast.makeText(
                    requireContext(),
                    "You need to have wines and questions before playing!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btnAll.setOnClickListener {
            if (viewModel.winesLiveData.value?.isNotEmpty() == true && viewModel.questionsLiveData.value?.isNotEmpty() == true) {
                viewModel.userState = Util.PLAYING
                viewModel.chosenType = Util.ALL
                viewModel.generateQuestion()
                viewModel.generateAnswerAndChoices()
                setGameLayout()
                setQuestionLayout()
                setAnswersLayout()
                setLives(viewModel.livesLeft)
                viewModel.setCountDownTimer()
            } else {
                Toast.makeText(
                    requireContext(),
                    "You need to have wines and questions before playing!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btnAnswer1.setOnClickListener {
            if (viewModel.answerBtnClickable) {
                viewModel.cancelCountDownTimer()
                if (viewModel.checkAnswer(btnAnswer1.text.toString())) {
                    viewModel.answer1Bkg = Util.GREEN
                } else {
                    setLives(viewModel.livesLeft - 1)
                    viewModel.answer1Bkg = Util.RED
                }
                setButtonBackgrounds()
                setScoreLayout()
                viewModel.answerBtnClickable = false
                val h = Handler()
                h.postDelayed({ showCorrectAnswer() }, 1000)
                if (viewModel.livesLeft > 0) {
                    h.postDelayed({
                        resetAnswerButtons()
                        viewModel.generateQuestion()
                        viewModel.generateAnswerAndChoices()
                        setQuestionLayout()
                        setAnswersLayout()
                        viewModel.setCountDownTimer()
                    }, 2000)
                } else {
                    viewModel.scoreDialogShow = true
                    scoreAlertDialog()
                }
            }
        }

        btnAnswer2.setOnClickListener {
            if (viewModel.answerBtnClickable) {
                viewModel.cancelCountDownTimer()
                if (viewModel.checkAnswer(btnAnswer2.text.toString())) {
                    viewModel.answer2Bkg = Util.GREEN
                } else {
                    setLives(viewModel.livesLeft - 1)
                    viewModel.answer2Bkg = Util.RED
                }
                setButtonBackgrounds()
                setScoreLayout()
                viewModel.answerBtnClickable = false
                val h = Handler()
                h.postDelayed({ showCorrectAnswer() }, 1000)
                if (viewModel.livesLeft > 0) {
                    h.postDelayed({
                        resetAnswerButtons()
                        viewModel.generateQuestion()
                        viewModel.generateAnswerAndChoices()
                        setQuestionLayout()
                        setAnswersLayout()
                        viewModel.setCountDownTimer()
                    }, 2000)
                } else {
                    viewModel.scoreDialogShow = true
                    scoreAlertDialog()
                }
            }
        }

        btnAnswer3.setOnClickListener {
            if (viewModel.answerBtnClickable) {
                viewModel.cancelCountDownTimer()
                if (viewModel.checkAnswer(btnAnswer3.text.toString())) {
                    viewModel.answer3Bkg = Util.GREEN
                } else {
                    setLives(viewModel.livesLeft - 1)
                    viewModel.answer3Bkg = Util.RED
                }
                setButtonBackgrounds()
                setScoreLayout()
                viewModel.answerBtnClickable = false
                val h = Handler()
                h.postDelayed({ showCorrectAnswer() }, 1000)
                if (viewModel.livesLeft > 0) {
                    h.postDelayed({
                        resetAnswerButtons()
                        viewModel.generateQuestion()
                        viewModel.generateAnswerAndChoices()
                        setQuestionLayout()
                        setAnswersLayout()
                        viewModel.setCountDownTimer()
                    }, 2000)
                } else {
                    viewModel.scoreDialogShow = true
                    scoreAlertDialog()
                }
            }
        }


    }

    private fun initTimer() {
        when (requireActivity().resources.configuration.orientation) {
            ORIENTATION_PORTRAIT -> {
                tvTimer.setClockRadius(200f)
            }
            ORIENTATION_LANDSCAPE -> {
                tvTimer.setClockRadius(150f)
            }
        }
    }

    private fun setGameLayout() {
        val categoriesLayout = requireView().findViewById<ConstraintLayout>(R.id.layout_categories)
        val playLayout = requireView().findViewById<ConstraintLayout>(R.id.layout_play)
        val tvMode = requireView().findViewById<TextView>(R.id.tv_mode)
        when (viewModel.chosenType) {
            1 -> tvMode.text = getString(R.string.Names)
            2 -> tvMode.text = getString(R.string.Categories)
            3 -> tvMode.text = getString(R.string.Prices)
            4 -> tvMode.text = getString(R.string.All)
            else -> tvMode.text = getString(R.string.Error)
        }
        categoriesLayout.visibility = View.GONE
        playLayout.visibility = View.VISIBLE
    }

    private fun setQuestionLayout() {
        if (viewModel.selectedWinesForThisTurn.isNotEmpty()) {
            val currentQuestion = viewModel.selectedQuestionForThisTurn?.question

            val one = currentQuestion?.substring(0, currentQuestion.indexOf("#"))
            val two = currentQuestion?.substring(currentQuestion.indexOf("!") + 1)


            val three =
                when {
                    currentQuestion?.contains("#name!") == true -> {
                        one + viewModel.answerWineForThisTurn?.name.toString() + two
                    }
                    currentQuestion?.contains("#glass!") == true -> {
                        one + viewModel.answerWineForThisTurn?.glassPrice.toString() + two
                    }
                    currentQuestion?.contains("#category!") == true -> {
                        one + viewModel.answerWineForThisTurn?.category.toString() + two
                    }
                    currentQuestion?.contains("#bottle!") == true -> {
                        one + viewModel.answerWineForThisTurn?.bottlePrice.toString() + two
                    }
                    else -> {
                        one + two
                    }
                }

            val tvQuestion = requireView().findViewById<TextView>(R.id.tv_play_question)
            tvQuestion.text = three
        }
    }

    private fun setAnswersLayout() {
        val btnAnswer1 = requireView().findViewById<Button>(R.id.btn_play_answer1)
        val btnAnswer2 = requireView().findViewById<Button>(R.id.btn_play_answer2)
        val btnAnswer3 = requireView().findViewById<Button>(R.id.btn_play_answer3)
        when (viewModel.selectedQuestionForThisTurn?.type) {
            "category" -> {
                btnAnswer1.text = (viewModel.selectedWinesForThisTurn[0].category)
                btnAnswer2.text = (viewModel.selectedWinesForThisTurn[1].category)
                btnAnswer3.text = (viewModel.selectedWinesForThisTurn[2].category)
            }
            "glass" -> {

                if (viewModel.selectedWinesForThisTurn[0].glassPrice == 0f) {
                    btnAnswer1.text = ("Not Sold")
                } else {
                    btnAnswer1.text = (viewModel.selectedWinesForThisTurn[0].glassPrice.toString())
                }
                if (viewModel.selectedWinesForThisTurn[1].glassPrice == 0f) {
                    btnAnswer2.text = ("Not Sold")
                } else {
                    btnAnswer2.text = (viewModel.selectedWinesForThisTurn[1].glassPrice.toString())
                }
                if (viewModel.selectedWinesForThisTurn[2].glassPrice == 0f) {
                    btnAnswer3.text = ("Not Sold")
                } else {
                    btnAnswer3.text = (viewModel.selectedWinesForThisTurn[2].glassPrice.toString())
                }
            }
            "bottle" -> {
                if (viewModel.selectedWinesForThisTurn[0].bottlePrice == 0f) {
                    btnAnswer1.text = ("Not Sold")
                } else {
                    btnAnswer1.text = (viewModel.selectedWinesForThisTurn[0].bottlePrice.toString())
                }
                if (viewModel.selectedWinesForThisTurn[1].bottlePrice == 0f) {
                    btnAnswer2.text = ("Not Sold")
                } else {
                    btnAnswer2.text = (viewModel.selectedWinesForThisTurn[1].bottlePrice.toString())
                }
                if (viewModel.selectedWinesForThisTurn[2].bottlePrice == 0f) {
                    btnAnswer3.text = ("Not Sold")
                } else {
                    btnAnswer3.text = (viewModel.selectedWinesForThisTurn[2].bottlePrice.toString())
                }
            }
            else -> throw IllegalStateException(
                "Unexpected value: " + viewModel.selectedQuestionForThisTurn?.type
            )
        }
    }

    private fun setButtonBackgrounds() {
        if (viewModel.answer1Bkg == Util.GREEN) {
            btnAnswer1.setBackgroundResource(R.drawable.background_button_navigation_green)
        } else if (viewModel.answer1Bkg == Util.RED) {
            btnAnswer1.setBackgroundResource(R.drawable.background_button_navigation_red)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //>= API 21
                btnAnswer1.foreground =
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_wrong_black_24dp, null)
            } else {
                btnAnswer1.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_wrong_black_24dp, null)
            }
        }
        if (viewModel.answer2Bkg == Util.GREEN) {
            btnAnswer2.setBackgroundResource(R.drawable.background_button_navigation_green)
        } else if (viewModel.answer2Bkg == Util.RED) {
            btnAnswer2.setBackgroundResource(R.drawable.background_button_navigation_red)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //>= API 21
                btnAnswer2.foreground =
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_wrong_black_24dp, null)
            } else {
                btnAnswer2.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_wrong_black_24dp, null)
            }
        }
        if (viewModel.answer3Bkg == Util.GREEN) {
            btnAnswer3.setBackgroundResource(R.drawable.background_button_navigation_green)
        } else if (viewModel.answer3Bkg == Util.RED) {
            btnAnswer3.setBackgroundResource(R.drawable.background_button_navigation_red)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //>= API 21
                btnAnswer3.foreground =
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_wrong_black_24dp, null)
            } else {
                btnAnswer3.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_wrong_black_24dp, null)
            }
        }
    }

    private fun setScoreLayout() {
        val tvScore = requireView().findViewById<TextView>(R.id.tv_play_score)
        tvScore.text = viewModel.currentScore.toString()
    }

    private fun showCorrectAnswer() {
        when (viewModel.answerIndex) {
            0 -> {
                requireView().findViewById<View>(R.id.btn_play_answer1)
                    .setBackgroundResource(R.drawable.background_button_navigation_green)
                viewModel.answer1Bkg = Util.GREEN
            }
            1 -> {
                requireView().findViewById<View>(R.id.btn_play_answer2)
                    .setBackgroundResource(R.drawable.background_button_navigation_green)
                viewModel.answer2Bkg = Util.GREEN
            }
            2 -> {
                requireView().findViewById<View>(R.id.btn_play_answer3)
                    .setBackgroundResource(R.drawable.background_button_navigation_green)
                viewModel.answer3Bkg = Util.GREEN
            }
        }
    }

    private fun resetAnswerButtons() {
        btnAnswer1.setBackgroundResource(R.drawable.background_button_navigation)
        btnAnswer2.setBackgroundResource(R.drawable.background_button_navigation)
        btnAnswer3.setBackgroundResource(R.drawable.background_button_navigation)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //>= API 21
            btnAnswer1.foreground =
                ResourcesCompat.getDrawable(resources, R.drawable.ic_clear_foreground_24dp, null)
            btnAnswer2.foreground =
                ResourcesCompat.getDrawable(resources, R.drawable.ic_clear_foreground_24dp, null)
            btnAnswer3.foreground =
                ResourcesCompat.getDrawable(resources, R.drawable.ic_clear_foreground_24dp, null)
        } else {
            btnAnswer1.background =
                ResourcesCompat.getDrawable(resources, R.drawable.ic_clear_foreground_24dp, null)
            btnAnswer2.background =
                ResourcesCompat.getDrawable(resources, R.drawable.ic_clear_foreground_24dp, null)
            btnAnswer3.background =
                ResourcesCompat.getDrawable(resources, R.drawable.ic_clear_foreground_24dp, null)

        }
        viewModel.answerBtnClickable = true
        viewModel.answer1Bkg = Util.WHITE
        viewModel.answer2Bkg = Util.WHITE
        viewModel.answer3Bkg = Util.WHITE
    }

    private fun setLives(livesLeft: Int) {
        viewModel.livesLeft = livesLeft
        val tvLives = requireView().findViewById<TextView>(R.id.tv_play_lives)
        tvLives.text = viewModel.livesLeft.toString()
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
        tvQuestion!!.text = resources.getString(R.string.PlayActivity_tv_ending_game)
        btnYes!!.setOnClickListener {
            viewModel.userState = Util.NOT_PLAYING
            viewModel.leavingDialogShow = false
            alertDialog.dismiss()
            setLives(3)
            requireView().findNavController().navigate(R.id.action_play_to_mainMenu)
        }
        btnCancel!!.setOnClickListener {
            alertDialog.dismiss()
            viewModel.leavingDialogShow = false
        }
    }

    private fun scoreAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
        val dialogView: View = layoutInflater.inflate(R.layout.dialog_game_over, null)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.show()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val tvScore = dialogView.findViewById<TextView>(R.id.tv_score)
        val etName = dialogView.findViewById<EditText>(R.id.et_name)
        tvScore.text = viewModel.currentScore.toString()
        val btnRetry = dialogView.findViewById<Button>(R.id.btn_retry)
        val btnHome = dialogView.findViewById<Button>(R.id.btn_home)
        btnRetry.setOnClickListener {
            if (etName.text.toString() == "") {
                Toast.makeText(
                    requireContext(),
                    "You need to write your name",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                viewModel.saveScore(
                    etName.text.toString(),
                    tvScore.text.toString().substring(0, 1).toInt()
                )

                viewModel.scoreDialogShow = false
                alertDialog.dismiss()
                setLives(3)
                requireView().findNavController().navigate(R.id.action_play_self)

            }
        }

        btnHome.setOnClickListener {
            if (etName.text.toString() == "") {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.PlayActivity_toast_you_need_to_write_your_name),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.saveScore(
                    etName.text.toString(),
                    tvScore.text.toString().substring(0, 1).toInt()
                )


                viewModel.scoreDialogShow = false
                alertDialog.dismiss()
                setLives(3)
                requireView().findNavController().navigate(R.id.action_play_to_mainMenu)
            }
        }
        tvScore.animation = AnimationUtils.loadAnimation(requireContext(), R.anim.anim_tv_score)
    }


    private fun subscribeObservers() {

        viewModel.time.observe(viewLifecycleOwner, { millisUntilFinished ->
            if (millisUntilFinished == 0L) {
                tvTimer.setClockColor(ContextCompat.getColor(requireContext(), R.color.red))

                viewModel.livesLeft -= 1

                viewModel.answerBtnClickable = false
                val h = Handler()
                h.postDelayed({ showCorrectAnswer() }, 1000)
                if (viewModel.livesLeft > 0) {
                    h.postDelayed({
                        viewModel.generateQuestion()
                        viewModel.generateAnswerAndChoices()
                        setGameLayout()
                        setQuestionLayout()
                        setAnswersLayout()
                        viewModel.setCountDownTimer()
                        resetAnswerButtons()
                    }, 2000)
                } else {
                    scoreAlertDialog()
                }
            } else if (millisUntilFinished < 2500) {
                tvTimer.setClockColor(ContextCompat.getColor(requireContext(), R.color.orange))
            } else if (millisUntilFinished < 5000) {
                tvTimer.setClockColor(ContextCompat.getColor(requireContext(), R.color.yellow))
            } else {
                tvTimer.setClockColor(ContextCompat.getColor(requireContext(), R.color.green))
            }
            val hms = String.format(
                "%02d.%02d",
                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished),
                millisUntilFinished % TimeUnit.SECONDS.toMillis(1) / 10
            )
            tvTimer.setClockTime(hms)

        })


    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                viewModel.leavingDialogShow = true
                exitDialog()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }


}