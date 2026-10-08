package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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

@Composable
fun HomeHelpApp(
    viewModel: HomeHelpViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val snackbarHostState = remember { SnackbarHostState() }
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

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
                        onNavigateToAuth = { navController.navigate(Screen.Auth.route) }
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
