package com.artezio.osport.tracker.presentation

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.presentation.event.SaveEventViewModel
import com.artezio.osport.tracker.util.DialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BackToMainDialog : DialogFragment() {

    private val viewmodel: SaveEventViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d("navigation", "Dialog was shown")
        return DialogBuilder(
            context = requireActivity(),
            "Внимание",
            "Вы уверены, что хотите вернуться на главный экран, не сохранив данные? Все несохраненные данные будут утеряны!",
            positiveButtonText = "Да",
            positiveButtonClick = { _, _ ->
                viewmodel.deleteLastEvent()
                findNavController().navigate(R.id.action_backToMainDialog_to_mainFragment)
                this.dialog?.dismiss()

            },
            negativeButtonText = "Не сейчас",
            negativeButtonClick = { _, _ ->
                this.dialog?.dismiss()
            }
        ).build()
    }
}