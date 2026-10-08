package com.example.data.repository

import com.example.data.local.AddressDao
import com.example.data.local.AddressEntity
import com.example.data.local.BookingDao
import com.example.data.local.BookingEntity
import com.example.data.local.NotificationDao
import com.example.data.local.NotificationEntity
import com.example.data.local.ReviewDao
import com.example.data.local.ReviewEntity
import com.example.data.local.SupportDao
import com.example.data.local.SupportTicketEntity
import com.example.data.local.UserDao
import com.example.data.local.UserEntity
import com.example.data.mock.InitialHyderabadServices
import com.example.data.model.Address
import com.example.data.model.AppNotification
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.data.model.CustomerReview
import com.example.data.model.CustomerUser
import com.example.data.model.ServiceCategory
import com.example.data.model.ServiceItem
import com.example.data.model.SupportTicket
import com.example.data.model.generateCustomerId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HomeHelpRepository(
    private val addressDao: AddressDao,
    private val bookingDao: BookingDao,
    private val notificationDao: NotificationDao,
    private val supportDao: SupportDao,
    private val reviewDao: ReviewDao,
    private val userDao: UserDao
) {

    // Customer Authentication & Profile (Deterministic, No Duplicates, No Random IDs)
    val activeUser: Flow<CustomerUser?> = userDao.getActiveUserFlow().map { it?.toDomain() }

    suspend fun getActiveUser(): CustomerUser? = userDao.getActiveUser()?.toDomain()

    suspend fun registerCustomer(
        name: String,
        phone: String,
        email: String,
        password: String,
        area: String
    ): Result<CustomerUser> {
        val cleanPhone = phone.filter { it.isDigit() }.takeLast(10)
        if (cleanPhone.length != 10) {
            return Result.failure(IllegalArgumentException("Please enter a valid 10-digit mobile number"))
        }
        val cleanEmail = email.trim().lowercase()
        if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) {
            return Result.failure(IllegalArgumentException("Please enter a valid email address"))
        }
        if (name.trim().isBlank()) {
            return Result.failure(IllegalArgumentException("Please enter your full name"))
        }
        if (password.trim().length < 4) {
            return Result.failure(IllegalArgumentException("Password must be at least 4 characters"))
        }

        // DUPLICATE CHECK: Strictly prevent duplicates
        val existingByPhone = userDao.getUserByPhone(cleanPhone)
        if (existingByPhone != null) {
            return Result.failure(IllegalStateException("Customer with mobile +91 $cleanPhone already registered! Please login with your password."))
        }

        val existingByEmail = userDao.getUserByEmail(cleanEmail)
        if (existingByEmail != null) {
            return Result.failure(IllegalStateException("An account with email $cleanEmail already exists. Please login instead."))
        }

        // DETERMINISTIC ID: strictly based on user's 10-digit phone number (NO random IDs)
        val customerId = generateCustomerId(cleanPhone)

        val newUser = CustomerUser(
            id = customerId,
            name = name.trim(),
            phone = cleanPhone,
            email = cleanEmail,
            city = "Hyderabad",
            primaryArea = area.ifBlank { "Madhapur, Hyderabad 500081" },
            registeredAt = System.currentTimeMillis()
        )

        // Log out any other session and save new user
        userDao.logoutAll()
        val userEntity = UserEntity.fromDomain(
            user = newUser,
            passwordHash = password.trim(),
            isLoggedIn = true
        )
        userDao.insertUser(userEntity)

        // Save default address with customer's real information
        addressDao.clearDefaultFlags()
        addressDao.insertAddress(
            AddressEntity(
                id = 0,
                label = "Home",
                houseNumber = "Primary Residence",
                streetArea = newUser.primaryArea,
                landmark = "Near Locality Landmark",
                city = "Hyderabad",
                pincode = "500081",
                contactName = newUser.name,
                contactPhone = "+91 ${newUser.phone}",
                isDefault = true
            )
        )

        return Result.success(newUser)
    }

    suspend fun loginCustomer(phoneOrEmail: String, password: String): Result<CustomerUser> {
        val query = phoneOrEmail.trim()
        val cleanPhone = query.filter { it.isDigit() }.takeLast(10)

        val userEntity = if (cleanPhone.length == 10) {
            userDao.getUserByPhone(cleanPhone)
        } else {
            userDao.getUserByEmail(query.lowercase())
        }

        if (userEntity == null) {
            return Result.failure(IllegalArgumentException("No registered customer found for '$query'. Please register with your details first."))
        }

        if (userEntity.passwordHash != password.trim()) {
            return Result.failure(IllegalArgumentException("Incorrect password for ${userEntity.name}. Please try again."))
        }

        userDao.logoutAll()
        userDao.setActiveUser(userEntity.id)
        return Result.success(userEntity.toDomain())
    }

    suspend fun logout() {
        userDao.logoutAll()
    }


    val categories: List<ServiceCategory> = InitialHyderabadServices.CATEGORIES
    val services: List<ServiceItem> = InitialHyderabadServices.SERVICES

    fun getServicesByCategory(categoryId: String): List<ServiceItem> {
        return services.filter { it.categoryId == categoryId }
    }

    fun getServiceById(serviceId: String): ServiceItem? {
        return services.find { it.id == serviceId }
    }

    fun searchServices(query: String): List<ServiceItem> {
        if (query.isBlank()) return services
        val q = query.trim().lowercase()
        return services.filter {
            it.title.lowercase().contains(q) ||
                    it.shortDesc.lowercase().contains(q) ||
                    it.categoryId.lowercase().contains(q) ||
                    it.whatIsIncluded.any { inc -> inc.lowercase().contains(q) }
        }
    }

    // Addresses
    val addresses: Flow<List<Address>> = addressDao.getAllAddresses().map { list ->
        list.map { it.toDomain() }
    }

    suspend fun saveAddress(address: Address): Long {
        if (address.isDefault) {
            addressDao.clearDefaultFlags()
        }
        val entity = AddressEntity.fromDomain(address)
        return addressDao.insertAddress(entity)
    }

    suspend fun setDefaultAddress(id: Long) {
        addressDao.clearDefaultFlags()
        addressDao.setDefaultAddress(id)
    }

    suspend fun deleteAddress(id: Long) {
        addressDao.deleteAddressById(id)
    }

    suspend fun getDefaultAddress(): Address? {
        return addressDao.getDefaultAddress()?.toDomain()
    }

    // Bookings
    val allBookings: Flow<List<Booking>> = bookingDao.getAllBookings().map { list ->
        list.map { it.toDomain() }
    }

    fun getBookingFlow(id: String): Flow<Booking?> {
        return bookingDao.getBookingByIdFlow(id).map { it?.toDomain() }
    }

    val activeBookingFlow: Flow<Booking?> = bookingDao.getActiveBookingFlow().map { it?.toDomain() }

    suspend fun createBooking(booking: Booking) {
        bookingDao.insertBooking(BookingEntity.fromDomain(booking))

        // Create booking confirmed notification
        val notif = NotificationEntity(
            id = "notif_${System.currentTimeMillis()}",
            title = "Booking Confirmed: ${booking.serviceTitle}",
            message = "Slot: ${booking.scheduledDate} (${booking.scheduledTimeSlot}). We are matching your verified pro!",
            timestamp = System.currentTimeMillis(),
            isRead = false,
            type = "BOOKING",
            bookingId = booking.id
        )
        notificationDao.insertNotification(notif)
    }

    suspend fun updateBookingStatus(id: String, newStatus: BookingStatus) {
        bookingDao.updateBookingStatus(id, newStatus.name)

        val notifMsg = when (newStatus) {
            BookingStatus.PROFESSIONAL_ASSIGNED -> "A verified pro has been assigned to your booking!"
            BookingStatus.EN_ROUTE -> "Your service pro is en route to your location in Hyderabad."
            BookingStatus.ARRIVED -> "Your pro has arrived! Share the 4-digit arrival OTP to start service."
            BookingStatus.OTP_VERIFIED -> "OTP verified. Your service is about to begin."
            BookingStatus.SERVICE_STARTED -> "Service is in progress. Relax while our pro takes care of it."
            BookingStatus.SERVICE_COMPLETED -> "Service completed! Please inspect the work and confirm payment."
            BookingStatus.PAID -> "Payment received. Thank you for choosing HomeHelp Hyderabad!"
            BookingStatus.REVIEWED -> "Thank you for your rating & feedback!"
            BookingStatus.CANCELLED -> "Your booking has been cancelled."
            else -> "Booking status updated to ${newStatus.displayName}"
        }

        notificationDao.insertNotification(
            NotificationEntity(
                id = "notif_${System.currentTimeMillis()}",
                title = newStatus.displayName,
                message = notifMsg,
                timestamp = System.currentTimeMillis(),
                isRead = false,
                type = "STATUS_UPDATE",
                bookingId = id
            )
        )
    }

    suspend fun cancelBooking(id: String) {
        updateBookingStatus(id, BookingStatus.CANCELLED)
    }

    // Notifications
    val notifications: Flow<List<AppNotification>> = notificationDao.getAllNotifications().map { list ->
        list.map {
            AppNotification(
                id = it.id,
                title = it.title,
                message = it.message,
                timestamp = it.timestamp,
                isRead = it.isRead,
                type = it.type,
                bookingId = it.bookingId
            )
        }
    }

    val unreadNotificationCount: Flow<Int> = notificationDao.getUnreadCount()

    suspend fun markNotificationAsRead(id: String) {
        notificationDao.markAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() {
        notificationDao.markAllAsRead()
    }

    // Support
    val supportTickets: Flow<List<SupportTicket>> = supportDao.getAllTickets().map { list ->
        list.map {
            SupportTicket(
                id = it.id,
                bookingId = it.bookingId,
                category = it.category,
                subject = it.subject,
                message = it.message,
                status = it.status,
                priority = it.priority,
                createdAt = it.createdAt
            )
        }
    }

    suspend fun createSupportTicket(ticket: SupportTicket) {
        supportDao.insertTicket(
            SupportTicketEntity(
                id = ticket.id,
                bookingId = ticket.bookingId,
                category = ticket.category,
                subject = ticket.subject,
                message = ticket.message,
                status = ticket.status,
                priority = ticket.priority,
                createdAt = ticket.createdAt
            )
        )
    }

    // Reviews
    val reviews: Flow<List<CustomerReview>> = reviewDao.getAllReviews().map { list ->
        list.map {
            CustomerReview(
                id = it.id,
                bookingId = it.bookingId,
                serviceTitle = it.serviceTitle,
                proName = it.proName,
                rating = it.rating,
                comment = it.comment,
                createdAt = it.createdAt
            )
        }
    }

    suspend fun addReview(review: CustomerReview) {
        reviewDao.insertReview(
            ReviewEntity(
                id = review.id,
                bookingId = review.bookingId,
                serviceTitle = review.serviceTitle,
                proName = review.proName,
                rating = review.rating,
                comment = review.comment,
                createdAt = review.createdAt
            )
        )
        updateBookingStatus(review.bookingId, BookingStatus.REVIEWED)
    }

    suspend fun seedInitialDataIfEmpty() {
        if (userDao.getUserCount() == 0) {
            userDao.insertUser(
                UserEntity(
                    id = "CUST-9876543210",
                    name = "Rahul Sharma",
                    phone = "9876543210",
                    email = "rahul.sharma@example.com",
                    passwordHash = "1234",
                    city = "Hyderabad",
                    primaryArea = "Ayyappa Society, Madhapur, Hyderabad 500081",
                    registeredAt = System.currentTimeMillis() - 86400000L * 30,
                    isLoggedIn = true
                )
            )
        }

        if (addressDao.getCount() == 0) {
            // Seed a realistic Hyderabad default address
            addressDao.insertAddress(
                AddressEntity(
                    id = 1,
                    label = "Home",
                    houseNumber = "Flat 402, Aditya Elite Apartments",
                    streetArea = "Ayyappa Society, Madhapur",
                    landmark = "Near Meridian School",
                    city = "Hyderabad",
                    pincode = "500081",
                    contactName = "Rahul Sharma",
                    contactPhone = "+91 98765 43210",
                    isDefault = true
                )
            )
            addressDao.insertAddress(
                AddressEntity(
                    id = 2,
                    label = "Work",
                    houseNumber = "Plot 18, Cyber Towers, 4th Floor",
                    streetArea = "Hitec City Main Road",
                    landmark = "Opposite Shilparamam",
                    city = "Hyderabad",
                    pincode = "500081",
                    contactName = "Rahul Sharma",
                    contactPhone = "+91 98765 43210",
                    isDefault = false
                )
            )

            // Seed a sample active booking so the user can immediately experience the tracking state machine
            val sampleBooking = Booking(
                id = "HH-HYD-7821",
                serviceId = "ac_jet_wash",
                serviceTitle = "PowerJet AC Servicing",
                categoryId = "ac_service",
                variantName = "1x Split AC Jet Service",
                variantPrice = 499,
                addOnsSummary = "Active Enzyme Foam Shield",
                addOnsTotal = 199,
                baseAmount = 698,
                platformFee = 49,
                taxes = 134,
                discountAmount = 100, // WELCOME100 applied
                totalAmount = 781,
                scheduledDate = "Today",
                scheduledTimeSlot = "02:00 PM - 04:00 PM",
                address = Address(
                    id = 1,
                    label = "Home",
                    houseNumber = "Flat 402, Aditya Elite",
                    streetArea = "Ayyappa Society, Madhapur",
                    landmark = "Near Meridian School",
                    city = "Hyderabad",
                    pincode = "500081",
                    contactName = "Rahul Sharma",
                    contactPhone = "+91 98765 43210",
                    isDefault = true
                ),
                status = BookingStatus.PROFESSIONAL_ASSIGNED,
                arrivalOtp = "4892",
                assignedPro = InitialHyderabadServices.TOP_PROS[0],
                paymentMethod = "UPI (Google Pay)",
                paymentStatus = "Pending on Service Completion",
                createdAt = System.currentTimeMillis() - 3600000,
                notes = "Please bring extra hose pipe for 4th floor balcony."
            )
            bookingDao.insertBooking(BookingEntity.fromDomain(sampleBooking))

            // Seed notification
            notificationDao.insertNotification(
                NotificationEntity(
                    id = "notif_welcome",
                    title = "Welcome to HomeHelp Hyderabad!",
                    message = "Enjoy verified, background-checked home service professionals. Use code WELCOME100 for ₹100 off!",
                    timestamp = System.currentTimeMillis() - 7200000,
                    isRead = false,
                    type = "PROMO",
                    bookingId = null
                )
            )
            notificationDao.insertNotification(
                NotificationEntity(
                    id = "notif_pro_assigned",
                    title = "Pro Assigned: Venkatesh Rao",
                    message = "Venkatesh Rao (4.9★, 620+ jobs) has been assigned to your AC PowerJet service.",
                    timestamp = System.currentTimeMillis() - 1800000,
                    isRead = false,
                    type = "STATUS_UPDATE",
                    bookingId = "HH-HYD-7821"
                )
            )
        }
    }
}
