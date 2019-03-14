package com.laquysoft.wordsearchai

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Pair
import android.view.View

class ImageLoader(
    contentResolver: ContentResolver,
    uri: Uri,
    private val selectedSize: String,
    private val isLandScape: Boolean,
    val previewPane: View
) {

    private val imageBitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)

    val resizedBitmap: Bitmap
        get() {
            // Get the dimensions of the View
            val targetedSize = getTargetedWidthHeight()

            val targetWidth = targetedSize.first
            val maxHeight = targetedSize.second

            // Determine how much to scale down the image
            val scaleFactor = Math.max(
                imageBitmap.width.toFloat() / targetWidth.toFloat(),
                imageBitmap.height.toFloat() / maxHeight.toFloat()
            )

            return Bitmap.createScaledBitmap(
                imageBitmap,
                (imageBitmap.width / scaleFactor).toInt(),
                (imageBitmap.height / scaleFactor).toInt(),
                true
            )
        }


    // Gets the targeted width / height.
    private fun getTargetedWidthHeight(): Pair<Int, Int> {
        val targetWidth: Int
        val targetHeight: Int

        when (selectedSize) {
            SIZE_PREVIEW -> {
                val maxWidthForPortraitMode = getImageMaxWidth()
                val maxHeightForPortraitMode = getImageMaxHeight()
                targetWidth = if (isLandScape) maxHeightForPortraitMode else maxWidthForPortraitMode
                targetHeight = if (isLandScape) maxWidthForPortraitMode else maxHeightForPortraitMode
            }
            SIZE_640_480 -> {
                targetWidth = if (isLandScape) 640 else 480
                targetHeight = if (isLandScape) 480 else 640
            }
            SIZE_1024_768 -> {
                targetWidth = if (isLandScape) 1024 else 768
                targetHeight = if (isLandScape) 768 else 1024
            }
            else -> throw IllegalStateException("Unknown size")
        }

        return Pair(targetWidth, targetHeight)
    }

    private var imageMaxWidth: Int = 0
    private var imageMaxHeight: Int = 0

    // Returns max image width, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private fun getImageMaxWidth(): Int {
        if (imageMaxWidth == 0) {
            // Calculate the max width in portrait mode. This is done lazily since we need to wait for
            // a UI layout pass to get the right values. So delay it to first time image rendering time.
            imageMaxWidth = if (isLandScape) {
                (previewPane.parent as View).height
            } else {
                (previewPane.parent as View).width
            }
        }

        return imageMaxWidth
    }

    // Returns max image height, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private fun getImageMaxHeight(): Int {
        if (imageMaxHeight == 0) {
            // Calculate the max width in portrait mode. This is done lazily since we need to wait for
            // a UI layout pass to get the right values. So delay it to first time image rendering time.
            imageMaxHeight = if (isLandScape) {
                (previewPane.parent as View).width
            } else {
                (previewPane.parent as View).height
            }
        }

        return imageMaxHeight
    }

    companion object {

        const val SIZE_PREVIEW = "w:max" // Available on-screen width.
        const val SIZE_1024_768 = "w:1024" // ~1024*768 in a normal ratio
        const val SIZE_640_480 = "w:640" // ~640*480 in a normal ratio

    }
}
