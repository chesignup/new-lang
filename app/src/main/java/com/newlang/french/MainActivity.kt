package com.newlang.french

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.newlang.french.ui.ConversationScreen
import com.newlang.french.ui.HomeScreen
import com.newlang.french.ui.PhrasesScreen
import com.newlang.french.ui.ProgressScreen
import com.newlang.french.ui.RoutineScreen
import com.newlang.french.ui.SettingsScreen
import com.newlang.french.ui.TranslateScreen
import com.newlang.french.ui.WritingScreen
import com.newlang.french.ui.theme.Burgundy
import com.newlang.french.ui.theme.Cream
import com.newlang.french.ui.theme.InkMuted
import com.newlang.french.ui.theme.NewLangTheme
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val locale = Locale("he")
        Locale.setDefault(locale)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewLangTheme {
                val app = NewLangApp.instance
                var progress by remember { mutableStateOf(app.progress.load()) }
                val nav = rememberNavController()
                val backStack by nav.currentBackStackEntryAsState()
                val route = backStack?.destination?.route ?: "home"
                val tabs = listOf("home", "progress", "settings")
                val showBar = route in tabs

                fun refresh() {
                    progress = app.progress.load()
                }

                Scaffold(
                    containerColor = Cream,
                    bottomBar = {
                        if (showBar) {
                            NavigationBar(containerColor = Cream, contentColor = Burgundy) {
                                NavigationBarItem(
                                    selected = route == "home",
                                    onClick = { nav.navigate("home") { launchSingleTop = true } },
                                    icon = { Icon(Icons.Outlined.Home, contentDescription = "בית") },
                                    label = { Text("בית") },
                                    colors = navColors()
                                )
                                NavigationBarItem(
                                    selected = route == "progress",
                                    onClick = {
                                        refresh()
                                        nav.navigate("progress") { launchSingleTop = true }
                                    },
                                    icon = { Icon(Icons.Outlined.Whatshot, contentDescription = "התקדמות") },
                                    label = { Text("התקדמות") },
                                    colors = navColors()
                                )
                                NavigationBarItem(
                                    selected = route == "settings",
                                    onClick = { nav.navigate("settings") { launchSingleTop = true } },
                                    icon = { Icon(Icons.Outlined.Settings, contentDescription = "הגדרות") },
                                    label = { Text("הגדרות") },
                                    colors = navColors()
                                )
                            }
                        }
                    }
                ) { padding ->
                    NavHost(
                        navController = nav,
                        startDestination = "home",
                        modifier = Modifier.padding(padding)
                    ) {
                        composable("home") {
                            HomeScreen(
                                progress = progress,
                                live = app.settings.load().hasKey,
                                onOpen = { id -> nav.navigate(id) }
                            )
                        }
                        composable("progress") { ProgressScreen(progress) }
                        composable("settings") {
                            SettingsScreen(onSaved = { refresh() })
                        }
                        composable("conversation") {
                            ConversationScreen(
                                cafe = false,
                                onClose = { nav.popBackStack() },
                                onFinished = { refresh() }
                            )
                        }
                        composable("cafe") {
                            ConversationScreen(
                                cafe = true,
                                onClose = { nav.popBackStack() },
                                onFinished = { refresh() }
                            )
                        }
                        composable("translate") {
                            TranslateScreen(
                                onClose = { nav.popBackStack() },
                                onFinished = { refresh() }
                            )
                        }
                        composable("writing") {
                            WritingScreen(
                                onClose = { nav.popBackStack() },
                                onFinished = { refresh() }
                            )
                        }
                        composable("phrases") {
                            PhrasesScreen(
                                onClose = { nav.popBackStack() },
                                onFinished = { refresh() }
                            )
                        }
                        composable("routine") {
                            RoutineScreen(
                                onClose = { nav.popBackStack() },
                                onFinished = { refresh() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun navColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = Burgundy,
    selectedTextColor = Burgundy,
    indicatorColor = Color.White,
    unselectedIconColor = InkMuted,
    unselectedTextColor = InkMuted
)
