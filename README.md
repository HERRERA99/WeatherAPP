# Weather App

Este es un proyecto de aplicación móvil para Android desarrollado en Kotlin con Jetpack Compose. La aplicación muestra información meteorológica basada en la ubicación del usuario, utilizando la API de Open-Meteo.

## Características

- Obtiene la ubicación actual del usuario.
- Consulta la API de Open-Meteo para obtener el clima en la ubicación actual.
- Muestra información como temperatura, estado del tiempo y humedad.
- Representa visualmente el clima con imágenes según el código WMO devuelto por la API.

## Tecnologías utilizadas

- **Kotlin**
- **Jetpack Compose**
- **Retrofit** (para realizar las solicitudes HTTP a la API de Open-Meteo)
- **Coil** (para la carga de imágenes)
- **Fused Location Provider API** (para obtener la ubicación del usuario)

## Instalación y ejecución

1. Clona este repositorio:
   ```sh
   git clone https://github.com/tu-usuario/weather-app.git
   ```
2. Abre el proyecto en Android Studio.
3. Agrega tu clave de API si es necesaria (para Open-Meteo no es obligatoria).
4. Ejecuta la aplicación en un emulador o dispositivo físico.

## Uso de la API de Open-Meteo

La aplicación realiza una petición a la API de Open-Meteo para obtener datos del clima. Ejemplo de URL de solicitud:

```sh
https://api.open-meteo.com/v1/forecast?latitude=40.4168&longitude=-3.7038&current_weather=true
```

La respuesta incluye datos como:

```json
{
  "latitude": 40.4168,
  "longitude": -3.7038,
  "current_weather": {
    "temperature": 25.3,
    "windspeed": 10.5,
    "weathercode": 0
  }
}
```

## Mapeo de Códigos WMO a Imágenes

La aplicación usa un `when` en Kotlin para asignar imágenes según el código WMO:

```kotlin
val imageRes = when (wmoCode) {
    0 -> R.drawable.sunny
    in 1..3 -> R.drawable.partly_cloudy
    in 45..48 -> R.drawable.fog
    in 51..67 -> R.drawable.rainy
    in 71..77 -> R.drawable.snowy
    in 80..99 -> R.drawable.thunderstorm
    else -> R.drawable.unknown
}
```

## Contribución

Si quieres contribuir al proyecto:

1. Haz un fork del repositorio.
2. Crea una nueva rama con tu mejora:
   ```sh
   git checkout -b mi-mejora
   ```
3. Realiza tus cambios y haz commit:
   ```sh
   git commit -m "Agregada nueva funcionalidad"
   ```
4. Sube los cambios a tu fork:
   ```sh
   git push origin mi-mejora
   ```
5. Abre un Pull Request en este repositorio.

## Licencia

Este proyecto está bajo la licencia MIT. Puedes ver más detalles en el archivo `LICENSE`.

