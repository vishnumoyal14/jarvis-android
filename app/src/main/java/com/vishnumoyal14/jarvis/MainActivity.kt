package com.vishnumoyal14.jarvis

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.provider.Settings
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
                    "You said:\n\"$command\"\n\nProcessing..."

                processCommand(command.lowercase(Locale.getDefault()))
            } else {
                status.text = "I couldn't understand that."
                speak("Sorry, I couldn't understand that.")
            }
        }
    }

    private fun processCommand(command: String) {

    when {
        command.contains("settings") ||
        command.contains("setting") ||
        command.contains("सेटिंग") -> {
            openSettings()
        }

        command.contains("home") ||
        command.contains("होम") -> {
            goHome()
        }

        command.contains("back") ||
        command.contains("पीछे") -> {
            goBack()
        }

        command.contains("increase volume") ||
        command.contains("volume बढ़ा") ||
        command.contains("आवाज़ बढ़ा") -> {
            changeVolume(AudioManager.ADJUST_RAISE)
        }

        command.contains("decrease volume") ||
        command.contains("volume कम") ||
        command.contains("आवाज़ कम") -> {
            changeVolume(AudioManager.ADJUST_LOWER)
        }

        command.contains("open ") ||
        command.contains(" kholo") ||
        command.contains("खोलो") -> {
            openInstalledApp(command)
        }

        else -> {
            status.text =
                "You said:\n\"$command\"\n\nI don't know that command yet."
            speak("I heard you, but I don't know that command yet.")
        }
    }
    }

    private fun openApp(packageName: String, appName: String) {

        val intent = packageManager.getLaunchIntentForPackage(packageName)

        if (intent != null) {
            startActivity(intent)
            status.text = "Opening $appName..."
            speak("Opening $appName")
        } else {
            status.text = "$appName is not installed."
            speak("$appName is not installed.")
        }
    }

    private fun openSettings() {
        startActivity(Intent(Settings.ACTION_SETTINGS))
        status.text = "Opening Settings..."
        speak("Opening Settings")
    }

    private fun goHome() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)

        speak("Going home")
    }

    private fun goBack() {
    @Suppress("DEPRECATION")
    onBackPressed()
    speak("Going back")
    }

    private fun changeVolume(direction: Int) {

        val audioManager =
            getSystemService(AUDIO_SERVICE) as AudioManager

        audioManager.adjustVolume(
            direction,
            AudioManager.FLAG_SHOW_UI
        )

        if (direction == AudioManager.ADJUST_RAISE) {
            speak("Volume increased")
        } else {
            speak("Volume decreased")
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
