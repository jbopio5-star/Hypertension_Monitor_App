@file:OptIn(ExperimentalMaterial3Api::class)
package com.opio.hypertensionmonitorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.opio.hypertensionmonitorapp.data.BpReading
import com.opio.hypertensionmonitorapp.ui.theme.HypertensionMonitorAppTheme
import com.opio.hypertensionmonitorapp.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


@AndroidEntryPoint  // ← ADD THIS LINE RIGHT HERE
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HypertensionMonitorAppTheme {
                SetupNavGraph()
            }
        }
    }
}

@Composable
fun SetupNavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        composable("welcome") { WelcomeScreen(navController) }
        composable("choice") { ChoiceScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("dashboard") { DashboardScreen(navController) }
        composable("history") { HistoryScreen(navController) }
        composable("manual") { ManualEntryScreen(navController) }
        composable("settings") { SettingsScreen(navController) }
        composable("sos") { SosScreen(navController) }
        composable("about") { AboutScreen(navController) }
        composable("pairWatch") { PairWatchScreen(navController) }
    }
}
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: AppViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val latestReading by viewModel.latestReading.collectAsState(initial = null)

    // Use sample data for preview/demo - this will be replaced with real data from database
    val systolic = latestReading?.systolic ?: 138
    val diastolic = latestReading?.diastolic ?: 88
    val heartRate = latestReading?.heartRate ?: 72
    val time = latestReading?.timestamp?.let {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(it)
    } ?: "02:45 PM"
    val notes = latestReading?.notes ?: "Feeling excellent"

    val status = when {
        systolic >= 180 || diastolic >= 120 -> "HYPERTENSIVE CRISIS - EMERGENCY!"
        systolic >= 140 || diastolic >= 90 -> "HIGH – See Doctor Soon"
        systolic >= 120 || diastolic >= 80 -> "ELEVATED"
        else -> "NORMAL"
    }

    val statusColor = when {
        systolic >= 180 || diastolic >= 120 -> Color.Red
        systolic >= 140 || diastolic >= 90 -> Color(0xFFFF9800) // Orange
        systolic >= 120 || diastolic >= 80 -> Color(0xFFFFC107) // Yellow
        else -> Color(0xFF4CAF50) // Green
    }

    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Hypertension Monitor", color = Color.White) },
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Text("⋮⋮", fontSize = 24.sp, color = Color.White)
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = {
                                navController.navigate("settings")
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("About") },
                            onClick = {
                                navController.navigate("about")
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                viewModel.logout()
                                navController.navigate("welcome") {
                                    popUpTo("welcome") { inclusive = true }
                                }
                                showMenu = false
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF006600)
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF006600)) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Text("🏠", fontSize = 24.sp) },
                    label = { Text("Home", color = Color.White) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("history") },
                    icon = { Text("📊", fontSize = 24.sp) },
                    label = { Text("History", color = Color.White) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("manual") },
                    icon = { Text("✍️", fontSize = 24.sp) },
                    label = { Text("Manual", color = Color.White) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Welcome back, ${currentUser?.fullName ?: "Patient"}!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF006600)
            )
            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(Color(0xFFFFCDD2)),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Latest Reading", fontSize = 16.sp, color = Color.Gray)
                    Text(
                        "$systolic/$diastolic mmHg",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                    Text("Heart Rate: $heartRate bpm", fontSize = 18.sp)
                    Text(time, fontSize = 16.sp, color = Color.Gray)
                    if (notes.isNotEmpty()) {
                        Text(
                            "\"$notes\"",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    Text(
                        status,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            Card(
                onClick = { navController.navigate("history") },
                modifier = Modifier.fillMaxWidth().height(130.dp),
                colors = CardDefaults.cardColors(Color(0xFFE8F5E8))
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📊", fontSize = 40.sp, color = Color(0xFF006600))
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Last 7 Days Trend",
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF006600)
                        )
                        Text("(Tap to view full history)", fontSize = 14.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(Modifier.height(30.dp))
            Button(
                onClick = { navController.navigate("sos") },
                modifier = Modifier.fillMaxWidth().height(80.dp),
                colors = ButtonDefaults.buttonColors(Color.Red),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    "🆘 SOS – CALL 112",
                    fontSize = 24.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.weight(1f))
            Text(
                "Designed by Opio John Bosco",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun AboutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("About", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("←", fontSize = 28.sp, color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF006600)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.watch),
                        contentDescription = "Smartwatch",
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Soroti RRH Hypertension Monitor",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF006600),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Text(
                "This app is designed to help hypertension patients at Soroti Regional Referral Hospital monitor their blood pressure in real-time using smartwatch technology.",
                fontSize = 16.sp
            )
            Text("Key Features:", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF006600))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FeatureItem("Real-time BP monitoring via smartwatch")
                FeatureItem("Emergency SOS with location sharing")
                FeatureItem("Automatic alerts to doctors & supporters")
                FeatureItem("Works offline with auto-sync")
                FeatureItem("Dual language support (English/Ateso)")
            }
            Spacer(Modifier.weight(1f))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Version 1.0", color = Color.Gray)
                Text("Designed by Opio John Bosco", color = Color.Gray)
                Text("Uganda Technology and Management University", color = Color.Gray, fontSize = 14.sp)
                Text("August 2025", color = Color.Gray)
            }
        }
    }
}


