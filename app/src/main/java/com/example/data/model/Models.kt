package com.example.data.model

import java.util.UUID

enum class EntityType(val label: String) {
    PT("Perseroan Terbatas (PT)"),
    CV("Persekutuan Komanditer (CV)"),
    UD("Usaha Dagang (UD)"),
    FIRMA("Firma"),
    INDIVIDUAL("Orang Pribadi (WPOP)")
}

enum class IndustryType(val label: String, val defaultTemplateCode: String) {
    SERVICES("Jasa & Konsultan", "PSAK_SERVICES_STD"),
    TRADING("Perdagangan / Retail", "PSAK_TRADING_PERPETUAL"),
    MANUFACTURING("Manufaktur / Pabrikasi", "PSAK_MFG_STD")
}

enum class RoleType(val code: String, val title: String, val badgeColorHex: Long, val description: String) {
    OWNER("OWNER", "Owner", 0xFF059669, "Akses Penuh: Kelola Rekening Bank, Notifikasi Jatuh Tempo & Eksekutif"),
    MANAGER("MANAGER", "Manajer", 0xFF2563EB, "Operasional: Kelola/Hapus Rekening Bank & Notifikasi Jatuh Tempo"),
    STAFF("STAFF", "Admin", 0xFFD97706, "Input Transaksi, Upload Bukti & Akses Tampilan Bank"),
    EXTERNAL("EXTERNAL", "External", 0xFF7C3AED, "Audit & Pemeriksaan: Total Kas, Pemasukan, Pengeluaran & Laporan Keuangan");

    // Compatibility aliases
    companion object {
        val SUPER_ADMIN = OWNER
        val COMPANY_ADMIN = MANAGER
        val STAFF_ACCOUNTING = STAFF
        val EXTERNAL_AUDITOR = EXTERNAL
        val FIELD_STAFF = STAFF
    }

    fun canDeleteJournal(): Boolean = this == OWNER || this == MANAGER
    fun canPostJournal(): Boolean = this == OWNER || this == MANAGER || this == STAFF
    fun canApprovePass(): Boolean = this == OWNER || this == MANAGER
    fun canInputTransaction(): Boolean = this != EXTERNAL
    fun canManageBank(): Boolean = this == OWNER || this == MANAGER
    fun shouldShowBank(): Boolean = this == OWNER || this == MANAGER
    fun shouldShowDueNotifications(): Boolean = this != EXTERNAL
    fun canScan(): Boolean = this != EXTERNAL
    fun canDoPayments(): Boolean = this != EXTERNAL
    fun canViewFinancialReports(): Boolean = this == OWNER || this == MANAGER || this == EXTERNAL
    fun canViewCashFlow(): Boolean = this == OWNER || this == MANAGER || this == EXTERNAL
    fun canViewStockReport(): Boolean = true
    fun isExternal(): Boolean = this == EXTERNAL
    fun isAdminOrStaff(): Boolean = this == STAFF
    fun isOwnerOrManager(): Boolean = this == OWNER || this == MANAGER
}

enum class PassStatus(val label: String, val colorHex: Long) {
    PENDING("Menunggu Persetujuan", 0xFFD97706),
    APPROVED("Disetujui / Aktif", 0xFF059669),
    REJECTED("Ditolak", 0xFFDC2626)
}

data class UserAccessPass(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val email: String,
    val requestedRole: RoleType,
    val approverEmail: String,
    val approverType: String, // "Owner (pteduworksasiaagroindustries@gmail.com)" or "Manajer (finance@sinergiabadi.co.id)"
    val passCode: String? = null,
    val status: PassStatus = PassStatus.PENDING,
    val requestDate: String,
    val approvedDate: String? = null,
    val notes: String = ""
)

enum class TransactionProofType(val label: String, val description: String) {
    EXPENSE("Bukti Pengeluaran", "Invoice/Bon & Bukti Pembayaran"),
    INCOME("Bukti Pemasukan", "Invoice/Bon & Bukti Penerimaan"),
    PURCHASE("Pembelian", "Invoice/Bon & Bukti Potong PPh (Opsional)"),
    SALES("Penjualan", "Invoice/Bon & Bukti Potong PPh (Opsional)"),
    SETTLEMENT("Pelunasan Pembelian & Penjualan", "Bukti Pembayaran")
}

