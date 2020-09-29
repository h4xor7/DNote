package com.pandey.saurabh.dnote.ui.main.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import com.pandey.saurabh.dnote.R
import kotlinx.android.synthetic.main.activity_make_entry.*

class MakeEntryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_entry)

        btnSave.setOnClickListener {

            val replyIntent =Intent()

            if (TextUtils.isEmpty(editTextTitle.text)|| TextUtils.isEmpty(editTextNote.text)){
                setResult(Activity.RESULT_CANCELED,replyIntent)
            }
            else{
                val  title =editTextTitle.text.toString()
                val note = editTextNote.text.toString()

                replyIntent.putExtra(EXTRA_TITLE,title)
                replyIntent.putExtra(EXTRA_NOTE,note)
                setResult(Activity.RESULT_OK,replyIntent)

            }

            finish()
        }



    }


    companion object{
        const val EXTRA_TITLE= "title_extra_tag"
        const val EXTRA_NOTE= "note_extra_tag"
    }

}