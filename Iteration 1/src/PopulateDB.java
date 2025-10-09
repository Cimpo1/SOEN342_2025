import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;

public class PopulateDB {
    private DBCities dbCities;
    private DBConnection dbConnection;
    private DBRoutes dbRoutes;

    // Read the CSV file and populate the DBCities, DBConnection and DBRoutes
    // classes
    public void populateDatabase(String csvFilePath) {
        DBCities dbCities = new DBCities();
        DBConnection dbConnection = new DBConnection();
        DBRoutes dbRoutes = new DBRoutes();

        // keep a single Cities instance per city name to ensure map lookups work
        Map<String, Cities> cityPool = new HashMap<>();
        // temporary routes map so we can aggregate multiple Routes per Connection
        HashSet<Routes> tempRoutes = new HashSet<>();
        //
        Map<Cities, ArrayList<Connection>> connections = new HashMap<>();

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

                    tempRoutes.add(route);
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

        
        //for(String s:cities){System.out.println(s);}

        for (String origin : cities) {
            for (String end : cities) {
                setOrigin = new HashSet<Routes>();
                setArr = new HashSet<Routes>();
                for (Routes r : dbRoutes.getRoutes()) {
                    
                    //System.out.println(r);
                    if (r.getDepartureCity().getName().equals(origin)) {
                        setOrigin.add(r);
                    }

                    else if (r.getArrivalCity().getName().equals(end)) {
                        setArr.add(r);
                    }
                }
                //System.out.println("Is setOrgin WORKING"+setOrigin);

                for (Routes routeO : setOrigin) {
                    for (Routes routeA : setArr) {
                        if (routeO.getArrivalCity().getName().equals(routeA.getDepartureCity().getName())) {
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
                                city = new ArrayList<>();
                                city.add(routeO.getArrivalCity());

                                routeList = new ArrayList<>();
                                routeList.add(routeO);
                                routeList.add(routeA);
                                placeholderConnection = new Connection(routeO.getDepartureCity(),
                                        routeA.getArrivalCity(), duration, 1, city, commonDays, routeList);
                                dbConnection.addConnection(placeholderConnection);
                            }
                        }
                    }
                }
            }
        }

        // push aggregated routes into DBRoutes
        dbRoutes.changeSet(tempRoutes);

        // At this point dbCities, dbConnection and dbRoutes have been populated.
        // You can expose them, store them as fields, or return them depending on how
        // the rest
        // of your program should access the populated data.

        this.dbCities = dbCities;
        this.dbConnection = dbConnection;
        this.dbRoutes = dbRoutes;
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
