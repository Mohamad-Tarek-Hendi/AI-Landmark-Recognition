package com.example.airecognition.domain


/**
 * Represents the result of an AI model classification, providing the name of a landmark and the corresponding score.
 *
 * @property name The name of the landmark as identified by the AI model.
 * @property score The score indicating how confident the AI model is in the correctness of the classification.
 */

data class Classification(
    val name: String,
    val score: Float
)

