package com.example.autoscrollapp

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class AutoScrollService : AccessibilityService() {

    private val handler = Handler(Looper.getMainLooper())
    private val scrollInterval = 1000L  // 1 second

    private val scrollRunnable = object : Runnable {
        override fun run() {
            if (ScrollController.isScrolling) {
                performScroll()
                handler.postDelayed(this, scrollInterval)
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Toast.makeText(this, "Accessibility Service connected!", Toast.LENGTH_LONG).show()
    }

    override fun onUnbind(intent: android.content.Intent?): Boolean {
        instance = null
        return super.onUnbind(intent)
    }

    override fun onInterrupt() {}
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Handle accessibility events to detect app changes
        event?.let {
            when (it.eventType) {
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                    // App window changed, ensure scrolling continues if it was active
                    if (ScrollController.isScrolling) {
                        // Restart scrolling with a slight delay to allow the new window to settle
                        handler.removeCallbacks(scrollRunnable)
                        handler.postDelayed(scrollRunnable, 500)
                    }
                }
            }
        }
    }

    fun startScrolling() {
        ScrollController.isScrolling = true
        handler.post(scrollRunnable)
    }

    fun stopScrolling() {
        ScrollController.isScrolling = false
        handler.removeCallbacks(scrollRunnable)
    }

    private fun performScroll() {
        // Use gesture scrolling as the primary method
        performGestureScroll()
    }

    private fun performGestureScroll() {
        val displayMetrics = resources.displayMetrics
        val middleX = displayMetrics.widthPixels / 2
        val startY = displayMetrics.heightPixels * 0.75f // Start from 75% of the screen height
        val endY = displayMetrics.heightPixels * 0.25f   // Scroll up to 25% of the screen height

        val path = Path().apply {
            moveTo(middleX.toFloat(), startY)
            lineTo(middleX.toFloat(), endY)
        }

        val gestureDescription = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 200)) // 200ms duration
            .build()

        // Dispatch gesture without callback
        dispatchGesture(gestureDescription, null, null)
    }

    companion object {
        var instance: AutoScrollService? = null
    }
}
