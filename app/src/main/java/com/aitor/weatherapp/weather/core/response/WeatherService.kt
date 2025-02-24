package com.aitor.weatherapp.weather.core.response

import com.aitor.weatherapp.weather.data.network.WeatherClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WeatherService @Inject constructor(private val weatherClient: WeatherClient) {

    suspend fun doWeatherRequest(location: LocationData, timezone: String): WeatherResponse {
        return withContext(Dispatchers.IO) {
            weatherClient.getWeather(
                latitude = location.latitude,
                longitude = location.longitude,
                timezone = timezone
            )
        }
    }
}