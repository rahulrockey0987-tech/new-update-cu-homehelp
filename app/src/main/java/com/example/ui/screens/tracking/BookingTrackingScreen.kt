package com.example.ui.screens.tracking

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BookingStatus
import com.example.ui.HomeHelpViewModel
import com.example.ui.components.StatusChip
import com.example.ui.theme.BrandAmberAccent
import com.example.ui.theme.BrandAmberContainer
import com.example.ui.theme.BrandAmberOnContainer
import com.example.ui.theme.BrandTealContainer
import com.example.ui.theme.BrandTealDark
import com.example.ui.theme.BrandTealPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.SuccessGreen
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingTrackingScreen(
    bookingId: String,
    viewModel: HomeHelpViewModel,
    onBack: () -> Unit,
    onSupportClick: () -> Unit
) {
    val allBookings by viewModel.allBookings.collectAsStateWithLifecycle()
    val booking = allBookings.find { it.id == bookingId }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    var showReviewDialog by remember { mutableStateOf(false) }
    var reviewRating by remember { mutableIntStateOf(5) }
    var reviewComment by remember { mutableStateOf("Excellent service! Arrived promptly and very professional.") }
    var showCancelDialog by remember { mutableStateOf(false) }

    // Live Map Simulation State: Partner Moving Towards Customer in Hyderabad
    var progressFraction by remember { mutableFloatStateOf(0.35f) } // 0.0 = starting point, 1.0 = arrived
    var isLiveTrackingRunning by remember { mutableStateOf(true) }
    var zoomLevel by remember { mutableFloatStateOf(1.0f) }
    var mapLayerType by remember { mutableStateOf("standard") } // standard, satellite, traffic
    var mapOffset by remember { mutableStateOf(Offset.Zero) }

    // Animate partner location along route
    LaunchedEffect(isLiveTrackingRunning) {
        while (isLiveTrackingRunning) {
            delay(1500)
            if (progressFraction < 0.98f) {
                progressFraction = (progressFraction + 0.035f).coerceAtMost(1.0f)
            }
        }
    }

    // Pulse animation for partner radar
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val radarPulse by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar"
    )

    if (booking == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Booking not found: $bookingId")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onBack) { Text("Go Back") }
            }
        }
        return
    }

    val pro = booking.assignedPro
    val proName = pro?.name ?: "Venkatesh Rao"

    // Dynamic telemetry calculated from progressFraction
    val distanceRemainingKm = String.format(java.util.Locale.US, "%.1f", (2.8f * (1f - progressFraction)).coerceAtLeast(0.05f)).toDoubleOrNull() ?: 1.2
    val etaMinutes = (distanceRemainingKm * 4.2).roundToInt().coerceAtLeast(1)
    val partnerSpeedKmh = if (progressFraction >= 0.98f) 0 else (28 + ((progressFraction * 10).toInt() % 7))
    val currentRoad = when {
        progressFraction < 0.25f -> "Hitec City Main Road, near Cyber Towers"
        progressFraction < 0.50f -> "Approaching Madhapur 100ft Road Junction"
        progressFraction < 0.75f -> "Turning onto Ayyappa Society Main Street"
        progressFraction < 0.95f -> "Entering customer lane • 150m away"
        else -> "Arrived at customer building doorstep"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Track Partner Location", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (progressFraction >= 0.98f) BrandAmberAccent else SuccessGreen)
                            )
                        }
                        Text(
                            text = "Live GPS • Booking ${booking.id}",
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
                actions = {
                    IconButton(onClick = onSupportClick) {
                        Icon(imageVector = Icons.Filled.SupportAgent, contentDescription = "Support", tint = BrandTealPrimary)
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
                .testTag("customer_partner_tracking_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // LIVE GOOGLE MAP PARTNER LOCATION TRACKER
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("live_google_map_container"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column {
                        // Map Canvas Container with Live Overlay Controls
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp)
                                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                                .background(
                                    when (mapLayerType) {
                                        "satellite" -> Color(0xFF1B2A26)
                                        "traffic" -> Color(0xFFEFF5F2)
                                        else -> Color(0xFFE8ECE9)
                                    }
                                )
                                .pointerInput(Unit) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        mapOffset = Offset(
                                            x = (mapOffset.x + dragAmount.x).coerceIn(-120f, 120f),
                                            y = (mapOffset.y + dragAmount.y).coerceIn(-80f, 80f)
                                        )
                                    }
                                }
                        ) {
                            // Custom Google Map Canvas: roads, customer pin, moving partner pin, route polyline
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height

                                // Draw simulated Hyderabad road grid
                                val roadColor = if (mapLayerType == "satellite") Color(0xFF334A3E) else Color(0xFFD6DDD8)
                                val majorRoadColor = if (mapLayerType == "satellite") Color(0xFF4C6B5A) else Color(0xFFFFFFFF)

                                // Minor grid roads
                                for (i in 0..6) {
                                    val yPos = (h / 6) * i + mapOffset.y * 0.5f
                                    drawLine(
                                        color = roadColor,
                                        start = Offset(0f, yPos),
                                        end = Offset(w, yPos),
                                        strokeWidth = 3f * zoomLevel
                                    )
                                }
                                for (i in 0..5) {
                                    val xPos = (w / 5) * i + mapOffset.x * 0.5f
                                    drawLine(
                                        color = roadColor,
                                        start = Offset(xPos, 0f),
                                        end = Offset(xPos, h),
                                        strokeWidth = 3f * zoomLevel
                                    )
                                }

                                // Major arterial roads (e.g. Hitec City road)
                                drawLine(
                                    color = majorRoadColor,
                                    start = Offset(0f, h * 0.75f + mapOffset.y),
                                    end = Offset(w, h * 0.25f + mapOffset.y),
                                    strokeWidth = 10f * zoomLevel
                                )
                                drawLine(
                                    color = majorRoadColor,
                                    start = Offset(w * 0.2f + mapOffset.x, 0f),
                                    end = Offset(w * 0.85f + mapOffset.x, h),
                                    strokeWidth = 8f * zoomLevel
                                )

                                // Traffic overlay if selected
                                if (mapLayerType == "traffic") {
                                    drawLine(
                                        color = Color(0xFF4CAF50),
                                        start = Offset(0f, h * 0.75f + mapOffset.y),
                                        end = Offset(w * 0.5f, h * 0.5f + mapOffset.y),
                                        strokeWidth = 4f
                                    )
                                    drawLine(
                                        color = Color(0xFFFF9800),
                                        start = Offset(w * 0.5f, h * 0.5f + mapOffset.y),
                                        end = Offset(w, h * 0.25f + mapOffset.y),
                                        strokeWidth = 4f
                                    )
                                }

                                // Route path from Partner starting spot (Hitec City) to Customer Home (Madhapur)
                                val startPoint = Offset(w * 0.18f + mapOffset.x, h * 0.82f + mapOffset.y)
                                val waypoint1 = Offset(w * 0.38f + mapOffset.x, h * 0.58f + mapOffset.y)
                                val waypoint2 = Offset(w * 0.58f + mapOffset.x, h * 0.42f + mapOffset.y)
                                val customerPoint = Offset(w * 0.82f + mapOffset.x, h * 0.22f + mapOffset.y)

                                val routePath = Path().apply {
                                    moveTo(startPoint.x, startPoint.y)
                                    lineTo(waypoint1.x, waypoint1.y)
                                    lineTo(waypoint2.x, waypoint2.y)
                                    lineTo(customerPoint.x, customerPoint.y)
                                }

                                // Draw Route Polyline (Teal glowing line)
                                drawPath(
                                    path = routePath,
                                    color = Color(0xFF0F766E).copy(alpha = 0.3f),
                                    style = Stroke(width = 12f * zoomLevel, cap = StrokeCap.Round)
                                )
                                drawPath(
                                    path = routePath,
                                    color = Color(0xFF0D9488),
                                    style = Stroke(
                                        width = 6f * zoomLevel,
                                        cap = StrokeCap.Round,
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(24f, 12f), 0f)
                                    )
                                )

                                // Customer Destination Home Pin (Static target)
                                drawCircle(
                                    color = Color(0xFFE11D48).copy(alpha = 0.25f),
                                    radius = 24f * zoomLevel,
                                    center = customerPoint
                                )
                                drawCircle(
                                    color = Color(0xFFE11D48),
                                    radius = 12f * zoomLevel,
                                    center = customerPoint
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 5f * zoomLevel,
                                    center = customerPoint
                                )

                                // Current Partner Live Location (Interpolated along route)
                                val partnerX = when {
                                    progressFraction < 0.35f -> startPoint.x + (waypoint1.x - startPoint.x) * (progressFraction / 0.35f)
                                    progressFraction < 0.70f -> waypoint1.x + (waypoint2.x - waypoint1.x) * ((progressFraction - 0.35f) / 0.35f)
                                    else -> waypoint2.x + (customerPoint.x - waypoint2.x) * ((progressFraction - 0.70f) / 0.30f)
                                }
                                val partnerY = when {
                                    progressFraction < 0.35f -> startPoint.y + (waypoint1.y - startPoint.y) * (progressFraction / 0.35f)
                                    progressFraction < 0.70f -> waypoint1.y + (waypoint2.y - waypoint1.y) * ((progressFraction - 0.35f) / 0.35f)
                                    else -> waypoint2.y + (customerPoint.y - waypoint2.y) * ((progressFraction - 0.70f) / 0.30f)
                                }
                                val partnerPoint = Offset(partnerX, partnerY)

                                // Pulsing Radar Beacon
                                drawCircle(
                                    color = Color(0xFF0D9488).copy(alpha = (1f - (radarPulse / 35f)).coerceIn(0f, 0.7f)),
                                    radius = radarPulse * zoomLevel,
                                    center = partnerPoint
                                )

                                // Partner Marker Pin
                                drawCircle(
                                    color = Color(0xFF0F766E),
                                    radius = 16f * zoomLevel,
                                    center = partnerPoint
                                )
                                drawCircle(
                                    color = Color(0xFFF59E0B),
                                    radius = 9f * zoomLevel,
                                    center = partnerPoint
                                )
                            }

                            // Top Left: Google Maps Hyderabad Watermark & Live GPS Tag
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .align(Alignment.TopStart),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(alpha = 0.95f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.NearMe,
                                            contentDescription = null,
                                            tint = BrandTealDark,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Google Maps • Hyderabad",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Slate900
                                        )
                                    }
                                }
                            }

                            // Top Right: Map Layer Switcher
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .align(Alignment.TopEnd),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.95f))
                                        .clickable {
                                            mapLayerType = when (mapLayerType) {
                                                "standard" -> "traffic"
                                                "traffic" -> "satellite"
                                                else -> "standard"
                                            }
                                        }
                                        .padding(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Layers,
                                        contentDescription = "Map Layer",
                                        tint = Slate900,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Bottom Right: Map Zoom & Recenter Controls
                            Column(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .align(Alignment.BottomEnd),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .clickable { zoomLevel = (zoomLevel + 0.2f).coerceAtMost(2.0f) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Filled.ZoomIn, contentDescription = "Zoom In", tint = Slate900, modifier = Modifier.size(18.dp))
                                }
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .clickable { zoomLevel = (zoomLevel - 0.2f).coerceAtLeast(0.6f) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Filled.ZoomOut, contentDescription = "Zoom Out", tint = Slate900, modifier = Modifier.size(18.dp))
                                }
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(BrandTealPrimary)
                                        .clickable { mapOffset = Offset.Zero },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Filled.MyLocation, contentDescription = "Recenter", tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }

                            // Bottom Left: Live ETA Card Overlaid on Map
                            Box(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .align(Alignment.BottomStart)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Slate900.copy(alpha = 0.90f))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (progressFraction >= 0.98f) BrandAmberAccent else SuccessGreen)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = if (progressFraction >= 0.98f) "ARRIVED AT DOORSTEP" else "ARRIVING IN ~${etaMinutes} MINS",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 11.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = if (progressFraction >= 0.98f) "Share arrival OTP below" else "$distanceRemainingKm km away • ${partnerSpeedKmh} km/h",
                                            fontSize = 10.sp,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }
                        }

                        // Bottom telemetry bar under map
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BrandTealDark)
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.DirectionsBike,
                                    contentDescription = null,
                                    tint = BrandAmberAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = currentRoad,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Vehicle: Honda Activa (TS 09 EJ 4821)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.75f)
                                    )
                                }
                            }

                            IconButton(
                                onClick = { isLiveTrackingRunning = !isLiveTrackingRunning },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = "Sync",
                                    tint = BrandAmberAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // SECURE ARRIVAL OTP CARD FOR CUSTOMER (Prominent Security Mechanism)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("customer_arrival_otp_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandAmberContainer),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandAmberAccent.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Security,
                                contentDescription = null,
                                tint = BrandAmberOnContainer,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CUSTOMER ARRIVAL VERIFICATION OTP",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                color = BrandAmberOnContainer
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = booking.arrivalOtp,
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 8.sp
                                ),
                                color = Slate900
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(booking.arrivalOtp))
                                    viewModel.showMessage("OTP copied: ${booking.arrivalOtp}")
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ContentCopy,
                                    contentDescription = "Copy OTP",
                                    tint = Slate900
                                )
                            }
                        }

                        Text(
                            text = "Please share this 4-digit code ONLY when the partner physically arrives at your premises. Do NOT share over phone call.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = Slate600,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // ASSIGNED PARTNER PROFILE & DIRECT CALL
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("assigned_partner_profile_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(BrandTealPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Person,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = proName,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Slate900
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Filled.Verified,
                                            contentDescription = "Police Verified",
                                            tint = SuccessGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = "${pro?.specialty ?: "General Task Partner"} • ${pro?.experienceYears ?: 4} yrs exp",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Slate600
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = null,
                                            tint = BrandAmberAccent,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "${pro?.rating ?: 4.9} (${pro?.reviewsCount ?: 620} reviews • Hyderabad Certified)",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Slate500
                                        )
                                    }
                                }
                            }

                            // Call button for customer to speak with partner
                            val phone = pro?.phone ?: "+91 98765 12345"
                            IconButton(
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                        context.startActivity(intent)
                                    } catch (_: Exception) {
                                        viewModel.showMessage("Calling partner: $phone")
                                    }
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(BrandTealContainer)
                                    .size(46.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Call,
                                    contentDescription = "Call Partner",
                                    tint = BrandTealDark
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Slate100)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Police Background Screening Verified",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Slate600
                                )
                            }
                            Text(
                                text = "Hyderabad Guild Certified",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = BrandTealDark
                            )
                        }
                    }
                }
            }

            // BOOKING SUMMARY & DESTINATION DETAILS
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Destination & Service Summary",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Service: ${booking.serviceTitle}", fontWeight = FontWeight.SemiBold)
                        Text(text = "Plan: ${booking.variantName}", style = MaterialTheme.typography.bodySmall, color = Slate600)
                        Text(text = "Deliver to: ${booking.address.houseNumber}, ${booking.address.streetArea}, ${booking.address.city}", style = MaterialTheme.typography.bodySmall, color = Slate600)
                        if (booking.notes.isNotBlank()) {
                            Text(text = "Task details: ${booking.notes}", style = MaterialTheme.typography.bodySmall, color = Slate600)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = Slate100)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Amount (${booking.paymentMethod})", fontWeight = FontWeight.Bold)
                            Text("₹${booking.totalAmount}", fontWeight = FontWeight.ExtraBold, color = BrandTealDark)
                        }
                    }
                }
            }

            // CUSTOMER SAFETY ASSISTANCE & CANCELLATION
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate100)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Security,
                                contentDescription = null,
                                tint = BrandTealDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "HomeHelp 24x7 Customer Safety Guarantee",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = BrandTealDark
                            )
                        }
                        Text(
                            text = "All active partners are live-tracked by Hyderabad Operations Control. For any urgent help, reach 24x7 support or emergency response.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate600
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onSupportClick,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Contact Support")
                            }

                            if (booking.status.stepIndex < BookingStatus.ARRIVED.stepIndex) {
                                OutlinedButton(
                                    onClick = { showCancelDialog = true },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
                                ) {
                                    Text("Cancel Task")
                                }
                            } else {
                                Button(
                                    onClick = { showReviewDialog = true },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandAmberAccent)
                                ) {
                                    Text("Rate Partner ★", color = Slate900, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Review Dialog
    if (showReviewDialog) {
        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = { Text("Rate Your Partner", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("How was your experience with $proName?")
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        (1..5).forEach { star ->
                            IconButton(onClick = { reviewRating = star }) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "$star Stars",
                                    tint = if (star <= reviewRating) BrandAmberAccent else Slate300,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = reviewComment,
                        onValueChange = { reviewComment = it },
                        label = { Text("Feedback for Partner") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitReview(
                            bookingId = booking.id,
                            serviceTitle = booking.serviceTitle,
                            proName = proName,
                            rating = reviewRating,
                            comment = reviewComment
                        )
                        showReviewDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandTealPrimary)
                ) {
                    Text("Submit Review")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Cancel Dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Task Booking?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Are you sure you want to cancel this booking? Free cancellation is allowed before partner arrival.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelBooking(booking.id)
                        showCancelDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Yes, Cancel")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) { Text("Keep Booking") }
            }
        )
    }
}
