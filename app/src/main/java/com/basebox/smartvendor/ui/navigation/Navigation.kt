package com.basebox.smartvendor.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
//import androidx.hilt.navigation.compose.hiltViewModel
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.basebox.smartvendor.ui.screens.HomeScreen
import com.basebox.smartvendor.ui.screens.SplashScreen
import com.basebox.smartvendor.ui.screens.auth.LoginScreen
import com.basebox.smartvendor.ui.screens.auth.SignUpScreen
import com.basebox.smartvendor.ui.screens.onboarding.OnboardingScreen
import com.basebox.smartvendor.ui.viewmodels.AuthViewModel
import com.basebox.smartvendor.ui.viewmodels.HomeViewModel
import com.basebox.smartvendor.ui.viewmodels.InventoryViewModel
import com.basebox.smartvendor.ui.viewmodels.NotificationsViewModel
import com.basebox.smartvendor.ui.viewmodels.SettingsViewModel
import com.basebox.smartvendor.ui.viewmodels.StockViewModel
import com.basebox.smartvendor.ui.viewmodels.VendorViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(
    homeViewModel: HomeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    vendorViewModel: VendorViewModel = hiltViewModel(),
    inventoryViewModel: InventoryViewModel = hiltViewModel(),
    notificationsViewModel: NotificationsViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
//    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()

    val startDestination = "splash"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("splash") {
            if (authState != null) {
                SplashScreen(onTimeout = {
                    navController.navigate("home") {
                        popUpTo("splash") {
                            inclusive = true
                        }
                    }
                })
            } else {
                SplashScreen(onTimeout = {
                    navController.navigate("onboarding") {
                        popUpTo("splash") {
                            inclusive = true
                        }
                    }
                })
            }
        }
        if (authState == null) {
            composable("onboarding") {
                OnboardingScreen(onFinished = { navController.navigate("signup") })
            }

            composable("signup") {
                SignUpScreen(
                    viewModel = authViewModel,
                    onSignUp =
                        { name, shopName, phone, email, password ->
                            authViewModel.registerVendor(
                                fullName = name,
                                email = email,
                                phone = phone,
                                businessName = shopName,
                                password = password,
                                onSuccess = { navController.navigate("login") },
                                onError = { /* Handle error */ })

//                    if (authState != null) navController.navigate("home") { popUpTo("signup") { inclusive = true } }
                        },
                    onLoginClicked = { navController.navigate("login") }
                )
            }
            composable("login") {
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = {

                        navController.navigate("home") {
                            popUpTo("login") {
                                inclusive = true
                            }
                        }
                    },
                    onSignUpClicked = { navController.navigate("signup") }
                )
            }
        }
        composable("home") {
            HomeScreen(homeViewModel, authState!!.uid,  vendorViewModel, authViewModel, inventoryViewModel, notificationsViewModel, settingsViewModel )
        }
    }
}
