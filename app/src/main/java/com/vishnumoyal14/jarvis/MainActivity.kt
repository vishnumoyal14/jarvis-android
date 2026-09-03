package com.vishnumoyal14.jarvis

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {

    private lateinit var status: TextView
    private lateinit var tts: TextToSpeech

    companion object {
        private const val REQUEST_AUDIO = 100
        private const val REQUEST_VOICE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this, this)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 80, 40, 40)

        val title = TextView(this)
        title.text = "JARVIS"
        title.textSize = 36f

        status = TextView(this)
        status.text = "System online.\nReady for your command."
        status.textSize = 20f

        val button = Button(this)
        button.text = "🎙️ ACTIVATE JARVIS"
        button.textSize = 18f

        layout.addView(title)
        layout.addView(status)
        layout.addView(button)

        setContentView(layout)

        button.setOnClickListener {
            startVoiceInput()
        }
    }

    override fun onInit(statusCode: Int) {
        if (statusCode == TextToSpeech.SUCCESS) {
            tts.language = Locale.getDefault()
        }
    }

    private fun startVoiceInput() {

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_AUDIO
            )
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            "Speak to JARVIS"
        )

        status.text = "🎙️ Listening..."

        startActivityForResult(intent, REQUEST_VOICE)
    }

    @Deprecated("Deprecated in Android API, but still supported")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_VOICE && resultCode == RESULT_OK) {

            val results =
                data?.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                )

            val command = results?.firstOrNull()

            if (command != null) {

                status.text =
                    "You said:\n\"$command\"\n\nJARVIS heard you."

                speak("I heard you. How can I help?")
            } else {

                status.text = "I couldn't understand that."

                speak("Sorry, I couldn't understand that.")
            }
        }
    }

    private fun speak(text: String) {
        tts.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "JARVIS_RESPONSE"
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == REQUEST_AUDIO &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            startVoiceInput()
        } else if (requestCode == REQUEST_AUDIO) {
            status.text = "Microphone permission is required."
        }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }

        super.onDestroy()
    }
}
