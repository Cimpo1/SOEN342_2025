import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class Routes {
    private String routeID;
    private Cities departureCity;
    private Cities arrivalCity;
    private Duration tripDuration;
    private DateTimeFormatter departureDateTime;
    private DateTimeFormatter arrivalDateTime;
    private String traintype;
    private String daysofoperation;
    private int firstClassPrice;
    private int secondClassPrice;

    public Routes(String routeID, Cities departureCity, Cities arrivalCity, Duration tripDuration,
            DateTimeFormatter departureDateTime, DateTimeFormatter arrivalDateTime, String traintype,
            String daysofoperation, int firstClassPrice, int secondClassPrice) {
        this.routeID = routeID;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.tripDuration = tripDuration;
        this.departureDateTime = departureDateTime;
        this.arrivalDateTime = arrivalDateTime;
        this.traintype = traintype;
        this.daysofoperation = daysofoperation;
        this.firstClassPrice = firstClassPrice;
        this.secondClassPrice = secondClassPrice;
    }

    public Routes() {
        this.routeID = "";
        this.departureCity = null;
        this.arrivalCity = null;
        this.tripDuration = Duration.ZERO;
        this.departureDateTime = null;
        this.arrivalDateTime = null;
        this.traintype = "";
        this.daysofoperation = "";
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

    public DateTimeFormatter getDepartureDateTime() {
        return departureDateTime;
    }

    public DateTimeFormatter getArrivalDateTime() {
        return arrivalDateTime;
    }

    public String getTraintype() {
        return traintype;
    }

    public String getDaysofoperation() {
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

    public void setDepartureDateTime(DateTimeFormatter departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    public void setArrivalDateTime(DateTimeFormatter arrivalDateTime) {
        this.arrivalDateTime = arrivalDateTime;
    }

    public void setTraintype(String traintype) {
        this.traintype = traintype;
    }

    public void setDaysofoperation(String daysofoperation) {
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
        return "Route ID: " + routeID + ", Departure: " + departureCity + ", Arrival: " + arrivalCity
                + ", Duration: " + tripDuration + ", Departure Time: " + departureDateTime
                + ", Arrival Time: " + arrivalDateTime + ", Train Type: " + traintype
                + ", Days of Operation: " + daysofoperation + ", First Class Price: " + firstClassPrice
                + ", Second Class Price: " + secondClassPrice;
    }

}
