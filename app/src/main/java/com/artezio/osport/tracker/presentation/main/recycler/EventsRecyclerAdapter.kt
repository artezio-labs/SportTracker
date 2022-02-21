package com.artezio.osport.tracker.presentation.main.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.databinding.EventRecyclerItemBinding
import com.artezio.osport.tracker.presentation.event.EventCreateAndUpdateFragmentArgs
import com.artezio.osport.tracker.presentation.main.IFragment
import com.artezio.osport.tracker.presentation.main.MainFragment
import com.artezio.osport.tracker.presentation.statistics.StatisticsFragmentArgs
import com.artezio.osport.tracker.util.millisecondsToDateFormat

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
                val args = EventCreateAndUpdateFragmentArgs(
                    eventId = event.id,
                    eventName = event.eventName,
                    eventStartDate = event.startDate,
                    eventEndDate = event.endDate ?: -1L,
                    title = "Редактировать"
                )
                (fragment as MainFragment).findNavController().navigate(
                    R.id.action_mainFragment_to_eventCreateAndUpdateFragment,
                    args.toBundle()
                )
            }


            binding.buttonShowStatistics.setOnClickListener {
                val args = StatisticsFragmentArgs(
                    eventId = event.id
                )
                (fragment as MainFragment).findNavController().navigate(
                    R.id.action_mainFragment_to_statisticsFragment,
                    args.toBundle()
                )
            }
        }
    }
}