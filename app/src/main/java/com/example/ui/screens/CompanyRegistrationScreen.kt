package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.EntityType
import com.example.data.model.IndustryType
import com.example.data.model.PassStatus
import com.example.data.model.RoleType
import com.example.data.model.UserAccessPass
import com.example.ui.theme.*
import com.example.ui.viewmodel.AccountingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyRegistrationScreen(
    viewModel: AccountingViewModel,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val userAccessPasses by viewModel.userAccessPasses.collectAsState()

    var loginMode by remember { mutableStateOf("MAGIC_LINK") } // MAGIC_LINK or PASSCODE
    var email by remember { mutableStateOf("pteduworksasiaagroindustries@gmail.com") }
    var password by remember { mutableStateOf("12345678") }
    var enteredPassCode by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passCodeError by remember { mutableStateOf<String?>(null) }

    var selectedRole by remember { mutableStateOf(RoleType.OWNER) }

    var isRegisterNewCompany by remember { mutableStateOf(false) }
    var companyName by remember { mutableStateOf("PT Eduworks Asia Agro Industries") }
    var selectedEntity by remember { mutableStateOf(EntityType.PT) }
    var selectedIndustry by remember { mutableStateOf(IndustryType.MANUFACTURING) }
    var npwp by remember { mutableStateOf("0123456789012345") }
    var address by remember { mutableStateOf("Kawasan Industri Agro Terpadu Kav. 18, Jakarta") }

    var showEntityDropdown by remember { mutableStateOf(false) }
    var showIndustryDropdown by remember { mutableStateOf(false) }

    // Dialog Request Access Pass
    var showRequestPassDialog by remember { mutableStateOf(false) }
    var reqName by remember { mutableStateOf("") }
    var reqEmail by remember { mutableStateOf("") }
    var reqRole by remember { mutableStateOf(RoleType.STAFF) }
    var reqApproverType by remember { mutableStateOf("Owner / Super Admin") }
    var reqApproverEmail by remember { mutableStateOf("pteduworksasiaagroindustries@gmail.com") }
    var reqNotes by remember { mutableStateOf("") }
    var showPassSuccessDialog by remember { mutableStateOf(false) }
    var lastSubmittedPass by remember { mutableStateOf<UserAccessPass?>(null) }

    val quickEmailSuggestions = listOf(
        "pteduworksasiaagroindustries@gmail.com" to RoleType.OWNER,
        "finance@sinergiabadi.co.id" to RoleType.MANAGER,
        "andi.staff@sinergiabadi.co.id" to RoleType.STAFF,
        "audit@kapharyanto.id" to RoleType.EXTERNAL
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Navy900,
                        Color(0xFF1E1035),
                        SurfaceLight
                    ),
                    startY = 0f,
                    endY = 1100f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Brand Logo & Header
            Surface(
                modifier = Modifier
                    .size(88.dp)
                    .shadow(16.dp, CircleShape),
                shape = CircleShape,
                color = Color.Transparent
            ) {
                Image(
                    painter = painterResource(id = R.drawable.easier_app_logo_1787737269892),
                    contentDescription = "easier Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "easier",
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 0.5.sp
            )

            // Slogan
            Text(
                text = "Getting easier in one comand.",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Purple100,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 4 User Roles Display Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Navy800.copy(alpha = 0.9f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Purple700.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Pilih Opsi Pengguna:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Purple700.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = "Akses: ${selectedRole.title}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RoleBadgeItem(
                            number = "1",
                            name = "Owner",
                            isSelected = selectedRole == RoleType.OWNER,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedRole = RoleType.OWNER }
                        )
                        RoleBadgeItem(
                            number = "2",
                            name = "Manajer",
                            isSelected = selectedRole == RoleType.MANAGER,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedRole = RoleType.MANAGER }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RoleBadgeItem(
                            number = "3",
                            name = "Admin",
                            isSelected = selectedRole == RoleType.STAFF,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedRole = RoleType.STAFF }
                        )
                        RoleBadgeItem(
                            number = "4",
                            name = "External",
                            isSelected = selectedRole == RoleType.EXTERNAL,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedRole = RoleType.EXTERNAL }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = selectedRole.description,
                        fontSize = 11.sp,
                        color = Purple200,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Registration / Login Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Login Mode Switcher
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceLight)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (loginMode == "MAGIC_LINK") Purple700 else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { loginMode = "MAGIC_LINK" }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AlternateEmail,
                                    contentDescription = null,
                                    tint = if (loginMode == "MAGIC_LINK") Color.White else Purple700,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Gmail / Magic Link",
                                    fontSize = 12.sp,
                                    fontWeight = if (loginMode == "MAGIC_LINK") FontWeight.Bold else FontWeight.Medium,
                                    color = if (loginMode == "MAGIC_LINK") Color.White else TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (loginMode == "PASSCODE") Purple700 else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { loginMode = "PASSCODE" }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VpnKey,
                                    contentDescription = null,
                                    tint = if (loginMode == "PASSCODE") Color.White else Purple700,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Passcode Akses",
                                    fontSize = 12.sp,
                                    fontWeight = if (loginMode == "PASSCODE") FontWeight.Bold else FontWeight.Medium,
                                    color = if (loginMode == "PASSCODE") Color.White else TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (loginMode == "PASSCODE") {
                        // Passcode Flow
                        Text(
                            text = "Kode Pass Akses (Hasil Persetujuan Owner/Manajer) *",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        OutlinedTextField(
                            value = enteredPassCode,
                            onValueChange = {
                                enteredPassCode = it.uppercase()
                                passCodeError = null
                            },
                            placeholder = { Text("cth: PASS-STF-8821", fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = null,
                                    tint = Purple700
                                )
                            },
                            isError = passCodeError != null,
                            supportingText = {
                                if (passCodeError != null) {
                                    Text(text = passCodeError ?: "", color = Crimson500, fontSize = 11.sp)
                                } else {
                                    Text(text = "Masukkan kode pass yang telah disetujui melalui email", color = TextSecondary, fontSize = 11.sp)
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick fill available approved passcodes
                        val approvedPasses = userAccessPasses.filter { it.status == PassStatus.APPROVED && it.passCode != null }
                        if (approvedPasses.isNotEmpty()) {
                            Text(
                                text = "Passcode Terdaftar yang Sudah Disetujui:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Emerald600,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                approvedPasses.forEach { pass ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Emerald50,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Emerald400),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable {
                                                enteredPassCode = pass.passCode ?: ""
                                                email = pass.email
                                                selectedRole = pass.requestedRole
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(
                                                    text = "${pass.name} (${pass.requestedRole.title})",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextPrimary
                                                )
                                                Text(
                                                    text = pass.email,
                                                    fontSize = 10.sp,
                                                    color = TextSecondary
                                                )
                                            }
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Emerald600
                                            ) {
                                                Text(
                                                    text = pass.passCode ?: "",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                val trimmedPass = enteredPassCode.trim()
                                if (trimmedPass.isBlank()) {
                                    passCodeError = "Harap masukkan kode pass Anda."
                                    return@Button
                                }
                                val found = userAccessPasses.find { it.passCode.equals(trimmedPass, ignoreCase = true) && it.status == PassStatus.APPROVED }
                                if (found != null) {
                                    viewModel.switchRole(found.requestedRole)
                                    viewModel.authenticateWithEmail(email = found.email)
                                } else {
                                    // Accept manual login with selected role if valid pattern
                                    viewModel.switchRole(selectedRole)
                                    viewModel.authenticateWithEmail(email = email.ifBlank { "user.pass@company.id" })
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Purple700),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Icon(imageVector = Icons.Default.VpnKey, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Masuk dengan Passcode", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                    } else {
                        // Standard Email & Password Flow
                        Text(
                            text = "Email Pengguna (${selectedRole.title}) *",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                if (emailError != null && it.contains("@")) {
                                    emailError = null
                                }
                            },
                            placeholder = { Text("contoh: pteduworks@gmail.com", fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Email,
                                    contentDescription = null,
                                    tint = if (emailError != null) Crimson500 else Purple700
                                )
                            },
                            trailingIcon = {
                                if (email.isNotBlank()) {
                                    IconButton(onClick = { email = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Hapus",
                                            tint = TextMuted,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            },
                            isError = emailError != null,
                            supportingText = {
                                if (emailError != null) {
                                    Text(text = emailError ?: "", color = Crimson500, fontSize = 11.sp)
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Quick Role Suggestions
                        Text(
                            text = "Pilihan Cepat Berdasarkan Lapisan Akun:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 6.dp)
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(quickEmailSuggestions) { (sugEmail, sugRole) ->
                                val isSelected = email == sugEmail
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isSelected) Purple700 else SurfaceLight,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) Purple700 else SurfaceBorder
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable {
                                            email = sugEmail
                                            selectedRole = sugRole
                                            emailError = null
                                        }
                                ) {
                                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                        Text(
                                            text = sugRole.title,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else Purple700
                                        )
                                        Text(
                                            text = sugEmail,
                                            fontSize = 10.sp,
                                            color = if (isSelected) Purple100 else TextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Gmail & Magic Link One-Click Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val trimmedEmail = email.trim()
                                    if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) {
                                        emailError = "Harap masukkan email yang valid"
                                        return@Button
                                    }
                                    keyboardController?.hide()
                                    viewModel.switchRole(selectedRole)
                                    viewModel.authenticateWithEmail(
                                        email = trimmedEmail,
                                        registerCompany = isRegisterNewCompany,
                                        companyName = companyName,
                                        entityType = selectedEntity,
                                        industryType = selectedIndustry,
                                        npwp16 = npwp,
                                        address = address,
                                        role = selectedRole
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Masuk via Gmail", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    val trimmedEmail = email.trim()
                                    if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) {
                                        emailError = "Harap masukkan email untuk Magic Link"
                                        return@OutlinedButton
                                    }
                                    keyboardController?.hide()
                                    viewModel.switchRole(selectedRole)
                                    viewModel.authenticateWithEmail(
                                        email = trimmedEmail,
                                        registerCompany = isRegisterNewCompany,
                                        companyName = companyName,
                                        entityType = selectedEntity,
                                        industryType = selectedIndustry,
                                        npwp16 = npwp,
                                        address = address,
                                        role = selectedRole
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Purple700),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = null,
                                    tint = Purple700,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Kirim Magic Link", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Purple700)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Password Field
                        Text(
                            text = "Kata Sandi Akun *",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = { Text("Minimal 6 karakter", fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Lock,
                                    contentDescription = null,
                                    tint = Purple700
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle password",
                                        tint = TextMuted
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Optional Register New Company Toggle
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isRegisterNewCompany) Purple100.copy(alpha = 0.5f) else SurfaceLight,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isRegisterNewCompany) Purple600 else SurfaceBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { isRegisterNewCompany = !isRegisterNewCompany }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Checkbox(
                                    checked = isRegisterNewCompany,
                                    onCheckedChange = { isRegisterNewCompany = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Purple700,
                                        checkmarkColor = Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Daftarkan Entitas / Perusahaan Baru",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isRegisterNewCompany) Purple900 else TextPrimary
                                    )
                                    Text(
                                        text = "Buat profil PT/CV baru dengan Bagan Akun PSAK otomatis",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }

                        // Register New Company Expandable Section
                        AnimatedVisibility(
                            visible = isRegisterNewCompany,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp)
                            ) {
                                Text(
                                    text = "Nama Perusahaan Baru *",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                OutlinedTextField(
                                    value = companyName,
                                    onValueChange = { companyName = it },
                                    placeholder = { Text("misal: PT Eduworks Asia Agro Industries", fontSize = 13.sp) },
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Outlined.Business, contentDescription = null, tint = Purple700)
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        OutlinedCard(
                                            onClick = { showEntityDropdown = true },
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text("Bentuk Badan", fontSize = 10.sp, color = TextSecondary)
                                                Text(selectedEntity.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                            }
                                        }
                                        DropdownMenu(
                                            expanded = showEntityDropdown,
                                            onDismissRequest = { showEntityDropdown = false }
                                        ) {
                                            EntityType.entries.forEach { entity ->
                                                DropdownMenuItem(
                                                    text = { Text(entity.label) },
                                                    onClick = {
                                                        selectedEntity = entity
                                                        showEntityDropdown = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    Box(modifier = Modifier.weight(1f)) {
                                        OutlinedCard(
                                            onClick = { showIndustryDropdown = true },
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text("Bidang Usaha", fontSize = 10.sp, color = TextSecondary)
                                                Text(selectedIndustry.label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                            }
                                        }
                                        DropdownMenu(
                                            expanded = showIndustryDropdown,
                                            onDismissRequest = { showIndustryDropdown = false }
                                        ) {
                                            IndustryType.entries.forEach { ind ->
                                                DropdownMenuItem(
                                                    text = { Text(ind.label) },
                                                    onClick = {
                                                        selectedIndustry = ind
                                                        showIndustryDropdown = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                val trimmedEmail = email.trim()
                                if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) {
                                    emailError = "Harap masukkan format email yang valid (cth: name@company.com)"
                                    return@Button
                                }

                                keyboardController?.hide()
                                viewModel.switchRole(selectedRole)
                                viewModel.authenticateWithEmail(
                                    email = trimmedEmail,
                                    registerCompany = isRegisterNewCompany,
                                    companyName = companyName,
                                    entityType = selectedEntity,
                                    industryType = selectedIndustry,
                                    npwp16 = npwp,
                                    address = address,
                                    role = selectedRole
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Purple700),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Icon(
                                imageVector = if (isRegisterNewCompany) Icons.Default.AppRegistration else Icons.Default.Login,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isRegisterNewCompany) "Daftarkan Perusahaan & Masuk" else "Masuk sebagai ${selectedRole.title}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    HorizontalDivider(color = SurfaceBorder)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Request User Access Pass Button (Approval via Email Owner/Manajer)
                    OutlinedButton(
                        onClick = {
                            reqEmail = email
                            showRequestPassDialog = true
                        },
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Purple700),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Purple700),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ForwardToInbox,
                            contentDescription = null,
                            tint = Purple700,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Minta Pass Akses (Approval via Email Owner/Manajer)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Purple700
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Trust & Security Badges
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Navy800.copy(alpha = 0.85f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ComplianceItem(icon = Icons.Outlined.VerifiedUser, text = "4 Lapisan Akses")
                    ComplianceItem(icon = Icons.Outlined.Email, text = "Email Pass Approval")
                    ComplianceItem(icon = Icons.Outlined.AccountBalance, text = "DJP Coretax")
                    ComplianceItem(icon = Icons.Outlined.Security, text = "AES-256 Bit")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "easier • Easy your life make easier",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Dialog: Request Access Pass
    if (showRequestPassDialog) {
        AlertDialog(
            onDismissRequest = { showRequestPassDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MarkEmailRead,
                        contentDescription = null,
                        tint = Purple700,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Minta Persetujuan Pass Akses", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Sistem akan mengirimkan permintaan persetujuan pass resmi langsung ke email Owner / Manajer.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    OutlinedTextField(
                        value = reqName,
                        onValueChange = { reqName = it },
                        label = { Text("Nama Lengkap Pemohon *") },
                        placeholder = { Text("cth: Maya Kartika") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = reqEmail,
                        onValueChange = { reqEmail = it },
                        label = { Text("Email Pemohon *") },
                        placeholder = { Text("cth: maya.finance@sinergiabadi.co.id") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Pilih Lapisan Pengguna yang Diminta:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(RoleType.STAFF, RoleType.EXTERNAL, RoleType.MANAGER).forEach { role ->
                            FilterChip(
                                selected = reqRole == role,
                                onClick = { reqRole = role },
                                label = { Text(role.title, fontSize = 11.sp) }
                            )
                        }
                    }

                    Text("Kirim Permintaan Persetujuan ke Email:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        ApproverOption(
                            title = "Owner / Super Admin",
                            email = "pteduworksasiaagroindustries@gmail.com",
                            isSelected = reqApproverEmail == "pteduworksasiaagroindustries@gmail.com",
                            onClick = {
                                reqApproverType = "Owner / Super Admin"
                                reqApproverEmail = "pteduworksasiaagroindustries@gmail.com"
                            }
                        )
                        ApproverOption(
                            title = "Manajer Keuangan",
                            email = "finance@sinergiabadi.co.id",
                            isSelected = reqApproverEmail == "finance@sinergiabadi.co.id",
                            onClick = {
                                reqApproverType = "Manajer Keuangan"
                                reqApproverEmail = "finance@sinergiabadi.co.id"
                            }
                        )
                    }

                    OutlinedTextField(
                        value = reqNotes,
                        onValueChange = { reqNotes = it },
                        label = { Text("Catatan / Alasan Akses (Opsional)") },
                        placeholder = { Text("cth: Akses input bukti transaksi kas harian & pelunasan") },
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (reqName.isNotBlank() && reqEmail.isNotBlank()) {
                            viewModel.requestUserAccessPass(
                                name = reqName,
                                email = reqEmail,
                                requestedRole = reqRole,
                                approverType = reqApproverType,
                                approverEmail = reqApproverEmail,
                                notes = reqNotes
                            )
                            lastSubmittedPass = userAccessPasses.firstOrNull()
                            showRequestPassDialog = false
                            showPassSuccessDialog = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Purple700),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Kirim via Email", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRequestPassDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Success Confirmation Dialog
    if (showPassSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showPassSuccessDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Emerald600,
                    modifier = Modifier.size(42.dp)
                )
            },
            title = {
                Text(
                    text = "Permintaan Pass Terkirim!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Permintaan pass akses untuk $reqName (${reqRole.title}) berhasil dikirimkan ke email:\n\n$reqApproverEmail ($reqApproverType)\n\nSetelah disetujui, passcode otentikasi akan dikirimkan kembali ke $reqEmail.",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = TextPrimary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPassSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Mengerti & Selesai")
                }
            }
        )
    }
}

@Composable
private fun RoleBadgeItem(
    number: String,
    name: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) Purple700 else Navy900.copy(alpha = 0.6f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) Purple300 else Navy700
        ),
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (isSelected) Color.White else Purple300,
                modifier = Modifier.size(18.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = number,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Purple900 else Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = name,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ApproverOption(
    title: String,
    email: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) Purple100 else SurfaceLight,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) Purple700 else SurfaceBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = Purple700)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = email, fontSize = 11.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun ComplianceItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Emerald400,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}
