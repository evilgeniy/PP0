package com.example.lr3.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity (
    tableName = "note_tag_join",
    primaryKeys = ["fk_note_id", "fk_tag_id"],
    indices = [
        Index("fk_note_id"),
        Index("fk_tag_id")
    ],
    foreignKeys = [
        ForeignKey(
            entity = Note::class,
            parentColumns = ["note_id"],
            childColumns = ["fk_note_id"]
        ),
        ForeignKey(
            entity = Tag::class,
            parentColumns = ["tag_id"],
            childColumns = ["fk_tag_id"]
        )
    ]
)
data class NoteTagJoin (
    @ColumnInfo(name = "fk_note_id") var noteId: Long,
    @ColumnInfo(name = "fk_tag_id") var tagId: Long
)