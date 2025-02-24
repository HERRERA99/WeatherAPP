package com.aitor.weatherapp.weather.domain

import com.aitor.weatherapp.weather.core.response.LocationData
import com.aitor.weatherapp.weather.core.response.WeatherResponse
import com.aitor.weatherapp.weather.data.WeatherRepository
import com.aitor.weatherapp.weather.ui.model.CurrentWeather
import com.aitor.weatherapp.weather.ui.model.DaylyWeather
import com.aitor.weatherapp.weather.ui.model.HourlyWeather
import com.aitor.weatherapp.weather.ui.model.WeatherModel
import javax.inject.Inject

class WeatherUseCase @Inject constructor(private val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(location: LocationData, timezone: String): WeatherModel {
        val weatherResponse = weatherRepository.getWeather(location = location, timezone = timezone)
        return WeatherModel(
            latitude = weatherResponse.latitude,
            longitude = weatherResponse.longitude,
            currentWeather = CurrentWeather(
                time = weatherResponse.current.time,
                temperature2m = weatherResponse.current.temperature2m,
                apparentTemperature = weatherResponse.current.apparentTemperature,
                weatherCode = weatherResponse.current.weatherCode,
                windSpeed10m = weatherResponse.current.windSpeed10m,
                temperature2mMax = weatherResponse.daily.temperature2mMax[1],
                temperature2mMin = weatherResponse.daily.temperature2mMin[1],
                uvIndexMax = weatherResponse.daily.uvIndexMax[1],
                precipitationProbabilityMax = weatherResponse.daily.precipitationProbabilityMax[1],
            ),
            dailyWeather = DaylyWeather(
                time = weatherResponse.daily.time,
                weatherCode = weatherResponse.daily.weatherCode,
                temperature2mMax = weatherResponse.daily.temperature2mMax,
                temperature2mMin = weatherResponse.daily.temperature2mMin,
                uvIndexMax = weatherResponse.daily.uvIndexMax,
                precipitationProbabilityMax = weatherResponse.daily.precipitationProbabilityMax
            ),
            hourlyWeather = HourlyWeather(
                time = weatherResponse.hourly.time,
                temperature2m = weatherResponse.hourly.temperature2m,
                precipitationProbability = weatherResponse.hourly.precipitationProbability,
                weatherCode = weatherResponse.hourly.weatherCode
            ),
        )
    }
}