package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AccountingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoretaxHubScreen(
    viewModel: AccountingViewModel,
    onBackClick: () -> Unit
) {
    val activeCompany = viewModel.getActiveCompany()
    val taxFilingsMap by viewModel.taxFilings.collectAsState()
    val prePopulatedEFaktur by viewModel.prePopulatedEFaktur.collectAsState()

    val companyTaxFilings = taxFilingsMap[activeCompany.id] ?: emptyList()
    var selectedTab by remember { mutableStateOf(0) } // 0: SPT & Billing, 1: Pre-Populated e-Faktur, 2: Security Vault

    var bpeReceiptToShow by remember { mutableStateOf<TaxFiling?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "DJP Coretax System",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Navy900
                    )
                    Text(
                        text = "Integrasi e-Faktur, ID Billing & Filing SPT",
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
            colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceCard)
        )

        // Status Card: Coretax Bridge Active
        Surface(
            color = Navy900,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Emerald500)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "API Gateway DJP Coretax: Terhubung",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "NPWP: ${activeCompany.npwp16 ?: "Belum Didaftarkan"} • Sertel Aktif (2027)",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
                Surface(
                    color = Emerald500.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "KMS ENCRYPTED",
                        color = Emerald400,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // Sub Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = SurfaceCard,
            contentColor = Emerald600
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Filing SPT & Billing", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("e-Faktur Pre-Populated", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Vault Sertel & KMS", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // TAB 0: SPT & MPN G3 Billing
                    item {
                        Text(
                            text = "DAFTAR SPT MASA & STATUS KODE BILLING",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(companyTaxFilings) { filing ->
                        TaxFilingCard(
                            filing = filing,
                            formatRupiah = { viewModel.formatRupiah(it) },
                            onGenerateBilling = { viewModel.generateTaxBilling(filing.id) },
                            onPayBilling = { viewModel.payTaxBilling(filing.id) },
                            onFileCoretax = { viewModel.fileToCoretax(filing.id) },
                            onViewBpe = { bpeReceiptToShow = filing }
                        )
                    }
                }

                1 -> {
                    // TAB 1: Pre-Populated e-Faktur from DJP Coretax
                    item {
                        Surface(
                            color = Blue100,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Sync, contentDescription = null, tint = Blue600)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Sinkronisasi Real-Time Coretax",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Blue600
                                    )
                                    Text(
                                        text = "Faktur Pajak Masukan supplier ditarik otomatis untuk rekonsiliasi OCR.",
                                        fontSize = 11.sp,
                                        color = Navy900
                                    )
                                }
                            }
                        }
                    }

                    items(prePopulatedEFaktur) { efaktur ->
                        EFakturCardItem(
                            efaktur = efaktur,
                            formatRupiah = { viewModel.formatRupiah(it) }
                        )
                    }
                }

                2 -> {
                    // TAB 2: Sertel & KMS Security Vault
                    item {
                        SecurityVaultDetails(activeCompany = activeCompany)
                    }
                }
            }
        }
    }

    // Official BPE Receipt Modal
    if (bpeReceiptToShow != null) {
        val filing = bpeReceiptToShow!!
        Dialog(onDismissRequest = { bpeReceiptToShow = null }) {
            Surface(
                color = SurfaceCard,
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 10.dp,
                modifier = Modifier.fillMaxWidth(0.95f)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Emerald100),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = Emerald600,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "BUKTI PENERIMAAN ELEKTRONIK (BPE)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Navy900
                    )
                    Text(
                        text = "Direktorat Jenderal Pajak RI - Coretax Engine",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceLight, RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BpeDetailRow("Nomor BPE (NTTE):", filing.bpeNumber ?: "-")
                        BpeDetailRow("Wajib Pajak:", activeCompany.name)
                        BpeDetailRow("NPWP 16:", activeCompany.npwp16 ?: "-")
                        BpeDetailRow("Jenis SPT:", filing.taxType.label)
                        BpeDetailRow("Masa / Tahun:", "${filing.period} ${filing.taxYear}")
                        BpeDetailRow("NTPN Setor:", filing.ntpn ?: "-")
                        BpeDetailRow("Jumlah Disetor:", viewModel.formatRupiah(filing.totalTax))
                        BpeDetailRow("Waktu Penerimaan:", filing.submittedAt ?: "-")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { bpeReceiptToShow = null },
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Tutup Bukti Penerimaan", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun BpeDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextSecondary, fontSize = 11.sp)
        Text(text = value, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1)
    }
}

