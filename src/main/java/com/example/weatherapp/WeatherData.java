package com.example.weatherapp;

public class WeatherData {
    private final double temperature;
    private final String conditions;
    private final String iconCode;
    private final int humidity;
    private final double windSpeed;

    public WeatherData(double temperature, String conditions, String iconCode, int humidity, double windSpeed) {
        this.temperature = temperature;
        this.conditions = conditions;
        this.iconCode = iconCode;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
    }

    public double getTemperature() {
        return temperature;
    }

    // Convert temperature from Kelvin to Fahrenheit
    public double getTemperatureFahrenheit() {
        return (temperature - 273.15) * 9.0 / 5.0 + 32.0;
    }

    // Convert temperature from Kelvin to Celsius
    public double getTemperatureCelsius() {
        return temperature - 273.15;
    }

    public String getConditions() {
        return conditions;
    }

    public String getIconCode() {
        return iconCode;
    }

    public int getHumidity() {
        return humidity;
    }

    // Convert wind speed from meters/second to mph
    public double getWindSpeedMph() {
        return windSpeed * 2.23694;
    }

    // Convert wind speed from meters/second to kph
    public double getWindSpeedKph() {
        return windSpeed * 3.6;
    }
}