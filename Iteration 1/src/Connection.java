import java.time.Duration;
import java.util.ArrayList;

public class Connection {
    private Cities departureCity;
    private Cities arrivalCity;
    private Duration tripDuration;
    private int qtyStops;
    private ArrayList<Cities> stopCities;

    public Connection(Cities departureCity, Cities arrivalCity, Duration tripDuration, int qtyStops,
            ArrayList<Cities> stopCities) {
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.tripDuration = tripDuration;
        this.qtyStops = qtyStops;
    }

    public Connection(Cities departureCity, Cities arrivalCity, Duration tripDuration) {
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.tripDuration = tripDuration;
        this.qtyStops = 0;
        this.stopCities = new ArrayList<Cities>();
    }

    public Cities getDepartureCity() {
        return departureCity;
    }

    public Cities getArrivalCity() {
        return arrivalCity;
    }

    public Duration getTripDuration() {
        return tripDuration;
    }

    public int getQtyStops() {
        return qtyStops;
    }

    public ArrayList<Cities> getStopCities() {
        return stopCities;
    }

    public void addStopCity(Cities city) {
        stopCities.add(city);
        qtyStops = stopCities.size();
    }
}
