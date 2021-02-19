package com.pandey.saurabh.dnote.data.repository

import androidx.lifecycle.LiveData
import com.pandey.saurabh.dnote.data.local.db.dao.AudioNoteDao
import com.pandey.saurabh.dnote.data.model.AudioNote

class AudioNoteRepository(private val audioNoteDao: AudioNoteDao) {

    val allAudioNotes: LiveData<List<AudioNote>> = audioNoteDao.getAllAudioNote()
  //  val allDoneAudioNotes: LiveData<List<AudioNote>> = audioNoteDao.getAllDoneAudioNote()
   // val allToDo: LiveData<List<AudioNote>> = audioNoteDao.getAllToDoAudioNote()

    val sortByIdAudioNotes: LiveData<List<AudioNote>> = audioNoteDao.getSortedByIdNote()
    val sortByTitleAudioNotes: LiveData<List<AudioNote>> = audioNoteDao.getSortedByTitleNote()

    suspend fun insert(audioNote: AudioNote) {
        audioNoteDao.insertAll(audioNote)
    }

    suspend fun delete(audioNote: AudioNote) {
        audioNoteDao.delete(audioNote)
    }

    suspend fun update(audioNote: AudioNote) {
        audioNoteDao.update(audioNote)
    }


}