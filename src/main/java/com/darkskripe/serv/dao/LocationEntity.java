package com.darkskripe.serv.dao;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;


@Table(name = "location",uniqueConstraints = @UniqueConstraint(columnNames = {"lat","lon"}))
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LocationEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name= "city")
    private String city;

    @Column(name = "lat",nullable = false)
    private BigDecimal  lat;

    @Column(name = "lon",nullable = false)
    private BigDecimal lon;

    @PrePersist
    @PreUpdate
    public void valid(){
        lat = lat.setScale(2, RoundingMode.HALF_UP);
        lon = lon.setScale(2, RoundingMode.HALF_UP);
    }
}
