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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AccountingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: AccountingViewModel,
    onBackClick: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val activeCompany = viewModel.getActiveCompany()
    val currentRole by viewModel.currentRole.collectAsState()

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
                    text = "Settings",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
            }

            // Company Card
            item {
                Surface(
                    color = SurfaceCard,
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEDE9FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Business,
                                contentDescription = null,
                                tint = Purple600,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = activeCompany.name,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${activeCompany.entityType.name} • ${if (activeCompany.isPkp) "PKP" else "Non-PKP"}",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Setting Groups
            item {
                Text(
                    text = "General Preferences",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            item {
                SettingActionRow(
                    icon = Icons.Outlined.AccountBalance,
                    title = "Tax & DJP Coretax Hub",
                    subtitle = "NPWP, e-Faktur, PPN 11% & PP 55",
                    onClick = { onNavigate("CORETAX") }
                )
            }

            item {
                SettingActionRow(
                    icon = Icons.Outlined.Shield,
                    title = "Users & RBAC Roles",
                    subtitle = "Manajer, Akuntan, Kasir, Auditor",
                    onClick = {}
                )
            }

            item {
                SettingActionRow(
                    icon = Icons.Outlined.Payments,
                    title = "SNAP BI-FAST Payment Gateway",
                    subtitle = "BCA, Mandiri, BRI, BNI Virtual Accounts",
                    onClick = { onNavigate("PAYMENT") }
                )
            }

            item {
                SettingActionRow(
                    icon = Icons.Outlined.DocumentScanner,
                    title = "OCR & Document Scanner",
                    subtitle = "Kamera HP/Laptop, Storage, Anti-Duplikasi",
                    onClick = { onNavigate("OCR") }
                )
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Application & Security",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            item {
                SettingActionRow(
                    icon = Icons.Outlined.Lock,
                    title = "Security & Session",
                    subtitle = "Biometric / PIN & Session Timeout",
                    onClick = {}
                )
            }

            item {
                SettingActionRow(
                    icon = Icons.Outlined.CloudSync,
                    title = "Backup & Cloud Storage",
                    subtitle = "Local Room DB & Cloud Export",
                    onClick = {}
                )
            }

            item {
                SettingActionRow(
                    icon = Icons.Outlined.Info,
                    title = "About easier Accounting",
                    subtitle = "v2.4.0 • Indonesian PSAK Standard",
                    onClick = {}
                )
            }
        }
    }
}

@Composable
fun SettingActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        color = SurfaceCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF3F0FA)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Purple600,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = subtitle,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
