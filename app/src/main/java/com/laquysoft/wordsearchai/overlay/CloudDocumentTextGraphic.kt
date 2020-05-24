package com.laquysoft.wordsearchai.overlay

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.laquysoft.wordsearchai.textrecognizer.Symbol

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

    override fun draw(canvas: Canvas?) {
        symbol.let { txt ->
            // Draws the bounding box around the TextBlock.
            val rect = translateRect(txt.rect)
            canvas?.drawRect(rect, rectPaint)
            val offset = (rect.right-rect.left) / txt.length
            canvas?.drawText(txt.text.orEmpty(), rect.left + (symbol.idx*offset), rect.bottom, textPaint)
        }
    }

    companion object {

        private const val TEXT_COLOR = Color.RED
        private const val TEXT_SIZE = 54.0f
        private const val STROKE_WIDTH = 4.0f
    }

}
