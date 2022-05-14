package com.artezio.osport.tracker.presentation

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.util.DialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BackToMainDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d("navigation", "Dialog was shown")
        return DialogBuilder(
            context = requireActivity(),
            "Внимание",
            "Вы уверены, что хотите вернуться на главный экран, не сохранив данные? Все несохраненные данные будут утеряны!",
            positiveButtonText = "Да",
            positiveButtonClick = { dialog, _ ->
                findNavController().navigate(R.id.action_backToMainDialog_to_mainFragment)
                this.dialog?.dismiss()

            },
            negativeButtonText = "Не сейчас",
            negativeButtonClick = { dialog, _ ->
                this.dialog?.dismiss()
            }
        ).build()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }
}