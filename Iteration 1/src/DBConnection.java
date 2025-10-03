import java.util.HashMap;
import java.util.ArrayList;

public class DBConnection {
    private HashMap<Cities, ArrayList<Connection>> map;

    public DBConnection() {
        map = new HashMap<Cities, ArrayList<Connection>>();
    }

    public void addConnection(Connection conn) {
        Cities dep = conn.getDepartureCity();
        ArrayList<Connection> list = map.get(dep);
        if (list == null) {
            list = new ArrayList<>();
            map.put(dep, list);
        }
        list.add(conn);
    }

    public ArrayList<Connection> getConnection(Cities city) {
        ArrayList<Connection> list = map.get(city);
        return (list == null) ? new ArrayList<>() : new ArrayList<>(list);
    }

    public boolean validateConnection(Cities departure, Cities destination) {
        // Check if both cities exist in the database
        boolean departureExists = map.containsKey(departure);
        boolean destinationExists = map.containsKey(destination);
        return departureExists && destinationExists;
    }

    public ArrayList<Connection> getAllConnections() {
        ArrayList<Connection> all = new ArrayList<>();
        for (ArrayList<Connection> lst : map.values()) {
            all.addAll(lst);
        }
        return all;
    }

}
