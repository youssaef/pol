package com.example.features.minigames

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
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
import com.example.core.audio.SoundManager
import com.example.core.i18n.AppStrings
import com.example.core.i18n.Language
import com.example.data.db.GameRepository
import com.example.data.education.EducationalRepository
import com.example.data.models.EducationalQuestion
import com.example.data.models.MemoryCard
import com.example.data.models.PrayerStep
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class MiniGameType {
    LETTER_MATCH,
    WORD_BUILDER,
    MEMORY_CARDS,
    PRAYER_STEPS,
    TRIVIA_QUIZ
}

@Composable
fun MiniGamesScreen(
    currentLanguage: Language,
    soundManager: SoundManager,
    repository: GameRepository,
    onBack: () -> Unit
) {
    var selectedGame by remember { mutableStateOf(MiniGameType.LETTER_MATCH) }

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
                        .testTag("minigames_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "🌟 " + AppStrings.learn.get(currentLanguage) + " 🌟",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFBBF24)
                )

                Spacer(Modifier.size(44.dp))
            }

            Spacer(Modifier.height(8.dp))

            // Game Tabs Selector
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    GameTab(
                        title = AppStrings.letterMatch.get(currentLanguage),
                        icon = "🔤",
                        isSelected = selectedGame == MiniGameType.LETTER_MATCH,
                        onClick = { selectedGame = MiniGameType.LETTER_MATCH; soundManager.playButton() }
                    )
                }
                item {
                    GameTab(
                        title = AppStrings.wordBuilder.get(currentLanguage),
                        icon = "🧩",
                        isSelected = selectedGame == MiniGameType.WORD_BUILDER,
                        onClick = { selectedGame = MiniGameType.WORD_BUILDER; soundManager.playButton() }
                    )
                }
                item {
                    GameTab(
                        title = AppStrings.memoryCards.get(currentLanguage),
                        icon = "🃏",
                        isSelected = selectedGame == MiniGameType.MEMORY_CARDS,
                        onClick = { selectedGame = MiniGameType.MEMORY_CARDS; soundManager.playButton() }
                    )
                }
                item {
                    GameTab(
                        title = AppStrings.prayerSteps.get(currentLanguage),
                        icon = "🕌",
                        isSelected = selectedGame == MiniGameType.PRAYER_STEPS,
                        onClick = { selectedGame = MiniGameType.PRAYER_STEPS; soundManager.playButton() }
                    )
                }
                item {
                    GameTab(
                        title = AppStrings.triviaChallenge.get(currentLanguage),
                        icon = "💡",
                        isSelected = selectedGame == MiniGameType.TRIVIA_QUIZ,
                        onClick = { selectedGame = MiniGameType.TRIVIA_QUIZ; soundManager.playButton() }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Active Game Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (selectedGame) {
                    MiniGameType.LETTER_MATCH -> LetterMatchGame(currentLanguage, soundManager, repository)
                    MiniGameType.WORD_BUILDER -> WordBuilderGame(currentLanguage, soundManager, repository)
                    MiniGameType.MEMORY_CARDS -> MemoryGame(currentLanguage, soundManager, repository)
                    MiniGameType.PRAYER_STEPS -> PrayerStepsGame(currentLanguage, soundManager, repository)
                    MiniGameType.TRIVIA_QUIZ -> TriviaGame(currentLanguage, soundManager, repository)
                }
            }
        }
    }
}

@Composable
private fun GameTab(
    title: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) Color(0xFF3B82F6) else Color.White.copy(alpha = 0.1f),
        modifier = Modifier.border(
            1.5.dp,
            if (isSelected) Color(0xFF93C5FD) else Color.Transparent,
            RoundedCornerShape(16.dp)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(icon, fontSize = 16.sp)
            Text(
                text = title,
                color = Color.White,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 13.sp
            )
        }
    }
}

