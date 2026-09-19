package com.example.features.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.data.education.EducationalRepository
import com.example.data.models.AgeGroup
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    currentLanguage: Language,
    onLanguageSelect: (Language) -> Unit,
    soundManager: SoundManager,
    repository: GameRepository,
    onCompleteOnboarding: () -> Unit
) {
    var step by remember { mutableStateOf(0) } // 0: Lang, 1: Age, 2: Hero, 3: Tutorial
    var selectedAge by remember { mutableStateOf(6) }
    var selectedHeroId by remember { mutableStateOf("zaki") }
    val scope = rememberCoroutineScope()

    val heroes = EducationalRepository.heroes.take(2) // Zaki & Sarah

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF0F172A))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF1E293B).copy(alpha = 0.95f),
            modifier = Modifier
                .widthIn(max = 520.dp)
                .fillMaxHeight(0.9f)
                .padding(16.dp)
                .border(2.dp, Color(0xFF38BDF8), RoundedCornerShape(28.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Progress indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (i in 0..3) {
                        Surface(
                            shape = CircleShape,
                            color = if (i <= step) Color(0xFF38BDF8) else Color.White.copy(alpha = 0.2f),
                            modifier = Modifier
                                .size(if (i == step) 14.dp else 10.dp)
                                .padding(2.dp)
                        ) {}
                        if (i < 3) Spacer(Modifier.width(10.dp))
                    }
                }

                // Step Contents
                when (step) {
                    0 -> {
                        // Step 0: Language
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("🌍", fontSize = 44.sp)
                            Text(
                                text = AppStrings.selectLanguage.get(currentLanguage),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Language.values().forEach { lang ->
                                    Button(
                                        onClick = {
                                            onLanguageSelect(lang)
                                            soundManager.playButton()
                                            scope.launch { repository.updateLanguage(lang.code) }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (currentLanguage == lang) Color(0xFF0284C7) else Color(0xFF334155)
                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier.testTag("onboarding_lang_${lang.code}")
                                    ) {
                                        Text("${lang.flag} ${lang.displayName}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        // Step 1: Age
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text("🎂", fontSize = 44.sp)
                            Text(
                                text = AppStrings.selectAge.get(currentLanguage),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                listOf(6 to "5-6", 8 to "7-8", 10 to "9-10").forEach { (age, label) ->
                                    Button(
                                        onClick = {
                                            selectedAge = age
                                            soundManager.playButton()
                                            scope.launch { repository.updateAge(age) }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (selectedAge == age) Color(0xFF10B981) else Color(0xFF334155)
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Text("$label سنوات", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                    2 -> {
                        // Step 2: Hero
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = AppStrings.selectCharacter.get(currentLanguage),
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                heroes.forEach { h ->
                                    Surface(
                                        onClick = {
                                            selectedHeroId = h.id
                                            soundManager.playButton()
                                            scope.launch { repository.selectHero(h.id) }
                                        },
                                        shape = RoundedCornerShape(20.dp),
                                        color = if (selectedHeroId == h.id) Color(0xFF4F46E5) else Color(0xFF334155),
                                        modifier = Modifier
                                            .size(130.dp, 120.dp)
                                            .border(
                                                2.dp,
                                                if (selectedHeroId == h.id) Color(0xFFFBBF24) else Color.Transparent,
                                                RoundedCornerShape(20.dp)
                                            )
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(h.avatarEmoji, fontSize = 44.sp)
                                            Text(h.name.get(currentLanguage), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    3 -> {
                        // Step 3: Tutorial
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "🎮 " + AppStrings.tutorialTitle.get(currentLanguage),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFDE047)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                TutorialTip("⬅️ ➡️", "تغيير المسار")
                                TutorialTip("⬆️", "قفز فوق الحواجز")
                                TutorialTip("⬇️", "انزلاق تحت العوارض")
                                TutorialTip("🧠", "اختر البوابة الصحيحة!")
                            }
                        }
                    }
                }

                // Next Button
                Button(
                    onClick = {
                        soundManager.playButton()
                        if (step < 3) {
                            step++
                        } else {
                            scope.launch {
                                repository.setTutorialCompleted()
                                onCompleteOnboarding()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.65f).testTag("onboarding_next_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = if (step < 3) AppStrings.next.get(currentLanguage) else AppStrings.start.get(currentLanguage),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(Modifier.width(6.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}

@Composable
private fun TutorialTip(icon: String, label: String) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF334155),
        modifier = Modifier.padding(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 20.sp)
            Text(label, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}
