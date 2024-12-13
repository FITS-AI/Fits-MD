package com.nudriin.fits.ui.camera

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
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
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.nudriin.fits.R
import com.nudriin.fits.adapter.AnalysisAdapter
import com.nudriin.fits.adapter.SavedAllergyAdapter
import com.nudriin.fits.common.ProductViewModel
import com.nudriin.fits.data.domain.HealthAnalysis
import com.nudriin.fits.data.domain.HealthRecommendationSummary
import com.nudriin.fits.data.dto.allergy.AllergyContainedItem
import com.nudriin.fits.data.dto.allergy.AllergyDetectRequest
import com.nudriin.fits.data.dto.gemini.Contents
import com.nudriin.fits.data.dto.gemini.GeminiGenerationResponse
import com.nudriin.fits.data.dto.gemini.GeminiRequest
import com.nudriin.fits.data.dto.gemini.Part
import com.nudriin.fits.data.dto.llm.LlmRequest
import com.nudriin.fits.data.dto.llm.LlmResponse
import com.nudriin.fits.data.dto.product.AllergyItem
import com.nudriin.fits.data.dto.product.ProductSaveRequest
import com.nudriin.fits.databinding.ActivityCameraBinding
import com.nudriin.fits.databinding.DialogCameraBinding
import com.nudriin.fits.databinding.DialogCapturedImgBinding
import com.nudriin.fits.databinding.DialogEditTextBinding
import com.nudriin.fits.ui.allergy.AllergyViewModel
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
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private lateinit var dialogBinding: DialogCameraBinding
    private lateinit var dialogSaveProductBinding: DialogEditTextBinding
    private lateinit var dialogPreviewBinding: DialogCapturedImgBinding
    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var flashMode: Int = ImageCapture.FLASH_MODE_OFF
    private var camera: Camera? = null
    private var isFlashOn = false
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private var snapState = 1
    private var nutritionData: String? = null
    private var improveGeneration: GeminiGenerationResponse? = null
    private var llmResponse: LlmResponse? = null
    private lateinit var healthRecommendationHelper: HealthRecommendationHelper
    private val appSettingsViewModel: AppSettingsViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private val productViewModel: ProductViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private val allergyViewModel: AllergyViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private var summary: HealthRecommendationSummary? = null
    private var analysisGrade: String? = null
    private lateinit var ocrHelper: OcrHelper
    private lateinit var ocrRealtimeHelper: OcrHelper

    private lateinit var bitmapImage: Bitmap
    private var ocrImageResult: Bitmap? = null
    private var detectedAllergy: String? = null
    private var allergyContained: List<AllergyContainedItem>? = null
    private var dialog: Dialog? = null
    private var savedDialog: Dialog? = null
    private var previewDialog: Dialog? = null

    private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCameraBinding.inflate(layoutInflater)
        dialogBinding = DialogCameraBinding.inflate(layoutInflater)
        dialogSaveProductBinding = DialogEditTextBinding.inflate(layoutInflater)
        dialogPreviewBinding = DialogCapturedImgBinding.inflate(layoutInflater)
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
        startCamera()
    }


    private fun startCamera() {
        ocrRealtimeHelper = OcrHelper(
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
                    imageHeight: Int,
                    imageWidth: Int,
                    boundingImg: Bitmap?
                ) {
                    runOnUiThread {
                        Log.d("REALTIME_OCR_RES", results.joinToString())
                        if (results.isNotEmpty() && results.size == 4) {
                            binding.realtimeOverlay.updateBoundingBox(
                                results,
                                imageWidth,
                                imageHeight
                            )
                        } else {
                            binding.realtimeOverlay.clear()
                        }
                    }
                }

            }
        )

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val resolutionSelector = ResolutionSelector.Builder()
                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
                .build()


            val imageAnalyzer = ImageAnalysis.Builder()
                .setResolutionSelector(resolutionSelector)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()

            imageAnalyzer.setAnalyzer(Executors.newSingleThreadExecutor()) { image ->
                ocrRealtimeHelper.detectRealtimeObject(image)
                image.close()
            }

            imageCapture = ImageCapture.Builder()
                .setTargetRotation(Surface.ROTATION_0)
                .setFlashMode(flashMode)
                .build()

            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                    }

                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer
                )
            } catch (exc: Exception) {
                showToast(this, "Failed to open camera.")
                Log.e(TAG, "startCamera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    @Suppress("DEPRECATION")
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
                            bitmapImage =
                                MediaStore.Images.Media.getBitmap(contentResolver, output.savedUri)
                            val rotation = getExifRotation(output.savedUri!!, this@CameraActivity)
                            val rotatedBitmap = rotateBitmap(bitmapImage, rotation.toFloat())
                            val croppedUri = cropImage(output.savedUri!!)
                            ocrHelper.detectObject(rotatedBitmap)
                            val textRecognizer =
                                TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                            val inputImage: InputImage = if (ocrImageResult != null) {
                                InputImage.fromBitmap(ocrImageResult!!, Surface.ROTATION_0)
                            } else {
                                InputImage.fromFilePath(this@CameraActivity, croppedUri)
                            }

                            textRecognizer.process(inputImage)
                                .addOnSuccessListener { visionText: Text ->
                                    val detectedText: String = visionText.text
                                    if (detectedText.isNotBlank()) {
                                        showToast(
                                            this@CameraActivity,
                                            "Success get nutritional data"
                                        )
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
                            showLoading(false)
                            binding.realtimeOverlay.visibility = View.GONE
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
                                        showToast(
                                            this@CameraActivity,
                                            "Success get composition data"
                                        )
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
                    showToast(this@CameraActivity, "Failed to take the picture.")
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

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        val title = getString(R.string.instruction, "One")
        val message = getString(R.string.nutrition_table_instruction)
        showDialog(title, message)

        healthRecommendationHelper = HealthRecommendationHelper(
            context = this,
            onResult = { _, result ->
                var isDiabetes = false
                appSettingsViewModel.getSettings().observe(
                    this@CameraActivity
                ) { settings ->
                    isDiabetes = settings.diabetes
                }

                analysisGrade = improveGeneration!!.grade

                summary =
                    healthRecommendationHelper.recommendationSummary(
                        analysisGrade ?: "",
                        isDiabetes,
                        improveGeneration!!.sugar,
                        improveGeneration!!.fat,
                        improveGeneration!!.protein,
                        improveGeneration!!.calories
                    )

                setAnalysisResult(summary!!, analysisGrade)
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
                    imageHeight: Int,
                    imageWidth: Int,
                    boundingImg: Bitmap?
                ) {
                    runOnUiThread {
                        Log.d("OCR_CAMERA_RES", results.joinToString())
                        showPreviewDialog(boundingImg!!)
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

        val sugarIng =
            resources.getString(R.string.content_ing, improveGeneration?.sugarIng ?: "")
        val fatIng =
            resources.getString(R.string.content_ing, improveGeneration?.fatIng ?: "")
        val proteinIng =
            resources.getString(R.string.content_ing, improveGeneration?.proteinIng ?: "")
        val caloriesIng =
            resources.getString(R.string.content_ing, improveGeneration?.caloriesIng ?: "")

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

        val sugar = HealthAnalysis(
            "Sugar ${improveGeneration?.sugar ?: ""}",
            summary.sugar,
            sugarIng
        )
        val fat = HealthAnalysis(
            "Fat ${improveGeneration?.fat ?: ""}",
            summary.fat,
            fatIng
        )
        val protein =
            HealthAnalysis(
                "Protein ${improveGeneration?.protein ?: ""}",
                summary.protein,
                proteinIng
            )
        val calories =
            HealthAnalysis(
                "Calories ${improveGeneration?.calories ?: ""}",
                summary.calories,
                caloriesIng
            )

        val analysisList = listOf<HealthAnalysis>(sugar, fat, protein, calories)

        val adapter = AnalysisAdapter(analysisList)
        binding.rvAnalysisResult.adapter = adapter
    }

    private fun showDialog(title: String, message: String) {
        dialog = Dialog(this, R.style.CustomDialogTheme)

        if (dialogBinding.root.parent != null) {
            (dialogBinding.root.parent as ViewGroup).removeView(dialogBinding.root)
        }

        dialogBinding.tvTitle.text = title
        dialogBinding.tvDescription.text = HtmlCompat.fromHtml(
            message,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        dialog?.setContentView(dialogBinding.root)
        dialog?.setCanceledOnTouchOutside(false)

        dialogBinding.btnUnderstand.setOnClickListener {
            dialog?.cancel()
        }

        appSettingsViewModel.getSettings().observe(
            this
        ) { settings ->
            if (settings.instruction) {
                dialog?.show()
            } else {
                dialog?.hide()
            }
        }

    }

    private fun showSaveProductDialog() {
        savedDialog = Dialog(this, R.style.CustomDialogTheme)

        if (dialogSaveProductBinding.root.parent != null) {
            (dialogSaveProductBinding.root.parent as ViewGroup).removeView(dialogSaveProductBinding.root)
        }

        savedDialog?.setContentView(dialogSaveProductBinding.root)
        savedDialog?.setCanceledOnTouchOutside(false)

        dialogSaveProductBinding.btnClose.setOnClickListener {
            savedDialog?.cancel()
        }

        dialogSaveProductBinding.btnSave.setOnClickListener {
            val productName = dialogSaveProductBinding.edtProductName.text.toString()
            var request: ProductSaveRequest
            val allergyRequest = allergyContained?.map {
                AllergyItem(it.id)
            } ?: listOf()
            improveGeneration.let {
                request = ProductSaveRequest(
                    gradesId = getGradeId(analysisGrade!!)!!,
                    name = productName,
                    calories = summary!!.calories,
                    caloriesIng = improveGeneration?.caloriesIng ?: "0.0",
                    protein = summary!!.protein,
                    proteinIng = improveGeneration?.proteinIng ?: "0.0",
                    fat = summary!!.fat,
                    fatIng = improveGeneration?.fatIng ?: "0.0",
                    fiber = "5 g",
                    fiberIng = "oats, flaxseed",
                    carbo = "20 g",
                    carboIng = "wheat, rice",
                    sugar = summary!!.sugar,
                    sugarIng = improveGeneration?.sugarIng ?: "0.0",
                    allergy = allergyRequest,
                    overall = "${summary!!.overall} ${summary?.warning ?: ""}",
                    healthAssessment = llmResponse?.data!!
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

        savedDialog?.show()
    }

    private fun showPreviewDialog(image: Bitmap) {
        previewDialog = Dialog(this, R.style.CustomDialogTheme)

        if (dialogPreviewBinding.root.parent != null) {
            (dialogPreviewBinding.root.parent as ViewGroup).removeView(dialogPreviewBinding.root)
        }

        previewDialog?.setContentView(dialogPreviewBinding.root)
        previewDialog?.setCanceledOnTouchOutside(false)

        dialogPreviewBinding.ivPreview.setImageBitmap(image)

        dialogPreviewBinding.btnCancel.setOnClickListener {
            previewDialog?.cancel()
            recreate()
        }

        dialogPreviewBinding.btnProcess.setOnClickListener {
            ocrImageResult = image
            previewDialog?.dismiss()
            val title = getString(R.string.instruction, "Two")
            val message = getString(R.string.composition_instruction)
            showDialog(title, message)
        }

        appSettingsViewModel.getSettings().observe(
            this
        ) { settings ->
            if (settings.instruction) {
                previewDialog?.show()
            } else {
                previewDialog?.hide()
            }
        }

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

                        improveGeneration = Gson().fromJson(
                            jsonString,
                            GeminiGenerationResponse::class.java
                        )

                        val inputValues =
                            floatArrayOf(
                                improveGeneration?.sugar?.toFloat()!!,
                                improveGeneration?.fat?.toFloat()!!,
                                improveGeneration?.protein?.toFloat()!!,
                                improveGeneration?.calories?.toFloat()!!
                            )

                        Log.d(
                            "HEALTH_REC_INPUT",
                            inputValues.joinToString()
                        )
                        setLlmResponse(improveGeneration)
                        healthRecommendationHelper.predict(inputValues)
                        detectUserAllergy(improveGeneration)
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

    private fun setLlmResponse(data: GeminiGenerationResponse?) {
        if (data == null) {
            return
        }

        val request = LlmRequest(
            resources.getString(
                R.string.llm_prompt,
                data.calories,
                data.fat,
                data.sugar,
                data.protein
            )
        )
        productViewModel.generateLlm(request).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                    binding.tvAssessmentBottomSheet.text = "Retrieving the data ..."
                }

                is Result.Success -> {
                    showLoading(false)
                    llmResponse = result.data
                    binding.tvAssessmentBottomSheet.text = result.data.data
                }

                is Result.Error -> {
                    showLoading(false)
                    binding.tvAssessmentBottomSheet.text = "No data"
                    result.error.getContentIfNotHandled().let { toastText ->
                        showToast(this, toastText.toString())
                    }
                }
            }
        }
    }

    private fun detectUserAllergy(data: GeminiGenerationResponse?) {
        if (data == null) {
            return
        }

        val ingredient = "${data.sugarIng} ${data.fatIng} ${data.proteinIng} ${data.caloriesIng}"
        val input = ingredient
            .replace(",", "")
            .replace("No data", "")
            .replace("\\s+".toRegex(), " ")
            .trim()

        allergyViewModel.detectAllergy(AllergyDetectRequest(input)).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                    binding.tvAllergenBottomSheet.text = "Retrieving the data ..."
                }

                is Result.Success -> {
                    showLoading(false)
                    result.data.allergyContained.map {
                        detectedAllergy = it.allergen
                    }

                    allergyContained = result.data.allergyContained

                    if (detectedAllergy == "") {
                        detectedAllergy = "No allergy detected"
                    }

                    binding.tvAllergenBottomSheet.text = detectedAllergy ?: "No allergy detected"
                }

                is Result.Error -> {
                    showLoading(false)
                    binding.tvAllergenBottomSheet.text = "No allergy detected"
                    result.error.getContentIfNotHandled().let { toastText ->
                        showToast(this, toastText.toString())
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

    override fun onPause() {
        super.onPause()
        dialog?.dismiss()
        savedDialog?.dismiss()
        previewDialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        healthRecommendationHelper.close()
        ocrHelper.close()
        dialog?.dismiss()
        savedDialog?.dismiss()
        previewDialog?.dismiss()
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

    fun rotateBitmap(source: Bitmap, rotation: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(rotation)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    fun getExifRotation(imageUri: Uri, context: Context): Int {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val exif = ExifInterface(inputStream!!)
        return when (exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    }

}