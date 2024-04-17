package com.example.examination.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.examination.R
import NoteList

class NoteAdapter(var notes: List<NoteList>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onEditClick(position: Int)
        fun onDeleteClick(position: Int)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteTitleTextView: TextView = itemView.findViewById(R.id.NoteTitleTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.DateTextView)
        val noteTextView: TextView = itemView.findViewById(R.id.NoteTextView)
        val editButton: Button = itemView.findViewById(R.id.editButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)

        init {
            editButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onEditClick(position)
                }
            }

            deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notes[position]
        holder.noteTitleTextView.text = note.title
        holder.dateTextView.text = note.date.toString()
        holder.noteTextView.text = note.note
    }

    override fun getItemCount(): Int {
        return notes.size
    }
}
