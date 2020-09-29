package com.pandey.saurabh.dnote.ui.main.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pandey.saurabh.dnote.R
import com.pandey.saurabh.dnote.data.model.Note
import com.pandey.saurabh.dnote.ui.main.adapter.NotesAdapter
import com.pandey.saurabh.dnote.ui.main.viewmodel.MakeEntryViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var makeEntryViewModel: MakeEntryViewModel
    private val newEntryRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rvNotes.layoutManager = layoutManager
        val adapter = NotesAdapter(this)
        rvNotes.adapter = adapter


        /*  val noteList = mutableListOf<String>()
          for (i in 1..30) {
              noteList.add("Practice Kotlin")
          }*/


        val factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MakeEntryViewModel(
                    application,

                ) as T
            }
        }



        makeEntryViewModel = ViewModelProvider(this,factory).get(MakeEntryViewModel::class.java)
        makeEntryViewModel.allNotes.observe(this, Observer { notes ->
            notes?.let { adapter.setNotes(it) }
        })


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

                 val note  = adapter.getNoteAtPosition(position)
                note?.let { makeEntryViewModel.delete(it) }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemCallback)
        itemTouchHelper.attachToRecyclerView(rvNotes)

        fabButton.setOnClickListener {
            val makeEntryIntent = Intent(this, MakeEntryActivity::class.java)
            startActivityForResult(makeEntryIntent, newEntryRequestCode)
        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newEntryRequestCode && resultCode == Activity.RESULT_OK) {
            val noteText = data?.getStringExtra(MakeEntryActivity.EXTRA_NOTE)
            val noteTitle = data?.getStringExtra(MakeEntryActivity.EXTRA_TITLE)
            val note = noteTitle?.let { noteText?.let { it1 -> Note(it, it1) } }

            note?.let { makeEntryViewModel.insert(it) }

        } else {

            Toast.makeText(this, "Empty Can Not Be Saved ", Toast.LENGTH_SHORT).show()
        }
    }


}