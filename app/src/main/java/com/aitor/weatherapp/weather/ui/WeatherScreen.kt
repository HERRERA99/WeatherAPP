package com.aitor.weatherapp.weather.ui

import androidx.compose.foundation.lazy.items
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.airbnb.lottie.compose.*
import com.aitor.weatherapp.weather.core.response.LocationData
import java.util.TimeZone
import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.aitor.weatherapp.weather.ui.model.HourlyWeather
import com.aitor.weatherapp.weather.ui.model.WeatherModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherScreen(
    modifier: Modifier,
    viewModel: WeatherViewModel,
    context: Context,
    snackbarHostState: SnackbarHostState
) {
    val activity = context as? Activity
    var showDialog by remember { mutableStateOf(false) }
    var location by remember {
        mutableStateOf(
            viewModel.getDeviceLocation(context) ?: LocationData(
                43.3082,
                -4.2357
            )
        )
    }
    val weatherData by viewModel.weatherData.collectAsState()
    var locationName by remember { mutableStateOf("Cargando...") }
    val locationError by viewModel.locationError.collectAsState(false)

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    1001
                )
            }
        }
    }

    LaunchedEffect(location) {
        locationName = viewModel.getLocationName(context, location.latitude, location.longitude)
        viewModel.getWeather(location, TimeZone.getDefault().id)
    }

    LaunchedEffect(locationError) {
        if (locationError == true) {
            snackbarHostState.showSnackbar("Ciudad no encontrada")
            viewModel.resetLocationError()  // Reiniciar el error
            locationName = viewModel.getLocationName(context, location.latitude, location.longitude)
            viewModel.getWeather(location, TimeZone.getDefault().id)
        }
    }

    if (showDialog) {
        LocationDialog(
            onDismiss = { showDialog = false },
            onLocationSelected = { newLocation ->
                locationName = newLocation
                viewModel.getWeatherByCity(newLocation, context) // Nueva función para obtener clima
                showDialog = false
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF2898EE))
    ) {
        TopBar(
            onLocationClick = {
                locationName =
                    viewModel.getLocationName(context, location.latitude, location.longitude)
                viewModel.getWeather(location, TimeZone.getDefault().id)
            },
            onSearchClick = { showDialog = true }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF2898EE))
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            weatherData?.let { weather ->

                // Lugar
                Lugar(
                    modifier = Modifier.fillMaxWidth(),
                    locationName = locationName
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Icono tiempo
                IconWeather(viewModel, weather.currentWeather.weatherCode)

                Spacer(modifier = Modifier.height(24.dp))

                // Temperatura
                Temperatura(weather)

                Spacer(modifier = Modifier.height(16.dp))

                // Temperaturas del dia
                TemperaturasDia(weather)

                Spacer(modifier = Modifier.height(16.dp))

                // Detalles
                DetallesDia(weather)

                Spacer(modifier = Modifier.height(16.dp))

                // Hourly
                HourlyRow(hourly = weather.hourlyWeather, viewModel)

                Spacer(modifier = Modifier.height(16.dp))

                // Dayly
                DaylyColumn(weather, viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(onLocationClick: () -> Unit, onSearchClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "WeatherApp",
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
        },
        actions = {
            // Ícono de ubicación
            Icon(
                imageVector = Icons.Filled.LocationSearching,
                contentDescription = "Ubicación",
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onLocationClick() }
                    .padding(8.dp)
            )
            // Ícono de búsqueda
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Buscar",
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onSearchClick() }
                    .padding(8.dp)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF2898EE),
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

@Composable
fun LocationDialog(
    onDismiss: () -> Unit,
    onLocationSelected: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF2898EE),
        title = {
            Text(
                text = "Cambiar ubicación",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
        },
        text = {
            Column {
                Text(
                    text = "Ingresa el nombre de la ciudad:",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.SansSerif
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Envolver el TextField en un Box para aplicar el fondo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp)) // Fondo semitransparente
                        .padding(8.dp) // Padding interno
                ) {
                    TextField(
                        value = text,
                        onValueChange = { text = it },
                        label = {
                            Text(
                                text = "Ciudad",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 16.sp,
                                fontFamily = FontFamily.SansSerif
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.White),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.White,
                            unfocusedIndicatorColor = Color.White.copy(alpha = 0.5f),
                            cursorColor = Color.White
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onLocationSelected(text) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF2898EE)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Aceptar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.2f),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Cancelar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

@Composable
fun Lugar(modifier: Modifier, locationName: String) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.LocationOn,
            contentDescription = "icon",
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = locationName,
            color = Color.White,
            fontSize = 18.sp,
            fontFamily = FontFamily.SansSerif
        )
    }
}

@Composable
fun Temperatura(weather: WeatherModel) {
    Text(
        text = "${weather.currentWeather.temperature2m}°C",
        color = Color.White,
        fontSize = 48.sp,  // Tamaño grande
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.SansSerif
    )
}

@Composable
fun TemperaturasDia(weather: WeatherModel) {
    Text(
        text = "${weather.currentWeather.temperature2mMax}º / ${weather.currentWeather.temperature2mMin}º  •  Sensación térmica: ${weather.currentWeather.apparentTemperature}º",
        color = Color.White,
        fontSize = 18.sp,
        fontFamily = FontFamily.SansSerif
    )
}

