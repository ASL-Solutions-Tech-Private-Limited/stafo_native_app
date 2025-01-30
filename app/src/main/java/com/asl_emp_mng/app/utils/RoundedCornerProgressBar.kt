package com.asl_emp_mng.app.utils


import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class RoundedCornerProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val unfilledPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color =
            ContextCompat.getColor(context, android.R.color.darker_gray) // Default unfilled color
        style = Paint.Style.FILL
    }

    private val filledPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color =
            ContextCompat.getColor(context, android.R.color.holo_blue_light) // Default filled color
        style = Paint.Style.FILL
    }

    private var progress = 0f // Progress as a percentage (0-100)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width.toFloat()
        val height = height.toFloat()
        val radius = height / 2 // Rounded corners

        // Draw unfilled background
        val unfilledRect = RectF(0f, 0f, width, height)
        canvas.drawRoundRect(unfilledRect, radius, radius, unfilledPaint)

        // Draw filled progress
        val filledWidth = (width * (progress / 100))
        val filledRect = RectF(0f, 0f, filledWidth, height)
        canvas.drawRoundRect(filledRect, radius, radius, filledPaint)
    }

    /**
     * Update the progress of the progress bar.
     * @param percentage: Progress value between 0 and 100.
     */
    fun setProgress(percentage: Float) {
        progress = percentage.coerceIn(0f, 100f) // Ensure progress is within bounds
        invalidate() // Redraw the view
    }

    /**
     * Set the unfilled color.
     */
    fun setUnfilledColor(color: Int) {
        unfilledPaint.color = color
        invalidate()
    }

    /**
     * Set the filled color.
     */
    fun setFilledColor(color: Int) {
        filledPaint.color = color
        invalidate()
    }
}
