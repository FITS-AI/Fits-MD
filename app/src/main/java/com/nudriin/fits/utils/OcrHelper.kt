package com.nudriin.fits.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.SystemClock
import android.util.Log
import com.nudriin.fits.ml.Ocr
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.CastOp
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
            val results = probabilities.floatArray

            // Get the original image dimensions
            val originalWidth = image.width
            val originalHeight = image.height

            // Assuming the model's expected input size is stored in a variable
            val modelInputWidth = 224
            val modelInputHeight = 224

            // Check if scaling is necessary based on model input size
            val needsScaling =
                originalWidth != modelInputWidth || originalHeight != modelInputHeight

            // Descale bounding boxes if necessary
            val scaledResults = if (needsScaling) {
                val scaleX = originalWidth.toFloat() / modelInputWidth.toFloat()
                val scaleY = originalHeight.toFloat() / modelInputHeight.toFloat()
                val scaledResults = FloatArray(results.size)
                for (i in results.indices step 4) {
                    scaledResults[i] = results[i] * scaleX  // Left X
                    scaledResults[i + 1] = results[i + 1] * scaleY // Top Y
                    scaledResults[i + 2] = results[i + 2] * scaleX // Right X
                    scaledResults[i + 3] = results[i + 3] * scaleY // Bottom Y
                }
                scaledResults
            } else {
                results
            }

//            val boundingBox = denormalizedBoundingBox(
//                results[1], results[2], results[3], results[4],
//                image.width, image.height
//            )

            val bitmapImage = drawBoundingBoxes(image, scaledResults)

            // Update the listener with the results, including the bounding box
            detectorListener?.onResults(
                scaledResults,
                inferenceTime,
                image.height,
                image.width,
                bitmapImage
            )

            Log.d(TAG, "Results: ${results.joinToString()}")
            Log.d(TAG, "Results: $results")
        } catch (e: Exception) {
            Log.e(TAG, "Error during inference: ${e.message}")
            detectorListener?.onError("Error during inference")
        }
    }

    private fun preprocessImage(image: Bitmap): TensorImage {
        val detectionImageMeans =
            floatArrayOf(103.94.toFloat(), 116.78.toFloat(), 123.68.toFloat())
        val detectionImageStds = floatArrayOf(1.toFloat(), 1.toFloat(), 1.toFloat())
        val imageProcessor = ImageProcessor.Builder()
            .add(
                ResizeOp(
                    224,
                    224,
                    ResizeOp.ResizeMethod.BILINEAR
                )
            ) // Resize to match model input size
            .add(NormalizeOp(detectionImageMeans, detectionImageStds))
            .add(CastOp(DataType.FLOAT32))
            .build()

        val tensorImage = TensorImage()
        tensorImage.load(image)
        return imageProcessor.process(tensorImage)
    }

    fun close() {
        model?.close()
    }

    private fun denormalizedBoundingBox(
        xMin: Float, yMin: Float, xMax: Float, yMax: Float,
        imageWidth: Int, imageHeight: Int
    ): Rect {
        return Rect(
            (xMin * imageWidth).toInt(),
            (yMin * imageHeight).toInt(),
            (xMax * imageWidth).toInt(),
            (yMax * imageHeight).toInt()
        )
    }

    private fun drawBoundingBoxes(
        bitmap: Bitmap,
        results: FloatArray
    ): Bitmap {
        // Copy the original bitmap to create a mutable version
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        val paint = Paint()
//        var scaleFactors = max(width * 1f / imageWidth, height * 1f / imageHeight)

        paint.style = Paint.Style.STROKE
        paint.color = Color.RED
        paint.strokeWidth = 8f


        // Process the output results
        val imageWidth = bitmap.width
        val imageHeight = bitmap.height
//        val scaleFactor = max(imageWidth * 1f / imageWidth, imageHeight * 1f / imageHeight)


        val xMin = results[0] * imageWidth
        val yMin = results[1] * imageHeight
        val xMax = results[2] * imageWidth
        val yMax = results[3] * imageHeight

        // Draw bounding box
        canvas.drawRect(
            xMin,
            yMin,
            xMax,
            yMax,
            paint
        )

        return mutableBitmap
    }

    interface DetectorListener {
        fun onError(error: String)
        fun onResults(
            results: FloatArray,
            inferenceTime: Long,
            imageHeight: Int,
            imageWidth: Int,
            boundingImg: Bitmap? = null
        )
    }

    companion object {
        private const val TAG = "OcrHelper"
    }
}