package resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/")
public class DiscoveryResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDiscoveryInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("version", "1.0");
        info.put("rooms_link", "/api/v1/rooms");
        info.put("sensors_link", "/api/v1/sensors");
        return Response.ok(info).build();
    }
}