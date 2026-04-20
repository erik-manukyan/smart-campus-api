package com.example.dao;

import com.example.model.Room;
import com.example.model.Sensor;
import com.example.model.SensorReading;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockDatabase {
    public static final List<Room> ROOMS = new ArrayList<>();
    public static final List<Sensor> SENSORS = new ArrayList<>();
    public static final List<SensorReading> SENSORREADINGS = new ArrayList<>();
    public static final Map<String, List<String>> SENSOR_READINGS = new HashMap<>();

    static {
        ROOMS.add(new Room("LIB-301", "Library Quiet Study", 60));
        ROOMS.add(new Room("ENG-201", "Engineering IoT Lab", 40));

        SENSORS.add(new Sensor("TEMP-001", "Temperature", "ACTIVE", 22.1, "LIB-301"));
        SENSORS.add(new Sensor("CO2-001", "CO2", "ACTIVE", 520.0, "LIB-301"));
        SENSORS.add(new Sensor("OCC-001", "Occupancy", "MAINTENANCE", 0.0, "ENG-201"));

        for (Sensor sensor : SENSORS) {
            for (Room room : ROOMS) {
                if (room.getId().equals(sensor.getRoomId())) {
                    room.getSensorIds().add(sensor.getId());
                    break;
                }
            }
        }

        long now = System.currentTimeMillis();

        SENSORREADINGS.add(new SensorReading("READ-TEMP-001", now - 3_600_000L, 21.8));
        SENSORREADINGS.add(new SensorReading("READ-TEMP-002", now - 1_800_000L, 22.1));
        SENSORREADINGS.add(new SensorReading("READ-CO2-001", now - 2_400_000L, 505.0));
        SENSORREADINGS.add(new SensorReading("READ-CO2-002", now - 1_200_000L, 520.0));
        SENSORREADINGS.add(new SensorReading("READ-OCC-001", now - 7_200_000L, 12.0));

        List<String> tempReadingIds = new ArrayList<>();
        tempReadingIds.add("READ-TEMP-001");
        tempReadingIds.add("READ-TEMP-002");
        SENSOR_READINGS.put("TEMP-001", tempReadingIds);

        List<String> co2ReadingIds = new ArrayList<>();
        co2ReadingIds.add("READ-CO2-001");
        co2ReadingIds.add("READ-CO2-002");
        SENSOR_READINGS.put("CO2-001", co2ReadingIds);

        List<String> occupancyReadingIds = new ArrayList<>();
        occupancyReadingIds.add("READ-OCC-001");
        SENSOR_READINGS.put("OCC-001", occupancyReadingIds);
    }
}
