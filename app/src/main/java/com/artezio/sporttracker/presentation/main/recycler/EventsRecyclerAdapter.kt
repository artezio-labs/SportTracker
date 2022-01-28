package com.artezio.sporttracker.presentation.main.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.artezio.sporttracker.domain.model.Event

class EventsRecyclerAdapter: ListAdapter<Item, BaseViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        when(viewType) {
            EVENT_TYPE_ITEM_TYPE -> EventTypeHeaderViewHolder(parent)
            else -> EventViewHolder(parent)
        }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when(val item = getItem(position)) {
            is Item.EventTypeHeader -> (holder as EventTypeHeaderViewHolder).bind(item)
            is Item.Event -> (holder as EventViewHolder).bind(item)
        }
    }



    companion object {
        const val EVENT_TYPE_ITEM_TYPE = 1
        const val EVENT_ITEM_TYPE = 2
    }

}

object DiffCallback : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return areItemsTheSame(oldItem, newItem)
    }

}