package com.vishnumoyal14.jarvis

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 80, 40, 40)

        val title = TextView(this)
        title.text = "JARVIS"
        title.textSize = 36f

        val status = TextView(this)
        status.text = "System online.\nReady for your command."
        status.textSize = 20f

        val button = Button(this)
        button.text = "ACTIVATE JARVIS"
        button.textSize = 18f

        layout.addView(title)
        layout.addView(status)
        layout.addView(button)

        setContentView(layout)
    }
}
