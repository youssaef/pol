package com.example.features.worlds

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audio.SoundManager
import com.example.core.i18n.AppStrings
import com.example.core.i18n.Language
import com.example.data.education.EducationalRepository
import com.example.data.models.WorldDefinition

@Composable
fun WorldsScreen(
    currentLanguage: Language,
    playerLevel: Int,
    soundManager: SoundManager,
    onSelectWorldToPlay: (WorldDefinition) -> Unit,
    onBack: () -> Unit
) {
    val worlds = EducationalRepository.worlds

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF0F172A))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                        .testTag("worlds_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "🗺️ " + AppStrings.worlds.get(currentLanguage) + " (11)",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFBBF24)
                )

                Spacer(Modifier.size(44.dp))
            }

            Spacer(Modifier.height(12.dp))

            // Grid of 11 Worlds
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(worlds) { world ->
                    val isUnlocked = playerLevel >= world.requiredLevel
                    WorldCard(
                        world = world,
                        isUnlocked = isUnlocked,
                        currentLanguage = currentLanguage,
                        onClick = {
                            if (isUnlocked) {
                                soundManager.playButton()
                                onSelectWorldToPlay(world)
                            } else {
                                soundManager.playWrong()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun WorldCard(
    world: WorldDefinition,
    isUnlocked: Boolean,
    currentLanguage: Language,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = Color(world.primaryColorHex).copy(alpha = if (isUnlocked) 0.85f else 0.4f),
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .border(
                2.dp,
                if (isUnlocked) Color(world.secondaryColorHex) else Color.Gray.copy(alpha = 0.5f),
                RoundedCornerShape(20.dp)
            )
            .testTag("world_card_${world.id}")
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Theme icon
                Text(world.themeEmoji, fontSize = 34.sp)

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = world.name.get(currentLanguage),
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                    Text(
                        text = world.description.get(currentLanguage),
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 10.sp,
                        maxLines = 2
                    )
                }

                if (isUnlocked) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF10B981),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                } else {
                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.6f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = Color.LightGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            if (!isUnlocked) {
                Text(
                    text = "${AppStrings.unlockAtLevel.get(currentLanguage)} ${world.requiredLevel}",
                    color = Color(0xFFFDE047),
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
        }
    }
}
