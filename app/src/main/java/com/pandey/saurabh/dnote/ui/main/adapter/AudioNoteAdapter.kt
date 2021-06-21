package com.pandey.saurabh.dnote.ui.main.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pandey.saurabh.dnote.R
import com.pandey.saurabh.dnote.data.model.AudioNote
import com.pandey.saurabh.dnote.data.model.Note
import kotlinx.android.synthetic.main.item_audio_notes.view.*

class AudioNoteAdapter : RecyclerView.Adapter<AudioNoteAdapter.AudioNoteViewHolder>() {

    private var audioNotes = emptyList<AudioNote>()
    private var itemListener: AudioEventListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioNoteViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_audio_notes, parent, false)
        return AudioNoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: AudioNoteViewHolder, position: Int) {
        holder.itemView.text_title.text = audioNotes[position].noteTitle
        holder.itemView.text_dateTime.text = audioNotes[position].dateTime
        holder.itemView.setOnClickListener {
            itemListener?.onAudioListItemClick(audioNotes[position],position)
        }

        when (position % 4) {
            0 -> holder.itemView.audio_card_parent.setBackgroundColor(Color.parseColor("#BEE9E8"))
            1 -> holder.itemView.audio_card_parent.setBackgroundColor(Color.parseColor("#F3E1E1"))
            2 -> holder.itemView.audio_card_parent.setBackgroundColor(Color.parseColor("#C2ECB1"))
            3 -> holder.itemView.audio_card_parent.setBackgroundColor(Color.parseColor("#F8DC88"))
            else -> holder.itemView.audio_card_parent.setBackgroundColor(Color.parseColor("#BEE9E8"))
        }


    }

    override fun getItemCount(): Int {
        return audioNotes.size
    }

    internal fun setAudioNotes(audioNotes: List<AudioNote>) {
        this.audioNotes = audioNotes
        notifyDataSetChanged()

    }


    fun getNoteAtPosition(position: Int): AudioNote? {
        return this.audioNotes[position]
    }





    interface AudioEventListener {
        fun onAudioListItemClick(audioNote: AudioNote, position: Int)
    }

    fun setAudioEventListener(onItemClick: AudioEventListener?) {
        itemListener = onItemClick
    }


    inner class AudioNoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            setAudioEventListener(itemListener)
        }


    }





    companion object {
        private const val TAG = "AudioNoteAdapter"
    }

}