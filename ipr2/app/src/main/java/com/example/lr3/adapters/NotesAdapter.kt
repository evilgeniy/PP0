package com.example.lr3.adapters

import android.content.Context
import android.view.*
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lr3.R
import com.example.lr3.data.Note
import com.example.lr3.viewModels.NotesViewModel

class NotesAdapter(
    val context: Context,
    private val notesViewModel: NotesViewModel,
    private val clickListener: (position: Int) -> Unit
): RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private var notes: MutableList<Note> = mutableListOf()

    override fun getItemCount(): Int = notes.size

    private fun getNote(position: Int) =  notes[position]


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getNote(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            inflater.inflate(R.layout.note_row, parent, false),
            clickListener
        ) { position ->
            deleteItem(position)
        }
    }

    fun deleteItem(position: Int) {
        notesViewModel.deleteNote(notes[position])
        notes.removeAt(position)
        notifyItemRemoved(position)
    }

    fun setNotes(notes: MutableList<Note>) {
        this.notes = notes
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View, listener: (position: Int) -> Unit,
                     delete_callback: (position: Int) -> Unit):
        RecyclerView.ViewHolder(itemView) {
        private val header: TextView = itemView.findViewById(R.id.note_header_preview)
        private val content: TextView = itemView.findViewById(R.id.note_сontent_preview)

        init {
            val position = adapterPosition
            itemView.setOnClickListener {
                val position = adapterPosition
                listener(position)
            }
            val deleteButton: ImageButton? = itemView.findViewById(R.id.delete_landscape)
                deleteButton?.setOnClickListener {
                    val position = adapterPosition
                    delete_callback(position)
                }
        }

        fun bind(note: Note) {
            header.text = note.title
            content.text = note.content
        }

    }
}