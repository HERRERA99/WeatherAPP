package com.aitor.weatherapp.weather.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.aitor.weatherapp.R
import com.aitor.weatherapp.weather.core.response.LocationData
import com.aitor.weatherapp.weather.domain.WeatherUseCase
import com.aitor.weatherapp.weather.ui.model.WeatherModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.Manifest
import android.app.Activity
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.aitor.weatherapp.weather.ui.model.HourlyWeather
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.TimeZone


@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherUseCase: WeatherUseCase
) : ViewModel() {

    private val _weatherData = MutableStateFlow<WeatherModel?>(null)
    val weatherData: StateFlow<WeatherModel?> = _weatherData

    private val _locationError = MutableStateFlow(false)
    val locationError: StateFlow<Boolean> = _locationError

    fun getWeather(location: LocationData, timezone: String) {
        viewModelScope.launch {
            val weather = weatherUseCase(location = location, timezone = timezone)
            _weatherData.value = weather
        }
    }

    fun getWeatherAnimation(wmoCode: Int): Int {
        return when (wmoCode) {
            0 -> R.raw.sunny // Clear sky
            1, 2 -> R.raw.party_cloudy // Mainly clear, partly cloudy,
            3 -> R.raw.cloudy // overcast
            45, 48 -> R.raw.fog // Fog and depositing rime fog
            51, 53, 55, 56, 57 -> R.raw.light_rain // Drizzle: Light, moderate, dense intensity, Light and dense intensity
            61, 63, 65, 66, 67 -> R.raw.rain // Rain: Slight, moderate, heavy intensity, Light and heavy intensity
            71, 73, 75, 77 -> R.raw.snow // Snow fall: Slight, moderate, heavy intensity, Snow grains
            80, 81, 82 -> R.raw.sunny_rain // Rain showers: Slight, moderate, and violent
            85, 86 -> R.raw.sunny_snow // Snow showers slight and heavy
            95 -> R.raw.thunderstorm // Thunderstorm: Slight or moderate
            96, 99 -> R.raw.sunny_storm // Thunderstorm with slight and heavy hail
            else -> R.raw.party_cloudy // Default animation
        }
    }

    @SuppressLint("MissingPermission")
    fun getDeviceLocation(context: Context): LocationData? {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null // No tenemos permisos
        }

        val location: Location? = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        return location?.let { LocationData(it.latitude, it.longitude) }
    }

    fun getLocationName(context: Context, latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            addresses?.firstOrNull()?.locality ?: "Ubicación desconocida"
        } catch (e: Exception) {
            "Ubicación desconocida"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun extraerHora(fechaString: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        val fecha = LocalDateTime.parse(fechaString, formatter)
        return fecha.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun obtenerNombreDia(fecha: String): String {
        val date = LocalDate.parse(fecha)
        val hoy = LocalDate.now()
        val ayer = hoy.minusDays(1)

        return when (date) {
            hoy -> "Hoy"
            ayer -> "Ayer"
            else -> date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("es", "ES"))
                .replaceFirstChar { it.uppercaseChar() }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun obtenerSiguientes24Horas(hourly: HourlyWeather): List<Int> {
        val formato = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        val ahora = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS) // Redondear a la hora actual
        val siguienteHora = ahora.plusHours(1) // Tomamos desde la siguiente hora en adelante

        return hourly.time
            .mapIndexed { index, hora -> index to LocalDateTime.parse(hora, formato) }
            .filter { (_, fechaHora) -> !fechaHora.isBefore(siguienteHora) } // Filtrar solo las horas a partir de la siguiente
            .take(24) // Tomar solo las siguientes 24 horas
            .map { (index, _) -> index } // Devolver solo los índices
    }

    fun getWeatherByCity(cityName: String, context: Context) {
        viewModelScope.launch {
            val coordinates = geocodeCity(cityName, context)
            if (coordinates != null) {
                _locationError.value = false // Reiniciamos el error si se encuentra la ciudad
                getWeather(LocationData(coordinates.first, coordinates.second), TimeZone.getDefault().id)
                Log.d("WeatherViewModel", "Ciudad encontrada: $cityName")
                Log.d("_locationError", "_locationError: ${_locationError.value}")
            } else {
                _locationError.value = true // Activamos el error si no se encuentra la ciudad
                Log.d("WeatherViewModel", "Ciudad no encontrada: $cityName")
                Log.d("_locationError", "_locationError: ${_locationError.value}")
            }
        }
    }

    fun geocodeCity(cityName: String, context: Context): Pair<Double, Double>? {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocationName(cityName, 1)
            addresses?.firstOrNull()?.let {
                Pair(it.latitude, it.longitude)
            }
        } catch (e: Exception) {
            null
        }
    }

    fun setLocationError() {
        _locationError.value = true
    }

    fun resetLocationError() {
        _locationError.value = false

    }
}