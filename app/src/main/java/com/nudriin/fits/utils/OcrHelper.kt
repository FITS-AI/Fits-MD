package com.nudriin.fits.utils

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import androidx.camera.core.ImageProxy
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class OcrHelper(
    private val context: Context,
    private val modelName: String = "opt_ocr.tflite",
    private val detectorListener: DetectorListener?
) {

    private var isGPUSupported: Boolean = false
    private var interpreter: Interpreter? = null

    init {
        initInterpreter()
    }

    fun detectObject(image: Bitmap) {
        if (interpreter == null) {
            detectorListener?.onError("Model is not initialized")
            return
        }

        try {

            val resizedBitmap = Bitmap.createScaledBitmap(image, image.width, image.height, true)
            val inputArray = preprocessImage(resizedBitmap)

            val outputArray = Array(1) { FloatArray(4) }

            interpreter?.run(inputArray, outputArray)

            val boundingBox = outputArray[0]
            val scaledBoundingBox = scaleBoundingBox(boundingBox, image.width, image.height)

            val resultBitmap = drawBoundingBox(image, scaledBoundingBox)

            val croppedBitmap = cropImage(image, scaledBoundingBox)

            detectorListener?.onResults(
                scaledBoundingBox,
                image.height,
                image.width,
                croppedBitmap
            )

        } catch (e: Exception) {
            Log.e(TAG, "Error during inference: ${e.message}")
            detectorListener?.onError("Error during inference")
        }
    }

    fun detectRealtimeObject(image: ImageProxy) {
        if (interpreter == null) {
            detectorListener?.onError("Model is not initialized")
            return
        }

        val resizedBitmap =
            Bitmap.createScaledBitmap(image.toBitmap(), image.width, image.height, true)
        val inputArray = preprocessImage(resizedBitmap)

        val outputArray = Array(1) { FloatArray(4) }

        interpreter?.run(inputArray, outputArray)

        val boundingBox = outputArray[0]
        val scaledBoundingBox = scaleBoundingBox(boundingBox, image.width, image.height)

        val resultBitmap = drawBoundingBox(image.toBitmap(), scaledBoundingBox)

        detectorListener?.onResults(
            scaledBoundingBox,
            image.height,
            image.width,
            resultBitmap
        )
    }

    private fun initInterpreter() {
        interpreter?.close()
        try {
            interpreter = Interpreter(loadModelFile(context.assets, modelName))
        } catch (e: Exception) {
            detectorListener?.onError(e.message.toString())
            Log.e(TAG, e.message.toString())
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

    private fun preprocessImage(bitmap: Bitmap): Array<Array<Array<FloatArray>>> {
        val inputArray = Array(1) { Array(224) { Array(224) { FloatArray(3) } } }
        for (y in 0 until 224) {
            for (x in 0 until 224) {
                val pixel = bitmap.getPixel(x, y)
                inputArray[0][y][x][0] = Color.red(pixel) / 255.0f
                inputArray[0][y][x][1] = Color.green(pixel) / 255.0f
                inputArray[0][y][x][2] = Color.blue(pixel) / 255.0f
            }
        }
        return inputArray
    }

    private fun scaleBoundingBox(
        boundingBox: FloatArray,
        originalWidth: Int,
        originalHeight: Int,
        offsetX: Float = 0f,
        offsetY: Float = 0f
    ): FloatArray {
        val yMin = boundingBox[0] * originalHeight + offsetY
        val xMin = boundingBox[1] * originalWidth + offsetX
        val yMax = boundingBox[2] * originalHeight + offsetY
        val xMax = boundingBox[3] * originalWidth + offsetX
        return floatArrayOf(yMin, xMin, yMax, xMax)
    }

    private fun drawBoundingBox(bitmap: Bitmap, boundingBox: FloatArray): Bitmap {
        val resultBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(resultBitmap)
        val paint = Paint()
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 8f

        val yMin = boundingBox[0].toInt()
        val xMin = boundingBox[1].toInt()
        val yMax = boundingBox[2].toInt()
        val xMax = boundingBox[3].toInt()

        canvas.drawRect(xMin.toFloat(), yMin.toFloat(), xMax.toFloat(), yMax.toFloat(), paint)
        return resultBitmap
    }

    private fun cropImage(bitmap: Bitmap, boundingBox: FloatArray): Bitmap {
        val yMin = boundingBox[0].toInt()
        val xMin = boundingBox[1].toInt()
        val yMax = boundingBox[2].toInt()
        val xMax = boundingBox[3].toInt()

        val cropX = xMin.coerceIn(0, bitmap.width)
        val cropY = yMin.coerceIn(0, bitmap.height)
        val cropWidth = (xMax - xMin).coerceIn(0, bitmap.width - cropX)
        val cropHeight = (yMax - yMin).coerceIn(0, bitmap.height - cropY)

        return Bitmap.createBitmap(bitmap, cropX, cropY, cropWidth, cropHeight)
    }

    fun close() {
        interpreter?.close()
    }

    interface DetectorListener {
        fun onError(error: String)
        fun onResults(
            results: FloatArray,
            imageHeight: Int,
            imageWidth: Int,
            boundingImg: Bitmap? = null
        )
    }

    companion object {
        private const val TAG = "OcrHelper"
    }
}
