package com.artezio.sporttracker.presentation.main.recycler

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.artezio.sporttracker.R
import com.artezio.sporttracker.databinding.EventRecyclerItemBinding
import com.artezio.sporttracker.databinding.EventShowAndUpdateDialogLayoutBinding
import com.artezio.sporttracker.databinding.EventTypeRecyclerItemBinding
import com.artezio.sporttracker.domain.model.Event
import com.artezio.sporttracker.presentation.event.EventCreateAndUpdateFragment
import com.artezio.sporttracker.presentation.main.IFragment
import com.artezio.sporttracker.presentation.main.MainFragment
import com.artezio.sporttracker.util.millisecondsToDateFormat

class EventsRecyclerAdapter(
    private val fragment: IFragment
) : RecyclerView.Adapter<EventsRecyclerAdapter.EventViewHolder>() {

    var list = emptyList<Item>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder =
        EventViewHolder(parent)

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class EventViewHolder(private val binding: EventRecyclerItemBinding) :
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

            binding.root.setOnClickListener {
                (fragment as MainFragment).parentFragmentManager.beginTransaction().apply {
                    setReorderingAllowed(true)
                    replace(R.id.fragmentContainerView, EventCreateAndUpdateFragment().apply {
                        arguments = bundleOf(
                            "eventId" to event.id,
                            "eventName" to event.eventName,
                            "eventStartDate" to event.startDate,
                            "eventEndDate" to event.endDate
                        )
                    })
                    commit()
                }
            }

        }
    }
}