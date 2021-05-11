package com.barryalan.winetraining.ui.highscores.state


sealed class HighScoreStateEvent {

    class GetScoresEvent(filter: Int) : HighScoreStateEvent()

    class None() : HighScoreStateEvent()
}