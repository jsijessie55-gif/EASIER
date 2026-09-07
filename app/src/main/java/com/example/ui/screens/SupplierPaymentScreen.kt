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
import com.example.data.model.Invoice
import com.example.data.model.InvoiceStatus
import com.example.data.model.SupplierPayment
import com.example.ui.theme.*
import com.example.ui.viewmodel.AccountingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierPaymentScreen(
    viewModel: AccountingViewModel,
    onBackClick: () -> Unit
) {
    val activeCompany = viewModel.getActiveCompany()
    val invoicesMap by viewModel.invoices.collectAsState()
    val paymentsMap by viewModel.supplierPayments.collectAsState()
    val bankAccountsMap by viewModel.bankAccounts.collectAsState()

    val companyInvoices = invoicesMap[activeCompany.id] ?: emptyList()
    val unpaidInvoices = companyInvoices.filter { it.status == InvoiceStatus.POSTED }
    val paymentHistory = paymentsMap[activeCompany.id] ?: emptyList()
    val companyBanks = bankAccountsMap[activeCompany.id] ?: emptyList()
    val primaryBank = companyBanks.firstOrNull { it.isPrimary } ?: companyBanks.firstOrNull()

    var selectedInvoiceForPayment by remember { mutableStateOf<Invoice?>(null) }
    var show2FaModal by remember { mutableStateOf(false) }

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
                        text = "Pembayaran Supplier",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Navy900
                    )
                    Text(
                        text = "Open Banking SNAP BI / BI-FAST Direct",
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Header Card: SNAP BI Open Banking Protocol Status
            item {
                Surface(
                    color = Navy900,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Blue600.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = Blue100,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "SNAP BI Direct Debit Gateway",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = if (primaryBank != null) {
                                    "Rekening: ${primaryBank.bankName} (${primaryBank.accountNumber}) • Terhubung"
                                } else {
                                    "Belum ada akun bank terhubung"
                                },
                                color = if (primaryBank != null) Emerald400 else Crimson400,
                                fontSize = 11.sp
                            )
                        }

                        Button(
                            onClick = { viewModel.navigateTo("BANK") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Kelola Bank", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 2. Unpaid Invoices Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TAGIHAN UTANG USAHA (ACCOUNTS PAYABLE)",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${unpaidInvoices.size} Tagihan Aktif",
                        color = Crimson500,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (unpaidInvoices.isEmpty()) {
                item {
                    Surface(
                        color = SurfaceCard,
                        shape = RoundedCornerShape(12.dp),
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
                                text = "Tidak Ada Tagihan Jatuh Tempo",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Semua kewajiban utang supplier telah dilunasi.",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                items(unpaidInvoices) { invoice ->
                    UnpaidInvoiceItem(
                        invoice = invoice,
                        formatRupiah = { viewModel.formatRupiah(it) },
                        onPayClick = {
                            selectedInvoiceForPayment = invoice
                            show2FaModal = true
                        }
                    )
                }
            }

            // 3. Payment History Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "RIWAYAT TRANSFER BI-FAST & AUTO-REKONSILIASI",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(paymentHistory) { payment ->
                PaymentHistoryCard(
                    payment = payment,
                    formatRupiah = { viewModel.formatRupiah(it) }
                )
            }
        }
    }

    // 2FA Biometric PIN Confirmation Modal
    if (show2FaModal && selectedInvoiceForPayment != null) {
        val invoice = selectedInvoiceForPayment!!
        var pinValue by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { show2FaModal = false }) {
            Surface(
                color = SurfaceCard,
                shape = RoundedCornerShape(24.dp),
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth(0.92f)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Blue100),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = null,
                            tint = Blue600,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Otorisasi Transfer BI-FAST",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Standar SNAP Bank Indonesia",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Transfer Details Summary Box
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceLight, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Penerima:", fontSize = 12.sp, color = TextSecondary)
                            Text(text = invoice.vendorName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Nominal Tagihan:", fontSize = 12.sp, color = TextSecondary)
                            Text(text = viewModel.formatRupiah(invoice.totalAmount), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Navy900)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Biaya BI-FAST:", fontSize = 12.sp, color = TextSecondary)
                            Text(text = "Rp 2.500", fontSize = 12.sp, color = Emerald600, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 6-Digit PIN Simulation Input
                    OutlinedTextField(
                        value = pinValue,
                        onValueChange = { if (it.length <= 6) pinValue = it },
                        label = { Text("Masukkan PIN Transaksi (6-Digit)") },
                        placeholder = { Text("••••••") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { show2FaModal = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Batal")
                        }
                        Button(
                            onClick = {
                                viewModel.executeSupplierPayment(
                                    invoiceId = invoice.id,
                                    bankName = "Bank Central Asia (BCA)",
                                    accountNumber = "8830192831",
                                    accountHolder = invoice.vendorName
                                )
                                show2FaModal = false
                                selectedInvoiceForPayment = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.4f)
                        ) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Konfirmasi & Bayar", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UnpaidInvoiceItem(
    invoice: Invoice,
    formatRupiah: (Double) -> String,
    onPayClick: () -> Unit
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
                        text = invoice.vendorName,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Inv #${invoice.invoiceNumber} • Jatuh Tempo: ${invoice.dueDate}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
                Surface(
                    color = Crimson100,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "BELUM LUNAS",
                        color = Crimson500,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = SurfaceBorder)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Total Nominal:", color = TextSecondary, fontSize = 11.sp)
                    Text(
                        text = formatRupiah(invoice.totalAmount),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Navy900
                    )
                }

                Button(
                    onClick = onPayClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Blue600),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Bayar BI-FAST", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PaymentHistoryCard(
    payment: SupplierPayment,
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
                    Text(
                        text = payment.vendorName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "${payment.bankName} • ${payment.date}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
                Surface(
                    color = Emerald100,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "SETTLED / LUNAS",
                        color = Emerald600,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ref: ${payment.bankRefNumber}",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextMuted
                )
                Text(
                    text = formatRupiah(payment.amount),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Emerald600
                )
            }
        }
    }
}
