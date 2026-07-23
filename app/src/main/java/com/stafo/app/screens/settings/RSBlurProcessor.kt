package com.stafo.app.screens.settings

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur

class RSBlurProcessor(context: Context) {

    private val rs: RenderScript = RenderScript.create(context)

    companion object {
        private const val MAX_RADIUS = 25f
    }

    fun blur(bitmap: Bitmap, radius: Float = 20f, repeat: Int = 0): Bitmap? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) return null

        val actualRadius = if (radius > MAX_RADIUS) MAX_RADIUS else radius

        val inputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        val outputBitmap = Bitmap.createBitmap(
            inputBitmap.width,
            inputBitmap.height,
            inputBitmap.config ?: Bitmap.Config.ARGB_8888
        )



        val input = Allocation.createFromBitmap(rs, inputBitmap)
        val output = Allocation.createTyped(rs, input.type)

        val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        blurScript.setRadius(actualRadius)
        blurScript.setInput(input)

        // First pass
        blurScript.forEach(output)

        // Repeat blur for stronger effect
        for (i in 0 until repeat) {
            output.copyTo(inputBitmap)
            input.copyFrom(inputBitmap)
            blurScript.forEach(output)
        }

        output.copyTo(outputBitmap)

        // Clean up
        input.destroy()
        output.destroy()
        blurScript.destroy()
        rs.destroy()

        return outputBitmap
    }
}