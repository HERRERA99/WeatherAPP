package com.aitor.weatherapp.weather.data

import com.aitor.weatherapp.weather.core.response.LocationData
import com.aitor.weatherapp.weather.core.response.WeatherResponse
import com.aitor.weatherapp.weather.core.response.WeatherService
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val weatherService: WeatherService) {
    suspend fun getWeather(location: LocationData, timezone: String): WeatherResponse {
        return weatherService.doWeatherRequest(location = location, timezone =  timezone)
    }

}