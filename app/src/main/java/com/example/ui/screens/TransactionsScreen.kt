package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppTransaction
import com.example.data.model.JournalEntry
import com.example.data.model.ProofFileItem
import com.example.data.model.TransactionProofType
import com.example.ui.theme.*
import com.example.ui.viewmodel.AccountingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: AccountingViewModel,
    onBackClick: () -> Unit
) {
    val activeCompany = viewModel.getActiveCompany()
    val appTransactionsMap by viewModel.appTransactions.collectAsState()
    val journalsMap by viewModel.journalEntries.collectAsState()
    val selectedFilter by viewModel.selectedTransactionFilter.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()

    val companyTrxs = appTransactionsMap[activeCompany.id] ?: emptyList()
    val companyJournals = journalsMap[activeCompany.id] ?: emptyList()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf(selectedFilter) }

    // Dialog state for reviewing proof
    var selectedTrxForReview by remember { mutableStateOf<AppTransaction?>(null) }
    var previewFileForViewing by remember { mutableStateOf<ProofFileItem?>(null) }

    // Dialog state for creating new transaction with proof upload
    var showCreateTrxDialog by remember { mutableStateOf(false) }

    // Sync selectedCategoryFilter with ViewModel filter
    LaunchedEffect(selectedFilter) {
        selectedCategoryFilter = selectedFilter
    }

    val filterOptions = listOf(
        "ALL" to "Semua",
        "Pengeluaran" to "Pengeluaran",
        "Pemasukan" to "Pemasukan",
        "Pembelian" to "Pembelian",
        "Penjualan" to "Penjualan",
        "Pelunasan" to "Pelunasan",
        "JOURNAL" to "Jurnal Umum"
    )

    // Filter transactions based on category and search query
    val filteredTrxs = remember(companyTrxs, selectedCategoryFilter, searchQuery) {
        companyTrxs.filter { trx ->
            val matchesCategory = when (selectedCategoryFilter) {
                "ALL" -> true
                "Pengeluaran" -> trx.proofType == TransactionProofType.EXPENSE
                "Pemasukan" -> trx.proofType == TransactionProofType.INCOME
                "Pembelian" -> trx.proofType == TransactionProofType.PURCHASE
                "Penjualan" -> trx.proofType == TransactionProofType.SALES
                "Pelunasan" -> trx.proofType == TransactionProofType.SETTLEMENT
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() ||
                    trx.title.contains(searchQuery, ignoreCase = true) ||
                    trx.partyName.contains(searchQuery, ignoreCase = true) ||
                    trx.transactionNumber.contains(searchQuery, ignoreCase = true) ||
                    trx.journalRefNumber.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    // Filtered journals for the Journal Umum tab or general category
    val filteredJournals = remember(companyJournals, selectedCategoryFilter, searchQuery) {
        companyJournals.filter { jrn ->
            val matchesCategory = when (selectedCategoryFilter) {
                "JOURNAL" -> true
                "Pengeluaran" -> jrn.memo.contains("Pengeluaran", ignoreCase = true) || jrn.memo.contains("Beban", ignoreCase = true)
                "Pemasukan" -> jrn.memo.contains("Pemasukan", ignoreCase = true) || jrn.memo.contains("Pendapatan", ignoreCase = true)
                "Pembelian" -> jrn.memo.contains("Pembelian", ignoreCase = true) || jrn.memo.contains("Persediaan", ignoreCase = true)
                "Penjualan" -> jrn.memo.contains("Penjualan", ignoreCase = true)
                "Pelunasan" -> jrn.memo.contains("Pelunasan", ignoreCase = true) || jrn.memo.contains("Utang", ignoreCase = true)
                else -> false
            }
            val matchesSearch = searchQuery.isBlank() ||
                    jrn.memo.contains(searchQuery, ignoreCase = true) ||
                    jrn.entryNumber.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Daftar Transaksi & Jurnal",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "${activeCompany.name} • Bukti Transaksi PDF / JPEG",
                            fontSize = 11.sp,
                            color = TextSecondary
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
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Purple100,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = currentRole.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Purple900,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceCard)
            )
        },
        floatingActionButton = {
            if (currentRole.canInputTransaction()) {
                ExtendedFloatingActionButton(
                    onClick = { showCreateTrxDialog = true },
                    containerColor = Purple700,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.AddPhotoAlternate, contentDescription = null) },
                    text = { Text("Buat Transaksi & Upload Bukti", fontWeight = FontWeight.Bold) }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(LavenderBackground)
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Info & Notice
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
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
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = null,
                                    tint = Purple700,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Review & Verifikasi Bukti Transaksi",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Emerald100
                            ) {
                                Text(
                                    text = "PSAK & DJP Verified",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald700,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Setiap transaksi mewajibkan upload bukti sah (PDF / JPEG) seperti Invoice/Bon, Bukti Bayar/Terima, dan Bukti Potong PPh.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari transaksi, lawan transaksi, no jurnal...", fontSize = 12.sp, color = TextMuted) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Outlined.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceCard,
                        unfocusedContainerColor = SurfaceCard,
                        focusedBorderColor = Purple700,
                        unfocusedBorderColor = SurfaceBorder
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Filter Category Tabs
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filterOptions) { (key, label) ->
                        val isSelected = selectedCategoryFilter == key
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = if (isSelected) Purple700 else SurfaceCard,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) Purple700 else SurfaceBorder
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .clickable {
                                    selectedCategoryFilter = key
                                    viewModel.setTransactionFilter(key)
                                }
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextSecondary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }

            // Transaksi dengan Bukti Terlampir Section
            if (selectedCategoryFilter != "JOURNAL") {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Transaksi Terverifikasi (${filteredTrxs.size})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Tekan item untuk review bukti",
                            fontSize = 11.sp,
                            color = Purple700,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (filteredTrxs.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SurfaceCard,
                            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.FolderOpen,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Belum ada transaksi pada kategori ini",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "Tekan tombol + untuk membuat transaksi dan upload bukti",
                                    fontSize = 11.sp,
                                    color = TextMuted,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(filteredTrxs) { trx ->
                        AppTransactionCard(
                            trx = trx,
                            formatRupiah = { viewModel.formatRupiah(it) },
                            onClick = { selectedTrxForReview = trx }
                        )
                    }
                }
            }

            // Jurnal Terkait Section
            if (filteredJournals.isNotEmpty() || selectedCategoryFilter == "JOURNAL") {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Jurnal Terkait di Buku Besar (${filteredJournals.size})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Navy800
                        ) {
                            Text(
                                text = "Double-Entry PSAK",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                items(filteredJournals) { journal ->
                    JournalOverviewCard(
                        journal = journal,
                        formatRupiah = { viewModel.formatRupiah(it) },
                        canDelete = currentRole.canDeleteJournal(),
                        onDeleteClick = { viewModel.deleteJournalEntry(journal.id) }
                    )
                }
            }
        }
    }

    // Modal: Review Bukti Transaksi
    selectedTrxForReview?.let { trx ->
        AlertDialog(
            onDismissRequest = { selectedTrxForReview = null },
            title = {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Review Bukti Transaksi",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (trx.isIncome) Emerald100 else Crimson100
                        ) {
                            Text(
                                text = trx.proofType.label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (trx.isIncome) Emerald800 else Crimson800,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "${trx.transactionNumber} • Ref Jurnal: ${trx.journalRefNumber}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Summary Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceLight,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = trx.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Lawan Transaksi:", fontSize = 11.sp, color = TextSecondary)
                                Text(text = trx.partyName, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Nominal:", fontSize = 11.sp, color = TextSecondary)
                                Text(
                                    text = "${if (trx.isIncome) "+ " else "- "}${viewModel.formatRupiah(trx.amount)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (trx.isIncome) Emerald600 else Crimson500
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Akun:", fontSize = 11.sp, color = TextSecondary)
                                Text(text = "${trx.accountCode} - ${trx.accountName}", fontSize = 11.sp, color = TextPrimary)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Diinput Oleh:", fontSize = 11.sp, color = TextSecondary)
                                Text(text = trx.createdBy, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Purple700)
                            }
                        }
                    }

                    Text(
                        text = "Dokumen Pembuktian Terunggah (${trx.proofFiles.size} Dokumen):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    // List of Proof files
                    trx.proofFiles.forEach { file ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceCard,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Purple200),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { previewFileForViewing = file }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (file.fileFormat == "PDF") Crimson100 else Blue100,
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = file.fileFormat,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (file.fileFormat == "PDF") Crimson700 else Blue700
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = file.stepName,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            if (file.isOptional) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "(Opsional)",
                                                    fontSize = 9.sp,
                                                    color = TextMuted
                                                )
                                            }
                                        }
                                        Text(
                                            text = file.fileName,
                                            fontSize = 11.sp,
                                            color = Purple700,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "${file.fileSize} • Diunggah: ${file.uploadTimestamp}",
                                            fontSize = 9.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Purple50
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Visibility,
                                            contentDescription = null,
                                            tint = Purple700,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "Lihat",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Purple700
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedTrxForReview = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Purple700),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Tutup Review")
                }
            }
        )
    }

    // Modal: Preview File (PDF/JPEG Visual Simulator)
    previewFileForViewing?.let { file ->
        AlertDialog(
            onDismissRequest = { previewFileForViewing = null },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(text = file.stepName, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(text = file.fileName, fontSize = 11.sp, color = Purple700)
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (file.fileFormat == "PDF") Crimson500 else Blue600
                    ) {
                        Text(
                            text = file.fileFormat,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Document Visual Container
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = if (file.fileFormat == "PDF") Icons.Default.PictureAsPdf else Icons.Default.Image,
                                    contentDescription = null,
                                    tint = if (file.fileFormat == "PDF") Crimson500 else Blue600,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = file.fileName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Ukuran: ${file.fileSize} • Terverifikasi Sah",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Emerald100
                                ) {
                                    Text(
                                        text = "✓ Digital Signature & Timestamp Valid",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Emerald700,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Catatan Verifikasi: ${file.previewNotes}",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { previewFileForViewing = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Purple700),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Selesai Melihat")
                }
            }
        )
    }

    // Modal: Wizard Pembuatan Transaksi & Upload Bukti Bertahap
    if (showCreateTrxDialog) {
        CreateTransactionWithProofWizard(
            onDismiss = { showCreateTrxDialog = false },
            onSubmit = { type, title, party, amount, isIncome, date, accountCode, accountName, proofFiles, notes ->
                viewModel.addTransactionWithProof(
                    proofType = type,
                    title = title,
                    partyName = party,
                    amount = amount,
                    isIncome = isIncome,
                    date = date,
                    accountCode = accountCode,
                    accountName = accountName,
                    proofFiles = proofFiles,
                    notes = notes
                )
                showCreateTrxDialog = false
            }
        )
    }
}

