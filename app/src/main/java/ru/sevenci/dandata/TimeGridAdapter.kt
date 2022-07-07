package ru.sevenci.dandata

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.util.*

//
class PointValueHolder(
    var ID: Int = 1,
    var timestamp: String = "",
    var rvalue: Int = 0,
    var rem: String = ""
)

// Адаптор для отображения значений
class TimeGridAdapter (var context: Context, var timerList: List<PointValueHolder>) : BaseAdapter() {
    var inFlater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return timerList.count()
    }

    override fun getItem(position: Int): Any {
        return timerList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val newView: View
        if (convertView == null) {
            newView = inFlater.inflate(R.layout.timerow, null)
        } else newView = convertView

        val ordNum = newView.findViewById<View>(R.id.tvID) as TextView
        ordNum.text = "N" + timerList[position].ID.toString()

        val value = newView.findViewById<View>(R.id.tvValue) as TextView
        value.text = timerList[position].rvalue.toString()

//        val ts = newView.findViewById<View>(R.id.tvTS) as TextView
//        ts.text = timerList[position].timestamp

        val rem = newView.findViewById<View>(R.id.tvRem) as TextView
        rem.text = timerList[position].rem

        return newView

    }

}