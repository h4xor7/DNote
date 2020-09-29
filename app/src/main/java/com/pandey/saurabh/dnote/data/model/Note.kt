package com.pandey.saurabh.dnote.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "note_table")
data class Note(
    val title: String,
 @PrimaryKey   val note: String
) {


}