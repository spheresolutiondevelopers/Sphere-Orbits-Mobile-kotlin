/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.app

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.orbits.app.navigation.AppNavGraph
import com.orbits.app.navigation.Routes
import com.orbits.core.theme.SphereTheme
import com.orbits.core.theme.ThemeMode
import com.orbits.feature.settings.SettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun SphereApp(
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val settingsState by settingsViewModel.state.collectAsState()
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    SphereTheme(
        themeMode = when (settingsState.themeMode) {
            "light" -> ThemeMode.LIGHT
            "dark" -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        },
        palette = settingsState.themePalette
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                SphereDrawerContent(
                    onItemClick = { route ->
                        scope.launch { drawerState.close() }
                        navController.navigate(route)
                    },
                    onSignOut = {
                        scope.launch { drawerState.close() }
                        // Handle sign out
                    }
                )
            }
        ) {
            Scaffold(
                topBar = {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    val isAuthRoute = currentRoute in listOf(Routes.LOGIN, Routes.SIGNUP)
                    val isTopLevel = currentRoute in listOf(
                        Routes.DASHBOARD,
                        Routes.TASKS,
                        Routes.MEETINGS,
                        Routes.ANALYTICS
                    )

                    if (!isAuthRoute) {
                        SphereTopAppBar(
                            onMenuClick = { scope.launch { drawerState.open() } },
                            onBackClick = { navController.popBackStack() },
                            onThemeToggle = {
                                val newMode = if (settingsState.themeMode == "dark") "light" else "dark"
                                settingsViewModel.handleEvent(com.orbits.feature.settings.SettingsEvent.UpdateTheme(newMode))
                            },
                            isDarkMode = settingsState.themeMode == "dark",
                            showBackButton = !isTopLevel,
                            title = when (currentRoute) {
                                Routes.DASHBOARD -> "Sphere."
                                Routes.TASKS -> "Tasks"
                                Routes.MEETINGS -> "Meetings"
                                Routes.ANALYTICS -> "Reports"
                                Routes.SETTINGS -> "Settings"
                                Routes.CALENDAR -> "Calendar"
                                Routes.CHAT -> "Chat"
                                Routes.NOTES -> "Notes"
                                Routes.EVENTS -> "Events"
                                else -> "Sphere."
                            }
                        )
                    }
                },
                bottomBar = {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    val isAuthRoute = currentRoute in listOf(Routes.LOGIN, Routes.SIGNUP)
                    
                    if (!isAuthRoute) {
                        SphereBottomBar(navController = navController)
                    }
                },
                containerColor = MaterialTheme.colorScheme.background
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    AppNavGraph(navController = navController)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SphereTopAppBar(
    onMenuClick: () -> Unit,
    onBackClick: () -> Unit,
    onThemeToggle: () -> Unit,
    isDarkMode: Boolean,
    showBackButton: Boolean,
    title: String
) {
    TopAppBar(
        title = {
            if (title == "Sphere.") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Gradient Logo
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary,
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                )
                            )
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White, CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    // Brand Name
                    Text(
                        text = "Sphere.",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
            } else {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = {
            // Theme Toggle
            ThemeToggleSwitch(
                isDarkMode = isDarkMode,
                onToggle = onThemeToggle
            )
            IconButton(onClick = { /* Notifications */ }) {
                Box {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .align(Alignment.TopEnd)
                            .background(MaterialTheme.colorScheme.tertiary, CircleShape)
                            .border(1.5.dp, MaterialTheme.colorScheme.background, CircleShape)
                    )
                }
            }
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
fun ThemeToggleSwitch(
    isDarkMode: Boolean,
    onToggle: () -> Unit
) {
    val thumbOffset by animateDpAsState(targetValue = if (isDarkMode) 2.dp else 26.dp, label = "thumbOffset")
    
    Box(
        modifier = Modifier
            .width(56.dp)
            .height(30.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
            .clickable { onToggle() }
            .padding(3.dp)
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(22.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
fun SphereBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        BottomNavItem("Home", Routes.DASHBOARD, Icons.Default.Home),
        BottomNavItem("Tasks", Routes.TASKS, Icons.AutoMirrored.Filled.List),
        BottomNavItem("Meetings", Routes.MEETINGS, Icons.Default.VideoCall),
        BottomNavItem("Reports", Routes.ANALYTICS, Icons.Default.BarChart)
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(74.dp)
            .shadow(elevation = 8.dp),
        color = SphereTheme.colors.navBackground,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.take(2).forEach { item ->
                SphereBottomNavItem(
                    item = item,
                    selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            // Central FAB
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
                    .clickable { /* Quick Add */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(28.dp))
            }

            items.takeLast(2).forEach { item ->
                SphereBottomNavItem(
                    item = item,
                    selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SphereBottomNavItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val alpha by animateFloatAsState(targetValue = if (selected) 1f else 0f, label = "pipAlpha")
    
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = if (selected) MaterialTheme.colorScheme.primary else SphereTheme.colors.textTertiary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) MaterialTheme.colorScheme.primary else SphereTheme.colors.textTertiary
        )
    }
}

data class BottomNavItem(val label: String, val route: String, val icon: ImageVector)

@Composable
fun SphereDrawerContent(
    onItemClick: (String) -> Unit,
    onSignOut: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.background
    ) {
        // Drawer Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("F", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("Faris Shikuku", style = MaterialTheme.typography.titleMedium)
                Text("faris@sphereapp.io", style = MaterialTheme.typography.bodySmall, color = SphereTheme.colors.textSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(11.dp)
                ) {
                    Text(
                        "Pro Plan",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        // Navigation Items
        DrawerItem("Appointments", Icons.Default.CalendarToday, Routes.APPOINTMENTS, onItemClick)
        DrawerItem("Notes", Icons.AutoMirrored.Filled.Note, Routes.NOTES, onItemClick)
        DrawerItem("Events", Icons.Default.Event, Routes.EVENTS, onItemClick)
        DrawerItem("Settings", Icons.Default.Settings, Routes.SETTINGS, onItemClick)
        
        Spacer(modifier = Modifier.weight(1f))
        
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        
        NavigationDrawerItem(
            label = { Text("Sign Out", color = MaterialTheme.colorScheme.error) },
            selected = false,
            onClick = onSignOut,
            icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun DrawerItem(
    label: String,
    icon: ImageVector,
    route: String,
    onClick: (String) -> Unit
) {
    NavigationDrawerItem(
        label = { Text(label) },
        selected = false,
        onClick = { onClick(route) },
        icon = { Icon(icon, contentDescription = null) },
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
    )
}
