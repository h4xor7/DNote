package com.pandey.saurabh.dnote.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pandey.saurabh.dnote.data.local.db.dao.NoteDao
import com.pandey.saurabh.dnote.data.model.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Note::class],version = 1,exportSchema = false)
abstract class NoteRoomDatabase :RoomDatabase() {

    abstract fun noteDao():NoteDao


    private  class NoteRoomDatabaseCallback(
        private val scope: CoroutineScope
    ): RoomDatabase.Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let {

                    database ->
                scope.launch {
                    var noteDao = database.noteDao()

                    // Delete all content here.
                    //   noteDao.deleteAll()

                    // Add sample words.
                    var note = Note("hello", false)
                    noteDao.insertAll(note)

                }
            }
        }

    }

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.

        @Volatile
        private var INSTANCE: NoteRoomDatabase? = null


        fun getDatabase(context: Context): NoteRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteRoomDatabase::class.java,
                    "note_database"
                ).build()
                INSTANCE = instance
                return instance
            }

        }
    }
}