package com.example.gameplay.runner

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.example.core.i18n.Language
import com.example.data.models.HeroDefinition
import com.example.data.models.PowerUpType
import com.example.data.models.WorldDefinition
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RunnerCanvas(
    modifier: Modifier = Modifier,
    state: RunnerGameState,
    world: WorldDefinition,
    hero: HeroDefinition,
    entities: List<TrackEntity>,
    floatingTexts: List<FloatingText>,
    animTimeSeconds: Float,
    currentLanguage: Language
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val horizonY = canvasHeight * 0.35f
        val vanishingPointX = canvasWidth * 0.5f

        // 1. Draw Sky & Backdrop
        drawSkyAndHorizon(world, canvasWidth, canvasHeight, horizonY, animTimeSeconds)

        // 2. Draw 3D Perspective Road Track
        drawTrack(world, canvasWidth, canvasHeight, horizonY, vanishingPointX, state.distanceMeters)

        // 3. Draw Track Entities sorted by depth (furthest Z first)
        val sortedEntities = entities.sortedByDescending { it.z }
        for (entity in sortedEntities) {
            if (entity.z in 0.0f..85.0f) {
                drawEntity(
                    entity = entity,
                    canvasWidth = canvasWidth,
                    canvasHeight = canvasHeight,
                    horizonY = horizonY,
                    vanishingPointX = vanishingPointX,
                    animTimeSeconds = animTimeSeconds,
                    textMeasurer = textMeasurer,
                    currentLanguage = currentLanguage
                )
            }
        }

        // 4. Draw Player Hero in 3D perspective
        drawPlayerHero(
            state = state,
            hero = hero,
            canvasWidth = canvasWidth,
            canvasHeight = canvasHeight,
            horizonY = horizonY,
            vanishingPointX = vanishingPointX,
            animTimeSeconds = animTimeSeconds
        )

        // 5. Draw Floating Feedback Texts
        for (ft in floatingTexts) {
            val textLayout = textMeasurer.measure(
                text = ft.text,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(ft.colorHex).copy(alpha = ft.alpha)
                )
            )
            val posX = canvasWidth * ft.xNorm - (textLayout.size.width / 2f)
            val posY = canvasHeight * ft.yNorm
            drawText(textLayout, topLeft = Offset(posX, posY))
        }
    }
}

private fun DrawScope.drawSkyAndHorizon(
    world: WorldDefinition,
    width: Float,
    height: Float,
    horizonY: Float,
    animTime: Float
) {
    // Sky gradient
    val skyBrush = Brush.verticalGradient(
        colors = listOf(
            Color(world.skyColorHex),
            Color(world.primaryColorHex).copy(alpha = 0.6f),
            Color(world.secondaryColorHex).copy(alpha = 0.9f)
        ),
        startY = 0f,
        endY = horizonY
    )
    drawRect(brush = skyBrush, size = Size(width, horizonY))

    // World-specific background skyline / celestial objects
    when (world.id) {
        "space" -> {
            // Draw celestial stars and moon
            for (i in 0..25) {
                val sx = ((i * 73 + (animTime * 5)) % width)
                val sy = (i * 29) % (horizonY * 0.9f)
                val starAlpha = (sin(animTime * 3f + i) * 0.5f + 0.5f).coerceIn(0.2f, 1f)
                drawCircle(Color.White.copy(alpha = starAlpha), radius = 2f + (i % 3), center = Offset(sx, sy))
            }
            // Glowing Planet
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFFF43F5E), Color(0xFF881337)),
                    center = Offset(width * 0.8f, horizonY * 0.4f),
                    radius = 45f
                ),
                radius = 45f,
                center = Offset(width * 0.8f, horizonY * 0.4f)
            )
        }
        "algeria" -> {
            // Glowing warm golden sun
            drawCircle(
                color = Color(0xFFFDE047),
                radius = 40f,
                center = Offset(width * 0.25f, horizonY * 0.45f)
            )
            // Sand dunes silhouette on horizon
            val path = Path().apply {
                moveTo(0f, horizonY)
                cubicTo(width * 0.25f, horizonY - 30f, width * 0.5f, horizonY - 10f, width * 0.75f, horizonY - 25f)
                lineTo(width, horizonY)
                close()
            }
            drawPath(path, color = Color(0xFFD97706).copy(alpha = 0.5f))
        }
        else -> {
            // Friendly distant clouds and hills
            for (c in 0..4) {
                val cx = ((c * 240 + animTime * 15) % (width + 100)) - 50
                val cy = horizonY * 0.35f + (c * 12)
                drawCircle(Color.White.copy(alpha = 0.65f), radius = 30f, center = Offset(cx, cy))
                drawCircle(Color.White.copy(alpha = 0.65f), radius = 40f, center = Offset(cx + 30, cy - 5))
                drawCircle(Color.White.copy(alpha = 0.65f), radius = 25f, center = Offset(cx + 60, cy))
            }
        }
    }

    // Ground scenery below horizon
    drawRect(
        color = Color(world.groundColorHex),
        topLeft = Offset(0f, horizonY),
        size = Size(width, height - horizonY)
    )
}

