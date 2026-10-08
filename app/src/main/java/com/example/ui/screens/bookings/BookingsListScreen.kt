package com.example.ui.screens.bookings

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.ui.HomeHelpViewModel
import com.example.ui.components.StatusChip
import com.example.ui.theme.BrandTealContainer
import com.example.ui.theme.BrandTealDark
import com.example.ui.theme.BrandTealPrimary
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate900

@Composable
fun BookingsListScreen(
    viewModel: HomeHelpViewModel,
    onTrackBooking: (String) -> Unit,
    onBookService: (String) -> Unit
) {
    val allBookings by viewModel.allBookings.collectAsStateWithLifecycle()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Active", "Completed", "Cancelled")

    val activeBookings = allBookings.filter {
        it.status != BookingStatus.SERVICE_COMPLETED &&
                it.status != BookingStatus.PAID &&
                it.status != BookingStatus.REVIEWED &&
                it.status != BookingStatus.CANCELLED
    }

    val completedBookings = allBookings.filter {
        it.status == BookingStatus.SERVICE_COMPLETED ||
                it.status == BookingStatus.PAID ||
                it.status == BookingStatus.REVIEWED
    }

    val cancelledBookings = allBookings.filter {
        it.status == BookingStatus.CANCELLED
    }

    val currentList = when (selectedTabIndex) {
        0 -> activeBookings
        1 -> completedBookings
        else -> cancelledBookings
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("bookings_list_screen")
    ) {
        Text(
            text = "My Bookings",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 12.dp)
        )

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = BrandTealPrimary
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                val count = when (index) {
                    0 -> activeBookings.size
                    1 -> completedBookings.size
                    else -> cancelledBookings.size
                }
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = "$title ($count)",
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == index) BrandTealDark else Slate600
                        )
                    }
                )
            }
        }

        if (currentList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.AssignmentTurnedIn,
                        contentDescription = null,
                        tint = Slate400,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No ${tabs[selectedTabIndex].lowercase()} bookings found",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Book an AC service, cleaning or repair in seconds.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(currentList) { booking ->
                    BookingItemCard(
                        booking = booking,
                        onTrackClick = { onTrackBooking(booking.id) },
                        onRebookClick = { onBookService(booking.serviceId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BookingItemCard(
    booking: Booking,
    onTrackClick: () -> Unit,
    onRebookClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTrackClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = booking.id,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Slate500
                )
                StatusChip(status = booking.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = booking.serviceTitle,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Slate900
            )

            Text(
                text = "${booking.scheduledDate} • ${booking.scheduledTimeSlot}",
                style = MaterialTheme.typography.bodySmall,
                color = Slate600
            )

            Text(
                text = "Address: ${booking.address.houseNumber}, ${booking.address.streetArea}",
                style = MaterialTheme.typography.bodySmall,
                color = Slate500
            )

            booking.assignedPro?.let { pro ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Technician: ${pro.name} (${pro.rating}★)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = BrandTealDark
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₹${booking.totalAmount}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = BrandTealPrimary
                )

                FilledTonalButton(
                    onClick = onTrackClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = BrandTealContainer,
                        contentColor = BrandTealDark
                    )
                ) {
                    Text(
                        text = if (booking.status == BookingStatus.SERVICE_COMPLETED || booking.status == BookingStatus.PAID) "View Invoice / Review" else "Track Live →",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
