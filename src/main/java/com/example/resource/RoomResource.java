/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.resource;

/**
 *
 * @author ericm
 */
import com.example.dao.GenericDAO;
import com.example.dao.MockDatabase;
import com.example.exception.RoomNotEmptyException;
import com.example.model.Room;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/rooms")
public class RoomResource {

    private final GenericDAO<Room> roomDAO = new GenericDAO<>(MockDatabase.ROOMS);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Room> getAllRooms() {
        return roomDAO.getAll();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Room createRoom(Room room) {
        roomDAO.add(room);
        return room;
    }

    @GET
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Room getRoomById(@PathParam("roomId") String id) {
        Room room = roomDAO.getById(id);

        if (room == null) {
            throw new WebApplicationException("Room not found", Response.Status.NOT_FOUND);
        }

        return room;
    }

    @DELETE
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Room deleteRoom(@PathParam("roomId") String id) {
        Room room = roomDAO.getById(id);

        if (room == null) {
            throw new WebApplicationException("Room not found", 404);
        }

        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(
                    "Cannot delete room '" + id + "': room contains active sensors"
            );
        }

        roomDAO.delete(id);
        return room;
    }
}
