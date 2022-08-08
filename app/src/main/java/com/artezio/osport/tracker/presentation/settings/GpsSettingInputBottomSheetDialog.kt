package com.artezio.osport.tracker.presentation.settings

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.artezio.osport.tracker.R
import com.artezio.osport.tracker.databinding.GpsSettingInputBottomSheetDialogLayoutBinding
import com.artezio.osport.tracker.util.ValidationUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GpsSettingInputBottomSheetDialog : BottomSheetDialogFragment() {

    private val binding: GpsSettingInputBottomSheetDialogLayoutBinding by lazy {
        GpsSettingInputBottomSheetDialogLayoutBinding.inflate(layoutInflater)
    }

    private val viewModel: SettingsViewModel by viewModels()

    private var gpsSettingFlag: Boolean = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        gpsSettingFlag = arguments?.getBoolean("gps_setting") ?: false

        setValues(gpsSettingFlag)

        binding.buttonSaveSetting.setOnClickListener {
            if (validateInput(gpsSettingFlag)) {
                lifecycleScope.launch {
                    viewModel.saveSetting(
                        gpsSettingFlag,
                        binding.inputValueTIL.editText?.editableText.toString().toInt()
                    )
                }
                this.dismiss()
            }
        }

        binding.buttonCancel.setOnClickListener {
            this.dismiss()
        }

        return BottomSheetDialog(requireContext(), theme).apply {
            setContentView(binding.root)
            setCanceledOnTouchOutside(false)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun validateInput(flag: Boolean): Boolean {
        return if (flag) {
            if (ValidationUtils.isInRange(
                    binding.inputValueTIL.editText?.editableText.toString(),
                    1,
                    60
                )
            ) {
                true
            } else {
                binding.inputValueTIL.error = getString(R.string.freq_setting_error_text)
                false
            }
        } else {
            if (ValidationUtils.isInRange(
                    binding.inputValueTIL.editText?.editableText.toString(),
                    3,
                    200
                )
            ) {
                true
            } else {
                binding.inputValueTIL.error = getString(R.string.distance_setting_error_text)
                false
            }
        }
    }

    private fun setValues(flag: Boolean) {
        binding.bottomSheetDialogTitle.text = viewModel.setTitle(flag)
        binding.inputValueTIL.hint = viewModel.setHint(flag)
        lifecycleScope.launch {
            viewModel.getSettingValue(flag).collect {
                binding.inputValueTIL.editText?.setText(it.toString())
            }
        }
    }

    companion object {
        const val TAG: String = "GpsSettingInputBottomSheetDialog"
    }

}