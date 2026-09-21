package com.darkskripe.serv.tests;

/*
 * Purpose:
 * - Unit tests for WeatherDtoMapper to ensure entities <-> DTOs mapping is correct.
 * - Important for verifying data shape before saving to DB or returning to clients.
 *
 * Why these tests:
 * - Mapping bugs silently corrupt data; simple assertions catch regressions early.
 * - Keep mapper logic simple and well-specified.
 */

import com.darkskripe.serv.controller.WeatherDto;
import com.darkskripe.serv.dao.LocationEntity;
import com.darkskripe.serv.dao.WeatherEntity;
import com.darkskripe.serv.service.WeatherDtoMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WeatherDtoMapperTest {

    @Test
    void locationToEntity_and_entityToDto_roundtrip() {
        WeatherDtoMapper mapper = new WeatherDtoMapper();

        // Build a small WeatherDto sample as external API would return
        WeatherDto.Location loc = new WeatherDto.Location(null, "Bucharest", 44.43f, 26.1f);
        WeatherDto.Forecast.Forecastday.Temperature temp = new WeatherDto.Forecast.Forecastday.Temperature(10f, 1f, 5f, 10, 0);
        WeatherDto.Forecast.Forecastday fd = new WeatherDto.Forecast.Forecastday(null, LocalDate.now(), Instant.now(), temp);
        WeatherDto.Forecast forecast = new WeatherDto.Forecast(List.of(fd));
        WeatherDto dto = new WeatherDto(loc, forecast);

        // Map to entity and assert values are transferred
        LocationEntity lEntity = mapper.locationToEntity(dto);
        assertEquals("Bucharest", lEntity.getCity());
        // BigDecimal created from float string; compare by value
        assertEquals(0, lEntity.getLat().compareTo(new BigDecimal("44.43")));
        assertEquals(0, lEntity.getLon().compareTo(new BigDecimal("26.1")));

        // Map forecast -> entities
        var weatherList = mapper.weatherToEntity(dto, lEntity);
        assertEquals(1, weatherList.size());
        WeatherEntity w = weatherList.get(0);
        assertEquals(LocalDate.now(), w.getTargetDay());
        assertEquals(lEntity, w.getLocation());
        assertEquals(10f, w.getMaxTemp());
        assertEquals(1f, w.getMinTemp());

        // Now test entityToDto: create a WeatherEntity list and ensure DTO is produced
        WeatherEntity persisted = new WeatherEntity();
        persisted.setId(42L);
        persisted.setTargetDay(LocalDate.of(2023,1,1));
        persisted.setLocation(lEntity);
        persisted.setMaxTemp(15f);
        persisted.setMinTemp(5f);
        persisted.setAvgTemp(10f);
        persisted.setRainChance(20);
        persisted.setSnowChance(0);

        var dtoOut = mapper.entityToDto(List.of(persisted), lEntity);
        assertEquals("Bucharest", dtoOut.location().city());
        assertEquals(1, dtoOut.forecast().forecastday().size());
        var outDay = dtoOut.forecast().forecastday().get(0);
        assertEquals(persisted.getTargetDay(), outDay.date());
        assertEquals(persisted.getMaxTemp(), outDay.Temperature().maxTemperature());
    }
}
