package com.example.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.data.db.GameRepository
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    currentLanguage: Language,
    onLanguageChange: (Language) -> Unit,
    showOnScreenControls: Boolean,
    onToggleOnScreenControls: (Boolean) -> Unit,
    soundManager: SoundManager,
    repository: GameRepository,
    currentAge: Int,
    onAgeChange: (Int) -> Unit,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var isMuted by remember { mutableStateOf(soundManager.isMuted) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF0F172A))
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
                        .testTag("settings_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "⚙️ " + AppStrings.settings.get(currentLanguage),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFBBF24)
                )

                Spacer(Modifier.size(44.dp))
            }

            Spacer(Modifier.height(12.dp))

            // Settings Items Row / Grid
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Column: Language & Age
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF1E293B),
                    modifier = Modifier.weight(1f).fillMaxHeight()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "🌐 " + AppStrings.selectLanguage.get(currentLanguage),
                            color = Color(0xFF38BDF8),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Language.values().forEach { lang ->
                                FilterChip(
                                    selected = currentLanguage == lang,
                                    onClick = {
                                        onLanguageChange(lang)
                                        soundManager.playButton()
                                        scope.launch { repository.updateLanguage(lang.code) }
                                    },
                                    label = { Text("${lang.flag} ${lang.displayName}") },
                                    modifier = Modifier.testTag("lang_chip_${lang.code}")
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "🎂 " + AppStrings.selectAge.get(currentLanguage),
                            color = Color(0xFF38BDF8),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(6 to "5-6 سنوات", 8 to "7-8 سنوات", 10 to "9-10 سنوات").forEach { (age, label) ->
                                FilterChip(
                                    selected = currentAge == age,
                                    onClick = {
                                        onAgeChange(age)
                                        soundManager.playButton()
                                        scope.launch { repository.updateAge(age) }
                                    },
                                    label = { Text(label) }
                                )
                            }
                        }
                    }
                }

                // Right Column: Audio & Controls
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF1E293B),
                    modifier = Modifier.weight(1f).fillMaxHeight()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Sound toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("🔊 " + AppStrings.soundEffects.get(currentLanguage), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("تشغيل المؤثرات الصوتية والأصوات التفاعلية", color = Color.LightGray, fontSize = 10.sp)
                            }
                            Switch(
                                checked = !isMuted,
                                onCheckedChange = {
                                    isMuted = !it
                                    soundManager.isMuted = !it
                                    if (it) soundManager.playButton()
                                }
                            )
                        }

                        // On-Screen Controls toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("🎮 " + AppStrings.onScreenControls.get(currentLanguage), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("أزرار اللمس على الشاشة لتسهيل اللعب للأطفال", color = Color.LightGray, fontSize = 10.sp)
                            }
                            Switch(
                                checked = showOnScreenControls,
                                onCheckedChange = {
                                    onToggleOnScreenControls(it)
                                    soundManager.playButton()
                                }
                            )
                        }

                        // About info
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.25f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = AppStrings.appTitle.get(currentLanguage) + " v1.0",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFFFBBF24)
                                )
                                Text(
                                    text = AppStrings.appSlogan.get(currentLanguage),
                                    fontSize = 10.sp,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
