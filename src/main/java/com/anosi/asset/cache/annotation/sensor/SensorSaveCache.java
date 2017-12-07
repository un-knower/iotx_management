package com.anosi.asset.cache.annotation.sensor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;

@Caching(  
        put = {  
                @CachePut(value = "sensor", key = "#sensor.id+'ForSensor'"),  
                @CachePut(value = "sensor", key = "#sensor.serialNo+'ForSensor'"),  
        }  
)  
@Target({ElementType.METHOD, ElementType.TYPE})  
@Retention(RetentionPolicy.RUNTIME)  
@Inherited  
public @interface SensorSaveCache {

}
