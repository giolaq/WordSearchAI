package com.laquysoft.wordsearchai.overlay

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.laquysoft.wordsearchai.textrecognizer.Symbol

/**
 * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
 * overlay view.
 */
class CloudDocumentTextGraphic(
    overlay: GraphicOverlay,
    private val symbol: Symbol
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
    override fun draw(canvas: Canvas?) {
        symbol.let { txt ->
            // Draws the bounding box around the TextBlock.
            val rect = RectF(txt.rect)
            rect.left = translateX(rect.left)
            rect.top = translateY(rect.top)
            rect.right = translateX(rect.right)
            rect.bottom = translateY(rect.bottom)
            canvas?.drawRect(rect, rectPaint)
            val offset = (rect.right-rect.left) / txt.length
            canvas?.drawText(txt.text, rect.left + (symbol.idx*offset), rect.bottom, textPaint)
        }
    }

    companion object {

        private const val TEXT_COLOR = Color.RED
        private const val TEXT_SIZE = 54.0f
        private const val STROKE_WIDTH = 4.0f
    }

}
