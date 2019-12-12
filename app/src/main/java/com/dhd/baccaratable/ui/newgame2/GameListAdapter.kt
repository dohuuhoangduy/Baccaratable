package com.dhd.baccaratable.ui.newgame2

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dhd.baccaratable.R
import android.content.Intent

class GameListAdapter internal constructor(
        context: Context, val sendCoupString : (String) -> Unit
) : RecyclerView.Adapter<GameListAdapter.GameViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var games = emptyList<SavedGame>() // Cached copy of games

    inner class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gameTitle: TextView = itemView.findViewById(R.id.gameTitle)
        val gameCreated: TextView = itemView.findViewById(R.id.gameCreated)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val itemView = inflater.inflate(R.layout.savedgame_item, parent, false)
        return GameViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val current = games[position]
        holder.gameTitle.text = current.title
        holder.gameCreated.text = current.created
        holder.gameTitle.setOnClickListener {v ->
            sendCoupString(current.coupString)
        }
    }

    internal fun setGames(games: List<SavedGame>) {
        this.games = games
        notifyDataSetChanged()
    }

    override fun getItemCount() = games.size
}