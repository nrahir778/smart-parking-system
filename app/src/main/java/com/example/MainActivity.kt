package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.ParkingViewModel
import com.example.ui.screens.BluetoothScreen
import com.example.ui.screens.GatewayLiveLinkScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class NavTab(val title: String) {
    DECK("Parking Deck"),
    BLUETOOTH("HC-05 BT"),
    GATEWAY("Live Cloud Link")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContainer()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer() {
    val viewModel: ParkingViewModel = viewModel()
    var selectedTab by remember { mutableStateOf(NavTab.DECK) }

    // Request Bluetooth permissions on Android 12+ or location on older devices
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        viewModel.refreshPairedDevices()
    }

    LaunchedEffect(Unit) {
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
        } else {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        permissionLauncher.launch(permissions.toTypedArray())
    }

    // Handle back button when on sub-screens
    BackHandler(enabled = selectedTab != NavTab.DECK) {
        selectedTab = NavTab.DECK
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (selectedTab) {
                            NavTab.DECK -> "Smart Parking 3-Slot"
                            NavTab.BLUETOOTH -> "HC-05 Bluetooth Telemetry"
                            NavTab.GATEWAY -> "Gateway & Public Live Link"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_navigation_bar")
            ) {
                // Tab 1: Parking Deck
                NavigationBarItem(
                    selected = selectedTab == NavTab.DECK,
                    onClick = { selectedTab = NavTab.DECK },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == NavTab.DECK) Icons.Filled.DirectionsCar else Icons.Outlined.DirectionsCar,
                            contentDescription = "Deck"
                        )
                    },
                    label = { Text("Deck", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkBg,
                        selectedTextColor = ElectricCyan,
                        indicatorColor = ElectricCyan,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_deck")
                )

                // Tab 2: HC-05 Bluetooth
                NavigationBarItem(
                    selected = selectedTab == NavTab.BLUETOOTH,
                    onClick = {
                        selectedTab = NavTab.BLUETOOTH
                        viewModel.refreshPairedDevices()
                    },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == NavTab.BLUETOOTH) Icons.Filled.Bluetooth else Icons.Outlined.Bluetooth,
                            contentDescription = "Bluetooth"
                        )
                    },
                    label = { Text("HC-05 BT", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkBg,
                        selectedTextColor = ElectricCyan,
                        indicatorColor = ElectricCyan,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_bluetooth")
                )

                // Tab 3: Gateway & Live Link
                NavigationBarItem(
                    selected = selectedTab == NavTab.GATEWAY,
                    onClick = { selectedTab = NavTab.GATEWAY },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == NavTab.GATEWAY) Icons.Filled.CloudSync else Icons.Outlined.CloudSync,
                            contentDescription = "Gateway"
                        )
                    },
                    label = { Text("Live Link", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkBg,
                        selectedTextColor = ElectricCyan,
                        indicatorColor = ElectricCyan,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_gateway")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBg)
        ) {
            when (selectedTab) {
                NavTab.DECK -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateToBluetooth = { selectedTab = NavTab.BLUETOOTH },
                    onNavigateToGateway = { selectedTab = NavTab.GATEWAY }
                )
                NavTab.BLUETOOTH -> BluetoothScreen(
                    viewModel = viewModel
                )
                NavTab.GATEWAY -> GatewayLiveLinkScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}
