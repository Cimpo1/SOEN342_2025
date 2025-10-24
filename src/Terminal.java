import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.time.LocalTime;

public class Terminal {

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
        // Populate the databases
        PopulateDB L = new PopulateDB();
        L.populateDatabase("docs/eu_rail_network.csv");
        this.dbConnection = L.getDbConnection();
        this.dbCities = L.getDbCities();
        this.dbRoutes = L.getDbRoutes();
        this.dbTrips = new DBTrips();
        this.dbReservations = new DBReservation();
        this.dbTickets = new DBTicket();
        this.dbClients = new DBClient();

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
        System.out.println("Thank you for using the Travel Terminal. Goodbye!");
        System.exit(1);
    }

    public void searchConnections(String departure, String arrival, String day, String depTime, String arrTime,
            String trainType, String firstRate, String secondRate) {
        // search connection between cities
        System.out.println("Searching for connections between cities...");

        Cities depCity = dbCities.getCityByName(departure);
        Cities arrCity = dbCities.getCityByName(arrival);
        if (depCity == null || arrCity == null) {
            System.out.println("One or both of the specified cities do not exist in the database.");
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
        System.out.println("New trip created with ID: " + this.currentTrip.getId());

    }

    public void addReservation(String firstName, String lastName, int age, int id) {
        // Implementation for adding a reservation to add after interaction design

        if (this.currentTrip == null) {
            System.out.println("No active trip. Please start a trip booking first.");
            return;
        }
        // get client
        Client client = dbClients.getClientById(id);
        if (client == null) {
            // create new client
            client = dbClients.addClient(firstName, lastName, age, id);
            System.out.println("New client created with ID: " + client.getId());
        }

        // check if client already has a reservation for this trip
        if (this.currentTrip.getReservations() != null) {
            for (Reservation r : this.currentTrip.getReservations()) {
                if (r.getClient().getId() == client.getId()) {
                    System.out.println("Client already has a reservation for this trip.");
                    return;
                }
            }
        }
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
        
        client = dbClients.getClientByIdAndLName(cid, lastName);
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a valid Client ID.");
        }

        if (client == null) {
            System.out.println("Invalid client.");
            return;
        }

        HashSet<Trip> trips = dbClients.getTripsByClient(client);
        if (trips.isEmpty()) {
            System.out.println("No trips found for client ID: " + client.getId());
        } else {
            System.out.println("Trips found for client ID: " + client.getId());
            for (Trip trip : trips) {
                // display only if trip is for today or future
                if (trip.getDepartureTime().isAfter(LocalTime.now())
                        || trip.getDepartureTime().equals(LocalTime.now())) {
                    System.out.println(trip);
                }
            }
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
        client = dbClients.getClientByIdAndLName(cid, lastName);
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a valid Client ID.");
        }

        if (client == null) {
            System.out.println("Invalid client.");
            return;
        }
        System.out.println("Trip history for client ID: " + client.getId());
        for (Trip trip : client.getTrips()) {
            // print only past trips
            if (trip.getDepartureTime().isBefore(LocalTime.now())) {
                System.out.println(trip);
            }
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

}