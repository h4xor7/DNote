package com.pandey.saurabh.dnote.data.local.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pandey.saurabh.dnote.data.model.Note


@Dao
interface NoteDao {
    @Query("SELECT * FROM note_table")
    fun getAll(): LiveData<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT * from note_table ORDER BY id ASC")
    fun getSortedByIdNote():  LiveData<List<Note>>

    @Query("SELECT * from note_table ORDER BY title ASC")
    fun getSortedByTitleNote():  LiveData<List<Note>>

}