package com.example.ui.screens.partner

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.mock.InitialHyderabadServices
import com.example.data.model.PartnerRequestStatus
import com.example.data.model.ProfessionalInfo
import com.example.ui.HomeHelpViewModel
import com.example.ui.theme.BrandAmberAccent
import com.example.ui.theme.BrandAmberContainer
import com.example.ui.theme.BrandTealDark
import com.example.ui.theme.BrandTealPrimary
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import kotlinx.coroutines.delay
import java.util.Locale

enum class PartnerHireStep {
    VOICE_RECORD,
    WORK_DETAILS,
    GOOGLE_MAP_LOCATION,
    CONFIRM_SEARCH
}

data class MapLocationData(
    val name: String,
    val locality: String,
    val landmark: String,
    val coordinates: String,
    val lat: Double,
    val lng: Double
)

val HYDERABAD_MAP_SPOTS = listOf(
    MapLocationData("Madhapur Tech Hub", "Madhapur, Hyderabad 500081", "Near Cyber Towers & Metro Stn", "17.4486° N, 78.3908° E", 17.4486, 78.3908),
    MapLocationData("Hitec City Phase 2", "Hitec City, Hyderabad 500081", "Mindspace Business Park", "17.4435° N, 78.3772° E", 17.4435, 78.3772),
    MapLocationData("Gachibowli Junction", "Gachibowli, Hyderabad 500032", "Financial District ORR", "17.4401° N, 78.3489° E", 17.4401, 78.3489),
    MapLocationData("Jubilee Hills Road 36", "Jubilee Hills, Hyderabad 500033", "Near Check Post & Metro", "17.4319° N, 78.4073° E", 17.4319, 78.4073),
    MapLocationData("Banjara Hills Road 12", "Banjara Hills, Hyderabad 500034", "Opposite City Center", "17.4156° N, 78.4347° E", 17.4156, 78.4347),
    MapLocationData("Kondapur Botanical Garden", "Kondapur, Hyderabad 500084", "Near Raghava Stadium", "17.4658° N, 78.3610° E", 17.4658, 78.3610),
    MapLocationData("Begumpet Prakash Nagar", "Begumpet, Hyderabad 500016", "Near Begumpet Airport & Metro", "17.4447° N, 78.4664° E", 17.4447, 78.4664),
    MapLocationData("Secunderabad Clock Tower", "Secunderabad, Hyderabad 500003", "Near Railway Station", "17.4399° N, 78.4983° E", 17.4399, 78.4983)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HirePartnerScreen(
    viewModel: HomeHelpViewModel,
    onBack: () -> Unit,
    onPartnerMatched: (String) -> Unit
) {
    val draft by viewModel.partnerHireDraft.collectAsStateWithLifecycle()
    val hourlyRate by viewModel.partnerHourlyRate.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var currentStep by remember { mutableStateOf(PartnerHireStep.VOICE_RECORD) }

    // Recording State
    var isRecording by remember { mutableStateOf(false) }
    var recordingSeconds by remember { mutableIntStateOf(0) }
    var isPlayingAudio by remember { mutableStateOf(false) }
    var playbackProgress by remember { mutableFloatStateOf(0f) }

    // Map selection state
    var selectedMapSpot by remember { mutableStateOf(HYDERABAD_MAP_SPOTS.first()) }

    // Timer when recording
    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordingSeconds = 0
            while (isRecording) {
                delay(1000)
                recordingSeconds++
            }
        }
    }

    // Audio Playback simulation timer
    LaunchedEffect(isPlayingAudio) {
        if (isPlayingAudio) {
            playbackProgress = 0f
            val totalSteps = 50
            for (i in 1..totalSteps) {
                if (!isPlayingAudio) break
                delay(80)
                playbackProgress = i / totalSteps.toFloat()
            }
            isPlayingAudio = false
            playbackProgress = 0f
        }
    }

    // Permission launcher for microphone
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        isRecording = true
        if (!isGranted) {
            viewModel.showMessage("Using audio simulation mode")
        }
    }

    fun startRecording() {
        val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
        if (permission == PackageManager.PERMISSION_GRANTED) {
            isRecording = true
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    fun stopRecording() {
        isRecording = false
        val finalSeconds = if (recordingSeconds > 0) recordingSeconds else 8
        viewModel.setPartnerVoiceRecording(finalSeconds)
        currentStep = PartnerHireStep.WORK_DETAILS
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Hire a Partner",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Slate900
                        )
                        Text(
                            text = "Voice Request • ₹$hourlyRate/hour",
                            style = MaterialTheme.typography.labelSmall,
                            color = BrandTealDark
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (draft.partnerRequestStatus == PartnerRequestStatus.FINDING_PARTNER ||
                                draft.partnerRequestStatus == PartnerRequestStatus.PARTNER_ACCEPTED
                            ) {
                                viewModel.initPartnerHireFlow()
                                onBack()
                            } else if (currentStep != PartnerHireStep.VOICE_RECORD) {
                                currentStep = when (currentStep) {
                                    PartnerHireStep.WORK_DETAILS -> PartnerHireStep.VOICE_RECORD
                                    PartnerHireStep.GOOGLE_MAP_LOCATION -> PartnerHireStep.WORK_DETAILS
                                    PartnerHireStep.CONFIRM_SEARCH -> PartnerHireStep.GOOGLE_MAP_LOCATION
                                    else -> PartnerHireStep.VOICE_RECORD
                                }
                            } else {
                                onBack()
                            }
                        },
                        modifier = Modifier.testTag("hire_partner_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Slate900
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                // Partner Accepted State (Partner Found)
                draft.partnerRequestStatus == PartnerRequestStatus.PARTNER_ACCEPTED -> {
                    PartnerAcceptedView(
                        partner = draft.candidatePartner,
                        draft = draft,
                        onProceedToTrack = {
                            viewModel.confirmPartnerBooking { newBookingId ->
                                onPartnerMatched(newBookingId)
                            }
                        }
                    )
                }

                // Radar Search Animation
                draft.partnerRequestStatus == PartnerRequestStatus.FINDING_PARTNER -> {
                    SearchingPartnerView(
                        draft = draft,
                        hourlyRate = hourlyRate
                    )
                }

                // Customer Steps
                else -> {
                    when (currentStep) {
                        PartnerHireStep.VOICE_RECORD -> {
                            VoiceRecordOnlyStep(
                                isRecording = isRecording,
                                recordingSeconds = recordingSeconds,
                                hasRecorded = draft.voiceAudioRecorded,
                                voiceDurationSeconds = draft.voiceDurationSeconds,
                                isPlayingAudio = isPlayingAudio,
                                playbackProgress = playbackProgress,
                                onStartRecording = { startRecording() },
                                onStopRecording = { stopRecording() },
                                onTogglePlayAudio = { isPlayingAudio = !isPlayingAudio },
                                onContinue = { currentStep = PartnerHireStep.WORK_DETAILS }
                            )
                        }

                        PartnerHireStep.WORK_DETAILS -> {
                            WorkDetailsVoiceStep(
                                voiceDurationSeconds = draft.voiceDurationSeconds,
                                requiredHours = draft.requiredHours,
                                hourlyRate = hourlyRate,
                                isPlayingAudio = isPlayingAudio,
                                playbackProgress = playbackProgress,
                                onTogglePlayAudio = { isPlayingAudio = !isPlayingAudio },
                                onHoursChange = { viewModel.setPartnerRequiredHours(it) },
                                onRecordAgain = {
                                    isPlayingAudio = false
                                    currentStep = PartnerHireStep.VOICE_RECORD
                                },
                                onProceedToMap = { currentStep = PartnerHireStep.GOOGLE_MAP_LOCATION }
                            )
                        }

                        PartnerHireStep.GOOGLE_MAP_LOCATION -> {
                            GoogleMapLocationPickerStep(
                                selectedSpot = selectedMapSpot,
                                onSelectSpot = { spot ->
                                    selectedMapSpot = spot
                                    viewModel.setPartnerMapLocation(spot.locality, spot.coordinates)
                                },
                                onConfirmLocation = {
                                    viewModel.setPartnerMapLocation(selectedMapSpot.locality, selectedMapSpot.coordinates)
                                    currentStep = PartnerHireStep.CONFIRM_SEARCH
                                }
                            )
                        }

                        PartnerHireStep.CONFIRM_SEARCH -> {
                            FinalReviewAndSearchStep(
                                draft = draft,
                                selectedSpot = selectedMapSpot,
                                hourlyRate = hourlyRate,
                                isPlayingAudio = isPlayingAudio,
                                playbackProgress = playbackProgress,
                                onTogglePlayAudio = { isPlayingAudio = !isPlayingAudio },
                                onChangeMapLocation = { currentStep = PartnerHireStep.GOOGLE_MAP_LOCATION },
                                onFindPartner = {
                                    viewModel.findPartner()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 1. VOICE RECORD ONLY STEP (NO TEXT OPTION)
// =========================================================================
@Composable
private fun VoiceRecordOnlyStep(
    isRecording: Boolean,
    recordingSeconds: Int,
    hasRecorded: Boolean,
    voiceDurationSeconds: Int,
    isPlayingAudio: Boolean,
    playbackProgress: Float,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onTogglePlayAudio: () -> Unit,
    onContinue: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecording) 1.25f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = BrandAmberContainer,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "🎙️ VOICE REQUEST ONLY",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = BrandTealDark,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Text(
                text = "What do you need help with?",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Slate900,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Describe your work using your voice. No typing required.",
                style = MaterialTheme.typography.bodyMedium,
                color = Slate600,
                textAlign = TextAlign.Center
            )
        }

        // Central Microphone Area
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(210.dp)
                    .clip(CircleShape)
            ) {
                // Wave ripple halos
                if (isRecording) {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(BrandTealPrimary.copy(alpha = 0.16f))
                    )
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .scale(pulseScale * 0.9f)
                            .clip(CircleShape)
                            .background(BrandTealPrimary.copy(alpha = 0.28f))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(170.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(BrandTealPrimary.copy(alpha = 0.08f))
                    )
                }

                // Core Circular Record Button
                Surface(
                    shape = CircleShape,
                    color = if (isRecording) Color(0xFFE53935) else BrandTealPrimary,
                    shadowElevation = 10.dp,
                    modifier = Modifier
                        .size(115.dp)
                        .clickable {
                            if (isRecording) onStopRecording() else onStartRecording()
                        }
                        .testTag("voice_only_record_button")
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = if (isRecording) Icons.Filled.Stop else Icons.Filled.Mic,
                            contentDescription = if (isRecording) "Stop Recording" else "Start Voice Recording",
                            tint = Color.White,
                            modifier = Modifier.size(54.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isRecording) {
                Text(
                    text = "Recording Voice...",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFFE53935)
                )
                val mins = recordingSeconds / 60
                val secs = recordingSeconds % 60
                Text(
                    text = String.format(Locale.US, "%02d:%02d", mins, secs),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = Slate900
                )

                Spacer(modifier = Modifier.height(14.dp))
                WaveformBarsAnimated(isActive = true)
                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onStopRecording,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.testTag("stop_voice_recording_btn")
                ) {
                    Icon(Icons.Filled.Stop, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Stop Recording")
                }
            } else {
                Text(
                    text = if (hasRecorded) "Voice Request Recorded (${voiceDurationSeconds}s)" else "Tap to Record",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Slate900
                )
                Text(
                    text = "Speak naturally in your preferred language",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate500
                )

                if (hasRecorded) {
                    Spacer(modifier = Modifier.height(16.dp))
                    // Audio Player Pill
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BrandAmberContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = BrandTealPrimary,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clickable { onTogglePlayAudio() }
                                        .testTag("play_recorded_voice_btn")
                                ) {
                                    Icon(
                                        imageVector = if (isPlayingAudio) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                        contentDescription = "Play voice audio",
                                        tint = Color.White,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Voice Request Recorded",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = BrandTealDark
                                    )
                                    Text(
                                        text = if (isPlayingAudio) "Playing audio note..." else "Tap play to verify your recording",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Slate600
                                    )
                                }
                            }

                            if (isPlayingAudio) {
                                Spacer(modifier = Modifier.height(10.dp))
                                LinearProgressIndicator(
                                    progress = { playbackProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = BrandTealPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Actions
        Column(modifier = Modifier.fillMaxWidth()) {
            if (hasRecorded && !isRecording) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onStartRecording,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Record Again")
                    }

                    Button(
                        onClick = onContinue,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("voice_continue_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandTealPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Continue")
                    }
                }
            } else {
                Text(
                    text = "Your voice recording will be sent directly to verified partners near you.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate500,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

// =========================================================================
// 2. WORK DETAILS STEP (VOICE NOTE + HOURS + PRICING)
// =========================================================================
@Composable
private fun WorkDetailsVoiceStep(
    voiceDurationSeconds: Int,
    requiredHours: Int,
    hourlyRate: Int,
    isPlayingAudio: Boolean,
    playbackProgress: Float,
    onTogglePlayAudio: () -> Unit,
    onHoursChange: (Int) -> Unit,
    onRecordAgain: () -> Unit,
    onProceedToMap: () -> Unit
) {
    val estimatedTotal = requiredHours * hourlyRate

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        item {
            Text(
                text = "Work Details",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Slate900
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Your work description is saved as a voice note.",
                style = MaterialTheme.typography.bodySmall,
                color = Slate600
            )
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Voice Note Summary Card (Replaces text description)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Slate100)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = BrandTealPrimary,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clickable { onTogglePlayAudio() }
                            ) {
                                Icon(
                                    imageVector = if (isPlayingAudio) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                    contentDescription = "Play voice recording",
                                    tint = Color.White,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Customer Voice Recording",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Slate900
                                )
                                Text(
                                    text = "Duration: ${voiceDurationSeconds}s • Ready for Partner",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BrandTealDark
                                )
                            }
                        }

                        IconButton(onClick = onRecordAgain) {
                            Icon(Icons.Filled.Mic, contentDescription = "Record Again", tint = Slate600)
                        }
                    }

                    if (isPlayingAudio) {
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { playbackProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = BrandTealPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Required Hours Selector
        item {
            Text(
                text = "Required Hours",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Slate900
            )
            Text(
                text = "Hourly rate is ₹$hourlyRate/hour",
                style = MaterialTheme.typography.bodySmall,
                color = Slate600
            )
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
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
                        Column {
                            Text(
                                text = "$requiredHours ${if (requiredHours == 1) "Hour" else "Hours"}",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                                color = Slate900
                            )
                            Text(
                                text = "Rate: ₹$hourlyRate/hour",
                                style = MaterialTheme.typography.labelMedium,
                                color = BrandTealDark
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = if (requiredHours > 1) BrandTealPrimary else Slate300,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clickable(enabled = requiredHours > 1) { onHoursChange(requiredHours - 1) }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("–", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "$requiredHours",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Slate900
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Surface(
                                shape = CircleShape,
                                color = BrandTealPrimary,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clickable { onHoursChange(requiredHours + 1) }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("+", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = Slate100)

                    // Pricing Quick Table
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(2 to 300, 3 to 450, 4 to 600, 5 to 750).forEach { (h, amt) ->
                            val isSel = requiredHours == h
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) BrandTealPrimary else Slate100)
                                    .clickable { onHoursChange(h) }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("${h}h", color = if (isSel) Color.White else Slate900, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text("₹$amt", color = if (isSel) Color.White.copy(alpha = 0.9f) else Slate600, fontSize = 10.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Estimated Total Banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(BrandAmberContainer)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Estimated Total ($requiredHours hrs)",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Slate700
                        )
                        Text(
                            text = "₹$estimatedTotal",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = BrandTealDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onProceedToMap,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("select_location_on_map_button"),
                colors = ButtonDefaults.buttonColors(containerColor = BrandTealPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Map, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Select Location on Google Map", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// =========================================================================
// 3. GOOGLE MAP INTERACTIVE LOCATION PICKER STEP
// =========================================================================
@Composable
private fun GoogleMapLocationPickerStep(
    selectedSpot: MapLocationData,
    onSelectSpot: (MapLocationData) -> Unit,
    onConfirmLocation: () -> Unit
) {
    var zoomLevel by remember { mutableIntStateOf(16) }
    var isSatelliteView by remember { mutableStateOf(false) }
    var pinOffset by remember { mutableStateOf(Offset.Zero) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Map Top Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Select Location on Google Map",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Slate900
            )
            Text(
                text = "Tap any Hyderabad hotspot or move map to position pin",
                style = MaterialTheme.typography.bodySmall,
                color = Slate600
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Hyderabad Locality Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(HYDERABAD_MAP_SPOTS) { spot ->
                    val isSelected = spot == selectedSpot
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (isSelected) BrandTealPrimary else Slate100,
                        modifier = Modifier.clickable {
                            onSelectSpot(spot)
                            pinOffset = Offset.Zero
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else BrandTealDark,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = spot.name,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) Color.White else Slate900
                            )
                        }
                    }
                }
            }
        }

        // Interactive Map View Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(if (isSatelliteView) Color(0xFF1E293B) else Color(0xFFE8ECEF))
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        pinOffset = Offset(
                            x = (tapOffset.x - centerX).coerceIn(-130f, 130f),
                            y = (tapOffset.y - centerY).coerceIn(-90f, 90f)
                        )
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        pinOffset = Offset(
                            x = (pinOffset.x + dragAmount.x).coerceIn(-130f, 130f),
                            y = (pinOffset.y + dragAmount.y).coerceIn(-90f, 90f)
                        )
                    }
                }
        ) {
            // Simulated Google Map vector graphics canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Road grid
                val roadColor = if (isSatelliteView) Color(0xFF334155) else Color(0xFFFFFFFF)
                val highwayColor = if (isSatelliteView) Color(0xFF475569) else Color(0xFFFDE68A)
                val parkColor = if (isSatelliteView) Color(0xFF064E3B) else Color(0xFFD1FAE5)
                val waterColor = if (isSatelliteView) Color(0xFF0C4A6E) else Color(0xFFBAE6FD)

                // Water body (e.g. Durgam Cheruvu / Hussain Sagar)
                drawCircle(
                    color = waterColor,
                    radius = 90f * (zoomLevel / 16f),
                    center = Offset(w * 0.25f + pinOffset.x * 0.5f, h * 0.35f + pinOffset.y * 0.5f)
                )

                // Park zone
                drawRect(
                    color = parkColor,
                    topLeft = Offset(w * 0.65f + pinOffset.x * 0.5f, h * 0.2f + pinOffset.y * 0.5f),
                    size = androidx.compose.ui.geometry.Size(140f, 110f)
                )

                // Major Highways & Arterials
                drawLine(
                    color = highwayColor,
                    start = Offset(0f, h * 0.45f + pinOffset.y),
                    end = Offset(w, h * 0.45f + pinOffset.y),
                    strokeWidth = 14f
                )
                drawLine(
                    color = highwayColor,
                    start = Offset(w * 0.52f + pinOffset.x, 0f),
                    end = Offset(w * 0.52f + pinOffset.x, h),
                    strokeWidth = 14f
                )

                // Grid Secondary Streets
                for (i in 1..8) {
                    val y = (h / 8) * i + (pinOffset.y * 0.7f)
                    drawLine(
                        color = roadColor,
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 6f
                    )
                }
                for (i in 1..6) {
                    val x = (w / 6) * i + (pinOffset.x * 0.7f)
                    drawLine(
                        color = roadColor,
                        start = Offset(x, 0f),
                        end = Offset(x, h),
                        strokeWidth = 6f
                    )
                }
            }

            // Google Maps Watermark Badge
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color.White.copy(alpha = 0.85f),
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = "Google Maps",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Slate700,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            // Map Controls (Zoom In/Out, Layer, MyLocation)
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.TopEnd),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Layer Toggle
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { isSatelliteView = !isSatelliteView }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Layers,
                        contentDescription = "Map Layers",
                        tint = if (isSatelliteView) BrandTealPrimary else Slate700,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                // Locate Me Button
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            pinOffset = Offset.Zero
                            onSelectSpot(HYDERABAD_MAP_SPOTS.first())
                        }
                ) {
                    Icon(
                        imageVector = Icons.Filled.MyLocation,
                        contentDescription = "My Location",
                        tint = BrandTealPrimary,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                // Zoom In
                Surface(
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { if (zoomLevel < 20) zoomLevel++ }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Zoom In",
                        tint = Slate700,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                // Zoom Out
                Surface(
                    shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { if (zoomLevel > 12) zoomLevel-- }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Remove,
                        contentDescription = "Zoom Out",
                        tint = Slate700,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            // Central Pinned Location Marker (Google Red Pin)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = pinOffset.x.dp, y = pinOffset.y.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Slate900,
                        modifier = Modifier.padding(bottom = 2.dp)
                    ) {
                        Text(
                            text = selectedSpot.name,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Pinned Location",
                        tint = Color(0xFFEA4335), // Google Red
                        modifier = Modifier.size(48.dp)
                    )

                    // Pin shadow ellipse
                    Box(
                        modifier = Modifier
                            .size(16.dp, 6.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.35f))
                    )
                }
            }
        }

        // Bottom Selected Location Sheet & Confirm Button
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFEA4335).copy(alpha = 0.12f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFFEA4335),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Pinned Google Map Location",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = BrandTealDark
                        )
                        Text(
                            text = selectedSpot.locality,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Slate900
                        )
                        Text(
                            text = "${selectedSpot.landmark} • ${selectedSpot.coordinates}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate500
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onConfirmLocation,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("confirm_map_location_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandTealPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirm This Map Location", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// =========================================================================
// 4. FINAL REVIEW & FIND A PARTNER (VOICE NOTE + MAP LOCATION)
// =========================================================================
@Composable
private fun FinalReviewAndSearchStep(
    draft: com.example.data.model.PartnerHireDraft,
    selectedSpot: MapLocationData,
    hourlyRate: Int,
    isPlayingAudio: Boolean,
    playbackProgress: Float,
    onTogglePlayAudio: () -> Unit,
    onChangeMapLocation: () -> Unit,
    onFindPartner: () -> Unit
) {
    val totalAmount = draft.requiredHours * hourlyRate

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        item {
            Text(
                text = "Ready to Find Your Partner?",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Slate900
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Review your voice request and map location before searching.",
                style = MaterialTheme.typography.bodySmall,
                color = Slate600
            )
            Spacer(modifier = Modifier.height(18.dp))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Voice Note Player
                    Text(
                        text = "Work Description (Voice Recording)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Slate500
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = BrandAmberContainer)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = BrandTealPrimary,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { onTogglePlayAudio() }
                            ) {
                                Icon(
                                    imageVector = if (isPlayingAudio) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                    contentDescription = "Play Voice Note",
                                    tint = Color.White,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Customer Voice Note (${draft.voiceDurationSeconds}s)",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = BrandTealDark
                                )
                                Text(
                                    text = if (isPlayingAudio) "Playing voice note..." else "Partner will listen to this recording",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Slate600
                                )
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = Slate100)

                    // Selected Map Location
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Google Map Location",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Slate500
                        )
                        Text(
                            text = "Change",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = BrandTealPrimary,
                            modifier = Modifier.clickable { onChangeMapLocation() }
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFFEA4335),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = selectedSpot.locality,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Slate900
                            )
                            Text(
                                text = "${selectedSpot.landmark} • ${selectedSpot.coordinates}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate500
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = Slate100)

                    // Hours & Rate
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Required Hours", style = MaterialTheme.typography.labelSmall, color = Slate500)
                            Text("${draft.requiredHours} Hours", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Slate900)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Hourly Rate", style = MaterialTheme.typography.labelSmall, color = Slate500)
                            Text("₹$hourlyRate/hour", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = BrandTealDark)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = Slate100)

                    // Estimated Total
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Estimated Total",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Slate900
                            )
                            Text("Cash or UPI on completion", style = MaterialTheme.typography.labelSmall, color = Slate500)
                        }
                        Text(
                            text = "₹$totalAmount",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = BrandTealDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onFindPartner,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("final_find_partner_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = BrandTealPrimary),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Filled.Navigation, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Find a Partner", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

// =========================================================================
// SEARCHING PARTNER VIEW (RADAR SCANNER)
// =========================================================================
@Composable
private fun SearchingPartnerView(
    draft: com.example.data.model.PartnerHireDraft,
    hourlyRate: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "search_radar")
    val radarScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_scale"
    )
    val radarAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(220.dp)) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .scale(radarScale)
                    .clip(CircleShape)
                    .background(BrandTealPrimary.copy(alpha = radarAlpha))
            )
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .scale(radarScale * 0.8f)
                    .clip(CircleShape)
                    .background(BrandTealPrimary.copy(alpha = radarAlpha * 0.8f))
            )
            Surface(
                shape = CircleShape,
                color = BrandTealPrimary,
                shadowElevation = 8.dp,
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Searching",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Finding a Partner...",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = Slate900
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Looking for an available partner near your pinned map location.",
            style = MaterialTheme.typography.bodyMedium,
            color = Slate600,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Slate100)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Voice Request Attached (${draft.voiceDurationSeconds}s)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = BrandTealDark)
                    Text("${draft.requiredHours}h @ ₹$hourlyRate/hr", style = MaterialTheme.typography.labelSmall, color = Slate600)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color(0xFFEA4335), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = draft.selectedLocation, style = MaterialTheme.typography.labelSmall, color = Slate600)
                }
            }
        }
    }
}

