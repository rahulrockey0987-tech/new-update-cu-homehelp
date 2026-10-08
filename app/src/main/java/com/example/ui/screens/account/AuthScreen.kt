package com.example.ui.screens.account

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.generateCustomerId
import com.example.ui.HomeHelpViewModel
import com.example.ui.theme.BrandAmberAccent
import com.example.ui.theme.BrandAmberContainer
import com.example.ui.theme.BrandTealContainer
import com.example.ui.theme.BrandTealDark
import com.example.ui.theme.BrandTealPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.SuccessGreen
import kotlinx.coroutines.delay

enum class AuthViewMode {
    LOGIN,
    REGISTER,
    VERIFY_OTP,
    FORGOT_PASSWORD
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: HomeHelpViewModel,
    onAuthSuccess: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    var viewMode by remember { mutableStateOf(AuthViewMode.LOGIN) }

    // Login Form State
    var loginIdentifier by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginPasswordVisible by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }

    // Register Form State
    var regName by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regConfirmPassword by remember { mutableStateOf("") }
    var regPasswordVisible by remember { mutableStateOf(false) }
    var regConfirmPasswordVisible by remember { mutableStateOf(false) }
    var regArea by remember { mutableStateOf("Madhapur, Hyderabad 500081") }
    var regCustomAddress by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }
    var regError by remember { mutableStateOf<String?>(null) }

    // OTP State
    var otpEntered by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf<String?>(null) }
    var otpCountdown by remember { mutableIntStateOf(30) }

    // Forgot Password State
    var forgotIdentifier by remember { mutableStateOf("") }
    var forgotOtpEntered by remember { mutableStateOf("") }
    var forgotNewPassword by remember { mutableStateOf("") }
    var forgotConfirmPassword by remember { mutableStateOf("") }
    var forgotStep by remember { mutableIntStateOf(1) } // 1 = Enter Phone/Email, 2 = Enter OTP + New Password
    var forgotError by remember { mutableStateOf<String?>(null) }

    val isAuthenticating by viewModel.isAuthenticating.collectAsStateWithLifecycle()
    val lastSentOtp by viewModel.lastSentOtp.collectAsStateWithLifecycle()
    val lastOtpTarget by viewModel.lastOtpTarget.collectAsStateWithLifecycle()

    // Countdown timer for OTP
    LaunchedEffect(viewMode, lastSentOtp) {
        if (viewMode == AuthViewMode.VERIFY_OTP || forgotStep == 2) {
            otpCountdown = 30
            while (otpCountdown > 0) {
                delay(1000L)
                otpCountdown--
            }
        }
    }

    // Back handling
    BackHandler {
        when (viewMode) {
            AuthViewMode.VERIFY_OTP -> {
                viewMode = AuthViewMode.REGISTER
                otpError = null
            }
            AuthViewMode.FORGOT_PASSWORD -> {
                if (forgotStep == 2) {
                    forgotStep = 1
                } else {
                    viewMode = AuthViewMode.LOGIN
                }
                forgotError = null
            }
            AuthViewMode.REGISTER -> {
                viewMode = AuthViewMode.LOGIN
                regError = null
            }
            AuthViewMode.LOGIN -> {
                onBack?.invoke()
            }
        }
    }

    val cleanDigits = regPhone.filter { it.isDigit() }
    val previewCustomerId = if (cleanDigits.isNotEmpty()) generateCustomerId(regPhone) else "CUST-XXXXXXXXXX"

    val hydLocalities = listOf(
        "Madhapur, Hyderabad 500081",
        "Hitec City Phase 2, Hyderabad 500081",
        "Jubilee Hills Road No. 36, Hyderabad 500033",
        "Gachibowli Financial Dist, Hyderabad 500032",
        "Banjara Hills Road No. 12, Hyderabad 500034",
        "Kondapur Botanical Garden, Hyderabad 500084",
        "Begumpet Metro Corridor, Hyderabad 500016"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(BrandTealPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.HomeRepairService,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "HomeHelp Customer Portal",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = Slate900
                            )
                            Text(
                                text = "Hyderabad • Mandatory Secure Access",
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate500
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (viewMode != AuthViewMode.LOGIN || onBack != null) {
                        IconButton(
                            onClick = {
                                when (viewMode) {
                                    AuthViewMode.VERIFY_OTP -> viewMode = AuthViewMode.REGISTER
                                    AuthViewMode.FORGOT_PASSWORD -> {
                                        if (forgotStep == 2) forgotStep = 1 else viewMode = AuthViewMode.LOGIN
                                    }
                                    AuthViewMode.REGISTER -> viewMode = AuthViewMode.LOGIN
                                    AuthViewMode.LOGIN -> onBack?.invoke()
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("auth_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Top Notice: Mandatory Customer Login
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandTealContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Security,
                            contentDescription = null,
                            tint = BrandTealDark,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Authentication Required",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = BrandTealDark
                            )
                            Text(
                                text = "To protect your home bookings and service safety, customer login is required before accessing services.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = Slate600
                            )
                        }
                    }
                }
            }

            // Mode Selector Tabs (only shown on Login / Register)
            if (viewMode == AuthViewMode.LOGIN || viewMode == AuthViewMode.REGISTER) {
                item {
                    TabRow(
                        selectedTabIndex = if (viewMode == AuthViewMode.LOGIN) 0 else 1,
                        containerColor = Slate100,
                        contentColor = BrandTealDark,
                        indicator = { tabPositions ->
                            val currentTabIndex = if (viewMode == AuthViewMode.LOGIN) 0 else 1
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[currentTabIndex]),
                                color = BrandTealPrimary
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = viewMode == AuthViewMode.LOGIN,
                            onClick = {
                                viewMode = AuthViewMode.LOGIN
                                loginError = null
                            },
                            text = {
                                Text(
                                    "Customer Login",
                                    fontWeight = if (viewMode == AuthViewMode.LOGIN) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        )
                        Tab(
                            selected = viewMode == AuthViewMode.REGISTER,
                            onClick = {
                                viewMode = AuthViewMode.REGISTER
                                regError = null
                            },
                            text = {
                                Text(
                                    "New Registration",
                                    fontWeight = if (viewMode == AuthViewMode.REGISTER) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        )
                    }
                }
            }

            // =========================================================================
            // VIEW 1: CUSTOMER LOGIN
            // =========================================================================
            if (viewMode == AuthViewMode.LOGIN) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "Welcome Back to HomeHelp",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Slate900
                            )
                            Text(
                                text = "Enter your registered mobile number or email and password to access your bookings and Hyderabad home services.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate600
                            )

                            // Error banner
                            AnimatedVisibility(visible = loginError != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ErrorRed.copy(alpha = 0.1f))
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = loginError.orEmpty(),
                                        color = ErrorRed,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                                    )
                                }
                            }

                            // Mobile or Email field
                            OutlinedTextField(
                                value = loginIdentifier,
                                onValueChange = {
                                    loginIdentifier = it
                                    loginError = null
                                },
                                label = { Text("Mobile Number (10 digits) or Email *") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (loginIdentifier.any { it.isDigit() }) Icons.Filled.Phone else Icons.Filled.Email,
                                        contentDescription = null,
                                        tint = BrandTealPrimary
                                    )
                                },
                                placeholder = { Text("e.g. 9876543210 or name@example.com") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_identifier_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandTealPrimary,
                                    focusedLabelColor = BrandTealPrimary
                                )
                            )

                            // Password field
                            OutlinedTextField(
                                value = loginPassword,
                                onValueChange = {
                                    loginPassword = it
                                    loginError = null
                                },
                                label = { Text("Password *") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Filled.Lock, contentDescription = null, tint = BrandTealPrimary)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { loginPasswordVisible = !loginPasswordVisible }) {
                                        Icon(
                                            imageVector = if (loginPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                            contentDescription = "Toggle password"
                                        )
                                    }
                                },
                                visualTransformation = if (loginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    if (loginIdentifier.isNotBlank() && loginPassword.isNotBlank()) {
                                        viewModel.loginCustomer(loginIdentifier, loginPassword, onAuthSuccess) { loginError = it }
                                    }
                                }),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_password_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandTealPrimary,
                                    focusedLabelColor = BrandTealPrimary
                                )
                            )

                            // Forgot Password Link
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = {
                                        viewMode = AuthViewMode.FORGOT_PASSWORD
                                        forgotStep = 1
                                        forgotIdentifier = loginIdentifier
                                        forgotError = null
                                    },
                                    modifier = Modifier.testTag("forgot_password_button")
                                ) {
                                    Text(
                                        "Forgot Password?",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = BrandTealDark
                                    )
                                }
                            }

                            // Login Button
                            Button(
                                onClick = {
                                    if (loginIdentifier.isBlank()) {
                                        loginError = "Please enter your registered mobile number or email"
                                        return@Button
                                    }
                                    if (loginPassword.isBlank()) {
                                        loginError = "Please enter your password"
                                        return@Button
                                    }
                                    viewModel.loginCustomer(
                                        phoneOrEmail = loginIdentifier,
                                        password = loginPassword,
                                        onSuccess = onAuthSuccess,
                                        onError = { loginError = it }
                                    )
                                },
                                enabled = !isAuthenticating,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("login_submit_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandTealPrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (isAuthenticating) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Validating Account...")
                                } else {
                                    Text("Login to Customer Home →", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }

                            // Demo Credentials Helper Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Slate100)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "Sample Customer Credentials:",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Slate800
                                    )
                                    Text(
                                        text = "Phone: 9876543210  •  Password: 1234",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                        color = BrandTealDark
                                    )
                                    Text(
                                        text = "Or create a new account using the 'New Registration' tab above.",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                        color = Slate500
                                    )
                                }
                            }

                            // Switch to Register option
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("New to HomeHelp?", style = MaterialTheme.typography.bodySmall, color = Slate600)
                                TextButton(onClick = {
                                    viewMode = AuthViewMode.REGISTER
                                    regError = null
                                }) {
                                    Text("Create Account", fontWeight = FontWeight.Bold, color = BrandTealDark)
                                }
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // VIEW 2: CUSTOMER REGISTRATION
            // =========================================================================
            if (viewMode == AuthViewMode.REGISTER) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "Register Customer Account",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Slate900
                            )
                            Text(
                                text = "Create your HomeHelp customer account. An SMS OTP will be sent to verify your phone number.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate600
                            )

                            // Error banner
                            AnimatedVisibility(visible = regError != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ErrorRed.copy(alpha = 0.1f))
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = regError.orEmpty(),
                                        color = ErrorRed,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                                    )
                                }
                            }

                            // 1. Full Name
                            OutlinedTextField(
                                value = regName,
                                onValueChange = {
                                    regName = it
                                    regError = null
                                },
                                label = { Text("Full Name *") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = BrandTealPrimary)
                                },
                                placeholder = { Text("e.g. Ramesh Reddy") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_name_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandTealPrimary,
                                    focusedLabelColor = BrandTealPrimary
                                )
                            )

                            // 2. Mobile Number (Phone)
                            OutlinedTextField(
                                value = regPhone,
                                onValueChange = {
                                    if (it.length <= 13) {
                                        regPhone = it
                                        regError = null
                                    }
                                },
                                label = { Text("Mobile Number (10 digits) *") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Filled.Phone, contentDescription = null, tint = BrandTealPrimary)
                                },
                                prefix = { Text("+91 ", fontWeight = FontWeight.SemiBold, color = Slate900) },
                                placeholder = { Text("9876543210") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_phone_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandTealPrimary,
                                    focusedLabelColor = BrandTealPrimary
                                )
                            )

                            // Live Customer ID Preview (Deterministic, No Random IDs)
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Slate100)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Badge,
                                        contentDescription = null,
                                        tint = BrandTealPrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "CUSTOMER ID PREVIEW:",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Slate600
                                        )
                                        Text(
                                            text = previewCustomerId,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                                            color = if (cleanDigits.length >= 10) BrandTealDark else Slate500
                                        )
                                        Text(
                                            text = "Permanently linked to your mobile. Keeps your bookings safe.",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                            color = Slate500
                                        )
                                    }
                                }
                            }

                            // 3. Email Address
                            OutlinedTextField(
                                value = regEmail,
                                onValueChange = {
                                    regEmail = it
                                    regError = null
                                },
                                label = { Text("Email Address *") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Filled.Email, contentDescription = null, tint = BrandTealPrimary)
                                },
                                placeholder = { Text("ramesh@example.com") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_email_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandTealPrimary,
                                    focusedLabelColor = BrandTealPrimary
                                )
                            )

                            // 4. Password
                            OutlinedTextField(
                                value = regPassword,
                                onValueChange = {
                                    regPassword = it
                                    regError = null
                                },
                                label = { Text("Password (min 4 characters) *") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Filled.Lock, contentDescription = null, tint = BrandTealPrimary)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { regPasswordVisible = !regPasswordVisible }) {
                                        Icon(
                                            imageVector = if (regPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                            contentDescription = "Toggle password"
                                        )
                                    }
                                },
                                visualTransformation = if (regPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_password_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandTealPrimary,
                                    focusedLabelColor = BrandTealPrimary
                                )
                            )

                            // 5. Confirm Password
                            OutlinedTextField(
                                value = regConfirmPassword,
                                onValueChange = {
                                    regConfirmPassword = it
                                    regError = null
                                },
                                label = { Text("Confirm Password *") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Filled.LockReset, contentDescription = null, tint = BrandTealPrimary)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { regConfirmPasswordVisible = !regConfirmPasswordVisible }) {
                                        Icon(
                                            imageVector = if (regConfirmPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                            contentDescription = "Toggle password"
                                        )
                                    }
                                },
                                visualTransformation = if (regConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_confirm_password_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandTealPrimary,
                                    focusedLabelColor = BrandTealPrimary
                                )
                            )

                            // 6. Location / Address
                            Text(
                                text = "Select Hyderabad Locality / Area *",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Slate900
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                hydLocalities.forEach { loc ->
                                    val isSelected = regArea == loc
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) BrandTealContainer else Slate100)
                                            .clickable { regArea = loc }
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.LocationOn,
                                            contentDescription = null,
                                            tint = if (isSelected) BrandTealDark else Slate500,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = loc,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            ),
                                            color = if (isSelected) BrandTealDark else Slate900
                                        )
                                    }
                                }
                            }

                            // Specific flat/building line
                            OutlinedTextField(
                                value = regCustomAddress,
                                onValueChange = { regCustomAddress = it },
                                label = { Text("Flat / House No., Building Name (Optional)") },
                                placeholder = { Text("e.g. Flat 304, Green Park Heights") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_location_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandTealPrimary,
                                    focusedLabelColor = BrandTealPrimary
                                )
                            )

                            // 7. Accept Terms & Conditions Checkbox
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { termsAccepted = !termsAccepted }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = termsAccepted,
                                    onCheckedChange = { termsAccepted = it },
                                    colors = CheckboxDefaults.colors(checkedColor = BrandTealPrimary),
                                    modifier = Modifier.testTag("reg_terms_checkbox")
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "I accept the Terms & Conditions and HomeHelp Privacy Policy for Hyderabad services.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate800
                                )
                            }

                            // Send OTP & Register button
                            Button(
                                onClick = {
                                    if (regName.isBlank()) {
                                        regError = "Please enter your full name"
                                        return@Button
                                    }
                                    if (cleanDigits.length < 10) {
                                        regError = "Please enter a valid 10-digit mobile number"
                                        return@Button
                                    }
                                    if (!regEmail.contains("@") || !regEmail.contains(".")) {
                                        regError = "Please enter a valid email address"
                                        return@Button
                                    }
                                    if (regPassword.length < 4) {
                                        regError = "Password must be at least 4 characters"
                                        return@Button
                                    }
                                    if (regPassword != regConfirmPassword) {
                                        regError = "Passwords do not match. Please re-enter confirm password."
                                        return@Button
                                    }
                                    if (!termsAccepted) {
                                        regError = "Please accept the Terms & Conditions to register."
                                        return@Button
                                    }

                                    val finalArea = if (regCustomAddress.isNotBlank()) "$regCustomAddress, $regArea" else regArea
                                    viewModel.initiateRegistration(
                                        name = regName,
                                        phone = regPhone,
                                        email = regEmail,
                                        password = regPassword,
                                        confirmPassword = regConfirmPassword,
                                        area = finalArea,
                                        termsAccepted = termsAccepted,
                                        onSuccess = { otp ->
                                            otpEntered = ""
                                            otpError = null
                                            viewMode = AuthViewMode.VERIFY_OTP
                                        },
                                        onError = { regError = it }
                                    )
                                },
                                enabled = !isAuthenticating,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("reg_submit_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandTealPrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (isAuthenticating) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Sending OTP...")
                                } else {
                                    Text("Send OTP & Continue →", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Already have an account?", style = MaterialTheme.typography.bodySmall, color = Slate600)
                                TextButton(onClick = {
                                    viewMode = AuthViewMode.LOGIN
                                    loginError = null
                                }) {
                                    Text("Login here", fontWeight = FontWeight.Bold, color = BrandTealDark)
                                }
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // VIEW 3: VERIFY OTP SCREEN (Registration completion)
            // =========================================================================
            if (viewMode == AuthViewMode.VERIFY_OTP) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(BrandTealContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Sms,
                                    contentDescription = null,
                                    tint = BrandTealDark,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Text(
                                text = "Verify Your Mobile Number",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Slate900,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "We have sent a 6-digit verification code to\n+91 ${lastOtpTarget ?: regPhone}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Slate600,
                                textAlign = TextAlign.Center
                            )

                            // Simulated SMS Banner for instant verification
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = BrandAmberContainer)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Info,
                                        contentDescription = null,
                                        tint = BrandAmberAccent,
                                        modifier = Modifier.size(26.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Simulated SMS Alert Received:",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Slate900
                                        )
                                        Text(
                                            text = "Your OTP is: ${lastSentOtp ?: "123456"}",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                            color = BrandTealDark
                                        )
                                        Text(
                                            text = "Tap to autofill code below",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                            color = Slate600
                                        )
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    Button(
                                        onClick = {
                                            otpEntered = lastSentOtp ?: "123456"
                                            otpError = null
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandTealPrimary),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Autofill", fontSize = 12.sp)
                                    }
                                }
                            }

                            // Error banner
                            AnimatedVisibility(visible = otpError != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ErrorRed.copy(alpha = 0.1f))
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = otpError.orEmpty(),
                                        color = ErrorRed,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            // 6-digit OTP Field
                            OutlinedTextField(
                                value = otpEntered,
                                onValueChange = {
                                    if (it.length <= 6 && it.all { ch -> ch.isDigit() }) {
                                        otpEntered = it
                                        otpError = null
                                    }
                                },
                                label = { Text("Enter 6-Digit OTP") },
                                placeholder = { Text("••••••") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Filled.Lock, contentDescription = null, tint = BrandTealPrimary)
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    if (otpEntered.length == 6) {
                                        viewModel.verifyOtpAndCompleteRegistration(otpEntered, onAuthSuccess) { otpError = it }
                                    }
                                }),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("otp_input_field"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandTealPrimary,
                                    focusedLabelColor = BrandTealPrimary
                                )
                            )

                            // Verify & Complete Registration Button
                            Button(
                                onClick = {
                                    if (otpEntered.length < 6) {
                                        otpError = "Please enter the complete 6-digit OTP"
                                        return@Button
                                    }
                                    viewModel.verifyOtpAndCompleteRegistration(
                                        enteredOtp = otpEntered,
                                        onSuccess = onAuthSuccess,
                                        onError = { otpError = it }
                                    )
                                },
                                enabled = !isAuthenticating,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("verify_otp_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandTealPrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (isAuthenticating) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Verifying...")
                                } else {
                                    Text("Verify OTP & Open Customer Home →", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                            }

                            // Resend OTP Section
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (otpCountdown > 0) {
                                    Text(
                                        text = "Resend OTP in ${otpCountdown}s",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Slate500
                                    )
                                } else {
                                    TextButton(
                                        onClick = {
                                            viewModel.resendRegistrationOtp(
                                                onSuccess = { otpCountdown = 30 },
                                                onError = { otpError = it }
                                            )
                                        },
                                        modifier = Modifier.testTag("resend_otp_button")
                                    ) {
                                        Icon(imageVector = Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Resend OTP", fontWeight = FontWeight.Bold, color = BrandTealDark)
                                    }
                                }
                            }

                            TextButton(
                                onClick = {
                                    viewMode = AuthViewMode.REGISTER
                                    otpError = null
                                }
                            ) {
                                Text("Edit Mobile Number / Details", color = Slate600)
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // VIEW 4: FORGOT / RESET PASSWORD SCREEN
            // =========================================================================
            if (viewMode == AuthViewMode.FORGOT_PASSWORD) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "Reset Your Password",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Slate900
                            )
                            Text(
                                text = if (forgotStep == 1)
                                    "Enter your registered mobile number or email to receive a password reset OTP."
                                else
                                    "Enter the OTP and your new password.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate600
                            )

                            // Error banner
                            AnimatedVisibility(visible = forgotError != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ErrorRed.copy(alpha = 0.1f))
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = forgotError.orEmpty(),
                                        color = ErrorRed,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                                    )
                                }
                            }

                            if (forgotStep == 1) {
                                OutlinedTextField(
                                    value = forgotIdentifier,
                                    onValueChange = {
                                        forgotIdentifier = it
                                        forgotError = null
                                    },
                                    label = { Text("Registered Mobile or Email *") },
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Filled.Phone, contentDescription = null, tint = BrandTealPrimary)
                                    },
                                    placeholder = { Text("e.g. 9876543210") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BrandTealPrimary,
                                        focusedLabelColor = BrandTealPrimary
                                    )
                                )

                                Button(
                                    onClick = {
                                        if (forgotIdentifier.isBlank()) {
                                            forgotError = "Please enter your mobile or email"
                                            return@Button
                                        }
                                        viewModel.initiateForgotPassword(
                                            phoneOrEmail = forgotIdentifier,
                                            onSuccess = {
                                                forgotStep = 2
                                                forgotOtpEntered = ""
                                                forgotError = null
                                            },
                                            onError = { forgotError = it }
                                        )
                                    },
                                    enabled = !isAuthenticating,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandTealPrimary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    if (isAuthenticating) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                    } else {
                                        Text("Send Reset OTP →", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    }
                                }
                            } else {
                                // Step 2: Enter OTP + New Password
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = BrandAmberContainer)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(imageVector = Icons.Filled.Info, contentDescription = null, tint = BrandAmberAccent)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "Reset OTP sent: ${lastSentOtp ?: "123456"}",
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Slate900
                                            )
                                            Text(text = "Use this code below to set new password", style = MaterialTheme.typography.labelSmall, color = Slate600)
                                        }
                                        Spacer(modifier = Modifier.weight(1f))
                                        Button(
                                            onClick = { forgotOtpEntered = lastSentOtp ?: "123456" },
                                            colors = ButtonDefaults.buttonColors(containerColor = BrandTealPrimary),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text("Fill", fontSize = 11.sp)
                                        }
                                    }
                                }

                                OutlinedTextField(
                                    value = forgotOtpEntered,
                                    onValueChange = {
                                        if (it.length <= 6) {
                                            forgotOtpEntered = it
                                            forgotError = null
                                        }
                                    },
                                    label = { Text("6-Digit OTP *") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BrandTealPrimary,
                                        focusedLabelColor = BrandTealPrimary
                                    )
                                )

                                OutlinedTextField(
                                    value = forgotNewPassword,
                                    onValueChange = {
                                        forgotNewPassword = it
                                        forgotError = null
                                    },
                                    label = { Text("New Password (min 4 chars) *") },
                                    visualTransformation = PasswordVisualTransformation(),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BrandTealPrimary,
                                        focusedLabelColor = BrandTealPrimary
                                    )
                                )

                                OutlinedTextField(
                                    value = forgotConfirmPassword,
                                    onValueChange = {
                                        forgotConfirmPassword = it
                                        forgotError = null
                                    },
                                    label = { Text("Confirm New Password *") },
                                    visualTransformation = PasswordVisualTransformation(),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BrandTealPrimary,
                                        focusedLabelColor = BrandTealPrimary
                                    )
                                )

                                Button(
                                    onClick = {
                                        if (forgotOtpEntered.length < 6) {
                                            forgotError = "Please enter the 6-digit OTP"
                                            return@Button
                                        }
                                        if (forgotNewPassword.length < 4) {
                                            forgotError = "Password must be at least 4 characters"
                                            return@Button
                                        }
                                        if (forgotNewPassword != forgotConfirmPassword) {
                                            forgotError = "Passwords do not match"
                                            return@Button
                                        }
                                        viewModel.verifyOtpAndResetPassword(
                                            enteredOtp = forgotOtpEntered,
                                            newPassword = forgotNewPassword,
                                            confirmPassword = forgotConfirmPassword,
                                            onSuccess = {
                                                viewMode = AuthViewMode.LOGIN
                                                loginIdentifier = forgotIdentifier
                                                loginPassword = forgotNewPassword
                                                loginError = null
                                            },
                                            onError = { forgotError = it }
                                        )
                                    },
                                    enabled = !isAuthenticating,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandTealPrimary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    if (isAuthenticating) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                    } else {
                                        Text("Update Password & Return to Login →", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    }
                                }
                            }

                            TextButton(
                                onClick = {
                                    viewMode = AuthViewMode.LOGIN
                                    forgotError = null
                                },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text("Back to Login", color = Slate600)
                            }
                        }
                    }
                }
            }
        }
    }
}
