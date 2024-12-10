package com.nudriin.fits.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import com.nudriin.fits.ml.Ocr
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp

class OcrHelper(
    private val context: Context,
    private val detectorListener: DetectorListener?
) {
    private var model: Ocr? = null

    init {
        try {
            model = Ocr.newInstance(context)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize the model: ${e.message}")
            detectorListener?.onError("Failed to initialize the model")
        }
    }

    fun detectObject(image: Bitmap) {
        if (model == null) {
            detectorListener?.onError("Model is not initialized")
            return
        }

        try {
            // Preprocess the image to match the model's expected input size
            val tensorImage = preprocessImage(image)

            // Record inference start time
            var inferenceTime = SystemClock.uptimeMillis()

            val byteBuffer = tensorImage.buffer

            // Run inference
            val outputs = model!!.process(tensorImage.tensorBuffer)
            inferenceTime = SystemClock.uptimeMillis() - inferenceTime

            // Extract results
            val probabilities = outputs.outputFeature0AsTensorBuffer
            println(probabilities)
            detectorListener?.onResults(
                "Success",
                inferenceTime,
                tensorImage.height,
                tensorImage.width
            )

//            Log.d(TAG, "Results: ${results.joinToString()}")
        } catch (e: Exception) {
            Log.e(TAG, "Error during inference: ${e.message}")
            detectorListener?.onError("Error during inference")
        }
    }

    private fun preprocessImage(image: Bitmap): TensorImage {
        val imageProcessor = ImageProcessor.Builder()
            .add(
                ResizeOp(
                    224,
                    224,
                    ResizeOp.ResizeMethod.BILINEAR
                )
            ) // Resize to match model input size
            .add(NormalizeOp(-1.0f, 1.0f)) // Normalize pixel values to [-1, 1]
            .build()

        val tensorImage = TensorImage()
        tensorImage.load(image)
        return imageProcessor.process(tensorImage)
    }

    fun close() {
        model?.close()
    }

    interface DetectorListener {
        fun onError(error: String)
        fun onResults(
            results: String, // Adjusted for model binding output type
            inferenceTime: Long,
            imageHeight: Int,
            imageWidth: Int
        )
    }

    companion object {
        private const val TAG = "OcrHelper"
    }
}
