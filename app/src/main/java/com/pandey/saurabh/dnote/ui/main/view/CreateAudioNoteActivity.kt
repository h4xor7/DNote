package com.pandey.saurabh.dnote.ui.main.view

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pandey.saurabh.dnote.R
import com.pandey.saurabh.dnote.utils.Constant.EXTRA_NOTE_FILE_PATH
import com.pandey.saurabh.dnote.utils.Constant.EXTRA_NOTE_PRIORITY
import com.pandey.saurabh.dnote.utils.Constant.EXTRA_NOTE_TITLE
import com.pandey.saurabh.dnote.utils.Constant.INTENT_FILE_PATH
import kotlinx.android.synthetic.main.activity_create_audio_note.*

class CreateAudioNoteActivity : AppCompatActivity() {
    var priority: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_audio_note)

        val filePath: String? = intent.getStringExtra(INTENT_FILE_PATH)
        val audioNote = editTextTitle.text.toString()

        radio_group.setOnCheckedChangeListener { group, checkedId ->

            if (checkedId == R.id.priorityLow) {

                priority = "Low"

            } else if (checkedId == R.id.priorityNormal) {

                priority = "Normal"
            } else {

                priority = "High"

            }


        }




        btnSaveAudio.setOnClickListener {

            if (!TextUtils.isEmpty(editTextTitle.text.toString()) && !filePath.equals("") && !priority.equals("")){
                val replyIntent = Intent(this, AudioNoteActivity::class.java)
                replyIntent.putExtra(EXTRA_NOTE_TITLE, editTextTitle.text.toString())
                replyIntent.putExtra(EXTRA_NOTE_FILE_PATH, filePath)
                replyIntent.putExtra(EXTRA_NOTE_PRIORITY, priority)
                startActivity(replyIntent)
                finish()
            }
            else{
                Toast.makeText(this, "Data can not be Empty", Toast.LENGTH_SHORT).show()
            }


        }

    }


}



