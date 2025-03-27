package com.example.weatherapp;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * WeatherService handles the communication with the OpenWeatherMap API to
 * fetch current weather and forecast data.
 */
public class WeatherService {

    private static final Logger LOGGER = Logger.getLogger(WeatherService.class.getName());

    /**
     * The API key for accessing the OpenWeatherMap API. Replace with your
     * actual API key.
     */
    private static final String API_KEY = "e7ac83aaf45de1f6d7457e7b7bd0cff9";

    /**
     * The base URL for fetching current weather data.
     */
    private static final String CURRENT_WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?q={location}&appid={apiKey}";

    /**
     * The base URL for fetching forecast data.
     */
    private static final String FORECAST_URL = "http://api.openweathermap.org/data/2.5/forecast?q={location}&appid={apiKey}";

    /**
     * The time zone ID for New York.
     */
    private static final String NEW_YORK_TIME_ZONE = "America/New_York";

    /**
     * Gets the current time in New York.
     *
     * @return The current time in New York.
     */
    public static ZonedDateTime getCurrentTimeInNewYork() {
        return ZonedDateTime.now(ZoneId.of(NEW_YORK_TIME_ZONE));
    }

    /**
     * Retrieves the current weather data for the specified location.
     *
     * @param location The location (city name or ZIP code) to fetch weather
     *                 data for.
     * @return A WeatherData object containing the current weather information,
     *         or null if the location is not found.
     * @throws IOException If there is an error during the HTTP request or
     *                     processing the response.
     */
    public WeatherData getCurrentWeather(String location) throws IOException {
        // URL-encode the location
        String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8);
        String url = CURRENT_WEATHER_URL.replace("{location}", encodedLocation).replace("{apiKey}", API_KEY);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            return httpClient.execute(httpGet, response -> {
                int statusCode = response.getCode();
                if (statusCode == 404) {
                    LOGGER.warning("Location not found: " + location);
                    return null; // Return null for 404
                } else if (statusCode == 401) {
                    LOGGER.severe("Failed to fetch current weather.  HTTP Status Code: " + statusCode
                            + " Check your API key.");
                    throw new IOException("Failed to fetch current weather. HTTP Status Code: " + statusCode
                            + " Check your API key.");
                } else if (statusCode != 200) {
                    LOGGER.severe("Failed to fetch current weather.  HTTP Status Code: " + statusCode);
                    throw new IOException("Failed to fetch current weather. HTTP Status Code: " + statusCode);
                }

                String jsonResponse = EntityUtils.toString(response.getEntity());
                JSONObject json = new JSONObject(jsonResponse);

                // Handle "cod" as an integer
                int cod = json.optInt("cod", 0);
                if (cod != 200) {
                    LOGGER.warning("API returned an error: " + json.optString("message", "No message"));
                    return null;
                }

                // Extract data correctly
                double temperatureKelvin = json.getJSONObject("main").getDouble("temp");
                String conditions = json.getJSONArray("weather").getJSONObject(0).getString("description");
                String iconCode = json.getJSONArray("weather").getJSONObject(0).getString("icon");
                int humidity = json.getJSONObject("main").getInt("humidity");
                double windSpeedMps = json.getJSONObject("wind").getDouble("speed");

                return new WeatherData(temperatureKelvin, conditions, iconCode, humidity, windSpeedMps);
            });
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error during getCurrentWeather: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Retrieves the weather forecast for the specified location.
     *
     * @param location The location (city name or ZIP code) to fetch the
     *                 forecast for.
     * @return A list of ForecastData objects containing the forecast
     *         information, or null if the location is not found.
     * @throws IOException If there is an error during the HTTP request or
     *                     processing the response.
     */
    public List<ForecastData> getForecast(String location) throws IOException {
        // URL-encode the location
        String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8);
        String url = FORECAST_URL.replace("{location}", encodedLocation).replace("{apiKey}", API_KEY);
        List<ForecastData> forecastList = new ArrayList<>();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            return httpClient.execute(httpGet, response -> {
                int statusCode = response.getCode();
                if (statusCode == 404) {
                    LOGGER.warning("Location not found for forecast: " + location);
                    return null; // Return null for 404.
                } else if (statusCode == 401) {
                    LOGGER.severe("Failed to fetch forecast. HTTP Status Code: " + statusCode
                            + " Check your API key.");
                    throw new IOException("Failed to fetch forecast. HTTP Status Code: " + statusCode
                            + " Check your API key.");
                } else if (statusCode != 200) {
                    LOGGER.severe("Failed to fetch forecast. HTTP Status Code: " + statusCode);
                    throw new IOException("Failed to fetch forecast. HTTP Status Code: " + statusCode);
                }

                String jsonResponse = EntityUtils.toString(response.getEntity());
                // Use JSONTokener to handle the response as a stream
                JSONTokener tokener = new JSONTokener(jsonResponse);
                JSONObject json = new JSONObject(tokener);

                // Handle "cod" as an integer
                int cod = json.optInt("cod", 0);
                if (cod != 200) {
                    LOGGER.warning("API returned an error: " + json.optString("message", "No message"));
                    return null;
                }

                JSONArray list = json.getJSONArray("list");
                for (int i = 0; i < list.length(); i++) {
                    JSONObject forecastItem = list.getJSONObject(i);
                    String time = forecastItem.getString("dt_txt");
                    double temperatureKelvin = forecastItem.getJSONObject("main").getDouble("temp");
                    String conditions = forecastItem.getJSONArray("weather").getJSONObject(0).getString("description");

                    forecastList.add(new ForecastData(time, temperatureKelvin, conditions));
                }
                return forecastList;
            });
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error during getForecast: " + e.getMessage(), e);
            throw e;
        }
    }
}
