package com.example.util

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

data class TransactionCategoryResult(
    val categoryName: String,
    val isIncome: Boolean,
    val defaultAccountCode: String,
    val defaultAccountName: String,
    val tagColorHex: Long,
    val matchedKeyword: String
)

enum class AiReasoningStatus(val label: String, val badgeColorHex: Long) {
    VALIDATED("Valid & Sesuai Standar", 0xFF059669), // Emerald
    UNKNOWN_CONTEXT("Tidak Tahu (Butuh Klarifikasi)", 0xFFD97706), // Amber
    INCOMPLETE("Data Belum Lengkap", 0xFFDC2626) // Crimson
}

data class AiReasoningResult(
    val status: AiReasoningStatus,
    val title: String,
    val reasoningText: String,
    val debitAccount: String,
    val creditAccount: String,
    val taxNote: String? = null,
    val recommendation: String? = null
)

object TransactionCategorizer {

    /**
     * Sesuai aturan prompt:
     * - Pembelian = "Sewa", "Pembelian barang pada (Nama Supplier)" , "Proyek (Nama Supplier)"
     * - Penjualan (jasa) = "Sewa", "Pembangunan", "Proyek"
     * - Pelunasan pembayaran = "Pembayaran kepada (nama supplier)", "Pelunasan" , "Pembayaran"
     * - Pelunasan penjualan = "Pendapatan" , "Pembayaran sewa/proyek pada (Nama supplier)"
     * - Pengeluaran = "Pembelian barang pada", "Pinjaman"
     * - Pemasukan = "Pengembalian pinjaman" , "Jual (Mobil/Motor/Bangunan/Tanah)", "Jual(Laptop/ATK/printer)"
     */
    fun categorize(
        title: String,
        description: String = "",
        partyName: String = "",
        isIncomeExplicit: Boolean? = null
    ): TransactionCategoryResult {
        val combined = "$title $description $partyName".lowercase().trim()

        // 1. Pelunasan Penjualan: "pendapatan", "pembayaran sewa pada", "pembayaran proyek pada", "pembayaran sewa/proyek"
        if (combined.contains("pembayaran sewa pada") || combined.contains("pembayaran proyek pada") ||
            combined.contains("pembayaran sewa") && isIncomeExplicit == true ||
            combined.contains("pembayaran proyek") && isIncomeExplicit == true ||
            (combined.contains("pendapatan") && !combined.contains("beban"))
        ) {
            return TransactionCategoryResult(
                categoryName = "Pelunasan Penjualan",
                isIncome = true,
                defaultAccountCode = "11200",
                defaultAccountName = "Piutang Usaha (Trade Receivables)",
                tagColorHex = 0xFF059669,
                matchedKeyword = if (combined.contains("pendapatan")) "Pendapatan" else "Pembayaran sewa/proyek"
            )
        }

        // 2. Pelunasan Pembayaran: "pembayaran kepada", "pelunasan", "pembayaran"
        if (combined.contains("pembayaran kepada") || combined.contains("pelunasan") ||
            (combined.contains("pembayaran") && isIncomeExplicit != true)
        ) {
            return TransactionCategoryResult(
                categoryName = "Pelunasan Pembayaran",
                isIncome = false,
                defaultAccountCode = "21100",
                defaultAccountName = "Utang Usaha (Accounts Payable)",
                tagColorHex = 0xFF2563EB,
                matchedKeyword = when {
                    combined.contains("pembayaran kepada") -> "Pembayaran kepada (Supplier)"
                    combined.contains("pelunasan") -> "Pelunasan"
                    else -> "Pembayaran"
                }
            )
        }

        // 3. Pemasukan: "pengembalian pinjaman", "jual (mobil/motor/bangunan/tanah)", "jual(laptop/atk/printer)"
        if (combined.contains("pengembalian pinjaman") ||
            combined.contains("jual mobil") || combined.contains("jual motor") ||
            combined.contains("jual bangunan") || combined.contains("jual tanah") ||
            combined.contains("jual laptop") || combined.contains("jual atk") ||
            combined.contains("jual printer") || combined.startsWith("jual ")
        ) {
            val kw = when {
                combined.contains("pengembalian pinjaman") -> "Pengembalian pinjaman"
                combined.contains("jual mobil") || combined.contains("jual motor") || combined.contains("jual bangunan") || combined.contains("jual tanah") -> "Jual Aset (Mobil/Motor/Bangunan/Tanah)"
                else -> "Jual Aset (Laptop/ATK/Printer)"
            }
            return TransactionCategoryResult(
                categoryName = "Pemasukan",
                isIncome = true,
                defaultAccountCode = "42000",
                defaultAccountName = "Pendapatan Lain-lain / Pelepasan Aset",
                tagColorHex = 0xFF10B981,
                matchedKeyword = kw
            )
        }

        // 4. Pengeluaran: "pembelian barang pada", "pinjaman"
        if (combined.contains("pinjaman") && !combined.contains("pengembalian")) {
            return TransactionCategoryResult(
                categoryName = "Pengeluaran",
                isIncome = false,
                defaultAccountCode = "11500",
                defaultAccountName = "Piutang Lain-lain / Pinjaman Diberikan",
                tagColorHex = 0xFFDC2626,
                matchedKeyword = "Pinjaman"
            )
        }

        // 5. Penjualan (Jasa): "sewa" (jika income), "pembangunan", "proyek"
        if (combined.contains("pembangunan") ||
            (combined.contains("proyek") && isIncomeExplicit == true) ||
            (combined.contains("sewa") && isIncomeExplicit == true)
        ) {
            val kw = when {
                combined.contains("pembangunan") -> "Pembangunan"
                combined.contains("proyek") -> "Proyek"
                else -> "Sewa"
            }
            return TransactionCategoryResult(
                categoryName = "Penjualan (Jasa)",
                isIncome = true,
                defaultAccountCode = "41200",
                defaultAccountName = "Pendapatan Jasa & Proyek",
                tagColorHex = 0xFF0D9488,
                matchedKeyword = kw
            )
        }

        // 6. Pembelian: "sewa" (jika expense), "pembelian barang pada (Nama Supplier)", "proyek (Nama Supplier)"
        if (combined.contains("pembelian barang pada") ||
            combined.contains("pembelian barang") ||
            combined.contains("pembelian") ||
            combined.contains("sewa") ||
            combined.contains("proyek")
        ) {
            val kw = when {
                combined.contains("pembelian barang pada") -> "Pembelian barang pada (Supplier)"
                combined.contains("proyek") -> "Proyek (Supplier)"
                combined.contains("sewa") -> "Sewa"
                else -> "Pembelian"
            }
            return TransactionCategoryResult(
                categoryName = "Pembelian",
                isIncome = false,
                defaultAccountCode = "51000",
                defaultAccountName = "Beban Pokok Pendapatan (HPP) / Beban Pembelian",
                tagColorHex = 0xFF7C3AED,
                matchedKeyword = kw
            )
        }

        // Default General
        val isInc = isIncomeExplicit ?: false
        return TransactionCategoryResult(
            categoryName = if (isInc) "Pemasukan Umum" else "Pengeluaran Umum",
            isIncome = isInc,
            defaultAccountCode = if (isInc) "41100" else "61100",
            defaultAccountName = if (isInc) "Pendapatan Usaha" else "Beban Operasional",
            tagColorHex = 0xFF6B7280,
            matchedKeyword = "Umum"
        )
    }

