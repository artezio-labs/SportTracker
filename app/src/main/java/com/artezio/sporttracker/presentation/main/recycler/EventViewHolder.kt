package com.artezio.sporttracker.presentation.main.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import com.artezio.sporttracker.databinding.EventRecyclerItemBinding
import com.artezio.sporttracker.databinding.EventTypeRecyclerItemBinding
import com.artezio.sporttracker.util.millisecondsToDateFormat

class EventViewHolder(private val binding: EventRecyclerItemBinding) :
    BaseViewHolder(binding.root) {

    constructor(parent: ViewGroup) : this(
        EventRecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun bind(item: Item) {
        val event = item as Item.Event
        binding.textViewEventTitle.text = event.eventName
        binding.textViewEventStartDate.text = millisecondsToDateFormat(event.startDate)
        binding.textViewEventEndDate.text =
            if (event.endDate != null) millisecondsToDateFormat(event.endDate) else ""
    }
}