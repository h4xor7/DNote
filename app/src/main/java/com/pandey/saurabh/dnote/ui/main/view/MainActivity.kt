package com.pandey.saurabh.dnote.ui.main.view

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.pandey.saurabh.dnote.R
import com.pandey.saurabh.dnote.data.model.Note
import com.pandey.saurabh.dnote.ui.base.BaseActivity
import com.pandey.saurabh.dnote.ui.main.adapter.CompletedNoteAdapter
import com.pandey.saurabh.dnote.ui.main.adapter.NotesAdapter
import com.pandey.saurabh.dnote.ui.main.viewmodel.MakeEntryViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    private lateinit var makeEntryViewModel: MakeEntryViewModel
    private  lateinit var  getDoneViewModel: MakeEntryViewModel
    private val newEntryRequestCode = 1
    private var isBottomVisible: Boolean = true

    lateinit var adapter: NotesAdapter
    lateinit var mAdapter: CompletedNoteAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initDrawer(true)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rvNotes.layoutManager = layoutManager
        adapter = NotesAdapter(this)


        val mlayoutManager = LinearLayoutManager(this)
        mlayoutManager.orientation = LinearLayoutManager.VERTICAL
        rvCompletedNotes.layoutManager = mlayoutManager
        mAdapter = CompletedNoteAdapter(this)

        textCompleted.setOnClickListener {
            if (isBottomVisible) {
                isBottomVisible = false
                rvCompletedNotes.visibility = View.GONE
                textCompleted.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_baseline_keyboard_arrow_up_24,
                    0,
                    0,
                    0
                );

            } else {

                isBottomVisible = true
                rvCompletedNotes.visibility = View.VISIBLE
                textCompleted.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_baseline_keyboard_arrow_down_24,
                    0,
                    0,
                    0
                );

            }


        }



        rvNotes.adapter = adapter
        adapter.setEventListener(object : NotesAdapter.EventListener {
            override fun onListItemClick(viewHolder: NotesAdapter.NoteViewHolder, position: Int) {

                val note = adapter.getNoteAtPosition(position)?.note?.let { it1 -> Note(it1, true) }
                note?.id = adapter.getNoteAtPosition(position)?.id
                note?.let { makeEntryViewModel.update(it) }
                adapter.notifyItemChanged(position)
                adapter.notifyDataSetChanged()

            }

        })

        rvCompletedNotes.adapter = mAdapter

        onSwipeDeleteCompleted()
        observeViewModel()
        fabButton.setOnClickListener {
            val makeEntryIntent = Intent(this, MakeEntryActivity::class.java)
            startActivityForResult(makeEntryIntent, newEntryRequestCode)
        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newEntryRequestCode && resultCode == Activity.RESULT_OK) {
            val noteText = data?.getStringExtra(MakeEntryActivity.EXTRA_NOTE)

            val note = noteText?.let { Note(it, false) }
            note?.let { makeEntryViewModel.insert(it) }

        } else {

            Toast.makeText(this, "Empty Can Not Be Saved ", Toast.LENGTH_SHORT).show()
        }
    }


    private fun onSwipeDeleteCompleted( ) {
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
                val note = mAdapter.getNoteAtPosition(position)
                note?.let { makeEntryViewModel.delete(it) }


                adapter.notifyItemChanged(position)


            }
        }

        val itemTouchHelper = ItemTouchHelper(itemCallback)
        itemTouchHelper.attachToRecyclerView(rvCompletedNotes)

    }


    private fun observeViewModel() {


        val factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MakeEntryViewModel(
                    application,

                    ) as T
            }
        }


        makeEntryViewModel = ViewModelProvider(this, factory).get(MakeEntryViewModel::class.java)

        getDoneViewModel = ViewModelProvider(this,factory).get(MakeEntryViewModel::class.java)



        makeEntryViewModel.allNotes.observe(this, Observer { notes ->
            notes?.let { adapter.setNotes(it) }
        })




        getDoneViewModel.allDoneNotes.observe(this, { notes ->
            notes?.let { mAdapter.setCompletedNotes(it) }
        })



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }




    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.sortBy -> {


            }
            R.id.showAllNotes -> {
/*
                makeEntryViewModel.sortByIdNotes.observe(this, Observer { notes ->
                    notes?.let {
                        adapter.setNotes(it)
                        adapter.notifyDataSetChanged()

                    }
                })
*/
            }

            else -> {

                Toast.makeText(this, "Nothing to do", Toast.LENGTH_SHORT).show()
            }

        }
        return super.onOptionsItemSelected(item)

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
            //  val note = adapter.getNoteAtPosition(position)
            val note = adapter.getNoteAtPosition(position)?.note?.let { it1 -> Note(it1, true) }
            note?.id = adapter.getNoteAtPosition(position)?.id
            note?.let { makeEntryViewModel.update(it) }
            // adapter.notifyItemChanged(position)
            adapter.notifyDataSetChanged()
            dialog.dismiss()
        }
        noBtn.setOnClickListener {
            adapter.notifyDataSetChanged()
            dialog.dismiss()
        }
        dialog.show()

        val layoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.getWindow()?.getAttributes())
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.getWindow()?.setAttributes(layoutParams)
    }


    companion object {
        const val CHANNEL_ID = "test_channel_id"
    }


    /*  viewHolder.itemView.doneCheckbox.setOnCheckedChangeListener { _, isChecked ->
          if (isChecked) {
              val position = viewHolder.adapterPosition
              val note = adapter.getNoteAtPosition(position)?.note?.let { it1 -> Note(it1, true) }
              note?.id = adapter.getNoteAtPosition(position)?.id
              note?.let { makeEntryViewModel.update(it) }
              adapter.notifyItemChanged(position)
              adapter.notifyDataSetChanged()
          } else {

          }
      }*/


}

