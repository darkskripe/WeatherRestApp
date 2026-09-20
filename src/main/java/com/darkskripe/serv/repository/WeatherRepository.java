package com.darkskripe.serv.repository;

import com.darkskripe.serv.dao.LocationEntity;
import com.darkskripe.serv.dao.WeatherEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface WeatherRepository extends JpaRepository<WeatherEntity, Long> {

    Optional<WeatherEntity> findWeatherEntityByTargetDayAndLocation(LocalDate targetDay, LocationEntity location);
}
