package com.example.airecognition.data

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.airecognition.domain.Classification
import com.example.airecognition.domain.LandmarkClassifier
import com.example.airecognition.presentation.centerCrop

class LandmarkImageAnalyzer(
    private val classifier: LandmarkClassifier,
    private val onResults: (List<Classification>) -> Unit
) : ImageAnalysis.Analyzer {

    private var frameSkipCount = 0

    // Image >>> bitmap >>> feed our AI model
    override fun analyze(image: ImageProxy) {

        // Skip 60 frames while I Analyse 1 frame (better user experience), note : in general we have 60 frames per second
        if (frameSkipCount % 60 == 0) {
            val rotationDegrees = image.imageInfo.rotationDegrees

            // Note it is good to use transfer image to bitmap direct which can classifier understand
            // but the Tensorflow doc show that the model expect only 321 x 321 (aspect ratio)bitmap
            // we must ensure that image is 321 x 321 to able classify (BitmapExt responsible for that)
            val bitmap = image
                .toBitmap()
                .centerCrop(321, 321)

            val result = classifier.classify(bitmap, rotationDegrees)
            onResults(result)
        }
        frameSkipCount++

        image.close()
    }

}

//This class It will be relevant with Camera X (Direct with Live camera)