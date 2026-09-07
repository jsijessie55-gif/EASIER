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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Invoice
import com.example.data.model.InvoiceStatus
import com.example.ui.theme.*
import com.example.ui.viewmodel.AccountingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesScreen(
    viewModel: AccountingViewModel,
    onBackClick: () -> Unit,
    onScanOcrClick: () -> Unit = {}
) {
    val activeCompany = viewModel.getActiveCompany()
    val invoicesMap by viewModel.invoices.collectAsState()
    val companyInvoices = invoicesMap[activeCompany.id] ?: emptyList()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(0) } // 0: All, 1: Unpaid, 2: Paid, 3: Overdue

    val filterTitles = listOf("All", "Unpaid", "Paid", "Overdue")

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
                IconButton(onClick = onScanOcrClick) {
                    Icon(
                        imageVector = Icons.Outlined.DocumentScanner,
                        contentDescription = "Scan Faktur OCR",
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Invoices",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )

                    Button(
                        onClick = onScanOcrClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Purple600),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Invoice", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search invoice / client name...", fontSize = 13.sp, color = TextMuted) },
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

            // Status Filter Tabs
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filterTitles.size) { index ->
                        val isSelected = selectedFilter == index
                        Surface(
                            color = if (isSelected) Purple600 else SurfaceCard,
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) Purple600 else SurfaceBorder
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { selectedFilter = index }
                        ) {
                            Text(
                                text = filterTitles[index],
                                color = if (isSelected) Color.White else TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Default Mock Invoices
            item {
                InvoiceCardItem(
                    clientName = "Client A",
                    invoiceNo = "INV-2026-001",
                    date = "Due 25 May 2026",
                    amount = "Rp 25.000.000",
                    status = "Paid",
                    statusColor = Emerald600,
                    statusBg = Color(0xFFDCFCE7)
                )
            }

            item {
                InvoiceCardItem(
                    clientName = "Client B",
                    invoiceNo = "INV-2026-002",
                    date = "Due 28 May 2026",
                    amount = "Rp 15.000.000",
                    status = "Unpaid",
                    statusColor = Amber500,
                    statusBg = Color(0xFFFEF3C7)
                )
            }

            item {
                InvoiceCardItem(
                    clientName = "Client C",
                    invoiceNo = "INV-2026-003",
                    date = "Due 10 May 2026",
                    amount = "Rp 8.750.000",
                    status = "Overdue",
                    statusColor = Crimson500,
                    statusBg = Color(0xFFFEE2E2)
                )
            }

            item {
                InvoiceCardItem(
                    clientName = "Client D",
                    invoiceNo = "INV-2026-004",
                    date = "Due 30 May 2026",
                    amount = "Rp 32.000.000",
                    status = "Paid",
                    statusColor = Emerald600,
                    statusBg = Color(0xFFDCFCE7)
                )
            }

            // Dynamic Repository Invoices
            items(companyInvoices) { inv ->
                InvoiceCardItem(
                    clientName = inv.vendorName,
                    invoiceNo = inv.invoiceNumber,
                    date = "Due ${inv.dueDate}",
                    amount = viewModel.formatRupiah(inv.totalAmount),
                    status = inv.status.label,
                    statusColor = if (inv.status == InvoiceStatus.PAID) Emerald600 else if (inv.status == InvoiceStatus.POSTED) Amber500 else Crimson500,
                    statusBg = if (inv.status == InvoiceStatus.PAID) Color(0xFFDCFCE7) else if (inv.status == InvoiceStatus.POSTED) Color(0xFFFEF3C7) else Color(0xFFFEE2E2)
                )
            }
        }
    }
}

@Composable
fun InvoiceCardItem(
    clientName: String,
    invoiceNo: String,
    date: String,
    amount: String,
    status: String,
    statusColor: Color,
    statusBg: Color
) {
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF3F0FA)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ReceiptLong,
                        contentDescription = null,
                        tint = Purple600,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = clientName,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$invoiceNo • $date",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = amount,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = statusBg,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = status,
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
