package com.dhd.baccaratable.ui.newgame2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

//data class SavedGame(val title:String) {
    @Entity(tableName = "saved_game")
    class SavedGame(
    @PrimaryKey @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "coupString") val coupString: String,
    @ColumnInfo(name = "created") val created: String
    )
//}