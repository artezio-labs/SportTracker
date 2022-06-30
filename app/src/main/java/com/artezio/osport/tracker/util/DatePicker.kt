package com.artezio.osport.tracker.util

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*

object DatePicker {

    fun pickDateAndTime(context: Context, fragmentManager: FragmentManager): Long {
        Calendar.getInstance().apply {
            this.set(Calendar.SECOND,0)
            this.set(Calendar.MILLISECOND,0)
            context.let {
                MaterialDatePicker.Builder.datePicker().build()
            }
        }
        return 1
    }
}