package ru.sevenci.dandata

import android.app.AlertDialog
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import ru.sevenci.dandata.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() { // CLASS

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DBHelper
    private var listPoints : MutableList<PointValueHolder> = mutableListOf(PointValueHolder())
    lateinit var timerAdapter : TimeGridAdapter
    var secondsoffset = 0
    var seconds = 60
    var minutes = 10
    var alertData = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbHelper = DBHelper(this) // db ini

        // ListPoints
        timerAdapter = TimeGridAdapter(this, listPoints)
        binding.lvPoints.adapter = timerAdapter
        binding.btnReset.setBackgroundColor(Color.RED)
        registerForContextMenu(binding.lvPoints)

        binding.data.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                if (binding.data.text.isNotEmpty()) {
                    val rv = binding.data.text.toString().toInt()
                    insertData(rv, binding.fldRem.text.toString())
                    binding.data.text.clear()
                    binding.fldRem.text.clear()
                    alertData = false
                }
                return@setOnEditorActionListener true
            }
            false
        }

        // Timer
        val t = Timer()
        t.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    val curTime = Calendar.getInstance()
                    curTime.add(Calendar.SECOND, secondsoffset*-1)
                    seconds = curTime.get(Calendar.SECOND)
                    minutes = curTime.get(Calendar.MINUTE)
                    (String.format("%02d", minutes) + ":" + String.format("%02d", seconds)).also { binding.fldTime.text = it }
                    if (seconds == 0) alertData = true
                    binding.btnReset.visibility = if (alertData) View.VISIBLE else View.INVISIBLE
                }
            }
        }, 0, 1000)
    } // ~On CREATE

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.topmenu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onStart() {  // START
        super.onStart()
        try {
            updatePoints()
        }catch(e: Exception){

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuResetTimer -> {
                val curTime = Calendar.getInstance()
                secondsoffset = curTime.get(Calendar.SECOND)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        if (v?.id==R.id.lvPoints) {
            menu?.add(0, 0, 1, "Delete")
            menu?.add(0, 1, 1, "Edit")
        }
        else
            super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if  (item.groupId == 0) {
            val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
            val index = info.position
            when (item.itemId) {
                0 -> deletePoint(index)
                1 -> editPoint(index)
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun deletePoint(myIndex: Int){
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setMessage("Are you sure you want to Delete?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                // Delete selected  from database
                val tmr = timerAdapter.getItem(myIndex) as PointValueHolder
                Log.d("myLog", "index: ${tmr.ID}")
                val database: SQLiteDatabase = dbHelper.writableDatabase
                database.execSQL("DELETE FROM ${DBHelper.TABLE_DATA} WHERE ${DBHelper.KEY_ID} = ${tmr.ID}")
                dbHelper.close()
                updatePoints()
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.dismiss() // Dismiss the dialog
            }
        val alert = builder.create()
        alert.show()
    }

    // открываем для редактирования
    private fun editPoint(myIndex: Int){

        updatePoints()
    }


    private fun updatePoints(){
        val database: SQLiteDatabase = dbHelper.writableDatabase
        val cursor: Cursor =
            database.rawQuery("SELECT * FROM " + DBHelper.TABLE_DATA +
                    " ORDER BY id DESC LIMIT 50 ", null)

        listPoints.clear()
        if (cursor.moveToFirst()) {
             cursor.getColumnIndex(DBHelper.KEY_ID)
            val idIndex: Int = cursor.getColumnIndex(DBHelper.KEY_ID)
            val valIndex: Int = cursor.getColumnIndex(DBHelper.KEY_VALUE)
            val renIndex: Int = cursor.getColumnIndex(DBHelper.KEY_REM)
            do {
                val pvh = PointValueHolder()
                pvh.ID = cursor.getInt(idIndex)
                pvh.rvalue = cursor.getInt(valIndex)
                pvh.rem = cursor.getString(renIndex)
                listPoints.add(pvh)
            } while (cursor.moveToNext())
        } else Log.d("mLog", "0 rows")
        cursor.close()
        dbHelper.close()
        timerAdapter.notifyDataSetChanged()
    }


    private fun insertData ( value: Int, rem : String){
        val contentValues = ContentValues()
        val database: SQLiteDatabase = dbHelper.writableDatabase
        contentValues.put(DBHelper.KEY_VALUE, value)
        contentValues.put(DBHelper.KEY_REM, rem)
        database.insert(DBHelper.TABLE_DATA, null, contentValues)
        dbHelper.close()
        updatePoints()
    }

}