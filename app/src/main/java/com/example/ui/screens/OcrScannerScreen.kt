package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.Invoice
import com.example.ui.theme.*
import com.example.ui.viewmodel.AccountingViewModel

data class SampleInvoicePreset(
    val vendorName: String,
    val vendorNpwp: String,
    val invoiceNumber: String,
    val date: String,
    val subtotal: Double,
    val isPkp: Boolean,
    val categoryCode: String,
    val categoryName: String,
    val description: String,
    val fileType: String = "JPEG", // "PDF" or "JPEG"
    val fileName: String = "invoice_scan.jpg",
    val fileSize: String = "1.2 MB",
    val isTestDuplicate: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcrScannerScreen(
    viewModel: AccountingViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val activeCompany = viewModel.getActiveCompany()
    val isScanning by viewModel.isScanning.collectAsState()

    // 0: Live Camera Scanner (HP/Laptop), 1: Upload File Local Storage (HP/Laptop)
    var inputMode by remember { mutableStateOf(0) }

    // Camera Permission State
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    // Selected Local File Storage state
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedFileUri = uri
            val name = uri.lastPathSegment?.substringAfterLast("/") ?: "dokumen_invoice.pdf"
            selectedFileName = name
        }
    }

    // Invoices Presets (including a pre-existing duplicate invoice to test anti-double scanning)
    val presets = remember(activeCompany.id) {
        listOf(
            SampleInvoicePreset(
                vendorName = "PT Krakatau Steel Tbk",
                vendorNpwp = "01.000.123.4-001.000",
                invoiceNumber = "INV/KS/2026/089",
                date = "26 Agu 2026",
                subtotal = 45000000.0,
                isPkp = true,
                categoryCode = if (activeCompany.industryType == com.example.data.model.IndustryType.MANUFACTURING) "11310" else "11300",
                categoryName = if (activeCompany.industryType == com.example.data.model.IndustryType.MANUFACTURING) "Persediaan Bahan Baku" else "Persediaan Barang Dagangan",
                description = "Baja Lembaran Dingin (Cold Rolled Coil) SPCC-SD 2.0mm",
                fileType = "PDF",
                fileName = "Faktur_Krakatau_Steel_2026.pdf",
                fileSize = "2.4 MB"
            ),
            SampleInvoicePreset(
                vendorName = "PT Telkom Data Ekosistem",
                vendorNpwp = "01.234.567.8-091.000",
                invoiceNumber = "INV/2026/08/9921", // This is already in company 1 (Test Duplicate!)
                date = "24 Agu 2026",
                subtotal = 15000000.0,
                isPkp = true,
                categoryCode = "61200",
                categoryName = "Beban Server & Cloud Hosting",
                description = "Layanan Cloud Server & Colocation Data Center (DUPLIKAT)",
                fileType = "JPEG",
                fileName = "Scan_Telkom_Cloud_9921.jpg",
                fileSize = "1.6 MB",
                isTestDuplicate = true
            ),
            SampleInvoicePreset(
                vendorName = "PT Mega Rental Solusi",
                vendorNpwp = "02.998.877.6-012.000",
                invoiceNumber = "INV/MRS/8812",
                date = "25 Agu 2026",
                subtotal = 8000000.0,
                isPkp = true,
                categoryCode = "61400",
                categoryName = "Beban Sewa Kantor & Utilitas",
                description = "Sewa Ruang Kantor Operasional Periode September 2026",
                fileType = "PDF",
                fileName = "Invoice_Sewa_Kantor_SCBD.pdf",
                fileSize = "940 KB"
            )
        )
    }

    var selectedPreset by remember { mutableStateOf(presets[0]) }

    // Custom Document Input Dialog
    var showCustomInputDialog by remember { mutableStateOf(false) }
    var customVendorName by remember { mutableStateOf("") }
    var customInvoiceNumber by remember { mutableStateOf("") }
    var customSubtotalText by remember { mutableStateOf("") }
    var customDescription by remember { mutableStateOf("") }

    // Check if the current document is already in system (Anti Double-Scanning detection)
    val duplicateInvoice: Invoice? = remember(selectedPreset, activeCompany.id) {
        viewModel.checkDuplicateInvoice(selectedPreset.invoiceNumber, selectedPreset.vendorName)
    }
    val isDuplicateDetected = duplicateInvoice != null

    // Laser Animation for Scanner
    val infiniteTransition = rememberInfiniteTransition(label = "laser_transition")
    val laserPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_pos"
    )

    val ppnAmount = if (activeCompany.isPkp && selectedPreset.isPkp) selectedPreset.subtotal * 0.11 else 0.0
    val totalAmount = selectedPreset.subtotal + ppnAmount

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
                        text = "Smart Invoice Ingestion",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Navy900
                    )
                    Text(
                        text = "Kamera OCR & Penyimpanan HP/Laptop • Anti Double-Scan",
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

        // Mode Switcher: Camera OCR vs Upload File
        TabRow(
            selectedTabIndex = inputMode,
            containerColor = SurfaceCard,
            contentColor = Emerald600
        ) {
            Tab(
                selected = inputMode == 0,
                onClick = { inputMode = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Kamera HP/Laptop", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
            Tab(
                selected = inputMode == 1,
                onClick = { inputMode = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Penyimpanan File", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ==================== PERMISSION STATUS BANNER ====================
            if (inputMode == 0) {
                Surface(
                    color = if (hasCameraPermission) Emerald100 else Amber100,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (hasCameraPermission) Emerald400 else Amber400),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (hasCameraPermission) Icons.Default.Videocam else Icons.Default.VideocamOff,
                                contentDescription = null,
                                tint = if (hasCameraPermission) Emerald600 else Amber500,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (hasCameraPermission) "Izin Kamera Aktif" else "Izin Kamera Belum Diberikan",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (hasCameraPermission) Color(0xFF064E3B) else Color(0xFF78350F)
                                )
                                Text(
                                    text = if (hasCameraPermission) "Kamera HP/Laptop siap memindai struk fisik" else "Ketuk untuk memberikan izin akses kamera",
                                    fontSize = 11.sp,
                                    color = if (hasCameraPermission) Color(0xFF064E3B) else Color(0xFF78350F)
                                )
                            }
                        }

                        if (!hasCameraPermission) {
                            Button(
                                onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                                colors = ButtonDefaults.buttonColors(containerColor = Amber500),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Beri Izin", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // ==================== DOUBLE SCANNING WARNING ALERT ====================
            AnimatedVisibility(
                visible = isDuplicateDetected,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    color = Crimson100,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Crimson500),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Peringatan Duplikat",
                                tint = Crimson500,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "DOKUMEN DITOLAK (DOUBLE SCANNING)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Crimson500
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Faktur #${selectedPreset.invoiceNumber} dari ${selectedPreset.vendorName} sudah pernah tercatat pada sistem (${duplicateInvoice?.invoiceDate ?: "sebelumnya"}). Sistem secara otomatis menolak pemindaian ganda untuk menjaga integritas pembukuan.",
                            fontSize = 12.sp,
                            color = Color(0xFF7F1D1D),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            if (inputMode == 0) {
                // ==================== MODE 0: LIVE CAMERA OCR VIEWFINDER ====================
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Navy900,
                    border = androidx.compose.foundation.BorderStroke(
                        2.dp,
                        if (isDuplicateDetected) Crimson500 else if (isScanning) Emerald400 else Navy700
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Document Mock in Viewfinder
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(18.dp)
                                .background(Navy800.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = selectedPreset.vendorName,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "NPWP: ${selectedPreset.vendorNpwp}",
                                        color = TextMuted,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Surface(
                                    color = if (isDuplicateDetected) Crimson500.copy(alpha = 0.2f) else Emerald500.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = if (isDuplicateDetected) "DUPLIKAT" else "FAKTUR VALID",
                                        color = if (isDuplicateDetected) Crimson100 else Emerald400,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }

                            // Line item inside viewfinder
                            Column {
                                Text(
                                    text = selectedPreset.description,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "DPP: ${viewModel.formatRupiah(selectedPreset.subtotal)}",
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        text = "PPN 11%: ${viewModel.formatRupiah(ppnAmount)}",
                                        color = Emerald400,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "TOTAL: ${viewModel.formatRupiah(totalAmount)}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = selectedPreset.invoiceNumber,
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Scanning Laser Line
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .align(Alignment.TopCenter)
                                .offset(y = (230 * laserPosition).dp)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = if (isDuplicateDetected) listOf(Color.Transparent, Crimson500, Crimson500, Color.Transparent)
                                        else listOf(Color.Transparent, Emerald400, Emerald500, Color.Transparent)
                                    )
                                )
                        )

                        // Bounding Box Indicators on Corners
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .align(Alignment.TopStart)
                                .offset(x = 12.dp, y = 12.dp)
                                .border(2.dp, if (isDuplicateDetected) Crimson500 else Emerald400, RoundedCornerShape(topStart = 6.dp))
                        )
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = (-12).dp, y = 12.dp)
                                .border(2.dp, if (isDuplicateDetected) Crimson500 else Emerald400, RoundedCornerShape(topEnd = 6.dp))
                        )
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .align(Alignment.BottomStart)
                                .offset(x = 12.dp, y = (-12).dp)
                                .border(2.dp, if (isDuplicateDetected) Crimson500 else Emerald400, RoundedCornerShape(bottomStart = 6.dp))
                        )
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .align(Alignment.BottomEnd)
                                .offset(x = (-12).dp, y = (-12).dp)
                                .border(2.dp, if (isDuplicateDetected) Crimson500 else Emerald400, RoundedCornerShape(bottomEnd = 6.dp))
                        )

                        // Confidence Score Pill
                        Surface(
                            color = Navy900.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = (-8).dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isDuplicateDetected) Crimson500 else Emerald500)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isDuplicateDetected) "Status: Duplikat Ditolak" else "Spatial Vision OCR: 99.1% Akurasi",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            } else {
                // ==================== MODE 1: UPLOAD DARI PENYIMPANAN HP/LAPTOP ====================
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .clickable { filePickerLauncher.launch("*/*") },
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFF1F5F9),
                    border = androidx.compose.foundation.BorderStroke(
                        2.dp,
                        if (isDuplicateDetected) Crimson500 else Blue600.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(if (isDuplicateDetected) Crimson100 else Blue100),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (selectedFileName != null) Icons.Default.CheckCircle else Icons.Default.FolderSpecial,
                                contentDescription = null,
                                tint = if (isDuplicateDetected) Crimson500 else Blue600,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = selectedFileName ?: selectedPreset.fileName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Navy900
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = if (selectedPreset.fileType == "PDF") Crimson100 else Blue100,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = selectedPreset.fileType,
                                    color = if (selectedPreset.fileType == "PDF") Crimson500 else Blue600,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "${selectedPreset.fileSize} • Dari Penyimpanan Lokal",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { filePickerLauncher.launch("*/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = Blue600),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Buka Penyimpanan HP / Laptop", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 2. Preset Invoices Selector (Pilih Dokumen Uji Coba)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DOKUMEN SAMPEL & UJI DUPLIKASI",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(
                    onClick = { showCustomInputDialog = true },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = Emerald600)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Input Manual", fontSize = 11.sp, color = Emerald600, fontWeight = FontWeight.Bold)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEachIndexed { index, preset ->
                    val isSelected = selectedPreset == preset
                    Surface(
                        color = if (isSelected) {
                            if (preset.isTestDuplicate) Crimson100 else (if (inputMode == 0) Emerald100 else Blue100)
                        } else SurfaceCard,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) {
                                if (preset.isTestDuplicate) Crimson500 else (if (inputMode == 0) Emerald600 else Blue600)
                            } else SurfaceBorder
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedPreset = preset }
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "#${index + 1}",
                                    color = if (isSelected) (if (preset.isTestDuplicate) Crimson500 else Emerald600) else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (preset.isTestDuplicate) {
                                    Surface(
                                        color = Crimson500,
                                        shape = RoundedCornerShape(3.dp)
                                    ) {
                                        Text(
                                            text = "DUPLIKAT",
                                            color = Color.White,
                                            fontSize = 7.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                                        )
                                    }
                                } else {
                                    Surface(
                                        color = if (preset.fileType == "PDF") Crimson100 else Blue100,
                                        shape = RoundedCornerShape(3.dp)
                                    ) {
                                        Text(
                                            text = preset.fileType,
                                            color = if (preset.fileType == "PDF") Crimson500 else Blue600,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = preset.vendorName.replace("PT ", ""),
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // 3. Extracted Accounting Rule & Double-Entry Balancing Card
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
                        Text(
                            text = "Draf Jurnal Otomatis (PSAK)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Surface(
                            color = if (isDuplicateDetected) Crimson100 else Emerald100,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (isDuplicateDetected) "STATUS: DITOLAK" else "SEIMBANG (D = K)",
                                color = if (isDuplicateDetected) Crimson500 else Color(0xFF064E3B),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Debit Line 1: Expense / Inventory (DPP)
                    JournalRowPreview(
                        accountCode = selectedPreset.categoryCode,
                        accountName = selectedPreset.categoryName,
                        debit = selectedPreset.subtotal,
                        credit = 0.0,
                        formatRupiah = { viewModel.formatRupiah(it) }
                    )

                    // Debit Line 2: PPN Masukan (if PKP)
                    if (ppnAmount > 0) {
                        Spacer(modifier = Modifier.height(6.dp))
                        JournalRowPreview(
                            accountCode = "11510",
                            accountName = "PPN Masukan (11%)",
                            debit = ppnAmount,
                            credit = 0.0,
                            formatRupiah = { viewModel.formatRupiah(it) }
                        )
                    }

                    // Credit Line 3: Utang Usaha (Total)
                    Spacer(modifier = Modifier.height(6.dp))
                    JournalRowPreview(
                        accountCode = "21100",
                        accountName = "Utang Usaha (${selectedPreset.vendorName})",
                        debit = 0.0,
                        credit = totalAmount,
                        formatRupiah = { viewModel.formatRupiah(it) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = SurfaceBorder)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total Debit:", fontSize = 12.sp, color = TextSecondary)
                        Text(text = viewModel.formatRupiah(totalAmount), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Navy900)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total Kredit:", fontSize = 12.sp, color = TextSecondary)
                        Text(text = viewModel.formatRupiah(totalAmount), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Navy900)
                    }
                }
            }

            // 4. Action Button: Capture & Process to Ledger with Duplicate Rejection
            Button(
                onClick = {
                    if (isDuplicateDetected) {
                        // Double scanning rejected immediately
                        return@Button
                    }
                    viewModel.simulateOcrScan(
                        vendorName = selectedPreset.vendorName,
                        vendorNpwp = selectedPreset.vendorNpwp,
                        invoiceNumber = selectedPreset.invoiceNumber,
                        date = selectedPreset.date,
                        subtotal = selectedPreset.subtotal,
                        isPkp = selectedPreset.isPkp,
                        categoryCode = selectedPreset.categoryCode,
                        categoryName = selectedPreset.categoryName,
                        description = selectedPreset.description,
                        onComplete = { success ->
                            if (success) {
                                onBackClick()
                            }
                        }
                    )
                },
                enabled = !isScanning && !isDuplicateDetected,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDuplicateDetected) Crimson500 else if (inputMode == 0) Emerald600 else Blue600,
                    disabledContainerColor = if (isDuplicateDetected) Crimson500.copy(alpha = 0.5f) else TextMuted
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (isScanning) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = if (inputMode == 0) "Memproses OCR Kamera..." else "Mengekstrak Dokumen...")
                } else if (isDuplicateDetected) {
                    Icon(imageVector = Icons.Default.Block, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ditolak: Terdeteksi Double Scanning",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                } else {
                    Icon(
                        imageVector = if (inputMode == 0) Icons.Default.CameraAlt else Icons.Default.FileUpload,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (inputMode == 0) "Ekstraksi Kamera & Simpan Draf" else "Upload Berkas & Simpan Draf",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }

    // Modal Input Custom Dokumen
    if (showCustomInputDialog) {
        AlertDialog(
            onDismissRequest = { showCustomInputDialog = false },
            title = {
                Text(
                    text = "Input / Uji Dokumen Kustom",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Navy900
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Masukkan nomor invoice dan nama vendor untuk menguji deteksi scan ganda:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    OutlinedTextField(
                        value = customVendorName,
                        onValueChange = { customVendorName = it },
                        label = { Text("Nama Vendor") },
                        placeholder = { Text("PT Contoh Sukses") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = customInvoiceNumber,
                        onValueChange = { customInvoiceNumber = it },
                        label = { Text("Nomor Invoice") },
                        placeholder = { Text("INV/2026/099") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = customSubtotalText,
                        onValueChange = { customSubtotalText = it },
                        label = { Text("Nominal DPP (Rp)") },
                        placeholder = { Text("5000000") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = customDescription,
                        onValueChange = { customDescription = it },
                        label = { Text("Deskripsi Barang / Jasa") },
                        placeholder = { Text("Pengadaan Perlengkapan Operasional") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val subtotal = customSubtotalText.toDoubleOrNull() ?: 5000000.0
                        selectedPreset = SampleInvoicePreset(
                            vendorName = if (customVendorName.isNotBlank()) customVendorName else "PT Vendor Custom",
                            vendorNpwp = "01.999.888.7-001.000",
                            invoiceNumber = if (customInvoiceNumber.isNotBlank()) customInvoiceNumber else "INV/CUST/${System.currentTimeMillis().toString().takeLast(4)}",
                            date = "26 Agu 2026",
                            subtotal = subtotal,
                            isPkp = true,
                            categoryCode = "61200",
                            categoryName = "Beban Operasional",
                            description = if (customDescription.isNotBlank()) customDescription else "Pengadaan Operasional",
                            fileType = "PDF",
                            fileName = "Dokumen_Custom.pdf",
                            fileSize = "1.1 MB"
                        )
                        showCustomInputDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald600)
                ) {
                    Text("Terapkan ke Scanner", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomInputDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun JournalRowPreview(
    accountCode: String,
    accountName: String,
    debit: Double,
    credit: Double,
    formatRupiah: (Double) -> String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceLight, RoundedCornerShape(8.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${if (credit > 0) "   " else ""}$accountCode - $accountName",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                maxLines = 1
            )
            Text(
                text = if (debit > 0) "Posisi: DEBIT" else "Posisi: KREDIT",
                fontSize = 10.sp,
                color = if (debit > 0) Emerald600 else TextSecondary
            )
        }
        Text(
            text = if (debit > 0) formatRupiah(debit) else formatRupiah(credit),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Navy900
        )
    }
}
