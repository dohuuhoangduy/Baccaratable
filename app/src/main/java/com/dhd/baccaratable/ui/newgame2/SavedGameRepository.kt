package com.dhd.baccaratable.ui.newgame2

import androidx.lifecycle.LiveData

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class SavedGameRepository(private val gameDAO: SavedGameDAO) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allGames: LiveData<List<SavedGame>> = gameDAO.getGame()

    suspend fun insert(game: SavedGame) {
        gameDAO.insert(game)
    }
}