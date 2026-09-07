package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Company
import com.example.data.model.RoleType
import com.example.ui.theme.*

@Composable
fun CompanyTopBar(
    activeCompany: Company,
    companies: List<Company>,
    currentRole: RoleType,
    userEmail: String? = null,
    onSwitchCompany: (String) -> Unit,
    onSwitchRole: (RoleType) -> Unit,
    onAddCompanyClick: () -> Unit,
    onLogout: () -> Unit = {}
) {
    var showCompanyMenu by remember { mutableStateOf(false) }
    var showAccountMenu by remember { mutableStateOf(false) }

    Surface(
        color = SurfaceCard,
        tonalElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Header Row: Menu Drawer, Logo & Subtitle, Notification & Profile
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Drawer / Company Switcher Button
                Box {
                    IconButton(
                        onClick = { showCompanyMenu = true },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Menu,
                            contentDescription = "Pilih Perusahaan & Menu",
                            tint = TextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Company Dropdown Menu
                    DropdownMenu(
                        expanded = showCompanyMenu,
                        onDismissRequest = { showCompanyMenu = false },
                        modifier = Modifier.background(SurfaceCard)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            Text(
                                text = "MULTI-ENTITY WORKSPACE",
                                color = Purple600,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Pilih Perusahaan Aktif",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        HorizontalDivider(color = SurfaceBorder)
                        companies.forEach { company ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = company.name,
                                            color = if (company.id == activeCompany.id) Purple700 else TextPrimary,
                                            fontWeight = if (company.id == activeCompany.id) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "${company.entityType.name} • ${if (company.isPkp) "PKP (PPN 11%)" else "Non-PKP"}",
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (company.id == activeCompany.id) Icons.Filled.CheckCircle else Icons.Outlined.Business,
                                        contentDescription = null,
                                        tint = if (company.id == activeCompany.id) Purple600 else TextMuted
                                    )
                                },
                                onClick = {
                                    onSwitchCompany(company.id)
                                    showCompanyMenu = false
                                }
                            )
                        }
                        HorizontalDivider(color = SurfaceBorder)
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "+ Tambah Perusahaan Baru",
                                    color = Purple600,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.AddBusiness,
                                    contentDescription = null,
                                    tint = Purple600
                                )
                            },
                            onClick = {
                                showCompanyMenu = false
                                onAddCompanyClick()
                            }
                        )
                    }
                }

                // Center: easier Branding & Slogan
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { showCompanyMenu = true }
                ) {
                    Text(
                        text = "easier",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Getting easier in one comand.",
                        color = Purple600,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.2.sp
                    )
                }

                // Right: Notification & Account (Role is hidden inside app as selected at login)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Notification & Account Bell
                    Box {
                        IconButton(
                            onClick = { showAccountMenu = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = Purple600,
                                        contentColor = Color.White
                                    ) {
                                        Text("3", fontSize = 9.sp)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Notifications,
                                    contentDescription = "Notifikasi & Akun",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showAccountMenu,
                            onDismissRequest = { showAccountMenu = false },
                            modifier = Modifier.background(SurfaceCard)
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                                Text(
                                    text = "AKUN TERDAFTAR",
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = userEmail ?: "pteduworksasiaagroindustries@gmail.com",
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Status: Terverifikasi DJP Coretax",
                                    color = Emerald600,
                                    fontSize = 11.sp
                                )
                            }
                            HorizontalDivider(color = SurfaceBorder)
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Keluar / Ganti Akun",
                                        color = Crimson500,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Logout,
                                        contentDescription = null,
                                        tint = Crimson500
                                    )
                                },
                                onClick = {
                                    showAccountMenu = false
                                    onLogout()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppNotificationBanner(
    title: String,
    message: String,
    isSuccess: Boolean = true,
    onDismiss: () -> Unit
) {
    Surface(
        color = if (isSuccess) Emerald600 else Crimson500,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Cancel,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = message,
                    color = Color.White.copy(alpha = 0.95f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Tutup",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    amount: String,
    subtitle: String,
    icon: ImageVector,
    iconBgColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SurfaceCard,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconBgColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconBgColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = amount,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 11.sp
            )
        }
    }
}
