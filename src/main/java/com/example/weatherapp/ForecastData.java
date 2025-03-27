package com.example.weatherapp;

public class ForecastData {
    private final String time;
    private final double temperature;
    private final String conditions;


    public ForecastData(String time, double temperature, String conditions) {
        this.time = time;
        this.temperature = temperature;
        this.conditions = conditions;
    }

    public String getTime() {
        return time;
    }

    public double getTemperature() {
        return temperature;
    }
    public double getTemperatureFahrenheit() {
        return (temperature - 273.15) * 9.0 / 5.0 + 32.0;
    }
    public double getTemperatureCelsius() {
        return temperature - 273.15;
    }

    public String getConditions() {
        return conditions;
    }
}