package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AccountingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialReportsScreen(
    viewModel: AccountingViewModel,
    onBackClick: () -> Unit
) {
    val activeCompany = viewModel.getActiveCompany()
    val currentRole by viewModel.currentRole.collectAsState()
    val accountsMap by viewModel.accounts.collectAsState()
    val journalsMap by viewModel.journalEntries.collectAsState()
    val inventoryMap by viewModel.inventoryItems.collectAsState()
    val payablesMap by viewModel.payableItems.collectAsState()
    val receivablesMap by viewModel.receivableItems.collectAsState()

    val companyAccounts = accountsMap[activeCompany.id] ?: emptyList()
    val companyJournals = journalsMap[activeCompany.id] ?: emptyList()
    val companyInventory = inventoryMap[activeCompany.id] ?: emptyList()
    val companyPayables = payablesMap[activeCompany.id] ?: emptyList()
    val companyReceivables = receivablesMap[activeCompany.id] ?: emptyList()
    val summary = viewModel.getFinancialSummary()

    // 0: Laba Rugi, 1: Neraca, 2: Arus Kas, 3: Stok Persediaan, 4: Hutang Pemasok, 5: Piutang Supplier, 6: Bagan Akun (CoA), 7: Buku Jurnal
    var selectedReportTab by remember { mutableStateOf(0) }

    var journalToDelete by remember { mutableStateOf<JournalEntry?>(null) }
    var showUnauthorizedDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
    ) {
        // Top App Bar
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
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali", tint = TextPrimary)
                }
            },
            actions = {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifikasi",
                        tint = TextPrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceCard)
        )

        // Sub Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedReportTab,
            containerColor = SurfaceCard,
            contentColor = Emerald600,
            edgePadding = 12.dp
        ) {
            Tab(
                selected = selectedReportTab == 0,
                onClick = { selectedReportTab = 0 },
                text = { Text("Laba Rugi (P&L)", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedReportTab == 1,
                onClick = { selectedReportTab = 1 },
                text = { Text("Neraca (Balance)", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedReportTab == 2,
                onClick = { selectedReportTab = 2 },
                text = { Text("Arus Kas", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedReportTab == 3,
                onClick = { selectedReportTab = 3 },
                text = { Text("Stok Persediaan", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedReportTab == 4,
                onClick = { selectedReportTab = 4 },
                text = { Text("Hutang (Pemasok)", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedReportTab == 5,
                onClick = { selectedReportTab = 5 },
                text = { Text("Piutang (Supplier)", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedReportTab == 6,
                onClick = { selectedReportTab = 6 },
                text = { Text("Bagan Akun (CoA)", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedReportTab == 7,
                onClick = { selectedReportTab = 7 },
                text = { Text("Buku Jurnal Umum", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (selectedReportTab) {
                0 -> {
                    // TAB 0: Laporan Laba Rugi (Income Statement)
                    item {
                        Surface(
                            color = SurfaceCard,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "LAPORAN LABA RUGI KOMPREHENSIF",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Navy900
                                )
                                Text(
                                    text = "Standar SAK EMKM / EP Indonesia",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Section 1: Pendapatan Usaha
                                Text(text = "1. PENDAPATAN USAHA", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Emerald600)
                                Spacer(modifier = Modifier.height(6.dp))
                                ReportRowItem("Pendapatan Penjualan & Layanan", summary.pendapatan, false, { viewModel.formatRupiah(it) })
                                ReportRowItem("Total Pendapatan Bersih", summary.pendapatan, true, { viewModel.formatRupiah(it) })

                                Spacer(modifier = Modifier.height(14.dp))

                                // Section 2: Beban Operasional & HPP
                                Text(text = "2. BEBAN OPERASIONAL & HPP", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Crimson500)
                                Spacer(modifier = Modifier.height(6.dp))
                                companyAccounts.filter { it.category == AccountCategory.EXPENSE }.forEach { acc ->
                                    ReportRowItem(acc.name, acc.balance, false, { viewModel.formatRupiah(it) })
                                }
                                ReportRowItem("Total Beban Usaha", summary.beban, true, { viewModel.formatRupiah(it) })

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = SurfaceBorder)
                                Spacer(modifier = Modifier.height(12.dp))

                                // Final Net Profit
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Emerald100, RoundedCornerShape(10.dp))
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = "LABA BERSIH BULAN INI", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF064E3B))
                                        Text(text = "(Pendapatan - Total Beban)", fontSize = 10.sp, color = Color(0xFF064E3B))
                                    }
                                    Text(
                                        text = viewModel.formatRupiah(summary.labaBersih),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = Emerald600
                                    )
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 1: Neraca / Laporan Posisi Keuangan (Balance Sheet)
                    val totalAset = companyAccounts.filter { it.category == AccountCategory.ASSET }.sumOf { it.balance }
                    val totalLiabilitas = companyAccounts.filter { it.category == AccountCategory.LIABILITY }.sumOf { it.balance }
                    val totalEkuitas = companyAccounts.filter { it.category == AccountCategory.EQUITY }.sumOf { it.balance } + summary.labaBersih

                    item {
                        Surface(
                            color = SurfaceCard,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = "LAPORAN POSISI KEUANGAN (NERACA)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Navy900)
                                        Text(text = "Persamaan Dasar: Aset = Liabilitas + Ekuitas", fontSize = 11.sp, color = TextSecondary)
                                    }
                                    Surface(color = Emerald100, shape = RoundedCornerShape(6.dp)) {
                                        Text(
                                            text = "BALANCED",
                                            color = Emerald600,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // ASET
                                Text(text = "ASET (AKTIVA)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Blue600)
                                Spacer(modifier = Modifier.height(6.dp))
                                companyAccounts.filter { it.category == AccountCategory.ASSET }.forEach { acc ->
                                    ReportRowItem(acc.name, acc.balance, false, { viewModel.formatRupiah(it) })
                                }
                                ReportRowItem("TOTAL ASET", totalAset, true, { viewModel.formatRupiah(it) })

                                Spacer(modifier = Modifier.height(14.dp))

                                // LIABILITAS
                                Text(text = "LIABILITAS (KEWAJIBAN)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Amber500)
                                Spacer(modifier = Modifier.height(6.dp))
                                companyAccounts.filter { it.category == AccountCategory.LIABILITY }.forEach { acc ->
                                    ReportRowItem(acc.name, acc.balance, false, { viewModel.formatRupiah(it) })
                                }
                                ReportRowItem("TOTAL LIABILITAS", totalLiabilitas, true, { viewModel.formatRupiah(it) })

                                Spacer(modifier = Modifier.height(14.dp))

                                // EKUITAS
                                Text(text = "EKUITAS (MODAL & LABA DITAHAN)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Teal600)
                                Spacer(modifier = Modifier.height(6.dp))
                                companyAccounts.filter { it.category == AccountCategory.EQUITY }.forEach { acc ->
                                    ReportRowItem(acc.name, acc.balance, false, { viewModel.formatRupiah(it) })
                                }
                                ReportRowItem("Laba Bersih Periode Berjalan", summary.labaBersih, false, { viewModel.formatRupiah(it) })
                                ReportRowItem("TOTAL EKUITAS", totalEkuitas, true, { viewModel.formatRupiah(it) })

                                Spacer(modifier = Modifier.height(14.dp))
                                HorizontalDivider(color = SurfaceBorder)
                                Spacer(modifier = Modifier.height(10.dp))

                                ReportRowItem("TOTAL LIABILITAS + EKUITAS", totalLiabilitas + totalEkuitas, true, { viewModel.formatRupiah(it) })
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 2: Laporan Arus Kas (Cash Flow Statement)
                    val cashInflow = summary.pendapatan
                    val cashOutflow = summary.beban
                    val netOperatingCash = cashInflow - cashOutflow
                    val investingCash = -25000000.0
                    val financingCash = 25000000.0
                    val netCashChange = netOperatingCash + investingCash + financingCash
                    val beginningCash = 82500000.0
                    val endingCash = beginningCash + netCashChange

                    item {
                        Surface(
                            color = SurfaceCard,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "LAPORAN ARUS KAS (CASH FLOW)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Navy900
                                        )
                                        Text(
                                            text = "Metode Langsung (Direct Method SAK EMKM)",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                    Surface(color = Emerald100, shape = RoundedCornerShape(6.dp)) {
                                        Text(
                                            text = "SURPLUS",
                                            color = Emerald600,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Section 1: Aktivitas Operasi
                                Text(text = "1. ARUS KAS DARI AKTIVITAS OPERASIONAL", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Blue600)
                                Spacer(modifier = Modifier.height(6.dp))
                                ReportRowItem("Penerimaan Kas dari Pelanggan", cashInflow, false, { viewModel.formatRupiah(it) })
                                ReportRowItem("Pembayaran Kas ke Pemasok & Beban", -cashOutflow, false, { viewModel.formatRupiah(it) })
                                ReportRowItem("Kas Bersih dari Aktivitas Operasional", netOperatingCash, true, { viewModel.formatRupiah(it) })

                                Spacer(modifier = Modifier.height(14.dp))

                                // Section 2: Aktivitas Investasi
                                Text(text = "2. ARUS KAS DARI AKTIVITAS INVESTASI", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Amber500)
                                Spacer(modifier = Modifier.height(6.dp))
                                ReportRowItem("Pembelian Aset Tetap / Peralatan Server", investingCash, false, { viewModel.formatRupiah(it) })
                                ReportRowItem("Kas Bersih Digunakan untuk Investasi", investingCash, true, { viewModel.formatRupiah(it) })

                                Spacer(modifier = Modifier.height(14.dp))

                                // Section 3: Aktivitas Pendanaan
                                Text(text = "3. ARUS KAS DARI AKTIVITAS PENDANAAN", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Purple600)
                                Spacer(modifier = Modifier.height(6.dp))
                                ReportRowItem("Setoran Tambahan Modal Usaha", financingCash, false, { viewModel.formatRupiah(it) })
                                ReportRowItem("Kas Bersih dari Aktivitas Pendanaan", financingCash, true, { viewModel.formatRupiah(it) })

                                Spacer(modifier = Modifier.height(14.dp))
                                HorizontalDivider(color = SurfaceBorder)
                                Spacer(modifier = Modifier.height(10.dp))

                                ReportRowItem("KENAIKAN BERSIH KAS & BANK", netCashChange, true, { viewModel.formatRupiah(it) })
                                ReportRowItem("Saldo Kas Awal Periode", beginningCash, false, { viewModel.formatRupiah(it) })
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Emerald100, RoundedCornerShape(10.dp))
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = "SALDO AKHIR KAS & SETARA KAS", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF064E3B))
                                        Text(text = "Rekonsiliasi Kas & Rekening Bank", fontSize = 10.sp, color = Color(0xFF064E3B))
                                    }
                                    Text(
                                        text = viewModel.formatRupiah(endingCash),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 17.sp,
                                        color = Emerald600
                                    )
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // TAB 3: Laporan Stok Persediaan (Inventory Stock)
                    val totalInventoryValue = companyInventory.sumOf { it.totalValue }
                    val criticalCount = companyInventory.count { it.isLowStock }

                    item {
                        Surface(
                            color = SurfaceCard,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "RINGKASAN VALUASI PERSEDIAAN GUDANG",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Navy900
                                )
                                Text(
                                    text = "Metode Penilaian: Rata-rata Bergerak (Moving Average)",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        color = SurfaceLight,
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text("Total Nilai Stok", fontSize = 10.sp, color = TextSecondary)
                                            Text(
                                                text = viewModel.formatRupiah(totalInventoryValue),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Navy900
                                            )
                                        }
                                    }

                                    Surface(
                                        color = if (criticalCount > 0) Color(0xFFFEF2F2) else Emerald100,
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text("Status Re-order", fontSize = 10.sp, color = if (criticalCount > 0) Crimson500 else Emerald600)
                                            Text(
                                                text = if (criticalCount > 0) "$criticalCount Item Kritis" else "Semua Aman",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (criticalCount > 0) Crimson500 else Emerald600
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "DAFTAR ITEM BARANG & POSISI STOK",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(companyInventory) { item ->
                        val isCritical = item.isLowStock
                        Surface(
                            color = SurfaceCard,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                if (isCritical) 1.5.dp else 1.dp,
                                if (isCritical) Crimson500 else SurfaceBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "SKU: ${item.sku} • ${item.category}",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isCritical) Color(0xFFFEE2E2) else Color(0xFFDCFCE7)
                                    ) {
                                        Text(
                                            text = if (isCritical) "⚠️ Butuh Reorder" else "Stok Tersedia",
                                            color = if (isCritical) Crimson500 else Emerald600,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = SurfaceBorder)
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = "Sisa Fisik", fontSize = 10.sp, color = TextSecondary)
                                        Text(
                                            text = "${item.currentStock} ${item.unit} (Min: ${item.minimumStock})",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isCritical) Crimson500 else Navy900
                                        )
                                        Text(text = "Diperbarui: ${item.lastUpdated}", fontSize = 10.sp, color = TextMuted)
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(text = "Total Valuasi", fontSize = 10.sp, color = TextSecondary)
                                        Text(
                                            text = viewModel.formatRupiah(item.totalValue),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 14.sp,
                                            color = Purple600
                                        )
                                        Text(
                                            text = "@ ${viewModel.formatRupiah(item.costPrice)}",
                                            fontSize = 10.sp,
                                            color = TextMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                4 -> {
                    // TAB 4: List Hutang Pemasok (Accounts Payable)
                    val totalPayables = companyPayables.sumOf { it.remainingAmount }

                    item {
                        Surface(
                            color = SurfaceCard,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "BUKU PEMBANTU UTANG PEMASOK",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Navy900
                                        )
                                        Text(
                                            text = "Daftar tagihan pihak ketiga belum lunas",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                    Surface(color = Color(0xFFFEF2F2), shape = RoundedCornerShape(6.dp)) {
                                        Text(
                                            text = "${companyPayables.size} Tagihan",
                                            color = Crimson500,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Total Utang Beredar:",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = viewModel.formatRupiah(totalPayables),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Crimson500
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            text = "RINCIAN TAGIHAN VENDOR / PEMASOK",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(companyPayables) { payable ->
                        val isDueSoon = payable.status == "JATUH_TEMPO"
                        Surface(
                            color = SurfaceCard,
                            shape = RoundedCornerShape(12.dp),
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
                                            text = payable.supplierName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "No. Faktur: ${payable.invoiceNumber}",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isDueSoon) Color(0xFFFEE2E2) else Color(0xFFEFF6FF)
                                    ) {
                                        Text(
                                            text = if (payable.isPaidOff) "Lunas" else if (isDueSoon) "Jatuh Tempo" else "Berjalan",
                                            color = if (isDueSoon) Crimson500 else Blue600,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = SurfaceBorder)
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Jatuh Tempo: ${payable.dueDate}",
                                            fontSize = 11.sp,
                                            color = if (isDueSoon) Crimson500 else TextSecondary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "Tgl Tagihan: ${payable.invoiceDate}",
                                            fontSize = 10.sp,
                                            color = TextMuted
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = viewModel.formatRupiah(payable.remainingAmount),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 15.sp,
                                            color = Navy900
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Button(
                                            onClick = { viewModel.navigateTo("PAYMENT") },
                                            colors = ButtonDefaults.buttonColors(containerColor = Blue600),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text("Bayar via SNAP BI", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                5 -> {
                    // TAB 5: List Piutang Supplier / Klien (Accounts Receivable)
                    val totalReceivables = companyReceivables.sumOf { it.remainingAmount }

                    item {
                        Surface(
                            color = SurfaceCard,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "BUKU PEMBANTU PIUTANG USAHA",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Navy900
                                        )
                                        Text(
                                            text = "Daftar tagihan penjualan & piutang berjalan",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                    Surface(color = Emerald100, shape = RoundedCornerShape(6.dp)) {
                                        Text(
                                            text = "${companyReceivables.size} Tagihan",
                                            color = Emerald600,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Total Piutang Belum Tertagih:",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = viewModel.formatRupiah(totalReceivables),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Emerald600
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            text = "RINCIAN PIUTANG KLIEN & REKANAN",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(companyReceivables) { receivable ->
                        val isDueSoon = receivable.status == "JATUH_TEMPO"
                        Surface(
                            color = SurfaceCard,
                            shape = RoundedCornerShape(12.dp),
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
                                            text = receivable.customerOrParty,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "No. Faktur: ${receivable.invoiceNumber}",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isDueSoon) Color(0xFFFEF3C7) else Color(0xFFDCFCE7)
                                    ) {
                                        Text(
                                            text = if (receivable.isPaidOff) "Lunas" else if (isDueSoon) "Jatuh Tempo" else "Berjalan",
                                            color = if (isDueSoon) Amber700 else Emerald600,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = SurfaceBorder)
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Jatuh Tempo: ${receivable.dueDate}",
                                            fontSize = 11.sp,
                                            color = if (isDueSoon) Amber700 else TextSecondary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "Tgl Faktur: ${receivable.invoiceDate}",
                                            fontSize = 10.sp,
                                            color = TextMuted
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = viewModel.formatRupiah(receivable.remainingAmount),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 15.sp,
                                            color = Emerald600
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        OutlinedButton(
                                            onClick = { viewModel.navigateTo("INVOICES") },
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text("Lihat Faktur", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Purple600)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                6 -> {
                    // TAB 6: Chart of Accounts (CoA) Tree Explorer
                    item {
                        Text(
                            text = "DAFTAR BAGAN AKUN (CHART OF ACCOUNTS)",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(companyAccounts) { account ->
                        Surface(
                            color = SurfaceCard,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${account.code} - ${account.name}",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${account.category.label} • Saldo Normal: ${account.normalBalance.name}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                                Text(
                                    text = viewModel.formatRupiah(account.balance),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Navy900
                                )
                            }
                        }
                    }
                }

                7 -> {
                    // TAB 7: Buku Jurnal Umum (General Ledger) with Role-Aware Deletion
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "BUKU JURNAL UMUM (GENERAL LEDGER)",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Hak Hapus: Hanya Manajer & Owner",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                            Surface(
                                color = Emerald100,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "${companyJournals.size} Entri",
                                    color = Emerald600,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    if (companyJournals.isEmpty()) {
                        item {
                            Surface(
                                color = SurfaceCard,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Belum ada entri jurnal yang terposting untuk perusahaan ini.",
                                    color = TextSecondary,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    } else {
                        items(companyJournals) { journal ->
                            JournalCardItem(
                                journal = journal,
                                formatRupiah = { viewModel.formatRupiah(it) },
                                currentRole = currentRole,
                                onDeleteClick = {
                                    if (currentRole.canDeleteJournal()) {
                                        journalToDelete = journal
                                    } else {
                                        showUnauthorizedDeleteDialog = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Konfirmasi Hapus Jurnal untuk Manajer & Owner
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
                    color = Navy900
                )
            },
            text = {
                Column {
                    Text(
                        text = "Hapus entri jurnal ${journalToDelete?.entryNumber} (${journalToDelete?.memo}) dari Buku Besar?",
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Amber100,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Otorisasi peran: ${currentRole.title}. Jika jurnal ini berasal dari scan invoice, status faktur akan dikembalikan menjadi draf.",
                            fontSize = 11.sp,
                            color = Color(0xFF78350F),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        journalToDelete?.let { viewModel.deleteJournalEntry(it.id) }
                        journalToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Crimson500)
                ) {
                    Text("Ya, Hapus Jurnal", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { journalToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }

    // Modal Peringatan Akses Ditolak
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
                    text = "Peran aktif Anda saat ini (${currentRole.title}) tidak memiliki izin menghapus jurnal yang sudah diinput.\n\nSesuai SOP akuntansi dan perpajakan, jurnal yang sudah dibukukan hanya dapat dihapus oleh tingkat MANAJER KEUANGAN dan OWNER.",
                    fontSize = 13.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showUnauthorizedDeleteDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Navy900)
                ) {
                    Text("Mengerti")
                }
            }
        )
    }
}

@Composable
fun ReportRowItem(
    title: String,
    amount: Double,
    isTotal: Boolean,
    formatRupiah: (Double) -> String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = if (isTotal) 12.sp else 11.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            color = if (isTotal) Navy900 else TextSecondary
        )
        Text(
            text = formatRupiah(amount),
            fontSize = if (isTotal) 13.sp else 11.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Medium,
            color = if (isTotal) Navy900 else TextPrimary
        )
    }
}
