package com.example.features.locker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
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
import com.example.data.models.HeroDefinition
import com.example.data.models.VehicleDefinition
import kotlinx.coroutines.launch

@Composable
fun LockerScreen(
    currentLanguage: Language,
    selectedHeroId: String,
    selectedVehicleId: String,
    playerLevel: Int,
    coins: Int,
    soundManager: SoundManager,
    repository: GameRepository,
    onBack: () -> Unit
) {
    val heroes = EducationalRepository.heroes
    val vehicles = EducationalRepository.vehicles
    val scope = rememberCoroutineScope()

    var activeTab by remember { mutableStateOf(0) } // 0: Heroes, 1: Vehicles

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
                        .testTag("locker_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0; soundManager.playButton() },
                        label = { Text("🏃 " + AppStrings.locker.get(currentLanguage)) }
                    )
                    FilterChip(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1; soundManager.playButton() },
                        label = { Text("🛴 المركبات والسرعة") }
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("🪙", fontSize = 16.sp)
                    Text("$coins", color = Color(0xFFFDE047), fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            if (activeTab == 0) {
                // Heroes Row
                LazyRow(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(heroes) { hero ->
                        val isEquipped = hero.id == selectedHeroId
                        val isUnlocked = (playerLevel >= hero.requiredLevel) && (coins >= hero.requiredCoins)
                        HeroCard(
                            hero = hero,
                            isEquipped = isEquipped,
                            isUnlocked = isUnlocked,
                            currentLanguage = currentLanguage,
                            onEquip = {
                                if (isUnlocked) {
                                    soundManager.playCorrect()
                                    scope.launch { repository.selectHero(hero.id) }
                                } else {
                                    soundManager.playWrong()
                                }
                            }
                        )
                    }
                }
            } else {
                // Vehicles Row
                LazyRow(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(vehicles) { vehicle ->
                        val isEquipped = vehicle.id == selectedVehicleId
                        val isUnlocked = (playerLevel >= vehicle.requiredLevel) && (coins >= vehicle.requiredCoins)
                        VehicleCard(
                            vehicle = vehicle,
                            isEquipped = isEquipped,
                            isUnlocked = isUnlocked,
                            currentLanguage = currentLanguage,
                            onEquip = {
                                if (isUnlocked) {
                                    soundManager.playCorrect()
                                    scope.launch { repository.selectVehicle(vehicle.id) }
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
}

@Composable
private fun HeroCard(
    hero: HeroDefinition,
    isEquipped: Boolean,
    isUnlocked: Boolean,
    currentLanguage: Language,
    onEquip: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF1E293B),
        modifier = Modifier
            .width(220.dp)
            .fillMaxHeight(0.92f)
            .border(
                2.5.dp,
                if (isEquipped) Color(0xFF10B981) else Color.White.copy(alpha = 0.2f),
                RoundedCornerShape(24.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = CircleShape,
                color = Color(hero.bodyColorHex),
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(hero.avatarEmoji, fontSize = 42.sp)
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = hero.name.get(currentLanguage),
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = Color.White
                )
                Text(
                    text = hero.description.get(currentLanguage),
                    fontSize = 11.sp,
                    color = Color.LightGray,
                    maxLines = 3
                )
            }

            if (isEquipped) {
                Button(
                    onClick = {},
                    enabled = false,
                    colors = ButtonDefaults.buttonColors(disabledContainerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(4.dp))
                    Text(AppStrings.equipped.get(currentLanguage), color = Color.White, fontWeight = FontWeight.Bold)
                }
            } else if (isUnlocked) {
                Button(
                    onClick = onEquip,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("equip_hero_${hero.id}")
                ) {
                    Text(AppStrings.equip.get(currentLanguage), fontWeight = FontWeight.Bold)
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.Black.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("مستوى ${hero.requiredLevel} • ${hero.requiredCoins} 🪙", color = Color.LightGray, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun VehicleCard(
    vehicle: VehicleDefinition,
    isEquipped: Boolean,
    isUnlocked: Boolean,
    currentLanguage: Language,
    onEquip: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF1E293B),
        modifier = Modifier
            .width(220.dp)
            .fillMaxHeight(0.92f)
            .border(
                2.5.dp,
                if (isEquipped) Color(0xFF10B981) else Color.White.copy(alpha = 0.2f),
                RoundedCornerShape(24.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF3B82F6),
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(vehicle.vehicleEmoji, fontSize = 42.sp)
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = vehicle.name.get(currentLanguage),
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = Color.White
                )
                Text(
                    text = "مكافأة السرعة: +${((vehicle.speedMultiplier - 1f) * 100).toInt()}%",
                    fontSize = 12.sp,
                    color = Color(0xFF38BDF8),
                    fontWeight = FontWeight.Bold
                )
            }

            if (isEquipped) {
                Button(
                    onClick = {},
                    enabled = false,
                    colors = ButtonDefaults.buttonColors(disabledContainerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(4.dp))
                    Text(AppStrings.equipped.get(currentLanguage), color = Color.White, fontWeight = FontWeight.Bold)
                }
            } else if (isUnlocked) {
                Button(
                    onClick = onEquip,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("equip_vehicle_${vehicle.id}")
                ) {
                    Text(AppStrings.equip.get(currentLanguage), fontWeight = FontWeight.Bold)
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.Black.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("مستوى ${vehicle.requiredLevel} • ${vehicle.requiredCoins} 🪙", color = Color.LightGray, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
