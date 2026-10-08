package com.example.data.model

data class ServiceCategory(
    val id: String,
    val name: String,
    val iconName: String,
    val description: String,
    val isPopular: Boolean = false,
    val serviceCount: Int = 4
)

data class ServiceVariant(
    val id: String,
    val name: String,
    val description: String,
    val price: Int,
    val durationMins: Int
)

data class ServiceAddOn(
    val id: String,
    val name: String,
    val price: Int,
    val description: String
)

data class ServiceItem(
    val id: String,
    val categoryId: String,
    val title: String,
    val shortDesc: String,
    val startingPrice: Int,
    val durationMins: Int,
    val rating: Double,
    val reviewCount: Int,
    val isVerified: Boolean = true,
    val whatIsIncluded: List<String>,
    val whatIsNotIncluded: List<String>,
    val faqs: List<Pair<String, String>>,
    val variants: List<ServiceVariant>,
    val addOns: List<ServiceAddOn>
)

data class CustomerUser(
    val id: String, // e.g. "CUST-9876543210" derived deterministically from customer's phone
    val name: String,
    val phone: String,
    val email: String,
    val city: String = "Hyderabad",
    val primaryArea: String = "Madhapur, Hyderabad",
    val registeredAt: Long = System.currentTimeMillis()
)

fun generateCustomerId(phone: String): String {
    val cleanDigits = phone.filter { it.isDigit() }.takeLast(10)
    return if (cleanDigits.length == 10) "CUST-$cleanDigits" else "CUST-${phone.hashCode().toString().takeLast(8)}"
}

data class Address(
    val id: Long = 0,
    val label: String = "Home", // Home, Work, Other
    val houseNumber: String,
    val streetArea: String,
    val landmark: String = "",
    val city: String = "Hyderabad",
    val pincode: String = "500081",
    val contactName: String = "Rahul Sharma",
    val contactPhone: String = "+91 98765 43210",
    val isDefault: Boolean = false
)

enum class BookingStatus(val displayName: String, val stepIndex: Int) {
    CONFIRMED("Booking Confirmed", 1),
    PROFESSIONAL_ASSIGNED("Pro Assigned", 2),
    EN_ROUTE("Pro En Route", 3),
    ARRIVED("Pro Arrived", 4),
    OTP_VERIFIED("OTP Verified", 5),
    SERVICE_STARTED("Service in Progress", 6),
    SERVICE_COMPLETED("Service Completed", 7),
    PAID("Payment Confirmed", 8),
    REVIEWED("Completed & Reviewed", 9),
    CANCELLED("Cancelled", 0)
}

data class ProfessionalInfo(
    val id: String,
    val name: String,
    val phone: String,
    val rating: Double,
    val reviewsCount: Int,
    val experienceYears: Int,
    val isPoliceVerified: Boolean = true,
    val specialty: String,
    val completedJobs: Int = 480
)

data class Booking(
    val id: String,
    val serviceId: String,
    val serviceTitle: String,
    val categoryId: String,
    val variantName: String,
    val variantPrice: Int,
    val addOnsSummary: String,
    val addOnsTotal: Int,
    val baseAmount: Int,
    val platformFee: Int = 49,
    val taxes: Int,
    val discountAmount: Int = 0,
    val totalAmount: Int,
    val scheduledDate: String,
    val scheduledTimeSlot: String,
    val address: Address,
    val status: BookingStatus,
    val arrivalOtp: String,
    val assignedPro: ProfessionalInfo?,
    val paymentMethod: String = "UPI (Google Pay / PhonePe)",
    val paymentStatus: String = "Pending On Service Completion",
    val createdAt: Long = System.currentTimeMillis(),
    val notes: String = ""
)

data class Coupon(
    val code: String,
    val discountAmount: Int,
    val minOrderValue: Int,
    val description: String
)

data class SupportTicket(
    val id: String,
    val bookingId: String?,
    val category: String,
    val subject: String,
    val message: String,
    val status: String = "Open",
    val priority: String = "Normal",
    val createdAt: Long = System.currentTimeMillis()
)

data class CustomerReview(
    val id: String,
    val bookingId: String,
    val serviceTitle: String,
    val proName: String,
    val rating: Int,
    val comment: String,
    val createdAt: Long = System.currentTimeMillis()
)

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val type: String = "BOOKING",
    val bookingId: String? = null
)

enum class PartnerRequestStatus {
    DRAFT,
    RECORDING,
    FINDING_PARTNER,
    PARTNER_ACCEPTED
}

data class PartnerHireDraft(
    val workDescription: String = "Customer voice note",
    val voiceAudioRecorded: Boolean = false,
    val voiceDurationSeconds: Int = 0,
    val requiredHours: Int = 2,
    val hourlyRate: Int = 150,
    val selectedLocation: String = "Madhapur, Hyderabad 500081",
    val mapCoordinates: String = "17.4486° N, 78.3908° E",
    val specificAddress: String = "",
    val partnerRequestStatus: PartnerRequestStatus = PartnerRequestStatus.DRAFT,
    val candidatePartner: ProfessionalInfo? = null,
    val partnerDistanceKm: Double = 1.2,
    val estimatedArrivalMins: Int = 15
) {
    val totalAmount: Int get() = requiredHours * hourlyRate
}
