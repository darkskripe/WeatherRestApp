package com.darkskripe.serv.repository;

import com.darkskripe.serv.dao.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {
    Optional<LocationEntity> findLocationEntityByLatAndLon(BigDecimal lat, BigDecimal lon);
}
