package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class AccountingRepository {

    private val indonesianLocale = Locale("id", "ID")
    private val currencyFormatter = NumberFormat.getCurrencyInstance(indonesianLocale).apply {
        maximumFractionDigits = 0
    }

    fun formatRupiah(amount: Double): String {
        return currencyFormatter.format(amount).replace("Rp", "Rp ")
    }

    fun getCurrentFormattedDate(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", indonesianLocale)
        return sdf.format(Date())
    }

    // Default Companies
    private val defaultCompanies = listOf(
        Company(
            id = "comp_1",
            name = "PT Sinergi Abadi Digital",
            entityType = EntityType.PT,
            industryType = IndustryType.TRADING,
            npwp16 = "0123456789012345",
            isPkp = true,
            address = "Gedung Cyber 2 Lt. 18, Jl. HR Rasuna Said, Jakarta Selatan",
            baseCurrency = "IDR"
        ),
        Company(
            id = "comp_2",
            name = "CV Berkah Jaya Abadi",
            entityType = EntityType.CV,
            industryType = IndustryType.SERVICES,
            npwp16 = null, // Non-PKP
            isPkp = false,
            address = "Ruko Grand Galaxy City Blok RGA No. 12, Bekasi",
            baseCurrency = "IDR"
        ),
        Company(
            id = "comp_3",
            name = "PT Nusantara Agro Industri",
            entityType = EntityType.PT,
            industryType = IndustryType.MANUFACTURING,
            npwp16 = "0987654321098765",
            isPkp = true,
            address = "Kawasan Industri KIIC Lot C-4, Karawang Barat",
            baseCurrency = "IDR"
        )
    )

    private val _companies = MutableStateFlow<List<Company>>(defaultCompanies)
    val companies: StateFlow<List<Company>> = _companies.asStateFlow()

    private val _activeCompanyId = MutableStateFlow("comp_1")
    val activeCompanyId: StateFlow<String> = _activeCompanyId.asStateFlow()

    private val _userEmail = MutableStateFlow<String?>("pteduworksasiaagroindustries@gmail.com")
    val userEmail: StateFlow<String?> = _userEmail.asStateFlow()

    private val _isAuthenticated = MutableStateFlow<Boolean>(true)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _currentRole = MutableStateFlow(RoleType.OWNER)
    val currentRole: StateFlow<RoleType> = _currentRole.asStateFlow()

    // Access Passes requested by users
    private val _userAccessPasses = MutableStateFlow<List<UserAccessPass>>(
        listOf(
            UserAccessPass(
                id = "pass_01",
                name = "Andi Pratama",
                email = "andi.staff@sinergiabadi.co.id",
                requestedRole = RoleType.STAFF,
                approverEmail = "pteduworksasiaagroindustries@gmail.com",
                approverType = "Owner / Super Admin",
                passCode = "PASS-STF-8821",
                status = PassStatus.APPROVED,
                requestDate = "24 Agu 2026",
                approvedDate = "24 Agu 2026 10:15 WIB",
                notes = "Otorisasi Staff Keuangan untuk operasional harian"
            ),
            UserAccessPass(
                id = "pass_02",
                name = "Maya Kartika",
                email = "maya.finance@sinergiabadi.co.id",
                requestedRole = RoleType.STAFF,
                approverEmail = "pteduworksasiaagroindustries@gmail.com",
                approverType = "Owner / Super Admin",
                passCode = null,
                status = PassStatus.PENDING,
                requestDate = "26 Agu 2026",
                notes = "Permintaan akses input bukti transaksi dan kas keluar"
            ),
            UserAccessPass(
                id = "pass_03",
                name = "KAP Haryanto & Rekan",
                email = "audit@kapharyanto.id",
                requestedRole = RoleType.EXTERNAL,
                approverEmail = "finance@sinergiabadi.co.id",
                approverType = "Manajer Keuangan",
                passCode = null,
                status = PassStatus.PENDING,
                requestDate = "26 Agu 2026",
                notes = "Permintaan akses pemeriksaan laporan keuangan dan audit bukti transaksi"
            )
        )
    )
    val userAccessPasses: StateFlow<List<UserAccessPass>> = _userAccessPasses.asStateFlow()

    // Transactions with Proof files
    private val _appTransactions = MutableStateFlow<Map<String, List<AppTransaction>>>(
        mapOf(
            "comp_1" to listOf(
                AppTransaction(
                    id = "trx_01",
                    companyId = "comp_1",
                    transactionNumber = "TRX/OUT/2026/0821",
                    proofType = TransactionProofType.EXPENSE,
                    title = "Tagihan Server & Cloud Hosting Data Center",
                    categoryName = "Pengeluaran",
                    partyName = "PT Telkom Data Ekosistem",
                    amount = 16650000.0,
                    isIncome = false,
                    date = "24 Agu 2026",
                    accountCode = "61200",
                    accountName = "Beban Server & Cloud Hosting",
                    journalRefNumber = "JU-2026-08001",
                    createdBy = "Andi Pratama (Staff)",
                    notes = "Layanan Cloud Colocation Server Utama",
                    proofFiles = listOf(
                        ProofFileItem(
                            stepName = "Invoice / Bon",
                            fileName = "Invoice_TelkomCloud_Aug26.pdf",
                            fileFormat = "PDF",
                            fileSize = "420 KB",
                            uploadTimestamp = "24 Agu 2026 09:30",
                            isOptional = false,
                            previewNotes = "Invoice resmi Telkom No #INV/2026/08/9921 lengkap dengan stempel"
                        ),
                        ProofFileItem(
                            stepName = "Bukti Pembayaran",
                            fileName = "Bukti_Transfer_BCA_Telkom.jpeg",
                            fileFormat = "JPEG",
                            fileSize = "850 KB",
                            uploadTimestamp = "24 Agu 2026 10:05",
                            isOptional = false,
                            previewNotes = "Struk bukti transfer m-Banking BCA Settled & Valid"
                        )
                    )
                ),
                AppTransaction(
                    id = "trx_02",
                    companyId = "comp_1",
                    transactionNumber = "TRX/IN/2026/0819",
                    proofType = TransactionProofType.INCOME,
                    title = "Pembayaran Jasa Implementasi ERP & Konsultasi",
                    categoryName = "Pemasukan",
                    partyName = "PT Mitra Sejahtera Digital",
                    amount = 45000000.0,
                    isIncome = true,
                    date = "25 Agu 2026",
                    accountCode = "41100",
                    accountName = "Pendapatan Penjualan / Layanan Utama",
                    journalRefNumber = "JU-2026-08003",
                    createdBy = "Andi Pratama (Staff)",
                    notes = "Pelunasan Termin 1 Project Integrasi API",
                    proofFiles = listOf(
                        ProofFileItem(
                            stepName = "Invoice / Bon",
                            fileName = "Faktur_Tagihan_Termin1_Mitra.pdf",
                            fileFormat = "PDF",
                            fileSize = "510 KB",
                            uploadTimestamp = "25 Agu 2026 11:20",
                            isOptional = false,
                            previewNotes = "Faktur komersial penagihan termin 1 resmi ditandatangani"
                        ),
                        ProofFileItem(
                            stepName = "Bukti Penerimaan",
                            fileName = "Koran_Penerimaan_Bank_BCA.jpeg",
                            fileFormat = "JPEG",
                            fileSize = "940 KB",
                            uploadTimestamp = "25 Agu 2026 14:00",
                            isOptional = false,
                            previewNotes = "Bukti mutasi rekening koran masuk Rp 45.000.000 via BI-FAST"
                        )
                    )
                ),
                AppTransaction(
                    id = "trx_03",
                    companyId = "comp_1",
                    transactionNumber = "TRX/BUY/2026/0825",
                    proofType = TransactionProofType.PURCHASE,
                    title = "Pembelian Persediaan Perangkat Jaringan & Switch",
                    categoryName = "Pembelian",
                    partyName = "PT Sumber Suplier Utama",
                    amount = 35520000.0,
                    isIncome = false,
                    date = "25 Agu 2026",
                    accountCode = "11300",
                    accountName = "Persediaan Barang Dagangan",
                    journalRefNumber = "JU-2026-08002",
                    createdBy = "Andi Pratama (Staff)",
                    notes = "Pengadaan 20 unit IoT node dan switch manageable",
                    proofFiles = listOf(
                        ProofFileItem(
                            stepName = "Invoice / Bon",
                            fileName = "Invoice_Suplier_SumberUtama.pdf",
                            fileFormat = "PDF",
                            fileSize = "620 KB",
                            uploadTimestamp = "25 Agu 2026 13:10",
                            isOptional = false,
                            previewNotes = "Faktur pembelian Suplier #INV/SS/88129"
                        ),
                        ProofFileItem(
                            stepName = "Bukti Potong PPh (Opsional)",
                            fileName = "BuktiPotong_PPh22_Pengadaan.pdf",
                            fileFormat = "PDF",
                            fileSize = "310 KB",
                            uploadTimestamp = "25 Agu 2026 13:15",
                            isOptional = true,
                            previewNotes = "Bukti Potong PPh 22 DJP e-Bupot terlampir"
                        )
                    )
                ),
                AppTransaction(
                    id = "trx_04",
                    companyId = "comp_1",
                    transactionNumber = "TRX/SALE/2026/0826",
                    proofType = TransactionProofType.SALES,
                    title = "Penjualan Perangkat Sensor IoT Pertanian",
                    categoryName = "Penjualan",
                    partyName = "PT Agrotek Prima Mandiri",
                    amount = 28500000.0,
                    isIncome = true,
                    date = "26 Agu 2026",
                    accountCode = "41100",
                    accountName = "Pendapatan Penjualan / Layanan Utama",
                    journalRefNumber = "JU-2026-08004",
                    createdBy = "Andi Pratama (Staff)",
                    notes = "Penjualan 15 kit sensor smart agriculture",
                    proofFiles = listOf(
                        ProofFileItem(
                            stepName = "Invoice / Bon",
                            fileName = "Faktur_Penjualan_FP2026_091.pdf",
                            fileFormat = "PDF",
                            fileSize = "480 KB",
                            uploadTimestamp = "26 Agu 2026 08:45",
                            isOptional = false,
                            previewNotes = "Faktur penjualan lengkap e-Faktur NSFP #010.000-26.88219"
                        ),
                        ProofFileItem(
                            stepName = "Bukti Potong PPh (Opsional)",
                            fileName = "Bupot_PPh23_Agrotek.jpeg",
                            fileFormat = "JPEG",
                            fileSize = "620 KB",
                            uploadTimestamp = "26 Agu 2026 09:10",
                            isOptional = true,
                            previewNotes = "Bukti potong PPh 23 dari rekanan pembeli"
                        )
                    )
                ),
                AppTransaction(
                    id = "trx_05",
                    companyId = "comp_1",
                    transactionNumber = "TRX/SETTLE/2026/0820",
                    proofType = TransactionProofType.SETTLEMENT,
                    title = "Pelunasan Utang Suplier PT Indonet Solusi",
                    categoryName = "Pelunasan",
                    partyName = "PT Indonet Solusi Global",
                    amount = 8325000.0,
                    isIncome = false,
                    date = "20 Agu 2026",
                    accountCode = "21100",
                    accountName = "Utang Usaha (Accounts Payable)",
                    journalRefNumber = "JU-2026-08005",
                    createdBy = "Andi Pratama (Staff)",
                    notes = "Pelunasan tagihan server bulan lalu via SNAP BI-FAST",
                    proofFiles = listOf(
                        ProofFileItem(
                            stepName = "Bukti Pembayaran",
                            fileName = "Settlement_BIFAST_Slip_BCA.pdf",
                            fileFormat = "PDF",
                            fileSize = "390 KB",
                            uploadTimestamp = "20 Agu 2026 15:40",
                            isOptional = false,
                            previewNotes = "Bukti pembayaran sah settlement BI-FAST Ref #BIFAST-88301928"
                        )
                    )
                )
            )
        )
    )
    val appTransactions: StateFlow<Map<String, List<AppTransaction>>> = _appTransactions.asStateFlow()

    // Chart of Accounts Map per Company
    private val _accounts = MutableStateFlow<Map<String, List<Account>>>(
        mapOf(
            "comp_1" to generateDefaultCoA(IndustryType.TRADING),
            "comp_2" to generateDefaultCoA(IndustryType.SERVICES),
            "comp_3" to generateDefaultCoA(IndustryType.MANUFACTURING)
        )
    )
    val accounts: StateFlow<Map<String, List<Account>>> = _accounts.asStateFlow()

    // Invoices Map per Company
    private val _invoices = MutableStateFlow<Map<String, List<Invoice>>>(
        mapOf(
            "comp_1" to listOf(
                Invoice(
                    id = "inv_101",
                    companyId = "comp_1",
                    invoiceNumber = "INV/2026/08/9921",
                    vendorName = "PT Telkom Data Ekosistem",
                    vendorNpwp = "0100000000000000",
                    invoiceDate = "24 Agu 2026",
                    dueDate = "07 Sep 2026",
                    subtotal = 15000000.0,
                    taxAmount = 1650000.0, // 11%
                    totalAmount = 16650000.0,
                    status = InvoiceStatus.POSTED,
                    ocrConfidence = 99.2,
                    lineItems = listOf(
                        InvoiceItem("Layanan Cloud Server & Colocation Data Center", 1.0, 15000000.0, 15000000.0, "61200")
                    )
                ),
                Invoice(
                    id = "inv_102",
                    companyId = "comp_1",
                    invoiceNumber = "INV/SS/88129",
                    vendorName = "PT Sumber Suplier Utama",
                    vendorNpwp = "0234567890123456",
                    invoiceDate = "25 Agu 2026",
                    dueDate = "10 Sep 2026",
                    subtotal = 32000000.0,
                    taxAmount = 3520000.0,
                    totalAmount = 35520000.0,
                    status = InvoiceStatus.POSTED,
                    ocrConfidence = 97.8,
                    lineItems = listOf(
                        InvoiceItem("Persediaan Perangkat Keras Jaringan & IoT Node", 20.0, 1600000.0, 32000000.0, "11300")
                    )
                ),
                Invoice(
                    id = "inv_103",
                    companyId = "comp_1",
                    invoiceNumber = "INV/JNE/202608-44",
                    vendorName = "PT Jalur Nugraha Ekakurir (JNE)",
                    vendorNpwp = "0145678901234567",
                    invoiceDate = "26 Agu 2026",
                    dueDate = "02 Sep 2026",
                    subtotal = 2800000.0,
                    taxAmount = 308000.0,
                    totalAmount = 3108000.0,
                    status = InvoiceStatus.DRAFT,
                    ocrConfidence = 98.9,
                    lineItems = listOf(
                        InvoiceItem("Biaya Ekspedisi & Distribusi Barang Retail", 1.0, 2800000.0, 2800000.0, "61300")
                    )
                )
            )
        )
    )
    val invoices: StateFlow<Map<String, List<Invoice>>> = _invoices.asStateFlow()

    // Journal Entries Map per Company
    private val _journalEntries = MutableStateFlow<Map<String, List<JournalEntry>>>(
        mapOf(
            "comp_1" to listOf(
                JournalEntry(
                    id = "jrn_1",
                    companyId = "comp_1",
                    entryNumber = "JU-2026-08001",
                    date = "24 Agu 2026",
                    referenceType = "OCR_INVOICE",
                    referenceId = "inv_101",
                    memo = "Penerimaan Tagihan Cloud Server PT Telkom Data Ekosistem",
                    lines = listOf(
                        JournalLine("61200", "Beban Server & Cloud Hosting", debit = 15000000.0, credit = 0.0),
                        JournalLine("11510", "PPN Masukan (11%)", debit = 1650000.0, credit = 0.0),
                        JournalLine("21100", "Utang Usaha (Accounts Payable)", debit = 0.0, credit = 16650000.0)
                    )
                ),
                JournalEntry(
                    id = "jrn_2",
                    companyId = "comp_1",
                    entryNumber = "JU-2026-08002",
                    date = "25 Agu 2026",
                    referenceType = "OCR_INVOICE",
                    referenceId = "inv_102",
                    memo = "Pembelian Persediaan Perangkat PT Sumber Suplier",
                    lines = listOf(
                        JournalLine("11300", "Persediaan Barang Dagangan", debit = 32000000.0, credit = 0.0),
                        JournalLine("11510", "PPN Masukan (11%)", debit = 3520000.0, credit = 0.0),
                        JournalLine("21100", "Utang Usaha (Accounts Payable)", debit = 0.0, credit = 35520000.0)
                    )
                )
            )
        )
    )
    val journalEntries: StateFlow<Map<String, List<JournalEntry>>> = _journalEntries.asStateFlow()

    // Supplier Payments per Company
    private val _supplierPayments = MutableStateFlow<Map<String, List<SupplierPayment>>>(
        mapOf(
            "comp_1" to listOf(
                SupplierPayment(
                    id = "pay_01",
                    companyId = "comp_1",
                    invoiceId = "inv_100_prev",
                    vendorName = "PT Indonet Solusi Global",
                    invoiceNumber = "INV/IND/7731",
                    bankName = "Bank Central Asia (BCA)",
                    accountNumber = "8830192831",
                    accountHolder = "PT Indonet Solusi Global",
                    amount = 8325000.0,
                    adminFee = 2500.0,
                    date = "20 Agu 2026"
                )
            )
        )
    )
    val supplierPayments: StateFlow<Map<String, List<SupplierPayment>>> = _supplierPayments.asStateFlow()

    // DJP Tax Filings per Company
    private val _taxFilings = MutableStateFlow<Map<String, List<TaxFiling>>>(
        mapOf(
            "comp_1" to listOf(
                TaxFiling(
                    id = "tax_01",
                    companyId = "comp_1",
                    taxType = TaxType.PPN_1111,
                    period = "Juli 2026",
                    taxYear = 2026,
                    idBilling = "829301928301923",
                    ntpn = "882A9F1B2C3D4E5F",
                    bpeNumber = "BPE-DJP-202608-994821",
                    totalTax = 12450000.0,
                    status = TaxFilingStatus.FILED,
                    submittedAt = "15 Agu 2026 10:24 WIB"
                ),
                TaxFiling(
                    id = "tax_02",
                    companyId = "comp_1",
                    taxType = TaxType.PPH_UNIFIKASI,
                    period = "Juli 2026",
                    taxYear = 2026,
                    idBilling = "910293847561029",
                    ntpn = "993F1E2D3C4B5A67",
                    bpeNumber = "BPE-DJP-202608-771923",
                    totalTax = 4200000.0,
                    status = TaxFilingStatus.FILED,
                    submittedAt = "18 Agu 2026 14:15 WIB"
                ),
                TaxFiling(
                    id = "tax_03",
                    companyId = "comp_1",
                    taxType = TaxType.PPN_1111,
                    period = "Agustus 2026",
                    taxYear = 2026,
                    idBilling = null,
                    totalTax = 5170000.0, // Calculated from current journals
                    status = TaxFilingStatus.DRAFT
                )
            ),
            "comp_2" to listOf(
                TaxFiling(
                    id = "tax_04",
                    companyId = "comp_2",
                    taxType = TaxType.PPH_FINAL_UMKM,
                    period = "Juli 2026",
                    taxYear = 2026,
                    idBilling = "718293041928374",
                    ntpn = "112A3B4C5D6E7F88",
                    bpeNumber = "BPE-DJP-202608-119283",
                    totalTax = 450000.0, // 0.5% x 90jt
                    status = TaxFilingStatus.FILED,
                    submittedAt = "10 Agu 2026 09:12 WIB"
                )
            )
        )
    )
    val taxFilings: StateFlow<Map<String, List<TaxFiling>>> = _taxFilings.asStateFlow()

    // Pre-populated e-Faktur from DJP Coretax
    private val _prePopulatedEFaktur = MutableStateFlow<List<EFakturPrePopulated>>(
        listOf(
            EFakturPrePopulated("010.000-26.88392019", "PT Telkom Data Ekosistem", "01.000.000.0-000.000", "24 Agu 2026", 15000000.0, 1650000.0, true),
            EFakturPrePopulated("010.000-26.77281920", "PT Sumber Suplier Utama", "02.345.678.9-012.000", "25 Agu 2026", 32000000.0, 3520000.0, true),
            EFakturPrePopulated("010.000-26.99182374", "PT Jalur Nugraha Ekakurir (JNE)", "01.456.789.0-123.000", "26 Agu 2026", 2800000.0, 308000.0, false)
        )
    )
    val prePopulatedEFaktur: StateFlow<List<EFakturPrePopulated>> = _prePopulatedEFaktur.asStateFlow()

    // Bank Accounts per Company
    private val _bankAccounts = MutableStateFlow<Map<String, List<BankAccount>>>(
        mapOf(
            "comp_1" to listOf(
                BankAccount(
                    id = "bank_01",
                    companyId = "comp_1",
                    bankName = "Bank Central Asia (BCA)",
                    cardTypes = setOf(CardType.DEBIT, CardType.CREDIT),
                    accountNumber = "0882-9912-30",
                    accountHolder = "PT Sinergi Abadi Digital",
                    isPrimary = true,
                    balance = 145000000.0,
                    branchOffice = "KCU Sudirman Jakarta"
                ),
                BankAccount(
                    id = "bank_02",
                    companyId = "comp_1",
                    bankName = "Bank Mandiri",
                    cardTypes = setOf(CardType.DEBIT),
                    accountNumber = "124-00-1928374-1",
                    accountHolder = "PT Sinergi Abadi Digital",
                    isPrimary = false,
                    balance = 68500000.0,
                    branchOffice = "KC Thamrin Jakarta"
                ),
                BankAccount(
                    id = "bank_03",
                    companyId = "comp_1",
                    bankName = "Bank Rakyat Indonesia (BRI)",
                    cardTypes = setOf(CardType.DEBIT),
                    accountNumber = "0206-01-002938-50-3",
                    accountHolder = "PT Sinergi Abadi Digital",
                    isPrimary = false,
                    balance = 35000000.0,
                    branchOffice = "KC Gatot Subroto"
                )
            ),
            "comp_2" to listOf(
                BankAccount(
                    id = "bank_04",
                    companyId = "comp_2",
                    bankName = "Bank Central Asia (BCA)",
                    cardTypes = setOf(CardType.DEBIT),
                    accountNumber = "5270-192-881",
                    accountHolder = "CV Berkah Jaya Abadi",
                    isPrimary = true,
                    balance = 42000000.0,
                    branchOffice = "KC Bekasi Barat"
                )
            ),
            "comp_3" to listOf(
                BankAccount(
                    id = "bank_05",
                    companyId = "comp_3",
                    bankName = "Bank Negara Indonesia (BNI)",
                    cardTypes = setOf(CardType.DEBIT, CardType.CREDIT),
                    accountNumber = "0392-881-229",
                    accountHolder = "PT Nusantara Agro Industri",
                    isPrimary = true,
                    balance = 185000000.0,
                    branchOffice = "KC Karawang"
                )
            )
        )
    )
    val bankAccounts: StateFlow<Map<String, List<BankAccount>>> = _bankAccounts.asStateFlow()

    // Inventory Items per Company
    private val _inventoryItems = MutableStateFlow<Map<String, List<InventoryItem>>>(
        mapOf(
            "comp_1" to listOf(
                InventoryItem(
                    id = "inv_item_1",
                    companyId = "comp_1",
                    sku = "PRD-SRV-01",
                    name = "Server Rack 24U Enterprise",
                    category = "Infrastruktur",
                    currentStock = 8,
                    minimumStock = 3,
                    unit = "Unit",
                    costPrice = 4500000.0,
                    sellingPrice = 6200000.0,
                    lastUpdated = "26 Agu 2026"
                ),
                InventoryItem(
                    id = "inv_item_2",
                    companyId = "comp_1",
                    sku = "PRD-RT-02",
                    name = "Router Gigabit VPN Dual-WAN",
                    category = "Networking",
                    currentStock = 24,
                    minimumStock = 5,
                    unit = "Unit",
                    costPrice = 1250000.0,
                    sellingPrice = 1850000.0,
                    lastUpdated = "25 Agu 2026"
                ),
                InventoryItem(
                    id = "inv_item_3",
                    companyId = "comp_1",
                    sku = "PRD-SW-03",
                    name = "Switch Managed PoE+ 24-Port",
                    category = "Networking",
                    currentStock = 2,
                    minimumStock = 5,
                    unit = "Unit",
                    costPrice = 2800000.0,
                    sellingPrice = 3900000.0,
                    lastUpdated = "24 Agu 2026"
                ),
                InventoryItem(
                    id = "inv_item_4",
                    companyId = "comp_1",
                    sku = "PRD-FO-04",
                    name = "Kabel Fiber Optic Single Mode 100m",
                    category = "Kabel & Aksesoris",
                    currentStock = 45,
                    minimumStock = 10,
                    unit = "Roll",
                    costPrice = 350000.0,
                    sellingPrice = 550000.0,
                    lastUpdated = "23 Agu 2026"
                ),
                InventoryItem(
                    id = "inv_item_5",
                    companyId = "comp_1",
                    sku = "PRD-SSD-05",
                    name = "SSD NVMe 2TB Enterprise M.2",
                    category = "Penyimpanan",
                    currentStock = 3,
                    minimumStock = 6,
                    unit = "Unit",
                    costPrice = 1800000.0,
                    sellingPrice = 2450000.0,
                    lastUpdated = "25 Agu 2026"
                )
            )
        )
    )
    val inventoryItems: StateFlow<Map<String, List<InventoryItem>>> = _inventoryItems.asStateFlow()

    // Payables (Hutang Pemasok)
    private val _payableItems = MutableStateFlow<Map<String, List<PayableItem>>>(
        mapOf(
            "comp_1" to listOf(
                PayableItem(
                    id = "pay_01",
                    companyId = "comp_1",
                    supplierName = "PT Sumber Suplier Utama",
                    invoiceNumber = "INV/SPL/9921",
                    invoiceDate = "15 Agu 2026",
                    dueDate = "28 Agu 2026",
                    totalAmount = 35520000.0,
                    paidAmount = 0.0,
                    status = "JATUH_TEMPO"
                ),
                PayableItem(
                    id = "pay_02",
                    companyId = "comp_1",
                    supplierName = "PT Telkom Data Ekosistem",
                    invoiceNumber = "INV/TDE/1092",
                    invoiceDate = "18 Agu 2026",
                    dueDate = "31 Agu 2026",
                    totalAmount = 16650000.0,
                    paidAmount = 0.0,
                    status = "MENUNGGU_JATUH_TEMPO"
                ),
                PayableItem(
                    id = "pay_03",
                    companyId = "comp_1",
                    supplierName = "PT Indonet Solusi Global",
                    invoiceNumber = "INV/IND/7731",
                    invoiceDate = "05 Agu 2026",
                    dueDate = "20 Agu 2026",
                    totalAmount = 8325000.0,
                    paidAmount = 8325000.0,
                    status = "LUNAS"
                )
            )
        )
    )
    val payableItems: StateFlow<Map<String, List<PayableItem>>> = _payableItems.asStateFlow()

    // Receivables (Piutang Pelanggan)
    private val _receivableItems = MutableStateFlow<Map<String, List<ReceivableItem>>>(
        mapOf(
            "comp_1" to listOf(
                ReceivableItem(
                    id = "rec_01",
                    companyId = "comp_1",
                    customerOrParty = "PT Mahakarya Multimedia",
                    invoiceNumber = "INV/2026/08/001",
                    invoiceDate = "14 Agu 2026",
                    dueDate = "29 Agu 2026",
                    totalAmount = 25000000.0,
                    receivedAmount = 0.0,
                    status = "JATUH_TEMPO"
                ),
                ReceivableItem(
                    id = "rec_02",
                    companyId = "comp_1",
                    customerOrParty = "CV Gemilang Nusantara",
                    invoiceNumber = "INV/2026/08/002",
                    invoiceDate = "18 Agu 2026",
                    dueDate = "02 Sep 2026",
                    totalAmount = 18500000.0,
                    receivedAmount = 5000000.0,
                    status = "MENUNGGU_JATUH_TEMPO"
                ),
                ReceivableItem(
                    id = "rec_03",
                    companyId = "comp_1",
                    customerOrParty = "PT Cipta Solusi Mandiri",
                    invoiceNumber = "INV/2026/07/045",
                    invoiceDate = "25 Jul 2026",
                    dueDate = "15 Agu 2026",
                    totalAmount = 45000000.0,
                    receivedAmount = 45000000.0,
                    status = "LUNAS"
                )
            )
        )
    )
    val receivableItems: StateFlow<Map<String, List<ReceivableItem>>> = _receivableItems.asStateFlow()

    fun addBankAccount(
        companyId: String,
        bankName: String,
        cardTypes: Set<CardType>,
        accountNumber: String,
        accountHolder: String = "PT Sinergi Abadi Digital",
        branchOffice: String = "Kantor Cabang Utama"
    ): BankAccount {
        val currentMap = _bankAccounts.value.toMutableMap()
        val currentList = currentMap[companyId]?.toMutableList() ?: mutableListOf()
        val isFirst = currentList.isEmpty()
        val newAccount = BankAccount(
            companyId = companyId,
            bankName = bankName.trim(),
            cardTypes = if (cardTypes.isEmpty()) setOf(CardType.DEBIT) else cardTypes,
            accountNumber = accountNumber.trim(),
            accountHolder = accountHolder.trim(),
            isPrimary = isFirst,
            balance = 50000000.0,
            branchOffice = branchOffice.trim()
        )
        currentList.add(newAccount)
        currentMap[companyId] = currentList
        _bankAccounts.value = currentMap
        return newAccount
    }

    fun deleteBankAccount(companyId: String, accountId: String) {
        val currentMap = _bankAccounts.value.toMutableMap()
        val currentList = currentMap[companyId]?.toMutableList() ?: return
        val removed = currentList.removeAll { it.id == accountId }
        if (removed && currentList.isNotEmpty() && currentList.none { it.isPrimary }) {
            currentList[0] = currentList[0].copy(isPrimary = true)
        }
        currentMap[companyId] = currentList
        _bankAccounts.value = currentMap
    }

    fun setPrimaryBankAccount(companyId: String, accountId: String) {
        val currentMap = _bankAccounts.value.toMutableMap()
        val currentList = currentMap[companyId]?.map {
            it.copy(isPrimary = it.id == accountId)
        } ?: return
        currentMap[companyId] = currentList
        _bankAccounts.value = currentMap
    }

    fun getDuePaymentItems(companyId: String): List<DuePaymentItem> {
        val list = mutableListOf<DuePaymentItem>()

        // 1. Piutang Jatuh Tempo (Receivables)
        val receivables = _receivableItems.value[companyId] ?: emptyList()
        receivables.filter { !it.isPaidOff }.forEach { rec ->
            val days = if (rec.status == "JATUH_TEMPO") 2 else 6
            list.add(
                DuePaymentItem(
                    id = "due_rec_${rec.id}",
                    category = DueCategory.RECEIVABLE,
                    title = "Tagihan Piutang Penjualan",
                    partyOrDescription = rec.customerOrParty,
                    referenceNumber = rec.invoiceNumber,
                    amount = rec.remainingAmount,
                    dueDate = rec.dueDate,
                    daysRemaining = days,
                    targetScreen = "INVOICES",
                    actionLabel = "Follow Up"
                )
            )
        }

        // 2. Hutang Jatuh Tempo (Payables)
        val payables = _payableItems.value[companyId] ?: emptyList()
        payables.filter { !it.isPaidOff }.forEach { pay ->
            val days = if (pay.status == "JATUH_TEMPO") 1 else 4
            list.add(
                DuePaymentItem(
                    id = "due_pay_${pay.id}",
                    category = DueCategory.PAYABLE,
                    title = "Tagihan Hutang Pemasok",
                    partyOrDescription = pay.supplierName,
                    referenceNumber = pay.invoiceNumber,
                    amount = pay.remainingAmount,
                    dueDate = pay.dueDate,
                    daysRemaining = days,
                    targetScreen = "PAYMENT",
                    actionLabel = "Bayar Sekarang"
                )
            )
        }

        // 3. Biaya Pajak (PPN & PPh)
        list.add(
            DuePaymentItem(
                id = "due_tax_ppn",
                category = DueCategory.TAX,
                title = "PPN Kurang Bayar (e-Faktur)",
                partyOrDescription = "DJP Coretax Masa Agustus 2026",
                referenceNumber = "SPT-PPN-1111-202608",
                amount = 5170000.0,
                dueDate = "31 Agu 2026",
                daysRemaining = 4,
                targetScreen = "CORETAX",
                actionLabel = "Setor NTPN"
            )
        )
        list.add(
            DuePaymentItem(
                id = "due_tax_pph",
                category = DueCategory.TAX,
                title = "PPh Pasal 21 Karyawan (Unifikasi)",
                partyOrDescription = "Kantor Pelayanan Pajak Pratama",
                referenceNumber = "EBILL-9102938475",
                amount = 4200000.0,
                dueDate = "10 Sep 2026",
                daysRemaining = 14,
                targetScreen = "CORETAX",
                actionLabel = "Kode Billing"
            )
        )

        return list.sortedBy { it.daysRemaining }
    }

    // Active Company Helper
    fun getActiveCompany(): Company {
        val currentId = _activeCompanyId.value
        return _companies.value.find { it.id == currentId } ?: _companies.value.first()
    }

    fun authenticateWithEmail(email: String, companyToRegister: Company? = null, role: RoleType = RoleType.OWNER) {
        _userEmail.value = email.trim()
        _currentRole.value = role
        if (companyToRegister != null) {
            _companies.value = _companies.value + companyToRegister
            _activeCompanyId.value = companyToRegister.id
            val currentAccounts = _accounts.value.toMutableMap()
            currentAccounts[companyToRegister.id] = generateDefaultCoA(companyToRegister.industryType)
            _accounts.value = currentAccounts
        }
        _isAuthenticated.value = true
    }

    fun logout() {
        _isAuthenticated.value = false
    }

    fun switchCompany(companyId: String) {
        _activeCompanyId.value = companyId
    }

    fun switchRole(role: RoleType) {
        _currentRole.value = role
    }

    // Add New Company
    fun addCompany(
        name: String,
        entityType: EntityType,
        industryType: IndustryType,
        npwp16: String?,
        address: String
    ): Company {
        val cleanNpwp = npwp16?.filter { it.isDigit() }?.takeIf { it.isNotBlank() }
        val isPkp = !cleanNpwp.isNullOrBlank()
        val newComp = Company(
            id = "comp_${System.currentTimeMillis()}",
            name = name,
            entityType = entityType,
            industryType = industryType,
            npwp16 = cleanNpwp,
            isPkp = isPkp,
            address = address
        )

        _companies.value = _companies.value + newComp

        // Initialize CoA
        val currentAccounts = _accounts.value.toMutableMap()
        currentAccounts[newComp.id] = generateDefaultCoA(industryType)
        _accounts.value = currentAccounts

        // Initialize Tax Filing default if PKP vs Non-PKP
        val currentTax = _taxFilings.value.toMutableMap()
        if (isPkp) {
            currentTax[newComp.id] = listOf(
                TaxFiling(
                    companyId = newComp.id,
                    taxType = TaxType.PPN_1111,
                    period = "Agustus 2026",
                    totalTax = 0.0,
                    status = TaxFilingStatus.DRAFT
                )
            )
        } else {
            currentTax[newComp.id] = listOf(
                TaxFiling(
                    companyId = newComp.id,
                    taxType = TaxType.PPH_FINAL_UMKM,
                    period = "Agustus 2026",
                    totalTax = 0.0,
                    status = TaxFilingStatus.DRAFT
                )
            )
        }
        _taxFilings.value = currentTax

        // Switch to newly created company
        _activeCompanyId.value = newComp.id
        return newComp
    }

    // Check for Duplicate Invoice (Anti Double-Scanning)
    fun findDuplicateInvoice(invoiceNumber: String, vendorName: String): Invoice? {
        val company = getActiveCompany()
        val existingInvoices = _invoices.value[company.id] ?: emptyList()
        val normalizedInvNum = invoiceNumber.trim().lowercase()
        val normalizedVendor = vendorName.trim().lowercase()

        return existingInvoices.find { existing ->
            existing.invoiceNumber.trim().lowercase() == normalizedInvNum ||
            (existing.vendorName.trim().lowercase() == normalizedVendor && existing.invoiceNumber.trim().lowercase() == normalizedInvNum)
        }
    }

    // Add Invoice and Auto-Journal Draft with Duplicate Check
    fun addInvoiceFromOcr(
        vendorName: String,
        vendorNpwp: String?,
        invoiceNumber: String,
        date: String,
        subtotal: Double,
        isPkp: Boolean,
        categoryAccountCode: String,
        categoryAccountName: String,
        lineDescription: String
    ): Result<Invoice> {
        val company = getActiveCompany()

        // Double Scanning Validation: Reject if already exists
        val duplicate = findDuplicateInvoice(invoiceNumber, vendorName)
        if (duplicate != null) {
            return Result.failure(
                IllegalStateException("Scan DITOLAK: Terdeteksi Double Scanning! Faktur #${duplicate.invoiceNumber} dari ${duplicate.vendorName} sudah pernah tercatat pada tanggal ${duplicate.invoiceDate} (Status: ${duplicate.status.label}).")
            )
        }

        val taxAmount = if (company.isPkp && isPkp) subtotal * 0.11 else 0.0
        val totalAmount = subtotal + taxAmount

        val invoice = Invoice(
            companyId = company.id,
            invoiceNumber = invoiceNumber,
            vendorName = vendorName,
            vendorNpwp = vendorNpwp,
            invoiceDate = date,
            dueDate = "10 Sep 2026",
            subtotal = subtotal,
            taxAmount = taxAmount,
            totalAmount = totalAmount,
            status = InvoiceStatus.DRAFT,
            ocrConfidence = 98.6,
            lineItems = listOf(
                InvoiceItem(lineDescription, 1.0, subtotal, subtotal, categoryAccountCode)
            )
        )

        val currentMap = _invoices.value.toMutableMap()
        val list = currentMap[company.id]?.toMutableList() ?: mutableListOf()
        list.add(0, invoice)
        currentMap[company.id] = list
        _invoices.value = currentMap

        return Result.success(invoice)
    }

    // Delete Journal Entry (Authorized for MANAGER and OWNER only)
    fun deleteJournalEntry(journalId: String, currentRole: RoleType): Result<JournalEntry> {
        if (!currentRole.canDeleteJournal()) {
            return Result.failure(
                SecurityException("Akses Ditolak! Hanya tingkat Manajer dan Owner yang memiliki otorisasi untuk menghapus jurnal yang sudah diinput.")
            )
        }

        val company = getActiveCompany()
        val currentJournals = _journalEntries.value[company.id] ?: emptyList()
        val journalToDelete = currentJournals.find { it.id == journalId }
            ?: return Result.failure(IllegalArgumentException("Jurnal dengan ID $journalId tidak ditemukan."))

        // Remove from list
        val jrnMap = _journalEntries.value.toMutableMap()
        jrnMap[company.id] = currentJournals.filter { it.id != journalId }
        _journalEntries.value = jrnMap

        // If this journal was tied to an invoice, revert invoice back to DRAFT
        if (journalToDelete.referenceType == "OCR_INVOICE" && journalToDelete.referenceId != null) {
            val invMap = _invoices.value.toMutableMap()
            val compInvoices = invMap[company.id] ?: emptyList()
            invMap[company.id] = compInvoices.map { inv ->
                if (inv.id == journalToDelete.referenceId) inv.copy(status = InvoiceStatus.DRAFT) else inv
            }
            _invoices.value = invMap
        }

        return Result.success(journalToDelete)
    }

    // Delete Invoice Draft (Authorized for staff and managers)
    fun deleteInvoiceDraft(invoiceId: String): Result<Unit> {
        val company = getActiveCompany()
        val compInvoices = _invoices.value[company.id] ?: emptyList()
        val invMap = _invoices.value.toMutableMap()
        invMap[company.id] = compInvoices.filter { it.id != invoiceId }
        _invoices.value = invMap
        return Result.success(Unit)
    }

    // Post Invoice to General Ledger
    fun postInvoiceToLedger(invoiceId: String) {
        val company = getActiveCompany()
        val compInvoices = _invoices.value[company.id] ?: return
        val invoice = compInvoices.find { it.id == invoiceId } ?: return

        // Create Balanced Double Entry Journal Lines
        val lines = mutableListOf<JournalLine>()
        val lineItem = invoice.lineItems.firstOrNull()
        val expenseAccountCode = lineItem?.accountCode ?: "61200"
        val expenseAccountName = getAccountName(company.id, expenseAccountCode)

        // Debit Expense / Inventory
        lines.add(JournalLine(expenseAccountCode, expenseAccountName, debit = invoice.subtotal, credit = 0.0))

        // Debit PPN Masukan if applicable
        if (invoice.taxAmount > 0) {
            lines.add(JournalLine("11510", "PPN Masukan (11%)", debit = invoice.taxAmount, credit = 0.0))
        }

        // Credit Accounts Payable
        lines.add(JournalLine("21100", "Utang Usaha (Accounts Payable)", debit = 0.0, credit = invoice.totalAmount))

        val journalEntry = JournalEntry(
            companyId = company.id,
            entryNumber = "JU-2026-08${System.currentTimeMillis().toString().takeLast(3)}",
            date = invoice.invoiceDate,
            referenceType = "OCR_INVOICE",
            referenceId = invoice.id,
            memo = "Posting Tagihan ${invoice.vendorName} #${invoice.invoiceNumber}",
            lines = lines,
            isPosted = true
        )

        // Update Journal Map
        val jrnMap = _journalEntries.value.toMutableMap()
        val jrnList = jrnMap[company.id]?.toMutableList() ?: mutableListOf()
        jrnList.add(0, journalEntry)
        jrnMap[company.id] = jrnList
        _journalEntries.value = jrnMap

        // Update Invoice status
        val invMap = _invoices.value.toMutableMap()
        val updatedInvList = compInvoices.map {
            if (it.id == invoiceId) it.copy(status = InvoiceStatus.POSTED) else it
        }
        invMap[company.id] = updatedInvList
        _invoices.value = invMap
    }

    // Execute Open Banking (BI-FAST) Supplier Payment
    fun executeSupplierPayment(
        invoiceId: String,
        bankName: String,
        accountNumber: String,
        accountHolder: String
    ): SupplierPayment {
        val company = getActiveCompany()
        val compInvoices = _invoices.value[company.id] ?: emptyList()
        val invoice = compInvoices.find { it.id == invoiceId } ?: throw IllegalArgumentException("Invoice not found")

        val payment = SupplierPayment(
            companyId = company.id,
            invoiceId = invoice.id,
            vendorName = invoice.vendorName,
            invoiceNumber = invoice.invoiceNumber,
            bankName = bankName,
            accountNumber = accountNumber,
            accountHolder = accountHolder,
            amount = invoice.totalAmount,
            adminFee = 2500.0,
            date = getCurrentFormattedDate(),
            paymentStatus = PaymentStatus.SUCCESS
        )

        // Update payment history
        val payMap = _supplierPayments.value.toMutableMap()
        val payList = payMap[company.id]?.toMutableList() ?: mutableListOf()
        payList.add(0, payment)
        payMap[company.id] = payList
        _supplierPayments.value = payMap

        // Update invoice to PAID
        val invMap = _invoices.value.toMutableMap()
        invMap[company.id] = compInvoices.map {
            if (it.id == invoiceId) it.copy(status = InvoiceStatus.PAID) else it
        }
        _invoices.value = invMap

        // Auto Reconciliation Journal: Debit Utang Usaha, Debit Biaya Admin, Kredit Kas/Bank BCA
        val jrnMap = _journalEntries.value.toMutableMap()
        val jrnList = jrnMap[company.id]?.toMutableList() ?: mutableListOf()
        val reconJournal = JournalEntry(
            companyId = company.id,
            entryNumber = "JU-PAY-${System.currentTimeMillis().toString().takeLast(4)}",
            date = getCurrentFormattedDate(),
            referenceType = "OPEN_BANKING",
            referenceId = payment.id,
            memo = "Pelunasan Tagihan ${invoice.vendorName} via SNAP BI-FAST",
            lines = listOf(
                JournalLine("21100", "Utang Usaha (Accounts Payable)", debit = invoice.totalAmount, credit = 0.0),
                JournalLine("61910", "Beban Administrasi Bank", debit = 2500.0, credit = 0.0),
                JournalLine("11120", "Bank BCA Rekening Operasional", debit = 0.0, credit = invoice.totalAmount + 2500.0)
            ),
            isPosted = true
        )
        jrnList.add(0, reconJournal)
        jrnMap[company.id] = jrnList
        _journalEntries.value = jrnMap

        return payment
    }

    // Generate MPN G3 ID Billing for Tax
    fun generateTaxBilling(taxFilingId: String): String {
        val company = getActiveCompany()
        val currentFilings = _taxFilings.value[company.id] ?: emptyList()
        val generatedBilling = "88${(1000000000000L..9999999999999L).random()}"

        val updatedList = currentFilings.map {
            if (it.id == taxFilingId) {
                it.copy(
                    idBilling = generatedBilling,
                    status = TaxFilingStatus.BILLED
                )
            } else it
        }
        val map = _taxFilings.value.toMutableMap()
        map[company.id] = updatedList
        _taxFilings.value = map
        return generatedBilling
    }

    // Pay Tax Billing via Open Banking
    fun payTaxBilling(taxFilingId: String) {
        val company = getActiveCompany()
        val currentFilings = _taxFilings.value[company.id] ?: emptyList()
        val filing = currentFilings.find { it.id == taxFilingId } ?: return
        val generatedNtpn = (10000000..99999999).random().toString(16).uppercase() + "9F1B"

        val updatedList = currentFilings.map {
            if (it.id == taxFilingId) {
                it.copy(
                    ntpn = generatedNtpn,
                    status = TaxFilingStatus.PAID
                )
            } else it
        }
        val map = _taxFilings.value.toMutableMap()
        map[company.id] = updatedList
        _taxFilings.value = map

        // Auto Journal for Tax Settlement
        val jrnMap = _journalEntries.value.toMutableMap()
        val jrnList = jrnMap[company.id]?.toMutableList() ?: mutableListOf()
        val taxJournal = JournalEntry(
            companyId = company.id,
            entryNumber = "JU-TAX-${System.currentTimeMillis().toString().takeLast(4)}",
            date = getCurrentFormattedDate(),
            referenceType = "TAX_SETTLEMENT",
            referenceId = filing.id,
            memo = "Penyetoran Pajak ${filing.taxType.label} Masa ${filing.period} (NTPN: $generatedNtpn)",
            lines = listOf(
                JournalLine("21210", "Utang Pajak (PPN/PPh)", debit = filing.totalTax, credit = 0.0),
                JournalLine("11120", "Bank BCA Rekening Operasional", debit = 0.0, credit = filing.totalTax)
            ),
            isPosted = true
        )
        jrnList.add(0, taxJournal)
        jrnMap[company.id] = jrnList
        _journalEntries.value = jrnMap
    }

    // One-Click File to DJP Coretax
    fun fileToCoretax(taxFilingId: String): String {
        val company = getActiveCompany()
        val currentFilings = _taxFilings.value[company.id] ?: emptyList()
        val bpeNumber = "BPE-DJP-${SimpleDateFormat("yyyyMM", Locale.getDefault()).format(Date())}-${(100000..999999).random()}"
        val submittedTime = SimpleDateFormat("dd MMM yyyy HH:mm", indonesianLocale).format(Date()) + " WIB"

        val updatedList = currentFilings.map {
            if (it.id == taxFilingId) {
                it.copy(
                    bpeNumber = bpeNumber,
                    status = TaxFilingStatus.FILED,
                    submittedAt = submittedTime
                )
            } else it
        }
        val map = _taxFilings.value.toMutableMap()
        map[company.id] = updatedList
        _taxFilings.value = map
        return bpeNumber
    }

    // Calculate Financial Summary for Dashboard
    fun getFinancialSummary(): FinancialSummary {
        val company = getActiveCompany()
        val invoices = _invoices.value[company.id] ?: emptyList()
        val unpaidInvoices = invoices.filter { it.status == InvoiceStatus.POSTED }
        val accounts = _accounts.value[company.id] ?: emptyList()
        val appTrxs = _appTransactions.value[company.id] ?: emptyList()

        val kasBank = accounts.filter { it.code.startsWith("111") }.sumOf { it.balance }
        val piutang = accounts.filter { it.code.startsWith("112") }.sumOf { it.balance }
        val utangUsaha = unpaidInvoices.sumOf { it.totalAmount }
        
        val trxIncome = appTrxs.filter { it.isIncome }.sumOf { it.amount }
        val trxExpense = appTrxs.filter { !it.isIncome }.sumOf { it.amount }

        val pendapatanBulanIni = (accounts.filter { it.category == AccountCategory.REVENUE }.sumOf { it.balance } + trxIncome)
        val bebanBulanIni = (accounts.filter { it.category == AccountCategory.EXPENSE }.sumOf { it.balance } +
                invoices.filter { it.status != InvoiceStatus.CANCELLED }.sumOf { it.subtotal } + trxExpense)
        val labaBersih = pendapatanBulanIni - bebanBulanIni

        return FinancialSummary(
            kasBank = kasBank,
            piutangUsaha = piutang,
            utangUsaha = utangUsaha,
            pendapatan = pendapatanBulanIni,
            beban = bebanBulanIni,
            labaBersih = labaBersih
        )
    }

    // Access Pass Request Flow (Sends notification / request to Owner/Manager email)
    fun requestUserAccessPass(
        name: String,
        email: String,
        requestedRole: RoleType,
        approverType: String, // "Owner / Super Admin" or "Manajer Keuangan"
        approverEmail: String,
        notes: String
    ): UserAccessPass {
        val newPass = UserAccessPass(
            id = "pass_${System.currentTimeMillis()}",
            name = name.trim(),
            email = email.trim(),
            requestedRole = requestedRole,
            approverEmail = approverEmail.trim(),
            approverType = approverType,
            passCode = null,
            status = PassStatus.PENDING,
            requestDate = getCurrentFormattedDate(),
            notes = notes.trim()
        )
        _userAccessPasses.value = listOf(newPass) + _userAccessPasses.value
        return newPass
    }

    // Approve User Pass by Owner / Manager
    fun approveUserAccessPass(passId: String, currentRole: RoleType): Result<UserAccessPass> {
        if (!currentRole.canApprovePass()) {
            return Result.failure(SecurityException("Hanya Owner / Super Admin dan Manajer yang berwenang menyetujui pass akses pengguna."))
        }

        val list = _userAccessPasses.value
        val target = list.find { it.id == passId }
            ?: return Result.failure(IllegalArgumentException("Permintaan pass tidak ditemukan."))

        val generatedPassCode = "PASS-${target.requestedRole.code}-${(1000..9999).random()}"
        val approved = target.copy(
            status = PassStatus.APPROVED,
            passCode = generatedPassCode,
            approvedDate = "${getCurrentFormattedDate()} (Disetujui ${currentRole.title})"
        )

        _userAccessPasses.value = list.map { if (it.id == passId) approved else it }
        return Result.success(approved)
    }

    // Reject User Pass
    fun rejectUserAccessPass(passId: String, reason: String): Result<UserAccessPass> {
        val list = _userAccessPasses.value
        val target = list.find { it.id == passId }
            ?: return Result.failure(IllegalArgumentException("Permintaan pass tidak ditemukan."))

        val rejected = target.copy(
            status = PassStatus.REJECTED,
            notes = if (reason.isNotBlank()) "Ditolak: $reason" else target.notes
        )

        _userAccessPasses.value = list.map { if (it.id == passId) rejected else it }
        return Result.success(rejected)
    }

    // Add Transaction with Proof files
    fun addTransactionWithProof(
        proofType: TransactionProofType,
        title: String,
        partyName: String,
        amount: Double,
        isIncome: Boolean,
        date: String,
        accountCode: String,
        accountName: String,
        proofFiles: List<ProofFileItem>,
        notes: String
    ): AppTransaction {
        val company = getActiveCompany()
        val trxNumber = when (proofType) {
            TransactionProofType.EXPENSE -> "TRX/OUT/2026/${(1000..9999).random()}"
            TransactionProofType.INCOME -> "TRX/IN/2026/${(1000..9999).random()}"
            TransactionProofType.PURCHASE -> "TRX/BUY/2026/${(1000..9999).random()}"
            TransactionProofType.SALES -> "TRX/SALE/2026/${(1000..9999).random()}"
            TransactionProofType.SETTLEMENT -> "TRX/SETTLE/2026/${(1000..9999).random()}"
        }

        val categoryName = when (proofType) {
            TransactionProofType.EXPENSE -> "Pengeluaran"
            TransactionProofType.INCOME -> "Pemasukan"
            TransactionProofType.PURCHASE -> "Pembelian"
            TransactionProofType.SALES -> "Penjualan"
            TransactionProofType.SETTLEMENT -> "Pelunasan"
        }

        val journalRef = "JU-2026-${(10000..99999).random()}"

        val trx = AppTransaction(
            companyId = company.id,
            transactionNumber = trxNumber,
            proofType = proofType,
            title = title.trim(),
            categoryName = categoryName,
            partyName = partyName.trim(),
            amount = amount,
            isIncome = isIncome,
            date = date,
            accountCode = accountCode,
            accountName = accountName,
            proofFiles = proofFiles,
            journalRefNumber = journalRef,
            createdBy = _currentRole.value.title,
            notes = notes.trim()
        )

        // Append to transactions map
        val currentMap = _appTransactions.value.toMutableMap()
        val list = currentMap[company.id]?.toMutableList() ?: mutableListOf()
        list.add(0, trx)
        currentMap[company.id] = list
        _appTransactions.value = currentMap

        // Also add matching Journal Entry
        val jrnLines = mutableListOf<JournalLine>()
        if (isIncome) {
            // Debit Bank BCA / Kas, Credit Pendapatan
            jrnLines.add(JournalLine("11120", "Bank BCA Rekening Operasional", debit = amount, credit = 0.0))
            jrnLines.add(JournalLine(accountCode, accountName, debit = 0.0, credit = amount))
        } else {
            // Debit Beban / Persediaan / Utang, Credit Bank BCA
            jrnLines.add(JournalLine(accountCode, accountName, debit = amount, credit = 0.0))
            jrnLines.add(JournalLine("11120", "Bank BCA Rekening Operasional", debit = 0.0, credit = amount))
        }

        val journalEntry = JournalEntry(
            companyId = company.id,
            entryNumber = journalRef,
            date = date,
            referenceType = "TRX_PROOF_${proofType.name}",
            referenceId = trx.id,
            memo = "${trx.categoryName}: ${trx.title} ($partyName)",
            lines = jrnLines,
            isPosted = true
        )

        val jrnMap = _journalEntries.value.toMutableMap()
        val jList = jrnMap[company.id]?.toMutableList() ?: mutableListOf()
        jList.add(0, journalEntry)
        jrnMap[company.id] = jList
        _journalEntries.value = jrnMap

        return trx
    }

    private fun getAccountName(companyId: String, code: String): String {
        val list = _accounts.value[companyId] ?: emptyList()
        return list.find { it.code == code }?.name ?: "Akun Operasional"
    }

    companion object {
        fun generateDefaultCoA(industryType: IndustryType): List<Account> {
            val list = mutableListOf(
                // 1. ASET
                Account("11110", "Kas Kecil (Petty Cash)", AccountCategory.ASSET, NormalBalance.DEBIT, 15000000.0),
                Account("11120", "Bank BCA Rekening Operasional", AccountCategory.ASSET, NormalBalance.DEBIT, 185400000.0),
                Account("11130", "Bank Mandiri Payroll", AccountCategory.ASSET, NormalBalance.DEBIT, 45000000.0),
                Account("11200", "Piutang Usaha (Accounts Receivable)", AccountCategory.ASSET, NormalBalance.DEBIT, 78200000.0),
                Account("11510", "PPN Masukan (11%)", AccountCategory.ASSET, NormalBalance.DEBIT, 5478000.0),
                Account("12100", "Peralatan & Perangkat Kantor", AccountCategory.ASSET, NormalBalance.DEBIT, 35000000.0),
                Account("12190", "Akumulasi Penyusutan Peralatan", AccountCategory.ASSET, NormalBalance.CREDIT, -7000000.0),

                // 2. LIABILITAS
                Account("21100", "Utang Usaha (Accounts Payable)", AccountCategory.LIABILITY, NormalBalance.CREDIT, 52170000.0),
                Account("21210", "Utang PPN Keluaran", AccountCategory.LIABILITY, NormalBalance.CREDIT, 10648000.0),
                Account("21220", "Utang PPh Pasal 21/23 Karyawan", AccountCategory.LIABILITY, NormalBalance.CREDIT, 3850000.0),
                Account("21300", "Beban Akrual / Utang Gaji", AccountCategory.LIABILITY, NormalBalance.CREDIT, 15000000.0),

                // 3. EKUITAS
                Account("31100", "Modal Disetor Pemegang Saham", AccountCategory.EQUITY, NormalBalance.CREDIT, 200000000.0),
                Account("32100", "Saldo Laba Ditahan (Retained Earnings)", AccountCategory.EQUITY, NormalBalance.CREDIT, 56000000.0),

                // 4. PENDAPATAN
                Account("41100", "Pendapatan Penjualan / Layanan Utama", AccountCategory.REVENUE, NormalBalance.CREDIT, 142000000.0),
                Account("41200", "Diskon & Potongan Penjualan", AccountCategory.REVENUE, NormalBalance.DEBIT, 0.0),

                // 6. BEBAN OPERASIONAL
                Account("61100", "Beban Gaji & Tunjangan Karyawan", AccountCategory.EXPENSE, NormalBalance.DEBIT, 38000000.0),
                Account("61200", "Beban Server & Cloud Hosting", AccountCategory.EXPENSE, NormalBalance.DEBIT, 15000000.0),
                Account("61300", "Beban Ekspedisi & Logistik", AccountCategory.EXPENSE, NormalBalance.DEBIT, 4200000.0),
                Account("61400", "Beban Sewa Kantor & Utilitas", AccountCategory.EXPENSE, NormalBalance.DEBIT, 8500000.0),
                Account("61910", "Beban Administrasi Bank", AccountCategory.EXPENSE, NormalBalance.DEBIT, 45000.0)
            )

            when (industryType) {
                IndustryType.TRADING -> {
                    list.add(4, Account("11300", "Persediaan Barang Dagangan", AccountCategory.ASSET, NormalBalance.DEBIT, 48000000.0))
                    list.add(Account("51000", "Harga Pokok Penjualan (HPP)", AccountCategory.EXPENSE, NormalBalance.DEBIT, 62000000.0))
                }
                IndustryType.MANUFACTURING -> {
                    list.add(4, Account("11310", "Persediaan Bahan Baku Utama", AccountCategory.ASSET, NormalBalance.DEBIT, 64000000.0))
                    list.add(5, Account("11320", "Barang Dalam Proses (WIP)", AccountCategory.ASSET, NormalBalance.DEBIT, 22000000.0))
                    list.add(6, Account("11330", "Persediaan Barang Jadi (Finished Goods)", AccountCategory.ASSET, NormalBalance.DEBIT, 85000000.0))
                    list.add(Account("51100", "Biaya Bahan Baku Langsung", AccountCategory.EXPENSE, NormalBalance.DEBIT, 45000000.0))
                    list.add(Account("51200", "Biaya Tenaga Kerja Langsung (BTKL)", AccountCategory.EXPENSE, NormalBalance.DEBIT, 28000000.0))
                    list.add(Account("51300", "Biaya Overhead Pabrik (BOP)", AccountCategory.EXPENSE, NormalBalance.DEBIT, 14000000.0))
                }
                IndustryType.SERVICES -> {
                    list.add(Account("51000", "Beban Pokok Proyek & Jasa Konsultan", AccountCategory.EXPENSE, NormalBalance.DEBIT, 34000000.0))
                }
            }

            return list
        }
    }
}

data class FinancialSummary(
    val kasBank: Double,
    val piutangUsaha: Double,
    val utangUsaha: Double,
    val pendapatan: Double,
    val beban: Double,
    val labaBersih: Double
)
