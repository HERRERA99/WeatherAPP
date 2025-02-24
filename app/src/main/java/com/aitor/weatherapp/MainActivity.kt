package com.aitor.weatherapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.aitor.weatherapp.ui.theme.WeatherAppTheme
import com.aitor.weatherapp.weather.ui.WeatherScreen
import com.aitor.weatherapp.weather.ui.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val weatherViewModel: WeatherViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val context = this

                // Envolver el Scaffold en un Box con fondo azul
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF2898EE)) // Fondo azul
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        containerColor = Color.Transparent // Hacer transparente el fondo del Scaffold
                    ) { innerPadding ->
                        WeatherScreen(
                            modifier = Modifier.padding(innerPadding),
                            viewModel = weatherViewModel,
                            context = context,
                            snackbarHostState = snackbarHostState
                        )
                    }
                }
            }
        }
    }
}
