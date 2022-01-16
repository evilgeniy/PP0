package com.example.lr3.ui

import android.content.res.Configuration
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import com.example.lr3.R
import com.example.lr3.adapters.NotesAdapter
import com.example.lr3.adapters.SwipeToDeleteCallback
import com.example.lr3.data.Note
import com.example.lr3.viewModels.NotesViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NotesListFragment : Fragment() {

    private lateinit var notesList: RecyclerView
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var viewModel: NotesViewModel
    private lateinit var notes: List<Note>
    private lateinit var newButton: FloatingActionButton
    private  lateinit var sortButton: ImageButton
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_notes_list, container, false)


        viewModel = activity?.run {
            ViewModelProvider(this)[NotesViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        notesAdapter = NotesAdapter(requireContext(), viewModel) { position ->
            setUpDetails(position)
        }
        notesList = view.findViewById(R.id.notes_list)
        notesList.adapter = notesAdapter

        viewModel.allNotes.observe(viewLifecycleOwner, Observer { notes ->
            // Update the cached copy of the words in the adapter.
            notes?.let {
                notesAdapter.setNotes(it.toMutableList())
                this.notes = it
            }
        })

        setUpRecyclerViewLayout()

        newButton = view.findViewById(R.id.new_note_button)
        newButton.setOnClickListener {
            redirectToNewFragment()
        }

        sortButton = view.findViewById(R.id.sort_image_button)
        sortButton.setOnClickListener {
            showPopup(it)
        }
        searchView = view.findViewById(R.id.notes_search)
        searchView.setOnCloseListener {
            viewModel.setAllNotes()
            false
        }
        searchView.setOnQueryTextListener (object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.tagSearchString.value = searchView.query.toString()
                viewModel.searchTags()
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })
        return view
    }

    private fun setUpRecyclerViewLayout() {
        val currentOrientation = activity?.resources?.configuration?.orientation
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            notesList.layoutManager = GridLayoutManager(requireContext(), 3)
        } else {
            notesList.layoutManager = LinearLayoutManager(requireContext())
            val decoration = DividerItemDecoration(requireContext(), LinearLayout.VERTICAL)
            notesList.addItemDecoration(decoration)
            val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(notesAdapter))
            itemTouchHelper.attachToRecyclerView(notesList)
        }
    }

    private fun setUpDetails(position: Int) {
        val note = notes[position]
        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        val fragment = NoteFragment.newInstance(note, viewModel)
        fragmentTransaction
            ?.addToBackStack(null)
            ?.replace(R.id.fragment_container, fragment)
            ?.commit()
    }

    private fun redirectToNewFragment() {
        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        val fragment = NoteFormFragment()
        fragmentTransaction
            ?.addToBackStack(null)
            ?.replace(R.id.fragment_container, fragment)
            ?.commit()
    }

    private fun showPopup(view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.inflate(R.menu.sort_menu)

        popup.setOnMenuItemClickListener { item: MenuItem? ->
            when (item!!.itemId) {
                R.id.by_title_option -> {
                    viewModel.rearrangeNotes(NotesViewModel.NotesOrder.BY_TITLE)
                }
                R.id.by_date_option -> {
                    viewModel.rearrangeNotes(NotesViewModel.NotesOrder.BY_DATE)
                }
            }
            true
        }
        popup.show()
    }
}
