import java.util.HashMap;
import java.util.ArrayList;

public class DBRoutes {
    private HashMap<Connection, ArrayList<Routes>> map;

    public DBRoutes() {
        map = new HashMap<Connection, ArrayList<Routes>>();
    }

    public void addRoutes(Connection conn, ArrayList<Routes> routes) {
        map.put(conn, routes);
    }

    public ArrayList<Routes> getRoutes(Connection conn) {
        ArrayList<Routes> list = map.get(conn);
        return (list == null) ? new ArrayList<>() : new ArrayList<>(list);
    }

    public boolean validateRoutes(Connection conn) {
        return map.containsKey(conn);
    }

}
