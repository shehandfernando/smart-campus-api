package exceptions;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

public class ExceptionMappers {

    @Provider
    public static class RoomNotEmptyMapper implements ExceptionMapper<ApiExceptions.RoomNotEmptyException> {
        public Response toResponse(ApiExceptions.RoomNotEmptyException ex) {
            return buildError(Response.Status.CONFLICT, ex.getMessage());
        }
    }

    @Provider
    public static class LinkedResourceNotFoundMapper implements ExceptionMapper<ApiExceptions.LinkedResourceNotFoundException> {
        public Response toResponse(ApiExceptions.LinkedResourceNotFoundException ex) {
            return buildError(Response.Status.fromStatusCode(422), ex.getMessage());
        }
    }

    @Provider
    public static class SensorUnavailableMapper implements ExceptionMapper<ApiExceptions.SensorUnavailableException> {
        public Response toResponse(ApiExceptions.SensorUnavailableException ex) {
            return buildError(Response.Status.FORBIDDEN, ex.getMessage());
        }
    }

    @Provider
    public static class GlobalMapper implements ExceptionMapper<Throwable> {
        public Response toResponse(Throwable ex) {
            return buildError(Response.Status.INTERNAL_SERVER_ERROR, "Unexpected server error.");
        }
    }

    private static Response buildError(Response.StatusType status, String msg) {
        Map<String, String> error = new HashMap<>();
        error.put("error", status.getReasonPhrase());
        error.put("message", msg);
        return Response.status(status).entity(error).build();
    }
}