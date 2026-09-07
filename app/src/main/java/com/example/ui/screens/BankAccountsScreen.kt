package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BankAccount
import com.example.data.model.CardType
import com.example.ui.theme.*
import com.example.ui.viewmodel.AccountingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankAccountsScreen(
    viewModel: AccountingViewModel,
    onBackClick: () -> Unit
) {
    val activeCompany = viewModel.getActiveCompany()
    val bankAccountsMap by viewModel.bankAccounts.collectAsState()
    val companyBankAccounts = bankAccountsMap[activeCompany.id] ?: emptyList()

    var showAddBankDialog by remember { mutableStateOf(false) }
    var accountToDelete by remember { mutableStateOf<BankAccount?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Kelola Akun Bank",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Navy900
                        )
                        Text(
                            text = "Rekening Operasional & Kartu Transaksi",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Navy900)
                    }
                },
                actions = {
                    IconButton(onClick = { showAddBankDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Tambah Akun Bank",
                            tint = Purple600
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceCard)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddBankDialog = true },
                containerColor = Purple600,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Tambah Akun Bank", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceLight)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Summary Banner
            item {
                Surface(
                    color = Navy900,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Blue600.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalance,
                                        contentDescription = null,
                                        tint = Blue100,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Gateway Perbankan Terhubung",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "SNAP BI Open Banking & BI-FAST",
                                        color = Emerald400,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "${companyBankAccounts.size} Akun",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                        Spacer(modifier = Modifier.height(12.dp))

                        val primaryAcc = companyBankAccounts.firstOrNull { it.isPrimary } ?: companyBankAccounts.firstOrNull()
                        if (primaryAcc != null) {
                            Text(
                                text = "Rekening Utama Operasional:",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${primaryAcc.bankName} - ${primaryAcc.accountNumber}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = primaryAcc.accountHolder,
                                    color = Emerald400,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Header Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DAFTAR AKUN BANK TERDAFTAR",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tekan ikon tempat sampah untuk menghapus",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            if (companyBankAccounts.isEmpty()) {
                item {
                    Surface(
                        color = SurfaceCard,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AccountBalance,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Belum Ada Akun Bank",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tambahkan akun bank untuk memproses transfer SNAP BI, kartu debit, dan kartu kredit operasional.",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showAddBankDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Purple600),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Tambah Akun Bank Sekarang", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                items(companyBankAccounts) { account ->
                    BankAccountCardItem(
                        account = account,
                        onDelete = { accountToDelete = account },
                        onSetPrimary = { viewModel.setPrimaryBankAccount(account.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }

    // Modal Tambah Akun Bank (Form Pengisian sesuai permintaan user: 1. Nama Bank, 2. Jenis Kartu (Debit/Kredit)-kolom centang, 3. No. Rekening Bank)
    if (showAddBankDialog) {
        AddBankAccountDialog(
            defaultHolderName = activeCompany.name,
            onDismiss = { showAddBankDialog = false },
            onSave = { bankName, cardTypes, accountNumber, holderName, branch ->
                viewModel.addBankAccount(
                    bankName = bankName,
                    cardTypes = cardTypes,
                    accountNumber = accountNumber,
                    accountHolder = holderName,
                    branchOffice = branch
                )
                showAddBankDialog = false
            }
        )
    }

    // Modal Konfirmasi Hapus Akun Bank
    accountToDelete?.let { account ->
        AlertDialog(
            onDismissRequest = { accountToDelete = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = null,
                    tint = Crimson500,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Hapus Akun Bank?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin menghapus akun ${account.bankName} no. ${account.accountNumber}? Akun tidak akan bisa digunakan lagi untuk transaksi otomatis.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteBankAccount(account.id)
                        accountToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Crimson500),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Ya, Hapus Akun", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { accountToDelete = null },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun BankAccountCardItem(
    account: BankAccount,
    onDelete: () -> Unit,
    onSetPrimary: () -> Unit
) {
    Surface(
        color = SurfaceCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            if (account.isPrimary) 1.5.dp else 1.dp,
            if (account.isPrimary) Purple600 else SurfaceBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Purple700, Purple500)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = account.bankName.take(3).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = account.bankName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextPrimary
                            )
                            if (account.isPrimary) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Purple100
                                ) {
                                    Text(
                                        text = "UTAMA",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Purple700,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = account.branchOffice,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Delete Icon Button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Hapus Akun Bank",
                        tint = Crimson500,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Card Types (Debit/Kredit Badges)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Jenis Kartu:",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                account.cardTypes.forEach { type ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (type == CardType.DEBIT) Color(0xFFEFF6FF) else Color(0xFFFEF3C7),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (type == CardType.DEBIT) Color(0xFF93C5FD) else Color(0xFFFCD34D)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = if (type == CardType.DEBIT) Icons.Outlined.CreditCard else Icons.Outlined.Payment,
                                contentDescription = null,
                                tint = if (type == CardType.DEBIT) Blue600 else Amber700,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = type.label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (type == CardType.DEBIT) Blue600 else Amber700
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = SurfaceBorder)
            Spacer(modifier = Modifier.height(12.dp))

            // Account Number & Holder
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Nomor Rekening",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = account.accountNumber,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = Navy900,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "a/n ${account.accountHolder}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                if (!account.isPrimary) {
                    OutlinedButton(
                        onClick = onSetPrimary,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Set Utama", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Purple600)
                    }
                }
            }
        }
    }
}

@Composable
fun AddBankAccountDialog(
    defaultHolderName: String,
    onDismiss: () -> Unit,
    onSave: (bankName: String, cardTypes: Set<CardType>, accountNumber: String, holderName: String, branch: String) -> Unit
) {
    var bankName by remember { mutableStateOf("") }
    var hasDebitCard by remember { mutableStateOf(true) }
    var hasCreditCard by remember { mutableStateOf(false) }
    var accountNumber by remember { mutableStateOf("") }
    var accountHolder by remember { mutableStateOf(defaultHolderName.ifBlank { "PT Sinergi Abadi Digital" }) }
    var branchOffice by remember { mutableStateOf("Kantor Cabang Utama") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val bankSuggestions = listOf("BCA", "Bank Mandiri", "BRI", "BNI", "Bank Syariah Indonesia", "CIMB Niaga", "Permata Bank")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Tambah Akun Bank Baru",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = TextPrimary
                )
                Text(
                    text = "Lengkapi data perbankan operasional usaha",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Nama Bank
                Text(
                    text = "1. Nama Bank",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Navy900
                )
                OutlinedTextField(
                    value = bankName,
                    onValueChange = {
                        bankName = it
                        errorMessage = null
                    },
                    placeholder = { Text("Contoh: BCA, Bank Mandiri, BRI") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                // Quick chips for bank suggestions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    bankSuggestions.take(4).forEach { suggestion ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (bankName == suggestion) Purple100 else SurfaceBorder.copy(alpha = 0.5f),
                            modifier = Modifier.clickable { bankName = suggestion }
                        ) {
                            Text(
                                text = suggestion,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (bankName == suggestion) Purple700 else TextSecondary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // 2. Jenis Kartu (Debit / Kredit) - beri kolom centang
                Text(
                    text = "2. Jenis Kartu (Beri centang):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Navy900
                )

                Surface(
                    color = SurfaceLight,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                        // Checkbox Debit
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { hasDebitCard = !hasDebitCard }
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = hasDebitCard,
                                onCheckedChange = { hasDebitCard = it },
                                colors = CheckboxDefaults.colors(checkedColor = Blue600)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "Kartu Debit",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Untuk penarikan tunai & debit rekening otomatis",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        HorizontalDivider(color = SurfaceBorder)

                        // Checkbox Kredit
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { hasCreditCard = !hasCreditCard }
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = hasCreditCard,
                                onCheckedChange = { hasCreditCard = it },
                                colors = CheckboxDefaults.colors(checkedColor = Amber500)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "Kartu Kredit Operasional",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Untuk pembayaran tempo & belanja korporat",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // 3. No. Rekening Bank
                Text(
                    text = "3. No. Rekening Bank",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Navy900
                )
                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = {
                        accountNumber = it
                        errorMessage = null
                    },
                    placeholder = { Text("Contoh: 0882-9912-30") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                // Pemilik Rekening
                Text(
                    text = "Nama Pemilik Rekening",
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                OutlinedTextField(
                    value = accountHolder,
                    onValueChange = { accountHolder = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = Crimson500,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (bankName.isBlank()) {
                        errorMessage = "Nama Bank wajib diisi"
                        return@Button
                    }
                    if (!hasDebitCard && !hasCreditCard) {
                        errorMessage = "Pilih minimal satu jenis kartu (Debit atau Kredit)"
                        return@Button
                    }
                    if (accountNumber.isBlank() || accountNumber.length < 5) {
                        errorMessage = "Nomor rekening tidak valid (minimal 5 digit)"
                        return@Button
                    }

                    val cardTypes = mutableSetOf<CardType>()
                    if (hasDebitCard) cardTypes.add(CardType.DEBIT)
                    if (hasCreditCard) cardTypes.add(CardType.CREDIT)

                    onSave(bankName.trim(), cardTypes, accountNumber.trim(), accountHolder.trim(), branchOffice.trim())
                },
                colors = ButtonDefaults.buttonColors(containerColor = Purple600),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Simpan Akun Bank", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Batal")
            }
        }
    )
}
