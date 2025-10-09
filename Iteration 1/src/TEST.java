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

        // city names trim
        System.out.println(citiesDB.getCityByName("A Coruña"));

        Cities amiens = citiesDB.getCityByName("amiens");

        // test 2 degree connection
        ArrayList<Connection> connectionsFromAmiens = connectionDB.getConnection(berlin);
        for (Connection conn : connectionsFromAmiens) {
            if (conn.getQtyStops() == 1) {
                System.out.println(conn);
            }
        }
    }

}
