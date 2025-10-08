import

public class Terminal{

    //private attributes
    private DBConnection dbConnection;
    private DBCities dbCities;
    private DBRoutes dbRoutes;

    public Terminal(){
        this.dbConnection = new DBConnection();
        this.dbCities = new DBCities();
        this.dbRoutes = new DBRoutes();
    }

    public void accessTerminal(){
        //maybe populate db here
        System.out.println()
    }


}