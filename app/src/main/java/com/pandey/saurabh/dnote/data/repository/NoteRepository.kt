package com.pandey.saurabh.dnote.data.repository

import androidx.lifecycle.LiveData
import com.pandey.saurabh.dnote.data.local.db.dao.NoteDao
import com.pandey.saurabh.dnote.data.model.Note

class NoteRepository(private  val noteDao: NoteDao) {


    val allNotes:LiveData<List<Note>> = noteDao.getAll()

    suspend fun insert(note: Note){
        noteDao.insertAll(note)
    }

    suspend fun  delete(note: Note){
        noteDao.delete(note)
    }
}