// 1. Letter Match Game
@Composable
private fun LetterMatchGame(lang: Language, sound: SoundManager, repo: GameRepository) {
    val items = remember { EducationalRepository.wordItems }
    var currentIndex by remember { mutableStateOf(0) }
    var feedback by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val currentItem = items[currentIndex % items.size]
    val options = remember(currentIndex) {
        items.shuffled().take(3).let { if (it.contains(currentItem)) it else (it.take(2) + currentItem).shuffled() }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("طابق الحرف مع الصورة المناسبة", color = Color(0xFFFDE047), fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        // Big Letter Display
        Surface(
            shape = CircleShape,
            color = Color(0xFFF97316),
            modifier = Modifier.size(90.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = currentItem.arabicWord.first().toString(),
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Choices
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            options.forEach { opt ->
                Surface(
                    onClick = {
                        if (opt == currentItem) {
                            sound.playCorrect()
                            feedback = "أحسنت! ${opt.arabicWord} (${opt.emojiIcon})"
                            scope.launch {
                                repo.recordRunResults(10, 1, 50, 5, 5, 1, 1, 100, "alphabet")
                                delay(1200)
                                feedback = null
                                currentIndex++
                            }
                        } else {
                            sound.playWrong()
                            feedback = "حاول مرة أخرى! 💪"
                        }
                    },
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF334155),
                    modifier = Modifier
                        .size(100.dp, 80.dp)
                        .border(2.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(opt.emojiIcon, fontSize = 28.sp)
                        Text(opt.arabicWord, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        feedback?.let {
            Spacer(Modifier.height(10.dp))
            Text(it, color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

// 2. Word Builder Game
@Composable
private fun WordBuilderGame(lang: Language, sound: SoundManager, repo: GameRepository) {
    val words = remember { listOf("أسد", "فيل", "شمس", "قمر", "ورد") }
    var wordIndex by remember { mutableStateOf(0) }
    val currentWord = words[wordIndex % words.size]

    val letters = remember(currentWord) { currentWord.map { it.toString() }.shuffled() }
    val builtLetters = remember { mutableStateListOf<String>() }
    val scope = rememberCoroutineScope()
    var isCorrect by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("ركّب حروف الكلمة بالترتيب الصحيح 🧩", color = Color(0xFFFDE047), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        // Assembled slots
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (i in currentWord.indices) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (i < builtLetters.size) Color(0xFF10B981) else Color(0xFF334155),
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = builtLetters.getOrNull(i) ?: "",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Letter Choices
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            letters.forEach { charStr ->
                Surface(
                    onClick = {
                        if (builtLetters.size < currentWord.length) {
                            builtLetters.add(charStr)
                            sound.playButton()
                            if (builtLetters.size == currentWord.length) {
                                if (builtLetters.joinToString("") == currentWord) {
                                    sound.playCorrect()
                                    isCorrect = true
                                    scope.launch {
                                        repo.recordRunResults(15, 1, 60, 6, 6, 1, 1, 120, "words")
                                        delay(1300)
                                        builtLetters.clear()
                                        isCorrect = false
                                        wordIndex++
                                    }
                                } else {
                                    sound.playWrong()
                                    scope.launch {
                                        delay(600)
                                        builtLetters.clear()
                                    }
                                }
                            }
                        }
                    },
                    shape = CircleShape,
                    color = Color(0xFF2563EB),
                    modifier = Modifier.size(54.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(charStr, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        Button(
            onClick = { builtLetters.clear() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64748B))
        ) {
            Text("إعادة الترتيب", fontSize = 12.sp)
        }
    }
}

// 3. Memory Match Cards Game
@Composable
private fun MemoryGame(lang: Language, sound: SoundManager, repo: GameRepository) {
    val emojis = listOf("🦁", "🚗", "🚀", "🐝")
    var cards by remember {
        mutableStateOf(
            (emojis + emojis).shuffled().mapIndexed { index, emoji ->
                MemoryCard(id = index, matchId = emojis.indexOf(emoji), contentEmoji = emoji, label = "")
            }
        )
    }
    var flippedCards by remember { mutableStateOf<List<Int>>(emptyList()) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("اقلب البطاقات وابحث عن الأزواج المتطابقة 🃏", color = Color(0xFFFDE047), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        // Grid 4x2
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            for (col in 0..3) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    for (row in 0..1) {
                        val cardIndex = col * 2 + row
                        val card = cards[cardIndex]
                        val isRevealed = card.isFlipped || card.isMatched || flippedCards.contains(card.id)

                        Surface(
                            onClick = {
                                if (flippedCards.size < 2 && !isRevealed) {
                                    sound.playButton()
                                    val newFlipped = flippedCards + card.id
                                    flippedCards = newFlipped
                                    if (newFlipped.size == 2) {
                                        val firstCard = cards.first { it.id == newFlipped[0] }
                                        val secondCard = cards.first { it.id == newFlipped[1] }
                                        if (firstCard.matchId == secondCard.matchId) {
                                            sound.playCorrect()
                                            cards = cards.map {
                                                if (it.id == firstCard.id || it.id == secondCard.id) it.copy(isMatched = true) else it
                                            }
                                            flippedCards = emptyList()
                                            scope.launch {
                                                repo.recordRunResults(20, 2, 80, 8, 8, 1, 1, 150, "trivia")
                                            }
                                        } else {
                                            sound.playWrong()
                                            scope.launch {
                                                delay(900)
                                                flippedCards = emptyList()
                                            }
                                        }
                                    }
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isRevealed) Color(0xFF0284C7) else Color(0xFF334155),
                            modifier = Modifier
                                .size(64.dp, 64.dp)
                                .border(1.5.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (isRevealed) {
                                    Text(card.contentEmoji, fontSize = 28.sp)
                                } else {
                                    Text("⭐", fontSize = 20.sp, color = Color.White.copy(alpha = 0.5f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 4. Prayer & Wudhu Steps
@Composable
private fun PrayerStepsGame(lang: Language, sound: SoundManager, repo: GameRepository) {
    val steps = remember { EducationalRepository.prayerSteps }
    var currentStepIndex by remember { mutableStateOf(0) }
    val step = steps[currentStepIndex]
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("خطوات الوضوء الطاهر بالترتيب 🕌", color = Color(0xFF34D399), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF065F46),
            modifier = Modifier
                .widthIn(max = 420.dp)
                .padding(horizontal = 16.dp)
                .border(2.dp, Color(0xFF34D399), RoundedCornerShape(20.dp))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(step.iconEmoji, fontSize = 42.sp)
                Column(modifier = Modifier.weight(1f)) {
                    Text("الخطوة ${step.stepNumber}: ${step.title.get(lang)}", color = Color(0xFFFDE047), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(step.description.get(lang), color = Color.White, fontSize = 12.sp)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = {
                    sound.playCorrect()
                    if (currentStepIndex < steps.size - 1) {
                        currentStepIndex++
                    } else {
                        currentStepIndex = 0
                        scope.launch {
                            repo.recordRunResults(25, 2, 100, 10, 10, 1, 1, 200, "prayer")
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
            ) {
                Text(if (currentStepIndex < steps.size - 1) "الخطوة التالية ➡️" else "أحسنت! أتممت الوضوء 🎉", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// 5. Trivia Quiz Game
@Composable
private fun TriviaGame(lang: Language, sound: SoundManager, repo: GameRepository) {
    val questions = remember { EducationalRepository.questions }
    var qIndex by remember { mutableStateOf(0) }
    val q = questions[qIndex % questions.size]
    var feedback by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1E293B),
            modifier = Modifier
                .widthIn(max = 440.dp)
                .padding(horizontal = 12.dp)
                .border(1.5.dp, Color(0xFFFBBF24), RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(q.question.get(lang), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            q.choices.forEachIndexed { idx, choice ->
                val icon = q.choiceIcons.getOrNull(idx) ?: "⭐"
                Button(
                    onClick = {
                        if (idx == q.correctIndex) {
                            sound.playCorrect()
                            feedback = "🎉 ${q.explanation.get(lang)}"
                            scope.launch {
                                repo.recordRunResults(20, 1, 80, 8, 8, 1, 1, 150, q.category)
                                delay(2000)
                                feedback = null
                                qIndex++
                            }
                        } else {
                            sound.playWrong()
                            feedback = "💡 ${q.hint.get(lang)}"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("$icon ${choice.get(lang)}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        feedback?.let {
            Spacer(Modifier.height(10.dp))
            Text(
                text = it,
                color = Color(0xFFFBBF24),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.widthIn(max = 440.dp)
            )
        }
    }
}
