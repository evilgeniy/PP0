package com.example.lr3.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tags",
    indices = [Index("tag_id")]
)
data class Tag (
    @ColumnInfo(name = "tag") var tag: String,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tag_id")
    var id: Long = 0

)