@Composable
fun TaxFilingCard(
    filing: TaxFiling,
    formatRupiah: (Double) -> String,
    onGenerateBilling: () -> Unit,
    onPayBilling: () -> Unit,
    onFileCoretax: () -> Unit,
    onViewBpe: () -> Unit
) {
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
                        text = filing.taxType.label,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Masa Pajak: ${filing.period} ${filing.taxYear} • KAP/KJS: ${filing.taxType.kapKjs}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
                Surface(
                    color = when (filing.status) {
                        TaxFilingStatus.DRAFT -> Amber100
                        TaxFilingStatus.BILLED -> Blue100
                        TaxFilingStatus.PAID -> Emerald100
                        TaxFilingStatus.FILED -> Color(0xFFE0E7FF)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = filing.status.label,
                        color = when (filing.status) {
                            TaxFilingStatus.DRAFT -> Color(0xFFB45309)
                            TaxFilingStatus.BILLED -> Blue600
                            TaxFilingStatus.PAID -> Emerald600
                            TaxFilingStatus.FILED -> Color(0xFF4338CA)
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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
                    Text(text = "Nominal Pajak Kurang Bayar:", fontSize = 11.sp, color = TextSecondary)
                    Text(
                        text = formatRupiah(filing.totalTax),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Navy900
                    )
                    if (filing.idBilling != null) {
                        Text(
                            text = "ID Billing: ${filing.idBilling}",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Blue600,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Workflow Action Buttons
                when (filing.status) {
                    TaxFilingStatus.DRAFT -> {
                        Button(
                            onClick = onGenerateBilling,
                            colors = ButtonDefaults.buttonColors(containerColor = Blue600),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("Buat ID Billing", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    TaxFilingStatus.BILLED -> {
                        Button(
                            onClick = onPayBilling,
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("Setor via Bank", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    TaxFilingStatus.PAID -> {
                        Button(
                            onClick = onFileCoretax,
                            colors = ButtonDefaults.buttonColors(containerColor = Navy900),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Submit Coretax", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    TaxFilingStatus.FILED -> {
                        OutlinedButton(
                            onClick = onViewBpe,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Lihat BPE", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EFakturCardItem(
    efaktur: EFakturPrePopulated,
    formatRupiah: (Double) -> String
) {
    Surface(
        color = SurfaceCard,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = efaktur.vendorName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                    Text(text = "NSFP: ${efaktur.nsfp}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = TextSecondary)
                }
                Surface(
                    color = if (efaktur.isMatched) Emerald100 else Amber100,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (efaktur.isMatched) "COCOK DENGAN OCR" else "PERLU VERIFIKASI",
                        color = if (efaktur.isMatched) Emerald600 else Amber500,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "DPP: ${formatRupiah(efaktur.dpp)}", fontSize = 11.sp, color = TextSecondary)
                Text(text = "PPN 11%: ${formatRupiah(efaktur.ppn)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Emerald600)
            }
        }
    }
}

@Composable
fun SecurityVaultDetails(activeCompany: Company) {
    Surface(
        color = SurfaceCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Security, contentDescription = null, tint = Emerald600)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = "Vault Kredensial Perpajakan DJP", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                    Text(text = "Enkripsi Hardware Security Module (HSM) / KMS", fontSize = 11.sp, color = TextSecondary)
                }
            }

            HorizontalDivider(color = SurfaceBorder)

            VaultInfoRow("NPWP 16 Digit", activeCompany.npwp16 ?: "0123456789012345")
            VaultInfoRow("NITKU (Nomor Identitas Tempat Kegiatan Usaha)", "000000")
            VaultInfoRow("Sertifikat Elektronik (.p12)", "Tersimpan (Exp: 15 Des 2027)")
            VaultInfoRow("Passphrase Coretax", "••••••••••••")
            VaultInfoRow("Algoritma Kunci", "AES-256-GCM / Envelope Encryption")
            VaultInfoRow("KMS Key Version", "v2.1-prod-jkt")

            Surface(
                color = Emerald100,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Emerald600, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Kredensial didekripsi secara ephemeral in-memory hanya saat signing SPT.",
                        fontSize = 11.sp,
                        color = Color(0xFF064E3B)
                    )
                }
            }
        }
    }
}

@Composable
fun VaultInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextSecondary, fontSize = 12.sp)
        Text(text = value, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
    }
}
