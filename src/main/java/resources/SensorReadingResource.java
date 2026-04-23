package resources;

import database.DataStore;
import exceptions.ApiExceptions.SensorUnavailableException;
import models.Sensor;
import models.SensorReading;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {
    private String sensorId;
    private Map<String, SensorReading> readingsDb = DataStore.getInstance().getReadings();
    private Map<String, Sensor> sensorsDb = DataStore.getInstance().getSensors();

    public SensorReadingResource(String sensorId) { this.sensorId = sensorId; }

    @GET
    public Response getHistory() {
        return Response.ok(new ArrayList<>(readingsDb.values())).build();
    }

    @POST
    public Response addReading(SensorReading reading) {
        Sensor parent = sensorsDb.get(sensorId);
        if (parent == null) return Response.status(Response.Status.NOT_FOUND).build();
        if ("MAINTENANCE".equalsIgnoreCase(parent.getStatus())) {
            throw new SensorUnavailableException("Sensor is down for maintenance.");
        }

        reading.setId(UUID.randomUUID().toString());
        reading.setTimestamp(System.currentTimeMillis());
        readingsDb.put(reading.getId(), reading);

        parent.setCurrentValue(reading.getValue());
        sensorsDb.put(sensorId, parent);

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}