package com.nudriin.fits.customView

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.nudriin.fits.R

class OverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val borderPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 8f
        isAntiAlias = true
    }

    private val cornerLength = 80f // panjang sisi sudut

    private val overlayWidth = 278f
    private val overlayHeight = 350f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val scale = resources.displayMetrics.density
        val widthPx = overlayWidth * scale
        val heightPx = overlayHeight * scale

        val left = (width - widthPx) / 2
        val top = (height - heightPx) / 2
        val right = left + widthPx
        val bottom = top + heightPx

        // kiri top
        canvas.drawLine(left, top, left + cornerLength, top, borderPaint)
        canvas.drawLine(left, top, left, top + cornerLength, borderPaint)

        // kanan top
        canvas.drawLine(right, top, right - cornerLength, top, borderPaint)
        canvas.drawLine(right, top, right, top + cornerLength, borderPaint)

        // kanan bottom
        canvas.drawLine(right, bottom, right - cornerLength, bottom, borderPaint)
        canvas.drawLine(right, bottom, right, bottom - cornerLength, borderPaint)

        // kiri bottom
        canvas.drawLine(left, bottom, left + cornerLength, bottom, borderPaint)
        canvas.drawLine(left, bottom, left, bottom - cornerLength, borderPaint)
    }

    fun getOverlayBounds(): RectF {
        val scale = resources.displayMetrics.density
        val widthPx = overlayWidth * scale
        val heightPx = overlayHeight * scale
        val left = (width - widthPx) / 2
        val top = (height - heightPx) / 2
        return RectF(left, top, left + widthPx, top + heightPx)
    }
}
