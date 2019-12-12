package com.dhd.baccaratable.ui.newgame2

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.chrono.HijrahChronology
import java.time.chrono.HijrahChronology.INSTANCE


// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = arrayOf(SavedGame::class), version = 1)
public abstract class SavedGameDatabase : RoomDatabase() {

    abstract fun savedGameDao(): SavedGameDAO

   /* private class GameDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.savedGameDao())
                }
            }
        }

        suspend fun populateDatabase(savedGameDao: SavedGameDAO) {
            // Delete all content here.
            savedGameDao.deleteAll()

            // Add sample words.
            var game = SavedGame("Hello", "0,1","10/23/2019")
            savedGameDao.insert(game)
            game = SavedGame("World!","0,1","10/23/2019")
            savedGameDao.insert(game)

            // TODO: Add your own words!
        }
    }*/

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: SavedGameDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SavedGameDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SavedGameDatabase::class.java,
                    "saved_game"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}

