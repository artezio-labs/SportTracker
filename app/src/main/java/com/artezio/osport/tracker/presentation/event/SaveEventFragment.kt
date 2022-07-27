package com.artezio.osport.tracker.presentation.event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.databinding.FragmentSaveEventBinding
import com.artezio.osport.tracker.presentation.BaseFragment
import com.artezio.osport.tracker.util.DialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SaveEventFragment : BaseFragment<FragmentSaveEventBinding, SaveEventViewModel>() {

    override var bottomNavigationViewVisibility = View.GONE
    override val viewModel: SaveEventViewModel by viewModels()

    override var onBackPressed: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getLastEventName()
        binding.buttonSaveEvent.setOnClickListener {
            val eventName = binding.eventNameTIL.editText?.editableText.toString()
            viewModel.updateEvent(eventName)
            navigate()
        }
        binding.buttonDeleteEvent.setOnClickListener {
            val dialog = DialogBuilder(
                context = requireContext(),
                title = "Внимание!",
                message = "Вы действительно хотите удалить записанную сессию?",
                positiveButtonText = "Да",
                positiveButtonClick = { dialog, _ ->
                    viewModel.deleteLastEvent()
                    navigate()
                    dialog.dismiss()
                },
                negativeButtonText = "Отмена",
                negativeButtonClick = { dialog, _ -> dialog.cancel() },
                needsToShow = true
            ).build()
        }
        viewModel.eventNameLiveData.observe(viewLifecycleOwner) {
            binding.eventNameTIL.editText?.setText(it)
        }
    }
    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSaveEventBinding = FragmentSaveEventBinding.inflate(inflater, container, false)

    private fun navigate() {
        findNavController().navigate(R.id.action_saveEventFragment2_to_mainFragment)
    }
}
