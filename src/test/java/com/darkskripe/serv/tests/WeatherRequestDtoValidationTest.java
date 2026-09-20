package com.darkskripe.serv.tests;

/*
 * Purpose:
 * - Validate the Bean Validation rules applied on WeatherRequestDto.
 * - Tests show how @AssertTrue and field-level constraints behave.
 *
 * Why these tests:
 * - Ensure controller-bound objects are rejected when invalid.
 * - Prevent invalid inputs reaching service/database layers.
 */

import com.darkskripe.serv.requestWeather.WeatherRequestDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class WeatherRequestDtoValidationTest {
    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setup() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void valid_city_request_has_no_violations() {
        WeatherRequestDto req = new WeatherRequestDto("Bucuresti", null, null, 2);
        Set<ConstraintViolation<WeatherRequestDto>> violations = validator.validate(req);
        assertTrue(violations.isEmpty(), "Expected no validation errors for valid city request");
    }

    @Test
    void missing_city_and_coords_violates_assertTrue() {
        WeatherRequestDto req = new WeatherRequestDto(null, null, null, 1);
        Set<ConstraintViolation<WeatherRequestDto>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
        boolean containsMessage = violations.stream().anyMatch(v -> v.getMessage().contains("Trebuie furnizat"));
        assertTrue(containsMessage, "Expected @AssertTrue message when both city and coords are missing");
    }

    @Test
    void both_city_and_coords_is_invalid() {
        WeatherRequestDto req = new WeatherRequestDto("Cluj", 46.77, 23.59, 1);
        Set<ConstraintViolation<WeatherRequestDto>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
    }

    @Test
    void lat_out_of_range_is_invalid() {
        WeatherRequestDto req = new WeatherRequestDto(null, 100.0, 10.0, 1);
        Set<ConstraintViolation<WeatherRequestDto>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
    }
}
