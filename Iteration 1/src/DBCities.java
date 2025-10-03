import java.util.HashMap;

public class DBCities {
    private HashMap<String, Cities> cityMap;

    public DBCities() {
        cityMap = new HashMap<String, Cities>();
    }

    public void addCity(Cities city) {
        cityMap.put(city.getName().toLowerCase(), city);
    }

    public boolean validateCities(String departure, String destination) {
        // Check if both cities exist in the database
        boolean departureExists = cityMap.containsKey(departure.toLowerCase());
        boolean destinationExists = cityMap.containsKey(destination.toLowerCase());
        return departureExists && destinationExists;
    }

    public Cities getCityByName(String name) {
        return cityMap.get(name.toLowerCase());
    }

    public String[] getAllCityNames() {
        return cityMap.keySet().toArray(new String[0]);
    }
}
