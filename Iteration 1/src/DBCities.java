import java.util.ArrayList;

public class DBCities {
    private ArrayList<Cities> citiesList;

    public DBCities() {
        citiesList = new ArrayList<Cities>();
    }

    public void addCity(Cities city) {
        citiesList.add(city);
    }

    public boolean validateCities(String departure, String destination) {
        // Check if both cities exist in the database
        boolean departureExists = citiesList.stream().anyMatch(city -> city.getName().equalsIgnoreCase(departure));
        boolean destinationExists = citiesList.stream().anyMatch(city -> city.getName().equalsIgnoreCase(destination));
        return departureExists && destinationExists;
    }
}
