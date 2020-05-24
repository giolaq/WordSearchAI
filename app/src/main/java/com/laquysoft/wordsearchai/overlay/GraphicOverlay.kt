package com.laquysoft.wordsearchai.overlay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.huawei.hms.mlsdk.common.LensEngine
import java.util.*

class GraphicOverlay(
    context: Context?,
    attrs: AttributeSet?
) : View(context, attrs) {
    private val lock = Any()
    private var previewWidth = 0
    private var widthScaleFactor = 1.0f
    private var previewHeight = 0
    private var heightScaleFactor = 1.0f
    private var facing = LensEngine.BACK_LENS
    private val graphics: MutableList<Graphic> = ArrayList()

    abstract class Graphic(private val overlay: GraphicOverlay) {

        abstract fun draw(canvas: Canvas?)

        private fun scaleX(horizontal: Float): Float {
            return horizontal * overlay.widthScaleFactor
        }

        private fun scaleY(vertical: Float): Float {
            return vertical * overlay.heightScaleFactor
        }

        private fun translateX(x: Float): Float {
            return if (overlay.facing == LensEngine.FRONT_LENS) {
                overlay.width - scaleX(x)
            } else {
                scaleX(x)
            }
        }

        private fun translateY(y: Float): Float {
            return scaleY(y)
        }

        fun translateRect(rect: Rect) = RectF(
            translateX(rect.left.toFloat()),
            translateY(rect.top.toFloat()),
            translateX(rect.right.toFloat()),
            translateY(rect.bottom.toFloat())
        )

        fun postInvalidate() {
            overlay.postInvalidate()
        }

    }

    fun clear() {
        synchronized(lock) { graphics.clear() }
        postInvalidate()
    }

    fun add(graphic: Graphic) {
        synchronized(lock) { graphics.add(graphic) }
    }

    fun remove(graphic: Graphic) {
        synchronized(lock) { graphics.remove(graphic) }
        postInvalidate()
    }

    fun setCameraInfo(previewWidth: Int, previewHeight: Int, facing: Int) {
        synchronized(lock) {
            this.previewWidth = previewWidth
            this.previewHeight = previewHeight
            this.facing = facing
        }
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        synchronized(lock) {
            if (previewWidth != 0 && previewHeight != 0) {
                widthScaleFactor = width.toFloat() / previewWidth
                heightScaleFactor = height.toFloat() / previewHeight
            }
            for (graphic in graphics) {
                graphic.draw(canvas)
            }
        }
    }
}
