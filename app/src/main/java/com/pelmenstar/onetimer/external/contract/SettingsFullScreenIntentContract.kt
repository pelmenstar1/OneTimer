package com.pelmenstar.onetimer.external.contract

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi

class SettingsFullScreenIntentContract : ActivityResultContract<Unit, Unit>() {
  @RequiresApi(34)
  override fun createIntent(
    context: Context,
    input: Unit
  ): Intent {
    return Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT).apply {
      data = packageUri()
    }
  }

  override fun parseResult(resultCode: Int, intent: Intent?) = Unit
}
