package com.codemonkeylabs.fpslibrary.sample

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by brianplummer on 8/30/15.
 */
class FpsSampleAdapter : RecyclerView.Adapter<FPSSampleViewHolder?>() {
    private var megaBytes = 1f

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FPSSampleViewHolder {
        val itemView = LayoutInflater.from(
            parent.context
        ).inflate(R.layout.sample_item, parent, false)
        return FPSSampleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FPSSampleViewHolder, position: Int) {
        holder.onBind(position, megaBytes)
    }

    override fun getItemCount(): Int = 255

    fun setMegaBytes(megaBytes: Float) {
        this.megaBytes = megaBytes
    }
}