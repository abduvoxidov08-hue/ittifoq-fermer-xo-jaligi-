package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Ittifoq Fermer Xo'jaligi", appName)
  }

  @Test
  fun `verify farm payment calculation logic`() {
    val annualTarget = 75_000_000L
    val paymentAmount = 15_000_000L

    // Step 2 Logic:
    // Remaining balance = (Target - Payment)
    val remainingBalance = annualTarget - paymentAmount
    assertEquals(60_000_000L, remainingBalance)

    // Completion percentage = (Payment / Target) * 100%
    val completionPercentage = (paymentAmount.toDouble() / annualTarget.toDouble()) * 100.0
    assertEquals(20.0, completionPercentage, 0.01)
  }
}
