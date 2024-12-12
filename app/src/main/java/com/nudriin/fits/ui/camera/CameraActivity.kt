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
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.nudriin.fits.R
import com.nudriin.fits.adapter.AnalysisAdapter
import com.nudriin.fits.common.ProductViewModel
import com.nudriin.fits.data.domain.HealthAnalysis
import com.nudriin.fits.data.domain.HealthRecommendationSummary
import com.nudriin.fits.data.dto.allergy.AllergyItem
import com.nudriin.fits.data.dto.product.ProductSaveRequest
import com.nudriin.fits.databinding.ActivityCameraBinding
import com.nudriin.fits.databinding.DialogCameraBinding
import com.nudriin.fits.databinding.DialogEditTextBinding
import com.nudriin.fits.ui.appSettings.AppSettingsViewModel
import com.nudriin.fits.utils.HealthRecommendationHelper
import com.nudriin.fits.utils.Result
import com.nudriin.fits.utils.ViewModelFactory
import com.nudriin.fits.utils.getGradeId
import com.nudriin.fits.utils.showToast
import java.io.File
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
    private lateinit var nutritionData: String
    private lateinit var compositionData: String
    private lateinit var healthRecommendationHelper: HealthRecommendationHelper
    private val appSettingsViewModel: AppSettingsViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private val productViewModel: ProductViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private var summary: HealthRecommendationSummary? = null
    private var analysisGrade: String? = null

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
                            showToast(this@CameraActivity, "Success get nutritional fact data!")
                            nutritionData = "Data Gizi Berhasil di ekstrak"
                            snapState = 2
                            val title = getString(R.string.instruction, "Two")
                            val message = getString(R.string.composition_instruction)
                            showDialog(title, message)
                            showLoading(false)
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

                                        // TODO( Change this with analysis result)
                                        val inputString =
                                            "0.0, 1.0, 1.1, 10.0" // Contoh input gula, lemak, protein, kalori
                                        val inputValues =
                                            inputString.split(",").map { it.trim().toFloat() }
                                                .toFloatArray()
                                        healthRecommendationHelper.predict(inputValues)
                                        bottomSheetBehavior.state =
                                            BottomSheetBehavior.STATE_HALF_EXPANDED
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

            val request = ProductSaveRequest(
                gradesId = getGradeId(analysisGrade!!)!!,
                name = productName,
                calories = summary!!.calories,
                caloriesIng = "sugar, flour",
                protein = summary!!.protein,
                proteinIng = "soy, milk",
                fat = summary!!.fat,
                fatIng = "butter, cream",
                fiber = "2 g",
                fiberIng = "oats, flaxseed",
                carbo = "20 g",
                carboIng = "wheat, rice",
                sugar = summary!!.sugar,
                sugarIng = "sucrose, honey",
                allergy = listOf()
            )

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