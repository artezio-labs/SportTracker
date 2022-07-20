package com.artezio.osport.tracker.presentation.main.recycler.finished_events

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.artezio.osport.tracker.presentation.main.recycler.Item

abstract class BaseViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: Item)
}