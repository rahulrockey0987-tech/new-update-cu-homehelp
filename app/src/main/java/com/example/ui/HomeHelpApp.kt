package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.components.HomeHelpBottomNav
import com.example.ui.navigation.Screen
import com.example.ui.screens.account.AccountScreen
import com.example.ui.screens.account.AddressesScreen
import com.example.ui.screens.account.AuthScreen
import com.example.ui.screens.account.NotificationsScreen
import com.example.ui.screens.account.ReviewsScreen
import com.example.ui.screens.account.SupportScreen
import com.example.ui.screens.booking.BookingFlowScreen
import com.example.ui.screens.bookings.BookingsListScreen
import com.example.ui.screens.category.CategoryDetailScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.partner.HirePartnerScreen
import com.example.ui.screens.search.SearchScreen
import com.example.ui.screens.service.ServiceDetailScreen
import com.example.ui.screens.tracking.BookingTrackingScreen
import com.example.ui.theme.BrandTealDark
import com.example.ui.theme.BrandTealPrimary
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate900

@Composable
fun HomeHelpApp(
    viewModel: HomeHelpViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val snackbarHostState = remember { SnackbarHostState() }
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val activeUser by viewModel.activeUser.collectAsStateWithLifecycle()
    val isSessionChecked by viewModel.isSessionChecked.collectAsStateWithLifecycle()

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    // 1. Session checking splash
    if (!isSessionChecked) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(BrandTealPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.HomeRepairService,
                        contentDescription = "HomeHelp Logo",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "HomeHelp",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Slate900
                )
                Text(
                    text = "Hyderabad's On-Demand Home Services",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate500
                )
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressIndicator(
                    color = BrandTealPrimary,
                    modifier = Modifier.size(32.dp),
                    strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Checking customer session...",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate500
                )
            }
        }
        return
    }

    // 2. MANDATORY LOGIN GUARD: If customer is not authenticated, show AuthScreen ONLY.
    // Customers cannot access Home, Explore, Hire Partner, Bookings, Profile or any feature until authenticated.
    if (activeUser == null) {
        AuthScreen(
            viewModel = viewModel,
            onAuthSuccess = {
                // activeUser flow will emit the logged-in user, automatically transitioning to Customer Home
            },
            onBack = null
        )
        return
    }

    // 3. Authenticated customer experience with Protected Routes
    val isTopLevelScreen = currentRoute in listOf(
        Screen.Home.route,
        Screen.Search.route,
        Screen.Bookings.route,
        Screen.Account.route
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (isTopLevelScreen) {
                HomeHelpBottomNav(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        if (currentRoute != route) {
                            navController.navigate(route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        viewModel = viewModel,
                        onCategoryClick = { categoryId ->
                            navController.navigate(Screen.CategoryDetail.createRoute(categoryId))
                        },
                        onServiceClick = { serviceId ->
                            navController.navigate(Screen.ServiceDetail.createRoute(serviceId))
                        },
                        onSearchClick = {
                            navController.navigate(Screen.Search.route)
                        },
                        onHirePartnerClick = {
                            viewModel.initPartnerHireFlow()
                            navController.navigate(Screen.HirePartner.route)
                        },
                        onTrackBookingClick = { bookingId ->
                            navController.navigate(Screen.BookingTracking.createRoute(bookingId))
                        },
                        onNotificationsClick = {
                            navController.navigate(Screen.Notifications.route)
                        }
                    )
                }

                composable(Screen.Search.route) {
                    SearchScreen(
                        viewModel = viewModel,
                        onServiceClick = { serviceId ->
                            navController.navigate(Screen.ServiceDetail.createRoute(serviceId))
                        }
                    )
                }

                composable(Screen.Bookings.route) {
                    BookingsListScreen(
                        viewModel = viewModel,
                        onTrackBooking = { bookingId ->
                            navController.navigate(Screen.BookingTracking.createRoute(bookingId))
                        },
                        onBookService = { serviceId ->
                            navController.navigate(Screen.ServiceDetail.createRoute(serviceId))
                        }
                    )
                }

                composable(Screen.Account.route) {
                    AccountScreen(
                        viewModel = viewModel,
                        onNavigateToAddresses = { navController.navigate(Screen.Addresses.route) },
                        onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                        onNavigateToSupport = { navController.navigate(Screen.Support.route) },
                        onNavigateToReviews = { navController.navigate(Screen.Reviews.route) },
                        onNavigateToAuth = {
                            navController.navigate(Screen.Auth.route)
                        }
                    )
                }

                composable(
                    route = Screen.CategoryDetail.route,
                    arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
                ) { backStack ->
                    val categoryId = backStack.arguments?.getString("categoryId") ?: ""
                    CategoryDetailScreen(
                        categoryId = categoryId,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onServiceClick = { serviceId ->
                            navController.navigate(Screen.ServiceDetail.createRoute(serviceId))
                        }
                    )
                }

                composable(
                    route = Screen.ServiceDetail.route,
                    arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
                ) { backStack ->
                    val serviceId = backStack.arguments?.getString("serviceId") ?: ""
                    ServiceDetailScreen(
                        serviceId = serviceId,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onProceedToBook = {
                            navController.navigate(Screen.BookingFlow.route)
                        }
                    )
                }

                composable(Screen.BookingFlow.route) {
                    BookingFlowScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onBookingConfirmed = { newBookingId ->
                            navController.navigate(Screen.BookingTracking.createRoute(newBookingId)) {
                                popUpTo(Screen.Home.route)
                            }
                        }
                    )
                }

                composable(Screen.HirePartner.route) {
                    HirePartnerScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onPartnerMatched = { newBookingId ->
                            navController.navigate(Screen.BookingTracking.createRoute(newBookingId)) {
                                popUpTo(Screen.Home.route)
                            }
                        }
                    )
                }

                composable(
                    route = Screen.BookingTracking.route,
                    arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
                ) { backStack ->
                    val bookingId = backStack.arguments?.getString("bookingId") ?: ""
                    BookingTrackingScreen(
                        bookingId = bookingId,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onSupportClick = { navController.navigate(Screen.Support.route) }
                    )
                }

                composable(Screen.Addresses.route) {
                    AddressesScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Notifications.route) {
                    NotificationsScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onOpenBooking = { bookingId ->
                            navController.navigate(Screen.BookingTracking.createRoute(bookingId))
                        }
                    )
                }

                composable(Screen.Support.route) {
                    SupportScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Reviews.route) {
                    ReviewsScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Auth.route) {
                    AuthScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onAuthSuccess = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
