package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AccountingViewModel

@Composable
fun DashboardScreen(
    viewModel: AccountingViewModel,
    onNavigate: (String) -> Unit
) {
    val activeCompany = viewModel.getActiveCompany()
    val currentRole by viewModel.currentRole.collectAsState()
    val invoicesMap by viewModel.invoices.collectAsState()
    val journalsMap by viewModel.journalEntries.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()
    val userAccessPasses by viewModel.userAccessPasses.collectAsState()

    val companyInvoices = invoicesMap[activeCompany.id] ?: emptyList()
    val companyJournals = journalsMap[activeCompany.id] ?: emptyList()
    val summary = viewModel.getFinancialSummary()
    val pendingPasses = userAccessPasses.filter { it.status == com.example.data.model.PassStatus.PENDING }

    var selectedTimeframe by remember { mutableStateOf("This Month") }
    var showTimeframeMenu by remember { mutableStateOf(false) }

    val duePaymentItems = remember(activeCompany.id) { viewModel.getDuePaymentItems() }
    var selectedDueCategory by remember { mutableStateOf<DueCategory?>(null) }
    val filteredDueItems = remember(duePaymentItems, selectedDueCategory) {
        if (selectedDueCategory == null) duePaymentItems
        else duePaymentItems.filter { it.category == selectedDueCategory }
    }

    // Dialogs for deletion/authorization
    var journalToDelete by remember { mutableStateOf<JournalEntry?>(null) }
    var showUnauthorizedDeleteDialog by remember { mutableStateOf(false) }

    // Greeting Name extraction
    val greetingName = remember(userEmail, activeCompany) {
        val emailPrefix = userEmail?.substringBefore("@")?.split(".")?.firstOrNull()?.replaceFirstChar { it.uppercase() }
        if (!emailPrefix.isNullOrBlank() && emailPrefix.length in 3..15) {
            emailPrefix
        } else if (activeCompany.name.isNotBlank()) {
            activeCompany.name.split(" ").firstOrNull() ?: "Jessie"
        } else {
            "Jessie"
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LavenderBackground),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. User Greeting Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hello, $greetingName ",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "👋",
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Let's grow your business",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 1.5 Pending Pass Requests Banner (Only shown if Owner/Manager and there are pending passes)
        if (currentRole.canApprovePass() && pendingPasses.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Amber50),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Amber400),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MarkEmailUnread,
                                    contentDescription = null,
                                    tint = Amber600,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Permintaan Pass Masuk (${pendingPasses.size})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Amber500
                            ) {
                                Text(
                                    text = "Butuh Otorisasi ${currentRole.title}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        pendingPasses.forEach { pass ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${pass.name} (${pass.requestedRole.title})",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "Email: ${pass.email}",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                        if (pass.notes.isNotBlank()) {
                                            Text(
                                                text = "Keperluan: ${pass.notes}",
                                                fontSize = 10.sp,
                                                color = Purple700,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        OutlinedButton(
                                            onClick = { viewModel.rejectUserAccessPass(pass.id, "Ditolak oleh ${currentRole.title}") },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Crimson500),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                            modifier = Modifier.height(34.dp)
                                        ) {
                                            Text("Tolak", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = { viewModel.approveUserAccessPass(pass.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.height(34.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Setujui Pass", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Role-Based Primary Section:
        if (currentRole.isExternal()) {
            // EKSTERNAL: Tampilan Total Kas, Piutang, Hutang, dan Laporan Keuangan
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Ringkasan Keuangan (Eksternal)",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ExternalMetricCard(
                            title = "Total Kas",
                            amount = if (summary.kasBank > 0) viewModel.formatRupiah(summary.kasBank) else "Rp 125.500.000",
                            icon = Icons.Default.AccountBalanceWallet,
                            color = Emerald600,
                            modifier = Modifier.weight(1f)
                        )
                        ExternalMetricCard(
                            title = "Total Piutang",
                            amount = if (summary.piutangUsaha > 0) viewModel.formatRupiah(summary.piutangUsaha) else "Rp 45.000.000",
                            icon = Icons.Default.CallReceived,
                            color = Blue600,
                            modifier = Modifier.weight(1f)
                        )
                        ExternalMetricCard(
                            title = "Total Hutang",
                            amount = if (summary.utangUsaha > 0) viewModel.formatRupiah(summary.utangUsaha) else "Rp 28.500.000",
                            icon = Icons.Default.CallMade,
                            color = Crimson500,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Akses Laporan Keuangan", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                            Text("Laba Rugi, Neraca, Arus Kas & Analisis", fontSize = 11.sp, color = TextSecondary)
                        }
                        Button(
                            onClick = { onNavigate("REPORTS") },
                            colors = ButtonDefaults.buttonColors(containerColor = Purple600),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Buka Laporan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else if (currentRole.isAdminOrStaff()) {
            // ADMIN / STAFF: Hanya scan dokumen/faktur/nota, pembayaran hutang, pembayaran piutang, pembayaran pajak, dan laporan stok barang
            item {
                Surface(
                    color = SurfaceCard,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Badge, contentDescription = null, tint = Purple600, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Panel Tugas Operasional Staff & Admin", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            StaffTaskCard(
                                title = "Scan OCR",
                                subtitle = "Dokumen / Faktur / Nota",
                                icon = Icons.Filled.QrCodeScanner,
                                color = Purple600,
                                modifier = Modifier.weight(1f),
                                onClick = { onNavigate("OCR") }
                            )
                            StaffTaskCard(
                                title = "Bayar Hutang",
                                subtitle = "Pembayaran Pemasok",
                                icon = Icons.Filled.Payments,
                                color = Crimson500,
                                modifier = Modifier.weight(1f),
                                onClick = { onNavigate("PAYMENT") }
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            StaffTaskCard(
                                title = "Terima Piutang",
                                subtitle = "Penagihan Pelanggan",
                                icon = Icons.Filled.ReceiptLong,
                                color = Emerald600,
                                modifier = Modifier.weight(1f),
                                onClick = { onNavigate("PAYMENT") }
                            )
                            StaffTaskCard(
                                title = "Bayar Pajak",
                                subtitle = "PPh & PPN Coretax",
                                icon = Icons.Filled.AccountBalance,
                                color = Amber500,
                                modifier = Modifier.weight(1f),
                                onClick = { onNavigate("CORETAX") }
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        StaffTaskCard(
                            title = "Laporan Stok Persediaan",
                            subtitle = "Monitoring opname & kartu stok barang",
                            icon = Icons.Filled.Inventory,
                            color = Blue600,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onNavigate("REPORTS") }
                        )
                    }
                }
            }
        } else {
            // OWNER & MANAGER: Total Balance Card with decorative wave
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.Transparent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFEDE9FE), // Lavender 100
                                        Color(0xFFF3E8FF), // Purple 50
                                        Color(0xFFE0E7FF)  // Indigo 100
                                    )
                                )
                            )
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val width = size.width
                            val height = size.height

                            val path1 = Path().apply {
                                moveTo(0f, height * 0.75f)
                                cubicTo(
                                    width * 0.35f, height * 0.55f,
                                    width * 0.65f, height * 0.95f,
                                    width, height * 0.65f
                                )
                                lineTo(width, height)
                                lineTo(0f, height)
                                close()
                            }
                            drawPath(
                                path = path1,
                                color = Color(0xFFDDD6FE).copy(alpha = 0.45f)
                            )

                            val path2 = Path().apply {
                                moveTo(0f, height * 0.85f)
                                cubicTo(
                                    width * 0.4f, height * 0.70f,
                                    width * 0.7f, height * 1.0f,
                                    width, height * 0.80f
                                )
                                lineTo(width, height)
                                lineTo(0f, height)
                                close()
                            }
                            drawPath(
                                path = path2,
                                color = Color(0xFFC4B5FD).copy(alpha = 0.35f)
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 22.dp, vertical = 20.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Total Balance",
                                    color = Color(0xFF6B7280),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (summary.kasBank > 0) viewModel.formatRupiah(summary.kasBank) else "Rp 125.500.000",
                                    color = TextPrimary,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = (-0.5).sp
                                )
                            }

                            Surface(
                                color = Color.White.copy(alpha = 0.85f),
                                shape = RoundedCornerShape(20.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDD6FE))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "↗ 12% from last month",
                                        color = Purple700,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick Menu (Owner/Manager)
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Quick Menu",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickMenuButton(
                            label = "Transactions",
                            icon = Icons.Outlined.SwapHoriz,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate("TRANSACTIONS") }
                        )
                        QuickMenuButton(
                            label = "Invoices",
                            icon = Icons.Outlined.ReceiptLong,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate("INVOICES") }
                        )
                        QuickMenuButton(
                            label = "Expenses",
                            icon = Icons.Outlined.AccountBalanceWallet,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate("EXPENSES") }
                        )
                        QuickMenuButton(
                            label = "Reports",
                            icon = Icons.Outlined.PieChart,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate("REPORTS") }
                        )
                    }
                }
            }

            // Feature Badges (Owner/Manager)
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        ComplianceChip(
                            title = "📷 Scan OCR Struk",
                            subtitle = "Auto Extract & Anti-Duplikasi",
                            badgeColor = Purple600,
                            onClick = { onNavigate("OCR") }
                        )
                    }
                    item {
                        ComplianceChip(
                            title = "🏛️ DJP Coretax Hub",
                            subtitle = if (activeCompany.isPkp) "e-Faktur PPN 11%" else "PP 55 Non-PKP",
                            badgeColor = Amber500,
                            onClick = { onNavigate("CORETAX") }
                        )
                    }
                    item {
                        ComplianceChip(
                            title = "🏦 Akun Bank",
                            subtitle = "Debit & Kredit Rekening",
                            badgeColor = Emerald600,
                            onClick = { onNavigate("BANK") }
                        )
                    }
                    item {
                        ComplianceChip(
                            title = "⚡ SNAP BI-FAST",
                            subtitle = "Multi-Bank Direct Payout",
                            badgeColor = Blue600,
                            onClick = { onNavigate("PAYMENT") }
                        )
                    }
                }
            }
        }

        // 5. Cash Flow Overview Card (Chart & Legend)
        item {
            Surface(
                color = SurfaceCard,
                shape = RoundedCornerShape(22.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Header & Timeframe Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cash Flow Overview",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Box {
                            Surface(
                                color = Color(0xFFF3F4F6),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { showTimeframeMenu = true }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = selectedTimeframe,
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = showTimeframeMenu,
                                onDismissRequest = { showTimeframeMenu = false }
                            ) {
                                listOf("This Month", "Last 3 Months", "Year 2026").forEach { timeframe ->
                                    DropdownMenuItem(
                                        text = { Text(timeframe, fontSize = 12.sp) },
                                        onClick = {
                                            selectedTimeframe = timeframe
                                            showTimeframeMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Interactive Income vs Expenses Dual Action Cards (Clickable to Journals)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Income Card (Clickable to Pemasukan Journals)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Emerald50,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Emerald400),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    viewModel.openTransactionsFiltered("Pemasukan")
                                    onNavigate("TRANSACTIONS")
                                }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(Emerald600)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Pemasukan",
                                            color = Emerald900,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ArrowOutward,
                                        contentDescription = "Buka Jurnal Pemasukan",
                                        tint = Emerald600,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (summary.pendapatan > 0) viewModel.formatRupiah(summary.pendapatan) else "Rp 175.000.000",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "👉 Tekan untuk lihat jurnal",
                                    color = Emerald700,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Expense Card (Clickable to Pengeluaran Journals)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Crimson50,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Crimson400),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    viewModel.openTransactionsFiltered("Pengeluaran")
                                    onNavigate("TRANSACTIONS")
                                }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(Crimson500)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Pengeluaran",
                                            color = Crimson900,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ArrowOutward,
                                        contentDescription = "Buka Jurnal Pengeluaran",
                                        tint = Crimson500,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (summary.beban > 0) viewModel.formatRupiah(summary.beban) else "Rp 49.500.000",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "👉 Tekan untuk lihat jurnal",
                                    color = Crimson700,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 6-Month Bar Chart Visualization
                    CashFlowBarChart()
                }
            }
        }

        // 6. Notifikasi Pembayaran Jatuh Tempo (Piutang, Hutang, Biaya Pajak PPh/PPN)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = Crimson500,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Jatuh Tempo Pembayaran",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Piutang, Hutang Usaha & Biaya Pajak (PPN/PPh)",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Crimson50,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Crimson400.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "${filteredDueItems.size} Perlu Aksi",
                            color = Crimson500,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Chips (Semua, Piutang Usaha, Hutang Usaha, Pajak)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedDueCategory == null,
                            onClick = { selectedDueCategory = null },
                            label = { Text("Semua (${duePaymentItems.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedDueCategory == DueCategory.RECEIVABLE,
                            onClick = { selectedDueCategory = DueCategory.RECEIVABLE },
                            label = { Text("Piutang (${duePaymentItems.count { it.category == DueCategory.RECEIVABLE }})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2563EB))
                                )
                            }
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedDueCategory == DueCategory.PAYABLE,
                            onClick = { selectedDueCategory = DueCategory.PAYABLE },
                            label = { Text("Hutang (${duePaymentItems.count { it.category == DueCategory.PAYABLE }})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFDC2626))
                                )
                            }
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedDueCategory == DueCategory.TAX,
                            onClick = { selectedDueCategory = DueCategory.TAX },
                            label = { Text("Pajak (${duePaymentItems.count { it.category == DueCategory.TAX }})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFD97706))
                                )
                            }
                        )
                    }
                }
            }
        }

        if (filteredDueItems.isEmpty()) {
            item {
                Surface(
                    color = SurfaceCard,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Emerald600,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tidak Ada Pembayaran Jatuh Tempo",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Semua piutang, hutang, dan kewajiban pajak telah diproses dengan tertib.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(filteredDueItems) { dueItem ->
                DuePaymentCardRow(
                    item = dueItem,
                    formatRupiah = { viewModel.formatRupiah(it) },
                    onActionClick = { onNavigate(dueItem.targetScreen) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Modal Konfirmasi Hapus Jurnal
    if (journalToDelete != null) {
        AlertDialog(
            onDismissRequest = { journalToDelete = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = null,
                    tint = Crimson500,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Konfirmasi Hapus Jurnal",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin menghapus entri jurnal ${journalToDelete?.entryNumber} (${journalToDelete?.memo})?\n\nTindakan ini diotorisasi oleh peran ${currentRole.title}.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        journalToDelete?.let { viewModel.deleteJournalEntry(it.id) }
                        journalToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Crimson500)
                ) {
                    Text("Ya, Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { journalToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }

    // Modal Akses Ditolak
    if (showUnauthorizedDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showUnauthorizedDeleteDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.GppBad,
                    contentDescription = null,
                    tint = Crimson500,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Akses Ditolak!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Crimson500
                )
            },
            text = {
                Text(
                    text = "Peran aktif Anda saat ini (${currentRole.title}) tidak memiliki izin menghapus jurnal.\n\nHanya tingkat MANAJER KEUANGAN dan OWNER yang diizinkan.",
                    fontSize = 13.sp,
                    color = TextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = { showUnauthorizedDeleteDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Purple600)
                ) {
                    Text("Mengerti")
                }
            }
        )
    }
}

@Composable
fun QuickMenuButton(
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = SurfaceCard,
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF3F0FA)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Purple600,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ComplianceChip(
    title: String,
    subtitle: String,
    badgeColor: Color,
    onClick: () -> Unit
) {
    Surface(
        color = SurfaceCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.25f)),
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(badgeColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun DuePaymentCardRow(
    item: DuePaymentItem,
    formatRupiah: (Double) -> String,
    onActionClick: () -> Unit
) {
    val (bgColor, iconColor, iconVector) = when (item.category) {
        DueCategory.RECEIVABLE -> Triple(Color(0xFFEFF6FF), Color(0xFF2563EB), Icons.Outlined.CallReceived)
        DueCategory.PAYABLE -> Triple(Color(0xFFFEF2F2), Color(0xFFDC2626), Icons.Outlined.CallMade)
        DueCategory.TAX -> Triple(Color(0xFFFFFBEB), Color(0xFFD97706), Icons.Outlined.AccountBalance)
    }

    Surface(
        color = SurfaceCard,
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = bgColor
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = iconVector,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item.category.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = iconColor
                        )
                    }
                }

                // Urgency badge
                val (urgencyBg, urgencyText, urgencyLabel) = when {
                    item.daysRemaining <= 1 -> Triple(Color(0xFFFEE2E2), Color(0xFFDC2626), "Jatuh Tempo Besok!")
                    item.daysRemaining <= 3 -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), "${item.daysRemaining} Hari Lagi")
                    else -> Triple(Color(0xFFF1F5F9), Color(0xFF475569), "${item.daysRemaining} Hari Lagi")
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = urgencyBg
                ) {
                    Text(
                        text = urgencyLabel,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = urgencyText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title & Party
            Text(
                text = item.partyOrDescription,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.title,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                Text(
                    text = " • ",
                    fontSize = 11.sp,
                    color = TextMuted
                )
                Text(
                    text = item.referenceNumber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = SurfaceBorder.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(10.dp))

            // Amount & Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Nominal",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = formatRupiah(item.amount),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = iconColor
                    )
                    Text(
                        text = "Jatuh Tempo: ${item.dueDate}",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }

                Button(
                    onClick = onActionClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = iconColor
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.height(38.dp)
                ) {
                    Text(
                        text = item.actionLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun CashFlowBarChart() {
    val months = listOf(
        Triple("Jan", 0.6f, 0.35f),
        Triple("Feb", 0.75f, 0.45f),
        Triple("Mar", 0.55f, 0.3f),
        Triple("Apr", 0.85f, 0.5f),
        Triple("May", 0.95f, 0.4f),
        Triple("Jun", 0.7f, 0.38f)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        months.forEach { (month, incomeRatio, expenseRatio) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.height(100.dp)
                ) {
                    // Income Bar (Purple)
                    Box(
                        modifier = Modifier
                            .width(8.dp)
                            .fillMaxHeight(incomeRatio)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Purple500, Purple700)
                                )
                            )
                    )

                    // Expense Bar (Rose / Coral)
                    Box(
                        modifier = Modifier
                            .width(8.dp)
                            .fillMaxHeight(expenseRatio)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFFF87171), Color(0xFFEF4444))
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = month,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun JournalCardItem(
    journal: JournalEntry,
    formatRupiah: (Double) -> String,
    currentRole: RoleType,
    onDeleteClick: () -> Unit
) {
    Surface(
        color = SurfaceCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = journal.entryNumber,
                        color = Purple700,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = journal.date,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Hapus Jurnal",
                        tint = if (currentRole.canDeleteJournal()) Crimson500 else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = journal.memo,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = SurfaceBorder)
            Spacer(modifier = Modifier.height(8.dp))

            journal.lines.forEach { line ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${line.accountCode} - ${line.accountName}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Text(
                        text = if (line.debit > 0) "D: ${formatRupiah(line.debit)}" else "K: ${formatRupiah(line.credit)}",
                        color = if (line.debit > 0) Purple700 else Emerald600,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
