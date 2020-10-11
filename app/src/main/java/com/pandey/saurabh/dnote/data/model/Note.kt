package com.pandey.saurabh.dnote.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "note_table")
data class Note(
    var timestamp: Long = 0L, //for date and time
    val title: String,
    val note: String

) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

}