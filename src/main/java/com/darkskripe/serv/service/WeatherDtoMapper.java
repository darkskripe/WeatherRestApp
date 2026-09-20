package com.darkskripe.serv.service;

import com.darkskripe.serv.controller.WeatherDto;
import com.darkskripe.serv.dao.LocationEntity;
import com.darkskripe.serv.dao.WeatherEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class WeatherDtoMapper {

    public LocationEntity locationToEntity(WeatherDto weatherDto) {
        LocationEntity lEntity=new LocationEntity();
        lEntity.setCity(weatherDto.location().city());
        lEntity.setLat(new BigDecimal(weatherDto.location().lat().toString()));
        lEntity.setLon(new BigDecimal(weatherDto.location().lon().toString()));
        return lEntity;
    }

    public List<WeatherEntity> weatherToEntity(WeatherDto weatherDto, LocationEntity lEntity) {
        List<WeatherEntity> weatherEntityList=new ArrayList<>();
        weatherDto.forecast().forecastday().stream().forEach(f ->{
                    WeatherEntity wEntity = new WeatherEntity();
                    wEntity.setTargetDay(f.date());
                    wEntity.setLocation(lEntity);
                    wEntity.setMaxTemp(f.Temperature().maxTemperature());
                    wEntity.setMinTemp(f.Temperature().minTemperature());
                    wEntity.setAvgTemp(f.Temperature().avgTemperature());
                    wEntity.setRainChance(f.Temperature().rainChance());
                    wEntity.setSnowChance(f.Temperature().snowChance());
                    wEntity.setLastUpdate(Instant.now());
                    weatherEntityList.add(wEntity);
                }
        );
        return weatherEntityList;
    }

    public WeatherDto entityToDto(List<WeatherEntity> weatherEntityList, LocationEntity lEntity) {
        WeatherDto.Location location = new WeatherDto.Location(lEntity.getId(),lEntity.getCity(),lEntity.getLat().floatValue(),lEntity.getLon().floatValue());
        WeatherDto.Forecast forecast = new WeatherDto.Forecast(
                weatherEntityList.stream().map(e -> new WeatherDto.Forecast.Forecastday(
                        e.getId(), e.getTargetDay(), e.getLastUpdate(),
                        new WeatherDto.Forecast.Forecastday.Temperature(
                                e.getMaxTemp(),
                                e.getMinTemp(),
                                e.getAvgTemp(),
                                e.getRainChance(),
                                e.getSnowChance()
                                )
                        )
                ).toList()
        );
        return new WeatherDto(location,forecast);
    }

}
