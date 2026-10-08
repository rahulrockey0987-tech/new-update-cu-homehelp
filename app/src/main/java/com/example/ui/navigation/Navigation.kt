package com.example.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Bookings : Screen("bookings")
    data object Account : Screen("account")

    data object CategoryDetail : Screen("category/{categoryId}") {
        fun createRoute(categoryId: String) = "category/$categoryId"
    }

    data object ServiceDetail : Screen("service/{serviceId}") {
        fun createRoute(serviceId: String) = "service/$serviceId"
    }

    data object BookingFlow : Screen("booking_flow")
    data object HirePartner : Screen("hire_partner")

    data object BookingTracking : Screen("tracking/{bookingId}") {
        fun createRoute(bookingId: String) = "tracking/$bookingId"
    }

    data object Addresses : Screen("addresses")
    data object Notifications : Screen("notifications")
    data object Support : Screen("support")
    data object Reviews : Screen("reviews")
    data object Auth : Screen("auth")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object VerifyOtp : Screen("verify_otp")
    data object ForgotPassword : Screen("forgot_password")
}
