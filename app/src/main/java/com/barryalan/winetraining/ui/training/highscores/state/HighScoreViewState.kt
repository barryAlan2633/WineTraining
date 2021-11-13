package com.barryalan.winetraining.ui.training.highscores.state

import com.barryalan.winetraining.model.menu.Score

class HighScoreViewState(
    var scores: List<Score>? = null,
    var gameType: Int = -1
)