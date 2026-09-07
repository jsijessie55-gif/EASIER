package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
fun ExpensesScreen(
    viewModel: AccountingViewModel,
    onBackClick: () -> Unit,
    onAddExpenseClick: () -> Unit = {}
) {
    val summary = viewModel.getFinancialSummary()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(0) }

    val categories = listOf("All", "Office", "Marketing", "Utilities", "Salary", "Travel")

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
            actions = {
                IconButton(onClick = onAddExpenseClick) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah Pengeluaran",
                        tint = Purple600
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
                    text = "Expenses",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
            }

            // Total Expenses Card
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = Color.Transparent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFFEDE9FE),
                                        Color(0xFFF3E8FF),
                                        Color(0xFFFEE2E2)
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total Expenses (This Month)",
                                    color = Color(0xFF6B7280),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Crimson500.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.TrendingDown,
                                        contentDescription = null,
                                        tint = Crimson500,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (summary.beban > 0) viewModel.formatRupiah(summary.beban) else "Rp 49.500.000",
                                color = TextPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "↓ 4% lower than last month",
                                color = Emerald600,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search expense item...", fontSize = 13.sp, color = TextMuted) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = SurfaceCard,
                        focusedContainerColor = SurfaceCard,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedBorderColor = Purple600
                    ),
                    singleLine = true
                )
            }

            // Category Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories.size) { index ->
                        val isSelected = selectedCategory == index
                        Surface(
                            color = if (isSelected) Purple600 else SurfaceCard,
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) Purple600 else SurfaceBorder
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { selectedCategory = index }
                        ) {
                            Text(
                                text = categories[index],
                                color = if (isSelected) Color.White else TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Expenses Item List
            item {
                TransactionItemRow(
                    title = "Office Supplies & Stationery",
                    subtitle = "Office • 19 May 2026",
                    amount = "- Rp 1.250.000",
                    isIncome = false,
                    icon = Icons.Outlined.ShoppingBag,
                    iconBg = Color(0xFFFEE2E2),
                    iconTint = Crimson500,
                    onClick = {}
                )
            }

            item {
                TransactionItemRow(
                    title = "Internet & Telecommunication",
                    subtitle = "Utilities • 18 May 2026",
                    amount = "- Rp 500.000",
                    isIncome = false,
                    icon = Icons.Outlined.Wifi,
                    iconBg = Color(0xFFFEF3C7),
                    iconTint = Amber500,
                    onClick = {}
                )
            }

            item {
                TransactionItemRow(
                    title = "Monthly Staff Payroll",
                    subtitle = "Salary • 16 May 2026",
                    amount = "- Rp 12.000.000",
                    isIncome = false,
                    icon = Icons.Outlined.Badge,
                    iconBg = Color(0xFFFEE2E2),
                    iconTint = Crimson500,
                    onClick = {}
                )
            }

            item {
                TransactionItemRow(
                    title = "Digital Ads & Campaigns",
                    subtitle = "Marketing • 14 May 2026",
                    amount = "- Rp 3.750.000",
                    isIncome = false,
                    icon = Icons.Outlined.Campaign,
                    iconBg = Color(0xFFEDE9FE),
                    iconTint = Purple600,
                    onClick = {}
                )
            }

            item {
                TransactionItemRow(
                    title = "Office Electricity & Water",
                    subtitle = "Utilities • 10 May 2026",
                    amount = "- Rp 1.850.000",
                    isIncome = false,
                    icon = Icons.Outlined.Bolt,
                    iconBg = Color(0xFFFEF3C7),
                    iconTint = Amber500,
                    onClick = {}
                )
            }
        }
    }
}

@Composable
fun TransactionItemRow(
    title: String,
    subtitle: String,
    amount: String,
    isIncome: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Surface(
        color = SurfaceCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Text(
                text = amount,
                color = if (isIncome) Emerald600 else TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
