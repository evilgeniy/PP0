package com.example.lr3.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

class TagWithNotesPojo {
    @Embedded
    lateinit var tag: Tag

    @Relation(
        parentColumn = "tag_id",
        entityColumn = "note_id",
        associateBy = Junction(
            value = NoteTagJoin::class,
            parentColumn = "fk_tag_id",
            entityColumn = "fk_note_id"
        )
    )
    var notes: List<Note> = arrayListOf()
}