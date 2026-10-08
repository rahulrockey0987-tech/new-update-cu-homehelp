package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.mock.InitialHyderabadServices
import com.example.data.model.BookingStatus
import com.example.data.model.Coupon
import com.example.data.model.ServiceVariant
import com.example.ui.BookingDraft
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("HomeHelp", appName)
    }

    @Test
    fun `verify hyderabad catalog integrity`() {
        val categories = InitialHyderabadServices.CATEGORIES
        assertEquals(8, categories.size)
        assertTrue(categories.any { it.id == "cleaning" })
        assertTrue(categories.any { it.id == "ac_service" })
        assertTrue(categories.any { it.id == "electrical" })
        assertTrue(categories.any { it.id == "plumbing" })

        val services = InitialHyderabadServices.SERVICES
        assertTrue(services.isNotEmpty())
        val fullClean = services.find { it.id == "clean_full_home" }
        assertNotNull(fullClean)
        assertTrue(fullClean!!.variants.isNotEmpty())
        assertTrue(fullClean.whatIsIncluded.isNotEmpty())
    }

    @Test
    fun `verify transparent server side pricing calculation`() {
        val draft = BookingDraft(
            selectedVariant = ServiceVariant("v1", "2 BHK", "desc", 2499, 180),
            selectedAddOns = emptySet(),
            appliedCoupon = Coupon("WELCOME100", 100, 499, "First user discount")
        )
        // baseAmount: 2499
        // platformFee: 49
        // taxes (18% of 2548): 458
        // discount: 100
        // finalTotal: 2499 + 49 + 458 - 100 = 2906
        assertEquals(2499, draft.baseAmount)
        assertEquals(49, draft.platformFee)
        assertEquals(458, draft.taxes)
        assertEquals(100, draft.discountAmount)
        assertEquals(2906, draft.finalTotal)
    }

    @Test
    fun `verify booking state machine sequential progression`() {
        val steps = listOf(
            BookingStatus.CONFIRMED,
            BookingStatus.PROFESSIONAL_ASSIGNED,
            BookingStatus.EN_ROUTE,
            BookingStatus.ARRIVED,
            BookingStatus.OTP_VERIFIED,
            BookingStatus.SERVICE_STARTED,
            BookingStatus.SERVICE_COMPLETED,
            BookingStatus.PAID,
            BookingStatus.REVIEWED
        )
        for (i in 0 until steps.size - 1) {
            assertTrue(steps[i].stepIndex < steps[i + 1].stepIndex)
        }
    }

    @Test
    fun `verify hire a partner hourly rate dynamic calculation`() {
        val draft2h = com.example.data.model.PartnerHireDraft(requiredHours = 2, hourlyRate = 150)
        assertEquals(300, draft2h.totalAmount)

        val draft3h = com.example.data.model.PartnerHireDraft(requiredHours = 3, hourlyRate = 150)
        assertEquals(450, draft3h.totalAmount)

        val draft4h = com.example.data.model.PartnerHireDraft(requiredHours = 4, hourlyRate = 150)
        assertEquals(600, draft4h.totalAmount)

        val draft5h = com.example.data.model.PartnerHireDraft(requiredHours = 5, hourlyRate = 150)
        assertEquals(750, draft5h.totalAmount)

        // Configurable rate test
        val customRateDraft = com.example.data.model.PartnerHireDraft(requiredHours = 3, hourlyRate = 200)
        assertEquals(600, customRateDraft.totalAmount)
    }

    @Test
    fun `verify partner request status progression`() {
        var draft = com.example.data.model.PartnerHireDraft()
        assertEquals(com.example.data.model.PartnerRequestStatus.DRAFT, draft.partnerRequestStatus)

        draft = draft.copy(
            voiceAudioRecorded = true,
            voiceDurationSeconds = 12,
            workDescription = "Customer Voice Recording (12s)",
            partnerRequestStatus = com.example.data.model.PartnerRequestStatus.FINDING_PARTNER
        )
        assertTrue(draft.voiceAudioRecorded)
        assertEquals(12, draft.voiceDurationSeconds)
        assertEquals(com.example.data.model.PartnerRequestStatus.FINDING_PARTNER, draft.partnerRequestStatus)

        draft = draft.copy(
            partnerRequestStatus = com.example.data.model.PartnerRequestStatus.PARTNER_ACCEPTED
        )
        assertEquals(com.example.data.model.PartnerRequestStatus.PARTNER_ACCEPTED, draft.partnerRequestStatus)
    }

    @Test
    fun `verify google map spots selection for partner hire`() {
        val spots = com.example.ui.screens.partner.HYDERABAD_MAP_SPOTS
        assertTrue(spots.isNotEmpty())
        assertTrue(spots.any { it.locality.contains("Madhapur") })
        assertTrue(spots.any { it.locality.contains("Jubilee Hills") })
        val firstSpot = spots.first()
        assertTrue(firstSpot.coordinates.isNotEmpty())
    }

    @Test
    fun `verify deterministic customer ID generation without random numbers`() {
        val phone1 = "9876543210"
        val id1 = com.example.data.model.generateCustomerId(phone1)
        assertEquals("CUST-9876543210", id1)

        // Multiple calls with same phone must yield identical deterministic ID (no random UUID/number)
        val id1Repeat = com.example.data.model.generateCustomerId("+91 98765 43210")
        assertEquals(id1, id1Repeat)

        val phone2 = "8765432109"
        val id2 = com.example.data.model.generateCustomerId(phone2)
        assertEquals("CUST-8765432109", id2)
        assertTrue(id1 != id2)
    }

    @Test
    fun `verify customer partner tracking status and arrival otp`() {
        val testBooking = com.example.data.model.Booking(
            id = "HH-HYD-3210-001",
            serviceId = "hire_a_partner",
            serviceTitle = "Hire a Partner",
            categoryId = "partner",
            variantName = "3 Hours",
            variantPrice = 450,
            addOnsSummary = "Voice Request Recorded (12s)",
            addOnsTotal = 0,
            baseAmount = 450,
            platformFee = 0,
            taxes = 0,
            discountAmount = 0,
            totalAmount = 450,
            scheduledDate = "Today",
            scheduledTimeSlot = "Immediate",
            address = com.example.data.model.Address(
                id = 1,
                label = "Home",
                houseNumber = "Flat 101",
                streetArea = "Madhapur",
                city = "Hyderabad",
                pincode = "500081",
                contactName = "Ramesh Reddy",
                contactPhone = "+91 9876543210"
            ),
            status = BookingStatus.EN_ROUTE,
            arrivalOtp = "4892",
            assignedPro = com.example.data.mock.InitialHyderabadServices.TOP_PROS.first(),
            paymentMethod = "UPI",
            paymentStatus = "Pending",
            createdAt = System.currentTimeMillis()
        )

        assertEquals("HH-HYD-3210-001", testBooking.id)
        assertEquals("4892", testBooking.arrivalOtp)
        assertEquals(BookingStatus.EN_ROUTE, testBooking.status)
        assertNotNull(testBooking.assignedPro)
        assertEquals("Ramesh Reddy", testBooking.address.contactName)
    }

    @Test
    fun `verify password hashing with salt and security verification`() {
        val password = "secretPassword123"
        val salt = com.example.util.SecurityUtils.generateSalt()
        assertTrue(salt.isNotEmpty())

        val hash1 = com.example.util.SecurityUtils.hashPassword(password, salt)
        val hash2 = com.example.util.SecurityUtils.hashPassword(password, salt)
        assertEquals(hash1, hash2)

        // Passwords must never equal raw plaintext
        assertTrue(hash1 != password)

        // Verification must succeed
        assertTrue(com.example.util.SecurityUtils.verifyPassword(password, salt, hash1))
        // Incorrect password must fail
        assertTrue(!com.example.util.SecurityUtils.verifyPassword("wrongPassword", salt, hash1))
    }

    @Test
    fun `verify JWT authentication token generation and validation`() {
        val customerId = "CUST-9876543210"
        val phone = "9876543210"
        val token = com.example.util.SecurityUtils.generateJwtToken(customerId, phone, "customer")

        assertTrue(token.isNotEmpty())
        val parts = token.split(".")
        assertEquals(3, parts.size) // header, payload, signature
        assertTrue(com.example.util.SecurityUtils.isTokenValid(token))
    }
}
