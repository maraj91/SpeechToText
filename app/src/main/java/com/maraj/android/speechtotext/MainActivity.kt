package com.maraj.android.speechtotext

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.content.Intent
import android.content.pm.PackageManager
import android.Manifest.permission
import android.Manifest.permission.RECORD_AUDIO
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.speech.SpeechRecognizer
import android.view.MotionEvent
import kotlinx.android.synthetic.main.activity_main.*
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import java.util.*
import android.system.Os.shutdown




class MainActivity : AppCompatActivity() {

    //text to speech
    private lateinit var mTextToSpeech : TextToSpeech

    private lateinit var mSpeechRecognizer: SpeechRecognizer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermission()

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        val mSpeechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        mSpeechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        mSpeechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        )

        mSpeechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {

            }

            override fun onBeginningOfSpeech() {

            }

            override fun onRmsChanged(v: Float) {

            }

            override fun onBufferReceived(bytes: ByteArray) {

            }

            override fun onEndOfSpeech() {

            }

            override fun onError(i: Int) {

            }

            override fun onResults(bundle: Bundle) {
                //getting all the matches
                val matches = bundle
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

                //displaying the first match
                if (matches != null)
                    editText.setText(matches[0])
            }

            override fun onPartialResults(bundle: Bundle) {

            }

            override fun onEvent(i: Int, bundle: Bundle) {

            }
        })

        button.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_UP -> {
                    mSpeechRecognizer.stopListening()
                    //when the user removed the finger
                    editText.hint = "You will see input here"
                }
                MotionEvent.ACTION_DOWN -> {
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent)
                    //finger is on the button
                    editText.setText("")
                    editText.hint = "Listening..."
                }
            }
            false
        }

        mTextToSpeech = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                mTextToSpeech.language = Locale.UK
            }
        })

        Handler().postDelayed({
            val msg = "Hold button and speak, Release to get result."
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mTextToSpeech.speak(msg,TextToSpeech.QUEUE_FLUSH,null,null)
            } else {
                mTextToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null)
            }
        },1000)

    }

    public override fun onPause() {
        if (mTextToSpeech != null) {
            mTextToSpeech.stop()
            mTextToSpeech.shutdown()
        }
        super.onPause()
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
                finish()
            }
        }
    }
}
