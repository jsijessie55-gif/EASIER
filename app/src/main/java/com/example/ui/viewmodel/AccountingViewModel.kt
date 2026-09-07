package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.AccountingRepository
import com.example.data.repository.FinancialSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class UiNotification(
    val title: String,
    val message: String,
    val isSuccess: Boolean = true
)

class AccountingViewModel(
    private val repository: AccountingRepository = AccountingRepository()
) : ViewModel() {

    val companies: StateFlow<List<Company>> = repository.companies
    val activeCompanyId: StateFlow<String> = repository.activeCompanyId
    val userEmail: StateFlow<String?> = repository.userEmail
    val isAuthenticated: StateFlow<Boolean> = repository.isAuthenticated
    val currentRole: StateFlow<RoleType> = repository.currentRole
    val invoices: StateFlow<Map<String, List<Invoice>>> = repository.invoices
    val journalEntries: StateFlow<Map<String, List<JournalEntry>>> = repository.journalEntries
    val supplierPayments: StateFlow<Map<String, List<SupplierPayment>>> = repository.supplierPayments
    val taxFilings: StateFlow<Map<String, List<TaxFiling>>> = repository.taxFilings
    val accounts: StateFlow<Map<String, List<Account>>> = repository.accounts
    val prePopulatedEFaktur: StateFlow<List<EFakturPrePopulated>> = repository.prePopulatedEFaktur
    val userAccessPasses: StateFlow<List<UserAccessPass>> = repository.userAccessPasses
    val appTransactions: StateFlow<Map<String, List<AppTransaction>>> = repository.appTransactions
    val bankAccounts: StateFlow<Map<String, List<BankAccount>>> = repository.bankAccounts
    val inventoryItems: StateFlow<Map<String, List<InventoryItem>>> = repository.inventoryItems
    val payableItems: StateFlow<Map<String, List<PayableItem>>> = repository.payableItems
    val receivableItems: StateFlow<Map<String, List<ReceivableItem>>> = repository.receivableItems

    private val _selectedTransactionFilter = MutableStateFlow("ALL")
    val selectedTransactionFilter: StateFlow<String> = _selectedTransactionFilter.asStateFlow()

    private val _notification = MutableStateFlow<UiNotification?>(null)
    val notification: StateFlow<UiNotification?> = _notification.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _activeScreen = MutableStateFlow("DASHBOARD")
    val activeScreen: StateFlow<String> = _activeScreen.asStateFlow()

    fun navigateTo(screen: String) {
        _activeScreen.value = screen
    }

    fun openTransactionsFiltered(category: String) {
        _selectedTransactionFilter.value = category
        _activeScreen.value = "TRANSACTIONS"
    }

    fun setTransactionFilter(category: String) {
        _selectedTransactionFilter.value = category
    }

    fun requestUserAccessPass(
        name: String,
        email: String,
        requestedRole: RoleType,
        approverType: String,
        approverEmail: String,
        notes: String
    ) {
        val pass = repository.requestUserAccessPass(name, email, requestedRole, approverType, approverEmail, notes)
        _notification.value = UiNotification(
            title = "Permintaan Pass Terkirim",
            message = "Permintaan pass untuk ${pass.name} (${pass.requestedRole.title}) telah dikirimkan ke email ${pass.approverEmail} untuk ditinjau & disetujui.",
            isSuccess = true
        )
    }

    fun approveUserAccessPass(passId: String) {
        val currentRoleVal = repository.currentRole.value
        val result = repository.approveUserAccessPass(passId, currentRoleVal)
        result.fold(
            onSuccess = { pass ->
                _notification.value = UiNotification(
                    title = "Pass Akses Disetujui",
                    message = "Passcode ${pass.passCode} diterbitkan untuk ${pass.name} (${pass.requestedRole.title}). Pengguna kini memiliki akses aktif.",
                    isSuccess = true
                )
            },
            onFailure = { error ->
                _notification.value = UiNotification(
                    title = "Gagal Menyetujui Pass",
                    message = error.message ?: "Otorisasi tidak mencukupi untuk menyetujui pass.",
                    isSuccess = false
                )
            }
        )
    }

    fun rejectUserAccessPass(passId: String, reason: String = "") {
        val result = repository.rejectUserAccessPass(passId, reason)
        result.fold(
            onSuccess = { pass ->
                _notification.value = UiNotification(
                    title = "Permintaan Pass Ditolak",
                    message = "Permintaan akses untuk ${pass.name} telah ditolak.",
                    isSuccess = false
                )
            },
            onFailure = { error ->
                _notification.value = UiNotification(
                    title = "Gagal",
                    message = error.message ?: "Gagal memproses penolakan pass.",
                    isSuccess = false
                )
            }
        )
    }

    fun categorizeTransaction(
        title: String,
        description: String = "",
        partyName: String = "",
        isIncome: Boolean? = null
    ): com.example.util.TransactionCategoryResult {
        return com.example.util.TransactionCategorizer.categorize(
            title = title,
            description = description,
            partyName = partyName,
            isIncomeExplicit = isIncome
        )
    }

    fun getAiReasoning(
        title: String,
        description: String = "",
        partyName: String = "",
        amount: Double = 0.0
    ): com.example.util.AiReasoningResult {
        return com.example.util.TransactionCategorizer.getAiReasoning(
            title = title,
            description = description,
            partyName = partyName,
            amount = amount
        )
    }

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
    ) {
        val categoryResult = com.example.util.TransactionCategorizer.categorize(
            title = title,
            description = notes,
            partyName = partyName,
            isIncomeExplicit = isIncome
        )
        val finalAccountCode = if (accountCode.isNotBlank() && accountCode != "11100") accountCode else categoryResult.defaultAccountCode
        val finalAccountName = if (accountName.isNotBlank() && accountName != "Kas Utama") accountName else categoryResult.defaultAccountName

        val trx = repository.addTransactionWithProof(
            proofType = proofType,
            title = title,
            partyName = partyName,
            amount = amount,
            isIncome = categoryResult.isIncome,
            date = date,
            accountCode = finalAccountCode,
            accountName = finalAccountName,
            proofFiles = proofFiles,
            notes = if (notes.isNotBlank()) notes else "Kategori Otomatis: ${categoryResult.categoryName} (${categoryResult.matchedKeyword})"
        )
        _notification.value = UiNotification(
            title = "Transaksi & Bukti Tersimpan",
            message = "Transaksi ${trx.transactionNumber} (${trx.proofType.label}) dikategorikan '${categoryResult.categoryName}' dengan ${proofFiles.size} bukti sah.",
            isSuccess = true
        )
    }

    fun getActiveCompany(): Company {
        return repository.getActiveCompany()
    }

    fun formatRupiah(amount: Double): String {
        return repository.formatRupiah(amount)
    }

    fun formatRupiah(amount: Long): String {
        return repository.formatRupiah(amount.toDouble())
    }

    fun formatRupiah(amount: Number): String {
        return repository.formatRupiah(amount.toDouble())
    }

    fun switchCompany(companyId: String) {
        repository.switchCompany(companyId)
        val comp = repository.getActiveCompany()
        _notification.value = UiNotification(
            title = "Beralih Perusahaan",
            message = "Sekarang mengelola data ${comp.name} (${if (comp.isPkp) "PKP" else "Non-PKP"})",
            isSuccess = true
        )
    }

    fun switchRole(role: RoleType) {
        repository.switchRole(role)
        _notification.value = UiNotification(
            title = "Mode Pengguna Diubah",
            message = "Hak akses aktif: ${role.title}",
            isSuccess = true
        )
    }

    fun authenticateWithEmail(
        email: String,
        registerCompany: Boolean = false,
        companyName: String = "",
        entityType: EntityType = EntityType.PT,
        industryType: IndustryType = IndustryType.TRADING,
        npwp16: String? = null,
        address: String = "",
        role: RoleType = RoleType.OWNER
    ) {
        val newCompany = if (registerCompany && companyName.isNotBlank()) {
            val cleanNpwp = npwp16?.filter { it.isDigit() }?.takeIf { it.isNotBlank() }
            Company(
                id = "comp_${System.currentTimeMillis()}",
                name = companyName.trim(),
                entityType = entityType,
                industryType = industryType,
                npwp16 = cleanNpwp,
                isPkp = !cleanNpwp.isNullOrBlank(),
                address = address.ifBlank { "Jakarta, Indonesia" }
            )
        } else null

        repository.authenticateWithEmail(email, newCompany, role)
        _activeScreen.value = "DASHBOARD"
        _notification.value = UiNotification(
            title = "Selamat Datang di easier",
            message = "Masuk sebagai ${role.title}: ${email.trim()}. Getting easier in one comand.",
            isSuccess = true
        )
    }

    fun logout() {
        repository.logout()
        _notification.value = UiNotification(
            title = "Keluar Akun",
            message = "Sesi perusahaan telah ditutup dengan aman.",
            isSuccess = true
        )
    }

    fun clearNotification() {
        _notification.value = null
    }

    fun addCompany(
        name: String,
        entityType: EntityType,
        industryType: IndustryType,
        npwp16: String?,
        address: String
    ) {
        val newComp = repository.addCompany(name, entityType, industryType, npwp16, address)
        _notification.value = UiNotification(
            title = "Perusahaan Berhasil Dibuat",
            message = "${newComp.name} siap digunakan dengan template ${industryType.label} (PSAK).",
            isSuccess = true
        )
    }

    fun checkDuplicateInvoice(invoiceNumber: String, vendorName: String): Invoice? {
        return repository.findDuplicateInvoice(invoiceNumber, vendorName)
    }

    fun simulateOcrScan(
        vendorName: String,
        vendorNpwp: String?,
        invoiceNumber: String,
        date: String,
        subtotal: Double,
        isPkp: Boolean,
        categoryCode: String,
        categoryName: String,
        description: String,
        onComplete: (Boolean) -> Unit = {}
    ) {
        _isScanning.value = true
        viewModelScope.launch {
            kotlinx.coroutines.delay(1200)
            val result = repository.addInvoiceFromOcr(
                vendorName = vendorName,
                vendorNpwp = vendorNpwp,
                invoiceNumber = invoiceNumber,
                date = date,
                subtotal = subtotal,
                isPkp = isPkp,
                categoryAccountCode = categoryCode,
                categoryAccountName = categoryName,
                lineDescription = description
            )
            _isScanning.value = false

            result.fold(
                onSuccess = { invoice ->
                    _notification.value = UiNotification(
                        title = "OCR Berhasil Diekstraksi",
                        message = "Invoice #${invoice.invoiceNumber} (${invoice.vendorName}) tersimpan sebagai draf jurnal.",
                        isSuccess = true
                    )
                    onComplete(true)
                },
                onFailure = { exception ->
                    _notification.value = UiNotification(
                        title = "Dokumen DITOLAK (Double Scanning)",
                        message = exception.message ?: "Terdeteksi faktur duplikat pada sistem.",
                        isSuccess = false
                    )
                    onComplete(false)
                }
            )
        }
    }

    fun deleteJournalEntry(journalId: String) {
        val currentRoleVal = repository.currentRole.value
        val result = repository.deleteJournalEntry(journalId, currentRoleVal)
        result.fold(
            onSuccess = { journal ->
                _notification.value = UiNotification(
                    title = "Jurnal Berhasil Dihapus",
                    message = "Entri ${journal.entryNumber} telah dihapus dari Buku Besar oleh ${currentRoleVal.title}.",
                    isSuccess = true
                )
            },
            onFailure = { error ->
                _notification.value = UiNotification(
                    title = "Gagal Menghapus Jurnal",
                    message = error.message ?: "Otorisasi tidak mencukupi untuk menghapus jurnal.",
                    isSuccess = false
                )
            }
        )
    }

    fun deleteInvoiceDraft(invoiceId: String) {
        repository.deleteInvoiceDraft(invoiceId)
        _notification.value = UiNotification(
            title = "Draf Dihapus",
            message = "Draf invoice hasil scan telah dibersihkan.",
            isSuccess = true
        )
    }

    fun postInvoiceToLedger(invoiceId: String) {
        repository.postInvoiceToLedger(invoiceId)
        _notification.value = UiNotification(
            title = "Jurnal Terposting",
            message = "Transaksi telah dicatat ke Buku Besar secara seimbang (Double-Entry).",
            isSuccess = true
        )
    }

    fun executeSupplierPayment(
        invoiceId: String,
        bankName: String,
        accountNumber: String,
        accountHolder: String
    ) {
        val payment = repository.executeSupplierPayment(invoiceId, bankName, accountNumber, accountHolder)
        _notification.value = UiNotification(
            title = "Pembayaran BI-FAST Berhasil",
            message = "Transfer ${formatRupiah(payment.amount)} ke ${payment.vendorName} sukses. Rekonsiliasi jurnal otomatis.",
            isSuccess = true
        )
    }

    fun generateTaxBilling(taxFilingId: String) {
        val billingId = repository.generateTaxBilling(taxFilingId)
        _notification.value = UiNotification(
            title = "Kode Billing Terbit",
            message = "ID Billing MPN G3: $billingId berhasil dibuat melalui gateway DJP.",
            isSuccess = true
        )
    }

    fun payTaxBilling(taxFilingId: String) {
        repository.payTaxBilling(taxFilingId)
        _notification.value = UiNotification(
            title = "Pajak Berhasil Disetor",
            message = "NTPN resmi telah diterbitkan & jurnal pelunasan utang pajak selesai dibukukan.",
            isSuccess = true
        )
    }

    fun fileToCoretax(taxFilingId: String) {
        val bpeNumber = repository.fileToCoretax(taxFilingId)
        _notification.value = UiNotification(
            title = "SPT Berhasil Dilaporkan (BPE Terbit)",
            message = "Nomor Tanda Terima Resmi: $bpeNumber tersimpan di vault Coretax.",
            isSuccess = true
        )
    }

    fun getFinancialSummary(): FinancialSummary {
        return repository.getFinancialSummary()
    }

    fun addBankAccount(
        bankName: String,
        cardTypes: Set<CardType>,
        accountNumber: String,
        accountHolder: String = "PT Sinergi Abadi Digital",
        branchOffice: String = "Kantor Cabang Utama"
    ) {
        val company = getActiveCompany()
        val created = repository.addBankAccount(company.id, bankName, cardTypes, accountNumber, accountHolder, branchOffice)
        val cardTypesStr = created.cardTypes.joinToString("/") { it.label }
        _notification.value = UiNotification(
            title = "Akun Bank Ditambahkan",
            message = "Akun ${created.bankName} ($cardTypesStr) no. ${created.accountNumber} berhasil didaftarkan.",
            isSuccess = true
        )
    }

    fun deleteBankAccount(accountId: String) {
        val company = getActiveCompany()
        repository.deleteBankAccount(company.id, accountId)
        _notification.value = UiNotification(
            title = "Akun Bank Dihapus",
            message = "Akun rekening telah dihapus dari sistem.",
            isSuccess = true
        )
    }

    fun setPrimaryBankAccount(accountId: String) {
        val company = getActiveCompany()
        repository.setPrimaryBankAccount(company.id, accountId)
        _notification.value = UiNotification(
            title = "Rekening Utama Diubah",
            message = "Rekening utama operasional berhasil diperbarui.",
            isSuccess = true
        )
    }

    fun getDuePaymentItems(): List<DuePaymentItem> {
        val company = getActiveCompany()
        return repository.getDuePaymentItems(company.id)
    }
}
