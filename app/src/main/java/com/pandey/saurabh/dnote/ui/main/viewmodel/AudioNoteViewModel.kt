package com.pandey.saurabh.dnote.ui.main.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.pandey.saurabh.dnote.data.local.db.AudioNoteRoomDatabase
import com.pandey.saurabh.dnote.data.model.AudioNote
import com.pandey.saurabh.dnote.data.repository.AudioNoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AudioNoteViewModel(application: Application) : AndroidViewModel(application) {

    private  val repository:AudioNoteRepository
    val allAudioNotes: LiveData<List<AudioNote>>
    val sortByIdAudioNotes: LiveData<List<AudioNote>>
    val sortByTitleAudioNotes: LiveData<List<AudioNote>>


    init {
        val audioNoteDao = AudioNoteRoomDatabase.getDatabase(application).audioNoteDao()
        repository = AudioNoteRepository(audioNoteDao)
        allAudioNotes = repository.allAudioNotes
        sortByIdAudioNotes = repository.sortByIdAudioNotes
        sortByTitleAudioNotes = repository.sortByTitleAudioNotes


    }
    fun insert(audioNote: AudioNote) = viewModelScope.launch(Dispatchers.IO) {

        repository.insert(audioNote)
    }

    fun delete(audioNote: AudioNote) = viewModelScope.launch(Dispatchers.IO) {

        repository.delete(audioNote)
    }

    fun  update(audioNote: AudioNote)= viewModelScope.launch (Dispatchers.IO){
        repository.update(audioNote)
    }




}