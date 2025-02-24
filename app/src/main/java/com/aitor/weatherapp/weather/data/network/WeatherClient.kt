package com.aitor.weatherapp.weather.data.network

import com.aitor.weatherapp.weather.core.response.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherClient {

    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,apparent_temperature,weather_code,wind_speed_10m",
        @Query("hourly") hourly: String = "temperature_2m,precipitation_probability,weather_code",
        @Query("daily") daily: String = "weather_code,temperature_2m_max,temperature_2m_min,uv_index_max,precipitation_probability_max",
        @Query("timezone") timezone: String = "GMT",
        @Query("past_days") pastDays: Int = 1
    ): WeatherResponse

    
}