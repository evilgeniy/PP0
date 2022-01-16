package com.example.lr3.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.lr3.data.*
import kotlinx.coroutines.launch

class NotesViewModel(application: Application): AndroidViewModel(application) {

    private val repository: NotesRepository
    private val notesOrderedByTitle: LiveData<List<Note>>
    private val notesOrderedByDate: LiveData<List<Note>>
    private val notesFiltered: LiveData<List<Note>>
    var allNotes = MediatorLiveData<List<Note>>()
    var tagSearchString = MutableLiveData<String>()
    val TAG = "NotesViewModel"
    enum class NotesOrder { BY_TITLE, BY_DATE }
    private var currentOrder: NotesOrder


    init {
        Log.d(TAG, "DB was not created")
        val db = NotesDatabase.getDatabase(application, viewModelScope)
        Log.d(TAG, "DB was created")
        val noteDataDao = db.run { noteDataDao() }
        val noteTagDao = db.run { noteTagDao() }

        repository = NotesRepository(noteDataDao, noteTagDao)
        notesOrderedByTitle = repository.notesByTitle
        notesOrderedByDate = repository.notesByDate
        notesFiltered = Transformations.switchMap(
            tagSearchString,
            { repository.getNotesByTag(it) }
        )
        currentOrder = NotesOrder.BY_DATE

        allNotes.addSource(notesOrderedByTitle) { result ->
            if (currentOrder == NotesOrder.BY_TITLE) {
                result?.let { allNotes.value = it }
            }
        }
        allNotes.addSource(notesOrderedByDate) { result ->
            if (currentOrder == NotesOrder.BY_DATE) {
                result?.let { allNotes.value = it }
            }
        }
        allNotes.addSource(notesFiltered) { }
    }

    fun insertNote(note: Note, tagsString: String) = viewModelScope.launch {
        val tagsList = separateTags(tagsString).map { tagStr -> Tag(tagStr) }
        repository.insertNote(note, tagsList)
    }

    fun fetchTagsFromNoteAsync(note: Note): List<Tag> = repository.getTagsFromNote(note.id)


    fun deleteNote (note: Note) = viewModelScope.launch {
        repository.deleteNote(note)
    }

    fun updateNote(
        note: Note,
        oldTags: List<String>,
        newTitle: String,
        newTagsString: String,
        newContent: String
    ) {
        val oldTagsSet = oldTags.toSet()
        val newTagsSet = separateTags(newTagsString).toSet()
        val tagsToDetach = (oldTagsSet subtract newTagsSet).toList()
        val tagsToAttach = (newTagsSet subtract oldTagsSet).toList()
        repository.updateNote(note, newTitle, newContent, tagsToDetach, tagsToAttach)
    }

    private fun separateTags(tagsString: String): List<String> {
        return tagsString
            ?.split("\\s+".toRegex())
            .flatMap {tag -> tag.split("#")}
            .filter { tag -> tag != "" }
    }

    fun setAllNotes() {
        if (currentOrder == NotesOrder.BY_TITLE) {
            notesOrderedByTitle.value?.let { allNotes.value = it }
        } else {
            notesOrderedByDate.value?.let { allNotes.value = it }
        }
    }

    fun rearrangeNotes(order: NotesOrder) = when (order) {
        NotesOrder.BY_TITLE -> notesOrderedByTitle.value?.let { allNotes.value = it }
        NotesOrder.BY_DATE -> notesOrderedByDate.value?.let { allNotes.value = it }
    }.also { currentOrder = order }

    fun searchTags() {
        if (tagSearchString.value == "")
            setAllNotes()
        else {
            notesFiltered.value?.let { allNotes.value = it }
            Log.d("ViewModel", "allNotes are updated")
        }
    }
}