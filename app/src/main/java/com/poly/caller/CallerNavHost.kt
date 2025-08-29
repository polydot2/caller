package com.poly.caller

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.poly.caller.presentation.home.HomeScreen
import com.poly.caller.presentation.screen1.ScreenAScreen
import com.poly.caller.presentation.screen2.ScreenBScreen
import com.poly.mylibrary.CameraScreen
import com.poly.mylibrary.data.Configuration
import com.poly.mylibrary.data.ConfigurationRepository
import com.poly.mylibrary.data.Result
import com.poly.mylibrary.data.ResultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject

@HiltViewModel
class CaptureContractViewModel @Inject constructor(
    private val configurationRepository: ConfigurationRepository,
    private val resultRepository: ResultRepository
) : ViewModel() {
    init {
        configurationRepository.save(Configuration(param1 = "fromApp", param2 = true))
    }

    fun observeResult(onResult: (Result) -> Unit) {
        viewModelScope.launch {
            resultRepository.resultFlow.collectLatest { result ->
                if (result != null) {
                    onResult(result)
                    resultRepository.setResult(null) // Réinitialisation
                }
            }
        }
    }
}

@HiltViewModel
class CaptureContract2ViewModel @Inject constructor(
    private val configurationRepository: ConfigurationRepository,
    private val resultRepository: ResultRepository
) : ViewModel() {
    init {
        configurationRepository.save(Configuration(param1 = "fromApp", param2 = true))
    }

    fun observeResult(onResult: (Result) -> Unit) {
        viewModelScope.launch {
            resultRepository.resultFlow.collectLatest { result ->
                if (result != null) {
                    onResult(result)
                    resultRepository.setResult(null) // Réinitialisation
                }
            }
        }
    }
}

@Composable
fun CallerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    finish: () -> Unit,
) {
    val viewModel: CaptureContractViewModel = hiltViewModel() // ViewModel partagé au niveau du NavHost
    val viewModel2: CaptureContract2ViewModel = hiltViewModel() // ViewModel partagé au niveau du NavHost

    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier
    ) {
        composable<HomeRoute> {
            HomeScreen(
                navigateTo = { index ->
                    val route = when (index) {
                        0 -> ScreenARoute
                        1 -> ScreenBRoute
                        2 -> ScreenCRoute
                        3 -> ScreenDRoute
                        else -> ScreenDRoute
                    }

                    navController.navigate(route)
                }
            )
        }
        composable<ScreenARoute> {
            ScreenAScreen()
        }
        composable<ScreenBRoute> {
            ScreenBScreen()
        }
        composable<ScreenCRoute> {
            LaunchedEffect(Unit) {
                viewModel.observeResult { result ->
                    // Traiter le résultat et naviguer vers D
                    navController.navigate(ScreenDRoute)
                }
            }

            CameraScreen(
                exitApplication = {
                    navController.popBackStack()
                }
            )
        }
        composable<ScreenDRoute> {
            LaunchedEffect(Unit) {
                viewModel2.observeResult { result ->
                    // traiter le résultat

                    // finir l'activité
                    finish()
                }
            }

            CameraScreen(
                exitApplication = {
                    navController.popBackStack()
                }
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

@Serializable
data object ScreenCRoute : Route() {
    override val title: String
        get() = "C"
}


@Serializable
data object ScreenDRoute : Route() {
    override val title: String
        get() = "D"
}

