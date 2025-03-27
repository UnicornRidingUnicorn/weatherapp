package com.example.weatherapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.concurrent.Task;

import java.io.IOException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WeatherApp extends Application {

    private static final Logger LOGGER = Logger.getLogger(WeatherApp.class.getName());

    // Constants for String literals
    private static final String FAHRENHEIT = "Fahrenheit";
    private static final String CELSIUS = "Celsius";
    private static final String ERROR_TITLE = "Error";
    private static final String DEFAULT_BACKGROUND_PATH = "/default_background.png";
    private static final String SUNRISE_IMAGE_PATH = "/sunrise.png";
    private static final String SUNSET_IMAGE_PATH = "/sunset.png";
    private static final String NIGHT_IMAGE_PATH = "/night.png";
    private static final String LOCATION_PROMPT_TEXT = "Enter city/zip";

    // Constants for time ranges (in hours)
    private static final int SUNRISE_START_HOUR = 6;
    private static final int SUNRISE_END_HOUR = 9;
    private static final int SUNSET_START_HOUR = 17;
    private static final int SUNSET_END_HOUR = 20;

    private final WeatherService weatherService = new WeatherService();
    private final ImageView backgroundImageView = new ImageView();
    private final Label temperatureLabel = new Label();
    private final Label conditionsLabel = new Label();
    private final Label humidityLabel = new Label();
    private final Label windSpeedLabel = new Label();
    private final ComboBox<String> unitComboBox = new ComboBox<>();
    private final ListView<String> historyListView = new ListView<>();
    private final ObservableList<String> searchHistory = FXCollections.observableArrayList();
    private final VBox forecastVBox = new VBox(5);
    private final VBox weatherInfoVBox = new VBox(10); // Reverted to a single VBox

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Weather Information App");

        // UI Setup
        BorderPane root = createRootLayout();
        Scene scene = new Scene(new StackPane(backgroundImageView, root), 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);

        // Load default background immediately
        loadDefaultBackground();

        // Update background based on time
        Platform.runLater(this::updateBackgroundImage);

        primaryStage.show();
    }

    private BorderPane createRootLayout() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10)); 

        TitledPane searchPane = new TitledPane("Search", createInputHBox());
        searchPane.setCollapsible(false);

        VBox weatherInfoBox = createWeatherInfoVBox();

        // Fix Results pane to a static width & height
        TitledPane resultsPane = new TitledPane("Results", createForecastScrollPane());
        resultsPane.setCollapsible(false);
        resultsPane.setPrefSize(350, 220);

        TitledPane historyPane = new TitledPane("History", createHistoryListView());
        historyPane.setCollapsible(false);

        // Add extra spacing so History and Results don’t overlap
        HBox bottomBox = new HBox(20, resultsPane, historyPane);

        VBox centerBox = new VBox(15, weatherInfoBox, bottomBox);
        centerBox.setAlignment(Pos.CENTER);

        root.setTop(searchPane);
        root.setCenter(centerBox);
        return root;
    }

    private HBox createInputHBox() {
        Label locationLabel = new Label("City or State:");
        locationLabel.getStyleClass().add("location-label");

        TextField locationInput = new TextField(); 
        locationInput.getStyleClass().add("location-input");
        // Remove the prompt text if you only want the label, e.g.:
        // locationInput.setPromptText(null);

        Button searchButton = new Button("Search");
        searchButton.setOnAction(event -> fetchWeatherData(locationInput.getText()));

        Button quitButton = new Button("Quit");
        quitButton.setOnAction(event -> Platform.exit());

        HBox inputHBox = new HBox(10, locationLabel, locationInput, searchButton, quitButton);
        inputHBox.setAlignment(Pos.CENTER);
        return inputHBox;
    }

    private VBox createWeatherInfoVBox() {
        weatherInfoVBox.setAlignment(Pos.CENTER);
        weatherInfoVBox.getStyleClass().add("weather-info-vbox");
        temperatureLabel.getStyleClass().add("weather-label");
        conditionsLabel.getStyleClass().add("weather-label");
        humidityLabel.getStyleClass().add("weather-label");
        windSpeedLabel.getStyleClass().add("weather-label");
        weatherInfoVBox.getChildren().addAll(temperatureLabel, conditionsLabel, humidityLabel, windSpeedLabel);

        unitComboBox.getItems().addAll(FAHRENHEIT, CELSIUS);
        unitComboBox.setValue(FAHRENHEIT);
        unitComboBox.getStyleClass().add("unit-combo-box");
        unitComboBox.setOnAction(event -> updateDisplayedUnits());
        weatherInfoVBox.getChildren().add(unitComboBox);

        VBox.setVgrow(weatherInfoVBox, Priority.ALWAYS);
        return weatherInfoVBox;
    }

    private ScrollPane createForecastScrollPane() {
        forecastVBox.setAlignment(Pos.CENTER_LEFT);
        forecastVBox.setPrefHeight(200); // Fixed height for scroll
        ScrollPane forecastScrollPane = new ScrollPane(forecastVBox);
        forecastScrollPane.setFitToWidth(true);
        return forecastScrollPane;
    }

    private ListView<String> createHistoryListView() {
        historyListView.setItems(searchHistory);
        historyListView.setPrefWidth(200);
        historyListView.setPrefHeight(200); // Fixed height for scroll
        historyListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String selectedLocation = newVal.split(" \\(")[0];
                fetchWeatherData(selectedLocation);
            }
        });
        return historyListView;
    }

    private void fetchWeatherData(String location) {
        if (location == null || location.trim().isEmpty()) {
            showAlert(ERROR_TITLE, "Please enter a location.");
            return;
        }

        // Current Weather Task
        Task<WeatherData> currentWeatherTask = new Task<>() {
            @Override
            protected WeatherData call() throws Exception {
                return weatherService.getCurrentWeather(location);
            }
        };

        currentWeatherTask.setOnSucceeded(event -> {
            WeatherData data = currentWeatherTask.getValue();
            if (data != null) {
                updateWeatherDisplay(data);
                addToHistory(location);
                updateBackgroundImage();
            } else {
                showAlert(ERROR_TITLE, "Could not retrieve weather data. Check your input and try again.");
            }
        });

        currentWeatherTask.setOnFailed(event -> handleTaskFailure("current weather", currentWeatherTask.getException()));

        // Forecast Task
        Task<List<ForecastData>> forecastTask = new Task<>() {
            @Override
            protected List<ForecastData> call() throws Exception {
                return weatherService.getForecast(location);
            }
        };

        forecastTask.setOnSucceeded(event -> {
            List<ForecastData> forecast = forecastTask.getValue();
            if (forecast != null && !forecast.isEmpty()) {
                updateForecastDisplay(forecast);
            } else {
                showAlert(ERROR_TITLE, "Could not retrieve forecast data.");
            }
        });

        forecastTask.setOnFailed(event -> handleTaskFailure("forecast", forecastTask.getException()));

        // Start tasks
        new Thread(currentWeatherTask).start();
        new Thread(forecastTask).start();
    }

    private void handleTaskFailure(String taskName, Throwable exception) {
        LOGGER.log(Level.SEVERE, exception, () -> "Error in " + taskName + " task");
        showAlert(ERROR_TITLE, "Failed to fetch " + taskName + ": " + getErrorMessage(exception));
    }

    private void addToHistory(String location) {
        ZonedDateTime now = ZonedDateTime.now();
        String formattedDateTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String historyEntry = location + " (" + formattedDateTime + ")";

        Platform.runLater(() -> {
            if (!searchHistory.contains(historyEntry)) {
                searchHistory.add(0, historyEntry);
                if (searchHistory.size() > 10) {
                    searchHistory.remove(searchHistory.size() - 1);
                }
            }
        });
    }

    private void updateWeatherDisplay(WeatherData data) {
        String iconUrl = "https://openweathermap.org/img/wn/" + data.getIconCode() + "@2x.png";
        Image newIcon = new Image(iconUrl, true);
        ImageView newIconView = new ImageView(newIcon);
        newIconView.setFitHeight(50);
        newIconView.setFitWidth(50);

        Platform.runLater(() -> {
            // Remove any existing icon
            weatherInfoVBox.getChildren().removeIf(node -> node instanceof ImageView);

            // Insert the updated icon at the top
            weatherInfoVBox.getChildren().add(0, newIconView);

            // Update labels
            if (FAHRENHEIT.equals(unitComboBox.getValue())) {
                temperatureLabel.setText(String.format("Temperature: %.1f °F", data.getTemperatureFahrenheit()));
                windSpeedLabel.setText(String.format("Wind Speed: %.1f mph", data.getWindSpeedMph()));
            } else {
                temperatureLabel.setText(String.format("Temperature: %.1f °C", data.getTemperatureCelsius()));
                windSpeedLabel.setText(String.format("Wind Speed: %.1f kph", data.getWindSpeedKph()));
            }
            conditionsLabel.setText("Conditions: " + data.getConditions());
            humidityLabel.setText("Humidity: " + data.getHumidity() + "%");
        });
    }

    private void updateForecastDisplay(List<ForecastData> forecast) {
        Platform.runLater(() -> {
            forecastVBox.getChildren().clear();
            for (ForecastData data : forecast) {
                Label timeLabel = new Label(data.getTime());
                timeLabel.getStyleClass().add("forecast-label");
                Label tempLabel;
                if (FAHRENHEIT.equals(unitComboBox.getValue())) {
                    tempLabel = new Label(String.format("%.1f °F", data.getTemperatureFahrenheit()));
                } else {
                    tempLabel = new Label(String.format("%.1f °C", data.getTemperatureCelsius()));
                }
                tempLabel.getStyleClass().add("forecast-label");
                Label condLabel = new Label(data.getConditions());
                condLabel.getStyleClass().add("forecast-label");

                HBox forecastItem = new HBox(10, timeLabel, tempLabel, condLabel);
                forecastItem.setAlignment(Pos.CENTER_LEFT);
                forecastVBox.getChildren().add(forecastItem);
            }
        });
    }

    private void updateDisplayedUnits() {
        String selectedLocation = null;
        if (!searchHistory.isEmpty()) {
            selectedLocation = searchHistory.get(0).split(" \\(")[0];
        }

        if (selectedLocation != null) {
            fetchWeatherData(selectedLocation);
        }
    }

    private void updateBackgroundImage() {
        ZonedDateTime now = WeatherService.getCurrentTimeInNewYork();
        int hour = now.getHour();
        String imagePath;

        if (hour >= SUNRISE_START_HOUR && hour < SUNRISE_END_HOUR) {
            imagePath = SUNRISE_IMAGE_PATH;
        } else if (hour >= SUNSET_START_HOUR && hour < SUNSET_END_HOUR) {
            imagePath = SUNSET_IMAGE_PATH;
        } else {
            imagePath = NIGHT_IMAGE_PATH;
        }

        loadImage(imagePath);
    }

    private void loadImage(String imagePath) {
        try {
            URL resourceUrl = getClass().getResource(imagePath);
            if (resourceUrl != null) {
                Image image = new Image(resourceUrl.toExternalForm());
                backgroundImageView.setImage(image);
            } else {
                LOGGER.severe(() -> String.format("Could not find image: %s", imagePath));
                loadDefaultBackground();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e, () -> String.format("Error loading background image: %s", imagePath));
            loadDefaultBackground();
        }
    }

    private void loadDefaultBackground() {
        loadImage(DEFAULT_BACKGROUND_PATH);
    }

    private String getErrorMessage(Throwable t) {
        if (t instanceof IOException) {
            return "Network error. Please check your internet connection.";
        } else if (t instanceof InterruptedException) {
            return "Operation was interrupted.";
        } else {
            return "An unexpected error occurred: " + t.getMessage();
        }
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