private fun DrawScope.drawTrack(
    world: WorldDefinition,
    width: Float,
    height: Float,
    horizonY: Float,
    vpX: Float,
    distanceMeters: Float
) {
    val roadTopWidth = width * 0.12f
    val roadBottomWidth = width * 0.75f

    val roadTopLeft = Offset(vpX - roadTopWidth / 2f, horizonY)
    val roadTopRight = Offset(vpX + roadTopWidth / 2f, horizonY)
    val roadBottomLeft = Offset(vpX - roadBottomWidth / 2f, height)
    val roadBottomRight = Offset(vpX + roadBottomWidth / 2f, height)

    // Road asphalt / base path
    val roadPath = Path().apply {
        moveTo(roadTopLeft.x, roadTopLeft.y)
        lineTo(roadTopRight.x, roadTopRight.y)
        lineTo(roadBottomRight.x, roadBottomRight.y)
        lineTo(roadBottomLeft.x, roadBottomLeft.y)
        close()
    }
    drawPath(roadPath, color = Color(0xFF1E293B))

    // Speed perspective stripes across track
    val numSegments = 16
    val segmentOffset = (distanceMeters * 1.5f) % 1.0f

    for (i in 0 until numSegments) {
        val tNorm = ((i + segmentOffset) / numSegments).coerceIn(0f, 1f)
        val nextNorm = ((i + 0.5f + segmentOffset) / numSegments).coerceIn(0f, 1f)

        // Non-linear depth curve for perspective
        val p1 = tNorm * tNorm
        val p2 = nextNorm * nextNorm

        val y1 = horizonY + (height - horizonY) * p1
        val y2 = horizonY + (height - horizonY) * p2

        val w1 = roadTopWidth + (roadBottomWidth - roadTopWidth) * p1
        val w2 = roadTopWidth + (roadBottomWidth - roadTopWidth) * p2

        val slabColor = if (i % 2 == 0) Color(0x18FFFFFF) else Color(0x00000000)
        val slabPath = Path().apply {
            moveTo(vpX - w1 / 2f, y1)
            lineTo(vpX + w1 / 2f, y1)
            lineTo(vpX + w2 / 2f, y2)
            lineTo(vpX - w2 / 2f, y2)
            close()
        }
        drawPath(slabPath, color = slabColor)
    }

    // Lane Dividers (Dashed Perspective Lines)
    val laneFractions = listOf(-0.33f, 0.33f)
    for (frac in laneFractions) {
        for (i in 0 until 12) {
            val tNorm = ((i * 2 + (distanceMeters * 1.5f)) % 24) / 24f
            val p = tNorm * tNorm
            val nextP = ((tNorm + 0.03f) * (tNorm + 0.03f)).coerceAtMost(1f)

            val y1 = horizonY + (height - horizonY) * p
            val y2 = horizonY + (height - horizonY) * nextP
            val curRoadW = roadTopWidth + (roadBottomWidth - roadTopWidth) * p
            val x1 = vpX + (curRoadW * frac)
            val x2 = vpX + ((roadTopWidth + (roadBottomWidth - roadTopWidth) * nextP) * frac)

            drawLine(
                color = Color(0xFFFBBF24),
                start = Offset(x1, y1),
                end = Offset(x2, y2),
                strokeWidth = (1f + p * 6f),
                cap = StrokeCap.Round
            )
        }
    }

    // Colorful Side Curbs with warning stripes
    val curbWidthBottom = 22f
    val leftCurbPath = Path().apply {
        moveTo(roadTopLeft.x, roadTopLeft.y)
        lineTo(roadTopLeft.x - 4f, roadTopLeft.y)
        lineTo(roadBottomLeft.x - curbWidthBottom, height)
        lineTo(roadBottomLeft.x, height)
        close()
    }
    drawPath(leftCurbPath, color = Color(0xFFE11D48))

    val rightCurbPath = Path().apply {
        moveTo(roadTopRight.x, roadTopRight.y)
        lineTo(roadTopRight.x + 4f, roadTopRight.y)
        lineTo(roadBottomRight.x + curbWidthBottom, height)
        lineTo(roadBottomRight.x, height)
        close()
    }
    drawPath(rightCurbPath, color = Color(0xFFE11D48))
}

