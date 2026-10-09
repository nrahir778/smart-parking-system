package com.example.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Tactile Haptic Feedback for Smart Parking state changes.
 * Distinct, beautiful haptic profiles for car arrival and departure.
 */
class ParkingHaptics(private val context: Context) {

    private val vibrator: Vibrator? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    } catch (_: Exception) {
        null
    }

    /**
     * Subtle, tactile double-tap pulse when a car arrives and settles into a bay.
     * Gentle approach tick followed by solid seating feedback.
     */
    fun playCarArrived() {
        try {
            vibrator ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Waveform: wait 0ms, buzz 20ms at mid intensity, pause 40ms, solid 35ms thud
                val timings = longArrayOf(0, 20, 40, 35)
                val amplitudes = intArrayOf(0, 150, 0, 240)
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(40)
            }
        } catch (_: Exception) {}
    }

    /**
     * Crisp, light single release tick when a car departs and leaves the bay vacant.
     */
    fun playCarDeparted() {
        try {
            vibrator ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(18, 130))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(20)
            }
        } catch (_: Exception) {}
    }

    /**
     * Subtle alert pulse when all 3 bays become full and the MG995 gate barrier closes.
     */
    fun playLotFullAlert() {
        try {
            vibrator ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 40, 60, 40)
                val amplitudes = intArrayOf(0, 180, 0, 220)
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 40, 60, 40), -1)
            }
        } catch (_: Exception) {}
    }
}
