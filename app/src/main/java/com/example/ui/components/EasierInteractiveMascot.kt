package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EasierInteractiveMascot(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showCsModal by remember { mutableStateOf(false) }

    // Draggable coordinates offset
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    // Breathing / Bouncing Animation
    val infiniteTransition = rememberInfiniteTransition(label = "mascot_idle")
    val bounceY by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    fun callPhone(number: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$number")
        }
        context.startActivity(intent)
    }

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), (offsetY + bounceY).roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
            .clickable { showCsModal = true }
    ) {
        // Glowing Aura Ring
        Box(
            modifier = Modifier
                .size(68.dp)
                .scale(glowScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Purple600.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Mascot Body Container
        Surface(
            shape = CircleShape,
            color = Color.Transparent,
            modifier = Modifier
                .size(56.dp)
                .align(Alignment.Center)
                .shadow(12.dp, CircleShape)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF7C3AED), // Purple 600
                                Color(0xFF4F46E5), // Indigo 600
                                Color(0xFF06B6D4)  // Cyan 500
                            )
                        )
                    )
                    .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Robot / Mascot Eyes & Face
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Eye
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                        // Smile mouth
                        Box(
                            modifier = Modifier
                                .width(8.dp)
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFFFEF08A)) // Yellow smile
                        )
                        // Right Eye
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    // Mini Headset Icon
                    Icon(
                        imageVector = Icons.Default.Headphones,
                        contentDescription = "CS Mascot",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Mini Active Badge Indicator
        Surface(
            shape = CircleShape,
            color = Emerald500,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White),
            modifier = Modifier
                .size(14.dp)
                .align(Alignment.TopEnd)
        ) {}
    }

    // Modal Customer Service (3 Options to 08115666645)
    if (showCsModal) {
        ModalBottomSheet(
            onDismissRequest = { showCsModal = false },
            containerColor = SurfaceCard,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Mascot Avatar
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Purple600, Color(0xFF06B6D4))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Halo! Saya Asisten easier",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Navy900
                )
                Text(
                    text = "Getting easier in one comand. Ada yang bisa kami bantu? Silakan pilih tim spesialis kami:",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Customer Service IT
                CsOptionCard(
                    title = "Customer Service IT",
                    subtitle = "Bantuan teknis aplikasi, kendala bug, sinkronisasi sistem, & instalasi.",
                    phoneNumber = "08115666645",
                    icon = Icons.Outlined.Computer,
                    badgeColor = Purple600,
                    onCall = {
                        showCsModal = false
                        callPhone("08115666645")
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 2. Consultant Service
                CsOptionCard(
                    title = "Consultant Service",
                    subtitle = "Konsultasi pembukuan akuntansi, standar PSAK/SAK EMKM, rekonsiliasi & audit.",
                    phoneNumber = "08115666645",
                    icon = Icons.Outlined.Psychology,
                    badgeColor = Blue600,
                    onCall = {
                        showCsModal = false
                        callPhone("08115666645")
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 3. Layanan Pendamping Bisnis & Pajak
                CsOptionCard(
                    title = "Layanan Konsultasi Pajak & Bisnis",
                    subtitle = "Pendampingan e-Faktur Coretax, PPh 21/23/Unifikasi, dan restrukturisasi kas.",
                    phoneNumber = "08115666645",
                    icon = Icons.Outlined.AccountBalance,
                    badgeColor = Emerald600,
                    onCall = {
                        showCsModal = false
                        callPhone("08115666645")
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun CsOptionCard(
    title: String,
    subtitle: String,
    phoneNumber: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badgeColor: Color,
    onCall: () -> Unit
) {
    Surface(
        color = SurfaceLight,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCall() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(badgeColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = badgeColor,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = phoneNumber,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = badgeColor
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onCall,
                colors = ButtonDefaults.buttonColors(containerColor = badgeColor),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text("Panggil", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
