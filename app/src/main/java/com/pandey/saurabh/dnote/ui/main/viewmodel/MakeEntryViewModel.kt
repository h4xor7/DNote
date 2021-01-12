package com.pandey.saurabh.dnote.ui.main.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.pandey.saurabh.dnote.data.local.db.NoteRoomDatabase
import com.pandey.saurabh.dnote.data.model.Note
import com.pandey.saurabh.dnote.data.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MakeEntryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository
    val allNotes: LiveData<List<Note>>
    val allDoneNotes :LiveData<List<Note>>
     val sortByIdNotes: LiveData<List<Note>>
     val sortByTitleNotes: LiveData<List<Note>>

    init {
        val noteDao = NoteRoomDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(noteDao)
        allNotes = repository.allToDo
        allDoneNotes = repository.allDoneNotes
        sortByIdNotes= repository.sortByIdNotes
        sortByTitleNotes= repository.sortByTitleNotes


    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */

    fun insert(note: Note) = viewModelScope.launch(Dispatchers.IO) {

        repository.insert(note)
    }

    fun delete(note: Note) = viewModelScope.launch(Dispatchers.IO) {

        repository.delete(note)
    }

    fun  update(note: Note)= viewModelScope.launch (Dispatchers.IO){
        repository.update(note)
    }




}