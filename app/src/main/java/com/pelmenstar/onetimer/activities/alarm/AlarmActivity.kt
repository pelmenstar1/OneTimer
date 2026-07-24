package com.pelmenstar.onetimer.activities.alarm

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.pelmenstar.onetimer.R
import com.pelmenstar.onetimer.external.alarm.scheduleAlarm
import com.pelmenstar.onetimer.ui.theme.OneTimerTheme
import com.pelmenstar.onetimer.utils.getDefaultVibrator
import com.pelmenstar.onetimer.utils.vibrateWaveform

class AlarmActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    enableEdgeToEdge()
    setContent {
      OneTimerTheme {
        Scaffold(
          modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
          AlarmScreen(
            modifier = Modifier
              .fillMaxSize()
              .background(Color(0.298f, 0.188f, 0.337f))
              .padding(innerPadding)
          )
        }
      }
    }
  }
}

const val RESCHEDULE_MINUTES = 10

@Preview
@Composable
fun AlarmScreen(modifier: Modifier = Modifier) {
  val activity = LocalActivity.current
  val buttonModifier = Modifier
    .fillMaxHeight(1f)
    .aspectRatio(1f)

  val buttonColor = Color(0.192f, 0.098f, 0.227f, 1.0f)

  DisposableEffect(activity) {
    val vibrator = getDefaultVibrator(activity as Context)

    // Wait 0ms, vibrate 500ms, pause 500ms, then repeat from index 1.
    val timings = longArrayOf(0, 500, 500)
    vibrator.vibrateWaveform(timings, 1)

    onDispose {
      vibrator.cancel()
    }
  }

  DisposableEffect(activity) {
    val player = MediaPlayer.create(activity, R.raw.alarm_sound).apply {
      val attrsBuilder = AudioAttributes.Builder()
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .setUsage(AudioAttributes.USAGE_ALARM)

      if (Build.VERSION.SDK_INT >= 29) {
        attrsBuilder.setHapticChannelsMuted(false)
      }

      setAudioAttributes(attrsBuilder.build())
      setVolume(0.8f, 0.8f)
      setOnCompletionListener { release() }

      start()
    }

    onDispose {
      player.release()
    }
  }

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceEvenly,
    modifier = modifier
  ) {
    Image(
      modifier = Modifier
        .fillMaxWidth(0.8f)
        .fillMaxHeight(0.2f),
      painter = painterResource(R.drawable.baseline_alarm_24),
      contentDescription = null
    )

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.25f),
      horizontalArrangement = Arrangement.SpaceAround
    ) {
      Button(
        modifier = buttonModifier,
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        onClick = {
          if (activity != null) {
            scheduleAlarm(activity, RESCHEDULE_MINUTES)
          }
        }) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "Let me sleep more",
            style = TextStyle(
              textAlign = TextAlign.Center,
              fontWeight = FontWeight(900)
            )
          )
          Text(
            text = "$RESCHEDULE_MINUTES min",
            style = TextStyle(fontWeight = FontWeight(900))
          )
        }
      }

      Button(
        modifier = buttonModifier,
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        onClick = {
          activity?.finish()
        }) {
        Text(
          text = "I'm awake",
          style = TextStyle(
            fontSize = TextUnit(20f, TextUnitType.Sp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight(900)
          )
        )
      }
    }
  }
}
