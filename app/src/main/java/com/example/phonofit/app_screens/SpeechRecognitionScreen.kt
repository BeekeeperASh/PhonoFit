package com.example.phonofit.app_screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.phonofit.MainActivity
import com.example.phonofit.R


@Composable
fun SpeechRecognitionScreen(
    modifier: Modifier = Modifier,
    textString: String,
    len: MutableState<Int>,
    maxLen: Int,
    accuracy: MutableState<Int>,
    isEnd: MutableState<Boolean>,
    finalAccuracy: MutableState<Int>
) {

    var speechRecognizer: SpeechRecognizer? = null
    val flag = remember {
        mutableStateOf(false)
    }
    val textResult = remember {
        mutableStateOf(ArrayList<String>())
    }

    val context: Context = LocalContext.current

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) !=
        PackageManager.PERMISSION_GRANTED
    ) {
        checkPermissions(context)
    }
    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

    val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    speechRecognizerIntent.putExtra(
        RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
    )
    speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
    speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Something")

    speechRecognizer!!.setRecognitionListener(object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}

        override fun onBeginningOfSpeech() {
            //Toast.makeText(context, "The user has started to speak.", Toast.LENGTH_SHORT).show()
        }

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
            //Toast.makeText(context, "The user has stopped to speak.", Toast.LENGTH_SHORT).show()
            flag.value = false
        }

        override fun onError(error: Int) {
            Toast.makeText(context, "Please repeat", Toast.LENGTH_SHORT).show()
            flag.value = false
        }

        override fun onResults(bundle: Bundle?) {
            //Toast.makeText(context, "Results", Toast.LENGTH_SHORT).show()
            val data = bundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (data != null) {
                textResult.value = data
            }
            flag.value = false
            val percent = levenshteinDistancePercentage(data?.get(0) ?: "", textString)
            if (percent > 50 && len.value < maxLen) { //
                finalAccuracy.value += percent.toInt()
                len.value += 1
            } else if (percent > 50){
                isEnd.value = true
                finalAccuracy.value += percent.toInt()
            }
            //Toast.makeText(context, messageList.first().text, Toast.LENGTH_SHORT).show()
            //Toast.makeText(context, percent.toString(), Toast.LENGTH_SHORT).show()
            accuracy.value = percent.toInt()
            //Toast.makeText(context, textString, Toast.LENGTH_SHORT).show()
        }

        override fun onPartialResults(partialResults: Bundle?) {}

        override fun onEvent(eventType: Int, params: Bundle?) {}

    })

    Box(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .heightIn(100.dp),
        contentAlignment = Alignment.TopCenter
    ) {

        IconButton(
            onClick = {
                if (flag.value) {
                    speechRecognizer.stopListening()
                } else {
                    speechRecognizer.startListening(
                        speechRecognizerIntent
                    )
                    flag.value = true
                }
            },
            enabled = len.value <= maxLen
        ) {
            Icon(
                painter = painterResource(id = if (flag.value) R.drawable.mic else R.drawable.mic_off),
                contentDescription = "icon",
                modifier = Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .border(4.dp, MaterialTheme.colorScheme.tertiary, CircleShape),
                tint = MaterialTheme.colorScheme.tertiary,
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            speechRecognizer.destroy()
        }
    }

}

fun checkPermissions(context: Context) {
    ActivityCompat.requestPermissions(
        context as Activity,
        arrayOf(Manifest.permission.RECORD_AUDIO),
        MainActivity.RecordAudioRequestCode
    )
}

fun levenshteinDistancePercentage(s1: String, s2: String): Double {
    val n = s1.length
    val m = s2.length
    if (n == 0) {
        return if (m == 0) 100.0 else 0.0
    }
    if (m == 0) {
        return 0.0
    }
    val dp = Array(n + 1) { IntArray(m + 1) }
    for (i in 0..n) {
        dp[i][0] = i
    }
    for (j in 0..m) {
        dp[0][j] = j
    }
    for (i in 1..n) {
        for (j in 1..m) {
            val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
            dp[i][j] =
                (dp[i - 1][j] + 1).coerceAtMost((dp[i][j - 1] + 1).coerceAtMost(dp[i - 1][j - 1] + cost))
        }
    }
    val distance = dp[n][m]
    val maxLength = n.coerceAtLeast(m)
    val accuracy = (maxLength - distance).toDouble() / maxLength * 100
    return if(accuracy + 5.0 > 100) 100.0 else accuracy + 5.0
}