package com.sonholab.androidstarter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sonholab.androidstarter.core.ui.theme.AndroidStarterTheme
import com.sonholab.androidstarter.features.auth.presentation.LoginScreen
import com.sonholab.androidstarter.features.auth.presentation.LoginUiState
import com.sonholab.androidstarter.features.auth.presentation.LoginViewModel
import com.sonholab.androidstarter.features.users.presentation.UsersScreen
import dagger.hilt.android.AndroidEntryPoint

private object Routes {
    const val LOGIN = "login"
    const val USERS = "users"
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidStarterTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
private fun AppNavigation(
    navController: NavHostController = rememberNavController(),
) {
    val loginViewModel: LoginViewModel = hiltViewModel()
    val loginState by loginViewModel.uiState.collectAsState()

    val startDestination = if (loginState is LoginUiState.Success) {
        Routes.USERS
    } else {
        Routes.LOGIN
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.USERS) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.USERS) {
            UsersScreen(
                onLogout = {
                    loginViewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.USERS) { inclusive = true }
                    }
                },
            )
        }
    }
}
