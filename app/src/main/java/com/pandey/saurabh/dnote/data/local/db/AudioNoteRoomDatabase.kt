package com.pandey.saurabh.dnote.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pandey.saurabh.dnote.data.local.db.dao.AudioNoteDao
import com.pandey.saurabh.dnote.data.model.AudioNote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Database(entities = [AudioNote::class],version = 1,exportSchema = false)
abstract class AudioNoteRoomDatabase: RoomDatabase() {

    abstract  fun audioNoteDao() :AudioNoteDao

    private class AudioNoteRoomDatabaseCallback(private  val scope: CoroutineScope):
        RoomDatabase.Callback(){
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)

           INSTANCE?.let{
               database ->
               scope.launch {


               }

            }


        }
    }

companion object{

    @Volatile
    private var INSTANCE: AudioNoteRoomDatabase? = null

    fun getDatabase(context: Context):AudioNoteRoomDatabase{
        val tempAudoiInstance = INSTANCE
        if (tempAudoiInstance != null) {
            return tempAudoiInstance
        }


        synchronized(this){
            val instance = Room.databaseBuilder(context.applicationContext,
            AudioNoteRoomDatabase::class.java,
            "audio_note_database").build()
            INSTANCE = instance
            return  instance
        }

    }



}


}