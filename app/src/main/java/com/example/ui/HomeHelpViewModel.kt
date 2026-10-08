package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.mock.InitialHyderabadServices
import com.example.data.model.Address
import com.example.data.model.AppNotification
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.data.model.Coupon
import com.example.data.model.CustomerReview
import com.example.data.model.ProfessionalInfo
import com.example.data.model.ServiceAddOn
import com.example.data.model.ServiceCategory
import com.example.data.model.ServiceItem
import com.example.data.model.ServiceVariant
import com.example.data.model.SupportTicket
import com.example.data.repository.HomeHelpRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

data class BookingDraft(
    val service: ServiceItem? = null,
    val selectedVariant: ServiceVariant? = null,
    val selectedAddOns: Set<ServiceAddOn> = emptySet(),
    val selectedAddress: Address? = null,
    val scheduledDate: String = "Today",
    val scheduledTimeSlot: String = "11:00 AM - 01:00 PM",
    val notes: String = "",
    val appliedCoupon: Coupon? = null,
    val paymentMethod: String = "UPI (Google Pay / PhonePe)"
) {
    val variantPrice: Int get() = selectedVariant?.price ?: service?.startingPrice ?: 0
    val addOnsTotal: Int get() = selectedAddOns.sumOf { it.price }
    val baseAmount: Int get() = variantPrice + addOnsTotal
    val platformFee: Int get() = 49
    val taxes: Int get() = ((baseAmount + platformFee) * 0.18).toInt()
    val discountAmount: Int get() = appliedCoupon?.discountAmount ?: 0
    val finalTotal: Int get() = maxOf(0, baseAmount + platformFee + taxes - discountAmount)
}

class HomeHelpViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HomeHelpRepository

    // Location state
    private val _currentLocation = MutableStateFlow("Madhapur, Hyderabad 500081")
    val currentLocation: StateFlow<String> = _currentLocation.asStateFlow()

    // Search state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Active Category filter
    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()

    // Draft booking state for booking workflow
    private val _bookingDraft = MutableStateFlow(BookingDraft())
    val bookingDraft: StateFlow<BookingDraft> = _bookingDraft.asStateFlow()

    // Transient UI toast or banner message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        repository = HomeHelpRepository(
            addressDao = db.addressDao(),
            bookingDao = db.bookingDao(),
            notificationDao = db.notificationDao(),
            supportDao = db.supportDao(),
            reviewDao = db.reviewDao(),
            userDao = db.userDao()
        )
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    val activeUser: StateFlow<com.example.data.model.CustomerUser?> = repository.activeUser.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun registerCustomer(
        name: String,
        phone: String,
        email: String,
        password: String,
        area: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.registerCustomer(
                name = name,
                phone = phone,
                email = email,
                password = password,
                area = area
            )
            result.onSuccess { user ->
                showMessage("Welcome ${user.name}! Registered successfully with Customer ID: ${user.id}")
                onSuccess()
            }.onFailure { err ->
                onError(err.message ?: "Registration failed")
            }
        }
    }

    fun loginCustomer(
        phoneOrEmail: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.loginCustomer(phoneOrEmail, password)
            result.onSuccess { user ->
                showMessage("Welcome back, ${user.name}!")
                onSuccess()
            }.onFailure { err ->
                onError(err.message ?: "Login failed")
            }
        }
    }

    fun logoutCustomer() {
        viewModelScope.launch {
            repository.logout()
            showMessage("You have logged out successfully")
        }
    }

    val categories: List<ServiceCategory> = repository.categories
    val services: List<ServiceItem> = repository.services

    val addresses: StateFlow<List<Address>> = repository.addresses.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allBookings: StateFlow<List<Booking>> = repository.allBookings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val activeBooking: StateFlow<Booking?> = repository.activeBookingFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val notifications: StateFlow<List<AppNotification>> = repository.notifications.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val unreadNotificationCount: StateFlow<Int> = repository.unreadNotificationCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val supportTickets: StateFlow<List<SupportTicket>> = repository.supportTickets.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val reviews: StateFlow<List<CustomerReview>> = repository.reviews.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setLocation(location: String) {
        _currentLocation.value = location
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(categoryId: String?) {
        _selectedCategoryId.value = categoryId
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    // Booking draft mutations
    fun initBookingDraft(service: ServiceItem) {
        val defaultVariant = service.variants.firstOrNull()
        val defaultAddr = addresses.value.firstOrNull { it.isDefault } ?: addresses.value.firstOrNull()
        _bookingDraft.value = BookingDraft(
            service = service,
            selectedVariant = defaultVariant,
            selectedAddOns = emptySet(),
            selectedAddress = defaultAddr,
            scheduledDate = "Tomorrow",
            scheduledTimeSlot = "10:00 AM - 12:00 PM",
            notes = "",
            appliedCoupon = null
        )
    }

    fun selectVariant(variant: ServiceVariant) {
        _bookingDraft.value = _bookingDraft.value.copy(selectedVariant = variant)
    }

    fun toggleAddOn(addon: ServiceAddOn) {
        val current = _bookingDraft.value.selectedAddOns.toMutableSet()
        if (current.contains(addon)) {
            current.remove(addon)
        } else {
            current.add(addon)
        }
        _bookingDraft.value = _bookingDraft.value.copy(selectedAddOns = current)
    }

    fun selectAddress(address: Address) {
        _bookingDraft.value = _bookingDraft.value.copy(selectedAddress = address)
    }

    fun setSchedule(date: String, slot: String) {
        _bookingDraft.value = _bookingDraft.value.copy(scheduledDate = date, scheduledTimeSlot = slot)
    }

    fun setBookingNotes(notes: String) {
        _bookingDraft.value = _bookingDraft.value.copy(notes = notes)
    }

    fun setPaymentMethod(method: String) {
        _bookingDraft.value = _bookingDraft.value.copy(paymentMethod = method)
    }

    fun applyCoupon(code: String): Boolean {
        val coupon = InitialHyderabadServices.COUPONS.find { it.code.equals(code.trim(), ignoreCase = true) }
        return if (coupon != null) {
            val base = _bookingDraft.value.baseAmount
            if (base >= coupon.minOrderValue) {
                _bookingDraft.value = _bookingDraft.value.copy(appliedCoupon = coupon)
                showMessage("Coupon ${coupon.code} applied! Saved ₹${coupon.discountAmount}")
                true
            } else {
                showMessage("Min order value for ${coupon.code} is ₹${coupon.minOrderValue}")
                false
            }
        } else {
            showMessage("Invalid coupon code")
            false
        }
    }

    fun removeCoupon() {
        _bookingDraft.value = _bookingDraft.value.copy(appliedCoupon = null)
        showMessage("Coupon removed")
    }

    fun confirmBooking(onSuccess: (String) -> Unit) {
        val draft = _bookingDraft.value
        val service = draft.service ?: return
        val user = activeUser.value
        val contactName = user?.name ?: "Customer"
        val contactPhone = if (user != null) "+91 ${user.phone}" else "+91 98765 43210"

        val address = draft.selectedAddress ?: addresses.value.firstOrNull() ?: Address(
            id = 1,
            label = "Home",
            houseNumber = "Flat 101, Lakeview Residency",
            streetArea = user?.primaryArea ?: "Madhapur, Hyderabad",
            landmark = "Near Metro",
            city = "Hyderabad",
            pincode = "500081",
            contactName = contactName,
            contactPhone = contactPhone,
            isDefault = true
        )

        val phoneSuffix = user?.phone?.takeLast(4) ?: "5001"
        val bookingSeq = allBookings.value.size + 1
        val newBookingId = "HH-HYD-$phoneSuffix-${String.format(java.util.Locale.US, "%03d", bookingSeq)}"
        val otp = String.format(java.util.Locale.US, "%04d", 4100 + (bookingSeq * 67) % 4900)
        val randomPro = InitialHyderabadServices.TOP_PROS.random()

        val addOnsSummary = draft.selectedAddOns.joinToString(", ") { it.name }.ifEmpty { "None" }

        val newBooking = Booking(
            id = newBookingId,
            serviceId = service.id,
            serviceTitle = service.title,
            categoryId = service.categoryId,
            variantName = draft.selectedVariant?.name ?: "Standard Service",
            variantPrice = draft.variantPrice,
            addOnsSummary = addOnsSummary,
            addOnsTotal = draft.addOnsTotal,
            baseAmount = draft.baseAmount,
            platformFee = draft.platformFee,
            taxes = draft.taxes,
            discountAmount = draft.discountAmount,
            totalAmount = draft.finalTotal,
            scheduledDate = draft.scheduledDate,
            scheduledTimeSlot = draft.scheduledTimeSlot,
            address = address,
            status = BookingStatus.CONFIRMED,
            arrivalOtp = otp,
            assignedPro = randomPro,
            paymentMethod = draft.paymentMethod,
            paymentStatus = if (draft.paymentMethod.startsWith("UPI") || draft.paymentMethod.startsWith("Card")) "Paid Online" else "Pay After Service",
            createdAt = System.currentTimeMillis(),
            notes = draft.notes
        )

        viewModelScope.launch {
            repository.createBooking(newBooking)
            onSuccess(newBookingId)
        }
    }

    // Tracking state machine controls
    fun advanceBookingStatus(bookingId: String, currentStatus: BookingStatus) {
        viewModelScope.launch {
            val nextStatus = when (currentStatus) {
                BookingStatus.CONFIRMED -> BookingStatus.PROFESSIONAL_ASSIGNED
                BookingStatus.PROFESSIONAL_ASSIGNED -> BookingStatus.EN_ROUTE
                BookingStatus.EN_ROUTE -> BookingStatus.ARRIVED
                BookingStatus.ARRIVED -> BookingStatus.OTP_VERIFIED
                BookingStatus.OTP_VERIFIED -> BookingStatus.SERVICE_STARTED
                BookingStatus.SERVICE_STARTED -> BookingStatus.SERVICE_COMPLETED
                BookingStatus.SERVICE_COMPLETED -> BookingStatus.PAID
                else -> currentStatus
            }
            if (nextStatus != currentStatus) {
                repository.updateBookingStatus(bookingId, nextStatus)
                showMessage("Status updated: ${nextStatus.displayName}")
            }
        }
    }

    fun verifyArrivalOtp(bookingId: String, expectedOtp: String, enteredOtp: String, onResult: (Boolean) -> Unit) {
        if (enteredOtp.trim() == expectedOtp.trim()) {
            viewModelScope.launch {
                repository.updateBookingStatus(bookingId, BookingStatus.OTP_VERIFIED)
                showMessage("OTP Verified successfully! Technician can start service.")
                onResult(true)
            }
        } else {
            showMessage("Invalid OTP. Please check the 4-digit code shown to customer.")
            onResult(false)
        }
    }

    fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            repository.cancelBooking(bookingId)
            showMessage("Booking $bookingId cancelled.")
        }
    }

    // Address management
    fun addNewAddress(address: Address) {
        viewModelScope.launch {
            repository.saveAddress(address)
            showMessage("Address added for ${address.label}")
        }
    }

    fun setDefaultAddress(id: Long) {
        viewModelScope.launch {
            repository.setDefaultAddress(id)
            showMessage("Default address updated")
        }
    }

    fun deleteAddress(id: Long) {
        viewModelScope.launch {
            repository.deleteAddress(id)
            showMessage("Address removed")
        }
    }

    // Notifications
    fun markNotificationAsRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
            showMessage("All notifications marked as read")
        }
    }

    // Support
    fun submitSupportTicket(bookingId: String?, category: String, subject: String, message: String) {
        viewModelScope.launch {
            val ticket = SupportTicket(
                id = "TKT-${Random.nextInt(10000, 99999)}",
                bookingId = bookingId,
                category = category,
                subject = subject,
                message = message,
                status = "Open",
                priority = "Normal",
                createdAt = System.currentTimeMillis()
            )
            repository.createSupportTicket(ticket)
            showMessage("Support ticket ${ticket.id} submitted. Our Hyderabad team will contact you within 30 mins.")
        }
    }

    // Review
    fun submitReview(bookingId: String, serviceTitle: String, proName: String, rating: Int, comment: String) {
        viewModelScope.launch {
            val review = CustomerReview(
                id = "REV-${Random.nextInt(1000, 9999)}",
                bookingId = bookingId,
                serviceTitle = serviceTitle,
                proName = proName,
                rating = rating,
                comment = comment,
                createdAt = System.currentTimeMillis()
            )
            repository.addReview(review)
            showMessage("Thank you! Review submitted for $proName.")
        }
    }

    // Configurable default rate for Hire a Partner
    private val _partnerHourlyRate = MutableStateFlow(150)
    val partnerHourlyRate: StateFlow<Int> = _partnerHourlyRate.asStateFlow()

    // Hire a partner draft state
    private val _partnerHireDraft = MutableStateFlow(com.example.data.model.PartnerHireDraft())
    val partnerHireDraft: StateFlow<com.example.data.model.PartnerHireDraft> = _partnerHireDraft.asStateFlow()

    private val _partnerPool = listOf(
        ProfessionalInfo(
            id = "partner_rajesh",
            name = "Rajesh Kumar",
            phone = "+91 98492 88123",
            rating = 4.92,
            reviewsCount = 480,
            experienceYears = 5,
            isPoliceVerified = true,
            specialty = "General Task Partner & Helper",
            completedJobs = 480
        ),
        ProfessionalInfo(
            id = "partner_vikram",
            name = "Vikram Singh",
            phone = "+91 97011 44521",
            rating = 4.88,
            reviewsCount = 310,
            experienceYears = 4,
            isPoliceVerified = true,
            specialty = "All-Round Assistant & Shifting Specialist",
            completedJobs = 310
        ),
        ProfessionalInfo(
            id = "partner_suresh",
            name = "Suresh Goud",
            phone = "+91 94400 34567",
            rating = 4.89,
            reviewsCount = 380,
            experienceYears = 6,
            isPoliceVerified = true,
            specialty = "Handyman & General Assistant",
            completedJobs = 490
        ),
        ProfessionalInfo(
            id = "partner_anil",
            name = "Anil Krishna",
            phone = "+91 91210 98765",
            rating = 4.86,
            reviewsCount = 260,
            experienceYears = 4,
            isPoliceVerified = true,
            specialty = "Home Helper & Assembler",
            completedJobs = 340
        )
    )
    private var currentPartnerIndex = 0

    fun updatePartnerHourlyRate(newRate: Int) {
        _partnerHourlyRate.value = newRate
        _partnerHireDraft.value = _partnerHireDraft.value.copy(hourlyRate = newRate)
    }

    fun initPartnerHireFlow() {
        val currentLoc = _currentLocation.value
        _partnerHireDraft.value = com.example.data.model.PartnerHireDraft(
            hourlyRate = _partnerHourlyRate.value,
            selectedLocation = currentLoc,
            requiredHours = 2,
            workDescription = "",
            voiceAudioRecorded = false,
            voiceDurationSeconds = 0,
            partnerRequestStatus = com.example.data.model.PartnerRequestStatus.DRAFT
        )
    }

    fun setPartnerVoiceRecording(durationSeconds: Int) {
        val desc = "Customer Voice Recording (${durationSeconds}s)"
        _partnerHireDraft.value = _partnerHireDraft.value.copy(
            voiceAudioRecorded = true,
            voiceDurationSeconds = durationSeconds,
            workDescription = desc,
            partnerRequestStatus = com.example.data.model.PartnerRequestStatus.DRAFT
        )
    }

    fun setPartnerMapLocation(location: String, coordinates: String) {
        _partnerHireDraft.value = _partnerHireDraft.value.copy(
            selectedLocation = location,
            mapCoordinates = coordinates
        )
    }

    fun setPartnerWorkDescription(description: String) {
        _partnerHireDraft.value = _partnerHireDraft.value.copy(
            workDescription = description
        )
    }

    fun setPartnerRequiredHours(hours: Int) {
        val clamped = hours.coerceIn(1, 12)
        _partnerHireDraft.value = _partnerHireDraft.value.copy(
            requiredHours = clamped
        )
    }

    fun setPartnerLocation(location: String) {
        _partnerHireDraft.value = _partnerHireDraft.value.copy(
            selectedLocation = location
        )
    }

    fun findPartner() {
        _partnerHireDraft.value = _partnerHireDraft.value.copy(
            partnerRequestStatus = com.example.data.model.PartnerRequestStatus.FINDING_PARTNER
        )
        currentPartnerIndex = 0
        viewModelScope.launch {
            kotlinx.coroutines.delay(2200)
            val partner = _partnerPool[currentPartnerIndex % _partnerPool.size]
            val distance = String.format(java.util.Locale.US, "%.1f", 0.8 + (currentPartnerIndex * 0.4)).toDoubleOrNull() ?: 1.2
            _partnerHireDraft.value = _partnerHireDraft.value.copy(
                candidatePartner = partner,
                partnerDistanceKm = distance,
                partnerRequestStatus = com.example.data.model.PartnerRequestStatus.PARTNER_ACCEPTED,
                estimatedArrivalMins = (10 + (distance * 5)).toInt()
            )
        }
    }

    fun confirmPartnerBooking(onConfirmed: (String) -> Unit) {
        val draft = _partnerHireDraft.value
        val partner = draft.candidatePartner ?: _partnerPool.first()
        val user = activeUser.value
        val contactName = user?.name ?: "Customer"
        val contactPhone = if (user != null) "+91 ${user.phone}" else "+91 98765 43210"

        val userSuffix = user?.phone?.takeLast(4) ?: "5001"
        val bookingSeq = allBookings.value.size + 1
        val newBookingId = "HH-PARTNER-$userSuffix-${String.format(java.util.Locale.US, "%03d", bookingSeq)}"
        val otp = String.format(java.util.Locale.US, "%04d", 4100 + (bookingSeq * 67) % 4900)
        val currentAddr = addresses.value.firstOrNull() ?: Address(
            id = 1,
            label = "Current Location",
            houseNumber = "Service Location",
            streetArea = draft.selectedLocation,
            city = "Hyderabad",
            pincode = "500081",
            contactName = contactName,
            contactPhone = contactPhone
        )

        val totalAmt = draft.totalAmount
        val booking = Booking(
            id = newBookingId,
            serviceId = "hire_a_partner",
            serviceTitle = "Hire a Partner (General Assistance)",
            categoryId = "general_partner",
            variantName = "${draft.requiredHours} Hours @ ₹${draft.hourlyRate}/hr",
            variantPrice = totalAmt,
            addOnsSummary = if (draft.voiceAudioRecorded) "Voice Request Recorded (${draft.voiceDurationSeconds}s)" else "Direct Request",
            addOnsTotal = 0,
            baseAmount = totalAmt,
            platformFee = 0,
            taxes = 0,
            discountAmount = 0,
            totalAmount = totalAmt,
            scheduledDate = "Today (Immediate)",
            scheduledTimeSlot = "Starts in ~${draft.estimatedArrivalMins} mins",
            address = currentAddr.copy(streetArea = draft.selectedLocation),
            status = BookingStatus.PROFESSIONAL_ASSIGNED,
            arrivalOtp = otp,
            assignedPro = partner,
            paymentMethod = "Cash / UPI after task completion",
            paymentStatus = "Pending On Completion",
            createdAt = System.currentTimeMillis(),
            notes = draft.workDescription
        )

        viewModelScope.launch {
            repository.createBooking(booking)
            showMessage("Partner ${partner.name} accepted your request!")
            onConfirmed(newBookingId)
        }
    }
}