data class ProofFileItem(
    val id: String = UUID.randomUUID().toString(),
    val stepName: String, // "Invoice / Bon", "Bukti Pembayaran", "Bukti Penerimaan", "Bukti Potong PPh (Opsional)"
    val fileName: String,
    val fileFormat: String, // "PDF" or "JPEG"
    val fileSize: String,
    val uploadTimestamp: String,
    val isOptional: Boolean = false,
    val previewNotes: String = "Dokumen terverifikasi"
)

data class AppTransaction(
    val id: String = UUID.randomUUID().toString(),
    val companyId: String,
    val transactionNumber: String,
    val proofType: TransactionProofType,
    val title: String,
    val categoryName: String, // "Pengeluaran", "Pemasukan", "Pembelian", "Penjualan", "Pelunasan"
    val partyName: String, // Vendor / Klien / Rekanan
    val amount: Double,
    val isIncome: Boolean,
    val date: String,
    val accountCode: String,
    val accountName: String,
    val proofFiles: List<ProofFileItem>,
    val journalRefNumber: String,
    val createdBy: String = "Staff",
    val approverEmail: String? = null,
    val notes: String = ""
)

data class Company(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val entityType: EntityType,
    val industryType: IndustryType,
    val npwp16: String? = null,
    val isPkp: Boolean = !npwp16.isNullOrBlank(),
    val address: String = "Jakarta, Indonesia",
    val baseCurrency: String = "IDR",
    val fiscalYearStartMonth: Int = 1
)

enum class AccountCategory(val label: String) {
    ASSET("Aset"),
    LIABILITY("Liabilitas"),
    EQUITY("Ekuitas"),
    REVENUE("Pendapatan"),
    EXPENSE("Beban & HPP")
}

enum class NormalBalance {
    DEBIT,
    CREDIT
}

data class Account(
    val code: String,
    val name: String,
    val category: AccountCategory,
    val normalBalance: NormalBalance,
    val balance: Double = 0.0,
    val isHeader: Boolean = false,
    val parentCode: String? = null
)

enum class InvoiceStatus(val label: String) {
    DRAFT("Draf OCR"),
    POSTED("Terposting"),
    PAID("Lunas"),
    CANCELLED("Dibatalkan")
}

data class InvoiceItem(
    val description: String,
    val quantity: Double,
    val unitPrice: Double,
    val totalPrice: Double,
    val accountCode: String
)

data class Invoice(
    val id: String = UUID.randomUUID().toString(),
    val companyId: String,
    val invoiceNumber: String,
    val vendorName: String,
    val vendorNpwp: String? = null,
    val invoiceDate: String,
    val dueDate: String,
    val subtotal: Double,
    val taxAmount: Double, // PPN 11%
    val totalAmount: Double,
    val status: InvoiceStatus = InvoiceStatus.DRAFT,
    val ocrConfidence: Double = 98.4,
    val lineItems: List<InvoiceItem> = emptyList(),
    val scannedImageUrl: String? = null
)

data class JournalLine(
    val accountCode: String,
    val accountName: String,
    val debit: Double = 0.0,
    val credit: Double = 0.0
)

data class JournalEntry(
    val id: String = UUID.randomUUID().toString(),
    val companyId: String,
    val entryNumber: String,
    val date: String,
    val referenceType: String, // OCR_INVOICE, OPEN_BANKING, TAX_FILING, MANUAL
    val referenceId: String? = null,
    val memo: String,
    val lines: List<JournalLine>,
    val isPosted: Boolean = true
)

enum class PaymentStatus(val label: String) {
    PENDING("Menunggu Otorisasi"),
    SUCCESS("Berhasil / Settled"),
    FAILED("Gagal")
}

data class SupplierPayment(
    val id: String = UUID.randomUUID().toString(),
    val companyId: String,
    val invoiceId: String,
    val vendorName: String,
    val invoiceNumber: String,
    val bankName: String,
    val accountNumber: String,
    val accountHolder: String,
    val amount: Double,
    val adminFee: Double = 2500.0, // Bi-Fast fee
    val bankRefNumber: String = "BIFAST-${System.currentTimeMillis().toString().takeLast(8)}",
    val paymentStatus: PaymentStatus = PaymentStatus.SUCCESS,
    val date: String
)

enum class TaxType(val label: String, val kapKjs: String) {
    PPN_1111("SPT Masa PPN 1111", "411211 / 100"),
    PPH_UNIFIKASI("PPh Unifikasi (21/23/4(2))", "411124 / 104"),
    PPH_FINAL_UMKM("PPh Final PP 55 (0.5%)", "411128 / 420")
}

