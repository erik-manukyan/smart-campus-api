/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.resource;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author ericm
 */

@Path("/")
public class DiscoveryResource {
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getAPIinfo(){
        
        Map<String, Object> APIinfo = new HashMap<>();
        
        // Version info
        APIinfo.put("version", "1.0.0");
        APIinfo.put("name", "Smart Campus API");
        
        // Administrative contact
        APIinfo.put("contact", "facilities@westminster.ac.uk");
        
        // Resource links
        Map<String, String> resources = new HashMap<>();
        resources.put("rooms", "/api/v1/rooms");
        resources.put("sensors", "/api/v1/sensors");
        
        APIinfo.put("resources", resources);
        
        return APIinfo;
    }
}
