package com.shaluambasta.speechandhearingapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import java.util.*

class MainActivity : AppCompatActivity() {


    private lateinit var etSpeechInput: TextInputLayout
    private lateinit var textSpeechOutput: MaterialTextView
    private lateinit var btnTalk: MaterialButton
    private lateinit var btnListen: MaterialButton

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var textToSpeech: TextToSpeech
    private var etInput: String? = null
    private val TAG = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etSpeechInput = findViewById(R.id.et_speech_input)
        textSpeechOutput = findViewById(R.id.text_speech_output)
        btnTalk = findViewById(R.id.btn_talk)
        btnListen = findViewById(R.id.btn_listen)



        btnTalk.setOnClickListener {

            etSpeechInput.visibility = View.VISIBLE
            textSpeechOutput.visibility = View.GONE

        }

        etSpeechInput.setEndIconOnClickListener { _ ->

            if (checkPermissions()) {

                etInput = etSpeechInput.editText?.text.toString()

                if (etInput.isNullOrEmpty()) {

                    Toast.makeText(this, "Give Me Some Text :(", Toast.LENGTH_LONG).show()
                    etSpeechInput.requestFocus()

                } else {

                    etSpeechInput.clearFocus()
                    textToSpeech(etInput!!)

                }
            } else {

                Toast.makeText(this, "Allow Microphone Permission", Toast.LENGTH_SHORT).show()
            }
        }

        btnListen.setOnClickListener {

            if (checkPermissions()) {

                etSpeechInput.visibility = View.GONE
                textSpeechOutput.visibility = View.VISIBLE
                speechToText()

            } else {

                Toast.makeText(this, "Allow Microphone Permission", Toast.LENGTH_SHORT).show()
            }

        }

    }


    private fun checkPermissions(): Boolean {

        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            val permissions = arrayOf(
                Manifest.permission.RECORD_AUDIO
            )
            ActivityCompat.requestPermissions(this, permissions, 0)
            false

        } else {
            true
        }
    }


    private fun textToSpeech(text: String) {

        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {

                textToSpeech.language = Locale.UK
                textToSpeech.setSpeechRate(1.0f)
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null)

            }
        }

    }


    private fun speechToText() {

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {

            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Please Speak!")

        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d(TAG, "onReadyForSpeech")
            }

            override fun onBeginningOfSpeech() {
                Log.d(TAG, "onBeginningOfSpeech")
            }

            override fun onRmsChanged(rmsdB: Float) {
                Log.d(TAG, "onRmsChanged")
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                Log.d(TAG, "onBufferReceived")
            }

            override fun onEndOfSpeech() {
                Log.d(TAG, "onEndOfSpeech")
            }

            override fun onError(error: Int) {

                val errorMessage: String = getErrorText(error)
                Log.d(TAG, "FAILED $errorMessage")
                Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()

            }

            override fun onResults(results: Bundle?) {

                val result = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (result != null) {
                    textSpeechOutput.text = result[0]
                }

            }

            override fun onPartialResults(partialResults: Bundle?) {
                Log.d(TAG, "onPartialResults")
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                Log.d(TAG, "onEvent")
            }


        })

        speechRecognizer.startListening(speechRecognizerIntent)

    }


    private fun getErrorText(error: Int): String {
        var message = ""
        message = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No match"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
            SpeechRecognizer.ERROR_SERVER -> "Error from server"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Didn't understand, please try again :("
        }
        return message
    }

}