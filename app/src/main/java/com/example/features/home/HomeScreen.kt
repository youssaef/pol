package com.example.features.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.audio.SoundManager
import com.example.core.i18n.AppStrings
import com.example.core.i18n.Language
import com.example.data.db.AchievementEntity
import com.example.data.db.DailyMissionEntity
import com.example.data.db.GameRepository
import com.example.data.db.UserProfileEntity
import com.example.data.education.EducationalRepository
import com.example.data.models.HeroDefinition
import com.example.data.models.WorldDefinition
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    currentLanguage: Language,
    selectedWorld: WorldDefinition,
    soundManager: SoundManager,
    repository: GameRepository,
    onPlay: () -> Unit,
    onNavigateToWorlds: () -> Unit,
    onNavigateToMinigames: () -> Unit,
    onNavigateToLocker: () -> Unit,
    onNavigateToParents: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val profile by repository.userProfile.collectAsStateWithLifecycle(initialValue = null)
    val missions by repository.dailyMissions.collectAsStateWithLifecycle(initialValue = emptyList())
    val achievements by repository.achievements.collectAsStateWithLifecycle(initialValue = emptyList())

    var showMissionsDialog by remember { mutableStateOf(false) }
    var showAchievementsDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val currentHero = remember(profile?.selectedHeroId) {
        EducationalRepository.heroes.find { it.id == profile?.selectedHeroId }
            ?: EducationalRepository.heroes.first()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        Color(0xFF0F172A)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top Bar: Profile, Level, Currencies, Parents, Settings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile & Level & XP
                profile?.let { p ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.1f),
                        modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(currentHero.avatarEmoji, fontSize = 26.sp)
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = p.playerName,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF10B981)
                                    ) {
                                        Text(
                                            text = "Lv.${p.currentLevel}",
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 9.sp,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                val maxLevelXp = p.currentLevel * 200
                                LinearProgressIndicator(
                                    progress = { (p.currentXp.toFloat() / maxLevelXp).coerceIn(0f, 1f) },
                                    modifier = Modifier.width(90.dp).height(5.dp),
                                    color = Color(0xFFFBBF24),
                                    trackColor = Color.Black.copy(alpha = 0.3f),
                                )
                            }
                        }
                    }
                }

                // Currencies: Coins & Stars
                profile?.let { p ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.Black.copy(alpha = 0.4f),
                            modifier = Modifier.border(1.dp, Color(0xFFFBBF24).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("🪙", fontSize = 16.sp)
                                Text("${p.coins}", color = Color(0xFFFDE047), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.Black.copy(alpha = 0.4f),
                            modifier = Modifier.border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("⭐", fontSize = 16.sp)
                                Text("${p.stars}", color = Color(0xFF38BDF8), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                            }
                        }
                    }
                }

                // Actions: Missions, Achievements, Parents, Settings
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TopIconButton(icon = "🎯", testTag = "missions_button") {
                        soundManager.playButton()
                        showMissionsDialog = true
                    }
                    TopIconButton(icon = "🏆", testTag = "achievements_button") {
                        soundManager.playButton()
                        showAchievementsDialog = true
                    }
                    TopIconButton(icon = "👨‍👩‍👧", testTag = "parents_button") {
                        soundManager.playButton()
                        onNavigateToParents()
                    }
                    TopIconButton(icon = "⚙️", testTag = "settings_button") {
                        soundManager.playButton()
                        onNavigateToSettings()
                    }
                }
            }

            // 2. Central Hero Stage & Big Play Action
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Main Play Call-To-Action Box
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = Color(0xFF1E293B).copy(alpha = 0.85f),
                    modifier = Modifier
                        .fillMaxWidth(0.68f)
                        .border(2.5.dp, Color(0xFF3B82F6), RoundedCornerShape(26.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Hero & Selected World Info
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(selectedWorld.themeEmoji, fontSize = 28.sp)
                                Column {
                                    Text(
                                        text = selectedWorld.name.get(currentLanguage),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 17.sp,
                                        color = Color(0xFFFBBF24)
                                    )
                                    Text(
                                        text = selectedWorld.description.get(currentLanguage),
                                        fontSize = 11.sp,
                                        color = Color.LightGray,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        // Big Play Button
                        Button(
                            onClick = {
                                soundManager.playButton()
                                onPlay()
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            contentPadding = PaddingValues(horizontal = 26.dp, vertical = 14.dp),
                            modifier = Modifier.testTag("main_play_button")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(28.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = AppStrings.play.get(currentLanguage),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }

            // 3. Bottom Navigation Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BottomNavCard(
                    title = AppStrings.worlds.get(currentLanguage),
                    subtitle = "11 عوالم",
                    icon = "🗺️",
                    tag = "nav_worlds",
                    color = Color(0xFF4F46E5),
                    modifier = Modifier.weight(1f)
                ) {
                    soundManager.playButton()
                    onNavigateToWorlds()
                }

                BottomNavCard(
                    title = AppStrings.learn.get(currentLanguage),
                    subtitle = "ألعاب تعليمية",
                    icon = "🌟",
                    tag = "nav_minigames",
                    color = Color(0xFF0284C7),
                    modifier = Modifier.weight(1f)
                ) {
                    soundManager.playButton()
                    onNavigateToMinigames()
                }

                BottomNavCard(
                    title = AppStrings.locker.get(currentLanguage),
                    subtitle = "أبطال ومركبات",
                    icon = "🎒",
                    tag = "nav_locker",
                    color = Color(0xFFD97706),
                    modifier = Modifier.weight(1f)
                ) {
                    soundManager.playButton()
                    onNavigateToLocker()
                }
            }
        }

        // Daily Missions Dialog
        if (showMissionsDialog) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF1E293B),
                    modifier = Modifier.widthIn(max = 440.dp).padding(16.dp).border(2.dp, Color(0xFFFBBF24), RoundedCornerShape(24.dp))
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("🎯 المهام اليومية", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFFFBBF24))
                            IconButton(onClick = { showMissionsDialog = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(missions) { mission ->
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(0xFF334155),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(mission.titleAr, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text("المكافأة: +${mission.rewardCoins} 🪙  +${mission.rewardXp} XP", color = Color(0xFFFDE047), fontSize = 10.sp)
                                            Text("${mission.currentCount} / ${mission.targetCount}", color = Color.LightGray, fontSize = 10.sp)
                                        }

                                        if (mission.isClaimed) {
                                            Text("مكتملة ✅", color = Color(0xFF34D399), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        } else if (mission.currentCount >= mission.targetCount) {
                                            Button(
                                                onClick = {
                                                    soundManager.playCorrect()
                                                    scope.launch { repository.claimMission(mission.missionId) }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Text("استلام", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        } else {
                                            Text("جارية...", color = Color.LightGray, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Achievements Dialog
        if (showAchievementsDialog) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF1E293B),
                    modifier = Modifier.widthIn(max = 440.dp).padding(16.dp).border(2.dp, Color(0xFF38BDF8), RoundedCornerShape(24.dp))
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("🏆 الإنجازات والأوسمة", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF38BDF8))
                            IconButton(onClick = { showAchievementsDialog = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(achievements) { ach ->
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(0xFF334155),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(ach.iconEmoji, fontSize = 28.sp)
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(ach.titleAr, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text("المكافأة: +${ach.rewardStars} ⭐", color = Color(0xFF38BDF8), fontSize = 10.sp)
                                            Text("${ach.currentCount} / ${ach.targetCount}", color = Color.LightGray, fontSize = 10.sp)
                                        }
                                        if (ach.isUnlocked) {
                                            Text("مفتوح 🏆", color = Color(0xFFFBBF24), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        } else {
                                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopIconButton(icon: String, testTag: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.12f),
        modifier = Modifier.size(38.dp).border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape).testTag(testTag)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(icon, fontSize = 18.sp)
        }
    }
}

@Composable
private fun BottomNavCard(
    title: String,
    subtitle: String,
    icon: String,
    tag: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = color.copy(alpha = 0.85f),
        modifier = modifier
            .height(76.dp)
            .border(1.5.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(icon, fontSize = 28.sp)
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
                Text(subtitle, color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
            }
        }
    }
}
