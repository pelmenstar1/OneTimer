package com.pelmenstar.onetimer.external.contract

import android.net.Uri
import androidx.core.net.toUri

fun packageUri(): Uri {
  return "package:com.pelmenstar.onetimer".toUri()
}
