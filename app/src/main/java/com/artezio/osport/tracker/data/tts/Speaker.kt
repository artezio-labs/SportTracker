package com.artezio.osport.tracker.data.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.*

class Speaker(
    private val context: Context,
    private val listener: TextToSpeech.OnInitListener
) : ISpeaker {

    override val speaker: TextToSpeech
        get() = TextToSpeech(context, listener)

    override fun speak(phrase: Phrase) {
        Log.d("text_to_speech", "TTS speaks")
        speaker.speak(phrase.text, TextToSpeech.QUEUE_FLUSH, phrase.params, phrase.id)
    }

    override fun onInit(status: Int): Boolean {
        Log.d("text_to_speech", "onInit: ")
        return if (status == TextToSpeech.SUCCESS) {
            Log.d("text_to_speech", "Status successed ")
            val locale = Locale.ENGLISH
            val result = speaker.setLanguage(locale)
            Log.d("text_to_speech", "Russian lang is not exists: ${(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)}")
            !(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
        } else {
            false
        }
    }

    override fun onDestroy() {
        if (speaker != null) {
            speaker.stop()
            speaker.shutdown()
        }
    }
}