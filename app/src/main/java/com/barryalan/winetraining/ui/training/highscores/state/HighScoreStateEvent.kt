package com.barryalan.winetraining.ui.training.highscores.state


sealed class HighScoreStateEvent {

    class GetScoresEvent(filter: Int) : HighScoreStateEvent()

    class None() : HighScoreStateEvent()
}