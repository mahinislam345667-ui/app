package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.DisclaimerDialog
import com.example.ui.screens.CameraScanScreen
import com.example.ui.screens.GamificationScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProcessingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ResultScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MainViewModel

sealed class Screen(val route: String, val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object CameraScan : Screen("camera_scan", "Scan", Icons.Filled.Home, Icons.Outlined.Home)
    object Processing : Screen("processing", "Analyzing", Icons.Filled.Home, Icons.Outlined.Home)
    object Result : Screen("result", "Report", Icons.Filled.Home, Icons.Outlined.Home)
    object History : Screen("history", "History", Icons.Filled.History, Icons.Outlined.History)
    object Gamification : Screen("gamification", "Rewards", Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun AppNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showDisclaimer by viewModel.showDisclaimer.collectAsState()

    if (showDisclaimer) {
        DisclaimerDialog(onDismiss = { viewModel.setShowDisclaimer(false) })
    }

    val bottomBarScreens = listOf(
        Screen.Home,
        Screen.History,
        Screen.Gamification,
        Screen.Profile
    )

    val showBottomBar = currentRoute in bottomBarScreens.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = DarkSurface,
                    contentColor = TextPrimary,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    bottomBarScreens.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.title,
                                    tint = if (isSelected) NeonCyan else TextSecondary
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    color = if (isSelected) TextPrimary else TextSecondary
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = NeonPurple.copy(alpha = 0.25f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToCamera = { navController.navigate(Screen.CameraScan.route) },
                        onNavigateToProcessing = { navController.navigate(Screen.Processing.route) },
                        onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
                    )
                }

                composable(Screen.CameraScan.route) {
                    CameraScanScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToProcessing = { navController.navigate(Screen.Processing.route) }
                    )
                }

                composable(Screen.Processing.route) {
                    ProcessingScreen(
                        viewModel = viewModel,
                        onNavigateToResult = {
                            navController.navigate(Screen.Result.route) {
                                popUpTo(Screen.Home.route) { inclusive = false }
                            }
                        }
                    )
                }

                composable(Screen.Result.route) {
                    ResultScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.navigate(Screen.Home.route) }
                    )
                }

                composable(Screen.History.route) {
                    HistoryScreen(
                        viewModel = viewModel,
                        onSelectScan = { navController.navigate(Screen.Result.route) }
                    )
                }

                composable(Screen.Gamification.route) {
                    GamificationScreen(viewModel = viewModel)
                }

                composable(Screen.Profile.route) {
                    ProfileScreen(viewModel = viewModel)
                }
            }
        }
    }
}
