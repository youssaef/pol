package com.example.gameplay.runner

import com.example.data.models.EducationalQuestion
import com.example.data.models.PowerUpType

enum class Lane(val xOffset: Float) {
    LEFT(-1.0f),
    CENTER(0.0f),
    RIGHT(1.0f)
}

enum class ObstacleType {
    HURDLE_JUMP,     // Must jump over
    BARRIER_SLIDE,   // Must slide under
    BLOCK_LANE       // Must change lane
}

sealed class TrackEntity(
    open var z: Float,
    open val lane: Lane
) {
    data class Coin(
        override var z: Float,
        override val lane: Lane,
        var isCollected: Boolean = false
    ) : TrackEntity(z, lane)

    data class Obstacle(
        override var z: Float,
        override val lane: Lane,
        val type: ObstacleType,
        val labelEmoji: String
    ) : TrackEntity(z, lane)

    data class PowerUp(
        override var z: Float,
        override val lane: Lane,
        val type: PowerUpType,
        var isCollected: Boolean = false
    ) : TrackEntity(z, lane)

    data class EducationalGate(
        override var z: Float,
        val question: EducationalQuestion,
        var isPassed: Boolean = false,
        var chosenLane: Lane? = null
    ) : TrackEntity(z, Lane.CENTER)
}

data class FloatingText(
    val text: String,
    val xNorm: Float,
    val yNorm: Float,
    var alpha: Float = 1.0f,
    val colorHex: Long = 0xFFFBBF24
)

data class RunnerGameState(
    val currentLane: Lane = Lane.CENTER,
    val targetLane: Lane = Lane.CENTER,
    val laneTransitionProgress: Float = 1.0f,
    val isJumping: Boolean = false,
    val jumpProgress: Float = 0.0f,
    val isSliding: Boolean = false,
    val slideProgress: Float = 0.0f,
    val hearts: Int = 3,
    val isInvulnerable: Boolean = false,
    val invulnerabilityTimerMs: Long = 0L,
    val distanceMeters: Float = 0.0f,
    val score: Int = 0,
    val coinsCollected: Int = 0,
    val starsCollected: Int = 0,
    val questionsAnswered: Int = 0,
    val questionsCorrect: Int = 0,
    val currentSpeed: Float = 22.0f,
    val isGameOver: Boolean = false,
    val isPaused: Boolean = false,
    val activePowerUp: PowerUpType? = null,
    val powerUpRemainingMs: Long = 0L,
    val bannerQuestion: EducationalQuestion? = null,
    val runDurationSeconds: Long = 0L,
    val learningSeconds: Long = 0L
)
