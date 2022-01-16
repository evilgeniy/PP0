package com.example.lr3.data

import androidx.room.*

@Dao
interface NoteTagDao {

    @Insert
    suspend fun insertNoteTagJoin(noteTagJoin: NoteTagJoin): Long

    @Delete
    suspend fun deleteNoteTagJoin(noteTagJoin: NoteTagJoin)

    @Query("SELECT * FROM note_tag_join WHERE fk_note_id = :noteId AND fk_tag_id = :tagId LIMIT 1")
    suspend fun getNoteTagJoin(noteId: Long, tagId: Long): NoteTagJoin

    @Query("DELETE FROM note_tag_join")
    suspend fun deleteAllNoteTagJoins()

    @Transaction @Query("SELECT * FROM notes WHERE note_id == :noteId")
    suspend fun getNoteWithTags(noteId: Long): NoteWithTagsPojo

    @Transaction @Query("SELECT * FROM tags WHERE tag LIKE :tagString LIMIT 1")
    suspend fun getTagWithNotes(tagString: String): TagWithNotesPojo?

}