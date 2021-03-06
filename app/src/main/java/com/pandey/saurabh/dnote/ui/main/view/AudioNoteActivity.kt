package com.pandey.saurabh.dnote.ui.main.view

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
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
import com.google.android.material.button.MaterialButton
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
    private var isPlaying = false
    private var updateSeekbar: Runnable? = null

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
                    /*  mp.setDataSource(audioNote.filePath)
                    mp.prepare()
                    mp.start()*/
                    playerDialog(mp, audioNote)

                } catch (e: Exception) {
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

                showDeleteDialog(audioNote!!.noteTitle, viewHolder)

            }
        }

        val itemTouchHelper = ItemTouchHelper(itemCallback)
        itemTouchHelper.attachToRecyclerView(rvAudioNotes)
       // Toast.makeText(this, "Audio Note Deleted !", Toast.LENGTH_SHORT).show()

    }

    companion object {
        private const val TAG = "AudioNoteActivity"
    }

    override fun onStop() {
        super.onStop()
         if (isRecording){
             stopRecording()
         }
    }

    override fun onDestroy() {
        super.onDestroy()
        /*if (isRecording) {
            stopRecording()
        }*/

    }


    private fun showDeleteDialog(title: String, viewHolder: RecyclerView.ViewHolder) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog)
        val body = dialog.findViewById(R.id.txt_dia) as TextView
        body.text = title
        val yesBtn = dialog.findViewById(R.id.btn_yes) as MaterialButton
        val noBtn = dialog.findViewById(R.id.btn_no) as MaterialButton
        yesBtn.setOnClickListener {
            val position = viewHolder.adapterPosition
            val audioNote = audioNoteAdapter.getNoteAtPosition(position)

            val myFilePath = audioNote?.filePath
            val file: File = File(myFilePath)
            val deleted = file.delete()

            audioNote?.let { audioNoteViewModel.delete(it) }
            audioNoteAdapter.notifyItemChanged(position)

            dialog.dismiss()
        }
        noBtn.setOnClickListener {
          //  adapter.notifyDataSetChanged()
            val position = viewHolder.adapterPosition
            audioNoteAdapter.notifyItemChanged(position)
            dialog.dismiss()
        }
        dialog.show()

        val layoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.getWindow()?.getAttributes())
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.getWindow()?.setAttributes(layoutParams)


    }


    private  fun playSpecificFile(filePath: String?){

        Log.d(TAG, "playSpecificFile: testing yml file ")
    }

    fun playerDialog(mediaPlayer: MediaPlayer, audioNote: AudioNote){

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_audio_player)


      //  val playBtn = dialog.findViewById<ImageView>(R.id.playRecord)
       // val pauseBtn = dialog.findViewById<ImageView>(R.id.pauseRecord)
       // val stopBtn =dialog.findViewById<ImageView>(R.id.stopRecord)
        val title = dialog.findViewById<TextView>(R.id.textView4)
        val seekBar = dialog.findViewById<SeekBar>(R.id.seekBar)

        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                //     pauseAudio()


            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (audioNote != null) {
                    val progress = seekBar.progress
                    mediaPlayer.seekTo(progress)
                    //   resumeAudio()
                }
            }
        })
        mediaPlayer.setDataSource(audioNote.filePath)
        mediaPlayer.prepare()
        mediaPlayer.start()
        isPlaying = true


        seekBar.max = mediaPlayer.duration
        val  seekBarHandler :Handler = android.os.Handler()
        updateRunnable(mediaPlayer,seekBar,seekBarHandler)
        updateSeekbar?.let { seekBarHandler.postDelayed(it,0) }




        dialog.show()

        val layoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.getWindow()?.getAttributes())
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.getWindow()?.setAttributes(layoutParams)
        title.text = audioNote.noteTitle



        mediaPlayer.setOnCompletionListener {
            mediaPlayer.stop()
            isPlaying = false
            dialog.dismiss()
        }

       /* playBtn.setOnClickListener {
            if (isPlaying){
                Toast.makeText(dialog.context, "Already Playing", Toast.LENGTH_SHORT).show()
            }else{

                mediaPlayer.start()
                isPlaying = true
            }
        }


        stopBtn.setOnClickListener {
            mediaPlayer.stop()
            isPlaying = false
            dialog.dismiss()
        }*/
    }


    private fun updateRunnable(mediaPlayer: MediaPlayer,seekBar: SeekBar,handler: Handler) {
        updateSeekbar = object : Runnable {
            override fun run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition())
                handler.postDelayed(this, 500)
            }
        }
    }

    //ci/cd test
}