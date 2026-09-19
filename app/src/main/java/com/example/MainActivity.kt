package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.audio.SoundManager
import com.example.core.i18n.Language
import com.example.data.db.GameDatabase
import com.example.data.db.GameRepository
import com.example.data.education.EducationalRepository
import com.example.data.models.AgeGroup
import com.example.data.models.WorldDefinition
import com.example.features.gameplay.RunnerScreen
import com.example.features.home.HomeScreen
import com.example.features.locker.LockerScreen
import com.example.features.minigames.MiniGamesScreen
import com.example.features.onboarding.OnboardingScreen
import com.example.features.parents.ParentsScreen
import com.example.features.settings.SettingsScreen
import com.example.features.worlds.WorldsScreen
import com.example.gameplay.runner.RunnerViewModel
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

enum class ScreenRoute {
    ONBOARDING,
    HOME,
    RUNNER,
    WORLDS,
    MINIGAMES,
    LOCKER,
    PARENTS,
    SETTINGS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = GameDatabase.getInstance(applicationContext)
        val repository = GameRepository(db.gameDao())
        val soundManager = SoundManager()

        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val scope = rememberCoroutineScope()

                val profile by repository.userProfile.collectAsStateWithLifecycle(initialValue = null)

                var currentRoute by remember { mutableStateOf(ScreenRoute.HOME) }
                var selectedWorld by remember { mutableStateOf(EducationalRepository.worlds.first()) }
                var showOnScreenControls by remember { mutableStateOf(true) }

                var currentLanguage by remember { mutableStateOf(Language.ARABIC) }

                LaunchedEffect(Unit) {
                    repository.initializeDefaultsIfNeeded()
                }

                LaunchedEffect(profile) {
                    profile?.let { p ->
                        val lang = Language.values().find { it.code == p.languageCode } ?: Language.ARABIC
                        currentLanguage = lang
                        if (!p.isTutorialCompleted && currentRoute != ScreenRoute.ONBOARDING) {
                            currentRoute = ScreenRoute.ONBOARDING
                        }
                    }
                }

                val currentHero = remember(profile?.selectedHeroId) {
                    EducationalRepository.heroes.find { it.id == profile?.selectedHeroId }
                        ?: EducationalRepository.heroes.first()
                }

                val ageGroup = remember(profile?.age) {
                    val a = profile?.age ?: 6
                    when {
                        a <= 6 -> AgeGroup.LEVEL_1
                        a <= 8 -> AgeGroup.LEVEL_2
                        else -> AgeGroup.LEVEL_3
                    }
                }

                val layoutDirection = if (currentLanguage.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        when (currentRoute) {
                            ScreenRoute.ONBOARDING -> {
                                OnboardingScreen(
                                    currentLanguage = currentLanguage,
                                    onLanguageSelect = { currentLanguage = it },
                                    soundManager = soundManager,
                                    repository = repository,
                                    onCompleteOnboarding = { currentRoute = ScreenRoute.HOME }
                                )
                            }
                            ScreenRoute.HOME -> {
                                HomeScreen(
                                    currentLanguage = currentLanguage,
                                    selectedWorld = selectedWorld,
                                    soundManager = soundManager,
                                    repository = repository,
                                    onPlay = { currentRoute = ScreenRoute.RUNNER },
                                    onNavigateToWorlds = { currentRoute = ScreenRoute.WORLDS },
                                    onNavigateToMinigames = { currentRoute = ScreenRoute.MINIGAMES },
                                    onNavigateToLocker = { currentRoute = ScreenRoute.LOCKER },
                                    onNavigateToParents = { currentRoute = ScreenRoute.PARENTS },
                                    onNavigateToSettings = { currentRoute = ScreenRoute.SETTINGS }
                                )
                            }
                            ScreenRoute.RUNNER -> {
                                val runnerViewModel = remember(selectedWorld, currentHero) {
                                    RunnerViewModel(
                                        world = selectedWorld,
                                        hero = currentHero,
                                        ageGroup = ageGroup,
                                        repository = repository,
                                        soundManager = soundManager
                                    )
                                }
                                RunnerScreen(
                                    viewModel = runnerViewModel,
                                    world = selectedWorld,
                                    hero = currentHero,
                                    currentLanguage = currentLanguage,
                                    showOnScreenControls = showOnScreenControls,
                                    onExitToHome = { currentRoute = ScreenRoute.HOME }
                                )
                            }
                            ScreenRoute.WORLDS -> {
                                WorldsScreen(
                                    currentLanguage = currentLanguage,
                                    playerLevel = profile?.currentLevel ?: 1,
                                    soundManager = soundManager,
                                    onSelectWorldToPlay = { world ->
                                        selectedWorld = world
                                        currentRoute = ScreenRoute.RUNNER
                                    },
                                    onBack = { currentRoute = ScreenRoute.HOME }
                                )
                            }
                            ScreenRoute.MINIGAMES -> {
                                MiniGamesScreen(
                                    currentLanguage = currentLanguage,
                                    soundManager = soundManager,
                                    repository = repository,
                                    onBack = { currentRoute = ScreenRoute.HOME }
                                )
                            }
                            ScreenRoute.LOCKER -> {
                                LockerScreen(
                                    currentLanguage = currentLanguage,
                                    selectedHeroId = profile?.selectedHeroId ?: "zaki",
                                    selectedVehicleId = profile?.selectedVehicleId ?: "scooter",
                                    playerLevel = profile?.currentLevel ?: 1,
                                    coins = profile?.coins ?: 100,
                                    soundManager = soundManager,
                                    repository = repository,
                                    onBack = { currentRoute = ScreenRoute.HOME }
                                )
                            }
                            ScreenRoute.PARENTS -> {
                                ParentsScreen(
                                    currentLanguage = currentLanguage,
                                    repository = repository,
                                    soundManager = soundManager,
                                    onBack = { currentRoute = ScreenRoute.HOME }
                                )
                            }
                            ScreenRoute.SETTINGS -> {
                                SettingsScreen(
                                    currentLanguage = currentLanguage,
                                    onLanguageChange = { currentLanguage = it },
                                    showOnScreenControls = showOnScreenControls,
                                    onToggleOnScreenControls = { showOnScreenControls = it },
                                    soundManager = soundManager,
                                    repository = repository,
                                    currentAge = profile?.age ?: 6,
                                    onAgeChange = { age ->
                                        scope.launch { repository.updateAge(age) }
                                    },
                                    onBack = { currentRoute = ScreenRoute.HOME }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
