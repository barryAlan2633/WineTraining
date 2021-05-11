package com.barryalan.winetraining.ui.highscores.state

import com.barryalan.winetraining.model.Score

class HighScoreViewState(
    var scores: List<Score>? = null,
    var gameType: Int = -1
)