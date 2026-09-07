package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AccountingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: AccountingViewModel,
    onBackClick: () -> Unit,
    onLogout: () -> Unit
) {
    val activeCompany = viewModel.getActiveCompany()
    val userEmail by viewModel.userEmail.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()

    val greetingName = remember(userEmail, activeCompany) {
        val emailPrefix = userEmail?.substringBefore("@")?.split(".")?.firstOrNull()?.replaceFirstChar { it.uppercase() }
        if (!emailPrefix.isNullOrBlank() && emailPrefix.length in 3..15) {
            emailPrefix
        } else {
            "Jessie"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LavenderBackground)
    ) {
        TopAppBar(
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "easier",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = TextPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Getting easier in one comand.",
                        fontSize = 10.sp,
                        color = Purple600,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.2.sp
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Kembali",
                        tint = TextPrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceCard)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Profile",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
            }

            // User Profile Hero Card
            item {
                Surface(
                    color = SurfaceCard,
                    shape = RoundedCornerShape(22.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Purple600, Purple800)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = greetingName.take(1),
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = greetingName,
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = userEmail ?: "pteduworksasiaagroindustries@gmail.com",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            color = Purple100,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Peran: ${currentRole.title}",
                                color = Purple700,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Profile Actions
            item {
                SettingActionRow(
                    icon = Icons.Outlined.Person,
                    title = "Personal Information",
                    subtitle = "Name, Phone & ID",
                    onClick = {}
                )
            }

            item {
                SettingActionRow(
                    icon = Icons.Outlined.Key,
                    title = "Change Password",
                    subtitle = "Security & PIN Authentication",
                    onClick = {}
                )
            }

            item {
                SettingActionRow(
                    icon = Icons.Outlined.Language,
                    title = "Language",
                    subtitle = "English (US) / Bahasa Indonesia",
                    onClick = {}
                )
            }

            item {
                SettingActionRow(
                    icon = Icons.Outlined.HelpOutline,
                    title = "Help Center & FAQ",
                    subtitle = "Support, User Guide & Guidelines",
                    onClick = {}
                )
            }

            item {
                SettingActionRow(
                    icon = Icons.Outlined.Description,
                    title = "Terms & Privacy Policy",
                    subtitle = "Data handling & PSAK standard compliance",
                    onClick = {}
                )
            }

            // Logout Button
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = Crimson500),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Keluar / Ganti Akun",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
