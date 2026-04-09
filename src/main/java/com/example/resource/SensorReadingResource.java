package com.example.resource;

import com.example.dao.GenericDAO;
import com.example.dao.MockDatabase;
import com.example.exception.SensorUnavailableException;
import com.example.model.Sensor;
import com.example.model.SensorReading;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

public class SensorReadingResource {

    private final String sensorId;
    private final GenericDAO<Sensor> sensorDAO = new GenericDAO<>(MockDatabase.SENSORS);
    private final GenericDAO<SensorReading> readingDAO = new GenericDAO<>(MockDatabase.SENSORREADINGS);

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SensorReading addReading(SensorReading reading) {

        Sensor sensor = sensorDAO.getById(this.sensorId);

        if (sensor == null) {
            throw new WebApplicationException("Sensor not found", 404);
        }

        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                    "Cannot add reading: sensor '" + this.sensorId + "' is currently under maintenance"
            );
        }

        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        sensor.setCurrentValue(reading.getValue());
        sensorDAO.update(sensor);
        readingDAO.add(reading);

        List<String> readingIds = MockDatabase.SENSOR_READINGS.get(this.sensorId);

        if (readingIds == null) {
            readingIds = new ArrayList<>();
            MockDatabase.SENSOR_READINGS.put(this.sensorId, readingIds);
        }

        readingIds.add(reading.getId());

        return reading;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SensorReading> getReadings(
            @QueryParam("start") Long startTime,
            @QueryParam("end") Long endTime) {

        Sensor sensor = sensorDAO.getById(sensorId);
        if (sensor == null) {
            throw new WebApplicationException("Sensor not found", 404);
        }

        List<String> readingIds = MockDatabase.SENSOR_READINGS.get(this.sensorId);

        if (readingIds == null || readingIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<SensorReading> filtered = new ArrayList<>();

        for (SensorReading reading : readingDAO.getAll()) {

            if (!readingIds.contains(reading.getId())) {
                continue;
            }

            boolean include = true;

            if (startTime != null && reading.getTimestamp() < startTime) {
                include = false;
            }

            if (endTime != null && reading.getTimestamp() > endTime) {
                include = false;
            }

            if (include) {
                filtered.add(reading);
            }
        }

        return filtered;
    }
}
