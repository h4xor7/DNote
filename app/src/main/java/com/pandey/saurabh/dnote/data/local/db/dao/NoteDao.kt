package com.pandey.saurabh.dnote.data.local.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.pandey.saurabh.dnote.data.model.Note


@Dao
interface NoteDao {
    @Query("SELECT * FROM note_table")
     fun getAll(): LiveData<List<Note>>

    @Insert
    suspend fun insertAll(note: Note)

    @Delete
    suspend fun delete(note: Note)
}