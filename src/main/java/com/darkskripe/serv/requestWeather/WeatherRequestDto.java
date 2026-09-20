package com.darkskripe.serv.requestWeather;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;


public record WeatherRequestDto(
        String city,
        @Max(90)
        @Min(-90)
        Double lat,
        @Max(180)
        @Min(-180)
        Double lon,
        @Min(1)
        Integer days

) {
    @AssertTrue(message = "Trebuie furnizat fie 'city', fie 'lat' și 'lon'")
    private boolean isValid() {
        boolean hasCity = city != null && !city.isBlank();
        boolean hasCoords = lat != null && lon != null;
        return hasCity ^ hasCoords; // XOR: exact una din opțiuni
    }
}
