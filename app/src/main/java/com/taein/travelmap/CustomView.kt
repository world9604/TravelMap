package com.taein.travelmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CustomView @JvmOverloads constructor(
    mBitmap: Bitmap,
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val bitmap: Bitmap
    private val text: String

    init {
        // Load your bitmap here
        bitmap = mBitmap

        // Set your text
        text = "Your Text"
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw white background
        val paint = Paint()
        paint.color = resources.getColor(android.R.color.white)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        // Draw bitmap in the center
        val centerX = (width - bitmap.width) / 2.0f
        val centerY = (height - bitmap.height) / 2.0f
        canvas.drawBitmap(bitmap, centerX, centerY, null)

        // Draw text at the bottom
        val textPaint = Paint()
        textPaint.color = resources.getColor(android.R.color.black)
        textPaint.textSize = 40f

        val textWidth = textPaint.measureText(text)
        val textX = (width - textWidth) / 2.0f
        val textY = height - 50f // You can adjust the distance from the bottom

        canvas.drawText(text, textX, textY, textPaint)
    }
}
