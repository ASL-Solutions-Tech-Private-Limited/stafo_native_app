package com.asl_emp_mng.app.utils

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.speech.tts.TextToSpeech
import java.util.Locale

class TTSWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams), TextToSpeech.OnInitListener {

    private lateinit var textToSpeech: TextToSpeech

    override fun doWork(): Result {
        val speakText = inputData.getString("speakText")

        if (speakText != null) {
            textToSpeech = TextToSpeech(applicationContext, this)
        }

        return Result.success()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(Locale("hi", "IN"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Language not supported
            } else {
                textToSpeech.speak(inputData.getString("speakText"), TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }
}
