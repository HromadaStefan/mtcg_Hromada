package at.fhtw.sampleapp.service.weather;

import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import at.fhtw.sampleapp.model.Weather;

import java.util.ArrayList;
import java.util.List;

public class WeatherDAL{
    private List<Weather> weatherData;

    public WeatherDAL() {
        weatherData = new ArrayList<>();
        weatherData.add(new Weather(1,"Vienna", 9.f));
        weatherData.add(new Weather(2,"Berlin", 8.f));
        weatherData.add(new Weather(3,"Tokyo", 12.f));
    }

    // GET /weather/:id
    public Weather getWeather(Integer ID) {
        Weather foundWaether = weatherData.stream()
                .filter(waether -> ID == waether.getId())
                .findAny()
                .orElse(null);

        return foundWaether;
    }

    // GET /weather
    public List<Weather> getWeather() {
        return weatherData;
    }

    // POST /weather
    public void addWeather(Weather weather) {
        weatherData.add(weather);
    }
}
