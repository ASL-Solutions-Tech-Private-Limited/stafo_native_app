package com.stafo.app.base

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.view.WindowManager

class FontScaleContextWrapper(base: Context) : ContextWrapper(base) {
    companion object {
        fun wrap(context: Context): ContextWrapper {
            val newConfig = Configuration(context.resources.configuration)
            newConfig.fontScale = 1.0f

            val metrics = context.resources.displayMetrics
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            wm.defaultDisplay.getMetrics(metrics)
            metrics.scaledDensity = newConfig.fontScale * metrics.density

            val newContext = context.createConfigurationContext(newConfig)
            return FontScaleContextWrapper(newContext)
        }
    }
}