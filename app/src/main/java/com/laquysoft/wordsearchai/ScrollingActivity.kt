package com.laquysoft.wordsearchai

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Pair
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_scrolling.*
import kotlinx.android.synthetic.main.result_layout.*
import java.io.IOException
import java.util.ArrayList

class ScrollingActivity : AppCompatActivity() {

    private lateinit var viewModel: WordSearchAiViewModel

    private var imageUri: Uri? = null
    // Max width (portrait mode)
    private var imageMaxWidth = 0
    // Max height (portrait mode)
    private var imageMaxHeight = 0
    private var bitmapForDetection: Bitmap? = null
    private var selectedSize: String = SIZE_1024_768

    private var isLandScape: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)

        if (!allPermissionsGranted()) {
            getRuntimePermissions()
        }

        fab.setOnClickListener { view ->
            // Menu for selecting either: a) take new photo b) select from existing
            val popup = PopupMenu(this, view)
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.select_images_from_local -> {
                        startChooseImageIntentForResult()
                        true
                    }
                    R.id.take_photo_using_camera -> {
                        startCameraIntentForResult()
                        true
                    }
                    else -> false
                }
            }

            val inflater = popup.menuInflater
            inflater.inflate(R.menu.camera_button_menu, popup.menu)
            popup.show()
        }
        if (previewPane == null) {
            Log.d(TAG, "Preview is null")
        }
        if (previewOverlay == null) {
            Log.d(TAG, "graphicOverlay is null")
        }


        isLandScape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        savedInstanceState?.let {
            imageUri = it.getParcelable(KEY_IMAGE_URI)
            imageMaxWidth = it.getInt(KEY_IMAGE_MAX_WIDTH)
            imageMaxHeight = it.getInt(KEY_IMAGE_MAX_HEIGHT)
            selectedSize = it.getString(KEY_SELECTED_SIZE)

            imageUri?.let { _ ->
                tryReloadAndDetectInImage()
            }
        }

        viewModel = ViewModelProviders.of(this).get(WordSearchAiViewModel::class.java)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startCameraIntentForResult() {
        // Clean up last time's image
        imageUri = null
        previewPane?.setImageBitmap(null)

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.resolveActivity(packageManager)?.let {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "New Picture")
            values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
            imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun startChooseImageIntentForResult() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CHOOSE_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            tryReloadAndDetectInImage()
        } else if (requestCode == REQUEST_CHOOSE_IMAGE && resultCode == Activity.RESULT_OK) {
            // In this case, imageUri is returned by the chooser, save it.
            imageUri = data!!.data
            tryReloadAndDetectInImage()
        }
    }

    private fun tryReloadAndDetectInImage() {
        try {
            if (imageUri == null) {
                return
            }

            // Clear the overlay first
            previewOverlay?.clear()

            val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)

            // Get the dimensions of the View
            val targetedSize = getTargetedWidthHeight()

            val targetWidth = targetedSize.first
            val maxHeight = targetedSize.second

            // Determine how much to scale down the image
            val scaleFactor = Math.max(
                imageBitmap.width.toFloat() / targetWidth.toFloat(),
                imageBitmap.height.toFloat() / maxHeight.toFloat()
            )

            val resizedBitmap = Bitmap.createScaledBitmap(
                imageBitmap,
                (imageBitmap.width / scaleFactor).toInt(),
                (imageBitmap.height / scaleFactor).toInt(),
                true
            )

            previewPane?.setImageBitmap(resizedBitmap)
            bitmapForDetection = resizedBitmap
            bitmapForDetection?.let {
                viewModel.detectDocumentTextIn(it)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error retrieving saved image")
        }
    }

    // Gets the targeted width / height.
    private fun getTargetedWidthHeight(): Pair<Int, Int> {
        var targetWidth = 0
        var targetHeight = 0

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

    private fun allPermissionsGranted(): Boolean {
        for (permission in getRequiredPermissions()) {
            permission?.let {
                if (!isPermissionGranted(this, it)) {
                    return false
                }
            }
        }
        return true
    }

    private fun getRequiredPermissions(): Array<String?> {
        return try {
            val info = this.packageManager
                .getPackageInfo(this.packageName, PackageManager.GET_PERMISSIONS)
            val ps = info.requestedPermissions
            if (ps != null && ps.isNotEmpty()) {
                ps
            } else {
                arrayOfNulls(0)
            }
        } catch (e: Exception) {
            arrayOfNulls(0)
        }
    }

    private fun getRuntimePermissions() {
        val allNeededPermissions = ArrayList<String>()
        for (permission in getRequiredPermissions()) {
            permission?.let {
                if (!isPermissionGranted(this, it)) {
                    allNeededPermissions.add(permission)
                }
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                this, allNeededPermissions.toTypedArray(), PERMISSION_REQUESTS
            )
        }
    }

    private fun isPermissionGranted(context: Context, permission: String): Boolean {
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission granted: $permission")
            return true
        }
        Log.i(TAG, "Permission NOT granted: $permission")
        return false
    }

    companion object {

        private const val TAG = "ScrollingActivity"

        private const val SIZE_PREVIEW = "w:max" // Available on-screen width.
        private const val SIZE_1024_768 = "w:1024" // ~1024*768 in a normal ratio
        private const val SIZE_640_480 = "w:640" // ~640*480 in a normal ratio

        private const val KEY_IMAGE_URI = " com.laquysoft.wordsearchai.KEY_IMAGE_URI"
        private const val KEY_IMAGE_MAX_WIDTH = "com.laquysoft.wordsearchai.KEY_IMAGE_MAX_WIDTH"
        private const val KEY_IMAGE_MAX_HEIGHT = "com.laquysoft.wordsearchai.KEY_IMAGE_MAX_HEIGHT"
        private const val KEY_SELECTED_SIZE = " com.laquysoft.wordsearchai.KEY_SELECTED_SIZE"

        private const val PERMISSION_REQUESTS = 1

        private const val REQUEST_IMAGE_CAPTURE = 1001
        private const val REQUEST_CHOOSE_IMAGE = 1002
    }
}
