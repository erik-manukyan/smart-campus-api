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
}