package com.artezio.osport.tracker.presentation.main.recycler.planned_events

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.artezio.osport.tracker.databinding.PlannedEventRecyclerItemBinding
import com.artezio.osport.tracker.presentation.main.IFragment
import com.artezio.osport.tracker.presentation.main.PlannedTracksFragment
import com.artezio.osport.tracker.presentation.main.recycler.Item
import com.artezio.osport.tracker.presentation.main.recycler.finished_events.BaseViewHolder
import com.artezio.osport.tracker.presentation.tracker.ScheduleTrackingBottomSheetDialog
import com.artezio.osport.tracker.util.UNIQUE_WORK_NAME
import com.artezio.osport.tracker.util.WORK_TAG
import com.artezio.osport.tracker.util.millisecondsToDateFormat
import kotlinx.coroutines.*

class PlannedEventsRecyclerAdapter(
    private val fragment: IFragment,
    private val onDeleteButtonClick: suspend (id: Long) -> Unit
) : RecyclerView.Adapter<PlannedEventsRecyclerAdapter.PlannedEventsViewHolder>() {

    val scope = CoroutineScope(Dispatchers.IO) + SupervisorJob()

    var list = emptyList<Item>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    inner class PlannedEventsViewHolder(private val binding: PlannedEventRecyclerItemBinding) :
        BaseViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(
            PlannedEventRecyclerItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        override fun bind(item: Item) {
            val event = item as Item.PlannedEvent
            binding.textViewEventTitle.text = event.eventName
            binding.textViewEventStartDate.text = millisecondsToDateFormat(event.startDate)
            binding.textViewEventEndDate.text =
                if (event.endDate != null) millisecondsToDateFormat(event.endDate) else ""
            binding.root.setOnClickListener {
                showBottomSheetDialog(event)
            }
            binding.imageViewDeleteButton.setOnClickListener {
                deletePlannedEvent(event)
            }
            val isEnqueued =
                WorkManager.getInstance((fragment as PlannedTracksFragment).requireContext())
                    .getWorkInfosForUniqueWork(
                        "${UNIQUE_WORK_NAME}_${event.id}"
                    ).get().map { it.state }.all { it == WorkInfo.State.ENQUEUED }
            Log.d("planner_worker_states", "Worker states: $isEnqueued")
            binding.textViewIsPlanned.text = if (isEnqueued) {
                "Запланировано ✔"
            } else {
                "Не запланировано ❌"
            }
        }

        private fun deletePlannedEvent(item: Item.PlannedEvent) {
            scope.launch { onDeleteButtonClick.invoke(item.id) }
            WorkManager.getInstance((fragment as PlannedTracksFragment).requireContext())
                .cancelAllWorkByTag(WORK_TAG)
        }

        private fun showBottomSheetDialog(event: Item.PlannedEvent) {
            val bottomSheet = ScheduleTrackingBottomSheetDialog().apply {
                arguments = bundleOf(
                    "eventId" to event.id,
                    "eventName" to event.eventName,
                    "startDate" to event.startDate,
                    "endDate" to event.endDate,
                    "exists" to true
                )
            }
            Log.d("planner_worker_states", "Event id: ${event.id}")
            bottomSheet.show(
                (fragment as PlannedTracksFragment).childFragmentManager,
                ScheduleTrackingBottomSheetDialog.TAG
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlannedEventsViewHolder {
        return PlannedEventsViewHolder(parent)
    }

    override fun onBindViewHolder(holder: PlannedEventsViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}