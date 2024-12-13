package com.nudriin.fits.customView

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.nudriin.fits.R

class RealtimeOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var boxPaint = Paint()

    init {
        initPaints()
    }

    private var boundingBox: FloatArray? = null
    private var scaleFactorX: Float = 1f
    private var scaleFactorY: Float = 1f

    override fun draw(canvas: Canvas) {
        
        super.draw(canvas)

        boundingBox?.let {
            val left = it[1] * scaleFactorX
            val top = it[0] * scaleFactorY
            val right = it[3] * scaleFactorX
            val bottom = it[2] * scaleFactorY

            canvas.drawRect(left, top, right, bottom, boxPaint)
        }
    }

    @Suppress("DEPRECATION")
    private fun initPaints() {
        boxPaint.color = resources.getColor(R.color.lime)
        boxPaint.style = Paint.Style.STROKE
        boxPaint.strokeWidth = 8f
    }

    fun updateBoundingBox(
        results: FloatArray,
        imageWidth: Int,
        imageHeight: Int
    ) {
        boundingBox = results
        scaleFactorX = width.toFloat() / imageWidth
        scaleFactorY = height.toFloat() / imageHeight
        invalidate()
    }

    fun clear() {
        boxPaint.reset()
        Log.d("RealtimeOverlayView", "Clear overlay called")
        boundingBox = null
        invalidate()
        initPaints()
    }
}