@Composable
private fun AppTransactionCard(
    trx: AppTransaction,
    formatRupiah: (Double) -> String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = if (trx.isIncome) Emerald100 else Crimson100,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (trx.isIncome) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = if (trx.isIncome) Emerald600 else Crimson500,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = trx.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${trx.partyName} • ${trx.date}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${if (trx.isIncome) "+ " else "- "}${formatRupiah(trx.amount)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (trx.isIncome) Emerald600 else Crimson500
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Purple100
                    ) {
                        Text(
                            text = "${trx.proofFiles.size} Bukti Terlampir",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Purple900,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = SurfaceBorder)
            Spacer(modifier = Modifier.height(8.dp))

            // Proof files row preview
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    trx.proofFiles.forEach { file ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (file.fileFormat == "PDF") Crimson50 else Blue50,
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, if (file.fileFormat == "PDF") Crimson200 else Blue200)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (file.fileFormat == "PDF") Icons.Default.PictureAsPdf else Icons.Default.Image,
                                    contentDescription = null,
                                    tint = if (file.fileFormat == "PDF") Crimson500 else Blue600,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = file.stepName,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Review Bukti", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Purple700)
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Purple700, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun JournalOverviewCard(
    journal: JournalEntry,
    formatRupiah: (Double) -> String,
    canDelete: Boolean,
    onDeleteClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = journal.entryNumber,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Purple700
                    )
                    Text(
                        text = "${journal.date} • ${journal.memo}",
                        fontSize = 11.sp,
                        color = TextPrimary
                    )
                }

                if (canDelete) {
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Hapus Jurnal",
                            tint = Crimson500,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            journal.lines.forEach { line ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${line.accountCode} ${line.accountName}",
                        fontSize = 10.sp,
                        color = TextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    if (line.debit > 0) {
                        Text(
                            text = "Dr ${formatRupiah(line.debit)}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Emerald700
                        )
                    }
                    if (line.credit > 0) {
                        Text(
                            text = "Cr ${formatRupiah(line.credit)}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Crimson700
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateTransactionWithProofWizard(
    onDismiss: () -> Unit,
    onSubmit: (
        type: TransactionProofType,
        title: String,
        party: String,
        amount: Double,
        isIncome: Boolean,
        date: String,
        accountCode: String,
        accountName: String,
        proofFiles: List<ProofFileItem>,
        notes: String
    ) -> Unit
) {
    var selectedType by remember { mutableStateOf(TransactionProofType.EXPENSE) }
    var title by remember { mutableStateOf("") }
    var partyName by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("26 Agu 2026") }
    var notesText by remember { mutableStateOf("") }

    // Uploaded Files State
    var fileFormatStep1 by remember { mutableStateOf("PDF") }
    var fileNameStep1 by remember { mutableStateOf("Invoice_Bon_Dokumen.pdf") }
    var isStep1Uploaded by remember { mutableStateOf(true) }

    var fileFormatStep2 by remember { mutableStateOf("JPEG") }
    var fileNameStep2 by remember { mutableStateOf("Bukti_Pembayaran_Slip.jpeg") }
    var isStep2Uploaded by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Buat Transaksi & Upload Bukti",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Langkah pembuktian terintegrasi PSAK (PDF / JPEG)",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = "1. Pilih Jenis Transaksi:", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(TransactionProofType.entries) { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type.label, fontSize = 11.sp) }
                        )
                    }
                }

                // Requirements Info Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Purple50,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Purple200),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Langkah Dokumen Pembuktian yang Diperlukan:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Purple900
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        when (selectedType) {
                            TransactionProofType.EXPENSE -> {
                                Text("• Langkah 1: Invoice / Bon (PDF / JPEG)", fontSize = 11.sp, color = TextPrimary)
                                Text("• Langkah 2: Bukti Pembayaran (PDF / JPEG)", fontSize = 11.sp, color = TextPrimary)
                            }
                            TransactionProofType.INCOME -> {
                                Text("• Langkah 1: Invoice / Bon (PDF / JPEG)", fontSize = 11.sp, color = TextPrimary)
                                Text("• Langkah 2: Bukti Penerimaan (PDF / JPEG)", fontSize = 11.sp, color = TextPrimary)
                            }
                            TransactionProofType.PURCHASE -> {
                                Text("• Langkah 1: Invoice / Bon (PDF / JPEG)", fontSize = 11.sp, color = TextPrimary)
                                Text("• Langkah 2: Bukti Potong PPh (Opsional) (PDF / JPEG)", fontSize = 11.sp, color = TextPrimary)
                            }
                            TransactionProofType.SALES -> {
                                Text("• Langkah 1: Invoice / Bon (PDF / JPEG)", fontSize = 11.sp, color = TextPrimary)
                                Text("• Langkah 2: Bukti Potong PPh (Opsional) (PDF / JPEG)", fontSize = 11.sp, color = TextPrimary)
                            }
                            TransactionProofType.SETTLEMENT -> {
                                Text("• Langkah 1: Bukti Pembayaran (PDF / JPEG)", fontSize = 11.sp, color = TextPrimary)
                            }
                        }
                    }
                }

                // Inputs
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul / Deskripsi Transaksi *") },
                    placeholder = { Text("cth: Pembayaran Server Cloud Hosting") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = partyName,
                    onValueChange = { partyName = it },
                    label = { Text("Lawan Transaksi (Vendor / Pelanggan) *") },
                    placeholder = { Text("cth: PT Telkom Data Ekosistem") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { char -> char.isDigit() } },
                    label = { Text("Nominal Transaksi (Rp) *") },
                    placeholder = { Text("cth: 15000000") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Upload Step 1 Section
                Text(text = "2. Upload Langkah 1: ${if (selectedType == TransactionProofType.SETTLEMENT) "Bukti Pembayaran" else "Invoice / Bon"}", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SurfaceCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isStep1Uploaded) Emerald400 else SurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = fileNameStep1, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                FilterChip(
                                    selected = fileFormatStep1 == "PDF",
                                    onClick = {
                                        fileFormatStep1 = "PDF"
                                        fileNameStep1 = "Dokumen_Step1.pdf"
                                    },
                                    label = { Text("PDF", fontSize = 10.sp) }
                                )
                                FilterChip(
                                    selected = fileFormatStep1 == "JPEG",
                                    onClick = {
                                        fileFormatStep1 = "JPEG"
                                        fileNameStep1 = "Foto_Step1.jpeg"
                                    },
                                    label = { Text("JPEG", fontSize = 10.sp) }
                                )
                            }
                        }
                        Text(
                            text = "✓ Dokumen siap diupload (${fileFormatStep1})",
                            fontSize = 10.sp,
                            color = Emerald700,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Upload Step 2 Section (if not SETTLEMENT)
                if (selectedType != TransactionProofType.SETTLEMENT) {
                    val step2Label = when (selectedType) {
                        TransactionProofType.EXPENSE -> "Bukti Pembayaran"
                        TransactionProofType.INCOME -> "Bukti Penerimaan"
                        TransactionProofType.PURCHASE, TransactionProofType.SALES -> "Bukti Potong PPh (Opsional)"
                        else -> "Dokumen Pendukung"
                    }

                    Text(text = "3. Upload Langkah 2: $step2Label", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SurfaceCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isStep2Uploaded) Emerald400 else SurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = fileNameStep2, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    FilterChip(
                                        selected = fileFormatStep2 == "PDF",
                                        onClick = {
                                            fileFormatStep2 = "PDF"
                                            fileNameStep2 = "Dokumen_Step2.pdf"
                                        },
                                        label = { Text("PDF", fontSize = 10.sp) }
                                    )
                                    FilterChip(
                                        selected = fileFormatStep2 == "JPEG",
                                        onClick = {
                                            fileFormatStep2 = "JPEG"
                                            fileNameStep2 = "Foto_Step2.jpeg"
                                        },
                                        label = { Text("JPEG", fontSize = 10.sp) }
                                    )
                                }
                            }
                            Text(
                                text = "✓ Dokumen siap diupload (${fileFormatStep2})",
                                fontSize = 10.sp,
                                color = Emerald700,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("Catatan / Keterangan Transaksi") },
                    placeholder = { Text("cth: Pelunasan via transfer bank") },
                    maxLines = 2,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && partyName.isNotBlank() && amount > 0) {
                        val isIncome = selectedType == TransactionProofType.INCOME || selectedType == TransactionProofType.SALES

                        val (accCode, accName) = when (selectedType) {
                            TransactionProofType.EXPENSE -> "61200" to "Beban Server & Cloud Hosting"
                            TransactionProofType.INCOME -> "41100" to "Pendapatan Penjualan / Layanan Utama"
                            TransactionProofType.PURCHASE -> "11300" to "Persediaan Barang Dagangan"
                            TransactionProofType.SALES -> "41100" to "Pendapatan Penjualan / Layanan Utama"
                            TransactionProofType.SETTLEMENT -> "21100" to "Utang Usaha (Accounts Payable)"
                        }

                        val proofList = mutableListOf<ProofFileItem>()

                        // Step 1 Proof File
                        proofList.add(
                            ProofFileItem(
                                stepName = if (selectedType == TransactionProofType.SETTLEMENT) "Bukti Pembayaran" else "Invoice / Bon",
                                fileName = fileNameStep1,
                                fileFormat = fileFormatStep1,
                                fileSize = if (fileFormatStep1 == "PDF") "450 KB" else "820 KB",
                                uploadTimestamp = "26 Agu 2026 14:00",
                                isOptional = false,
                                previewNotes = "Dokumen resmi terverifikasi sah"
                            )
                        )

                        // Step 2 Proof File
                        if (selectedType != TransactionProofType.SETTLEMENT) {
                            val step2Name = when (selectedType) {
                                TransactionProofType.EXPENSE -> "Bukti Pembayaran"
                                TransactionProofType.INCOME -> "Bukti Penerimaan"
                                TransactionProofType.PURCHASE, TransactionProofType.SALES -> "Bukti Potong PPh (Opsional)"
                                else -> "Dokumen Pendukung"
                            }
                            proofList.add(
                                ProofFileItem(
                                    stepName = step2Name,
                                    fileName = fileNameStep2,
                                    fileFormat = fileFormatStep2,
                                    fileSize = if (fileFormatStep2 == "PDF") "320 KB" else "910 KB",
                                    uploadTimestamp = "26 Agu 2026 14:02",
                                    isOptional = selectedType == TransactionProofType.PURCHASE || selectedType == TransactionProofType.SALES,
                                    previewNotes = "Dokumen verifikasi langkah 2 terunggah"
                                )
                            )
                        }

                        onSubmit(
                            selectedType,
                            title,
                            partyName,
                            amount,
                            isIncome,
                            dateText,
                            accCode,
                            accName,
                            proofList,
                            notesText
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Purple700),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Simpan Transaksi & Bukti", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
