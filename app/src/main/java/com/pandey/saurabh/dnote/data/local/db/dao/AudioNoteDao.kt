package com.pandey.saurabh.dnote.data.local.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pandey.saurabh.dnote.data.model.AudioNote
import com.pandey.saurabh.dnote.data.model.Note


@Dao
interface AudioNoteDao {

    @Query("SELECT * FROM audio_note_table")
    fun getAllAudioNote(): LiveData<List<AudioNote>>

 /*   @Query("SELECT * FROM audio_note_table WHERE isDone=0")
    fun getAllToDoAudioNote(): LiveData<List<AudioNote>>


    @Query("SELECT * FROM audio_note_table WHERE isDone=1")
    fun getAllDoneAudioNote(): LiveData<List<AudioNote>>
*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(note: AudioNote)


    @Delete
    suspend fun delete(audioNote: AudioNote)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(audioNote: AudioNote)

    @Query("SELECT * from audio_note_table ORDER BY id ASC")
    fun getSortedByIdNote(): LiveData<List<AudioNote>>


    @Query("SELECT * from audio_note_table ORDER BY noteTitle ASC")
    fun getSortedByTitleNote(): LiveData<List<AudioNote>>

}