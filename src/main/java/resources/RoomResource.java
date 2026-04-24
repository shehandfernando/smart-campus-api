package resources;

import database.DataStore;
import exceptions.ApiExceptions.RoomNotEmptyException;
import models.Room;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Map;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {
    private Map<String, Room> rooms = DataStore.getInstance().getRooms();

    @GET
    public Response getAllRooms() {
        return Response.ok(new ArrayList<>(rooms.values())).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room) {
        // 1. CHECK: Does this ID already exist in our HashMap?
        if (rooms.containsKey(room.getId())) {
            // 2. CONFLICT: If it exists, stop here and return 409
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"Room ID " + room.getId() + " already exists.\"}")
                    .build();
        }

        // 3. SUCCESS: If it's new, save it and return 201
        rooms.put(room.getId(), room);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteRoom(@PathParam("id") String id) {
        Room room = rooms.get(id);
        if (room == null) return Response.status(Response.Status.NOT_FOUND).build();
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Cannot delete room. Active sensors present.");
        }
        rooms.remove(id);
        return Response.noContent().build();
    }
}