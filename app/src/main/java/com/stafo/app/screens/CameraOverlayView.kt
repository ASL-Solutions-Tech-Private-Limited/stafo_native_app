package com.stafo.app.screens

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class CameraOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val overlayPaint = Paint().apply {
        color = Color.parseColor("#B3000000")
    }

    private val clearPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        isAntiAlias = true
    }

    private val strokePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 8f
        color = Color.GRAY
        isAntiAlias = true
    }

    private var strokeColor: Int = Color.GRAY

    fun setStrokeColor(color: Int) {
        strokeColor = color
        strokePaint.color = color
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val layer = canvas.saveLayer(null, null)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), overlayPaint)

        val radius = min(width, height) / 2.2f
        val cx = width / 2f
        val cy = height / 2f
        canvas.drawCircle(cx, cy, radius, clearPaint)


        canvas.drawCircle(cx, cy, radius, strokePaint)

        canvas.restoreToCount(layer)
    }
}
