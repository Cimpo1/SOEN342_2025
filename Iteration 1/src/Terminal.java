import java.sql.Time;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;

public class Terminal{

    //private attributes
    private DBConnection dbConnection;
    private DBCities dbCities;
    private DBRoutes dbRoutes;

    private HashSet<Connection> results;

    public Terminal(){
        this.dbConnection = new DBConnection();
        this.dbCities = new DBCities();
        this.dbRoutes = new DBRoutes();
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
        //maybe populate db here
        System.out.println("Welcome to the Travel Terminal!\n");
    }

    public void end(){
        System.out.println("Thank you for using the Travel Terminal. Goodbye!");
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

}