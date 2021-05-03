package com.pandey.saurabh.dnote.ui.main.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.pandey.saurabh.dnote.R
import com.pandey.saurabh.dnote.data.model.Note
import kotlinx.android.synthetic.main.item_notes.view.*

class CompletedNoteAdapter(private val context: Context) : RecyclerView.Adapter<CompletedNoteAdapter.CompletedNoteViewHolder>() {
    var colorRes = 0
    private var lastPosition = -1
    private var notes = emptyList<Note>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompletedNoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notes, parent, false)

        return CompletedNoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompletedNoteViewHolder, position: Int) {

        val current = notes[position]
        holder.itemView.textNotes.text = current.note
        holder.itemView.doneCheckbox.isChecked = true
        holder.itemView.textNotes.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

        when (position % 4) {
            0 -> holder.itemView.card_parent.setBackgroundColor(Color.parseColor("#BEE9E8"))
            1 -> holder.itemView.card_parent.setBackgroundColor(Color.parseColor("#F3E1E1"))
            2 -> holder.itemView.card_parent.setBackgroundColor(Color.parseColor("#C2ECB1"))
            3 -> holder.itemView.card_parent.setBackgroundColor(Color.parseColor("#F8DC88"))
            else -> holder.itemView.card_parent.setBackgroundColor(Color.parseColor("#BEE9E8"))
        }

        setAnimation(holder.itemView.card_parent, position)

    }

    override fun getItemCount(): Int {
        return notes.size
    }

    internal fun setCompletedNotes(words: List<Note>) {
        this.notes = words
        notifyDataSetChanged()
    }

    fun getNoteAtPosition(position: Int): Note? {
        return this.notes[position]
    }


    inner class CompletedNoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }


    private fun setAnimation(viewAnimate: View, position: Int) {

        if (position > lastPosition) {
            val animation: Animation =
                AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
            viewAnimate.startAnimation(animation)
            lastPosition = position
        }

    }
}