@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: AppViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    var language by remember { mutableStateOf("English") }
    var isAteso by remember { mutableStateOf(false) }
    var isBluetoothEnabled by remember { mutableStateOf(true) }
    var connectedWatch by remember { mutableStateOf("Not connected") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("←", fontSize = 28.sp, color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF006600)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            // FIX: Use safe call with let instead of smart cast
            currentUser?.let { user ->
                SettingsItem(
                    title = "My Profile",
                    subtitle = user.fullName,
                    action = {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "ID: ${user.patientId}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                "Phone: ${user.phone}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                )
                HorizontalDivider()
            }

            SettingsItem(
                title = "Language",
                subtitle = "Choose app language",
                action = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = language,
                            color = Color(0xFF006600),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        Switch(
                            checked = isAteso,
                            onCheckedChange = {
                                isAteso = it
                                language = if (it) "Ateso" else "English"
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF006600),
                                checkedTrackColor = Color(0xFF006600).copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            )
            HorizontalDivider()
            SettingsItem(
                title = "Smartwatch Connection",
                subtitle = if (isBluetoothEnabled) connectedWatch else "Not connected",
                action = {
                    Button(
                        onClick = { navController.navigate("pairWatch") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF006600)
                        ),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text(
                            if (isBluetoothEnabled) "Change" else "Pair",
                            fontSize = 14.sp
                        )
                    }
                }
            )

            Spacer(Modifier.weight(1f))
            Button(
                onClick = {
                    viewModel.logout()
                    navController.navigate("welcome") {
                        popUpTo("welcome") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("LOGOUT", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(10.dp))
            Text(
                "Designed by Opio John Bosco",
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    action: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(text = subtitle, fontSize = 14.sp, color = Color.Gray)
        }
        action()
    }
}

@Composable
fun PairWatchScreen(navController: NavController) {
    var isScanning by remember { mutableStateOf(true) }
    var foundDevices by remember { mutableStateOf(
        listOf("Samsung Galaxy Watch 4", "Xiaomi Mi Band 7", "Fitbit Charge 6")
    ) }
    var selectedDevice by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pair New Watch", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("←", fontSize = 28.sp, color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF006600)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                "Available Smartwatches",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF006600)
            )
            if (isScanning) Text("Scanning for devices...", color = Color.Gray)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                foundDevices.forEach { device ->
                    BluetoothDeviceItem(
                        deviceName = device,
                        isSelected = selectedDevice == device,
                        onSelect = { selectedDevice = device }
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            Button(
                onClick = { if (selectedDevice.isNotEmpty()) navController.popBackStack() },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedDevice.isNotEmpty()) Color(0xFF006600) else Color.Gray
                ),
                enabled = selectedDevice.isNotEmpty()
            ) {
                Text(
                    if (selectedDevice.isNotEmpty()) "Pair with $selectedDevice" else "Select a device",
                    fontSize = 18.sp
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                "Designed by Opio John Bosco",
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun BluetoothDeviceItem(
    deviceName: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE8F5E8) else Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick = onSelect
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⌚", fontSize = 20.sp, modifier = Modifier.padding(end = 16.dp))
            Text(
                text = deviceName,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color(0xFF006600) else Color.Black
            )
        }
    }
}

@Composable
fun WelcomeScreen(navController: NavController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF006600)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF006600)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.height(80.dp))
            Image(
                painter = painterResource(id = R.drawable.watch),
                contentDescription = "Smartwatch",
                modifier = Modifier.size(220.dp)
            )
            Text(
                "Soroti RRH\nHypertension Monitor",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(20.dp))
            Text(
                "Designed by Opio John Bosco",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(60.dp))
            Button(
                onClick = { navController.navigate("choice") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(horizontal = 40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    "Continue",
                    fontSize = 22.sp,
                    color = Color(0xFF006600),
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun ChoiceScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Get Started", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("←", fontSize = 28.sp, color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF006600)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { navController.navigate("register") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(horizontal = 40.dp, vertical = 10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006600))
            ) {
                Text("Register New Patient", fontSize = 20.sp, color = Color.White)
            }
            Spacer(Modifier.height(30.dp))
            Button(
                onClick = { navController.navigate("login") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(horizontal = 40.dp, vertical = 10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006600))
            ) {
                Text("Already Have Account? Login", fontSize = 20.sp, color = Color.White)
            }
            Spacer(Modifier.height(50.dp))
            Text("Designed by Opio John Bosco", color = Color.Gray)
        }
    }
}

