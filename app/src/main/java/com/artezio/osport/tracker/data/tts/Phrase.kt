package com.artezio.osport.tracker.data.tts

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.core.os.bundleOf

data class Phrase(
    val id: String,
    val text: String,
    val params: Bundle? = bundleOf(TextToSpeech.Engine.KEY_PARAM_VOLUME to 0.6F),
)
