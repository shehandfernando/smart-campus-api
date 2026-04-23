package resources;

import database.DataStore;
import exceptions.ApiExceptions.LinkedResourceNotFoundException;
import models.Room;
import models.Sensor;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {
    private Map<String, Sensor> sensors = DataStore.getInstance().getSensors();
    private Map<String, Room> rooms = DataStore.getInstance().getRooms();

    @POST
    public Response registerSensor(Sensor sensor) {
        if (!rooms.containsKey(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException("Room ID does not exist.");
        }
        sensors.put(sensor.getId(), sensor);
        rooms.get(sensor.getRoomId()).getSensorIds().add(sensor.getId());
        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    @GET
    public Response getSensors(@QueryParam("type") String type) {
        if (type != null) {
            List<Sensor> filtered = sensors.values().stream()
                    .filter(s -> type.equalsIgnoreCase(s.getType()))
                    .collect(Collectors.toList());
            return Response.ok(filtered).build();
        }
        return Response.ok(new ArrayList<>(sensors.values())).build();
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadings(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}