@Composable
fun DetallesDia(weather: WeatherModel) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .background(Color(0xff107acc).copy(alpha = 0.8f))
            .padding(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            MainIcons(
                Icons.Filled.Air,
                "${weather.currentWeather.windSpeed10m} Km/h",
                Modifier.weight(1f)
            )
            MainIcons(
                Icons.Filled.WbSunny,
                "${weather.currentWeather.uvIndexMax} UV",
                Modifier.weight(1f)
            )
            MainIcons(
                Icons.Filled.WaterDrop,
                "${weather.currentWeather.precipitationProbabilityMax} %",
                Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MainIcons(imageVector: ImageVector, value: String, modifier: Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Icon(
            imageVector = imageVector,
            contentDescription = "icon",
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            color = Color.White,
            fontSize = 16.sp,
            fontFamily = FontFamily.SansSerif
        )
    }
}

@Composable
fun IconWeather(viewModel: WeatherViewModel, weatherCode: Int) {
    val composition = rememberLottieComposition(
        LottieCompositionSpec.RawRes(viewModel.getWeatherAnimation(weatherCode))
    )
    Box(
        modifier = Modifier.size(250.dp), // Tamaño ajustado para que encaje mejor en el fondo blanco
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition.value,
            iterations = LottieConstants.IterateForever
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HourlyRow(hourly: HourlyWeather, viewModel: WeatherViewModel) {
    val indices = viewModel.obtenerSiguientes24Horas(hourly)

    LazyRow(modifier = Modifier.fillMaxWidth()) {
        items(
            items = hourly.time.slice(indices)
        ) { index ->
            HourlyItem(
                time = index, // index ya es el valor del tiempo
                temperature = hourly.temperature2m[hourly.time.indexOf(index)],
                weatherCode = hourly.weatherCode[hourly.time.indexOf(index)],
                proba = hourly.precipitationProbability[hourly.time.indexOf(index)],
                viewModel = viewModel
            )

            Spacer(Modifier.size(8.dp))
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HourlyItem(
    time: String,
    temperature: Double,
    weatherCode: Int,
    proba: Int,
    viewModel: WeatherViewModel
) {
    val composition = rememberLottieComposition(
        LottieCompositionSpec.RawRes(viewModel.getWeatherAnimation(weatherCode))
    )
    val hora = viewModel.extraerHora(time)

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xff107acc).copy(alpha = 0.8f))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = hora, fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 16.sp,
            fontFamily = FontFamily.SansSerif
        )
        LottieAnimation(
            composition = composition.value,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = "$temperature°C",
            color = Color.White,
            fontSize = 16.sp,
            fontFamily = FontFamily.SansSerif
        )
        Text(
            text = "$proba %",
            color = Color.White,
            fontSize = 16.sp,
            fontFamily = FontFamily.SansSerif
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DaylyColumn(weather: WeatherModel, viewModel: WeatherViewModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xff107acc).copy(alpha = 0.8f))
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            weather.dailyWeather.time.indices.forEach { index ->
                DaylyItem(
                    dia = viewModel.obtenerNombreDia(weather.dailyWeather.time[index]),
                    weatherCode = weather.dailyWeather.weatherCode[index],
                    temperatureMax = weather.dailyWeather.temperature2mMax[index],
                    temperatureMin = weather.dailyWeather.temperature2mMin[index],
                    precipitationProbability = weather.dailyWeather.precipitationProbabilityMax[index],
                    viewModel = viewModel
                )
                Spacer(Modifier.size(8.dp))
            }
        }
    }
}

@Composable
fun DaylyItem(
    dia: String,
    weatherCode: Int,
    temperatureMax: Double,
    temperatureMin: Double,
    precipitationProbability: Int,
    viewModel: WeatherViewModel
) {
    val composition = rememberLottieComposition(
        LottieCompositionSpec.RawRes(viewModel.getWeatherAnimation(weatherCode))
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Día (40% de ancho, alineado a la izquierda)
        Text(
            text = dia,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(0.4f)
                .align(Alignment.CenterVertically)
        )

        // Probabilidad de lluvia (15%, alineado a la derecha)
        Row(
            modifier = Modifier
                .weight(0.15f)
                .wrapContentWidth(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.WaterDrop,
                contentDescription = "icon",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "$precipitationProbability%",
                color = Color.White,
                fontSize = 16.sp
            )
        }

        // Icono del clima (15%, alineado a la derecha)
        LottieAnimation(
            composition = composition.value,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier
                .size(32.dp)
                .weight(0.15f)
                .wrapContentWidth(Alignment.End)
        )

        // Temperatura máxima (15%, alineado a la derecha)
        Text(
            text = "${temperatureMax}º",
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier
                .weight(0.15f)
                .wrapContentWidth(Alignment.End)
        )

        // Temperatura mínima (15%, alineado a la derecha)
        Text(
            text = "${temperatureMin}º",
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier
                .weight(0.15f)
                .wrapContentWidth(Alignment.End)
        )
    }
}

