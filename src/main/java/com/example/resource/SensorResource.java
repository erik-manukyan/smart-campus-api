/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.resource;

import com.example.dao.GenericDAO;
import com.example.dao.MockDatabase;
import com.example.exception.LinkedResourceNotFoundException;
import com.example.model.Room;
import com.example.model.Sensor;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;


/**
 *
 * @author ericm
 */
@Path("/sensors")
public class SensorResource {

    private final GenericDAO<Sensor> sensorDAO = new GenericDAO<>(MockDatabase.SENSORS);
    private final GenericDAO<Room> roomDAO = new GenericDAO<>(MockDatabase.ROOMS);

    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Sensor> getAllSensors(@QueryParam("type") String type) {
        List<Sensor> allSensors = sensorDAO.getAll();

        if (type == null || type.trim().isEmpty()) {
            return allSensors;
        }

        List<Sensor> filteredSensors = new ArrayList<>();
        for (Sensor sensor : allSensors) {
            if (sensor.getType() != null && sensor.getType().equalsIgnoreCase(type.trim())) {
                filteredSensors.add(sensor);
            }
        }

        return filteredSensors;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Sensor createSensor(Sensor sensor) {
        String roomId = sensor.getRoomId();

        if (roomId == null || roomId.trim().isEmpty()) {
        throw new RuntimeException("Room ID is required.");
    }

        Room room = roomDAO.getById(roomId);

        if (room == null) {
            throw new LinkedResourceNotFoundException(
            "Cannot create sensor: referenced room '" + roomId + "' does not exist"
            );
        }

        sensorDAO.add(sensor);

        room.getSensorIds().add(sensor.getId());
        roomDAO.update(room);

        return sensor; 
    }

    @GET
    @Path("/{sensorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Sensor getSensorById(@PathParam("sensorId") String id) {
        return sensorDAO.getById(id);
    }

    @Path("{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
