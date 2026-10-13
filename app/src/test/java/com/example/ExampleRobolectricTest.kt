package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    val creatorName = context.getString(R.string.creator_name)
    val creatorEmail = context.getString(R.string.creator_email)
    assertEquals("TallyKhata", appName)
    assertEquals("Md. Rabiul Islam", creatorName)
    assertEquals("rabiul.baishari@gmail.com", creatorEmail)
  }
}
