import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PopulateDB {
    private DBCities dbCities;
    private DBConnection dbConnection;
    private DBRoutes dbRoutes;
    private String dbUrl;

    // Default database path using current working directory
    private static final String DEFAULT_DB_PATH = System.getProperty("user.dir") + System.getProperty("file.separator")
            + "Iteration 3" + System.getProperty("file.separator") + "databaseProject.db";
    private static final String DEFAULT_DB_URL = "jdbc:sqlite:" + DEFAULT_DB_PATH;

    // COLORS
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    // Constructor that accepts database URL
    public PopulateDB(String dbUrl) {
        this.dbUrl = dbUrl;
        this.dbCities = new DBCities();
        this.dbConnection = new DBConnection();
        this.dbRoutes = new DBRoutes();
        System.out.println(ANSI_CYAN + "Custom database path: " + dbUrl + ANSI_RESET);
    }

    // Default constructor (uses default database path)
    public PopulateDB() {
        this.dbUrl = DEFAULT_DB_URL;
        this.dbCities = new DBCities();
        this.dbConnection = new DBConnection();
        this.dbRoutes = new DBRoutes();
        System.out.println(ANSI_BLUE + "Database path: " + DEFAULT_DB_PATH + ANSI_RESET);
    }

    // Setter to configure database URL
    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    // Load data from the SQLite database into memory objects
    public void loadFromDatabase() {
        System.out.println(ANSI_CYAN + "Loading data from database..." + ANSI_RESET);

        DBCities dbCities = new DBCities();
        DBConnection dbConnection = new DBConnection();
        DBRoutes dbRoutes = new DBRoutes();

        // Keep a map of city names to Cities objects for consistency
        Map<String, Cities> cityPool = new HashMap<>();

        try (var conn = DriverManager.getConnection(dbUrl)) {
            // Load all cities from the Cities table
            String citiesSql = "SELECT name FROM Cities ORDER BY name";
            try (var stmt = conn.createStatement();
                    var rs = stmt.executeQuery(citiesSql)) {
                while (rs.next()) {
                    String cityName = rs.getString("name");
                    Cities city = new Cities(cityName);
                    cityPool.put(cityName, city);
                    dbCities.addCity(city);
                }
            }

            System.out.println(ANSI_GREEN + "Loaded " + cityPool.size() + " cities from database" + ANSI_RESET);

            // Load all routes from the Routes table
            String routesSql = "SELECT routeID, departureDateTime, arrivalDateTime, traintype, " +
                    "firstClassPrice, secondClassPrice, departureCity, arrivalCity FROM Routes";
            try (var stmt = conn.createStatement();
                    var rs = stmt.executeQuery(routesSql)) {
                while (rs.next()) {
                    String routeID = rs.getString("routeID");
                    String depTimeStr = rs.getString("departureDateTime");
                    String arrTimeStr = rs.getString("arrivalDateTime");
                    String trainType = rs.getString("traintype");
                    int firstPrice = rs.getInt("firstClassPrice");
                    int secondPrice = rs.getInt("secondClassPrice");
                    String depCityName = rs.getString("departureCity");
                    String arrCityName = rs.getString("arrivalCity");

                    Cities depCity = cityPool.get(depCityName.toLowerCase());
                    Cities arrCity = cityPool.get(arrCityName.toLowerCase());

                    if (depCity != null && arrCity != null) {
                        try {
                            LocalTime depTime = LocalTime.parse(depTimeStr);
                            LocalTime arrTime = LocalTime.parse(arrTimeStr);
                            Duration duration = Duration.between(depTime, arrTime);
                            if (duration.isNegative()) {
                                duration = duration.plusDays(1);
                            }

                            Routes route = new Routes(routeID, depCity, arrCity, duration,
                                    depTime, arrTime, trainType, "Daily", firstPrice, secondPrice);
                            dbRoutes.addRoutes(route);
                        } catch (Exception e) {
                            System.err.println("Error loading route " + routeID + ": " + e.getMessage());
                        }
                    }
                }
            }

            System.out.println(
                    ANSI_GREEN + "Loaded " + dbRoutes.getRoutes().size() + " routes from database" + ANSI_RESET);

            // Load all connections from the Connection table
            String connectionsSql = "SELECT id, tripDuration, qtyStops, " +
                    "firstRouteID, secondRouteID, thirdRouteID, departureCity, arrivalCity, firstStop, secondStop FROM Connection";
            try (var stmt = conn.createStatement();
                    var rs = stmt.executeQuery(connectionsSql)) {
                while (rs.next()) {
                    String connId = rs.getString("id");
                    String durationStr = rs.getString("tripDuration");
                    int qtyStops = rs.getInt("qtyStops");
                    String firstRouteID = rs.getString("firstRouteID");
                    String secondRouteID = rs.getString("secondRouteID");
                    String thirdRouteID = rs.getString("thirdRouteID");
                    String depCityName = rs.getString("departureCity");
                    String arrCityName = rs.getString("arrivalCity");
                    String firstStopName = rs.getString("firstStop");
                    String secondStopName = rs.getString("secondStop");

                    try {
                        Duration tripDuration = Duration.parse(durationStr);
                        Cities depCity = cityPool.get(depCityName.toLowerCase());
                        Cities arrCity = cityPool.get(arrCityName.toLowerCase());

                        if (depCity != null && arrCity != null) {
                            ArrayList<Routes> routeList = new ArrayList<>();
                            Routes route1 = dbRoutes.getRouteByID(firstRouteID);
                            if (route1 != null)
                                routeList.add(route1);

                            if (secondRouteID != null) {
                                Routes route2 = dbRoutes.getRouteByID(secondRouteID);
                                if (route2 != null)
                                    routeList.add(route2);
                            }

                            if (thirdRouteID != null) {
                                Routes route3 = dbRoutes.getRouteByID(thirdRouteID);
                                if (route3 != null)
                                    routeList.add(route3);
                            }

                            // Load stop cities if they exist
                            ArrayList<Cities> stopCities = new ArrayList<>();
                            if (firstStopName != null && !firstStopName.isEmpty()) {
                                Cities stopCity = cityPool.get(firstStopName.toLowerCase());
                                if (stopCity != null)
                                    stopCities.add(stopCity);
                            }
                            if (secondStopName != null && !secondStopName.isEmpty()) {
                                Cities stopCity = cityPool.get(secondStopName.toLowerCase());
                                if (stopCity != null)
                                    stopCities.add(stopCity);
                            }

                            // Load days of operation for this connection
                            ArrayList<String> daysList = new ArrayList<>();
                            String daysSql = "SELECT dayOfOperation FROM connection_days WHERE connectionID = ?";
                            try (var dayStmt = conn.prepareStatement(daysSql)) {
                                dayStmt.setString(1, connId);
                                try (var daysRs = dayStmt.executeQuery()) {
                                    while (daysRs.next()) {
                                        daysList.add(daysRs.getString("dayOfOperation"));
                                    }
                                }
                            }

                            if (daysList.isEmpty()) {
                                daysList.add("Daily"); // Default to daily if no days found
                            }

                            Connection connection = new Connection(depCity, arrCity, tripDuration,
                                    qtyStops, stopCities, daysList, routeList);
                            connection.setId(connId);
                            dbConnection.addConnection(connection);
                        }
                    } catch (Exception e) {
                        System.err.println("Error loading connection " + connId + ": " + e.getMessage());
                    }
                }
            }

            System.out.println(ANSI_GREEN + "Loaded " + dbConnection.getAllConnections().size()
                    + " connections from database" + ANSI_RESET);

        } catch (SQLException e) {
            System.err.println(ANSI_RED + "Error loading from database: " + e.getMessage() + ANSI_RESET);
            e.printStackTrace();
        }

        // Assign loaded objects to instance fields
        this.dbCities = dbCities;
        this.dbConnection = dbConnection;
        this.dbRoutes = dbRoutes;
    }

    // Read the CSV file and populate the DBCities, DBConnection and DBRoutes
    // classes
    public void populateDatabase(String csvFilePath) {
        DBCities dbCities = new DBCities();
        DBConnection dbConnection = new DBConnection();
        DBRoutes dbRoutes = new DBRoutes();

        // keep a single Cities instance per city name to ensure map lookups work
        Map<String, Cities> cityPool = new HashMap<>();
        // temporary routes map so we can aggregate multiple Routes per Connection
        // HashSet<Routes> tempRoutes = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                String[] fields = parseCSVLine(line);
                if (fields.length < 9)
                    continue; // skip malformed lines

                String routeId = fields[0].trim();
                String depName = fields[1].trim();
                String arrName = fields[2].trim();
                String depTimeStr = fields[3].trim();
                String arrTimeStr = fields[4].trim();
                String trainType = fields[5].trim();
                String daysOfOp = fields[6].trim();
                String firstPriceStr = fields[7].trim();
                String secondPriceStr = fields[8].trim();

                Cities depCity = cityPool.computeIfAbsent(depName, k -> {
                    Cities c = new Cities(depName);
                    dbCities.addCity(c);
                    return c;
                });
                Cities arrCity = cityPool.computeIfAbsent(arrName, k -> {
                    Cities c = new Cities(arrName);
                    dbCities.addCity(c);
                    return c;
                });

                // parse times and compute duration
                boolean arrivalNextDay = arrTimeStr.contains("+1d") || arrTimeStr.contains("(+1d)");
                String arrTimeClean = arrTimeStr.replaceAll("\\s*\\(\\+1d\\)|\\+1d", "").trim();
                try {
                    LocalTime depTime = LocalTime.parse(depTimeStr);
                    LocalTime arrTime = LocalTime.parse(arrTimeClean);
                    Duration duration = Duration.between(depTime, arrTime);
                    if (duration.isNegative() || arrivalNextDay) {
                        duration = duration.plusDays(1);
                    }
                    // create Route and add to tempRoutes

                    int firstPrice = 0;
                    int secondPrice = 0;
                    try {
                        firstPrice = Integer.parseInt(firstPriceStr);
                    } catch (NumberFormatException e) {
                        System.out.println("System cannot read price integer for route id " + routeId);
                    }
                    try {
                        secondPrice = Integer.parseInt(secondPriceStr);
                    } catch (NumberFormatException e) {
                        System.out.println("System cannot read price integer for route id " + routeId);
                    }

                    Routes route = new Routes(routeId, depCity, arrCity, duration, depTime, arrTime, trainType,
                            daysOfOp, firstPrice, secondPrice);

                    Connection conn = new Connection(depCity, arrCity, duration, daysOfOp, route);
                    dbConnection.addConnection(conn);

                    // tempRoutes.add(route);
                    dbRoutes.addRoutes(route);

                } catch (Exception ex) {
                    // skip malformed time/line but continue parsing rest
                    continue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Total cities loaded: " + dbCities.getAllCityNames().length);
        System.out.println("Total connections loaded: " + dbConnection.getAllConnections().size());
        System.out.println("======================================================================");
        System.out.println("======================================================================");
        System.out.println("======================================================================");
        System.out.println("======================================================================");
        System.out.println("======================================================================");
        System.out.println("======================================================================");
        System.out.println("======================================================================");
        System.out.println("======================================================================");
        // populate connections of 1 stop (2 routes)
        String[] cities = dbCities.getAllCityNames();
        HashSet<Routes> setOrigin, setArr;
        setOrigin = new HashSet<Routes>();
        setArr = new HashSet<Routes>();
        // Connection(Cities departureCity, Cities arrivalCity, Duration tripDuration,
        // int qtyStops,ArrayList<Cities> stopCities)
        Connection placeholderConnection;
        Duration duration;
        ArrayList<String> commonDays;
        ArrayList<Cities> city;
        ArrayList<Routes> routeList;

        // for(String s:cities){System.out.println(s);}

        // for all the cities we create 1-stop connections with every other city (if
        // there are 2 routes connecting them)
        for (String origin : cities) {
            for (String end : cities) {
                setOrigin = new HashSet<Routes>();
                setArr = new HashSet<Routes>();
                for (Routes r : dbRoutes.getRoutes()) {
                    // add all the route that have the same departure city as the origin (from for
                    // loop)
                    if (r.getDepartureCity().getName().compareToIgnoreCase(origin) == 0) {
                        setOrigin.add(r);
                    }
                    // add all the route that have the same arrival city as the end (from for loop)
                    else if (r.getArrivalCity().getName().compareToIgnoreCase(end) == 0) {
                        setArr.add(r);
                    }
                }

                // check in both sets if the arrival of routes in origin set is the same as the
                // departure in route in arrival set
                // if there are, filter out the routes that don't operate on the same days and
                // routes with a time between routes thats less than 30 minutes
                for (Routes routeO : setOrigin) {
                    for (Routes routeA : setArr) {
                        if (routeO.getArrivalCity().getName()
                                .compareToIgnoreCase(routeA.getDepartureCity().getName()) == 0) {
                            // compare arrTime and departTime
                            duration = Duration.between(routeA.getDepartureDateTime(), routeO.getArrivalDateTime());
                            if (duration.isNegative() || duration.compareTo(Duration.ofMinutes(30)) < 0) {
                                continue;
                            } else {
                                commonDays = new ArrayList<>(routeA.getDaysofoperation());
                                commonDays.retainAll(routeO.getDaysofoperation());
                                if (commonDays.isEmpty()) {
                                    continue;
                                }
                                // keep track of stop cities (since 1 stop, the only stop city is the arrival of
                                // the 1st route || the departure of the 2nd route)
                                city = new ArrayList<>();
                                city.add(routeO.getArrivalCity());

                                routeList = new ArrayList<>();
                                // add the routes in order
                                routeList.add(routeO);
                                routeList.add(routeA);
                                placeholderConnection = new Connection(routeO.getDepartureCity(),
                                        routeA.getArrivalCity(), duration, 1, city, commonDays, routeList);
                                dbConnection.addConnection(placeholderConnection);
                                // System.out.println(ANSI_GREEN + "Added connection: " + placeholderConnection
                                // + ANSI_RESET);
                            }
                        }
                    }
                }
            }
        }

        // push aggregated routes into DBRoutes
        // dbRoutes.changeSet(tempRoutes);

        // third degree connections (2 stops, 3 routes) can be added similarly but
        Duration duration1;
        Duration duration2;
        Duration totalDuration;

        ArrayList<String> commonDays1;
        ArrayList<String> commonDays2;

        HashSet<Routes> tempRoutes = new HashSet<>();
        tempRoutes = dbRoutes.getRoutes();

        for (String o : cities) {
            HashSet<Routes> firstLeg = new HashSet<>();
            firstLeg = dbRoutes.getRouteByCityDep(o);

            // System.out.println("First leg from " + o + ": " + firstLeg);

            for (String e : cities) {
                HashSet<Routes> thirdLeg = new HashSet<>();
                thirdLeg = dbRoutes.getRouteByCityArr(e);

                // System.out.println("Third leg to " + e + ": " + thirdLeg);
                for (Routes r2 : tempRoutes) {
                    for (Routes r1 : firstLeg) {
                        for (Routes r3 : thirdLeg) {

                            // Check t.dep == r1.arr
                            if (r2.getDepartureCity().getName()
                                    .compareToIgnoreCase(r1.getArrivalCity().getName()) == 0) {

                                // check time
                                // compare arrTime and departTime
                                duration1 = Duration.between(r2.getDepartureDateTime(), r1.getArrivalDateTime());
                                if (duration1.isNegative() || duration1.compareTo(Duration.ofMinutes(30)) < 0) {
                                    continue;
                                } else {

                                    commonDays1 = new ArrayList<>(r2.getDaysofoperation());
                                    commonDays1.retainAll(r1.getDaysofoperation());
                                    if (commonDays1.isEmpty()) {
                                        continue;
                                    }

                                    if (r2.getArrivalCity().getName()
                                            .compareToIgnoreCase(r3.getDepartureCity().getName()) == 0) {

                                        // check time
                                        // compare arrTime and departTime
                                        duration2 = Duration.between(r3.getDepartureDateTime(),
                                                r2.getArrivalDateTime());
                                        if (duration2.isNegative() || duration2.compareTo(Duration.ofMinutes(30)) < 0) {
                                            continue;
                                        } else {

                                            commonDays2 = new ArrayList<>(r3.getDaysofoperation());
                                            commonDays2.retainAll(commonDays1);
                                            if (commonDays2.isEmpty()) {
                                                continue;
                                            }

                                            city = new ArrayList<>();
                                            city.add(r2.getDepartureCity());
                                            city.add(r2.getArrivalCity());

                                            totalDuration = Duration.between(r1.getDepartureDateTime(),
                                                    r3.getArrivalDateTime());

                                            routeList = new ArrayList<>();
                                            // add the routes in order
                                            routeList.add(r1);
                                            routeList.add(r2);
                                            routeList.add(r3);
                                            placeholderConnection = new Connection(r1.getDepartureCity(),
                                                    r3.getArrivalCity(), totalDuration, 2, city, commonDays2,
                                                    routeList);
                                            dbConnection.addConnection(placeholderConnection);
                                            // System.out.println(ANSI_GREEN + "Added connection: " +
                                            // placeholderConnection + ANSI_RESET);
                                        }

                                    }

                                }

                            }
                        }
                    }
                }

            }
        }

        // At this point dbCities, dbConnection and dbRoutes have been populated.
        // You can expose them, store them as fields, or return them depending on how
        // the rest
        // of your program should access the populated data.

        this.dbCities = dbCities;
        this.dbConnection = dbConnection;
        this.dbRoutes = dbRoutes;

        // Now, insert the data into the SQLite database

        /*
         * IN ORDER
         * Cities
         * Routes
         * routes_days
         * Connection
         * connection_days
         */

        // Database URL is configured (either default or custom)
        String sql = "INSERT OR IGNORE INTO Cities(name) VALUES(?)";

        try (var conn = DriverManager.getConnection(dbUrl);
                var pstmt = conn.prepareStatement(sql)) {

            for (String c : dbCities.getAllCityNames()) {
                pstmt.setString(1, c);
                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        sql = "INSERT OR IGNORE INTO Routes(routeID, departureDateTime, arrivalDateTime, traintype, firstClassPrice, secondClassPrice, departureCity, arrivalCity) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        try (var conn = DriverManager.getConnection(dbUrl);
                var pstmt = conn.prepareStatement(sql)) {

            for (Routes r : dbRoutes.getRoutes()) {
                pstmt.setString(1, r.getRouteID());
                pstmt.setString(2, r.getDepartureDateTime().toString());
                pstmt.setString(3, r.getArrivalDateTime().toString());
                pstmt.setString(4, r.getTraintype());
                pstmt.setInt(5, r.getFirstClassPrice());
                pstmt.setInt(6, r.getSecondClassPrice());
                pstmt.setString(7, r.getDepartureCity().getName());
                pstmt.setString(8, r.getArrivalCity().getName());
                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        sql = "INSERT OR IGNORE INTO route_days(routeID, dayOfOperation) VALUES(?, ?)";

        try (var conn = DriverManager.getConnection(dbUrl);
                var pstmt = conn.prepareStatement(sql)) {

            for (Routes route : dbRoutes.getRoutes()) {
                for (String day : route.getDaysofoperation()) {
                    pstmt.setString(1, route.getRouteID());
                    pstmt.setString(2, day);
                    pstmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        sql = "INSERT OR IGNORE INTO Connection(id, tripDuration, qtyStops, firstClassPrice, secondClassPrice, firstRouteID, secondRouteID, thirdRouteID, departureCity, arrivalCity, firstStop, secondStop) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (var conn = DriverManager.getConnection(dbUrl);
                var pstmt = conn.prepareStatement(sql)) {

            for (Connection connection : dbConnection.getAllConnections()) {
                pstmt.setString(1, connection.getId());
                pstmt.setString(2, connection.getTripDuration().toString());
                pstmt.setInt(3, connection.getQtyStops());
                pstmt.setInt(4, connection.getFirstClassPrice());
                pstmt.setInt(5, connection.getSecondClassPrice());
                pstmt.setString(6, connection.getRoutes().get(0).getRouteID());

                // check if there are stops to set the route IDs and stop city names

                // 1 stop -> 2 routes & 1 stop city
                // 2 stops -> 3 routes & 2 stop cities
                // 0 stops -> 1 route & no stop city
                if (connection.getQtyStops() == 1) {
                    pstmt.setString(7, connection.getRoutes().get(1).getRouteID());
                    pstmt.setString(8, null);
                    pstmt.setString(9, connection.getDepartureCity().getName());
                    pstmt.setString(10, connection.getArrivalCity().getName());
                    pstmt.setString(11, connection.getStopCities().get(0).getName());
                    pstmt.setString(12, null);
                } else if (connection.getQtyStops() == 2) {
                    pstmt.setString(7, connection.getRoutes().get(1).getRouteID());
                    pstmt.setString(8, connection.getRoutes().get(2).getRouteID());
                    pstmt.setString(9, connection.getDepartureCity().getName());
                    pstmt.setString(10, connection.getArrivalCity().getName());
                    pstmt.setString(11, connection.getStopCities().get(0).getName());
                    pstmt.setString(12, connection.getStopCities().get(1).getName());
                } else {
                    pstmt.setString(7, null);
                    pstmt.setString(8, null);
                    pstmt.setString(9, connection.getDepartureCity().getName());
                    pstmt.setString(10, connection.getArrivalCity().getName());
                    pstmt.setString(11, null);
                    pstmt.setString(12, null);
                }
                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        sql = "INSERT OR IGNORE INTO connection_days(connectionID, dayOfOperation) VALUES(?, ?)";

        try (var conn = DriverManager.getConnection(dbUrl);
                var pstmt = conn.prepareStatement(sql)) {

            for (Connection connection : dbConnection.getAllConnections()) {
                for (String day : connection.getDaysOfOperation()) {
                    pstmt.setString(1, connection.getId());
                    pstmt.setString(2, day);
                    pstmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }

    /**
     * Parse a CSV line into fields handling quoted fields with commas.
     */
    private String[] parseCSVLine(String line) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                inQuotes = !inQuotes;
            } else if (ch == ',' && !inQuotes) {
                fields.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(ch);
            }
        }
        fields.add(cur.toString());
        return fields.toArray(new String[0]);
    }

    public DBCities getDbCities() {
        return dbCities;
    }

    public DBConnection getDbConnection() {
        return dbConnection;
    }

    public DBRoutes getDbRoutes() {
        return dbRoutes;
    }
}
