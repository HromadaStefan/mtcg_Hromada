package at.fhtw.sampleapp.service.weather;

import at.fhtw.sampleapp.controller.Controller;
import at.fhtw.sampleapp.model.Weather;
import com.fasterxml.jackson.core.JsonProcessingException;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;

import java.util.List;

public class WeatherController extends Controller {
    private WeatherDAL weatherDAL;

    public WeatherController(WeatherDAL weatherDAL) {
        this.weatherDAL = weatherDAL;
    }

    // GET /weather(:id
    public Response getWeather(String id)
    {
        try {
            Weather weatherData = this.weatherDAL.getWeather(Integer.parseInt(id));
            // "[ { \"id\": 1, \"city\": \"Vienna\", \"temperature\": 9.0 }, { ... }, { ... } ]"
            String weatherDataJSON = this.getObjectMapper().writeValueAsString(weatherData);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    weatherDataJSON
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }
    // GET /weather
    public Response getWeather() {
        try {
            List weatherData = this.weatherDAL.getWeather();
            // "[ { \"id\": 1, \"city\": \"Vienna\", \"temperature\": 9.0 }, { ... }, { ... } ]"
            String weatherDataJSON = this.getObjectMapper().writeValueAsString(weatherData);

            return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                weatherDataJSON
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }

    // POST /weather
    public Response addWeather(Request request) {
        try {

            // request.getBody() => "{ \"id\": 4, \"city\": \"Graz\", ... }
            Weather weather = this.getObjectMapper().readValue(request.getBody(), Weather.class);
            this.weatherDAL.addWeather(weather);

            return new Response(
                HttpStatus.CREATED,
                ContentType.JSON,
                "{ message: \"Success\" }"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new Response(
            HttpStatus.INTERNAL_SERVER_ERROR,
            ContentType.JSON,
            "{ \"message\" : \"Internal Server Error\" }"
        );
    }
}
