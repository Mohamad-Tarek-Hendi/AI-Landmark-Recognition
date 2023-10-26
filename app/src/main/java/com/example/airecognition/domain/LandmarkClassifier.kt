package com.example.airecognition.domain

import android.graphics.Bitmap

/**
 * Interface for a landmark classifier that can classify images and provide a list of classifications.
 */
interface LandmarkClassifier {

    /**
     * Classifies the given bitmap image and returns a list of classifications.
     *
     * @param bitmap The bitmap image to be classified.
     * @param rotation The rotation angle of the image in degrees.
     * @return A list of [Classification] objects representing the identified landmarks and their confidence scores.
     */
    fun classify(bitmap: Bitmap, rotation: Int): List<Classification>
}
