package com.poly.caller

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.poly.caller.presentation.home.HomeScreen
import com.poly.caller.presentation.screen1.ScreenAScreen
import com.poly.caller.presentation.screen2.ScreenBScreen
import kotlinx.serialization.Serializable

@Composable
fun CallerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier
    ) {
        composable<HomeRoute> {
            HomeScreen(
                navigateTo = { index ->
                    val route = if (index == 0) ScreenARoute else ScreenBRoute
                    navController.navigate(route)
                }
            )
        }
        composable<ScreenARoute> {
            ScreenAScreen(
            )
        }
        composable<ScreenBRoute> {
            ScreenBScreen(
            )
        }
    }
}

@Serializable
sealed class Route {
    abstract val title: String
}

@Serializable
data object BaseRoute : Route() {
    override val title: String = ""
}

@Serializable
data object HomeRoute : Route() {
    override val title: String
        get() = "Home"
}

@Serializable
data object ScreenARoute : Route() {
    override val title: String
        get() = "A"
}

@Serializable
data object ScreenBRoute : Route() {
    override val title: String
        get() = "B"
}
