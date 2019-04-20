package com.laquysoft.wordsearchai.overlay

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText

/**
 * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
 * overlay view.
 */
class CloudDocumentTextGraphic(
    overlay: GraphicOverlay,
    private val symbol: FirebaseVisionDocumentText.Symbol?
) : GraphicOverlay.Graphic(overlay) {

    private val rectPaint = Paint().apply {
        color = TEXT_COLOR
        style = Paint.Style.STROKE
        strokeWidth = STROKE_WIDTH
    }

    private val textPaint = Paint().apply {
        color = TEXT_COLOR
        textSize = TEXT_SIZE
    }

    /** Draws the text block annotations for position, size, and raw value on the supplied canvas.  */
    override fun draw(canvas: Canvas) {
        symbol?.let { syb ->
            val rect = syb.boundingBox
            rect?.let {
                val scaledLeft = scaleX(it.left.toFloat()).toInt()
                val scaledTop = scaleY(it.top.toFloat()).toInt()
                val scaledRight = scaleX(it.right.toFloat()).toInt()
                val scaledBottom = scaleY(it.bottom.toFloat()).toInt()
                val scaledRect = Rect(scaledLeft, scaledTop, scaledRight, scaledBottom)
                canvas.drawRect(scaledRect, rectPaint)
                val x = scaleX(it.left.toFloat())
                val y = scaleY(it.bottom.toFloat())
                canvas.drawText(syb.text, x, y, textPaint)
            }
        } ?: kotlin.run { throw IllegalStateException("Attempting to draw a null text.") }
    }

    companion object {
        private const val TEXT_COLOR = Color.RED
        private const val TEXT_SIZE = 54.0f
        private const val STROKE_WIDTH = 4.0f
    }
}
