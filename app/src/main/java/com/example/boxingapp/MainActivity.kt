package com.example.boxingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.example.boxingapp.ui.theme.BoxingAppTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.boxingapp.presentation.screens.FighterDetailScreen
import com.example.boxingapp.presentation.screens.FighterScreen
import com.example.boxingapp.presentation.screens.NavRoutes
import com.example.boxingapp.data.model.Fighter
import com.example.boxingapp.presentation.screens.FavoritesScreen
import com.example.boxingapp.presentation.screens.HomeScreen
import com.example.boxingapp.data.preferences.ThemePreferenceRepository
import com.google.gson.Gson
import java.net.URLDecoder

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val themeRepo = ThemePreferenceRepository(applicationContext)

        setContent {
            val darkThemeEnabled by themeRepo.darkThemeFlow.collectAsState(initial = false)
            val scope = rememberCoroutineScope()

            BoxingAppTheme(darkTheme = darkThemeEnabled) {
                Surface {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = NavRoutes.Home
                    ) {
                        composable(NavRoutes.Home) {
                            HomeScreen(
                                navController = navController,
                                isDarkTheme = darkThemeEnabled,
                                onToggleTheme = { enabled -> scope.launch { themeRepo.setDarkTheme(enabled) } }

                            )
                        }
                        composable(NavRoutes.FighterList) {
                            FighterScreen(navController = navController)
                        }
                        composable(
                            route = "${NavRoutes.FighterDetail}/{fighterJson}",
                            arguments = listOf(navArgument("fighterJson") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val json = backStackEntry.arguments?.getString("fighterJson")?.let {
                                URLDecoder.decode(it, "UTF-8")
                            }
                            val fighter = Gson().fromJson(json, Fighter::class.java)
                            FighterDetailScreen(fighter = fighter)
                        }
                        composable(NavRoutes.Favorites) {
                            FavoritesScreen(navController = navController)
                        }

                    }

                }
            }
        }
    }
}
