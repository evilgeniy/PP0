package com.example.lr3.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.runBlocking

class NotesRepository(
    private val noteDataDao: NoteDataDao,
    private val noteTagDao: NoteTagDao
) {
    val notesByTitle: LiveData<List<Note>> = noteDataDao.getAllNotesByTitle()
    val notesByDate: LiveData<List<Note>> = noteDataDao.getAllNotesByDate()

    suspend fun insertNote(note: Note, tags: List<Tag>) {
        val noteId = noteDataDao.insertNote(note)
        Log.d("Repository", "inserted noteId $noteId")
        Log.d("Repository", "inserted noteId ${note.creationDate}")
        if (tags.isNotEmpty()) {
            var tagsIds = mutableListOf<Long>()
            for (tag in tags) {
                val existTagId = noteDataDao.getTagId(tag.tag)
                if (existTagId == null) {
                    tagsIds.add(noteDataDao.insertTag(tag))
                } else {
                    tagsIds.add(existTagId)
                }
                Log.d("Repository", "inserted tag ${tag.tag}")
            }
            tagsIds = tagsIds.distinct().toMutableList()
            for (tagId in tagsIds) {
                val id = noteTagDao.insertNoteTagJoin(NoteTagJoin(noteId, tagId))
                Log.d("Repository", "inserted join $id")
            }
        }
    }

    fun getTagsFromNote(noteId: Long): List<Tag>  = runBlocking {
        val notePojo: NoteWithTagsPojo = noteTagDao.getNoteWithTags(noteId)
        Log.d("Repository", "requested noteId $noteId")
        Log.d("Repository", "Gettin tags ${notePojo.tags}")
        return@runBlocking when {
            notePojo.tags.isNotEmpty() -> notePojo.tags
            else -> listOf()
        }
    }

    fun getNotesByTag(tagString: String): MutableLiveData<List<Note>>  = runBlocking {
        Log.d("Repository", "Tag string is $tagString")
        val tagPojo: TagWithNotesPojo? = noteTagDao.getTagWithNotes("$tagString%")
        return@runBlocking when {
            tagPojo != null ->  MutableLiveData<List<Note>>(tagPojo.notes)
            else -> MutableLiveData<List<Note>>()
        }
    }

    fun updateNote(
        note: Note,
        newTitle: String,
        newContent: String,
        tagsToDetach: List<String>,
        tagsToAttach: List<String>
        ) {
        note.title = newTitle
        note.content = newContent
        runBlocking {
            Log.d("Repository", "tags to detach $tagsToDetach")
            for (tag in tagsToDetach) {
                val tagId = noteDataDao.getTagId(tag)
                if (tagId != null) {
                    val noteTagJoin = noteTagDao.getNoteTagJoin(note.id, tagId)
                    noteTagDao.deleteNoteTagJoin(noteTagJoin)
                }
            }
            Log.d("Repository", "tags to attach $tagsToAttach")
            for (tag in tagsToAttach) {
                var tagId = noteDataDao.getTagId(tag)
                val noteTagJoin: NoteTagJoin
                if (tagId == null) {
                    tagId = noteDataDao.insertTag(Tag(tag))
                    noteTagJoin = NoteTagJoin(note.id, tagId)
                } else {
                    noteTagJoin = NoteTagJoin(note.id, tagId)
                }
                noteTagDao.insertNoteTagJoin(noteTagJoin)
            }
            noteDataDao.updateNote(note)
        }
    }

    suspend fun deleteNote(note: Note) {
        val notePojo: NoteWithTagsPojo = noteTagDao.getNoteWithTags(note.id)
        for (tag in notePojo.tags) {
            noteTagDao.deleteNoteTagJoin(NoteTagJoin(note.id, tag.id))
        }
        noteDataDao.deleteNote(note)
    }

}
