package com.example.weatherapp;

/**
 * WeatherData represents the current weather conditions for a location.
 * It stores temperature, conditions, icon code, humidity, and wind speed.
 */
public class WeatherData {
    private final double temperature;
    private final String conditions;
    private final String iconCode;
    private final int humidity;
    private final double windSpeed;

    /**
     * Constructs a WeatherData object with the specified parameters.
     *
     * @param temperature The temperature in Kelvin.
     * @param conditions  The weather conditions (e.g., "clear sky", "rain").
     * @param iconCode    The icon code for the weather conditions.
     * @param humidity    The humidity percentage.
     * @param windSpeed   The wind speed in meters per second.
     */
    public WeatherData(double temperature, String conditions, String iconCode, int humidity, double windSpeed) {
        this.temperature = temperature;
        this.conditions = conditions;
        this.iconCode = iconCode;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
    }

    /**
     * Gets the temperature in Kelvin.
     *
     * @return The temperature in Kelvin.
     */
    public double getTemperature() {
        return temperature;
    }

    /**
     * Converts the temperature from Kelvin to Fahrenheit.
     *
     * @return The temperature in Fahrenheit.
     */
    public double getTemperatureFahrenheit() {
        return (temperature - 273.15) * 9.0 / 5.0 + 32.0;
    }

    /**
     * Converts the temperature from Kelvin to Celsius.
     *
     * @return The temperature in Celsius.
     */
    public double getTemperatureCelsius() {
        return temperature - 273.15;
    }

    /**
     * Gets the weather conditions.
     *
     * @return The weather conditions.
     */
    public String getConditions() {
        return conditions;
    }

    /**
     * Gets the icon code for the weather conditions.
     *
     * @return The icon code.
     */
    public String getIconCode() {
        return iconCode;
    }

    /**
     * Gets the humidity percentage.
     *
     * @return The humidity percentage.
     */
    public int getHumidity() {
        return humidity;
    }

    /**
     * Converts the wind speed from meters per second to miles per hour (mph).
     *
     * @return The wind speed in mph.
     */
    public double getWindSpeedMph() {
        return windSpeed * 2.23694;
    }

    /**
     * Converts the wind speed from meters per second to kilometers per hour (kph).
     *
     * @return The wind speed in kph.
     */
    public double getWindSpeedKph() {
        return windSpeed * 3.6;
    }
}
