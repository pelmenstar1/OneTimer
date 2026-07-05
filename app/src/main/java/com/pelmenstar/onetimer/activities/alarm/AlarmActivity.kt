package com.pelmenstar.onetimer.activities.alarm

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.pelmenstar.onetimer.ui.theme.OneTimerTheme

class AlarmActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    enableEdgeToEdge()
    setContent {
      OneTimerTheme(darkTheme = true) {
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

@Preview
@Composable
fun AlarmScreen(modifier: Modifier = Modifier) {
  val activity = LocalActivity.current

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
        modifier = Modifier
          .fillMaxHeight(1f)
          .aspectRatio(1f),
        onClick = {}) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "Let me sleep more",
            style = TextStyle(
              textAlign = TextAlign.Center,
              fontWeight = FontWeight(900)
            )
          )
          Text(text = "10 min", style = TextStyle(fontWeight = FontWeight(900)))
        }
      }

      Button(
        modifier = Modifier
          .fillMaxHeight(1f)
          .aspectRatio(1f),
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
