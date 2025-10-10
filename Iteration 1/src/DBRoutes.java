import java.util.HashSet;

public class DBRoutes {
    private HashSet<Routes> map;

    public DBRoutes() {
        this.map = new HashSet<Routes>();
    }

    public void addRoutes(Routes r) {
        this.map.add(r);
    }

    public void changeSet(HashSet<Routes> set) {
        this.map = set;
    }

    public HashSet<Routes> getRoutes() {
        return (map == null) ? new HashSet<>() : map;
    }

    public boolean validateRoutes(Routes r) {
        return map.contains(r);
    }

    public HashSet<Routes> getRouteByCityDep(String cityName) {
        HashSet<Routes> result = new HashSet<>();
        for (Routes r : map) {
            if (r.getDepartureCity().getName().equalsIgnoreCase(cityName)) {
                result.add(r);
            }
        }
        return result;
    }

    public HashSet<Routes> getRouteByCityArr(String cityName) {
        HashSet<Routes> result = new HashSet<>();
        for (Routes r : map) {
            if (r.getArrivalCity().getName().equalsIgnoreCase(cityName)) {
                result.add(r);
            }
        }
        return result;
    }

    public Routes getRouteByID(String routeID) {
        for (Routes r : map) {
            if (r.getRouteID().equals(routeID)) {
                return r;
            }
        }
        return null;
    }

}
