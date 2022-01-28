package com.artezio.sporttracker.presentation.main.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import com.artezio.sporttracker.databinding.EventTypeRecyclerItemBinding

class EventTypeHeaderViewHolder(private val binding: EventTypeRecyclerItemBinding) :
    BaseViewHolder(binding.root) {

    constructor(parent: ViewGroup) : this(
        EventTypeRecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun bind(item: Item) {
        binding.textViewEventType.text = (item as Item.EventTypeHeader).title
    }
}