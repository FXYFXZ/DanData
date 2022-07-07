package ru.sevenci.dandata

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ru.sevenci.dandata.databinding.ActivityEditPointBinding


class EditPointActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditPointBinding
    private lateinit var dbHelper: DBHelper
    private var theID: Int =0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPointBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent
        theID = intent.getIntExtra("ID",0)
        binding.fldID.text = "N$theID"
        binding.fldRem.setText(intent.getStringExtra("remark"))
        binding.fldValue.setText(intent.getIntExtra("Value",0).toString())

        dbHelper = DBHelper(this) // db ini

        binding.btnSave.setOnClickListener{
            // save data
            val database: SQLiteDatabase = dbHelper.writableDatabase
            val strSQL :String
            strSQL = "UPDATE ${DBHelper.TABLE_DATA}  SET ${DBHelper.KEY_VALUE}  = " + binding.fldValue.text + ", " +
            "  ${DBHelper.KEY_REM}  = '" + binding.fldRem.text + "'" +
            "  WHERE ${DBHelper.KEY_ID} = $theID"
            database.execSQL(strSQL)
            Log.d("myLog", strSQL)
            dbHelper.close()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }
}