enum class TaxFilingStatus(val label: String) {
    DRAFT("Draf SPT"),
    BILLED("Kode Billing Aktif"),
    PAID("Billing Terbayar (NTPN)"),
    FILED("Dilaporkan (BPE Sah)")
}

data class TaxFiling(
    val id: String = UUID.randomUUID().toString(),
    val companyId: String,
    val taxType: TaxType,
    val period: String, // "Juli 2026", "Agustus 2026"
    val taxYear: Int = 2026,
    val idBilling: String? = null,
    val ntpn: String? = null,
    val bpeNumber: String? = null,
    val totalTax: Double,
    val status: TaxFilingStatus = TaxFilingStatus.DRAFT,
    val submittedAt: String? = null
)

data class EFakturPrePopulated(
    val nsfp: String,
    val vendorName: String,
    val vendorNpwp: String,
    val date: String,
    val dpp: Double,
    val ppn: Double,
    val isMatched: Boolean = true
)

data class CoretaxCredential(
    val companyId: String,
    val npwp16: String,
    val certExpiry: String = "15 Des 2027",
    val keyVersion: String = "KMS-HSM-v2.1",
    val isVerified: Boolean = true,
    val nitku: String = "000000"
)

enum class CardType(val label: String) {
    DEBIT("Debit"),
    CREDIT("Kredit")
}

data class BankAccount(
    val id: String = UUID.randomUUID().toString(),
    val companyId: String,
    val bankName: String,
    val cardTypes: Set<CardType> = setOf(CardType.DEBIT),
    val accountNumber: String,
    val accountHolder: String = "PT Sinergi Abadi Digital",
    val isPrimary: Boolean = false,
    val balance: Double = 0.0,
    val branchOffice: String = "KC Sudirman Jakarta"
)

enum class DueCategory(val label: String, val badgeColorHex: Long) {
    RECEIVABLE("Piutang Usaha", 0xFF2563EB), // Blue
    PAYABLE("Hutang Usaha", 0xFFDC2626),    // Red/Crimson
    TAX("Biaya Pajak (PPN/PPh)", 0xFFD97706) // Amber
}

data class DuePaymentItem(
    val id: String = UUID.randomUUID().toString(),
    val category: DueCategory,
    val title: String,
    val partyOrDescription: String,
    val referenceNumber: String,
    val amount: Double,
    val dueDate: String,
    val daysRemaining: Int, // 0 = Hari ini, 1 = Besok, etc.
    val targetScreen: String, // "INVOICES", "PAYMENT", "CORETAX"
    val actionLabel: String
)

data class InventoryItem(
    val id: String = UUID.randomUUID().toString(),
    val companyId: String,
    val sku: String,
    val name: String,
    val category: String,
    val currentStock: Int,
    val minimumStock: Int,
    val unit: String = "Pcs",
    val costPrice: Double, // HPP
    val sellingPrice: Double, // Harga Jual
    val lastUpdated: String = "26 Agu 2026"
) {
    val totalValue: Double get() = currentStock * costPrice
    val isLowStock: Boolean get() = currentStock <= minimumStock
}

data class PayableItem(
    val id: String = UUID.randomUUID().toString(),
    val companyId: String,
    val supplierName: String,
    val invoiceNumber: String,
    val invoiceDate: String,
    val dueDate: String,
    val totalAmount: Double,
    val paidAmount: Double,
    val status: String // "JATUH_TEMPO", "MENUNGGU_JATUH_TEMPO", "LUNAS"
) {
    val remainingAmount: Double get() = (totalAmount - paidAmount).coerceAtLeast(0.0)
    val isPaidOff: Boolean get() = remainingAmount <= 0.0
}

data class ReceivableItem(
    val id: String = UUID.randomUUID().toString(),
    val companyId: String,
    val customerOrParty: String,
    val invoiceNumber: String,
    val invoiceDate: String,
    val dueDate: String,
    val totalAmount: Double,
    val receivedAmount: Double,
    val status: String // "JATUH_TEMPO", "MENUNGGU_JATUH_TEMPO", "LUNAS"
) {
    val remainingAmount: Double get() = (totalAmount - receivedAmount).coerceAtLeast(0.0)
    val isPaidOff: Boolean get() = remainingAmount <= 0.0
}


