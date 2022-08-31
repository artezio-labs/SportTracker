package com.artezio.osport.tracker.data.tts

import android.speech.tts.TextToSpeech

interface ISpeaker {
    val speaker: TextToSpeech
    fun speak(phrase: Phrase)
    fun onInit(status: Int): Boolean
    fun onDestroy()
}