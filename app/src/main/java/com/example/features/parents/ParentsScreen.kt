package com.example.features.parents

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.audio.SoundManager
import com.example.core.i18n.AppStrings
import com.example.core.i18n.Language
import com.example.data.db.CategoryStatEntity
import com.example.data.db.GameRepository
import com.example.data.db.UserProfileEntity
import kotlin.random.Random

@Composable
fun ParentsScreen(
    currentLanguage: Language,
    repository: GameRepository,
    soundManager: SoundManager,
    onBack: () -> Unit
) {
    val profile by repository.userProfile.collectAsStateWithLifecycle(initialValue = null)
    val categoryStats by repository.categoryStats.collectAsStateWithLifecycle(initialValue = emptyList())

    // Math Gate State
    var isUnlocked by remember { mutableStateOf(false) }
    val num1 = remember { Random.nextInt(4, 9) }
    val num2 = remember { Random.nextInt(4, 9) }
    val expectedAnswer = num1 * num2
    var parentAnswerInput by remember { mutableStateOf("") }
    var gateError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF022C22), Color(0xFF064E3B), Color(0xFF0F172A))
                )
            )
    ) {
        if (!isUnlocked) {
            // Security Math Gate Dialog
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF065F46),
                    modifier = Modifier
                        .widthIn(max = 420.dp)
                        .padding(20.dp)
                        .border(2.dp, Color(0xFF34D399), RoundedCornerShape(24.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(shape = CircleShape, color = Color(0xFF047857), modifier = Modifier.size(56.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF6EE7B7), modifier = Modifier.size(32.dp))
                            }
                        }

                        Text(
                            text = AppStrings.parentGateTitle.get(currentLanguage),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "${AppStrings.parentGatePrompt.get(currentLanguage)}\n$num1 × $num2 = ؟",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFDE047)
                        )

                        OutlinedTextField(
                            value = parentAnswerInput,
                            onValueChange = { parentAnswerInput = it; gateError = false },
                            label = { Text("أدخل الناتج بالأرقام") },
                            isError = gateError,
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF34D399)
                            ),
                            modifier = Modifier.testTag("parent_gate_input")
                        )

                        if (gateError) {
                            Text("إجابة غير صحيحة، حاول مجددًا", color = Color(0xFFF87171), fontSize = 12.sp)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onBack,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(AppStrings.back.get(currentLanguage), color = Color.White)
                            }
                            Button(
                                onClick = {
                                    if (parentAnswerInput.trim() == expectedAnswer.toString()) {
                                        soundManager.playCorrect()
                                        isUnlocked = true
                                    } else {
                                        soundManager.playWrong()
                                        gateError = true
                                    }
                                },
                                modifier = Modifier.weight(1f).testTag("parent_gate_submit"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                            ) {
                                Text("تأكيد الدخول", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            // Unlocked Parent Dashboard Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Top Bar
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
                            .testTag("parents_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "👨‍👩‍👧 " + AppStrings.parents.get(currentLanguage),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF6EE7B7)
                    )

                    Spacer(Modifier.size(44.dp))
                }

                Spacer(Modifier.height(12.dp))

                // Stats Dashboard Grid
                profile?.let { p ->
                    val accuracyPercent = if (p.totalQuestionsAnswered > 0) {
                        ((p.totalCorrectAnswers.toFloat() / p.totalQuestionsAnswered) * 100).toInt()
                    } else 0

                    val playtimeMins = p.totalPlaytimeSeconds / 60
                    val learningMins = p.totalLearningTimeSeconds / 60

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ParentStatCard(
                            label = AppStrings.playtime.get(currentLanguage),
                            value = "$playtimeMins ${AppStrings.minutes.get(currentLanguage)}",
                            icon = "⏱️",
                            modifier = Modifier.weight(1f)
                        )
                        ParentStatCard(
                            label = AppStrings.learningTime.get(currentLanguage),
                            value = "$learningMins ${AppStrings.minutes.get(currentLanguage)}",
                            icon = "📖",
                            modifier = Modifier.weight(1f)
                        )
                        ParentStatCard(
                            label = AppStrings.questionsAnswered.get(currentLanguage),
                            value = "${p.totalQuestionsAnswered}",
                            icon = "🧠",
                            modifier = Modifier.weight(1f)
                        )
                        ParentStatCard(
                            label = AppStrings.accuracy.get(currentLanguage),
                            value = "$accuracyPercent%",
                            icon = "🎯",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // Adaptive Difficulty & Category Breakdown
                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Category Breakdown
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = Color(0xFF064E3B),
                            modifier = Modifier.weight(1.2f).fillMaxHeight()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "📊 " + AppStrings.categoriesProgress.get(currentLanguage),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFFFDE047)
                                )
                                Spacer(Modifier.height(8.dp))
                                if (categoryStats.isEmpty()) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "سيظهر تقدم المواد فور إجابة الطفل على الأسئلة في العوالم!",
                                            color = Color.LightGray,
                                            fontSize = 12.sp
                                        )
                                    }
                                } else {
                                    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        items(categoryStats) { stat ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(stat.categoryKey, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                Text(
                                                    "${stat.correctAnswers}/${stat.totalQuestions} صحيحة",
                                                    color = Color(0xFF6EE7B7),
                                                    fontSize = 11.sp
                                                )
                                            }
                                            LinearProgressIndicator(
                                                progress = { if (stat.totalQuestions > 0) stat.correctAnswers.toFloat() / stat.totalQuestions else 0f },
                                                modifier = Modifier.fillMaxWidth().height(6.dp),
                                                color = Color(0xFF34D399),
                                                trackColor = Color.Black.copy(alpha = 0.3f),
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Child Safety & Privacy Guarantees
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = Color(0xFF0F172A),
                            modifier = Modifier.weight(1f).fillMaxHeight().border(1.dp, Color(0xFF10B981), RoundedCornerShape(18.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("🛡️ بيئة آمنة 100% للأطفال", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF34D399))
                                Text("• لا توجد إعلانات خارجية متطفلة", color = Color.White, fontSize = 11.sp)
                                Text("• لا توجد غرف محادثة أو تواصل مع غرباء", color = Color.White, fontSize = 11.sp)
                                Text("• جميع البيانات والتقدم تُحفظ محليًا على الجهاز", color = Color.White, fontSize = 11.sp)
                                Text("• صعوبة تعليمية تتكيف مع أداء الطفل لتعزيز الثقة", color = Color(0xFFFDE047), fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ParentStatCard(
    label: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF065F46),
        modifier = modifier.border(1.dp, Color(0xFF34D399).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 18.sp)
            Text(value, fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color.White)
            Text(label, fontSize = 10.sp, color = Color(0xFF6EE7B7), maxLines = 1)
        }
    }
}
