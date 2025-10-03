import java.util.ArrayList;

public class TEST {
    public static void main(String[] args) {
        // Populate the databases
        PopulateDB L = new PopulateDB();
        L.populateDatabase("docs/eu_rail_network.csv");

        // print the cities to verify
        DBCities citiesDB = L.getDbCities();
        for (String cityName : citiesDB.getAllCityNames()) {
            System.out.println(cityName);
        }

        // Validate cities
        // is amsterdam in citiesDB?\
        System.out.println("Is Amsterdam in the database? " + citiesDB.validateCities("Amsterdam", "Amsterdam"));

        // Validate connections
        DBConnection connectionDB = L.getDbConnection();
        Cities amsterdam = citiesDB.getCityByName("Amsterdam");
        Cities berlin = citiesDB.getCityByName("Berlin");
        System.out.println("Is there a connection between Amsterdam and Berlin? "
                + connectionDB.validateConnection(amsterdam, berlin));

        // validate routes
        DBRoutes routesDB = L.getDbRoutes();
        ArrayList<Connection> connections = connectionDB.getConnection(amsterdam);
        if (!connections.isEmpty()) {
            Connection conn = connections.get(0);
            System.out.println("Routes for the first connection from Amsterdam:");
            ArrayList<Routes> routes = routesDB.getRoutes(conn);
            for (Routes route : routes) {
                System.out.println(route);
            }
        } else {
            System.out.println("No connections found from Amsterdam.");
        }
    }

}
