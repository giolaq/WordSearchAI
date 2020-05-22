package com.laquysoft.wordsearchai

import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.PopupMenu
import androidx.activity.invoke
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.vision.CameraSource
import com.laquysoft.wordsearchai.ImageLoader.Companion.SIZE_1024_768
import com.laquysoft.wordsearchai.overlay.CloudDocumentTextGraphic
import com.laquysoft.wordsearchai.textrecognizer.Symbol
import kotlinx.android.synthetic.main.activity_scrolling.*
import kotlinx.android.synthetic.main.result_layout.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var imageSize: Pair<Int, Int>
    private val viewModel by viewModels<WordSearchAiViewModel> { getViewModelFactory() }

    private var imageUri: Uri? = null

    // Max width (portrait mode)
    private var imageMaxWidth = 0

    // Max height (portrait mode)
    private var imageMaxHeight = 0
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
                        pickImages()
                        true
                    }
                    R.id.take_photo_using_camera -> {
                        takePicture()
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
            selectedSize = it.getString(KEY_SELECTED_SIZE, SIZE_1024_768)

            tryReloadAndDetectInImage()
        }

        val adapter = WordListAdapter()
        wordsList.adapter = adapter
        wordsList.layoutManager = LinearLayoutManager(this)
        subscribeUi(adapter)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_IMAGE_URI, imageUri)
        outState.putInt(KEY_IMAGE_MAX_WIDTH, imageMaxWidth)
        outState.putInt(KEY_IMAGE_MAX_HEIGHT, imageMaxHeight)
        outState.putString(KEY_SELECTED_SIZE, selectedSize)
    }

    private fun subscribeUi(adapter: WordListAdapter) {
        viewModel.resultList.observe(this, Observer { words ->
            if (words != null) adapter.submitList(words)
        })

        viewModel.resultBoundingBoxes.observe(this, Observer { boundingBoxes ->
            previewOverlay.clear()
            boundingBoxes.forEach{ symbol ->
                val cloudDocumentTextGraphic = CloudDocumentTextGraphic(
                    previewOverlay,
                    symbol)
                previewOverlay.add(cloudDocumentTextGraphic)
                previewOverlay.postInvalidate()
            }
        })
    }

    private fun takePicture() {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "New Picture")
            put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
        }
        imageUri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            tryReloadAndDetectInImage()
        }(imageUri)
    }

    private fun pickImages() {
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imageUri = uri
            tryReloadAndDetectInImage()
        }("image/*")
    }

    private fun tryReloadAndDetectInImage() {
        try {
            imageUri?.let {
                // Clear the overlay first
                previewOverlay?.clear()

                val imageLoader =
                    ImageLoader(contentResolver, it, selectedSize, isLandScape, previewPane)

                val resizedBitmap = imageLoader.resizedBitmap
                imageSize = Pair(resizedBitmap.width, resizedBitmap.height)
                previewPane?.setImageBitmap(resizedBitmap)

                previewOverlay.setCameraInfo(
                    resizedBitmap.width,
                    resizedBitmap.height,
                    CameraSource.CAMERA_FACING_BACK
                )

                resizedBitmap.let { bitmap ->
                    viewModel.detectDocumentTextIn(bitmap)
                }
            }

        } catch (e: IOException) {
            Log.e(TAG, "Error retrieving saved image")
        }
    }

    private fun allPermissionsGranted() =
        getRequiredPermissions().all { isPermissionGranted(this, it) }


    private fun getRequiredPermissions(): Array<String> {
        return try {
            val info = this.packageManager
                .getPackageInfo(this.packageName, PackageManager.GET_PERMISSIONS)
            val ps = info.requestedPermissions
            if (ps != null && ps.isNotEmpty()) {
                ps
            } else {
                emptyArray()
            }
        } catch (e: Exception) {
            emptyArray()
        }
    }

    private fun getRuntimePermissions() {
        val allNeededPermissions = getRequiredPermissions().mapNotNull {
            it.takeIf { !isPermissionGranted(this, it) }
        }

        if (allNeededPermissions.isNotEmpty()) {
            askMultiplePermissions(allNeededPermissions.toTypedArray())
        }
    }

    private fun isPermissionGranted(context: Context, permission: String): Boolean {
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.i(TAG, "Permission granted: $permission")
            return true
        }
        Log.i(TAG, "Permission NOT granted: $permission")
        return false
    }

    private val askMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}

    companion object {

        private const val TAG = "MainActivity"

        private const val KEY_IMAGE_URI = " com.laquysoft.wordsearchai.KEY_IMAGE_URI"
        private const val KEY_IMAGE_MAX_WIDTH = "com.laquysoft.wordsearchai.KEY_IMAGE_MAX_WIDTH"
        private const val KEY_IMAGE_MAX_HEIGHT = "com.laquysoft.wordsearchai.KEY_IMAGE_MAX_HEIGHT"
        private const val KEY_SELECTED_SIZE = " com.laquysoft.wordsearchai.KEY_SELECTED_SIZE"
    }
}
