import java.time.Duration;
import java.util.ArrayList;

public class Connection {
    private Cities departureCity;
    private Cities arrivalCity;
    private Duration tripDuration;
    private int qtyStops;
    private ArrayList<Cities> stopCities;
    private ArrayList<String> daysofoperation;
    private ArrayList<Routes> routes; // to keep track of which routes are part of this connection

    public Connection(Cities departureCity, Cities arrivalCity, Duration tripDuration, int qtyStops,
            ArrayList<Cities> stopCities, ArrayList<String> days, ArrayList<Routes> routes) {
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.tripDuration = tripDuration;
        this.qtyStops = qtyStops;
        this.stopCities = stopCities;
        this.routes = routes;
        this.daysofoperation = days;

    }

    public Connection(Cities departureCity, Cities arrivalCity, Duration tripDuration, String days, Routes route) {
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.tripDuration = tripDuration;
        this.qtyStops = 0;
        this.stopCities = new ArrayList<Cities>();
        this.routes = new ArrayList<Routes>();
        this.routes.add(route);

        // [MON, TUE, WED, THU, FRI, SAT, SUN]
        //
        this.daysofoperation = new ArrayList<>();

        if (days.contains("-")) {
            switch (days) {
                case "Mon-Fri":
                    this.daysofoperation.add("MON");
                    this.daysofoperation.add("TUE");
                    this.daysofoperation.add("WED");
                    this.daysofoperation.add("THU");
                    this.daysofoperation.add("FRI");
                    break;

                case "Sat-Sun":
                    this.daysofoperation.add("SAT");
                    this.daysofoperation.add("SUN");
                    break;

                default:
                    break;
            }
        } else {
            if (days.equals("Daily")) {
                this.daysofoperation.add("MON");
                this.daysofoperation.add("TUE");
                this.daysofoperation.add("WED");
                this.daysofoperation.add("THU");
                this.daysofoperation.add("FRI");
                this.daysofoperation.add("SAT");
                this.daysofoperation.add("SUN");
                return;
            } else {
                String[] ops = days.split(",");
                for (String day : ops) {
                    this.daysofoperation.add(day.trim().toUpperCase());
                }
            }
        }

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

    @Override
    public String toString() {
        return "Connection from " + departureCity + " to " + arrivalCity + " with duration " + tripDuration
                + " and " + qtyStops + " stops.";
    }
}
