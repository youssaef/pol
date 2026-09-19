package com.example.gameplay.runner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.audio.SoundManager
import com.example.data.db.GameRepository
import com.example.data.education.EducationalRepository
import com.example.data.models.AgeGroup
import com.example.data.models.HeroDefinition
import com.example.data.models.PowerUpType
import com.example.data.models.WorldDefinition
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

class RunnerViewModel(
    val world: WorldDefinition,
    val hero: HeroDefinition,
    val ageGroup: AgeGroup,
    private val repository: GameRepository,
    val soundManager: SoundManager
) : ViewModel() {

    private val spawner = TrackSpawner(world.id, ageGroup)

    private val _gameState = MutableStateFlow(RunnerGameState())
    val gameState: StateFlow<RunnerGameState> = _gameState.asStateFlow()

    private val _entities = MutableStateFlow<List<TrackEntity>>(emptyList())
    val entities: StateFlow<List<TrackEntity>> = _entities.asStateFlow()

    private val _floatingTexts = MutableStateFlow<List<FloatingText>>(emptyList())
    val floatingTexts: StateFlow<List<FloatingText>> = _floatingTexts.asStateFlow()

    private var gameLoopJob: Job? = null
    private var lastFrameTimeNanos: Long = 0L

    fun startGame() {
        spawner.reset()
        _gameState.value = RunnerGameState()
        _entities.value = emptyList()
        _floatingTexts.value = emptyList()
        lastFrameTimeNanos = System.nanoTime()

        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            var accumulatedRunTimeMs = 0L
            while (isActive && !_gameState.value.isGameOver) {
                if (!_gameState.value.isPaused) {
                    val nowNanos = System.nanoTime()
                    val dtSeconds = if (lastFrameTimeNanos == 0L) 0.016f else ((nowNanos - lastFrameTimeNanos) / 1_000_000_000.0f).coerceIn(0.005f, 0.05f)
                    lastFrameTimeNanos = nowNanos

                    updatePhysics(dtSeconds)
                    accumulatedRunTimeMs += (dtSeconds * 1000).toLong()
                    if (accumulatedRunTimeMs >= 1000L) {
                        accumulatedRunTimeMs = 0L
                        _gameState.update {
                            it.copy(
                                runDurationSeconds = it.runDurationSeconds + 1L,
                                learningSeconds = it.learningSeconds + if (it.bannerQuestion != null) 1L else 0L
                            )
                        }
                    }
                } else {
                    lastFrameTimeNanos = System.nanoTime()
                }
                delay(16) // ~60 FPS
            }
        }
    }

    private fun updatePhysics(dt: Float) {
        val state = _gameState.value

        // 1. Calculate current movement speed (increases with distance + power-up bonus)
        val speedMultiplier = if (state.activePowerUp == PowerUpType.SPEED_BOOST) 1.5f else 1.0f
        val baseSpeed = 22.0f + (state.distanceMeters / 120.0f).coerceAtMost(16.0f)
        val actualSpeed = baseSpeed * speedMultiplier
        val distanceDelta = actualSpeed * dt

        // 2. Update Lane Transition
        var laneProg = state.laneTransitionProgress + (dt * 7.5f)
        var curLane = state.currentLane
        if (laneProg >= 1.0f) {
            laneProg = 1.0f
            curLane = state.targetLane
        }

        // 3. Update Jump
        var isJumping = state.isJumping
        var jumpProg = state.jumpProgress
        if (isJumping) {
            jumpProg += dt * 1.55f // Jump lasts ~650ms
            if (jumpProg >= 1.0f) {
                isJumping = false
                jumpProg = 0.0f
            }
        }

        // 4. Update Slide
        var isSliding = state.isSliding
        var slideProg = state.slideProgress
        if (isSliding) {
            slideProg += dt * 1.45f // Slide lasts ~700ms
            if (slideProg >= 1.0f) {
                isSliding = false
                slideProg = 0.0f
            }
        }

        // 5. Update Invulnerability timer
        var isInvuln = state.isInvulnerable
        var invulnTimer = state.invulnerabilityTimerMs
        if (isInvuln) {
            invulnTimer -= (dt * 1000).toLong()
            if (invulnTimer <= 0) {
                isInvuln = false
                invulnTimer = 0L
            }
        }

        // 6. Update Active Power-Up timer
        var activePwr = state.activePowerUp
        var pwrTimer = state.powerUpRemainingMs
        if (activePwr != null) {
            pwrTimer -= (dt * 1000).toLong()
            if (pwrTimer <= 0) {
                activePwr = null
                pwrTimer = 0L
            }
        }

        // 7. Update Track Entities
        val updatedEntities = spawner.update(distanceDelta, state.distanceMeters + distanceDelta)

        // 8. Check Collisions & Pickups
        var hearts = state.hearts
        var scoreAdd = (distanceDelta * 2).toInt()
        var coinsAdd = 0
        var starsAdd = 0
        var questionsAnsAdd = 0
        var questionsCorAdd = 0
        var bannerQ = state.bannerQuestion

        val playerLane = state.targetLane
        val jumpHeightNorm = if (isJumping) kotlin.math.sin(jumpProg * Math.PI.toFloat()) else 0f

        val it = updatedEntities.iterator()
        while (it.hasNext()) {
            val entity = it.next()

            // Update banner if gate is approaching ahead
            if (entity is TrackEntity.EducationalGate && entity.z in 0f..55f) {
                bannerQ = entity.question
            }

            // Check collision when entity is close to player (z in [-0.8, 1.2])
            if (entity.z in -0.8f..1.4f) {
                when (entity) {
                    is TrackEntity.Coin -> {
                        val canCollect = (entity.lane == playerLane) || (state.activePowerUp == PowerUpType.MAGNET && entity.z in -1.5f..3.0f)
                        if (canCollect && !entity.isCollected) {
                            entity.isCollected = true
                            val coinBonus = if (state.activePowerUp == PowerUpType.COIN_BOOST) 2 else 1
                            coinsAdd += coinBonus
                            scoreAdd += 25 * coinBonus
                            soundManager.playCoin()
                            addFloatingText("+$coinBonus 🪙", 0.5f, 0.7f, 0xFFFDE047)
                        }
                    }
                    is TrackEntity.PowerUp -> {
                        if (entity.lane == playerLane && !entity.isCollected) {
                            entity.isCollected = true
                            activePwr = entity.type
                            pwrTimer = entity.type.durationMs
                            soundManager.playPowerUp()
                            addFloatingText("${entity.type.icon} POWER!", 0.5f, 0.6f, 0xFF38BDF8)
                        }
                    }
                    is TrackEntity.Obstacle -> {
                        if (entity.lane == playerLane) {
                            var avoided = false
                            if (entity.type == ObstacleType.HURDLE_JUMP && jumpHeightNorm > 0.45f) {
                                avoided = true // Successfully jumped over
                            } else if (entity.type == ObstacleType.BARRIER_SLIDE && isSliding) {
                                avoided = true // Successfully ducked under
                            }

                            if (!avoided && !isInvuln) {
                                if (activePwr == PowerUpType.SHIELD) {
                                    // Shield absorbs the blow!
                                    activePwr = null
                                    pwrTimer = 0L
                                    isInvuln = true
                                    invulnTimer = 1500L
                                    soundManager.playWrong()
                                    addFloatingText("🛡️ SHIELD SAVED!", 0.5f, 0.7f, 0xFF38BDF8)
                                } else {
                                    hearts--
                                    isInvuln = true
                                    invulnTimer = 2200L
                                    soundManager.playHeartLoss()
                                    addFloatingText("-1 ❤️", 0.5f, 0.7f, 0xFFEF4444)
                                }
                            }
                        }
                    }
                    is TrackEntity.EducationalGate -> {
                        if (!entity.isPassed) {
                            entity.isPassed = true
                            bannerQ = null
                            val chosenIndex = when (playerLane) {
                                Lane.LEFT -> 0
                                Lane.CENTER -> 1
                                Lane.RIGHT -> 2
                            }
                            questionsAnsAdd++
                            if (chosenIndex == entity.question.correctIndex) {
                                questionsCorAdd++
                                val xpGained = if (activePwr == PowerUpType.XP_BOOST) 200 else 100
                                scoreAdd += 300
                                coinsAdd += 15
                                starsAdd += 1
                                soundManager.playCorrect()
                                addFloatingText("🎉 أحسنت! +$xpGained XP", 0.5f, 0.45f, 0xFF10B981)
                            } else {
                                soundManager.playWrong()
                                addFloatingText("💡 ${entity.question.hint.ar}", 0.5f, 0.45f, 0xFFF59E0B)
                            }
                        }
                    }
                }
            }
        }

        // 9. Update Floating Texts (fade out)
        _floatingTexts.update { list ->
            list.mapNotNull {
                val nextAlpha = it.alpha - (dt * 1.5f)
                val nextY = it.yNorm - (dt * 0.12f)
                if (nextAlpha > 0f) it.copy(alpha = nextAlpha, yNorm = nextY) else null
            }
        }

        // 10. Check Game Over
        val isOver = hearts <= 0
        if (isOver) {
            gameLoopJob?.cancel()
            onGameOver(
                coins = state.coinsCollected + coinsAdd,
                stars = state.starsCollected + starsAdd,
                score = state.score + scoreAdd,
                questionsAns = state.questionsAnswered + questionsAnsAdd,
                questionsCor = state.questionsCorrect + questionsCorAdd,
                runSecs = state.runDurationSeconds,
                learnSecs = state.learningSeconds
            )
        }

        _gameState.update {
            it.copy(
                currentLane = curLane,
                laneTransitionProgress = laneProg,
                isJumping = isJumping,
                jumpProgress = jumpProg,
                isSliding = isSliding,
                slideProgress = slideProg,
                hearts = hearts,
                isInvulnerable = isInvuln,
                invulnerabilityTimerMs = invulnTimer,
                activePowerUp = activePwr,
                powerUpRemainingMs = pwrTimer,
                distanceMeters = it.distanceMeters + distanceDelta,
                score = it.score + scoreAdd,
                coinsCollected = it.coinsCollected + coinsAdd,
                starsCollected = it.starsCollected + starsAdd,
                questionsAnswered = it.questionsAnswered + questionsAnsAdd,
                questionsCorrect = it.questionsCorrect + questionsCorAdd,
                currentSpeed = actualSpeed,
                isGameOver = isOver,
                bannerQuestion = bannerQ
            )
        }
        _entities.value = updatedEntities.toList()
    }

    private fun addFloatingText(text: String, xNorm: Float, yNorm: Float, colorHex: Long) {
        _floatingTexts.update { it + FloatingText(text, xNorm, yNorm, colorHex = colorHex) }
    }

    fun moveLeft() {
        val currentTarget = _gameState.value.targetLane
        val nextLane = when (currentTarget) {
            Lane.RIGHT -> Lane.CENTER
            Lane.CENTER -> Lane.LEFT
            Lane.LEFT -> Lane.LEFT
        }
        if (nextLane != currentTarget) {
            soundManager.playSlide()
            _gameState.update {
                it.copy(
                    currentLane = it.targetLane,
                    targetLane = nextLane,
                    laneTransitionProgress = 0.0f
                )
            }
        }
    }

    fun moveRight() {
        val currentTarget = _gameState.value.targetLane
        val nextLane = when (currentTarget) {
            Lane.LEFT -> Lane.CENTER
            Lane.CENTER -> Lane.RIGHT
            Lane.RIGHT -> Lane.RIGHT
        }
        if (nextLane != currentTarget) {
            soundManager.playSlide()
            _gameState.update {
                it.copy(
                    currentLane = it.targetLane,
                    targetLane = nextLane,
                    laneTransitionProgress = 0.0f
                )
            }
        }
    }

    fun jump() {
        if (!_gameState.value.isJumping && !_gameState.value.isSliding) {
            soundManager.playJump()
            _gameState.update { it.copy(isJumping = true, jumpProgress = 0.0f) }
        }
    }

    fun slide() {
        if (!_gameState.value.isSliding && !_gameState.value.isJumping) {
            soundManager.playSlide()
            _gameState.update { it.copy(isSliding = true, slideProgress = 0.0f) }
        }
    }

    fun togglePause() {
        _gameState.update { it.copy(isPaused = !it.isPaused) }
    }

    private fun onGameOver(
        coins: Int,
        stars: Int,
        score: Int,
        questionsAns: Int,
        questionsCor: Int,
        runSecs: Long,
        learnSecs: Long
    ) {
        viewModelScope.launch {
            val xpEarned = (score / 10) + (questionsCor * 100)
            repository.recordRunResults(
                coinsEarned = coins,
                starsEarned = stars,
                xpEarned = xpEarned,
                runDurationSeconds = max(1L, runSecs),
                learningSeconds = max(1L, learnSecs),
                questionsAnswered = questionsAns,
                questionsCorrect = questionsCor,
                scoreAchieved = score,
                worldId = world.id
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
    }
}
