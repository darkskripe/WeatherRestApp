package com.darkskripe.serv.controller;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record WeatherDto(Location location,Forecast forecast) {

            public record Location(
                    @Nullable
                    Long locationId,
                    @JsonProperty("name") String city,
                    @JsonProperty("lat") Float lat,
                    @JsonProperty("lon") Float lon
            ){}


            public record Forecast(List<Forecastday> forecastday){
               public record  Forecastday(
                       @Nullable Long forecastDatId, LocalDate date,@Nullable Instant lastUpdate,
                       @JsonProperty("day") Temperature Temperature
               ){
                   public record Temperature(
                           @JsonProperty("maxtemp_c") Float maxTemperature,
                           @JsonProperty("mintemp_c") Float minTemperature,
                           @JsonProperty("avgtemp_c") Float avgTemperature,
                           @JsonProperty("daily_chance_of_rain") Integer rainChance,
                           @JsonProperty("daily_chance_of_snow") Integer snowChance

                   ){ }
               }
            }
}