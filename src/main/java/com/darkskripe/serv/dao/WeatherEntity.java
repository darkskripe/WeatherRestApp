package com.darkskripe.serv.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Table(name = "weather",uniqueConstraints = @UniqueConstraint(columnNames = {"target_day","location_id"}))
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WeatherEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "target_day")
    private LocalDate targetDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private LocationEntity location;

    @Column(name = "max_temp")
    private Float maxTemp;

    @Column(name = "min_temp")
    private Float minTemp;

    @Column(name = "avg_temp")
    private Float avgTemp;

    @Column(name = "chance_to_rain")
    private Integer rainChance;

    @Column(name = "chance_to_snow")
    private Integer snowChance;

    @Column(name = "last_update")
    private Instant lastUpdate;


}
