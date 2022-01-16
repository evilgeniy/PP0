package com.example.lr3.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

class NoteWithTagsPojo {
    @Embedded
    lateinit var note: Note

    @Relation(
        parentColumn = "note_id",
        entityColumn = "tag_id",
        associateBy = Junction(
            value = NoteTagJoin::class,
            parentColumn = "fk_note_id",
            entityColumn = "fk_tag_id"
        )
    )
    var tags: List<Tag> = arrayListOf()
}