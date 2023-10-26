package com.example.airecognition.data

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.airecognition.domain.Classification
import com.example.airecognition.domain.LandmarkClassifier
import com.example.airecognition.presentation.centerCrop
/**
 * ImageAnalyzer implementation for analyzing images from the camera and performing landmark classification using a LandmarkClassifier.
 *
 * @param classifier The LandmarkClassifier used for landmark classification.
 * @param onResults Callback function to receive the classification results.
 */
class LandmarkImageAnalyzer(
    private val classifier: LandmarkClassifier,
    private val onResults: (List<Classification>) -> Unit
) : ImageAnalysis.Analyzer {

    private var frameSkipCount = 0

    // Image >>> bitmap >>> feed our AI model
    /**
     * Analyzes the provided image from the camera and performs landmark classification.
     *
     * @param image The ImageProxy object representing the captured image from the camera.
     */
    override fun analyze(image: ImageProxy) {

        // Skip 60 frames while analyzing 1 frame for better user experience, note : in general we have 60 frames per second
        if (frameSkipCount % 60 == 0) {

            val rotationDegrees = image.imageInfo.rotationDegrees

            // Convert the image to a bitmap and resize it to the required size for classification
            // but the Tensorflow doc show that the model expect only 321 x 321 (aspect ratio)bitmap
            // we must ensure that image is 321 x 321 to able classify (BitmapExt responsible for that)
            val bitmap = image
                .toBitmap()
                .centerCrop(321, 321)

            // Perform landmark classification using the classifier with the bitmap and rotation angle
            val result = classifier.classify(bitmap, rotationDegrees)

            // Invoke the callback function with the classification results
            onResults(result)
        }
        frameSkipCount++

        // Close the ImageProxy to release its resources
        image.close()
    }

}

//This class It will be relevant with Camera X (Direct with Live camera)