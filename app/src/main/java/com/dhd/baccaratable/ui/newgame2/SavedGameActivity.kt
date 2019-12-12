package com.dhd.baccaratable.ui.newgame2

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhd.baccaratable.R

class SavedGameActivity : AppCompatActivity() {

    private lateinit var savedGameViewModel: SavedGameViewModel


    companion object {
        const val EXTRA_REPLY: String = "com.dhd.baccaratable.REPLY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_savedgame)

        val recyclerView = findViewById<RecyclerView>(R.id.savedGameView)
        val adapter = GameListAdapter(this, {coupString:String -> sendCoupString(coupString)})
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        savedGameViewModel = ViewModelProvider(this).get(SavedGameViewModel::class.java)
        savedGameViewModel.allGames.observe(this, Observer { games ->
            // Update the cached copy of the words in the adapter.
            games?.let { adapter.setGames(it) }
        })
        recyclerView.invalidate()
        //savedGameViewModel.insert(SavedGame("aaa", "bbb","ccc"))
        //savedGameViewModel.insert(SavedGame("D", "e","f"))

    }

    fun sendCoupString(coupString: String) {
        val replyIntent = Intent()
        replyIntent.putExtra(EXTRA_REPLY, coupString)
        setResult(Activity.RESULT_OK, replyIntent)
        //Toast.makeText(applicationContext, coupString, Toast.LENGTH_SHORT).show()
        finish()
    }
}
