package com.nudriin.fits.utils

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import com.google.android.gms.tflite.java.TfLite
import com.nudriin.fits.data.domain.HealthRecommendationSummary
import org.tensorflow.lite.InterpreterApi
import org.tensorflow.lite.gpu.GpuDelegateFactory
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class HealthRecommendationHelper(
    private val modelName: String = "model3.tflite",
    val context: Context,
    private val onResult: (String, FloatArray) -> Unit,
    private val onError: (String) -> Unit
) {
    private var isGPUSupported: Boolean = false
    private var interpreter: InterpreterApi? = null

    init {
        TfLiteGpu.isGpuDelegateAvailable(context).onSuccessTask { gpuAvailable ->
            val optionBuilder = TfLiteInitializationOptions.builder()
            if (gpuAvailable) {
                optionBuilder.setEnableGpuDelegateSupport(true)
                isGPUSupported = true
            }
            TfLite.initialize(context, optionBuilder.build())
        }.addOnSuccessListener {
            loadLocalModel()
        }.addOnFailureListener {
            onError("TFLite is not initialized!")
        }
    }

    fun predict(input: FloatArray) {
        if (interpreter == null) {
            return
        }

        val inputArray = arrayOf(input)
        val outputArray = Array(1) { FloatArray(4) }

        try {
            interpreter?.run(inputArray, outputArray)
            Log.d(TAG, outputArray[0].joinToString(", "))
            val predictedGrades = outputArray[0]
            val predictedIndex = predictedGrades.indices.maxByOrNull { predictedGrades[it] } ?: -1
            val predictedLabel = when (predictedIndex) {
                0 -> "A"
                1 -> "B"
                2 -> "C"
                3 -> "D"
                else -> "Unknown"
            }
            onResult(predictedLabel, input)
        } catch (e: Exception) {
            onError("TFLite interpreter not loaded")
            Log.e(TAG, e.message.toString())
        }
    }


    private fun initInterpreter(model: Any) {
        interpreter?.close()
        try {
            val options = InterpreterApi.Options()
                .setRuntime(InterpreterApi.Options.TfLiteRuntime.FROM_SYSTEM_ONLY)

            if (isGPUSupported) {
                options.addDelegateFactory(GpuDelegateFactory())
            }

            if (model is ByteBuffer) {
                interpreter = InterpreterApi.create(model, options)
            }
        } catch (e: Exception) {
            onError(e.message.toString())
            Log.e(TAG, e.message.toString())
        }
    }

    fun close() {
        interpreter?.close()
    }

    private fun loadLocalModel() {
        try {
            val buffer: ByteBuffer = loadModelFile(context.assets, modelName)
            initInterpreter(buffer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        assetManager.openFd(modelPath).use { fileDecriptor ->
            FileInputStream(fileDecriptor.fileDescriptor).use { inputStream ->
                val fileChannel = inputStream.channel
                val startOffset = fileDecriptor.startOffset
                val declaredLength = fileDecriptor.declaredLength
                return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            }
        }
    }

    fun classifyFood(input: FloatArray): String {
        if (input.size != 4) {
            throw IllegalArgumentException("Input must contain exactly 4 elements: Sugar, Fat, Protein, Calories")
        }

        val sugar = input[0]
        val fat = input[1]
        val protein = input[2]
        val calories = input[3]

        return when {
            calories in 10.0..100.0 && fat in 0.0..5.0 && sugar in 0.0..3.0 && protein in 20.0..35.0 -> "A"
            calories in 100.0..250.0 && fat in 5.0..10.0 && sugar in 3.0..10.0 && protein in 10.0..20.0 -> "B"
            calories in 250.0..400.0 && fat in 10.0..20.0 && sugar in 10.0..20.0 && protein in 3.0..10.0 -> "C"
            calories in 400.0..600.0 && fat in 20.0..50.0 && sugar in 20.0..50.0 && protein in 0.0..3.0 -> "D"
            else -> "D"
        }
    }


    fun recommendationSummary(
        grade: String,
        isDiabetes: Boolean = false,
        sugar: String,
        fat: String,
        protein: String,
        calories: String
    ): HealthRecommendationSummary {
        val healthRecommendationSummary: HealthRecommendationSummary
        when (grade) {
            "A" -> {
                healthRecommendationSummary = HealthRecommendationSummary(
                    "Healthy Food",
                    "Excellent for health. This food is nutrient-dense and can help maintain weight and heart health",
                    "Low (About ${sugar}g per serving)",
                    "Low (About ${fat}g per serving, with minimal saturated fat)",
                    "High (About ${protein}g per serving)",
                    "Moderate (About ${calories} calories per serving)",
                    warning = if (isDiabetes) "Note: This food is suitable for individuals with diabetes due to its low sugar content." else ""
                )
            }

            "B" -> {
                healthRecommendationSummary = HealthRecommendationSummary(
                    "Good Food",
                    "Good for health. This food can be part of a balanced diet, although portion size and frequency of consumption should be monitored",
                    "Moderate (About ${sugar}g per serving)",
                    "Moderate (About ${fat}g per serving, with controlled saturated fat)",
                    "Moderate (About ${protein}g per serving)",
                    "Moderate (About ${calories} calories per serving)",
                    warning = if (isDiabetes) "Note: Individuals with diabetes should consume this food in moderation due to its moderate sugar content." else ""
                )
            }

            "C" -> {
                healthRecommendationSummary = HealthRecommendationSummary(
                    "Fair Food",
                    "Should be consumed with caution. This food can be an occasional choice, but it should not be a mainstay in daily diet",
                    "High (About ${sugar}g per serving)",
                    "High (About ${fat}g per serving, with some saturated fat)",
                    "Low (About ${protein}g per serving)",
                    "High (About ${calories} calories per serving)",
                    warning = if (isDiabetes) "Warning: This food contains high sugar levels and is not recommended for individuals with diabetes." else ""
                )
            }

            "D" -> {
                healthRecommendationSummary = HealthRecommendationSummary(
                    "Unhealthy Food",
                    "Not recommended for regular consumption. This food can contribute to health issues such as obesity, diabetes, and heart disease",
                    "Very high (About ${sugar}g per serving)",
                    "Very high (About ${fat}g per serving, with high saturated fat)",
                    "Very low (About ${protein}g per serving)",
                    "Very high (About ${calories} calories per serving)",
                    warning = if (isDiabetes) "Warning: This food is extremely high in sugar and is unsafe for individuals with diabetes." else ""
                )
            }

            else -> {
                healthRecommendationSummary = HealthRecommendationSummary(
                    "Not Applicable",
                    "The health grade for this food item cannot be determined at the moment. This might be due to incomplete or insufficient data.",
                    "Not available",
                    "Not available",
                    "Not available",
                    "Not available",
                    warning = if (isDiabetes) "Not available" else "Not available"
                )
            }


        }
        return healthRecommendationSummary
    }

    companion object {
        private const val TAG = "HealthRecomendationHelper"
    }

}