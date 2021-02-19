package com.pandey.saurabh.dnote.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audio_note_table")
data class AudioNote(  val noteTitle: String,
                       val filePath:String,
                       val priority:String,
                       val dateTime:String,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

}