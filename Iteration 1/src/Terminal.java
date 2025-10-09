import java.sql.Time;
import java.time.format.DateTimeFormatter;

public class Terminal{

    //private attributes
    private DBConnection dbConnection;
    private DBCities dbCities;
    private DBRoutes dbRoutes;

    private Connection[] results;

    public Terminal(){
        this.dbConnection = new DBConnection();
        this.dbCities = new DBCities();
        this.dbRoutes = new DBRoutes();
    }

    public void accessTerminal(){
        //maybe populate db here
        System.out.println("Welcome to the Travel Terminal!\n");
    }

    public void end(){
        System.out.println("Thank you for using the Travel Terminal. Goodbye!");
    }

    public void searchConnection(String departure, String arrival, String day, DateTimeFormatter time){
        //search connection between cities
        System.out.println("Searching for connections between cities...");

        Cities depCity = dbCities.getCityByName(departure);
        Cities arrCity = dbCities.getCityByName(arrival);

        this.results=dbConnection.get
    }

}