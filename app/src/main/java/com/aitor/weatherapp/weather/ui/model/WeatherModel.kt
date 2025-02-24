package com.aitor.weatherapp.weather.ui.model

data class WeatherModel(
    val latitude: Double,
    val longitude: Double,
    val currentWeather: CurrentWeather,
    val dailyWeather: DaylyWeather,
    val hourlyWeather: HourlyWeather
)

data class CurrentWeather(
    val time: String,
    val temperature2m: Double,
    val apparentTemperature: Double,
    val weatherCode: Int,
    val windSpeed10m: Double,
    val temperature2mMax: Double,
    val temperature2mMin: Double,
    val uvIndexMax: Double,
    val precipitationProbabilityMax: Int
)

data class DaylyWeather(
    val time: List<String>,
    val weatherCode: List<Int>,
    val temperature2mMax: List<Double>,
    val temperature2mMin: List<Double>,
    val uvIndexMax: List<Double>,
    val precipitationProbabilityMax: List<Int>
)

data class HourlyWeather(
    val time: List<String>,
    val temperature2m: List<Double>,
    val precipitationProbability: List<Int>,
    val weatherCode: List<Int>
)
