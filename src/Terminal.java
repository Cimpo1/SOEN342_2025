import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Terminal {

    // Database URL constant using current working directory
    private static final String DB_PATH = System.getProperty("user.dir") + System.getProperty("file.separator")
            + "Iteration 3" + System.getProperty("file.separator") + "databaseProject.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;

    // private attributes
    private Scanner input = new Scanner(System.in);
    private DBConnection dbConnection;
    private DBCities dbCities;
    private DBRoutes dbRoutes;
    private DBTrips dbTrips;
    private DBReservation dbReservations;
    private DBTicket dbTickets;
    private DBClient dbClients;
    private int sortToggle = 1;
    private Connection selectedConnection;
    private Trip currentTrip;

    private HashSet<Connection> results;

    public Terminal() {

    }

    public Terminal(DBConnection dbConnection, DBCities dbCities, DBRoutes dbRoutes, DBTrips dbTrips,
            DBReservation dbReservations, DBTicket dbTickets, DBClient dbClients) {
        this.dbConnection = dbConnection;
        this.dbCities = dbCities;
        this.dbRoutes = dbRoutes;
        this.dbTrips = dbTrips;
        this.dbReservations = dbReservations;
        this.dbTickets = dbTickets;
        this.dbClients = dbClients;
    }

    private void displayResults() {
        if (results == null || results.size() == 0) {
            System.out.println("No connections found for the given search parameters.");
        } else {
            System.out.println("Search Results:");
            // Might need to format results
            for (Connection conn : results) {
                System.out.println(conn);
            }
        }
    }

    public void setResults(HashSet<Connection> connections) {
        if (connections != null) {
            this.results = connections;
        }
    }

    public void accessTerminal() {
        // Ask user if they want to populate the database
        System.out.println("\n============================================================");
        System.out.println("\t       Welcome to the Travel Terminal!");
        System.out.println("============================================================\n");
        System.out.println("Would you like to populate the database from CSV? (y/n): ");
        String populateChoice = input.nextLine().trim().toLowerCase();

        // Always create PopulateDB and get the objects
        PopulateDB L = new PopulateDB();

        // Only populate from CSV if user chooses 'y'
        if (populateChoice.equals("y") || populateChoice.equals("yes")) {
            System.out.println("Populating database...");
            L.populateDatabase("docs/eu_rail_network.csv");
        } else {
            System.out.println("Loading data from database...");
            L.loadFromDatabase();
        }

        // Always get the database objects (populated or loaded from database)
        this.dbConnection = L.getDbConnection();
        this.dbCities = L.getDbCities();
        this.dbRoutes = L.getDbRoutes();

        this.dbTrips = new DBTrips();
        this.dbReservations = new DBReservation();
        this.dbTickets = new DBTicket();
        this.dbClients = new DBClient();

        // Load existing clients from database
        this.loadClientsFromDatabase();

        System.out.println("\n============================================================\n" +
                "\t       Welcome to the Travel Terminal!\n" +
                "============================================================\n");
        this.displayMenu();
    }

    public void displayMenu() {
        while (true) {
            System.out.println(
                    "What would you like to do? \n\t1. Look for a connection\n\t2. Select a connection\n\t3. book a trip\n\t4. View Trips\n\t5. View Trip History\n\t6. Exit");
            String answer = input.nextLine();
            switch (answer) {
                case "1":
                    this.displaySortResults();
                    break;
                case "2":
                    this.selectConnection();
                    break;
                case "3":
                    this.startTripBooking();
                    // add reservation and then end trip booking
                    boolean state = true;
                    while (state) {
                        System.out.println(
                                "What would you like to do? \n\t1. Add a reservation\n\t2. End trip booking");
                        String ans = input.nextLine();
                        switch (ans) {
                            case "1":
                                System.out.print("Enter client's first name: ");
                                String firstName = input.nextLine();
                                System.out.print("Enter client's last name: ");
                                String lastName = input.nextLine();
                                System.out.print("Enter client's age: ");
                                int age = Integer.parseInt(input.nextLine());
                                System.out.print("Enter client's ID (numeric): ");
                                int id = Integer.parseInt(input.nextLine());
                                this.addReservation(firstName, lastName, age, id);
                                break;
                            case "2":
                                this.endTripBooking();
                                state = false;
                                break;
                            default:
                                System.out.println("Woah there, this is not a valid input, please try again");
                        }
                    }
                    break;
                case "4":
                    this.searchTrips();
                    break;
                case "5":
                    this.viewHistoryCollection();
                    break;

                case "6":
                    this.displayEnd();
                    break;
                default:
                    System.out.println("Woah there, this is not a valid input, please try again");
            }
        }
    }

    public void displaySortResults() {
        String departureCity;
        String arrivalCity;
        String day;
        String depTime;
        String arrTime;
        String trainType;
        String firstClass;
        String secondClass;

        System.out.print("Enter departure city: ");
        departureCity = input.nextLine();

        System.out.print("Enter arrival city: ");
        arrivalCity = input.nextLine();

        System.out.print("Enter day of the week: ");
        day = input.nextLine();

        System.out.print("Enter earliest departure: ");
        depTime = input.nextLine();

        System.out.print("Enter latest arrival: ");
        arrTime = input.nextLine();

        System.out.print("Enter train type: ");
        trainType = input.nextLine();

        System.out.print("Enter max first class cost: ");
        firstClass = input.nextLine();

        System.out.print("Enter max second class cost: ");
        secondClass = input.nextLine();

        this.searchConnections(departureCity, arrivalCity, day, depTime, arrTime, trainType, firstClass, secondClass);
        boolean stayLooped = true;
        while (stayLooped) {
            System.out.println(
                    "What would you like to do? \n\t1. Go Back\n\t2. Select Connection\n\t3. Sort by day of the week\n\t4. Sort by departure time\n\t5. Sort by arrival time\n\t6. Sort by train type\n\t7. Sort by first class cost\n\t8. Sort by second class cost\n\t9. Exit");
            String answer = input.nextLine();
            switch (answer) {
                case "1":
                    stayLooped = false;
                    break;
                case "2":
                    this.selectConnection();
                    stayLooped = false;
                    break;
                case "3":
                    this.selectSort("day");
                    break;
                case "4":
                    this.selectSort("depTime");
                    break;
                case "5":
                    this.selectSort("arrTime");
                    break;
                case "6":
                    this.selectSort("trainType");
                    break;
                case "7":
                    this.selectSort("1cost");
                    break;
                case "8":
                    this.selectSort("2cost");
                    break;
                case "9":
                    this.displayEnd();
                    break;
                default:
                    System.out.println("Woah there, this is not a valid input, please try again");
            }
        }
    }

    public void selectSort(String choice) {
        ArrayList<Connection> temp = new ArrayList<Connection>(results);
        switch (choice) {
            case "day":
                temp.sort((a, b) -> {
                    return this.sortToggle * a.getDaysOfOperation().get(0).compareTo(b.getDaysOfOperation().get(0));
                });
                this.sortToggle *= -1;
                break;
            case "depTime":
                temp.sort((a, b) -> {
                    return this.sortToggle * a.getRoutes().get(0).getDepartureDateTime()
                            .compareTo(b.getRoutes().get(0).getDepartureDateTime());
                });
                this.sortToggle *= -1;
                break;
            case "arrTime":
                temp.sort((a, b) -> {
                    return this.sortToggle * a.getRoutes().get(0).getArrivalDateTime()
                            .compareTo(b.getRoutes().get(0).getArrivalDateTime());
                });
                this.sortToggle *= -1;
                break;
            case "trainType":
                temp.sort((a, b) -> {
                    return this.sortToggle
                            * a.getRoutes().get(0).getTraintype().compareTo(b.getRoutes().get(0).getTraintype());
                });
                this.sortToggle *= -1;
                break;
            case "1cost":
                temp.sort((a, b) -> {
                    return this.sortToggle * Integer.compare(a.getFirstClassPrice(), b.getFirstClassPrice());
                });
                this.sortToggle *= -1;
                break;
            case "2cost":
                temp.sort((a, b) -> {
                    return this.sortToggle * Integer.compare(a.getSecondClassPrice(), b.getSecondClassPrice());
                });
                this.sortToggle *= -1;
                break;
        }

        for (Connection con : temp) {
            System.out.println(con);
        }
    }

    public void displayEnd() {
        System.out.println("Saving clients to database...");
        this.saveClientsToDatabase();
        System.out.println("Thank you for using the Travel Terminal. Goodbye!");
        System.exit(0);
    }

    public void searchConnections(String departure, String arrival, String day, String depTime, String arrTime,
            String trainType, String firstRate, String secondRate) {
        // search connection between cities
        System.out.println("Searching for connections between cities...");

        Cities depCity = dbCities.getCityByName(departure);
        Cities arrCity = dbCities.getCityByName(arrival);
        if (depCity == null || arrCity == null) {
            System.out.println("One or both of the specified cities do not exist in the database.");
            System.out.println("Available cities: " + java.util.Arrays.toString(dbCities.getAllCityNames()));
            return;
        }
        // NOTE: THE DAY INPUT SHOULD BE GIVEN IN THE FORMAT "DAY,DAY,..." (e.g.,
        // "Mon,Wed,Fri") OR "DAILY" OR "WEEKENDS"
        // CAPITALIZATION WILL BE HANDLED SO IT DOES NOT MATTER AT FRONT-END LEVEL

        // NOTE: THE TIME INPUT SHOULD BE GIVEN IN THE FORMAT "HH:MM" OR "HH" (e.g.,
        // "14:30" or "9")
        // IMPORTANT: DEPARTURE TIME IS THE TIME AFTER WHICH THE USER WANTS TO DEPART
        // (E.G., IF USER INPUTS 14:00, THEY WANT TO DEPART AT 14:00 OR LATER)
        // IMPORTANT: ARRIVAL TIME IS THE TIME BEFORE WHICH THE USER WANTS TO ARRIVE
        // (E.G., IF USER INPUTS 18:00, THEY WANT TO ARRIVE AT 18:00 OR EARLIER)

        // NOTE: THE TRAIN TYPE INPUT SHOULD BE GIVEN IN THE FORMAT "TYPE1,TYPE2,..."
        // (e.g., "Express,Local")
        // IMPORTANT: IF TRAIN TYPE SIZE > 1, IT MEANS THE USER IS OKAY WITH ANY OF THE
        // TYPES LISTED
        // CAPITALIZATION WILL BE HANDLED SO IT DOES NOT MATTER AT FRONT-END LEVEL

        // NOTE: THE RATES PARAMS ARE GIVEN AS STRING, THE STRING TO INT IS HANDLED IN
        // THE DB CONNECTION CLASS
        // IMPORTANT: FIRST RATE IS THE MAXIMUM PRICE THE USER IS WILLING TO PAY FOR
        // FIRST CLASS
        // IMPORTANT: SECOND RATE IS THE MAXIMUM PRICE THE USER IS WILLING TO PAY FOR
        // SECOND CLASS
        HashSet<Connection> directConnections = dbConnection.getDirectConnections(depCity, arrCity, day, depTime,
                arrTime, trainType, firstRate, secondRate);
        HashSet<Connection> indirectConnections;
        if (directConnections != null && directConnections.size() > 0) {
            setResults(directConnections);
        }
        if (directConnections.size() == 0 || directConnections == null) {
            indirectConnections = dbConnection.getIndirectConnections(depCity, arrCity, day, depTime, arrTime,
                    trainType, firstRate, secondRate);
            setResults(indirectConnections);
        }
        displayResults();
    }

    public void startTripBooking() {
        // Implementation for starting a trip booking to add after interaction design

        if (this.selectedConnection != null) {
            System.out.println("Starting trip booking for the selected connection:");
            System.out.println(this.selectedConnection);
        } else {
            System.out.println("No connection selected. Please select a connection first.");
        }

        // add trip
        this.currentTrip = dbTrips.addTrip();
        Routes firstRoute = this.selectedConnection.getFirstRoute();
        this.currentTrip.setDepartureTime((firstRoute.getDepartureDateTime()));

        // insert trip into database
        String sql = "INSERT INTO Trip(id, departureTime) VALUES(?, ?)";

        try (var conn = DriverManager.getConnection(DB_URL);
                var pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, this.currentTrip.getId());
            pstmt.setString(2, this.currentTrip.getDepartureTime().toString());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        System.out.println("New trip created with ID: " + this.currentTrip.getId());

    }

    public void addReservation(String firstName, String lastName, int age, int id) {
        // Implementation for adding a reservation to add after interaction design

        if (this.currentTrip == null) {
            System.out.println("No active trip. Please start a trip booking first.");
            return;
        }
        // get client
        // TO BE REMOVED WHEN DATABASE IS FULLY INTEGRATED
        Client client = dbClients.getClientById(id);

        // search for client through database
        String sql = "SELECT * FROM Client WHERE id = " + id;

        try (var conn = DriverManager.getConnection(DB_URL);
                var stmt = conn.createStatement();
                var rs = stmt.executeQuery(sql)) {

            int rowCount = 0;
            if (rs.last()) { // Move cursor to the last row
                rowCount = rs.getRow(); // Get the current row number (which is the total count)
                rs.beforeFirst(); // Reset cursor to before the first row for further processing
            }
            if (rowCount > 0) {
                // Client exists, create client object
                rs.first(); // Move cursor to the first row
                client = new Client(rs.getString("firstName"), rs.getString("lastName"), rs.getInt("age"),
                        rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        if (client == null) {
            // create new client
            client = dbClients.addClient(firstName, lastName, age, id);

            // insert client into database

            sql = "INSERT INTO Client(id, firstName, lastName, age) VALUES(?, ?, ?, ?)";

            try (var conn = DriverManager.getConnection(DB_URL);
                    var pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, client.getId());
                pstmt.setString(2, client.getFirstName());
                pstmt.setString(3, client.getLastName());
                pstmt.setInt(4, client.getAge());
                pstmt.executeUpdate();
                System.out.println("New client created with ID: " + client.getId());

            } catch (SQLException e) {
                if (e.getMessage().contains("UNIQUE constraint failed")) {
                    System.out.println("\u001B[31mError: Client ID " + id
                            + " already exists. Please use a different ID.\u001B[0m");
                } else {
                    System.out.println("\u001B[31mDatabase Error: " + e.getMessage() + "\u001B[0m");
                }
            }
        }

        /*
         * NOT DEMANDED BY REQUIREMENTS
         * // check if client already has a reservation for this trip
         * if (this.currentTrip.getReservations() != null) {
         * for (Reservation r : this.currentTrip.getReservations()) {
         * if (r.getClient().getId() == client.getId()) {
         * System.out.println("Client already has a reservation for this trip.");
         * return;
         * }
         * }
         * }
         */
        // create reservation
        Reservation reservation = dbReservations.addReservation(client, this.selectedConnection);

        System.out.println("New reservation created with ID: " + reservation.getId());
        // add reservation to trip
        this.currentTrip.addReservation(reservation);
        System.out.println("Reservation added to current trip.");
        // create ticket
        Ticket ticket = dbTickets.generateTicket(reservation);
        System.out.println("New ticket created with ID: " + ticket.getId());

        // set ticket for reservation
        dbReservations.setTicketForReservation(reservation, ticket);
        System.out.println("Ticket assigned to reservation.");
        // add trip to client
        dbClients.addTripToClient(client, this.currentTrip);
        System.out.println("Trip added to client's trip history.");
        // display ticket info
        System.out.println("Here is your ticket information:");
        System.out.println(ticket);

        // insert reservation into database
        sql = "INSERT INTO Reservation(id, clientID, tripID, ticketID, connectionID) VALUES(?, ?, ?, ?, ?)";

        try (var conn = DriverManager.getConnection(DB_URL);
                var pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, reservation.getId());
            pstmt.setInt(2, client.getId());
            pstmt.setString(3, this.currentTrip.getId());
            pstmt.setString(4, ticket.getId());
            pstmt.setString(5, this.selectedConnection.getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        sql = "INSERT INTO Ticket(id, clientID, tripID, connectionID) VALUES(?, ?, ?, ?)";

        try (var conn = DriverManager.getConnection(DB_URL);
                var pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ticket.getId());
            pstmt.setInt(2, client.getId());
            pstmt.setString(3, this.currentTrip.getId());
            pstmt.setString(4, this.selectedConnection.getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        sql = "INSERT INTO trip_client(tripID, clientID) VALUES(?, ?)";

        try (var conn = DriverManager.getConnection(DB_URL);
                var pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, this.currentTrip.getId());
            pstmt.setInt(2, client.getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }

    public void endTripBooking() {
        // Implementation for ending a trip booking to add after interaction design

        if (this.currentTrip != null) {
            System.out.println("Trip booking ended! Here is the trip information: \n" + this.currentTrip);
            this.currentTrip = null;
        } else {
            System.out.println("No active trip to end.");
        }
    }

    public void searchTrips() {
        // Implementation for searching trips to add after interaction design
        Client client = null;
        try {
            System.out.print("Enter Client ID : ");
            int cid = Integer.parseInt(this.input.nextLine());
            System.out.print("Enter Client's last name to search for trips : ");
            String lastName = this.input.nextLine();

            // First, check in-memory clients (for recently created clients)
            client = dbClients.getClientByIdAndLName(cid, lastName);

            // If not found in memory, search database
            if (client == null) {
                String sql = "SELECT * FROM Client WHERE id = " + cid + " AND lastName = '" + lastName + "'";

                try (var conn = DriverManager.getConnection(DB_URL);
                        var stmt = conn.createStatement();
                        var rs = stmt.executeQuery(sql)) {

                    int rowCount = 0;
                    if (rs.last()) { // Move cursor to the last row
                        rowCount = rs.getRow(); // Get the current row number (which is the total count)
                        rs.beforeFirst(); // Reset cursor to before the first row for further processing
                    }
                    if (rowCount > 0) {
                        // Client exists in database, create client object
                        rs.first(); // Move cursor to the first row
                        client = new Client(rs.getString("firstName"), rs.getString("lastName"), rs.getInt("age"),
                                rs.getInt("id"));
                        // Also add to in-memory dbClients for future lookups
                        dbClients.addClient(client);
                    }
                }
            }

            if (client == null) {
                System.out.println("Invalid client.");
                return;
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a valid Client ID.");
            client = null;
        }

        // Check if client was found
        if (client == null) {
            System.out.println("Client not found in database or memory.");
            return;
        }

        // Query the database for reservations with this client
        System.out.println("\n============================================================");
        System.out.println("Upcoming Trips for Client: " + client.getFirstName() + " " + client.getLastName());
        System.out.println("============================================================\n");

        String sql = "SELECT r.id as reservationID, r.connectionID FROM Reservation r " +
                "WHERE r.clientID = ?";

        try (var conn = DriverManager.getConnection(DB_URL);
                var pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, client.getId());
            var rs = pstmt.executeQuery();

            int tripCount = 0;
            while (rs.next()) {
                String reservationID = rs.getString("reservationID");
                String connectionID = rs.getString("connectionID");

                // Get connection details
                String connSql = "SELECT departureCity, arrivalCity, qtyStops, firstClassPrice, secondClassPrice FROM Connection WHERE id = ?";
                try (var connStmt = conn.prepareStatement(connSql)) {
                    connStmt.setString(1, connectionID);
                    var connRs = connStmt.executeQuery();

                    if (connRs.next()) {
                        String depCity = connRs.getString("departureCity");
                        String arrCity = connRs.getString("arrivalCity");
                        int stops = connRs.getInt("qtyStops");
                        int firstPrice = connRs.getInt("firstClassPrice");
                        int secondPrice = connRs.getInt("secondClassPrice");

                        System.out.println("Reservation #" + (tripCount + 1));
                        System.out.println("  Route: " + depCity + " -> " + arrCity);
                        System.out.println("  Stops: " + stops);
                        System.out.println("  First Class Price: $" + firstPrice);
                        System.out.println("  Second Class Price: $" + secondPrice);
                        System.out.println("  Reservation ID: " + reservationID);
                        System.out.println();
                    }
                }

                tripCount++;
            }

            if (tripCount == 0) {
                System.out.println("No trips found for client ID: " + client.getId());
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving trips from database: " + e.getMessage());
        }
    }

    public void viewHistoryCollection() {
        // Implementation for viewing history collection to add after interaction design
        Client client = null;
        try {
            System.out.print("Enter Client ID : ");
            int cid = Integer.parseInt(this.input.nextLine());
            System.out.print("Enter Client's last name to search for trips : ");
            String lastName = this.input.nextLine();

            // First, check in-memory clients
            client = dbClients.getClientByIdAndLName(cid, lastName);

            // If not found in memory, search database
            if (client == null) {
                String sql = "SELECT * FROM Client WHERE id = ? AND lastName = ?";
                try (var conn = DriverManager.getConnection(DB_URL);
                        var pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, cid);
                    pstmt.setString(2, lastName);
                    var rs = pstmt.executeQuery();

                    if (rs.next()) {
                        client = new Client(rs.getString("firstName"), rs.getString("lastName"),
                                rs.getInt("age"), rs.getInt("id"));
                        dbClients.addClient(client);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a valid Client ID.");
        }

        if (client == null) {
            System.out.println("Invalid client.");
            return;
        }

        System.out.println("\n============================================================");
        System.out.println("Trip History for Client: " + client.getFirstName() + " " + client.getLastName());
        System.out.println("============================================================\n");

        // Query the database for past reservations only (where departure time is before
        // now)
        String sql = "SELECT r.id as reservationID, r.connectionID, t.departureTime FROM Reservation r " +
                "JOIN Trip t ON r.tripID = t.id " +
                "WHERE r.clientID = ? AND time(t.departureTime) < time('now') " +
                "ORDER BY t.departureTime DESC";

        try (var conn = DriverManager.getConnection(DB_URL);
                var pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, client.getId());
            var rs = pstmt.executeQuery();

            int tripCount = 0;
            while (rs.next()) {
                String reservationID = rs.getString("reservationID");
                String connectionID = rs.getString("connectionID");
                String departureTime = rs.getString("departureTime");

                // Get connection details
                String connSql = "SELECT departureCity, arrivalCity, qtyStops, firstClassPrice, secondClassPrice FROM Connection WHERE id = ?";
                try (var connStmt = conn.prepareStatement(connSql)) {
                    connStmt.setString(1, connectionID);
                    var connRs = connStmt.executeQuery();

                    if (connRs.next()) {
                        String depCity = connRs.getString("departureCity");
                        String arrCity = connRs.getString("arrivalCity");
                        int stops = connRs.getInt("qtyStops");
                        int firstPrice = connRs.getInt("firstClassPrice");
                        int secondPrice = connRs.getInt("secondClassPrice");

                        System.out.println("Reservation #" + (tripCount + 1));
                        System.out.println("  Route: " + depCity + " -> " + arrCity);
                        System.out.println("  Stops: " + stops);
                        System.out.println("  First Class Price: $" + firstPrice);
                        System.out.println("  Second Class Price: $" + secondPrice);
                        System.out.println("  Departure Time: " + departureTime);
                        System.out.println("  Reservation ID: " + reservationID);
                        System.out.println();
                    }
                }

                tripCount++;
            }

            if (tripCount == 0) {
                System.out.println("No past trips found for client ID: " + client.getId());
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving trip history from database: " + e.getMessage());
        }

        System.out.println("End of trip history.");
    }

    public void selectConnection() {
        // ask for connection ID
        System.out.print("Enter the Connection ID you wish to select: ");
        String connectionID = input.nextLine();
        // clean input
        // example id 36b0ce83-df44-42b2-8a04-b26acbe0487d
        connectionID = connectionID.trim();
        this.selectedConnection = dbConnection.getConnectionById(connectionID);
        if (this.selectedConnection != null) {
            System.out.println("You have selected the following connection:");
            System.out.println(this.selectedConnection);
        } else {
            System.out.println("Connection ID not found. Please try again.");
        }

    }

    /**
     * Load all clients from the database into the dbClients object
     */
    private void loadClientsFromDatabase() {
        String sql = "SELECT * FROM Client";
        try (var conn = DriverManager.getConnection(DB_URL);
                var stmt = conn.createStatement();
                var rs = stmt.executeQuery(sql)) {
            int clientCount = 0;
            while (rs.next()) {
                int id = rs.getInt("id");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                int age = rs.getInt("age");

                Client client = new Client(firstName, lastName, age, id);
                dbClients.addClient(client);
                clientCount++;
            }
            if (clientCount > 0) {
                System.out.println("\u001B[32mLoaded " + clientCount + " existing clients from database.\u001B[0m");
            }
        } catch (SQLException e) {
            System.err.println("Error loading clients from database: " + e.getMessage());
        }
    }

    private void saveClientsToDatabase() {
        // Get all clients from in-memory storage
        HashSet<Client> allClients = dbClients.getClients();

        if (allClients.isEmpty()) {
            System.out.println("No clients to save.");
            return;
        }

        // Insert or update clients in the database
        String sql = "INSERT OR REPLACE INTO Client(id, firstName, lastName, age) VALUES(?, ?, ?, ?)";

        try (var conn = DriverManager.getConnection(DB_URL);
                var pstmt = conn.prepareStatement(sql)) {

            int clientsSaved = 0;
            for (Client client : allClients) {
                pstmt.setInt(1, client.getId());
                pstmt.setString(2, client.getFirstName());
                pstmt.setString(3, client.getLastName());
                pstmt.setInt(4, client.getAge());
                pstmt.executeUpdate();
                clientsSaved++;
            }

            if (clientsSaved > 0) {
                System.out.println("\u001B[32mSuccessfully saved " + clientsSaved + " clients to database.\u001B[0m");
            }
        } catch (SQLException e) {
            System.err.println("\u001B[31mError saving clients to database: " + e.getMessage() + "\u001B[0m");
            e.printStackTrace();
        }
    }

}
