package com.nudriin.fits.ui.camera

import android.app.Dialog
import android.content.Context
import androidx.camera.core.Camera
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.OrientationEventListener
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.nudriin.fits.R
import com.nudriin.fits.databinding.ActivityCameraBinding
import com.nudriin.fits.databinding.DialogCameraBinding
import com.nudriin.fits.utils.showToast
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private lateinit var dialogBinding: DialogCameraBinding
    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var flashMode: Int = ImageCapture.FLASH_MODE_OFF
    private var camera: Camera? = null
    private var isFlashOn = false
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private var snapState = 1
    private lateinit var nutritionData: String
    private lateinit var compositionData: String

    private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCameraBinding.inflate(layoutInflater)
        dialogBinding = DialogCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        binding.flashCamera.setOnClickListener {
            isFlashOn = !isFlashOn
            if (camera?.cameraInfo?.hasFlashUnit() == true) {
                camera?.cameraControl?.enableTorch(isFlashOn)
            }

            if (!isFlashOn) {
                binding.flashCamera.setImageResource(R.drawable.ic_flash)
            } else {
                binding.flashCamera.setImageResource(R.drawable.ic_flash_disable)
            }

        }

        val title = getString(R.string.instruction, "One")
        val message = getString(R.string.nutrition_table_instruction)
        showDialog(title, message)

        binding.backBtn.setOnClickListener { finish() }

        binding.captureImage.setOnClickListener {
            takePhoto()
        }
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUI()
        startCamera()
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .setTargetRotation(Surface.ROTATION_0)
                .setFlashMode(flashMode)
                .build()

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                showToast(this, "Gagal memunculkan kamera.")
                Log.e(TAG, "startCamera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = createCustomTempFile(application)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        binding.progressIndicator.visibility = View.VISIBLE
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
//                    val intent = Intent()
//                    intent.putExtra(EXTRA_CAMERAX_IMAGE, output.savedUri.toString())
//                    setResult(CAMERAX_RESULT, intent)

                    when (snapState) {
                        1 -> {
                            showToast(this@CameraActivity, "Success get nutritional fact data!")
                            nutritionData = "Data Gizi Berhasil di ekstrak"
                            snapState = 2
                            val title = getString(R.string.instruction, "Two")
                            val message = getString(R.string.composition_instruction)
                            showDialog(title, message)
                            binding.progressIndicator.visibility = View.GONE
                        }

                        2 -> {
                            val textRecognizer =
                                TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                            val inputImage: InputImage =
                                InputImage.fromFilePath(this@CameraActivity, output.savedUri!!)

                            textRecognizer.process(inputImage)
                                .addOnSuccessListener { visionText: Text ->
                                    val detectedText: String = visionText.text
                                    if (detectedText.isNotBlank()) {
                                        compositionData = detectedText
                                        binding.tvGradeBottomSheet.text =
                                            "Nutritional: ${nutritionData}"
                                        binding.tvOverallBottomSheet.text =
                                            "Composition: ${compositionData}"
                                        bottomSheetBehavior.state =
                                            BottomSheetBehavior.STATE_EXPANDED
                                    } else {
                                        showToast(this@CameraActivity, "An error occurred!")
                                    }
                                    binding.progressIndicator.visibility = View.GONE
                                }
                                .addOnFailureListener {
                                    binding.progressIndicator.visibility = View.GONE
                                    showToast(this@CameraActivity, "An error occurred!")
                                }
                        }
                    }
                }

                override fun onError(exc: ImageCaptureException) {
                    showToast(this@CameraActivity, "Gagal mengambil gambar.")
                    Log.e(TAG, "onError: ${exc.message}")
                }
            }
        )
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private val orientationEventListener by lazy {
        object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) {
                    return
                }

                val rotation = when (orientation) {
                    in 45 until 135 -> Surface.ROTATION_270
                    in 135 until 225 -> Surface.ROTATION_180
                    in 225 until 315 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                imageCapture?.targetRotation = rotation
            }
        }
    }

    private fun showDialog(title: String, message: String) {
        val dialog = Dialog(this, R.style.CustomDialogTheme)

        if (dialogBinding.root.parent != null) {
            (dialogBinding.root.parent as ViewGroup).removeView(dialogBinding.root)
        }

        dialogBinding.tvTitle.text = title
        dialogBinding.tvDescription.text = HtmlCompat.fromHtml(
            message,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        dialog.setContentView(dialogBinding.root)
        dialog.setCanceledOnTouchOutside(false)

        dialogBinding.btnUnderstand.setOnClickListener {
            dialog.cancel()
        }

        dialog.show()
    }

    override fun onStart() {
        super.onStart()
        orientationEventListener.enable()
    }

    override fun onStop() {
        super.onStop()
        orientationEventListener.disable()
    }

    companion object {
        private const val TAG = "CameraActivity"
        const val EXTRA_CAMERAX_IMAGE = "CameraX Image"
        const val CAMERAX_RESULT = 200
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    }

    private fun createCustomTempFile(context: Context): File {
        val filesDir = context.externalCacheDir
        return File.createTempFile(timeStamp, ".jpg", filesDir)
    }
}