// =========================================================================
// CUSTOMER — PARTNER ACCEPTED VIEW
// =========================================================================
@Composable
private fun PartnerAcceptedView(
    partner: ProfessionalInfo?,
    draft: com.example.data.model.PartnerHireDraft,
    onProceedToTrack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = BrandTealPrimary.copy(alpha = 0.12f),
            modifier = Modifier.size(80.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Success",
                    tint = BrandTealPrimary,
                    modifier = Modifier.size(52.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Partner Found",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Slate900
        )
        Text(
            text = "Partner Accepted Your Request",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = BrandTealDark
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = BrandTealPrimary,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = (partner?.name?.take(1) ?: "R"),
                                color = Color.White,
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = partner?.name ?: "Rajesh Kumar",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Slate900
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = CircleShape,
                                color = BrandTealPrimary.copy(alpha = 0.15f),
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Verified,
                                    contentDescription = "Verified Badge",
                                    tint = BrandTealPrimary,
                                    modifier = Modifier.padding(2.dp)
                                )
                            }
                        }
                        Text(
                            text = partner?.specialty ?: "General Task Partner & Helper",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate600
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = BrandAmberAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${partner?.rating ?: 4.9}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Slate900
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "•  ${partner?.completedJobs ?: 480}+ completed jobs",
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate500
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = Slate100)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Distance", style = MaterialTheme.typography.labelSmall, color = Slate500)
                        Text(text = "${draft.partnerDistanceKm} km away", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Slate900)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Estimated Arrival", style = MaterialTheme.typography.labelSmall, color = Slate500)
                        Text(text = "~${draft.estimatedArrivalMins} mins", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = BrandTealDark)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onProceedToTrack,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("partner_accepted_track_button"),
            colors = ButtonDefaults.buttonColors(containerColor = BrandTealPrimary),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Filled.Navigation, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Track Partner on Live Map", fontWeight = FontWeight.Bold)
        }
    }
}

// =========================================================================
// HELPER: ANIMATED WAVEFORM
// =========================================================================
@Composable
private fun WaveformBarsAnimated(isActive: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform_anim")
    val heights = listOf(14, 28, 44, 22, 50, 36, 18, 42, 30, 48, 20, 38)

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(54.dp)
    ) {
        heights.forEachIndexed { idx, baseH ->
            val animFraction by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(400 + (idx * 50), easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar_$idx"
            )

            val currentHeight = if (isActive) (baseH * animFraction).dp else 8.dp

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(currentHeight)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isActive) BrandTealPrimary else Slate300)
            )
        }
    }
}
