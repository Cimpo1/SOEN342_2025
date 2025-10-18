import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class Terminal{

    //private attributes
    private Scanner input = new Scanner(System.in);
    private DBConnection dbConnection;
    private DBCities dbCities;
    private DBRoutes dbRoutes;
    private int sortToggle = 1;

    private HashSet<Connection> results;

    public Terminal(){
        
    }

    public Terminal(DBConnection dbConnection, DBCities dbCities, DBRoutes dbRoutes){
        this.dbConnection = dbConnection;
        this.dbCities = dbCities;
        this.dbRoutes = dbRoutes;
    }

    private void displayResults(){
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

    public void setResults(HashSet<Connection> connections){
        if(connections!=null){
            this.results=connections;
        }
    }

    public void accessTerminal(){
        // Populate the databases
        PopulateDB L = new PopulateDB();
        L.populateDatabase("docs/eu_rail_network.csv");
        this.dbConnection = L.getDbConnection();
        this.dbCities = L.getDbCities();
        this.dbRoutes = L.getDbRoutes();
        
        System.out.println("\n============================================================\n"+
                           "\t       Welcome to the Travel Terminal!\n"+
                           "============================================================\n");
        this.displayMenu();
    }
    public void displayMenu(){
        while(true){
        System.out.println("What would you like to do? \n\t1. Look for a trip\n\t2. Exit");
        String answer = input.nextLine();
            switch(answer){
                case "1": 
                    this.displaySortResults();
                    break;
                case "2": 
                    this.displayEnd();
                    break;
                default: 
                System.out.println("Woah there, this is not a valid input, please try again");
            }
        }
    }
    public void displaySortResults(){
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
        while(stayLooped){
        System.out.println("What would you like to do? \n\t1. Look for a new trip\n\t2. Sort by day of the week\n\t3. Sort by departure time\n\t4. Sort by arrival time\n\t5. Sort by train type\n\t6. Sort by first class cost\n\t7. Sort by second class cost\n\t8. Exit");
        String answer = input.nextLine();
            switch(answer){
                case "1": 
                    stayLooped = false;
                    break;
                case "2": 
                    this.selectSort("day");
                    break;
                case "3": 
                    this.selectSort("depTime");
                    break;
                case "4": 
                    this.selectSort("arrTime");
                    break;
                case "5": 
                    this.selectSort("trainType");
                    break;
                case "6": 
                    this.selectSort("1cost");
                    break;
                case "7": 
                    this.selectSort("2cost");
                    break;
                case "8": 
                    this.displayEnd();
                    break;
                default: 
                System.out.println("Woah there, this is not a valid input, please try again");
            }
        }
    }

    public void selectSort(String choice){
        ArrayList<Connection> temp = new ArrayList<Connection>(results);
        switch(choice){
            case "day":
                temp.sort((a, b) -> { return this.sortToggle * a.getDaysOfOperation().get(0).compareTo(b.getDaysOfOperation().get(0)); });
                this.sortToggle *= -1;
                break;
            case "depTime":
                temp.sort((a, b) -> { return this.sortToggle * a.getRoutes().get(0).getDepartureDateTime().compareTo(b.getRoutes().get(0).getDepartureDateTime()); });
                this.sortToggle *= -1;
                break;
            case "arrTime":
                temp.sort((a, b) -> { return this.sortToggle * a.getRoutes().get(0).getArrivalDateTime().compareTo(b.getRoutes().get(0).getArrivalDateTime()); });
                this.sortToggle *= -1;
                break;
            case "trainType":
                temp.sort((a, b) -> { return this.sortToggle * a.getRoutes().get(0).getTraintype().compareTo(b.getRoutes().get(0).getTraintype()); });
                this.sortToggle *= -1;
                break;
            case "1cost":
                temp.sort((a, b) -> { return this.sortToggle * Integer.compare(a.getFirstClassPrice(), b.getFirstClassPrice()); });
                this.sortToggle *= -1;
                break;
            case "2cost":
                temp.sort((a, b) -> { return this.sortToggle * Integer.compare(a.getSecondClassPrice(), b.getSecondClassPrice()); });
                this.sortToggle *= -1;
                break;
        }
        
        for (Connection con : temp) {
            System.out.println(con);
        }
    }

    public void displayEnd(){
        System.out.println("Thank you for using the Travel Terminal. Goodbye!");
        System.exit(1);
    }

    public void searchConnections(String departure, String arrival, String day, String depTime, String arrTime, String trainType, String firstRate, String secondRate){
        //search connection between cities
        System.out.println("Searching for connections between cities...");

        Cities depCity = dbCities.getCityByName(departure);
        Cities arrCity = dbCities.getCityByName(arrival);
        if(depCity==null || arrCity==null){
            System.out.println("One or both of the specified cities do not exist in the database.");
            return;
        }
        //NOTE: THE DAY INPUT SHOULD BE GIVEN IN THE FORMAT "DAY,DAY,..." (e.g., "Mon,Wed,Fri") OR "DAILY" OR "WEEKENDS"
        //CAPITALIZATION WILL BE HANDLED SO IT DOES NOT MATTER AT FRONT-END LEVEL

        //NOTE: THE TIME INPUT SHOULD BE GIVEN IN THE FORMAT "HH:MM" OR "HH" (e.g., "14:30" or "9")
        //IMPORTANT: DEPARTURE TIME IS THE TIME AFTER WHICH THE USER WANTS TO DEPART (E.G., IF USER INPUTS 14:00, THEY WANT TO DEPART AT 14:00 OR LATER)
        //IMPORTANT: ARRIVAL TIME IS THE TIME BEFORE WHICH THE USER WANTS TO ARRIVE (E.G., IF USER INPUTS 18:00, THEY WANT TO ARRIVE AT 18:00 OR EARLIER)

        //NOTE: THE TRAIN TYPE INPUT SHOULD BE GIVEN IN THE FORMAT "TYPE1,TYPE2,..." (e.g., "Express,Local")
        //IMPORTANT: IF TRAIN TYPE SIZE > 1, IT MEANS THE USER IS OKAY WITH ANY OF THE TYPES LISTED
        //CAPITALIZATION WILL BE HANDLED SO IT DOES NOT MATTER AT FRONT-END LEVEL

        //NOTE: THE RATES PARAMS ARE GIVEN AS STRING, THE STRING TO INT IS HANDLED IN THE DB CONNECTION CLASS
        //IMPORTANT: FIRST RATE IS THE MAXIMUM PRICE THE USER IS WILLING TO PAY FOR FIRST CLASS
        //IMPORTANT: SECOND RATE IS THE MAXIMUM PRICE THE USER IS WILLING TO PAY FOR SECOND CLASS
        HashSet<Connection> directConnections = dbConnection.getDirectConnections(depCity, arrCity, day, depTime, arrTime, trainType, firstRate, secondRate);
        HashSet<Connection> indirectConnections;
        if(directConnections != null && directConnections.size() > 0) {
            setResults(directConnections);
        }
        if(directConnections.size()==0||directConnections==null){
            indirectConnections=dbConnection.getIndirectConnections(depCity, arrCity, day, depTime, arrTime, trainType, firstRate, secondRate);
            setResults(indirectConnections);
        }
        displayResults();
    }

    public void startTripBooking(){
        // Implementation for starting a trip booking to add after interaction design
    }

    public void addReservation(String firstName, String lastName, int age, String id, String connectionID){
        // Implementation for adding a reservation to add after interaction design
    }

    public void endTripBooking(){
        // Implementation for ending a trip booking to add after interaction design
    }

    public void searchTrips(){
        // Implementation for searching trips to add after interaction design
    }

    public void viewHistoryCollection(){
        // Implementation for viewing history collection to add after interaction design
    }

}