package com.example.features.gameplay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.i18n.AppStrings
import com.example.core.i18n.Language
import com.example.data.models.HeroDefinition
import com.example.data.models.WorldDefinition
import com.example.gameplay.runner.RunnerCanvas
import com.example.gameplay.runner.RunnerViewModel
import kotlin.math.abs

@Composable
fun RunnerScreen(
    viewModel: RunnerViewModel,
    world: WorldDefinition,
    hero: HeroDefinition,
    currentLanguage: Language,
    showOnScreenControls: Boolean = true,
    onExitToHome: () -> Unit
) {
    val state by viewModel.gameState.collectAsStateWithLifecycle()
    val entities by viewModel.entities.collectAsStateWithLifecycle()
    val floatingTexts by viewModel.floatingTexts.collectAsStateWithLifecycle()

    val infiniteTransition = rememberInfiniteTransition(label = "runner_time")
    val animTimeSeconds by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 3600f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "anim_seconds"
    )

    LaunchedEffect(Unit) {
        viewModel.startGame()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                var totalDragX = 0f
                var totalDragY = 0f
                detectDragGestures(
                    onDragStart = {
                        totalDragX = 0f
                        totalDragY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        totalDragX += dragAmount.x
                        totalDragY += dragAmount.y
                    },
                    onDragEnd = {
                        val threshold = 35f
                        if (abs(totalDragX) > abs(totalDragY)) {
                            if (totalDragX > threshold) {
                                viewModel.moveRight()
                            } else if (totalDragX < -threshold) {
                                viewModel.moveLeft()
                            }
                        } else {
                            if (totalDragY < -threshold) {
                                viewModel.jump()
                            } else if (totalDragY > threshold) {
                                viewModel.slide()
                            }
                        }
                    }
                )
            }
    ) {
        // 3D Canvas
        RunnerCanvas(
            state = state,
            world = world,
            hero = hero,
            entities = entities,
            floatingTexts = floatingTexts,
            animTimeSeconds = animTimeSeconds,
            currentLanguage = currentLanguage
        )

        // Top HUD
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hearts & World Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Black.copy(alpha = 0.5f),
                    modifier = Modifier.border(1.5.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(world.themeEmoji, fontSize = 18.sp)
                        Text(
                            text = world.name.get(currentLanguage),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Hearts
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (i in 1..3) {
                        Text(
                            text = if (i <= state.hearts) "❤️" else "🖤",
                            fontSize = 20.sp
                        )
                    }
                }
            }

            // Central Stats: Coins & Stars & Score
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HudBadge(icon = "🪙", value = "${state.coinsCollected}")
                HudBadge(icon = "⭐", value = "${state.starsCollected}")
                HudBadge(icon = "🏃", value = "${state.distanceMeters.toInt()}m")
                HudBadge(icon = "🏆", value = "${state.score}")
            }

            // Power-Up indicator & Pause Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.activePowerUp?.let { pwr ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF0284C7).copy(alpha = 0.9f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(pwr.icon, fontSize = 16.sp)
                            Text(
                                text = "${state.powerUpRemainingMs / 1000}s",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { viewModel.togglePause() },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                        .testTag("pause_button")
                ) {
                    Icon(
                        imageVector = if (state.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = "Pause",
                        tint = Color.White
                    )
                }
            }
        }

        // Educational Question Banner (when approaching Gate)
        AnimatedVisibility(
            visible = state.bannerQuestion != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 64.dp)
        ) {
            state.bannerQuestion?.let { q ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF1E1B4B).copy(alpha = 0.92f),
                    modifier = Modifier
                        .border(2.dp, Color(0xFFFBBF24), RoundedCornerShape(20.dp))
                        .shadow(8.dp, RoundedCornerShape(20.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("🧠", fontSize = 24.sp)
                        Column {
                            Text(
                                text = AppStrings.educationalGate.get(currentLanguage),
                                color = Color(0xFFFBBF24),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                            Text(
                                text = q.question.get(currentLanguage),
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // On-Screen Controls for Children (optional toggle)
        if (showOnScreenControls && !state.isGameOver && !state.isPaused) {
            // Left & Right Buttons (Bottom-Left)
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 24.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ControlButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    description = "Left",
                    tag = "control_left"
                ) {
                    viewModel.moveLeft()
                }
                ControlButton(
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    description = "Right",
                    tag = "control_right"
                ) {
                    viewModel.moveRight()
                }
            }

            // Jump & Slide Buttons (Bottom-Right)
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ControlButton(
                    icon = Icons.Default.ArrowDownward,
                    description = "Slide",
                    tag = "control_slide",
                    accentColor = Color(0xFFEA580C)
                ) {
                    viewModel.slide()
                }
                ControlButton(
                    icon = Icons.Default.ArrowUpward,
                    description = "Jump",
                    tag = "control_jump",
                    accentColor = Color(0xFF16A34A)
                ) {
                    viewModel.jump()
                }
            }
        }

        // Pause Overlay Dialog
        if (state.isPaused && !state.isGameOver) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.65f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF1E293B),
                    modifier = Modifier
                        .widthIn(max = 400.dp)
                        .padding(24.dp)
                        .border(2.dp, Color(0xFF38BDF8), RoundedCornerShape(24.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = AppStrings.pause.get(currentLanguage),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Button(
                            onClick = { viewModel.togglePause() },
                            modifier = Modifier.fillMaxWidth().testTag("resume_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                        ) {
                            Text(AppStrings.resume.get(currentLanguage), fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.startGame() },
                            modifier = Modifier.fillMaxWidth().testTag("restart_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                        ) {
                            Text(AppStrings.restart.get(currentLanguage), fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onExitToHome,
                            modifier = Modifier.fillMaxWidth().testTag("home_button"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Text(AppStrings.home.get(currentLanguage))
                        }
                    }
                }
            }
        }

        // Game Over Dialog (Encouraging and Rewarding!)
        if (state.isGameOver) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.75f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = Color(0xFF0F172A),
                    modifier = Modifier
                        .widthIn(max = 460.dp)
                        .padding(20.dp)
                        .border(2.5.dp, Color(0xFFFBBF24), RoundedCornerShape(28.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "🎉 " + AppStrings.greatJob.get(currentLanguage),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFBBF24)
                        )

                        // Stats Summary Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ResultCard(label = AppStrings.score.get(currentLanguage), value = "${state.score}", icon = "🏆")
                            ResultCard(label = AppStrings.distance.get(currentLanguage), value = "${state.distanceMeters.toInt()}m", icon = "🏃")
                            ResultCard(label = AppStrings.coins.get(currentLanguage), value = "+${state.coinsCollected}", icon = "🪙")
                            ResultCard(label = AppStrings.correctAnswers.get(currentLanguage), value = "${state.questionsCorrect}/${state.questionsAnswered}", icon = "🧠")
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { viewModel.startGame() },
                                modifier = Modifier.weight(1f).testTag("play_again_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(Modifier.width(6.dp))
                                Text(AppStrings.restart.get(currentLanguage), fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = onExitToHome,
                                modifier = Modifier.weight(1f).testTag("exit_home_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                            ) {
                                Text(AppStrings.home.get(currentLanguage), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HudBadge(icon: String, value: String) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.Black.copy(alpha = 0.5f),
        modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(icon, fontSize = 14.sp)
            Text(
                text = value,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun ControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    tag: String,
    accentColor: Color = Color(0xFF2563EB),
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = accentColor.copy(alpha = 0.75f),
        modifier = Modifier
            .size(54.dp)
            .border(2.dp, Color.White.copy(alpha = 0.6f), CircleShape)
            .testTag(tag)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = description,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun ResultCard(label: String, value: String, icon: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1E293B),
        modifier = Modifier.padding(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 20.sp)
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color.White)
            Text(label, fontSize = 10.sp, color = Color.LightGray)
        }
    }
}
