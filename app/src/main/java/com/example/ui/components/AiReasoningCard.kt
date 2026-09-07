package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.AiReasoningResult
import com.example.util.AiReasoningStatus

@Composable
fun AiReasoningCard(
    result: AiReasoningResult,
    modifier: Modifier = Modifier
) {
    val containerBg = when (result.status) {
        AiReasoningStatus.VALIDATED -> Color(0xFFF0FDF4) // Light emerald
        AiReasoningStatus.UNKNOWN_CONTEXT -> Color(0xFFFFFBEB) // Light amber
        AiReasoningStatus.INCOMPLETE -> Color(0xFFFEF2F2) // Light red
    }
    val borderColor = when (result.status) {
        AiReasoningStatus.VALIDATED -> Color(0xFF86EFAC)
        AiReasoningStatus.UNKNOWN_CONTEXT -> Color(0xFFFDE68A)
        AiReasoningStatus.INCOMPLETE -> Color(0xFFFECACA)
    }
    val headerIcon = when (result.status) {
        AiReasoningStatus.VALIDATED -> Icons.Default.AutoAwesome
        AiReasoningStatus.UNKNOWN_CONTEXT -> Icons.Default.HelpOutline
        AiReasoningStatus.INCOMPLETE -> Icons.Default.WarningAmber
    }
    val iconColor = Color(result.status.badgeColorHex)

    Surface(
        color = containerBg,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(iconColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = headerIcon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Penalaran AI Transaksi",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Navy900
                    )
                }

                Surface(
                    color = iconColor,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = result.status.label,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Explanation Text
            Text(
                text = result.reasoningText,
                fontSize = 12.sp,
                color = TextPrimary,
                lineHeight = 17.sp
            )

            if (result.status == AiReasoningStatus.VALIDATED) {
                Spacer(modifier = Modifier.height(8.dp))
                // Debit & Credit validation display
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.7f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Debit (+)", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        Text(text = result.debitAccount, fontSize = 11.sp, color = Navy900, fontWeight = FontWeight.SemiBold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Kredit (-)", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        Text(text = result.creditAccount, fontSize = 11.sp, color = Navy900, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            if (!result.taxNote.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = Purple700,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Catatan Pajak: ${result.taxNote}",
                        fontSize = 11.sp,
                        color = Purple800,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (!result.recommendation.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "💡 Rekomendasi: ${result.recommendation}",
                    fontSize = 11.sp,
                    color = if (result.status == AiReasoningStatus.VALIDATED) Emerald700 else Amber700,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
