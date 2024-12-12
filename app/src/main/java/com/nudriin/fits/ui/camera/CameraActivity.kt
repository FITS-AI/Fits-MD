package com.nudriin.fits.ui.camera

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.camera.core.Camera
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.OrientationEventListener
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.nudriin.fits.R
import com.nudriin.fits.adapter.AnalysisAdapter
import com.nudriin.fits.common.ProductViewModel
import com.nudriin.fits.data.domain.HealthAnalysis
import com.nudriin.fits.data.domain.HealthRecommendationSummary
import com.nudriin.fits.data.dto.gemini.Contents
import com.nudriin.fits.data.dto.gemini.GeminiGenerationResponse
import com.nudriin.fits.data.dto.gemini.GeminiRequest
import com.nudriin.fits.data.dto.gemini.Part
import com.nudriin.fits.data.dto.product.ProductSaveRequest
import com.nudriin.fits.databinding.ActivityCameraBinding
import com.nudriin.fits.databinding.DialogCameraBinding
import com.nudriin.fits.databinding.DialogEditTextBinding
import com.nudriin.fits.ui.appSettings.AppSettingsViewModel
import com.nudriin.fits.utils.HealthRecommendationHelper
import com.nudriin.fits.utils.OcrHelper
import com.nudriin.fits.utils.Result
import com.nudriin.fits.utils.ViewModelFactory
import com.nudriin.fits.utils.getGradeId
import com.nudriin.fits.utils.showToast
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private lateinit var dialogBinding: DialogCameraBinding
    private lateinit var dialogSaveProductBinding: DialogEditTextBinding
    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var flashMode: Int = ImageCapture.FLASH_MODE_OFF
    private var camera: Camera? = null
    private var isFlashOn = false
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private var snapState = 1
    private var nutritionData: String? = null
    private var geminiGenerationResponse: GeminiGenerationResponse? = null
    private lateinit var healthRecommendationHelper: HealthRecommendationHelper
    private val appSettingsViewModel: AppSettingsViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private val productViewModel: ProductViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private var summary: HealthRecommendationSummary? = null
    private var analysisGrade: String? = null
    private lateinit var ocrHelper: OcrHelper

    private lateinit var bitmapImage: Bitmap

    private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCameraBinding.inflate(layoutInflater)
        dialogBinding = DialogCameraBinding.inflate(layoutInflater)
        dialogSaveProductBinding = DialogEditTextBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupView()
        setupAction()
    }

    public override fun onResume() {
        super.onResume()
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

        showLoading(true)
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    when (snapState) {
                        1 -> {
                            val croppedUri = cropImage(output.savedUri!!)
                            bitmapImage =
                                MediaStore.Images.Media.getBitmap(contentResolver, croppedUri)
                            ocrHelper.detectObject(bitmapImage)
                            val textRecognizer =
                                TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                            val inputImage: InputImage =
                                InputImage.fromFilePath(this@CameraActivity, croppedUri)

                            textRecognizer.process(inputImage)
                                .addOnSuccessListener { visionText: Text ->
                                    val detectedText: String = visionText.text
                                    if (detectedText.isNotBlank()) {
                                        nutritionData = detectedText
                                    } else {
                                        showToast(this@CameraActivity, "An error occurred!")
                                    }
                                    showLoading(false)
                                }
                                .addOnFailureListener {
                                    showLoading(false)
                                    showToast(this@CameraActivity, "An error occurred!")
                                }
                            snapState = 2
                            val title = getString(R.string.instruction, "Two")
                            val message = getString(R.string.composition_instruction)
                            showDialog(title, message)
                            showLoading(false)
                        }

                        2 -> {
                            val croppedUri = cropImage(output.savedUri!!)
                            val textRecognizer =
                                TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                            val inputImage: InputImage =
                                InputImage.fromFilePath(this@CameraActivity, croppedUri)

                            textRecognizer.process(inputImage)
                                .addOnSuccessListener { visionText: Text ->
                                    val detectedText: String = visionText.text
                                    if (detectedText.isNotBlank()) {
                                        setGeminiGenerationResponse(nutritionData!!, detectedText)
                                    } else {
                                        showToast(this@CameraActivity, "An error occurred!")
                                    }
                                    showLoading(false)
                                }
                                .addOnFailureListener {
                                    showLoading(false)
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

    private fun setupView() {
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

        startCamera()

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        val title = getString(R.string.instruction, "One")
        val message = getString(R.string.nutrition_table_instruction)
        showDialog(title, message)

        healthRecommendationHelper = HealthRecommendationHelper(
            context = this,
            onResult = { result ->
                var isDiabetes = false
                appSettingsViewModel.getSettings().observe(
                    this@CameraActivity
                ) { settings ->
                    isDiabetes = settings.diabetes
                }

                analysisGrade = result

                summary = healthRecommendationHelper.recommendationSummary(result, isDiabetes)

                setAnalysisResult(summary!!, result)
                bottomSheetBehavior.state =
                    BottomSheetBehavior.STATE_EXPANDED
            },
            onError = { msg ->
                showToast(this@CameraActivity, msg)
            }
        )

        appSettingsViewModel.getSettings().observe(
            this
        ) { settings ->
            if (settings.darkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        ocrHelper = OcrHelper(
            context = this@CameraActivity,
            detectorListener = object : OcrHelper.DetectorListener {
                override fun onError(error: String) {
                    runOnUiThread {
                        Log.d("CameraOCRError", error)
                        showToast(this@CameraActivity, error)
                    }
                }

                override fun onResults(
                    results: FloatArray,
                    inferenceTime: Long,
                    imageHeight: Int,
                    imageWidth: Int,
                    boundingImg: Bitmap?
                ) {
                    runOnUiThread {
                        Log.d("OCR_CAMERA_RES", results.joinToString())
                        showToast(this@CameraActivity, results.joinToString())
                    }
                }

            }
        )
    }

    private fun setupAction() {
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

        binding.backBtn.setOnClickListener { finish() }

        binding.captureImage.setOnClickListener {
            takePhoto()
        }

        binding.btnSaveHistory.setOnClickListener {
            showSaveProductDialog()
        }
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

    private fun setAnalysisResult(
        summary: HealthRecommendationSummary,
        analysisResult: String? = null
    ) {
        binding.tvGradeLabel.text = analysisResult ?: "!"
        binding.tvGradeBottomSheet.text = summary.grade
        binding.tvOverallBottomSheet.text = summary.overall
        if (summary.warning.isNotEmpty()) {
            binding.tvWarningBottomSheet.text = summary.warning
        } else {
            binding.tvWarningBottomSheet.visibility = View.GONE
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvAnalysisResult.layoutManager = layoutManager

        val sugar = HealthAnalysis("Sugar", summary.sugar)
        val fat = HealthAnalysis("Fat", summary.fat)
        val protein = HealthAnalysis("Protein", summary.protein)
        val calories = HealthAnalysis("Calories", summary.calories)

        val analysisList = listOf<HealthAnalysis>(sugar, fat, protein, calories)

        val adapter = AnalysisAdapter(analysisList)
        binding.rvAnalysisResult.adapter = adapter
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

        appSettingsViewModel.getSettings().observe(
            this
        ) { settings ->
            if (settings.instruction) {
                dialog.show()
            } else {
                dialog.hide()
            }
        }

    }

    private fun showSaveProductDialog() {
        val dialog = Dialog(this, R.style.CustomDialogTheme)

        if (dialogBinding.root.parent != null) {
            (dialogBinding.root.parent as ViewGroup).removeView(dialogBinding.root)
        }

        dialog.setContentView(dialogSaveProductBinding.root)
        dialog.setCanceledOnTouchOutside(false)

        dialogSaveProductBinding.btnClose.setOnClickListener {
            dialog.cancel()
        }

        dialogSaveProductBinding.btnSave.setOnClickListener {
            val productName = dialogSaveProductBinding.edtProductName.text.toString()
            var request: ProductSaveRequest
            geminiGenerationResponse.let {
                request = ProductSaveRequest(
                    gradesId = getGradeId(analysisGrade!!)!!,
                    name = productName,
                    calories = summary!!.calories,
                    caloriesIng = it?.caloriesIng!!,
                    protein = summary!!.protein,
                    proteinIng = it.proteinIng,
                    fat = summary!!.fat,
                    fatIng = it.fatIng,
                    fiber = "2 g",
                    fiberIng = "oats, flaxseed",
                    carbo = "20 g",
                    carboIng = "wheat, rice",
                    sugar = summary!!.sugar,
                    sugarIng = it.sugarIng,
                    allergy = listOf()
                )

            }

            productViewModel.saveProduct(request).observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Success -> {
                        showLoading(false)
                        showToast(this, result.data.message)
                        finish()
                    }

                    is Result.Error -> {
                        showLoading(false)
                        result.error.getContentIfNotHandled().let { toastText ->
                            showToast(this, toastText.toString())
                        }
                    }
                }
            }
        }

        dialog.show()
    }

    private fun setGeminiGenerationResponse(nutritionalData: String, compositionData: String) {
        val request = GeminiRequest(
            listOf(
                Contents(
                    listOf(
                        Part(
                            "Nutritional Fact: $nutritionalData Composition: $compositionData" + getString(
                                R.string.gemini_prompt
                            )
                        )
                    )
                )
            )
        )

        productViewModel.generateContent(request)
            .observe(this@CameraActivity) { result ->
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Success -> {
                        showLoading(false)
                        val response =
                            result.data.candidates.first().content.parts.first().text.trimIndent()

                        val jsonRegex =
                            """json\n(\{.*?\})""".toRegex(
                                RegexOption.DOT_MATCHES_ALL
                            )

                        val match = jsonRegex.find(response)
                        val jsonString = match?.groupValues?.get(1)

                        Log.d(
                            "GEMINI_RESULT",
                            jsonString!!
                        )

                        geminiGenerationResponse = Gson().fromJson(
                            jsonString,
                            GeminiGenerationResponse::class.java
                        )

                        val inputValues =
                            floatArrayOf(
                                geminiGenerationResponse?.sugar?.toFloat()!!,
                                geminiGenerationResponse?.fat?.toFloat()!!,
                                geminiGenerationResponse?.protein?.toFloat()!!,
                                geminiGenerationResponse?.calories?.toFloat()!!
                            )

                        Log.d(
                            "HEALTH_REC_INPUT",
                            inputValues.joinToString()
                        )
                        floatArrayOf()
                        healthRecommendationHelper.predict(floatArrayOf(100f, 200f, 0f, 300f))
                    }

                    is Result.Error -> {
                        showLoading(false)
                        result.error.getContentIfNotHandled()
                            .let { toastText ->
                                showToast(
                                    this@CameraActivity,
                                    toastText.toString()
                                )
                            }
                    }
                }
            }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressIndicator.visibility = View.VISIBLE
        } else {
            binding.progressIndicator.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        orientationEventListener.enable()
    }

    override fun onStop() {
        super.onStop()
        orientationEventListener.disable()
    }

    override fun onDestroy() {
        super.onDestroy()
        healthRecommendationHelper.close()
        ocrHelper.close()
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

    private fun cropImage(uri: Uri): Uri {
        val image = MediaStore.Images.Media.getBitmap(contentResolver, uri)

        val previewView = binding.viewFinder
        val overlayView = binding.overlay
        val overlayBounds = overlayView.getOverlayBounds()

        val scaleX = image.width.toFloat() / previewView.width
        val scaleY = image.height.toFloat() / previewView.height

        val cropLeft = (overlayBounds.left * scaleX).toInt()
        val cropTop = (overlayBounds.top * scaleY).toInt()
        val cropWidth = (overlayBounds.width() * scaleX).toInt()
        val cropHeight = (overlayBounds.height() * scaleY).toInt()

        val croppedBitmap = Bitmap.createBitmap(
            image,
            cropLeft.coerceAtLeast(0),
            cropTop.coerceAtLeast(0),
            cropWidth.coerceAtMost(image.width - cropLeft),
            cropHeight.coerceAtMost(image.height - cropTop)
        )

        val croppedFile = File(cacheDir, "cropped_image.jpg")
        val outputStream = FileOutputStream(croppedFile)
        croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()

        return Uri.fromFile(croppedFile)
    }
}