package com.example.lr3.ui


import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.example.lr3.R
import com.example.lr3.data.Note
import com.example.lr3.viewModels.NotesViewModel
import java.time.LocalDate

class NoteFormFragment : Fragment() {

    private lateinit var titleEditText: EditText
    private lateinit var tagsEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var doneButton: Button
    private lateinit var viewModel: NotesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_note_form, container, false)

        titleEditText = view.findViewById(R.id.form_title_edit_text)
        tagsEditText = view.findViewById(R.id.form_tags_edit_text)
        contentEditText = view.findViewById(R.id.form_content_edit_text)
        doneButton = view.findViewById(R.id.form_done_button)
        viewModel = activity?.run {
            ViewModelProvider(this)[NotesViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        doneButton.setOnClickListener {
            val title: String = if (TextUtils.isEmpty(titleEditText.text.toString())) {
                LocalDate.now().toString()
            } else {
                titleEditText.text.toString()
            }
            val tagsString: String?
            val content: String?
            tagsString = tagsEditText.text.toString()
            content = contentEditText.text.toString()
            val note = Note(title, content)
            viewModel.insertNote(note, tagsString)
            activity?.supportFragmentManager?.popBackStack()
        }
        return view
    }
}
