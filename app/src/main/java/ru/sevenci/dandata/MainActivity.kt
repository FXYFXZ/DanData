package ru.sevenci.dandata

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.ColorSpace
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import ru.sevenci.dandata.databinding.ActivityMainBinding
import java.util.*
import kotlin.time.seconds


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DBHelper
    private var listPoints : MutableList<PointValueHolder> = mutableListOf(PointValueHolder())
    lateinit var timerAdapter : TimeGridAdapter
    var seconds = 60
    var minutes = 10
    var alertData = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbHelper = DBHelper(this) // db ini


        timerAdapter = TimeGridAdapter(this, listPoints)
        binding.spPoints.adapter = timerAdapter
        binding.btnReset.setBackgroundColor(Color.RED)

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
                    seconds = curTime.get(Calendar.SECOND)
                    minutes = curTime.get(Calendar.MINUTE)
                    (String.format("%02d", minutes) + ":" + String.format("%02d", seconds)).also { binding.fldTime.text = it }
                    if (seconds == 0) alertData = true
                    binding.btnReset.visibility = if (alertData) View.VISIBLE else View.INVISIBLE
                }
            }
        }, 0, 1000)




    } // ~On CREATE

    override fun onStart() {  // START
        super.onStart()
        try {
            updatePoints()
        }catch(e: Exception){

        }

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

} // CLASS