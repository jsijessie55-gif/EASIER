package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.RoleType
import com.example.ui.components.AddCompanyDialog
import com.example.ui.components.AppNotificationBanner
import com.example.ui.components.CompanyTopBar
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AccountingViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SmartAccApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartAccApp(
    viewModel: AccountingViewModel = viewModel()
) {
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()
    val companies by viewModel.companies.collectAsState()
    val activeCompany = viewModel.getActiveCompany()
    val currentRole by viewModel.currentRole.collectAsState()
    val notification by viewModel.notification.collectAsState()
    val currentScreen by viewModel.activeScreen.collectAsState()

    var showAddCompanyDialog by remember { mutableStateOf(false) }
    var showQuickActionSheet by remember { mutableStateOf(false) }
    var showMoreMenuSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
            topBar = {
                if (currentScreen == "DASHBOARD") {
                    CompanyTopBar(
                        activeCompany = activeCompany,
                        companies = companies,
                        currentRole = currentRole,
                        userEmail = userEmail,
                        onSwitchCompany = { viewModel.switchCompany(it) },
                        onSwitchRole = { viewModel.switchRole(it) },
                        onAddCompanyClick = { showAddCompanyDialog = true },
                        onLogout = { viewModel.logout() }
                    )
                }
            },
            bottomBar = {
                Surface(
                    color = SurfaceCard,
                    tonalElevation = 8.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp)
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        // 1. Home
                        BottomNavItem(
                            icon = if (currentScreen == "DASHBOARD") Icons.Filled.Home else Icons.Outlined.Home,
                            label = "Home",
                            isSelected = currentScreen == "DASHBOARD",
                            onClick = { viewModel.navigateTo("DASHBOARD") }
                        )

                        // 2. Transactions
                        BottomNavItem(
                            icon = if (currentScreen == "TRANSACTIONS") Icons.Filled.SwapHoriz else Icons.Outlined.SwapHoriz,
                            label = "Transactions",
                            isSelected = currentScreen == "TRANSACTIONS",
                            onClick = { viewModel.navigateTo("TRANSACTIONS") }
                        )

                        // 3. Center Elevated FAB (+)
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Purple600, Purple800)
                                    )
                                )
                                .clickable { showQuickActionSheet = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Quick Actions",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        // 4. Reports
                        BottomNavItem(
                            icon = if (currentScreen == "REPORTS" || currentScreen == "CASHFLOW") Icons.Filled.PieChart else Icons.Outlined.PieChart,
                            label = "Reports",
                            isSelected = currentScreen == "REPORTS" || currentScreen == "CASHFLOW",
                            onClick = { viewModel.navigateTo("REPORTS") }
                        )

                        // 5. More
                        BottomNavItem(
                            icon = if (currentScreen in listOf("INVOICES", "EXPENSES", "ACCOUNTS", "CUSTOMERS", "SETTINGS", "PROFILE", "CORETAX", "PAYMENT")) Icons.Filled.MoreHoriz else Icons.Outlined.MoreHoriz,
                            label = "More",
                            isSelected = currentScreen in listOf("INVOICES", "EXPENSES", "ACCOUNTS", "CUSTOMERS", "SETTINGS", "PROFILE", "CORETAX", "PAYMENT"),
                            onClick = { showMoreMenuSheet = true }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Main Screen Routing
                when (currentScreen) {
                    "DASHBOARD" -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                    "TRANSACTIONS" -> TransactionsScreen(
                        viewModel = viewModel,
                        onBackClick = { viewModel.navigateTo("DASHBOARD") }
                    )
                    "INVOICES" -> InvoicesScreen(
                        viewModel = viewModel,
                        onBackClick = { viewModel.navigateTo("DASHBOARD") },
                        onScanOcrClick = { viewModel.navigateTo("OCR") }
                    )
                    "EXPENSES" -> ExpensesScreen(
                        viewModel = viewModel,
                        onBackClick = { viewModel.navigateTo("DASHBOARD") },
                        onAddExpenseClick = { viewModel.navigateTo("OCR") }
                    )
                    "CASHFLOW" -> CashFlowDetailScreen(
                        viewModel = viewModel,
                        onBackClick = { viewModel.navigateTo("DASHBOARD") }
                    )
                    "ACCOUNTS" -> AccountsCoaScreen(
                        viewModel = viewModel,
                        onBackClick = { viewModel.navigateTo("DASHBOARD") }
                    )
                    "CUSTOMERS" -> CustomersScreen(
                        viewModel = viewModel,
                        onBackClick = { viewModel.navigateTo("DASHBOARD") }
                    )
                    "SETTINGS" -> SettingsScreen(
                        viewModel = viewModel,
                        onBackClick = { viewModel.navigateTo("DASHBOARD") },
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                    "PROFILE" -> ProfileScreen(
                        viewModel = viewModel,
                        onBackClick = { viewModel.navigateTo("DASHBOARD") },
                        onLogout = { viewModel.logout() }
                    )
                    "OCR" -> OcrScannerScreen(
                        viewModel = viewModel,
                        onBackClick = { viewModel.navigateTo("DASHBOARD") }
                    )
                    "PAYMENT" -> SupplierPaymentScreen(
                        viewModel = viewModel,
                        onBackClick = { viewModel.navigateTo("DASHBOARD") }
                    )
                    "CORETAX" -> CoretaxHubScreen(
                        viewModel = viewModel,
                        onBackClick = { viewModel.navigateTo("DASHBOARD") }
                    )
                    "REPORTS" -> FinancialReportsScreen(
                        viewModel = viewModel,
                        onBackClick = { viewModel.navigateTo("DASHBOARD") }
                    )
                    "BANK" -> BankAccountsScreen(
                        viewModel = viewModel,
                        onBackClick = { viewModel.navigateTo("DASHBOARD") }
                    )
                }

                // Notification Banner Overlay
                AnimatedVisibility(
                    visible = notification != null,
                    enter = slideInVertically() + fadeIn(),
                    exit = slideOutVertically() + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                ) {
                    notification?.let { notif ->
                        AppNotificationBanner(
                            title = notif.title,
                            message = notif.message,
                            isSuccess = notif.isSuccess,
                            onDismiss = { viewModel.clearNotification() }
                        )
                    }
                }
            }
        }

    // Quick Action Center Sheet (+)
    if (showQuickActionSheet) {
        ModalBottomSheet(
            onDismissRequest = { showQuickActionSheet = false },
            containerColor = SurfaceCard
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Quick Actions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    QuickSheetItem(
                        title = "Scan OCR",
                        subtitle = "Faktur & Struk",
                        icon = Icons.Outlined.DocumentScanner,
                        color = Purple600,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            showQuickActionSheet = false
                            viewModel.navigateTo("OCR")
                        }
                    )

                    QuickSheetItem(
                        title = "New Invoice",
                        subtitle = "Buat Tagihan",
                        icon = Icons.Outlined.ReceiptLong,
                        color = Emerald600,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            showQuickActionSheet = false
                            viewModel.navigateTo("INVOICES")
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    QuickSheetItem(
                        title = "Bayar Supplier",
                        subtitle = "SNAP BI-FAST",
                        icon = Icons.Outlined.Payments,
                        color = Blue600,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            showQuickActionSheet = false
                            viewModel.navigateTo("PAYMENT")
                        }
                    )

                    QuickSheetItem(
                        title = "Coretax Hub",
                        subtitle = "Faktur Pajak PPN",
                        icon = Icons.Outlined.AccountBalance,
                        color = Amber500,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            showQuickActionSheet = false
                            viewModel.navigateTo("CORETAX")
                        }
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // More Menu Sheet
    if (showMoreMenuSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMoreMenuSheet = false },
            containerColor = SurfaceCard
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "All Modules & Features",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickSheetItem(
                        title = "Invoices",
                        subtitle = "Penjualan & Piutang",
                        icon = Icons.Outlined.ReceiptLong,
                        color = Purple600,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            showMoreMenuSheet = false
                            viewModel.navigateTo("INVOICES")
                        }
                    )
                    QuickSheetItem(
                        title = "Expenses",
                        subtitle = "Beban & Biaya",
                        icon = Icons.Outlined.AccountBalanceWallet,
                        color = Crimson500,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            showMoreMenuSheet = false
                            viewModel.navigateTo("EXPENSES")
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickSheetItem(
                        title = "Akun Bank",
                        subtitle = "Debit & Kredit Rekening",
                        icon = Icons.Outlined.AccountBalance,
                        color = Blue600,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            showMoreMenuSheet = false
                            viewModel.navigateTo("BANK")
                        }
                    )
                    QuickSheetItem(
                        title = "Laporan Lengkap",
                        subtitle = "Arus Kas & Persediaan",
                        icon = Icons.Outlined.BarChart,
                        color = Emerald600,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            showMoreMenuSheet = false
                            viewModel.navigateTo("REPORTS")
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickSheetItem(
                        title = "Accounts CoA",
                        subtitle = "Bagan Akun (PSAK)",
                        icon = Icons.Outlined.AccountTree,
                        color = Purple700,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            showMoreMenuSheet = false
                            viewModel.navigateTo("ACCOUNTS")
                        }
                    )
                    QuickSheetItem(
                        title = "Customers",
                        subtitle = "Daftar Klien",
                        icon = Icons.Outlined.People,
                        color = Blue600,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            showMoreMenuSheet = false
                            viewModel.navigateTo("CUSTOMERS")
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickSheetItem(
                        title = "Settings",
                        subtitle = "Konfigurasi Usaha",
                        icon = Icons.Outlined.Settings,
                        color = TextSecondary,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            showMoreMenuSheet = false
                            viewModel.navigateTo("SETTINGS")
                        }
                    )
                    QuickSheetItem(
                        title = "Profile",
                        subtitle = "Akun Pengguna",
                        icon = Icons.Outlined.Person,
                        color = Purple600,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            showMoreMenuSheet = false
                            viewModel.navigateTo("PROFILE")
                        }
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // Modal Onboarding Tambah Perusahaan Baru
    if (showAddCompanyDialog) {
        AddCompanyDialog(
            onDismiss = { showAddCompanyDialog = false },
            onSubmit = { name, entityType, industryType, npwp, address ->
                viewModel.addCompany(name, entityType, industryType, npwp, address)
            }
        )
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Purple600 else TextMuted,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            color = if (isSelected) Purple600 else TextMuted,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun QuickSheetItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = Color(0xFFF9FAFB),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}