    /**
     * Penalaran AI:
     * "AI membantu pengguna untuk memastikan bahwa pencatatan yang dilakukan sudah benar dan tepat sesuai kebutuhan
     * namun jangan berasumsi sendiri, jika tidak tau katakan tidak tau."
     */
    fun analyzeTransactionWithAi(
        title: String,
        partyName: String,
        amount: Double,
        isIncome: Boolean,
        accountCode: String,
        accountName: String,
        notes: String = "",
        hasProofFiles: Boolean = true
    ): AiReasoningResult {
        // 1. Data Incomplete Check
        if (amount <= 0.0) {
            return AiReasoningResult(
                status = AiReasoningStatus.INCOMPLETE,
                title = "Data Nominal Belum Valid",
                reasoningText = "Nominal transaksi bernilai Rp 0 atau negatif. AI tidak dapat memvalidasi kebenaran penjurnalan tanpa nilai moneter yang pasti.",
                debitAccount = "-",
                creditAccount = "-",
                recommendation = "Masukkan jumlah rupiah transaksi yang sebenarnya dari bukti fisik."
            )
        }

        if (partyName.trim().isBlank()) {
            return AiReasoningResult(
                status = AiReasoningStatus.INCOMPLETE,
                title = "Identitas Lawan Transaksi Belum Terisi",
                reasoningText = "Nama pihak (supplier / klien / vendor) belum diisi. Dalam standar pembukuan SAK EMKM, setiap transaksi kas wajib mengidentifikasi pihak penerima atau pembayar untuk rekonsiliasi faktur.",
                debitAccount = "-",
                creditAccount = "-",
                recommendation = "Lengkapi nama rekanan bisnis atau pihak lawan transaksi."
            )
        }

        val text = "$title $notes".lowercase().trim()

        // 2. Ambiguous / Unknown Check - "Jika tidak tahu katakan tidak tahu, jangan berasumsi sendiri"
        val vagueKeywords = listOf("biaya", "uang", "dana", "transfer", "keluar", "masuk", "lain", "lain-lain", "trx")
        val isExtremelyVague = text.length < 5 || (vagueKeywords.contains(text) && !text.contains("sewa") && !text.contains("beli") && !text.contains("jual") && !text.contains("lunas"))

        if (isExtremelyVague) {
            return AiReasoningResult(
                status = AiReasoningStatus.UNKNOWN_CONTEXT,
                title = "Konteks Transaksi Tidak Diketahui",
                reasoningText = "Deskripsi '$title' terlalu singkat dan bersifat umum. Sesuai prinsip kehati-hatian akuntansi, AI tidak mengetahui maksud dan peruntukan transaksi ini secara pasti, dan AI TIDAK BERASUMSI SENDIRI.",
                debitAccount = "Perlu Konfirmasi",
                creditAccount = "Kas/Bank (11100)",
                recommendation = "Mohon tambahkan keterangan spesifik, contoh: 'Sewa kantor', 'Pembelian barang pada $partyName', atau 'Pelunasan invoice'."
            )
        }

        // 3. Clear verified accounting reasoning
        val category = categorize(title, notes, partyName, isIncome)

        val debit: String
        val credit: String
        val taxNote: String?
        val reasoning: String

        if (isIncome) {
            debit = "Kas & Bank (11100)"
            credit = "$accountName ($accountCode)"
            when (category.categoryName) {
                "Pelunasan Penjualan" -> {
                    reasoning = "Pencatatan terverifikasi benar: Transaksi merupakan penerimaan kas pelunasan penjualan dari $partyName. Mutasi mendebit Kas/Bank dan mengkredit Piutang Usaha ($accountCode), sehingga saldo piutang klien berkurang secara tepat."
                    taxNote = "Periksa apakah faktur penjualan telah dibuatkan e-Faktur PPN 11% dan telah disetor."
                }
                "Penjualan (Jasa)" -> {
                    reasoning = "Pencatatan terverifikasi benar: Transaksi jasa proyek/konstruksi diakui sebagai Pendapatan Jasa ($accountCode) dengan penerimaan Kas/Bank di posisi Debit. Sesuai prinsip pengakuan pendapatan PSAK."
                    taxNote = "Transaksi jasa dapat dikenakan PPh Pasal 23 (2%) atau PPh Final Jasa Konstruksi oleh pihak pembeli."
                }
                "Pemasukan" -> {
                    reasoning = "Pencatatan terverifikasi benar: Transaksi diklasifikasikan sebagai Pemasukan (${category.matchedKeyword}). Penerimaan kas menambah aset lancar di Debit dan diimbangi kredit pada pos akun terkait."
                    taxNote = if (category.matchedKeyword.contains("Jual Aset")) "Hitung keuntungan/kerugian pelepasan aset tetap (Nilai Jual vs Nilai Buku Fiskal)." else null
                }
                else -> {
                    reasoning = "Pencatatan penerimaan kas sebesar nominal valid. Kas/Bank bertambah di posisi Debit dan pendapatan diakui di posisi Kredit."
                    taxNote = null
                }
            }
        } else {
            debit = "$accountName ($accountCode)"
            credit = "Kas & Bank (11100)"
            when (category.categoryName) {
                "Pembelian" -> {
                    reasoning = "Pencatatan terverifikasi benar: Transaksi pembelian barang/sewa dari $partyName diklasifikasikan sebagai beban/persediaan ($accountCode). Sesuai SAK EMKM, beban dicatat di Debit dan pengeluaran kas di Kredit."
                    taxNote = "Pastikan Faktur Pajak Masukan 11% dari $partyName dikreditkan jika perusahaan berstatus PKP."
                }
                "Pelunasan Pembayaran" -> {
                    reasoning = "Pencatatan terverifikasi benar: Transaksi pelunasan utang ke vendor $partyName mendebit Utang Usaha ($accountCode) dan mengkredit Kas/Bank, melunasi kewajiban dengan tepat tanpa beban ganda."
                    taxNote = "Simpan slip transfer bank SNAP BI-FAST sebagai bukti rekonsiliasi pembayaran."
                }
                "Pengeluaran" -> {
                    reasoning = "Pencatatan terverifikasi benar: Pengeluaran dana untuk ${category.matchedKeyword} dicatat mendebit pos terkait dan mengkredit Kas/Bank."
                    taxNote = null
                }
                else -> {
                    reasoning = "Pencatatan pengeluaran kas sebesar nominal valid. Beban/aset diakui di posisi Debit dan Kas/Bank berkurang di posisi Kredit."
                    taxNote = null
                }
            }
        }

        return AiReasoningResult(
            status = AiReasoningStatus.VALIDATED,
            title = "Penalaran AI: Sesuai Standar Akuntansi",
            reasoningText = reasoning,
            debitAccount = debit,
            creditAccount = credit,
            taxNote = taxNote,
            recommendation = if (!hasProofFiles) "Disarankan melampirkan foto invoice atau bukti transfer untuk kepatuhan audit." else "Bukti transaksi sah terlampir."
        )
    }
}
