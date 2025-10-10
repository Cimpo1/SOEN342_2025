import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Routes {
    private String routeID;
    private Cities departureCity;
    private Cities arrivalCity;
    private Duration tripDuration;
    private LocalTime departureDateTime;
    private LocalTime arrivalDateTime;
    private String traintype;
    private ArrayList<String> daysofoperation;
    private int firstClassPrice;
    private int secondClassPrice;

    public Routes(String routeID, Cities departureCity, Cities arrivalCity, Duration tripDuration,
            LocalTime departureDateTime, LocalTime arrivalDateTime, String traintype,
            String days, int firstClassPrice, int secondClassPrice) {
        this.routeID = routeID;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.tripDuration = tripDuration;
        this.departureDateTime = departureDateTime;
        this.arrivalDateTime = arrivalDateTime;
        this.traintype = traintype;
        this.firstClassPrice = firstClassPrice;
        this.secondClassPrice = secondClassPrice;

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

    public Routes() {
        this.routeID = "";
        this.departureCity = null;
        this.arrivalCity = null;
        this.tripDuration = Duration.ZERO;
        this.departureDateTime = null;
        this.arrivalDateTime = null;
        this.traintype = "";
        this.daysofoperation = new ArrayList<>();
        this.firstClassPrice = 0;
        this.secondClassPrice = 0;
    }

    public String getRouteID() {
        return routeID;
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

    public LocalTime getDepartureDateTime() {
        return departureDateTime;
    }

    public LocalTime getArrivalDateTime() {
        return arrivalDateTime;
    }

    public String getTraintype() {
        return traintype;
    }

    public ArrayList<String> getDaysofoperation() {
        return daysofoperation;
    }

    public int getFirstClassPrice() {
        return firstClassPrice;
    }

    public int getSecondClassPrice() {
        return secondClassPrice;
    }

    public void setRouteID(String routeID) {
        this.routeID = routeID;
    }

    public void setDepartureCity(Cities departureCity) {
        this.departureCity = departureCity;
    }

    public void setArrivalCity(Cities arrivalCity) {
        this.arrivalCity = arrivalCity;
    }

    public void setTripDuration(Duration tripDuration) {
        this.tripDuration = tripDuration;
    }

    public void setDepartureDateTime(LocalTime departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    public void setArrivalDateTime(LocalTime arrivalDateTime) {
        this.arrivalDateTime = arrivalDateTime;
    }

    public void setTraintype(String traintype) {
        this.traintype = traintype;
    }

    public void setDaysofoperation(ArrayList<String> daysofoperation) {
        this.daysofoperation = daysofoperation;
    }

    public void setFirstClassPrice(int firstClassPrice) {
        this.firstClassPrice = firstClassPrice;
    }

    public void setSecondClassPrice(int secondClassPrice) {
        this.secondClassPrice = secondClassPrice;
    }

    // Override toString() method for better representation
    @Override
    public String toString() {
        DateTimeFormatter formatObj = DateTimeFormatter.ofPattern("HH:mm:ss");
        String arrFTime, depFTime;
        arrFTime = arrivalDateTime.format(formatObj);
        depFTime = departureDateTime.format(formatObj);

        // DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy
        // HH:mm:ss");

        return "Route ID: " + routeID + ", Departure: " + departureCity + ", Arrival: " + arrivalCity
                + ", Duration: " + tripDuration + ", Departure Time: " + depFTime
                + ", Arrival Time: " + arrFTime + ", Train Type: " + traintype
                + ", Days of Operation: " + daysofoperation + ", First Class Price: " + firstClassPrice
                + ", Second Class Price: " + secondClassPrice;
    }

}
