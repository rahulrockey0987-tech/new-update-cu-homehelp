package com.example.ui.screens.account

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.generateCustomerId
import com.example.ui.HomeHelpViewModel
import com.example.ui.theme.BrandAmberAccent
import com.example.ui.theme.BrandAmberContainer
import com.example.ui.theme.BrandAmberOnContainer
import com.example.ui.theme.BrandTealContainer
import com.example.ui.theme.BrandTealDark
import com.example.ui.theme.BrandTealPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate900
import com.example.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: HomeHelpViewModel,
    onBack: () -> Unit,
    onAuthSuccess: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Login, 1 = Register

    // Login Form State
    var loginIdentifier by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginPasswordVisible by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }

    // Register Form State (Customer's Own Information)
    var regName by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regPasswordVisible by remember { mutableStateOf(false) }
    var regArea by remember { mutableStateOf("Madhapur, Hyderabad 500081") }
    var regError by remember { mutableStateOf<String?>(null) }

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
                    Column {
                        Text(
                            text = if (selectedTab == 0) "Customer Login" else "Customer Registration",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "HomeHelp Hyderabad • Verified Customer Portal",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate500
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            // Tab Selector: Login vs Register
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Slate100,
                    contentColor = BrandTealDark,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = BrandTealPrimary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            loginError = null
                        },
                        text = {
                            Text(
                                "Login",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                            regError = null
                        },
                        text = {
                            Text(
                                "Register (New Customer)",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }

            // Info Card: Policy against random IDs and duplicates
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandTealContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Security,
                            contentDescription = null,
                            tint = BrandTealDark,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Single Customer Identity Guarantee",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = BrandTealDark
                            )
                            Text(
                                text = "Your Customer ID is derived directly from your registered mobile number. Duplicate accounts are prevented, ensuring all bookings and tracking remain linked to you.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate600
                            )
                        }
                    }
                }
            }

            // TAB 0: LOGIN
            if (selectedTab == 0) {
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
                                text = "Login with Your Customer Information",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Slate900
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
                                label = { Text("Mobile Number (10 digits) or Email") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (loginIdentifier.any { it.isDigit() }) Icons.Filled.Phone else Icons.Filled.Email,
                                        contentDescription = null,
                                        tint = BrandTealPrimary
                                    )
                                },
                                placeholder = { Text("e.g. 9876543210 or yourname@gmail.com") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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
                                label = { Text("Password") },
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
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_password_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandTealPrimary,
                                    focusedLabelColor = BrandTealPrimary
                                )
                            )

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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("login_submit_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandTealPrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Login to HomeHelp →", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Don't have an account?", style = MaterialTheme.typography.bodySmall, color = Slate600)
                                TextButton(onClick = { selectedTab = 1 }) {
                                    Text("Register here", fontWeight = FontWeight.Bold, color = BrandTealDark)
                                }
                            }
                        }
                    }
                }
            }

            // TAB 1: REGISTER (Customer's Own Information, Duplicate Check, Deterministic ID)
            if (selectedTab == 1) {
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
                                text = "Enter your genuine personal contact information. We use this to prevent duplicate registrations and assign your exact Customer ID.",
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

                            // Full Name
                            OutlinedTextField(
                                value = regName,
                                onValueChange = {
                                    regName = it
                                    regError = null
                                },
                                label = { Text("Your Full Name *") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = BrandTealPrimary)
                                },
                                placeholder = { Text("e.g. Ramesh Reddy") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_name_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandTealPrimary,
                                    focusedLabelColor = BrandTealPrimary
                                )
                            )

                            // Mobile Number (Phone)
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
                                placeholder = { Text("98765 43210") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
                                            text = "YOUR DETERMINISTIC CUSTOMER ID:",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Slate600
                                        )
                                        Text(
                                            text = previewCustomerId,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                                            color = if (cleanDigits.length >= 10) BrandTealDark else Slate500
                                        )
                                        Text(
                                            text = "Strictly bounded to your mobile number. Never random, no duplicate accounts.",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                            color = Slate500
                                        )
                                    }
                                }
                            }

                            // Email Address
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
                                placeholder = { Text("name@example.com") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_email_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandTealPrimary,
                                    focusedLabelColor = BrandTealPrimary
                                )
                            )

                            // Password
                            OutlinedTextField(
                                value = regPassword,
                                onValueChange = {
                                    regPassword = it
                                    regError = null
                                },
                                label = { Text("Create Password (min 4 chars) *") },
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
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_password_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandTealPrimary,
                                    focusedLabelColor = BrandTealPrimary
                                )
                            )

                            // Hyderabad Primary Area
                            Text(
                                text = "Select Your Hyderabad Locality:",
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

                            // Register button
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

                                    viewModel.registerCustomer(
                                        name = regName,
                                        phone = regPhone,
                                        email = regEmail,
                                        password = regPassword,
                                        area = regArea,
                                        onSuccess = onAuthSuccess,
                                        onError = { regError = it }
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("reg_submit_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandTealPrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Register & Create Customer Account →", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Already registered?", style = MaterialTheme.typography.bodySmall, color = Slate600)
                                TextButton(onClick = { selectedTab = 0 }) {
                                    Text("Login here", fontWeight = FontWeight.Bold, color = BrandTealDark)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
