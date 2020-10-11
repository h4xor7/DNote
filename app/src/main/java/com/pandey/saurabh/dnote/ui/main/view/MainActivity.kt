package com.pandey.saurabh.dnote.ui.main.view

import android.app.Activity
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.pandey.saurabh.dnote.R
import com.pandey.saurabh.dnote.data.model.Note
import com.pandey.saurabh.dnote.ui.main.adapter.NotesAdapter
import com.pandey.saurabh.dnote.ui.main.viewmodel.MakeEntryViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var makeEntryViewModel: MakeEntryViewModel
    private val newEntryRequestCode = 1

    lateinit var adapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(mainToolBar)


        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rvNotes.layoutManager = layoutManager
        adapter = NotesAdapter(this)
        rvNotes.adapter = adapter

        onSwipeDelete()

        observeViewModel()
        showDrawer()

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
            val note = noteTitle?.let { noteText?.let { it1 -> Note(
                System.currentTimeMillis(),
                it,
                it1
            ) } }

            note?.let { makeEntryViewModel.insert(it) }

        } else {

            Toast.makeText(this, "Empty Can Not Be Saved ", Toast.LENGTH_SHORT).show()
        }
    }


    private fun onSwipeDelete() {
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

                showCustomDialog(resources.getString(R.string.are_you_sure), viewHolder)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemCallback)
        itemTouchHelper.attachToRecyclerView(rvNotes)

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
        makeEntryViewModel.allNotes.observe(this, Observer { notes ->
            notes?.let { adapter.setNotes(it) }
        })


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    private fun showDrawer() {

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this, drawer_layout, mainToolBar,
            R.string.open_nav_drawer, R.string.close_nav_drawer
        );

        drawer_layout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)


    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.id_home -> {
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.sortBy -> {
                /*   makeEntryViewModel.allNotes.observe(this, Observer { notes ->
                       notes?.let { adapter.setNotes(it) }
                   })*/

                showSortByDialog()


            }
            else -> {

                Toast.makeText(this, "Nothing to do", Toast.LENGTH_SHORT).show()
            }

        }
        return super.onOptionsItemSelected(item)

    }

    private fun showCustomDialog(title: String, viewHolder: RecyclerView.ViewHolder) {
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
            val note = adapter.getNoteAtPosition(position)
            note?.let { makeEntryViewModel.delete(it) }
            dialog.dismiss()
        }
        noBtn.setOnClickListener {
            adapter.notifyDataSetChanged()
            dialog.dismiss() }
        dialog.show()

        val layoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.getWindow()?.getAttributes())
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.getWindow()?.setAttributes(layoutParams)
    }




    private fun showSortByDialog() {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.sort_by_dialog)
        val sortTitle = dialog.findViewById(R.id.containerTitle) as ConstraintLayout

        val sortId = dialog.findViewById(R.id.containerId)as ConstraintLayout


        sortId.setOnClickListener {
            makeEntryViewModel.sortByIdNotes.observe(this, Observer { notes ->
                notes?.let {
                    adapter.setNotes(it)
                    adapter.notifyDataSetChanged()
                    dialog.dismiss()

                }
            })
        }

        sortTitle.setOnClickListener {
            makeEntryViewModel.sortByTitleNotes.observe(this, Observer { notes ->
                notes?.let {
                    adapter.setNotes(it)
                    adapter.notifyDataSetChanged()
                    dialog.dismiss()

                }
            })
        }



        dialog.show()

        val layoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.getWindow()?.getAttributes())
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.getWindow()?.setAttributes(layoutParams)
    }





}

