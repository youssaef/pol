package com.example.data.models

import com.example.core.i18n.LocalizedText

enum class AgeGroup(val minAge: Int, val maxAge: Int, val label: LocalizedText) {
    LEVEL_1(5, 6, LocalizedText("المستوى الأول (5-6 سنوات)", "Niveau 1 (5-6 ans)", "Level 1 (5-6 yrs)")),
    LEVEL_2(7, 8, LocalizedText("المستوى الثاني (7-8 سنوات)", "Niveau 2 (7-8 ans)", "Level 2 (7-8 yrs)")),
    LEVEL_3(9, 10, LocalizedText("المستوى الثالث (9-10 سنوات)", "Niveau 3 (9-10 ans)", "Level 3 (9-10 yrs)"))
}

enum class PowerUpType(val title: LocalizedText, val icon: String, val durationMs: Long) {
    MAGNET(LocalizedText("مغناطيس العملات", "Aimant à pièces", "Coin Magnet"), "🧲", 10000L),
    SPEED_BOOST(LocalizedText("دفعة السرعة الخارقة", "Super Vitesse", "Speed Boost"), "⚡", 7000L),
    SHIELD(LocalizedText("درع الحماية", "Bouclier protecteur", "Shield"), "🛡️", 12000L),
    XP_BOOST(LocalizedText("مضاعف الخبرة 2X", "Double XP", "Double XP"), "⭐", 10000L),
    COIN_BOOST(LocalizedText("مضاعف العملات 2X", "Double Pièces", "Double Coins"), "🪙", 10000L)
}

data class WorldDefinition(
    val id: String,
    val name: LocalizedText,
    val description: LocalizedText,
    val themeEmoji: String,
    val primaryColorHex: Long,
    val secondaryColorHex: Long,
    val skyColorHex: Long,
    val groundColorHex: Long,
    val requiredLevel: Int,
    val categoryTag: String
)

data class HeroDefinition(
    val id: String,
    val name: LocalizedText,
    val avatarEmoji: String,
    val description: LocalizedText,
    val requiredLevel: Int,
    val requiredCoins: Int,
    val bodyColorHex: Long,
    val shirtColorHex: Long
)

data class VehicleDefinition(
    val id: String,
    val name: LocalizedText,
    val vehicleEmoji: String,
    val speedMultiplier: Float,
    val requiredLevel: Int,
    val requiredCoins: Int
)

data class EducationalQuestion(
    val id: String,
    val worldId: String,
    val ageGroup: AgeGroup,
    val question: LocalizedText,
    val choices: List<LocalizedText>,
    val choiceIcons: List<String>,
    val correctIndex: Int,
    val hint: LocalizedText,
    val explanation: LocalizedText,
    val category: String
)

data class PrayerStep(
    val stepNumber: Int,
    val title: LocalizedText,
    val description: LocalizedText,
    val iconEmoji: String,
    val isWudhu: Boolean
)

data class WordMatchingItem(
    val id: String,
    val arabicWord: String,
    val englishWord: String,
    val frenchWord: String,
    val emojiIcon: String,
    val category: String
)

data class MemoryCard(
    val id: Int,
    val matchId: Int,
    val contentEmoji: String,
    val label: String,
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false
)
