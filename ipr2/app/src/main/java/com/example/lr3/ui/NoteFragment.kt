package com.example.lr3.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.example.lr3.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View.GONE
import android.widget.HorizontalScrollView
import com.example.lr3.data.Note
import com.example.lr3.viewModels.NotesViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton


class NoteFragment : Fragment() {

    private lateinit var headerTextView: TextView
    private lateinit var tagsChipGroup: ChipGroup
    private lateinit var contentTextView: TextView
    private lateinit var editNoteButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_note, container, false)
        headerTextView = view.findViewById(R.id.note_header)
        headerTextView.movementMethod = ScrollingMovementMethod()
        tagsChipGroup = view.findViewById(R.id.tags_chip_group)
        contentTextView = view.findViewById(R.id.note_content)
        contentTextView.movementMethod = ScrollingMovementMethod()
        editNoteButton = view.findViewById(R.id.edit_note_button)


        val tags = arguments?.getStringArrayList(ARG_TAGS)?.toList()!!
        val note = arguments?.getParcelable<Note>(ARG_NOTE)
        note.run {
            headerTextView.text = this?.title ?: ""
            contentTextView.text = this?.content ?: ""

            Log.d("NoteFragment", "tags from viewModel: $tags")

            if (tags.isNotEmpty()) {
                for (tag in tags) {
                    val chip = Chip(tagsChipGroup.context).apply {
                        text = tag
                        setChipBackgroundColorResource(R.color.colorCardBackground)
                        textSize = resources.getDimension(R.dimen.tag_text_size)
                    }
                    tagsChipGroup.addView(chip)
                }
            } else {
                view.findViewById<HorizontalScrollView>(R.id.tags_scroll).visibility = GONE
            }
        }

        editNoteButton.setOnClickListener {
            redirectToEditFragment(note!!, tags)
        }
        return view
    }

    companion object {

        private const val ARG_NOTE: String = "ARG_NOTE"
        private const val ARG_TAGS: String = "ARG_TAGS"

        fun newInstance(note: Note?, viewModel: NotesViewModel): NoteFragment {
            val fragment = NoteFragment()
            val bundle = Bundle()
            val tags = viewModel.fetchTagsFromNoteAsync(note!!)
            val arrayTags = ArrayList<String>(tags.map { tag -> tag.tag })
            bundle.putParcelable(ARG_NOTE, note)
            bundle.putStringArrayList(ARG_TAGS, arrayTags)
            fragment.arguments = bundle
            return fragment
        }
    }

    private fun redirectToEditFragment(note: Note, tags: List<String>) {
        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        val fragment = NoteEditFormFragment.newInstance(note, tags)
        fragmentManager?.popBackStack()
        fragmentTransaction
            ?.addToBackStack(null)
            ?.replace(R.id.fragment_container, fragment)
            ?.commit()
    }

}
