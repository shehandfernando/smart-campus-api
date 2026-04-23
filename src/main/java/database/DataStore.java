package database;
import models.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {
    private static DataStore instance = new DataStore();

    private Map<String, Room> rooms = new ConcurrentHashMap<>();
    private Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private Map<String, SensorReading> readings = new ConcurrentHashMap<>();

    private DataStore() {
        rooms.put("LIB-301", new Room("LIB-301", "Library", 50));
    }

    public static DataStore getInstance() { return instance; }
    public Map<String, Room> getRooms() { return rooms; }
    public Map<String, Sensor> getSensors() { return sensors; }
    public Map<String, SensorReading> getReadings() { return readings; }
}