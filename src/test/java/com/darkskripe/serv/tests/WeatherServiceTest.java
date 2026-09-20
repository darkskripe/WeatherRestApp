package com.darkskripe.serv.tests;

/*
 * Purpose:
 * - Unit tests for WeatherService.mappAndSave using Mockito to mock external
 *   dependencies (RestTemplate, repositories, mapper).
 * - This verifies the happy-path orchestration without requiring a running DB
 *   or external HTTP calls.
 *
 * Why this test:
 * - Services coordinate calls to other layers; unit tests ensure correct
 *   calls happen and the final DTO is returned as expected.
 */

import com.darkskripe.serv.controller.WeatherDto;
import com.darkskripe.serv.dao.LocationEntity;
import com.darkskripe.serv.dao.WeatherEntity;
import com.darkskripe.serv.exception.ExternalServiceException;
import com.darkskripe.serv.repository.LocationRepository;
import com.darkskripe.serv.repository.WeatherRepository;
import com.darkskripe.serv.service.WeatherDtoMapper;
import com.darkskripe.serv.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTest {

    @Mock
    RestTemplate restTemplate;

    @Mock
    WeatherRepository wRepository;

    @Mock
    LocationRepository lRepository;

    @Mock
    WeatherDtoMapper mapper;

    @InjectMocks
    WeatherService weatherService;

    @Test
    void mappAndSave_returns_mappedDto_on_success() throws Exception {
        URI uri = new URI("http://example.com/test");

        // Prepare DTO returned by RestTemplate
        WeatherDto.Location loc = new WeatherDto.Location(null, "Bucuresti", 44.43f, 26.1f);
        WeatherDto.Forecast.Forecastday.Temperature temp = new WeatherDto.Forecast.Forecastday.Temperature(10f, 1f, 5f, 10, 0);
        WeatherDto.Forecast.Forecastday fd = new WeatherDto.Forecast.Forecastday(null, LocalDate.now(), Instant.now(), temp);
        WeatherDto.Forecast forecast = new WeatherDto.Forecast(List.of(fd));
        WeatherDto remoteDto = new WeatherDto(loc, forecast);

        when(restTemplate.getForObject(uri, WeatherDto.class)).thenReturn(remoteDto);

        // Prepare mapper/DB interactions
        LocationEntity mappedLocation = new LocationEntity();
        mappedLocation.setCity("Bucuresti");
        mappedLocation.setLat(new java.math.BigDecimal("44.43"));
        mappedLocation.setLon(new java.math.BigDecimal("26.10"));

        when(mapper.locationToEntity(remoteDto)).thenReturn(mappedLocation);
        // Simulate existing location found in DB
        when(lRepository.findLocationEntityByLatAndLon(mappedLocation.getLat(), mappedLocation.getLon()))
                .thenReturn(Optional.of(mappedLocation));

        WeatherEntity wEntity = new WeatherEntity();
        wEntity.setId(1L);
        wEntity.setTargetDay(LocalDate.now());
        wEntity.setLocation(mappedLocation);
        when(mapper.weatherToEntity(remoteDto, mappedLocation)).thenReturn(List.of(wEntity));
        when(wRepository.findWeatherEntityByTargetDayAndLocation(any(), any())).thenReturn(Optional.of(wEntity));

        WeatherDto expectedDto = new WeatherDto(loc, forecast);
        when(mapper.entityToDto(anyList(), eq(mappedLocation))).thenReturn(expectedDto);

        WeatherDto result = weatherService.mappAndSave(uri);

        assertNotNull(result);
        assertEquals(expectedDto.location().city(), result.location().city());

        verify(restTemplate, times(1)).getForObject(uri, WeatherDto.class);
        verify(mapper, times(1)).locationToEntity(remoteDto);
        verify(lRepository, times(1)).findLocationEntityByLatAndLon(mappedLocation.getLat(), mappedLocation.getLon());
    }
}
