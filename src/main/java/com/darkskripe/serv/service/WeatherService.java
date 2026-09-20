package com.darkskripe.serv.service;

import com.darkskripe.serv.controller.WeatherDto;
import com.darkskripe.serv.dao.LocationEntity;
import com.darkskripe.serv.dao.WeatherEntity;
import com.darkskripe.serv.exception.ExternalServiceException;
import com.darkskripe.serv.repository.LocationRepository;
import com.darkskripe.serv.repository.WeatherRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class WeatherService {
    RestTemplate restTemplate;
    WeatherRepository wRepository;
    LocationRepository lRepository;
    WeatherDtoMapper mapper;

    @Value("${weather.api.url}")
    String url;

    @Value("${weather.api.key}")
    String apiKey;

    @Value("${weather.api.maxDays}")
    Integer maxDays;

    public WeatherService(RestTemplate restTemplate, WeatherRepository wRepository, LocationRepository lRepository, WeatherDtoMapper mapper) {
        this.restTemplate = restTemplate;
        this.wRepository = wRepository;
        this.lRepository = lRepository;
        this.mapper = mapper;
    }

    public WeatherDto getForecast(String city, Integer days) {
        if (days>maxDays) days=maxDays;
        URI uri= UriComponentsBuilder.fromUriString(url)
                .queryParam("key",apiKey)
                .queryParam("q",city)
                .queryParam("days",days.toString())
                .queryParam("aqi","no")
                .build().toUri();

        return mappAndSave(uri);
    }

    public WeatherDto getForecast(Double lat,Double lon, Integer days) {
        if (days>maxDays) days=maxDays;
        URI uri= UriComponentsBuilder.fromUriString(url)
                .queryParam("key",apiKey)
                .queryParam("q",lat+","+lon)
                .queryParam("days",days.toString())
                .queryParam("aqi","no")
                .build().toUri();

        return mappAndSave(uri);
    }

    @Transactional
    public WeatherDto mappAndSave(URI uri) {
        WeatherDto weatherDto = restTemplate.getForObject(uri, WeatherDto.class);
        if (weatherDto==null)throw new ExternalServiceException("WeatherDto is null");

        LocationEntity mapped = mapper.locationToEntity(weatherDto);
        mapped.valid();
        LocationEntity lEntity = lRepository.findLocationEntityByLatAndLon(mapped.getLat(),mapped.getLon())
                .orElseGet(()->lRepository.save(mapped));


        List<WeatherEntity> mappedList = mapper.weatherToEntity(weatherDto,lEntity);
        List<WeatherEntity> weatherEntityList = mappedList.stream().map(entity ->
                wRepository.findWeatherEntityByTargetDayAndLocation(entity.getTargetDay(),entity.getLocation())
                        .orElseGet(()->wRepository.save(entity))
        ).toList();

        return mapper.entityToDto(weatherEntityList,lEntity);
    }
}
