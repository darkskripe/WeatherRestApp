package com.darkskripe.serv.controller;


import com.darkskripe.serv.service.WeatherService;
import com.darkskripe.serv.requestWeather.WeatherRequestDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/weather")
public class WeatherController {
    private WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public ResponseEntity<WeatherDto> getForecastWeather(@Valid @ModelAttribute WeatherRequestDto request) {
        if (request.city()==null||request.city().isBlank()) {
            log.debug("getWeather(lat={},long={},days={})", request.lat(),request.lon(), request.days());
            return ResponseEntity.ok().body(weatherService.getForecast(request.lat(),request.lon(), request.days()));
        }
        log.debug("getWeather(city={},days={})", request.city(), request.days());
        return ResponseEntity.ok().body(weatherService.getForecast(request.city(),request.days()));
    }

}
