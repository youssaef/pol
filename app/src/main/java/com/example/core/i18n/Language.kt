package com.example.core.i18n

enum class Language(val code: String, val displayName: String, val isRtl: Boolean, val flag: String) {
    ARABIC("ar", "العربية", true, "🇩🇿"),
    FRENCH("fr", "Français", false, "🇫🇷"),
    ENGLISH("en", "English", false, "🇬🇧")
}

data class LocalizedText(
    val ar: String,
    val fr: String,
    val en: String
) {
    fun get(lang: Language): String = when (lang) {
        Language.ARABIC -> ar
        Language.FRENCH -> fr
        Language.ENGLISH -> en
    }
}
