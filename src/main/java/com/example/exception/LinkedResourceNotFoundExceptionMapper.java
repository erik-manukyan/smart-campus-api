/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.exception;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author ericm
 */

@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {
    @Override
    public Response toResponse(LinkedResourceNotFoundException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Unprocessable Entity");
        error.put("message", ex.getMessage());
        error.put("status", 422);
        
        return Response.status(422).entity(error).build();
    }
}