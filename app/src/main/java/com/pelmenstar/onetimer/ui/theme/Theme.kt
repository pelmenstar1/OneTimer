package com.pelmenstar.onetimer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.colorResource
import com.pelmenstar.onetimer.R

@Composable
fun OneTimerTheme(
  content: @Composable () -> Unit
) {
  val background = colorResource(R.color.background)

  val colorScheme = remember(background) {
    darkColorScheme(
      primary = Purple80,
      secondary = PurpleGrey80,
      tertiary = Pink80,
      background = background
    )
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
