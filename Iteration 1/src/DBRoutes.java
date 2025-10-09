import java.util.HashSet;

public class DBRoutes {
    private HashSet<Routes> map;

    public DBRoutes() {
        this.map = new HashSet<Routes>();
    }

    public void addRoutes(Routes r) {
        this.map.add(r);
    }

    public void changeSet(HashSet<Routes> set){
        this.map=set;
    }

    public HashSet<Routes> getRoutes(Connection conn) {
        return (map == null) ? new HashSet<>() : map;
    }

    public boolean validateRoutes(Routes r) {
        return map.contains(r);
    }

}
