package com.codemonkeylabs.fpslibrary.sample

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.SystemClock
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by brianplummer on 8/30/15.
 */
class FPSSampleViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    private val data: IntArray
    private val colorImg: ImageView
    private val bindTime: TextView
    fun onBind(value: Int, megaBytes: Float) {
        configureColor(value)
        val total = (megaBytes * 100f).toInt()
        val start = SystemClock.elapsedRealtime()
        val startInt = start.toInt()
        for (i in 0 until total) {
            for (e in data.indices) {
                // set dummy value (start time)
                data[e] = startInt
            }
        }
        val end = SystemClock.elapsedRealtime()
        val bindTimeMs = end - start
        bindTime.text = bindTimeMs.toString() + "ms onBind()"
    }

    private fun configureColor(value: Int) {
        val multiplier = 22
        val hundred = value / 100
        val tens = (value - hundred * 100) / 10
        val ones = value - hundred * 100 - tens * 10
        val r = hundred * multiplier
        val g = tens * multiplier
        val b = ones * multiplier
        val colorVal = Color.rgb(r, g, b)
        colorImg.setImageDrawable(ColorDrawable(colorVal))
    }

    init {
        colorImg =
            itemView.findViewById<View>(R.id.colorImg) as ImageView
        bindTime = itemView.findViewById<View>(R.id.bindTime) as TextView
        data = IntArray(1024 * 10)
    }
}