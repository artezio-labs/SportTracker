package com.artezio.osport.tracker.presentation.event

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.databinding.FragmentEventInfoBinding
import com.artezio.osport.tracker.presentation.BaseFragment
import com.artezio.osport.tracker.util.DialogBuilder
import com.artezio.osport.tracker.util.MapUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class EventInfoFragment : BaseFragment<FragmentEventInfoBinding, EventInfoViewModel>() {

    override var bottomNavigationViewVisibility = View.GONE
    override var onBackPressed: Boolean = false

    override val viewModel: EventInfoViewModel by viewModels()
    private val navArgs: EventInfoFragmentArgs by navArgs()
    private var file: File? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonClose.setOnClickListener {
            viewModel.goBackToMainFragment()
        }
        val id = navArgs.eventId

        viewModel.getLocationsById(id)
        viewModel.locationsLiveData.observe(viewLifecycleOwner) { locations ->
            MapUtils.drawRoute(requireContext(), binding.eventInfoMap, locations)
        }

        viewModel.getEventInfo(id)
        viewModel.eventInfoLiveData.observe(viewLifecycleOwner) { eventInfo ->
            binding.eventTitle.setText(eventInfo.title)
            binding.materialTextViewTimeValue.text = eventInfo.time
            binding.materialTextViewDistanceValue.text = eventInfo.distance
            binding.materialTextViewSpeedValue.text = eventInfo.speed
            binding.materialTextViewTempoValue.text = eventInfo.tempo
            binding.materialTextViewStepsValue.text = eventInfo.steps
            binding.materialTextViewGPSValue.text = eventInfo.gpsPoints
        }
        lifecycleScope.launch {
            file = viewModel.writeGpx(id).await()
        }

        binding.eventTitle.addTextChangedListener { eventName ->
            viewModel.updateEventName(id, eventName.toString())
        }
        binding.imageViewMenu.setOnClickListener { menuButton ->
            val menu = PopupMenu(requireActivity(), menuButton)
            menu.inflate(R.menu.event_info_menu)
            menu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.delete -> {
                        DialogBuilder(
                            context = requireContext(),
                            title = "Внимание",
                            message = "Вы уверены, что хотите удалить тренировку? Все данные будут утеряны!",
                            positiveButtonText = "Да",
                            positiveButtonClick = { dialog, _ ->
                                viewModel.deleteEvent(id)
                                dialog.dismiss()
                                viewModel.goBackToMainFragment()
                            },
                            negativeButtonText = "Не сейчас",
                            negativeButtonClick = { dialog, _ -> dialog.cancel() }
                        ).build()
                    }
                    R.id.share -> {
                        file?.let { shareFile(it) }
                    }
                }
                false
            }
            menu.show()
        }
    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEventInfoBinding =
        FragmentEventInfoBinding.inflate(inflater, container, false)

    private fun shareFile(file: File) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/gpx"
            putExtra(
                Intent.EXTRA_STREAM,
                FileProvider.getUriForFile(requireContext(), "com.artezio.osport.tracker", file)
            )
        }
        startActivity(Intent.createChooser(intent, "Поделиться"))
    }
}