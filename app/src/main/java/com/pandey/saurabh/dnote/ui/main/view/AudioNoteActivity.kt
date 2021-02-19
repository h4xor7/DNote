package com.pandey.saurabh.dnote.ui.main.view

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pandey.saurabh.dnote.R
import com.pandey.saurabh.dnote.data.model.AudioNote
import com.pandey.saurabh.dnote.ui.base.BaseActivity
import com.pandey.saurabh.dnote.ui.main.adapter.AudioNoteAdapter
import com.pandey.saurabh.dnote.ui.main.viewmodel.AudioNoteViewModel
import com.pandey.saurabh.dnote.utils.Constant.EXTRA_NOTE_FILE_PATH
import com.pandey.saurabh.dnote.utils.Constant.EXTRA_NOTE_PRIORITY
import com.pandey.saurabh.dnote.utils.Constant.EXTRA_NOTE_TITLE
import com.pandey.saurabh.dnote.utils.Constant.INTENT_FILE_PATH
import kotlinx.android.synthetic.main.activity_audio_note.*
import kotlinx.android.synthetic.main.dialog_start_recording.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class AudioNoteActivity : BaseActivity() {

    var isRecording = false
    private var filePath: String? = null
    private var fileName: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false
    private var intentFilePath: String? = null
    private var recordingStopped: Boolean = false
    private val newAudioEntryRequestCode = 101
    private lateinit var audioNoteViewModel: AudioNoteViewModel

    lateinit var audioNoteAdapter: AudioNoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_note)

        initDrawer(true)
        initViewModel()
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rvAudioNotes.layoutManager = layoutManager
        audioNoteAdapter = AudioNoteAdapter()
        audioNoteAdapter.setAudioEventListener(object : AudioNoteAdapter.AudioEventListener {
            override fun onAudioListItemClick(audioNote: AudioNote, position: Int) {
                val mp = MediaPlayer()

                try {
                    mp.setDataSource(audioNote.filePath)

                    Log.d(Companion.TAG, "onAudioListItemClick: path is ${audioNote.filePath}")
                    mp.prepare()
                    mp.start()
                }
                
                catch (e: Exception) {
                    e.printStackTrace()
                }

            }

        })


        rvAudioNotes.adapter = audioNoteAdapter
        onSwipeDeleteAudioNotes()




        floating_action_button.setOnClickListener {

            showStartRecordingDialog()
        }

        getIntentValue()
        observeViewModel()

    }


    private fun showStartRecordingDialog() {

        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_start_recording)
        // dialog.window?.setLayout((6 * width) / 7, (4 * height) / 5)
        dialog.window?.setLayout((6 * width) / 7, ConstraintLayout.LayoutParams.WRAP_CONTENT)

        dialog.mic_image.setOnClickListener {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val permissions = arrayOf(
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                ActivityCompat.requestPermissions(this, permissions, 0)
            } else {

                if (isRecording) {

                    dialog.timer.stop()
                    stopRecording()
                    dialog.record_hint_text.text = "Tap to Start Recording"

                    isRecording = false

                } else {
                    dialog.timer.base = SystemClock.elapsedRealtime()
                    dialog.timer.start()
                    startRecording()
                    dialog.record_hint_text.text = "Recording...."
                    isRecording = true

                }


            }

        }





        dialog.dialog_cancel_button.setOnClickListener {

            dialog.timer.stop()
            stopRecording()

            dialog.dismiss()
        }

        dialog.dialog_save_button.setOnClickListener {

            dialog.timer.stop()
            stopRecording()

            dialog.dismiss()

            val intent = Intent(this, CreateAudioNoteActivity::class.java)
            intent.putExtra(INTENT_FILE_PATH, intentFilePath)
            startActivity(intent)
        }

        dialog.show()


    }

    private fun startRecording() {


        //Get app external directory path
        val recordPath = activity.getExternalFilesDir("/")!!.absolutePath

        filePath = activity.getExternalFilesDir("/")!!.absolutePath
        val formatter = SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA)
        val now = Date()
        fileName = "Recording_" + formatter.format(now) + ".3gp"

        mediaRecorder = MediaRecorder()
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mediaRecorder?.setOutputFile("$filePath/$fileName")

        intentFilePath = "$filePath/$fileName"





        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            state = true
            Toast.makeText(this, "Recording started!", Toast.LENGTH_SHORT).show()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun stopRecording() {
        if (state) {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            state = false
        } else {
            Toast.makeText(this, "You are not recording right now!", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDateTime(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return current.format(formatter)

    }

    private fun getIntentValue() {
        val title: String
        val priority: String
        val filePath: String

        if (intent.hasExtra(EXTRA_NOTE_TITLE) && intent.hasExtra(EXTRA_NOTE_PRIORITY) && intent.hasExtra(
                EXTRA_NOTE_FILE_PATH
            )
        ) {

            title = intent.getStringExtra(EXTRA_NOTE_TITLE).toString()
            priority = intent.getStringExtra(EXTRA_NOTE_PRIORITY).toString()
            filePath = intent.getStringExtra(EXTRA_NOTE_FILE_PATH).toString()

            val audioNote = AudioNote(title, filePath, priority, getOldDateTime())
            audioNoteViewModel.insert(audioNote)

        }

    }

    private fun observeViewModel() {


        audioNoteViewModel.allAudioNotes.observe(this, androidx.lifecycle.Observer { audioNotes ->
            audioNoteAdapter.setAudioNotes(audioNotes)

        })

    }

    private fun initViewModel() {
        val factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return AudioNoteViewModel(application) as T
            }
        }

        audioNoteViewModel = ViewModelProvider(this, factory).get(AudioNoteViewModel::class.java)

    }

    private fun getOldDateTime(): String {

        val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.CANADA)
        val date = Date()
        return formatter.format(date)
    }

    private fun onSwipeDeleteAudioNotes() {
        val itemCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val position = viewHolder.adapterPosition
                val audioNote = audioNoteAdapter.getNoteAtPosition(position)

                val myFilePath = audioNote?.filePath
                val file: File = File(myFilePath)
                val deleted = file.delete()

                audioNote?.let { audioNoteViewModel.delete(it) }
                audioNoteAdapter.notifyItemChanged(position)

            }
        }

        val itemTouchHelper = ItemTouchHelper(itemCallback)
        itemTouchHelper.attachToRecyclerView(rvAudioNotes)
        Toast.makeText(this, "Audio Note Deleted !", Toast.LENGTH_SHORT).show()

    }

    companion object {
        private const val TAG = "AudioNoteActivity"
    }

    override fun onStop() {
        super.onStop()
        /* if (isRecording){
             stopRecording()
         }
 */
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRecording) {
            stopRecording()
        }

    }

}