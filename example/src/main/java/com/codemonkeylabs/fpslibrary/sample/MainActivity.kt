package com.codemonkeylabs.fpslibrary.sample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codemonkeylabs.fpslibrary.TinyDancer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val tinyDancer by lazy { TinyDancer.Builder.create(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        recyclerView.run {
            val layoutManager = LinearLayoutManager(context)
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL)
            setLayoutManager(layoutManager)
            addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
            adapter = FpsSampleAdapter()
        }

        setupRadioGroup()
        tinyDancer.show()

        start.setOnClickListener { tinyDancer.show() }
        stop.setOnClickListener { tinyDancer.hide() }
    }

    private fun setupRadioGroup() {
        loadIndicator.progress = 50
        loadIndicator.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                i: Int,
                b: Boolean
            ) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }
}