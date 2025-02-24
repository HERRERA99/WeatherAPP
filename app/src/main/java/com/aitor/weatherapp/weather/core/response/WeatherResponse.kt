package com.aitor.weatherapp.weather.core.response

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("current") val current: CurrentWeather,
    @SerializedName("daily") val daily: DailyWeather,
    @SerializedName("hourly") val hourly: HourlyWeather
)

data class CurrentWeather(
    @SerializedName("time") val time: String,
    @SerializedName("temperature_2m") val temperature2m: Double,
    @SerializedName("apparent_temperature") val apparentTemperature: Double,
    @SerializedName("weather_code") val weatherCode: Int,
    @SerializedName("wind_speed_10m") val windSpeed10m: Double
)

data class DailyWeather(
    @SerializedName("time") val time: List<String>,
    @SerializedName("weather_code") val weatherCode: List<Int>,
    @SerializedName("temperature_2m_max") val temperature2mMax: List<Double>,
    @SerializedName("temperature_2m_min") val temperature2mMin: List<Double>,
    @SerializedName("uv_index_max") val uvIndexMax: List<Double>,
    @SerializedName("precipitation_probability_max") val precipitationProbabilityMax: List<Int>
)

data class HourlyWeather(
    @SerializedName("time") val time: List<String>,
    @SerializedName("temperature_2m") val temperature2m: List<Double>,
    @SerializedName("precipitation_probability") val precipitationProbability: List<Int>,
    @SerializedName("weather_code") val weatherCode: List<Int>
)

data class LocationData(
    val latitude: Double,
    val longitude: Double
)