private fun DrawScope.drawEntity(
    entity: TrackEntity,
    canvasWidth: Float,
    canvasHeight: Float,
    horizonY: Float,
    vanishingPointX: Float,
    animTimeSeconds: Float,
    textMeasurer: TextMeasurer,
    currentLanguage: Language
) {
    val z = entity.z
    val scale = (12.0f / (z + 12.0f)).coerceIn(0.05f, 1.2f)

    val roadTopWidth = canvasWidth * 0.12f
    val roadBottomWidth = canvasWidth * 0.75f

    val screenY = horizonY + (canvasHeight - horizonY) * scale
    val curRoadWidth = roadTopWidth + (roadBottomWidth - roadTopWidth) * scale
    val laneXOffset = entity.lane.xOffset * (curRoadWidth * 0.32f)
    val screenX = vanishingPointX + laneXOffset

    when (entity) {
        is TrackEntity.Coin -> {
            if (!entity.isCollected) {
                // 3D spinning gold coin
                val spinWidth = (16f * scale * abs(cos(animTimeSeconds * 6f))).coerceAtLeast(3f)
                val coinHeight = 22f * scale
                val coinY = screenY - 20f * scale

                // Shadow
                drawOval(
                    color = Color.Black.copy(alpha = 0.35f * scale),
                    topLeft = Offset(screenX - 14f * scale, screenY - 5f * scale),
                    size = Size(28f * scale, 10f * scale)
                )
                // Gold body
                drawOval(
                    brush = Brush.horizontalGradient(
                        listOf(Color(0xFFF59E0B), Color(0xFFFDE047), Color(0xFFD97706))
                    ),
                    topLeft = Offset(screenX - spinWidth, coinY - coinHeight),
                    size = Size(spinWidth * 2f, coinHeight * 2f)
                )
                // Star sparkle
                drawCircle(
                    color = Color.White.copy(alpha = 0.8f),
                    radius = (4f * scale),
                    center = Offset(screenX, coinY)
                )
            }
        }
        is TrackEntity.Obstacle -> {
            val obsW = 46f * scale
            val obsH = 34f * scale

            // Ground shadow
            drawOval(
                color = Color.Black.copy(alpha = 0.4f * scale),
                topLeft = Offset(screenX - obsW * 0.8f, screenY - 4f * scale),
                size = Size(obsW * 1.6f, 12f * scale)
            )

            when (entity.type) {
                ObstacleType.HURDLE_JUMP -> {
                    // Hurdle wood bar
                    drawRoundRect(
                        color = Color(0xFF92400E),
                        topLeft = Offset(screenX - obsW, screenY - obsH),
                        size = Size(obsW * 2f, obsH * 0.4f),
                        cornerRadius = CornerRadius(6f * scale, 6f * scale)
                    )
                    // Hurdle legs
                    drawRect(
                        color = Color(0xFF78350F),
                        topLeft = Offset(screenX - obsW * 0.8f, screenY - obsH),
                        size = Size(8f * scale, obsH)
                    )
                    drawRect(
                        color = Color(0xFF78350F),
                        topLeft = Offset(screenX + obsW * 0.6f, screenY - obsH),
                        size = Size(8f * scale, obsH)
                    )
                }
                ObstacleType.BARRIER_SLIDE -> {
                    // High beam with warning stripes
                    val beamY = screenY - obsH * 1.6f
                    drawRoundRect(
                        color = Color(0xFFDC2626),
                        topLeft = Offset(screenX - obsW * 1.1f, beamY),
                        size = Size(obsW * 2.2f, obsH * 0.5f),
                        cornerRadius = CornerRadius(6f * scale, 6f * scale)
                    )
                    // Downward warning chevron
                    val textLayout = textMeasurer.measure(
                        text = "⬇ SLIDE ⬇",
                        style = TextStyle(fontSize = (10f * scale).coerceAtLeast(6f).sp, color = Color.White, fontWeight = FontWeight.Black)
                    )
                    drawText(textLayout, topLeft = Offset(screenX - textLayout.size.width / 2f, beamY + 2f * scale))
                }
                ObstacleType.BLOCK_LANE -> {
                    // Barrier block
                    drawRoundRect(
                        color = Color(0xFFEA580C),
                        topLeft = Offset(screenX - obsW * 0.8f, screenY - obsH * 1.2f),
                        size = Size(obsW * 1.6f, obsH * 1.2f),
                        cornerRadius = CornerRadius(8f * scale, 8f * scale)
                    )
                    // Warning icon
                    val layout = textMeasurer.measure(
                        text = "⚠️",
                        style = TextStyle(fontSize = (18f * scale).coerceAtLeast(8f).sp)
                    )
                    drawText(layout, topLeft = Offset(screenX - layout.size.width / 2f, screenY - obsH))
                }
            }
        }
        is TrackEntity.PowerUp -> {
            if (!entity.isCollected) {
                val pulse = (sin(animTimeSeconds * 8f) * 0.15f + 1.0f)
                val orbRadius = 24f * scale * pulse
                val orbY = screenY - 28f * scale

                // Glow ring
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Color(0xFF38BDF8), Color(0x0038BDF8)),
                        center = Offset(screenX, orbY),
                        radius = orbRadius * 1.8f
                    ),
                    radius = orbRadius * 1.8f,
                    center = Offset(screenX, orbY)
                )
                // Solid orb
                drawCircle(
                    color = Color(0xFF0284C7),
                    radius = orbRadius,
                    center = Offset(screenX, orbY)
                )
                // Icon emoji
                val iconLayout = textMeasurer.measure(
                    text = entity.type.icon,
                    style = TextStyle(fontSize = (18f * scale).coerceAtLeast(8f).sp)
                )
                drawText(iconLayout, topLeft = Offset(screenX - iconLayout.size.width / 2f, orbY - iconLayout.size.height / 2f))
            }
        }
        is TrackEntity.EducationalGate -> {
            // 3D Grand Interactive Educational Gate Arch spanning all 3 lanes!
            val gateW = curRoadWidth * 1.05f
            val gateH = 75f * scale
            val gateY = screenY - gateH * 1.8f

            // Left & Right Pillars
            drawRoundRect(
                color = Color(0xFF4F46E5),
                topLeft = Offset(vanishingPointX - gateW / 2f, gateY),
                size = Size(16f * scale, gateH * 1.8f),
                cornerRadius = CornerRadius(4f * scale, 4f * scale)
            )
            drawRoundRect(
                color = Color(0xFF4F46E5),
                topLeft = Offset(vanishingPointX + gateW / 2f - 16f * scale, gateY),
                size = Size(16f * scale, gateH * 1.8f),
                cornerRadius = CornerRadius(4f * scale, 4f * scale)
            )

            // Overhead Banner Bar
            drawRoundRect(
                brush = Brush.horizontalGradient(listOf(Color(0xFF4338CA), Color(0xFF6366F1), Color(0xFF4338CA))),
                topLeft = Offset(vanishingPointX - gateW / 2f, gateY),
                size = Size(gateW, gateH * 0.65f),
                cornerRadius = CornerRadius(10f * scale, 10f * scale)
            )

            // Question Text on Arch
            val questionStr = entity.question.question.get(currentLanguage)
            val qLayout = textMeasurer.measure(
                text = "✨ " + questionStr + " ✨",
                style = TextStyle(
                    fontSize = (12f * scale).coerceIn(7f, 22f).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFDE047)
                )
            )
            drawText(
                qLayout,
                topLeft = Offset(vanishingPointX - qLayout.size.width / 2f, gateY + (gateH * 0.15f))
            )

            // 3 Lane Portals (Options)
            val laneOptions = listOf(Lane.LEFT, Lane.CENTER, Lane.RIGHT)
            for (i in 0..2) {
                val l = laneOptions[i]
                val laneOffset = l.xOffset * (curRoadWidth * 0.32f)
                val portalX = vanishingPointX + laneOffset
                val portalY = screenY - gateH * 0.85f
                val portalW = (curRoadWidth * 0.28f).coerceAtLeast(30f)
                val portalH = gateH * 0.9f

                // Portal Frame
                val isCorrectLane = (i == entity.question.correctIndex)
                val portalColor = if (isCorrectLane) Color(0xFF10B981) else Color(0xFF3B82F6)

                drawRoundRect(
                    color = portalColor.copy(alpha = 0.85f),
                    topLeft = Offset(portalX - portalW / 2f, portalY),
                    size = Size(portalW, portalH),
                    cornerRadius = CornerRadius(8f * scale, 8f * scale)
                )

                // Portal Icon & Label
                val choiceText = entity.question.choices.getOrNull(i)?.get(currentLanguage) ?: ""
                val choiceIcon = entity.question.choiceIcons.getOrNull(i) ?: "⭐"
                val choiceLayout = textMeasurer.measure(
                    text = "$choiceIcon $choiceText",
                    style = TextStyle(
                        fontSize = (10f * scale).coerceIn(6f, 16f).sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                )
                drawText(
                    choiceLayout,
                    topLeft = Offset(portalX - choiceLayout.size.width / 2f, portalY + portalH * 0.3f)
                )
            }
        }
    }
}

