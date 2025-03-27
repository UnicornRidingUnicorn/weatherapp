# Weather Information App

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Overview

The Weather Information App is a JavaFX application that allows users to retrieve current weather conditions and forecasts for a specified location. It utilizes the OpenWeatherMap API to fetch weather data and displays it in a user-friendly interface.

## Features

- **Current Weather:** Displays the current temperature, conditions, humidity, and wind speed for a given city or ZIP code.
- **Forecast:** Shows a list of upcoming weather forecasts.
- **Unit Conversion:** Allows users to switch between Fahrenheit and Celsius.
- **Search History:** Keeps track of the last 10 searched locations.
- **Dynamic Background:** Changes the background image based on the time of day (sunrise, sunset, night).
- **Error Handling:** Displays error messages if the weather data cannot be retrieved.
- **Clear UI:** The UI is designed to be easy to read and navigate.
- **Static Results Box:** The results box has a static size and a scroll bar if needed.

## Technologies Used

- **Java:** The core programming language.
- **JavaFX:** For creating the graphical user interface.
- **Maven:** For project management and dependency handling.
- **OpenWeatherMap API:** For fetching weather data.
- **Apache HttpClient 5:** For making HTTP requests to the OpenWeatherMap API.
- **JSON:** For parsing the JSON responses from the API.
- **Gson:** For parsing JSON responses.
- **SLF4J/Logback (Optional):** For logging (Logback is an optional dependency).

## Prerequisites

- **Java Development Kit (JDK):** Java 17 or later is recommended.
- **Maven:** Make sure Maven is installed and configured.
- **OpenWeatherMap API Key:** You'll need a free API key from [OpenWeatherMap](https://openweathermap.org/) to use the application.

## Setup and Installation

1.  **Clone the Repository:**

    ```bash
    git clone '[https://github.com/UnicornRidingUnicorn/weatherapp]'
    cd weatherapp
    ```

2.  **OpenWeatherMap API Key:**

    - Create a free account at OpenWeatherMap.
    - Obtain your API key from your account dashboard.
    - Open the `WeatherService.java` file located at `src/main/java/com/example/weatherapp/WeatherService.java`.
    - Replace `"e7ac83aaf45de1f6d7457e7b7bd0cff9"` with your actual API key:
      ```java
      private static final String API_KEY = "e7ac83aaf45de1f6d7457e7b7bd0cff9"; // Replace with your actual API key
      ```

3.  **Compile and Run:**
    ```bash
    mvn clean compile
    mvn javafx:run
    ```

## Usage

1.  **Enter Location:** In the "Search" box, type the name of a city or a ZIP code.
2.  **Click Search:** Click the "Search" button to retrieve the weather data.
3.  **View Results:** The current weather information will be displayed in the "Results" box.
4.  **View Forecast:** The upcoming forecast will be displayed below the current weather.
5.  **Change Units:** Use the dropdown menu to switch between Fahrenheit and Celsius.
6.  **View History:** The last 10 searched locations will be displayed in the "History" box.
7.  **Quit:** Click the "Quit" button to exit the application.

## Project Structure

weatherapp/ 
    ├── pom.xml # Maven project configuration 
    ├── README.md # This file (project description) 
    ├── LICENSE.md # MIT License 
        └── src/ 
            └── main/ 
                ├── java/ 
                    │ └── com/ 
                        │ └── example/ 
                            │ └── weatherapp/ 
                            │ ├── ForecastData.java # Data class for forecast information 
                            │ ├── WeatherApp.java # Main application class 
                            │ ├── WeatherData.java # Data class for current weather 
                            │ └── WeatherService.java # Handles API requests 
                    └── resources/ 
                        ├── default_background.png # Default background image 
                        ├── night.png # Night background image 
                        ├── sunrise.png # Sunrise background image 
                        ├── sunset.png # Sunset background image 
                        └── styles.css # CSS for styling the UI

## License

This project is licensed under the MIT License - see the LICENSE.md file for details.

## Contributing

Contributions are welcome! If you'd like to contribute, please follow these steps:

## Fork the repository.

Create a new branch for your feature or bug fix.
Make your changes and commit them.
Push your branch to your fork.
Submit a pull request.
Contact
If you have any questions or suggestions, feel free to contact me at [rusty.lockett@gmail.com].

## Acknowledgments

OpenWeatherMap for providing the weather data API.
The JavaFX community for creating a powerful UI framework.
The Apache Software Foundation for the HttpClient library.
The Gson and JSON libraries.
The SLF4J and Logback libraries.

## `styles.css`

```css
.root {
    -fx-background-color: black; /* Set the background color of the root */
}

.weather-label {
    -fx-font-size: 16px;
    -fx-text-fill: white;
}

.weather-info-vbox {
    -fx-padding: 10px;
    -fx-spacing: 10px;
    -fx-alignment: center;
    -fx-background-color: rgba(0, 0, 0, 0.5); /* Add a semi-transparent background */
    -fx-background-radius: 10px; /* Add rounded corners */
}

.location-input {
    -fx-text-fill: black; /* ensures search box text is visible */
}

.unit-combo-box{
    -fx-text-fill: black;
}

.forecast-label{
    -fx-text-fill: black; /* change from white to black for results box */
}

.location-label {
    -fx-text-fill: white; /* switch to white if background is black */
    -fx-font-size: 16px;
}

## 'pom.xml'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>weatherapp</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging> <!-- Make sure packaging is JAR -->

    <properties>
        <maven.compiler.source>24</maven.compiler.source>
        <maven.compiler.target>24</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <javafx.version>24</javafx.version>
    </properties>

    <dependencies>
        <!-- JavaFX Dependencies -->
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-controls</artifactId>
            <version>${javafx.version}</version>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-fxml</artifactId>
            <version>${javafx.version}</version>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-graphics</artifactId>
            <version>${javafx.version}</version>
            <classifier>win</classifier>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-graphics</artifactId>
            <version>${javafx.version}</version>
            <classifier>linux</classifier>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-graphics</artifactId>
            <version>${javafx.version}</version>
            <classifier>mac</classifier>
        </dependency>

        <!-- Apache HttpClient 5 -->
        <dependency>
            <groupId>org.apache.httpcomponents.client5</groupId>
            <artifactId>httpclient5</artifactId>
            <version>5.2.1</version>
        </dependency>

        <!-- JSON -->
        <dependency>
            <groupId>org.json</groupId>
            <artifactId>json</artifactId>
            <version>20231013</version>
        </dependency>

        <!-- Gson -->
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.10.1</version>
        </dependency>

        <!-- SLF4J Logback (Optional) -->
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.4.14</version>
            <optional>true</optional>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>24</source>
                    <target>24</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.openjfx</groupId>
                <artifactId>javafx-maven-plugin</artifactId>
                <version>0.0.8</version>
                <configuration>
                    <mainClass>com.example.weatherapp.WeatherApp</mainClass>
                    <vmArgs>
                        <vmArg>--enable-native-access=javafx.graphics</vmArg>
                    </vmArgs>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```
