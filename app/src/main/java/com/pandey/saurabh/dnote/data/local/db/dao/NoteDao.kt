package com.pandey.saurabh.dnote.data.local.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pandey.saurabh.dnote.data.model.Note


@Dao
interface NoteDao {
    @Query("SELECT * FROM note_table")
    fun getAll(): LiveData<List<Note>>

    @Query("SELECT * FROM note_table WHERE isDone=0")
    fun getAllToDo(): LiveData<List<Note>>


    @Query("SELECT * FROM note_table WHERE isDone=1")
    fun getAllDone(): LiveData<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(note: Note)


    @Delete
    suspend fun delete(note: Note)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(note: Note)

    @Query("SELECT * from note_table ORDER BY id ASC")
    fun getSortedByIdNote(): LiveData<List<Note>>


    @Query("SELECT * from note_table ORDER BY note ASC")
    fun getSortedByTitleNote(): LiveData<List<Note>>


}