@Composable
fun RegisterScreen(navController: NavController) {
    val viewModel: AppViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var patientId by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Register Patient", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("Back", fontSize = 28.sp, color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF006600))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            OutlinedTextField(
                value = patientId,
                onValueChange = { patientId = it },
                label = { Text("SRRH Patient ID") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = pin,
                onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) pin = it },
                label = { Text("Create PIN (4 digits)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = PasswordVisualTransformation()
            )
            OutlinedTextField(
                value = confirmPin,
                onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) confirmPin = it },
                label = { Text("Confirm PIN") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = PasswordVisualTransformation()
            )

            if (error.isNotEmpty()) {
                Text(error, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    error = "" // clear previous error
                    when {
                        fullName.isBlank() -> error = "Please enter full name"
                        phone.isBlank() -> error = "Please enter phone number"
                        patientId.isBlank() -> error = "Please enter patient ID"
                        pin.length != 4 -> error = "PIN must be 4 digits"
                        pin != confirmPin -> error = "PINs do not match"
                        else -> {
                            scope.launch {
                                val success = viewModel.register(fullName, phone, patientId, pin)
                                if (success) {
                                    navController.navigate("dashboard") {
                                        popUpTo("welcome") { inclusive = true }
                                    }
                                } else {
                                    error = "Phone or Patient ID already exists"
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006600))
            ) {
                Text("REGISTER", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.weight(1f))
            Text(
                "Designed by Opio John Bosco",
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun LoginScreen(navController: NavController) {
    val viewModel: AppViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    var phone by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Login Patient", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("Back", fontSize = 28.sp, color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF006600))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            OutlinedTextField(
                value = pin,
                onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) pin = it },
                label = { Text("4-digit PIN") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
            )

            if (error.isNotEmpty()) {
                Text(error, color = Color.Red)
            }

            Spacer(Modifier.height(30.dp))

            Button(
                onClick = {
                    when {
                        phone.isBlank() -> error = "Please enter phone number"
                        pin.length != 4 -> error = "PIN must be 4 digits"
                        else -> {
                            scope.launch {
                                val success = viewModel.login(phone, pin)
                                if (success) {
                                    navController.navigate("dashboard") {
                                        popUpTo("welcome") { inclusive = true }
                                    }
                                } else {
                                    error = "Wrong phone number or PIN"
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006600))
            ) {
                Text("LOGIN", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.weight(1f))
            Text("Designed by Opio John Bosco", color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: AppViewModel = hiltViewModel()
) {
    val readings by viewModel.readings.collectAsState(emptyList())
    // Remove unused currentUser
    // val currentUser by viewModel.currentUser.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("History & Graph", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("←", fontSize = 28.sp, color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF006600)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            if (readings.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No readings yet", fontSize = 20.sp, color = Color.Gray)
                        Spacer(Modifier.height(8.dp))
                        Text("Go to Manual Entry to save your first BP", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(readings.reversed()) { reading ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (reading.systolic >= 140 || reading.diastolic >= 90)
                                    Color(0xFFFFEBEE)
                                else
                                    Color(0xFFE8F5E8)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        "${reading.systolic}/${reading.diastolic} mmHg",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (reading.systolic >= 140 || reading.diastolic >= 90)
                                            Color.Red
                                        else
                                            Color(0xFF006600)
                                    )
                                    Text("Heart Rate: ${reading.heartRate} bpm", fontSize = 16.sp)
                                    if (reading.notes.isNotEmpty()) {
                                        Text(
                                            "\"${reading.notes}\"",
                                            fontSize = 14.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                                Text(
                                    SimpleDateFormat(
                                        "dd MMM yyyy\nhh:mm a",
                                        Locale.getDefault()
                                    ).format(reading.timestamp),
                                    textAlign = TextAlign.End,
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(Color(0xFFF5F5F5))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📈", fontSize = 48.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Blood Pressure Trend Graph",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text("Total Readings: ${readings.size}", color = Color.Gray)
                        }
                    }
                }
            }
            Text(
                "Designed by Opio John Bosco",
                color = Color.Gray,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun ManualEntryScreen(
    navController: NavController,
    viewModel: AppViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    var systolic by remember { mutableStateOf("") }
    var diastolic by remember { mutableStateOf("") }
    var heartRate by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var success by remember { mutableStateOf("") }

    // Get currentUser as a local variable
    val currentUser by viewModel.currentUser.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Manual BP Entry", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("←", fontSize = 28.sp, color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF006600)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Enter Blood Pressure Reading", fontSize = 24.sp, color = Color(0xFF006600))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = systolic,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() } && it.length <= 3) systolic = it
                    },
                    label = { Text("Systolic") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("e.g., 120") }
                )
                OutlinedTextField(
                    value = diastolic,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() } && it.length <= 3) diastolic = it
                    },
                    label = { Text("Diastolic") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("e.g., 80") }
                )
            }

            OutlinedTextField(
                value = heartRate,
                onValueChange = {
                    if (it.all { char -> char.isDigit() } && it.length <= 3) heartRate = it
                },
                label = { Text("Heart Rate (bpm)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = { Text("e.g., 72") }
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 3,
                placeholder = { Text("e.g., After exercise, morning reading...") }
            )

            if (error.isNotEmpty()) {
                Text(error, color = Color.Red)
            }
            if (success.isNotEmpty()) {
                Text(success, color = Color(0xFF006600))
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    when {
                        systolic.isBlank() -> error = "Please enter systolic value"
                        diastolic.isBlank() -> error = "Please enter diastolic value"
                        heartRate.isBlank() -> error = "Please enter heart rate"
                        systolic.toIntOrNull() == null -> error = "Invalid systolic value"
                        diastolic.toIntOrNull() == null -> error = "Invalid diastolic value"
                        heartRate.toIntOrNull() == null -> error = "Invalid heart rate"
                        else -> {
                            scope.launch {
                                try {
                                    // FIX: Use safe call with let instead of smart cast
                                    currentUser?.let { user ->
                                        val reading = BpReading(
                                            userId = user.id,
                                            systolic = systolic.toInt(),
                                            diastolic = diastolic.toInt(),
                                            heartRate = heartRate.toInt(),
                                            notes = notes,
                                            timestamp = System.currentTimeMillis()
                                        )

                                        viewModel.insertReading(reading)

                                        // Clear fields
                                        systolic = ""
                                        diastolic = ""
                                        heartRate = ""
                                        notes = ""
                                        error = ""
                                        success = "Reading saved successfully!"
                                    } ?: run {
                                        error = "Please login first"
                                    }
                                } catch (e: Exception) {
                                    error = "Failed to save reading: ${e.message}"
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006600))
            ) {
                Text("SAVE READING", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.weight(1f))
            Text(
                "Designed by Opio John Bosco",
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun FeatureItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("•", color = Color(0xFF006600), modifier = Modifier.padding(end = 8.dp))
        Text(text, fontSize = 16.sp)
    }
}

@Composable
fun SosScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("SOS Emergency", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("←", fontSize = 28.sp, color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF006600)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.Red)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("EMERGENCY SOS", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(20.dp))
            Text("Calling 112...", fontSize = 24.sp, color = Color.White)
            Spacer(Modifier.height(10.dp))
            Text("GPS location sent to emergency contacts", fontSize = 16.sp, color = Color.White)
            Spacer(Modifier.height(40.dp))
            Text(
                "SMS sent to:\n• Treatment Supporter\n• Hospital Doctor",
                fontSize = 18.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.weight(1f))
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("CANCEL", fontSize = 18.sp, color = Color.Red, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(20.dp))
            Text(
                "Designed by Opio John Bosco",
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

// Previews
// Previews - Updated with simple UI-only previews
@Preview(showBackground = true, name = "Welcome Screen")
@Composable
fun WelcomeScreenPreview() {
    HypertensionMonitorAppTheme {
        WelcomeScreen(rememberNavController())
    }
}

@Preview(showBackground = true, name = "Choice Screen")
@Composable
fun ChoiceScreenPreview() {
    HypertensionMonitorAppTheme {
        ChoiceScreen(rememberNavController())
    }
}

@Preview(showBackground = true, name = "Login Screen")
@Composable
fun LoginScreenPreview() {
    HypertensionMonitorAppTheme {
        // Simple UI-only preview
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Login Patient", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { }) {
                            Text("←", fontSize = 28.sp, color = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFF006600)
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.White)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    label = { Text("4-digit PIN") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                )

                Spacer(Modifier.height(30.dp))

                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006600))
                ) {
                    Text("LOGIN", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                ) {
                    Text("TEST LOGIN", fontSize = 16.sp, color = Color.White)
                }

                Spacer(Modifier.weight(1f))
                Text(
                    "Designed by Opio John Bosco",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Register Screen")
@Composable
fun RegisterScreenPreview() {
    HypertensionMonitorAppTheme {
        // Simple UI-only preview
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Register Patient", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { }) {
                            Text("←", fontSize = 28.sp, color = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFF006600)
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.White)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    label = { Text("SRRH Patient ID") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    label = { Text("Create PIN (4 digits)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation()
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    label = { Text("Confirm PIN") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006600))
                ) {
                    Text("REGISTER", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.weight(1f))
                Text(
                    "Designed by Opio John Bosco",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Dashboard")
@Composable
fun DashboardPreview() {
    HypertensionMonitorAppTheme {
        // Simple UI-only preview without ViewModel
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Hypertension Monitor", color = Color.White) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFF006600)
                    )
                )
            },
            bottomBar = {
                NavigationBar(containerColor = Color(0xFF006600)) {
                    NavigationBarItem(
                        selected = true,
                        onClick = { },
                        icon = { Text("🏠", fontSize = 24.sp) },
                        label = { Text("Home", color = Color.White) }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = { Text("📊", fontSize = 24.sp) },
                        label = { Text("History", color = Color.White) }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = { Text("✍️", fontSize = 24.sp) },
                        label = { Text("Manual", color = Color.White) }
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.White)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Welcome back, Opio John Bosco!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF006600)
                )
                Spacer(Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(Color(0xFFFFCDD2)),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Latest Reading", fontSize = 16.sp, color = Color.Gray)
                        Text(
                            "138/88 mmHg",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                        Text("Heart Rate: 72 bpm", fontSize = 18.sp)
                        Text("02:45 PM", fontSize = 16.sp, color = Color.Gray)
                        Text("Feeling excellent", fontSize = 14.sp, color = Color.Gray)
                        Text("NORMAL", color = Color.Green, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }

                Spacer(Modifier.height(20.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().height(130.dp),
                    colors = CardDefaults.cardColors(Color(0xFFE8F5E8))
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📊", fontSize = 40.sp, color = Color(0xFF006600))
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Last 7 Days Trend",
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF006600)
                            )
                            Text("(Tap to view full history)", fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                }

                Spacer(Modifier.height(30.dp))
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    colors = ButtonDefaults.buttonColors(Color.Red),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        "🆘 SOS – CALL 112",
                        fontSize = 24.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.weight(1f))
                Text(
                    "Designed by Opio John Bosco",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Settings Screen")
@Composable
fun SettingsScreenPreview() {
    HypertensionMonitorAppTheme {
        // Simple UI-only preview
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Settings", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { }) {
                            Text("←", fontSize = 28.sp, color = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFF006600)
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.White)
            ) {
                SettingsItem(
                    title = "My Profile",
                    subtitle = "Opio John Bosco",
                    action = {
                        Column(horizontalAlignment = Alignment.End) {
                            Text("ID: SRRH-2025-0744", fontSize = 12.sp, color = Color.Gray)
                            Text("Phone: 0771234567", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                )
                HorizontalDivider()

                SettingsItem(
                    title = "Language",
                    subtitle = "Choose app language",
                    action = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("English", color = Color(0xFF006600), fontWeight = FontWeight.Medium, modifier = Modifier.padding(end = 16.dp))
                            Switch(
                                checked = false,
                                onCheckedChange = { },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF006600),
                                    checkedTrackColor = Color(0xFF006600).copy(alpha = 0.5f)
                                )
                            )
                        }
                    }
                )
                HorizontalDivider()

                SettingsItem(
                    title = "Smartwatch Connection",
                    subtitle = "Not connected",
                    action = {
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006600)),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text("Pair", fontSize = 14.sp)
                        }
                    }
                )

                Spacer(Modifier.weight(1f))
                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("LOGOUT", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    "Designed by Opio John Bosco",
                    color = Color.Gray,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Manual Entry Screen")
@Composable
fun ManualEntryScreenPreview() {
    HypertensionMonitorAppTheme {
        // Simple UI-only preview
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Manual BP Entry", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { }) {
                            Text("←", fontSize = 28.sp, color = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFF006600)
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.White)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Enter Blood Pressure Reading", fontSize = 24.sp, color = Color(0xFF006600))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = "",
                        onValueChange = { },
                        label = { Text("Systolic") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text("e.g., 120") }
                    )
                    OutlinedTextField(
                        value = "",
                        onValueChange = { },
                        label = { Text("Diastolic") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text("e.g., 80") }
                    )
                }

                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    label = { Text("Heart Rate (bpm)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("e.g., 72") }
                )

                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 3,
                    placeholder = { Text("e.g., After exercise, morning reading...") }
                )

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006600))
                ) {
                    Text("SAVE READING", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.weight(1f))
                Text(
                    "Designed by Opio John Bosco",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "History Screen")
@Composable
fun HistoryPreview() {
    HypertensionMonitorAppTheme {
        // Simple UI-only preview for History
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("History & Graph", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { }) {
                            Text("←", fontSize = 28.sp, color = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFF006600)
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.White)
            ) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(listOf(
                        BpReading(userId = 1L, systolic = 148, diastolic = 96, heartRate = 88, notes = "After lunch", timestamp = System.currentTimeMillis()),
                        BpReading(userId = 1L, systolic = 132, diastolic = 84, heartRate = 70, notes = "Morning", timestamp = System.currentTimeMillis() - 86_400_000L),
                        BpReading(userId = 1L, systolic = 138, diastolic = 88, heartRate = 72, notes = "", timestamp = System.currentTimeMillis() - 172_800_000L)
                    )) { reading ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (reading.systolic >= 140 || reading.diastolic >= 90)
                                    Color(0xFFFFEBEE)
                                else
                                    Color(0xFFE8F5E8)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        "${reading.systolic}/${reading.diastolic} mmHg",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (reading.systolic >= 140 || reading.diastolic >= 90)
                                            Color.Red
                                        else
                                            Color(0xFF006600)
                                    )
                                    Text("Heart Rate: ${reading.heartRate} bpm", fontSize = 16.sp)
                                    if (reading.notes.isNotEmpty()) {
                                        Text(
                                            "\"${reading.notes}\"",
                                            fontSize = 14.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                                Text(
                                    SimpleDateFormat(
                                        "dd MMM yyyy\nhh:mm a",
                                        Locale.getDefault()
                                    ).format(reading.timestamp),
                                    textAlign = TextAlign.End,
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(Color(0xFFF5F5F5))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📈", fontSize = 48.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Blood Pressure Trend Graph",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text("Total Readings: 3", color = Color.Gray)
                        }
                    }
                }
                Text(
                    "Designed by Opio John Bosco",
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

// Add other previews as needed
@Preview(showBackground = true, name = "About Screen")
@Composable
fun AboutScreenPreview() {
    HypertensionMonitorAppTheme {
        AboutScreen(rememberNavController())
    }
}

@Preview(showBackground = true, name = "Pair Watch Screen")
@Composable
fun PairWatchScreenPreview() {
    HypertensionMonitorAppTheme {
        PairWatchScreen(rememberNavController())
    }
}

@Preview(showBackground = true, name = "SOS Screen")
@Composable
fun SosScreenPreview() {
    HypertensionMonitorAppTheme {
        SosScreen(rememberNavController())
    }
}