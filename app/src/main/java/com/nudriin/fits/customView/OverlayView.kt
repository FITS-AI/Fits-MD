package com.nudriin.fits.customView

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.nudriin.fits.R
import java.text.NumberFormat
import kotlin.math.max

class OverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var boundingBox: Rect? = null
    private var image: Bitmap? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        image?.let { img ->
            // Draw the image
            canvas.drawBitmap(img, 0f, 0f, null)

            boundingBox?.let { box ->
                // Draw the bounding box
                val paint = Paint()
                paint.color = Color.RED
                paint.strokeWidth = 4f
                paint.style = Paint.Style.STROKE
                canvas.drawRect(box, paint)
            }
        }
    }

    fun setBoundingBox(box: Rect) {
        boundingBox = box
        invalidate() // Trigger a redraw
    }

    fun setImage(img: Bitmap) {
        image = img
        invalidate() // Trigger a redraw
    }
}