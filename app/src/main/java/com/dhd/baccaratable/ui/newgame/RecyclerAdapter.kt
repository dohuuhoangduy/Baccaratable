package com.dhd.baccaratable.ui.newgame

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.dhd.baccaratable.R
import kotlinx.android.synthetic.main.item_file.view.*

class RecyclerAdapter(val files:ArrayList<String>, val context:Context, val updateFileName: (String) -> Unit) : RecyclerView.Adapter<RecyclerAdapter.FileHolder>()  {

    override fun getItemCount() = files.size

    override fun onBindViewHolder(holder: RecyclerAdapter.FileHolder, position: Int) {
        holder.fileName?.text = files.get(position)
        holder.itemView.setOnClickListener {
            updateFileName(files.get(position))
            Toast.makeText(context, "${files.get(position)}", Toast.LENGTH_SHORT).show()
        }
        holder.itemView.setOnCreateContextMenuListener { contextMenu, _, _ ->
            contextMenu.add("Delete").setOnMenuItemClickListener {
                Toast.makeText(context, "I'm pressed for the item at position => $position",Toast.LENGTH_SHORT).show()
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.FileHolder {
        return FileHolder(LayoutInflater.from(context).inflate(R.layout.item_file, parent, false))
    }
    class FileHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val fileName = view.file_name
    }
}

