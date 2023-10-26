package com.example.airecognition.data

import android.content.Context
import android.graphics.Bitmap
import android.view.Surface
import com.example.airecognition.domain.Classification
import com.example.airecognition.domain.LandmarkClassifier
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.vision.classifier.ImageClassifier


/**
 * Responsible for classifying images and providing a list of classifications based on landmark recognition.
 *
 * @param context The application context.
 * @param scoreThreshold The minimum confidence score required for a classification to be considered valid. Default is 0.5.
 * @param maxResult The maximum number of classification results to return. Default is 1.
 */

class TfLiteLandmarkClassifier(
    private val context: Context,
    private val scoreThreshold: Float = 0.5f,
    private val maxResult: Int = 1
) : LandmarkClassifier {

    private var classifier: ImageClassifier? = null


    private fun setupClassifier() {
        // Initialize and configure classifier
        val baseOption = BaseOptions.builder()
            .setNumThreads(NUMBER_THREADS)
            .build()

        val options = ImageClassifier.ImageClassifierOptions.builder()
            .setBaseOptions(baseOption)
            .setMaxResults(maxResult)
            .setScoreThreshold(scoreThreshold)
            .build()

        try {
            classifier = ImageClassifier.createFromFileAndOptions(
                context,
                "landmark.tflite",
                options

            )
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    private fun getOrientationFromRotation(rotation: Int): ImageProcessingOptions.Orientation {
        return when (rotation) {
            Surface.ROTATION_270 -> ImageProcessingOptions.Orientation.BOTTOM_RIGHT
            Surface.ROTATION_90 -> ImageProcessingOptions.Orientation.TOP_LEFT
            Surface.ROTATION_180 -> ImageProcessingOptions.Orientation.RIGHT_BOTTOM
            else -> ImageProcessingOptions.Orientation.RIGHT_TOP
        }
    }

    override fun classify(bitmap: Bitmap, rotation: Int): List<Classification> {
        if (classifier == null) {
            setupClassifier()
        }

        // Fit the image to bitmap
        val imageProcessor = ImageProcessor.Builder().build()
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))

        // Rotation
        val imageProcessingOptions = ImageProcessingOptions.builder()
            .setOrientation(getOrientationFromRotation(rotation))
            .build()

        //get result
        val results = classifier?.classify(tensorImage, imageProcessingOptions)

        // transforms into a list of Classification objects

        // We use flatmap to iterate over each classification in the results list and apply a transformation to it.
        // Note classifications represents a single classification and contains a list of categories.
        // We use map function is used to iterate over each category in the classifications list and transform it into a Classification object.
        return results?.flatMap { classifications ->
            classifications.categories.map { category ->
                Classification(name = category.displayName, score = category.score)
            }
        }
            //After the flatMap operation, the resulting list may contain duplicate Classification objects with the same name,
            // so the distinctBy function is used to filter out duplicates based on the name property of each Classification object.
            ?.distinctBy { it.name } ?: emptyList()
    }


    companion object {
        const val NUMBER_THREADS = 2
    }
}