private fun DrawScope.drawPlayerHero(
    state: RunnerGameState,
    hero: HeroDefinition,
    canvasWidth: Float,
    canvasHeight: Float,
    horizonY: Float,
    vanishingPointX: Float,
    animTimeSeconds: Float
) {
    // If invulnerable from hit, blink player
    if (state.isInvulnerable && (animTimeSeconds * 12).toInt() % 2 == 0) {
        return
    }

    val roadBottomWidth = canvasWidth * 0.75f
    val baseScreenY = canvasHeight * 0.91f

    // Smooth lane position calculation
    val startXOffset = state.currentLane.xOffset
    val targetXOffset = state.targetLane.xOffset
    val curXOffset = startXOffset + (targetXOffset - startXOffset) * state.laneTransitionProgress
    val playerScreenX = vanishingPointX + (curXOffset * (roadBottomWidth * 0.32f))

    // Vertical Jump or Slide offset
    val jumpHeight = if (state.isJumping) {
        sin(state.jumpProgress * PI.toFloat()) * 95f
    } else 0f

    val playerScreenY = baseScreenY - jumpHeight
    val isSliding = state.isSliding

    // 1. Dynamic Ground Shadow
    val shadowScale = (1.0f - (jumpHeight / 140f)).coerceIn(0.3f, 1.0f)
    drawOval(
        color = Color.Black.copy(alpha = 0.45f * shadowScale),
        topLeft = Offset(playerScreenX - (34f * shadowScale), baseScreenY - 6f),
        size = Size(68f * shadowScale, 18f * shadowScale)
    )

    // 2. Running Leg Animation Cycle
    val runCycle = sin(animTimeSeconds * 16f)
    val legSwing = if (state.isJumping || isSliding) 0f else runCycle * 14f

    if (!isSliding) {
        // Legs
        val legWidth = 12f
        val legHeight = 32f
        // Left Leg
        drawRoundRect(
            color = Color(0xFF1E3A8A),
            topLeft = Offset(playerScreenX - 16f, playerScreenY - 32f + legSwing),
            size = Size(legWidth, legHeight),
            cornerRadius = CornerRadius(6f, 6f)
        )
        // Right Leg
        drawRoundRect(
            color = Color(0xFF1E3A8A),
            topLeft = Offset(playerScreenX + 4f, playerScreenY - 32f - legSwing),
            size = Size(legWidth, legHeight),
            cornerRadius = CornerRadius(6f, 6f)
        )
        // Red Sneakers
        drawRoundRect(
            color = Color(0xFFEF4444),
            topLeft = Offset(playerScreenX - 18f, playerScreenY - 4f + legSwing),
            size = Size(16f, 10f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = Color(0xFFEF4444),
            topLeft = Offset(playerScreenX + 2f, playerScreenY - 4f - legSwing),
            size = Size(16f, 10f),
            cornerRadius = CornerRadius(4f, 4f)
        )

        // Torso / Shirt
        val torsoY = playerScreenY - 74f
        drawRoundRect(
            color = Color(hero.bodyColorHex),
            topLeft = Offset(playerScreenX - 22f, torsoY),
            size = Size(44f, 44f),
            cornerRadius = CornerRadius(10f, 10f)
        )

        // Backpack on back
        drawRoundRect(
            color = Color(0xFF4338CA),
            topLeft = Offset(playerScreenX - 16f, torsoY + 4f),
            size = Size(32f, 32f),
            cornerRadius = CornerRadius(8f, 8f)
        )
        // Backpack stripe / emblem
        drawRoundRect(
            color = Color(0xFFFBBF24),
            topLeft = Offset(playerScreenX - 10f, torsoY + 14f),
            size = Size(20f, 8f),
            cornerRadius = CornerRadius(3f, 3f)
        )

        // Arms swinging
        val armSwing = -legSwing * 1.2f
        drawRoundRect(
            color = Color(0xFFFED7AA),
            topLeft = Offset(playerScreenX - 28f, torsoY + 8f + armSwing),
            size = Size(10f, 26f),
            cornerRadius = CornerRadius(5f, 5f)
        )
        drawRoundRect(
            color = Color(0xFFFED7AA),
            topLeft = Offset(playerScreenX + 18f, torsoY + 8f - armSwing),
            size = Size(10f, 26f),
            cornerRadius = CornerRadius(5f, 5f)
        )

        // Head & Cap
        val headY = torsoY - 26f
        drawCircle(
            color = Color(0xFFFED7AA),
            radius = 16f,
            center = Offset(playerScreenX, headY)
        )
        // Hero Cap / Hair
        drawCircle(
            color = Color(hero.shirtColorHex),
            radius = 16.5f,
            center = Offset(playerScreenX, headY - 3f)
        )
    } else {
        // Sliding Crouch Pose
        val slideY = playerScreenY - 28f
        // Tucked Torso
        drawRoundRect(
            color = Color(hero.bodyColorHex),
            topLeft = Offset(playerScreenX - 24f, slideY - 14f),
            size = Size(48f, 26f),
            cornerRadius = CornerRadius(10f, 10f)
        )
        // Backpack
        drawRoundRect(
            color = Color(0xFF4338CA),
            topLeft = Offset(playerScreenX - 18f, slideY - 20f),
            size = Size(36f, 18f),
            cornerRadius = CornerRadius(6f, 6f)
        )
        // Tucked Head
        drawCircle(
            color = Color(hero.shirtColorHex),
            radius = 13f,
            center = Offset(playerScreenX + 16f, slideY - 6f)
        )
        // Slide dust clouds behind
        for (i in 0..2) {
            val dustX = playerScreenX - 32f - (i * 12f)
            val dustY = baseScreenY - 4f - (i * 3f)
            drawCircle(
                color = Color.White.copy(alpha = 0.5f - (i * 0.15f)),
                radius = 6f + (i * 3f),
                center = Offset(dustX, dustY)
            )
        }
    }

    // Power-Up Auras
    if (state.activePowerUp == PowerUpType.SHIELD) {
        // Glowing cyan shield sphere
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0x2238BDF8), Color(0xAA0EA5E9), Color(0xFF38BDF8)),
                center = Offset(playerScreenX, playerScreenY - 45f),
                radius = 58f
            ),
            radius = 58f,
            center = Offset(playerScreenX, playerScreenY - 45f)
        )
    } else if (state.activePowerUp == PowerUpType.SPEED_BOOST) {
        // Speed dash trails
        for (i in 1..3) {
            drawLine(
                color = Color(0xFFFDE047).copy(alpha = 0.7f / i),
                start = Offset(playerScreenX - 20f * i, playerScreenY - 40f),
                end = Offset(playerScreenX - 5f, playerScreenY - 40f),
                strokeWidth = 4f
            )
            drawLine(
                color = Color(0xFFFDE047).copy(alpha = 0.7f / i),
                start = Offset(playerScreenX + 5f, playerScreenY - 40f),
                end = Offset(playerScreenX + 20f * i, playerScreenY - 40f),
                strokeWidth = 4f
            )
        }
    }
}
