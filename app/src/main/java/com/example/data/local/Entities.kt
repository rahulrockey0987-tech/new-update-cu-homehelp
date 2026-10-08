package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Address
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.data.model.ProfessionalInfo

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val label: String,
    val houseNumber: String,
    val streetArea: String,
    val landmark: String,
    val city: String,
    val pincode: String,
    val contactName: String,
    val contactPhone: String,
    val isDefault: Boolean
) {
    fun toDomain(): Address = Address(
        id = id,
        label = label,
        houseNumber = houseNumber,
        streetArea = streetArea,
        landmark = landmark,
        city = city,
        pincode = pincode,
        contactName = contactName,
        contactPhone = contactPhone,
        isDefault = isDefault
    )

    companion object {
        fun fromDomain(address: Address): AddressEntity = AddressEntity(
            id = address.id,
            label = address.label,
            houseNumber = address.houseNumber,
            streetArea = address.streetArea,
            landmark = address.landmark,
            city = address.city,
            pincode = address.pincode,
            contactName = address.contactName,
            contactPhone = address.contactPhone,
            isDefault = address.isDefault
        )
    }
}

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val id: String,
    val serviceId: String,
    val serviceTitle: String,
    val categoryId: String,
    val variantName: String,
    val variantPrice: Int,
    val addOnsSummary: String,
    val addOnsTotal: Int,
    val baseAmount: Int,
    val platformFee: Int,
    val taxes: Int,
    val discountAmount: Int,
    val totalAmount: Int,
    val scheduledDate: String,
    val scheduledTimeSlot: String,
    // Address flattened
    val addressLabel: String,
    val addressHouseNumber: String,
    val addressStreetArea: String,
    val addressLandmark: String,
    val addressCity: String,
    val addressPincode: String,
    val addressContactName: String,
    val addressContactPhone: String,
    // Status
    val status: String,
    val arrivalOtp: String,
    // Pro
    val proId: String?,
    val proName: String?,
    val proPhone: String?,
    val proRating: Double?,
    val proReviewsCount: Int?,
    val proExperienceYears: Int?,
    val proSpecialty: String?,
    val proCompletedJobs: Int?,
    // Payment
    val paymentMethod: String,
    val paymentStatus: String,
    val createdAt: Long,
    val notes: String
) {
    fun toDomain(): Booking {
        val pro = if (proId != null && proName != null) {
            ProfessionalInfo(
                id = proId,
                name = proName,
                phone = proPhone ?: "+91 99887 76655",
                rating = proRating ?: 4.9,
                reviewsCount = proReviewsCount ?: 312,
                experienceYears = proExperienceYears ?: 6,
                isPoliceVerified = true,
                specialty = proSpecialty ?: "Lead Specialist",
                completedJobs = proCompletedJobs ?: 480
            )
        } else null

        val address = Address(
            id = 0,
            label = addressLabel,
            houseNumber = addressHouseNumber,
            streetArea = addressStreetArea,
            landmark = addressLandmark,
            city = addressCity,
            pincode = addressPincode,
            contactName = addressContactName,
            contactPhone = addressContactPhone,
            isDefault = false
        )

        val parsedStatus = try {
            BookingStatus.valueOf(status)
        } catch (_: Exception) {
            BookingStatus.CONFIRMED
        }

        return Booking(
            id = id,
            serviceId = serviceId,
            serviceTitle = serviceTitle,
            categoryId = categoryId,
            variantName = variantName,
            variantPrice = variantPrice,
            addOnsSummary = addOnsSummary,
            addOnsTotal = addOnsTotal,
            baseAmount = baseAmount,
            platformFee = platformFee,
            taxes = taxes,
            discountAmount = discountAmount,
            totalAmount = totalAmount,
            scheduledDate = scheduledDate,
            scheduledTimeSlot = scheduledTimeSlot,
            address = address,
            status = parsedStatus,
            arrivalOtp = arrivalOtp,
            assignedPro = pro,
            paymentMethod = paymentMethod,
            paymentStatus = paymentStatus,
            createdAt = createdAt,
            notes = notes
        )
    }

    companion object {
        fun fromDomain(b: Booking): BookingEntity = BookingEntity(
            id = b.id,
            serviceId = b.serviceId,
            serviceTitle = b.serviceTitle,
            categoryId = b.categoryId,
            variantName = b.variantName,
            variantPrice = b.variantPrice,
            addOnsSummary = b.addOnsSummary,
            addOnsTotal = b.addOnsTotal,
            baseAmount = b.baseAmount,
            platformFee = b.platformFee,
            taxes = b.taxes,
            discountAmount = b.discountAmount,
            totalAmount = b.totalAmount,
            scheduledDate = b.scheduledDate,
            scheduledTimeSlot = b.scheduledTimeSlot,
            addressLabel = b.address.label,
            addressHouseNumber = b.address.houseNumber,
            addressStreetArea = b.address.streetArea,
            addressLandmark = b.address.landmark,
            addressCity = b.address.city,
            addressPincode = b.address.pincode,
            addressContactName = b.address.contactName,
            addressContactPhone = b.address.contactPhone,
            status = b.status.name,
            arrivalOtp = b.arrivalOtp,
            proId = b.assignedPro?.id,
            proName = b.assignedPro?.name,
            proPhone = b.assignedPro?.phone,
            proRating = b.assignedPro?.rating,
            proReviewsCount = b.assignedPro?.reviewsCount,
            proExperienceYears = b.assignedPro?.experienceYears,
            proSpecialty = b.assignedPro?.specialty,
            proCompletedJobs = b.assignedPro?.completedJobs,
            paymentMethod = b.paymentMethod,
            paymentStatus = b.paymentStatus,
            createdAt = b.createdAt,
            notes = b.notes
        )
    }
}

@Entity(tableName = "support_tickets")
data class SupportTicketEntity(
    @PrimaryKey val id: String,
    val bookingId: String?,
    val category: String,
    val subject: String,
    val message: String,
    val status: String,
    val priority: String,
    val createdAt: Long
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean,
    val type: String,
    val bookingId: String?
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val bookingId: String,
    val serviceTitle: String,
    val proName: String,
    val rating: Int,
    val comment: String,
    val createdAt: Long
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String, // Deterministic: CUST-XXXXXXXXXX
    val name: String,
    val phone: String,
    val email: String,
    val passwordHash: String,
    val city: String,
    val primaryArea: String,
    val registeredAt: Long,
    val isLoggedIn: Boolean
) {
    fun toDomain(): com.example.data.model.CustomerUser = com.example.data.model.CustomerUser(
        id = id,
        name = name,
        phone = phone,
        email = email,
        city = city,
        primaryArea = primaryArea,
        registeredAt = registeredAt
    )

    companion object {
        fun fromDomain(user: com.example.data.model.CustomerUser, passwordHash: String, isLoggedIn: Boolean): UserEntity = UserEntity(
            id = user.id,
            name = user.name,
            phone = user.phone,
            email = user.email,
            passwordHash = passwordHash,
            city = user.city,
            primaryArea = user.primaryArea,
            registeredAt = user.registeredAt,
            isLoggedIn = isLoggedIn
        )
    }
}

