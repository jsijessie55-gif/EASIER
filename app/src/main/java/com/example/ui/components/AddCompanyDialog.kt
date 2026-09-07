package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.EntityType
import com.example.data.model.IndustryType
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCompanyDialog(
    onDismiss: () -> Unit,
    onSubmit: (name: String, entityType: EntityType, industryType: IndustryType, npwp16: String?, address: String) -> Unit
) {
    var companyName by remember { mutableStateOf("") }
    var selectedEntityType by remember { mutableStateOf(EntityType.PT) }
    var selectedIndustryType by remember { mutableStateOf(IndustryType.TRADING) }
    var npwpInput by remember { mutableStateOf("") }
    var addressInput by remember { mutableStateOf("") }

    val cleanNpwp = npwpInput.filter { it.isDigit() }
    val isPkpDetected = cleanNpwp.length >= 15

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f),
            shape = RoundedCornerShape(24.dp),
            color = SurfaceCard,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
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
                                .background(Emerald100),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddBusiness,
                                contentDescription = null,
                                tint = Emerald600
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Setup Perusahaan Baru",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Onboarding Mandiri SaaS & Multi-Tenant",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = SurfaceBorder)

                // Scrollable Form Fields
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 1. Nama Perusahaan
                    OutlinedTextField(
                        value = companyName,
                        onValueChange = { companyName = it },
                        label = { Text("Nama Lengkap Entitas / Perusahaan") },
                        placeholder = { Text("contoh: PT Sinergi Cipta Mandiri") },
                        leadingIcon = { Icon(Icons.Outlined.Business, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // 2. Bentuk Badan Hukum (EntityType)
                    Text(
                        text = "Bentuk Badan Usaha",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(EntityType.PT, EntityType.CV, EntityType.UD).forEach { type ->
                            val isSelected = selectedEntityType == type
                            Surface(
                                color = if (isSelected) Emerald600 else SurfaceLight,
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) Emerald600 else SurfaceBorder
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedEntityType = type }
                            ) {
                                Text(
                                    text = type.name,
                                    color = if (isSelected) Color.White else TextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp,
                                    modifier = Modifier
                                        .padding(vertical = 10.dp)
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    }

                    // 3. Jenis Industri (Menentukan Template Standar PSAK)
                    Text(
                        text = "Jenis Industri & Template PSAK",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        IndustryType.values().forEach { industry ->
                            val isSelected = selectedIndustryType == industry
                            Surface(
                                color = if (isSelected) Emerald100.copy(alpha = 0.5f) else SurfaceLight,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.5.dp,
                                    if (isSelected) Emerald600 else SurfaceBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedIndustryType = industry }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedIndustryType = industry },
                                        colors = RadioButtonDefaults.colors(selectedColor = Emerald600)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = industry.label,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextPrimary,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = when(industry) {
                                                IndustryType.SERVICES -> "Bagan Akun PSAK: Beban Proyek & Jasa Konsultan"
                                                IndustryType.TRADING -> "Bagan Akun PSAK: Persediaan Perpetual, HPP, & Piutang"
                                                IndustryType.MANUFACTURING -> "Bagan Akun PSAK: Raw Material, WIP, Finished Goods, BOP"
                                            },
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 4. Input NPWP 16 Digit & Dynamic PKP Classification
                    Column {
                        OutlinedTextField(
                            value = npwpInput,
                            onValueChange = { npwpInput = it },
                            label = { Text("NPWP 16 Digit (Opsional)") },
                            placeholder = { Text("contoh: 0123456789012345") },
                            leadingIcon = { Icon(Icons.Outlined.ReceiptLong, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        // Dynamic Tax Rule Badge
                        Surface(
                            color = if (isPkpDetected) Emerald100 else Amber100,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isPkpDetected) Icons.Default.CheckCircle else Icons.Default.Info,
                                    contentDescription = null,
                                    tint = if (isPkpDetected) Emerald600 else Amber500,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isPkpDetected)
                                        "Status Pajak: PKP Aktif. Mengaktifkan modul PPN 11%, e-Faktur Coretax, dan SPT Masa PPN 1111."
                                    else
                                        "Status Pajak: Non-PKP. Pelaporan disederhanakan (PPh Final UMKM 0.5% PP 55/2022).",
                                    color = if (isPkpDetected) Color(0xFF064E3B) else Color(0xFF78350F),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    // 5. Alamat Domisili
                    OutlinedTextField(
                        value = addressInput,
                        onValueChange = { addressInput = it },
                        label = { Text("Alamat Kantor / Operasional") },
                        placeholder = { Text("contoh: Jl. Sudirman No. 45, Jakarta Selatan") },
                        leadingIcon = { Icon(Icons.Outlined.LocationOn, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (companyName.isNotBlank()) {
                            onSubmit(
                                companyName.trim(),
                                selectedEntityType,
                                selectedIndustryType,
                                npwpInput.takeIf { it.isNotBlank() },
                                addressInput.ifBlank { "Indonesia" }
                            )
                            onDismiss()
                        }
                    },
                    enabled = companyName.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Inisialisasi & Buat Perusahaan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
