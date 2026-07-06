package com.example

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: InvitationViewModel = viewModel()
            
            MyApplicationTheme(darkTheme = viewModel.isDarkMode) {
                // Determine whether onboarding is completed
                val context = LocalContext.current
                val prefs = remember { context.getSharedPreferences("infinity_invitation_prefs", Context.MODE_PRIVATE) }
                var currentScreen by remember { mutableStateOf("Splash") }
                
                // Back Button Handler for proper stack simulation
                BackHandler(enabled = currentScreen != "Home" && currentScreen != "Splash") {
                    when (currentScreen) {
                        "Onboarding" -> currentScreen = "Splash"
                        "Templates" -> currentScreen = "Home"
                        "Editor" -> currentScreen = "Home"
                        "EventManager" -> currentScreen = "Editor"
                        "About" -> currentScreen = "Home"
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
                            when (screen) {
                                "Splash" -> {
                                    SplashScreen(onNavigateToNext = {
                                        val isDone = prefs.getBoolean("onboarding_completed", false)
                                        currentScreen = if (isDone) "Home" else "Onboarding"
                                    })
                                }
                                "Onboarding" -> {
                                    OnboardingScreen(onFinished = {
                                        prefs.edit().putBoolean("onboarding_completed", true).apply()
                                        currentScreen = "Home"
                                    })
                                }
                                "Home" -> {
                                    HomeScreen(
                                        viewModel = viewModel,
                                        onNavigateToTemplates = { currentScreen = "Templates" },
                                        onNavigateToEditor = { currentScreen = "Editor" },
                                        onNavigateToEventManager = { currentScreen = "EventManager" },
                                        onNavigateToAbout = { currentScreen = "About" }
                                    )
                                }
                                "Templates" -> {
                                    TemplateLibraryScreen(
                                        viewModel = viewModel,
                                        onNavigateToEditor = { currentScreen = "Editor" },
                                        onBack = { currentScreen = "Home" }
                                    )
                                }
                                "Editor" -> {
                                    EditorScreen(
                                        viewModel = viewModel,
                                        onNavigateToEventManager = { currentScreen = "EventManager" },
                                        onBack = { currentScreen = "Home" }
                                    )
                                }
                                "EventManager" -> {
                                    EventManagerScreen(
                                        viewModel = viewModel,
                                        onBack = { currentScreen = "Editor" }
                                    )
                                }
                                "About" -> {
                                    AboutScreen(
                                        viewModel = viewModel,
                                        onBack = { currentScreen = "Home" }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
