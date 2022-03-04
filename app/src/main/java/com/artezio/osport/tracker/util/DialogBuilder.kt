package com.artezio.osport.tracker.util

import android.content.Context
import android.content.DialogInterface
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DialogBuilder(
    private val context: Context,
    private val title: String? = null,
    private val message: String? = "",
    private val positiveButtonText: String? = "",
    private val positiveButtonClick: DialogInterface.OnClickListener? = null,
    private val negativeButtonText: String? = "",
    private val negativeButtonClick: DialogInterface.OnClickListener? = null,
    private val layoutId: Int? = null,
) {
    fun build() = MaterialAlertDialogBuilder(context).apply {
            if (!title.isNullOrEmpty()) setTitle(title)
            if (!message.isNullOrEmpty()) setMessage(message)
            if (positiveButtonClick != null) setPositiveButton(positiveButtonText, positiveButtonClick)
            if (negativeButtonClick != null) setNegativeButton(negativeButtonText, negativeButtonClick)
            if (layoutId != null) setView(layoutId)
            show()
        }.create()

}