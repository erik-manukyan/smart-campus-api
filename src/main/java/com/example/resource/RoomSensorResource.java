/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.resource;

import com.example.dao.GenericDAO;
import com.example.dao.MockDatabase;
import com.example.model.Room;
import com.example.model.Sensor;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author ericm
 */
@Path("/rooms/{roomId}/sensors")
public class RoomSensorResource {

    private final GenericDAO<Room> roomDAO = new GenericDAO<>(MockDatabase.ROOMS);
    private final GenericDAO<Sensor> sensorDAO = new GenericDAO<>(MockDatabase.SENSORS);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Sensor> getSensorsInRoom(@PathParam("roomId") String roomId) {
        Room room = roomDAO.getById(roomId);

        if (room == null) {
            throw new WebApplicationException("Room not found", 404);
        }

        List<Sensor> sensorsInRoom = new ArrayList<>();
        for (String sensorId : room.getSensorIds()) {
            Sensor sensor = sensorDAO.getById(sensorId);
            if (sensor != null) {
                sensorsInRoom.add(sensor);
            }
        }

        return sensorsInRoom;
    }
}
