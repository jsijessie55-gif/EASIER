package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AccountingViewModel

data class CustomerItem(
    val name: String,
    val contact: String,
    val outstanding: String,
    val invoiceCount: Int,
    val status: String,
    val avatarColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(
    viewModel: AccountingViewModel,
    onBackClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val customers = listOf(
        CustomerItem("Client A (PT Mega Retail)", "clienta@megaretail.co.id", "Rp 0 (Lunas)", 4, "Active", Purple600),
        CustomerItem("Client B (CV Agro Mandiri)", "finance@agromandiri.com", "Rp 15.000.000", 2, "Pending", Emerald600),
        CustomerItem("Client C (PT Sinar Logistik)", "accounts@sinarlogistik.id", "Rp 8.750.000", 1, "Overdue", Crimson500),
        CustomerItem("Client D (PT Digital Nusantara)", "billing@digitalnusantara.com", "Rp 0 (Lunas)", 5, "Active", Blue600),
        CustomerItem("Client E (CV Berkah Pangan)", "owner@berkahpangan.co.id", "Rp 4.200.000", 2, "Active", Amber500)
    )

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
                IconButton(onClick = { /* Add Customer */ }) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = "Tambah Klien",
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
                    text = "Customers & Clients",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
            }

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search client name or email...", fontSize = 13.sp, color = TextMuted) },
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

            items(customers.filter { it.name.contains(searchQuery, ignoreCase = true) || it.contact.contains(searchQuery, ignoreCase = true) }) { customer ->
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
                                    .clip(CircleShape)
                                    .background(customer.avatarColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = customer.name.take(1),
                                    color = customer.avatarColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = customer.name,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = customer.contact,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "${customer.invoiceCount} invoices",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = customer.outstanding,
                                color = if (customer.outstanding.contains("Rp 0")) Emerald600 else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
