package com.example.gameplay.runner

import com.example.data.education.EducationalRepository
import com.example.data.models.AgeGroup
import com.example.data.models.EducationalQuestion
import com.example.data.models.PowerUpType
import kotlin.random.Random

class TrackSpawner(
    private val worldId: String,
    private val ageGroup: AgeGroup
) {
    private val activeEntities = mutableListOf<TrackEntity>()
    private var nextSpawnZ = 30.0f
    private var lastGateZ = 0.0f
    private val gateIntervalMeters = 80.0f

    val entities: List<TrackEntity> get() = activeEntities

    fun update(distanceDelta: Float, currentDistance: Float): List<TrackEntity> {
        // Move all entities towards player
        val iterator = activeEntities.iterator()
        while (iterator.hasNext()) {
            val entity = iterator.next()
            entity.z -= distanceDelta
            // Despawn if passed behind player
            if (entity.z < -5.0f) {
                iterator.remove()
            }
        }

        // Spawn new segments ahead
        while (nextSpawnZ < currentDistance + 100.0f) {
            spawnSegment(nextSpawnZ, currentDistance)
            nextSpawnZ += Random.nextInt(18, 28)
        }

        return activeEntities
    }

    private fun spawnSegment(atZ: Float, currentDistance: Float) {
        val relZ = atZ - currentDistance

        // Check if it's time for an Educational Gate
        if (atZ - lastGateZ >= gateIntervalMeters) {
            val matchingQuestions = EducationalRepository.questions.filter {
                it.worldId == worldId || it.ageGroup == ageGroup
            }
            val question = (if (matchingQuestions.isNotEmpty()) matchingQuestions.random()
            else EducationalRepository.questions.random())

            activeEntities.add(TrackEntity.EducationalGate(z = relZ, question = question))
            lastGateZ = atZ
            return
        }

        val patternType = Random.nextInt(0, 10)
        when (patternType) {
            0, 1, 2 -> {
                // Coin trail on a lane
                val lane = Lane.values().random()
                for (i in 0..3) {
                    activeEntities.add(TrackEntity.Coin(z = relZ + (i * 3.5f), lane = lane))
                }
            }
            3, 4 -> {
                // Obstacle hurdle to jump or slide
                val lane = Lane.values().random()
                val obsType = if (Random.nextBoolean()) ObstacleType.HURDLE_JUMP else ObstacleType.BARRIER_SLIDE
                val emoji = if (obsType == ObstacleType.HURDLE_JUMP) "🪵" else "🚧"
                activeEntities.add(TrackEntity.Obstacle(z = relZ, lane = lane, type = obsType, labelEmoji = emoji))

                // Place coins on other lanes
                val otherLanes = Lane.values().filter { it != lane }
                val safeLane = otherLanes.random()
                activeEntities.add(TrackEntity.Coin(z = relZ, lane = safeLane))
            }
            5, 6 -> {
                // Lane blocker barrier on 1 or 2 lanes
                val blockedLane = Lane.values().random()
                activeEntities.add(TrackEntity.Obstacle(z = relZ, lane = blockedLane, type = ObstacleType.BLOCK_LANE, labelEmoji = "⛔"))
                val openLanes = Lane.values().filter { it != blockedLane }
                activeEntities.add(TrackEntity.Coin(z = relZ, lane = openLanes.random()))
            }
            7 -> {
                // Power-up orb!
                val pType = PowerUpType.values().random()
                val lane = Lane.values().random()
                activeEntities.add(TrackEntity.PowerUp(z = relZ, lane = lane, type = pType))
            }
            else -> {
                // Alternating coin zigzag
                val lanes = Lane.values()
                for (i in 0..2) {
                    activeEntities.add(TrackEntity.Coin(z = relZ + (i * 4.0f), lane = lanes[i]))
                }
            }
        }
    }

    fun removeEntity(entity: TrackEntity) {
        activeEntities.remove(entity)
    }

    fun reset() {
        activeEntities.clear()
        nextSpawnZ = 30.0f
        lastGateZ = 0.0f
    }
}
