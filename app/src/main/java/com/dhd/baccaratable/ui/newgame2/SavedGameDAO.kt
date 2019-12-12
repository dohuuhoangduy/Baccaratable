package com.dhd.baccaratable.ui.newgame2

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SavedGameDAO {

    @Query("SELECT title, coupString, created from saved_game")
    fun getGame(): LiveData<List<SavedGame>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(game: SavedGame)

    @Query("DELETE FROM saved_game")
    suspend fun deleteAll()
}