import java.util.HashMap;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;

public class DBConnection {
    private HashMap<Cities, ArrayList<Connection>> map;

    public DBConnection() {
        map = new HashMap<Cities, ArrayList<Connection>>();
    }

    public void addConnection(Connection conn) {
        Cities dep = conn.getDepartureCity();
        ArrayList<Connection> list = map.get(dep);
        if (list == null) {
            list = new ArrayList<>();
            map.put(dep, list);
        }
        list.add(conn);
    }

    public ArrayList<Connection> getConnection(Cities city) {
        ArrayList<Connection> list = map.get(city);
        return (list == null) ? new ArrayList<>() : list;
    }

    public boolean validateConnection(Cities departure, Cities destination) {
        // Check if both cities exist in the database
        boolean departureExists = map.containsKey(departure);
        boolean destinationExists = map.containsKey(destination);
        return departureExists && destinationExists;
    }

    public ArrayList<Connection> getAllConnections() {
        ArrayList<Connection> all = new ArrayList<>();
        for (ArrayList<Connection> lst : map.values()) {
            all.addAll(lst);
        }
        return all;
    }

    public ArrayList<Connection> getDirectConnections(Cities depCity, Cities arrCity, String day, String depTime, String arrTime, String trainType, String firstRate, String secondRate){
        ArrayList<Connection> directConnections = new ArrayList<>();

        //get all the connections from the given departure city
        directConnections=map.get(depCity);

        //if no connections found from the departure city, return empty list
        if(directConnections==null || directConnections.size()==0){
            System.out.println("No connections found from the departure city.");
            return new ArrayList<>();
        }
        
        //filter out the connections with stops
        for(Connection conn: directConnections){
            if(conn.getQtyStops()>0){
                directConnections.remove(conn);
            }
        }

        //filter out the connections with a different city from the arrival one
        for(Connection conn: directConnections){
            if(conn.getArrivalCity().getName().compareToIgnoreCase(arrCity.getName())!=0){
                directConnections.remove(conn);
            }
        }

        //filter out the connections that do not operate on the given day
        ArrayList<String> daysOp = new ArrayList<>();
        //but first handle the string to find the right days
        if(day.contains(",")){
            String[] ops = day.split(",");
            for (String d : ops) {
                switch (d.toUpperCase()) {
                    case "MON":
                        daysOp.add("MON");
                        break;
                    case "MONDAY":
                        daysOp.add("MON");
                        break;
                    case "TUE":
                        daysOp.add("TUE");
                        break;
                    case "TUESDAY":
                        daysOp.add("TUE");
                        break;
                    case "WED":
                        daysOp.add("WED");
                        break;
                    case "WEDNESDAY":
                        daysOp.add("WED");
                        break;
                    case "THU":
                        daysOp.add("THU");
                        break;
                    case "THURSDAY":
                        daysOp.add("THU");
                        break;
                    case "FRI":
                        daysOp.add("FRI");
                        break;
                    case "FRIDAY":
                        daysOp.add("FRI");
                        break;
                    case "SAT":
                        daysOp.add("SAT");
                        break;
                    case "SATURDAY":
                        daysOp.add("SAT");
                        break;
                    case "SUN":
                        daysOp.add("SUN");
                        break;
                    case "SUNDAY":
                        daysOp.add("SUN");
                        break;
                
                    default:
                        System.out.println("Invalid day input.");
                        break;
                }
            }
        }else {
            switch (day.toUpperCase()) {
                case "DAILY":
                    daysOp.add("MON");
                    daysOp.add("TUE");
                    daysOp.add("WED");
                    daysOp.add("THU");
                    daysOp.add("FRI");
                    daysOp.add("SAT");
                    daysOp.add("SUN");
                    break;
                case "WEEKENDS":
                    daysOp.add("SAT");
                    daysOp.add("SUN");
                    break;
            
                default:
                    break;
            }
        }

        for(Connection conn: directConnections){
            boolean operates = false;
            for(String d: daysOp){
                if(conn.getDaysOfOperation().contains(d)){
                    operates=true;
                }
            }
            if(!operates){
                directConnections.remove(conn);
            }
        }

        //filter out the connections that do not fit the time frame

        //the formatter will parse both "HH" and "HH:MM" formats
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
        .appendPattern("H")
        .optionalStart().appendLiteral(":").appendPattern("mm").optionalEnd()
        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
        .toFormatter();

        LocalTime time, connArrTime, connDepTime;

        if(depTime!=null && !depTime.isEmpty()){
            time = LocalTime.parse(depTime, formatter);
            for(Connection conn: directConnections){
                connDepTime = conn.getRoutes().get(0).getDepartureDateTime();
                if(connDepTime.isBefore(time)){
                    directConnections.remove(conn);
                }
            }
        }

        if(arrTime!=null && !arrTime.isEmpty()){
            time = LocalTime.parse(arrTime, formatter);
            for(Connection conn: directConnections){
                connArrTime = conn.getRoutes().get(conn.getRoutes().size()-1).getArrivalDateTime();
                if(connArrTime.isAfter(time)){
                    directConnections.remove(conn);
                }
            }
        }

        //filter out the connections that do not match the train type
        if(trainType.contains(",")){
            String[] tType = trainType.split(",");
            for(Connection conn: directConnections){
                boolean typeMatch=false;
                for (String t : tType) {
                    if(conn.getRoutes().get(0).getTraintype().equalsIgnoreCase(t)){
                        typeMatch=true;
                    }
                }
                if(!typeMatch){
                    directConnections.remove(conn);
                }
            }
        }
        else if(trainType!=null && !trainType.isEmpty()){
            for(Connection conn: directConnections){
                if(!conn.getRoutes().get(0).getTraintype().equalsIgnoreCase(trainType)){
                    directConnections.remove(conn);
                }
            }
        }
        //filter out the connections that do not match the max rates
        if(firstRate!=null && !firstRate.isEmpty()){
            int rate1 = Integer.parseInt(firstRate);
            for(Connection conn: directConnections){
                if(conn.getFirstClassPrice()>rate1){
                    directConnections.remove(conn);
                }
            }
        }

        if(secondRate!=null && !secondRate.isEmpty()){
            int rate2 = Integer.parseInt(secondRate);
            for(Connection conn: directConnections){
                if(conn.getSecondClassPrice()>rate2){
                    directConnections.remove(conn);
                }
            }
        }


        return directConnections;
    }

    public ArrayList<Connection> getIndirectConnections(Cities depCity, Cities arrCity, String day, String depTime, String arrTime, String trainType, String firstRate, String secondRate){
    
        ArrayList<Connection> indirectConnections = new ArrayList<>();
        //get all the connections from the given departure city
        indirectConnections=map.get(depCity);

        //if no connections found from the departure city, return empty list
        if(indirectConnections==null || indirectConnections.size()==0){
            System.out.println("No connections found from the departure city.");
            return new ArrayList<>();
        }
        
        //filter out the connections with stops
        for(Connection conn: indirectConnections){
            if(conn.getQtyStops()==0){
                indirectConnections.remove(conn);
            }
        }

        //filter out the connections that do not operate on the given day
        ArrayList<String> daysOp = new ArrayList<>();
        //but first handle the string to find the right days
        if(day.contains(",")){
            String[] ops = day.split(",");
            for (String d : ops) {
                switch (d.toUpperCase()) {
                    case "MON":
                        daysOp.add("MON");
                        break;
                    case "MONDAY":
                        daysOp.add("MON");
                        break;
                    case "TUE":
                        daysOp.add("TUE");
                        break;
                    case "TUESDAY":
                        daysOp.add("TUE");
                        break;
                    case "WED":
                        daysOp.add("WED");
                        break;
                    case "WEDNESDAY":
                        daysOp.add("WED");
                        break;
                    case "THU":
                        daysOp.add("THU");
                        break;
                    case "THURSDAY":
                        daysOp.add("THU");
                        break;
                    case "FRI":
                        daysOp.add("FRI");
                        break;
                    case "FRIDAY":
                        daysOp.add("FRI");
                        break;
                    case "SAT":
                        daysOp.add("SAT");
                        break;
                    case "SATURDAY":
                        daysOp.add("SAT");
                        break;
                    case "SUN":
                        daysOp.add("SUN");
                        break;
                    case "SUNDAY":
                        daysOp.add("SUN");
                        break;
                
                    default:
                        System.out.println("Invalid day input.");
                        break;
                }
            }
        }else {
            switch (day.toUpperCase()) {
                case "DAILY":
                    daysOp.add("MON");
                    daysOp.add("TUE");
                    daysOp.add("WED");
                    daysOp.add("THU");
                    daysOp.add("FRI");
                    daysOp.add("SAT");
                    daysOp.add("SUN");
                    break;
                case "WEEKENDS":
                    daysOp.add("SAT");
                    daysOp.add("SUN");
                    break;
            
                default:
                    break;
            }
        }

        for(Connection conn: indirectConnections){
            boolean operates = false;
            for(String d: daysOp){
                if(conn.getDaysOfOperation().contains(d)){
                    operates=true;
                }
            }
            if(!operates){
                indirectConnections.remove(conn);
            }
        }

        //filter out the connections that do not fit the time frame

        //the formatter will parse both "HH" and "HH:MM" formats
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
        .appendPattern("H")
        .optionalStart().appendLiteral(":").appendPattern("mm").optionalEnd()
        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
        .toFormatter();

        LocalTime time, connArrTime, connDepTime;

        if(depTime!=null && !depTime.isEmpty()){
            time = LocalTime.parse(depTime, formatter);
            for(Connection conn: indirectConnections){
                //check departure time of the first route of the connection
                connDepTime = conn.getRoutes().get(0).getDepartureDateTime();
                if(connDepTime.isBefore(time)){
                    indirectConnections.remove(conn);
                }
            }
        }

        if(arrTime!=null && !arrTime.isEmpty()){
            time = LocalTime.parse(arrTime, formatter);
            for(Connection conn: indirectConnections){
                //check arrival time of the last route of the connection
                connArrTime = conn.getRoutes().get(conn.getRoutes().size()-1).getArrivalDateTime();
                if(connArrTime.isAfter(time)){
                    indirectConnections.remove(conn);
                }
            }
        }

                //filter out the connections that do not match the train type
        if(trainType.contains(",")){
            String[] tType = trainType.split(",");
            for(Connection conn: indirectConnections){
                //check if each route in the connection matches at least one of the types given
                //if the boolean array has any false value, it means at least one route did not match any of the types given
                boolean[] typeMatches = new boolean[conn.getRoutes().size()];

                for(int i=0; i<typeMatches.length; i++){
                    typeMatches[i]=false;
                    for (String t : tType) {
                        //check if the route matches one of the types given
                        if(conn.getRoutes().get(i).getTraintype().equalsIgnoreCase(t)){
                            typeMatches[i]=true;
                            //no need to check other types for this route if a match is found
                            break;
                        }
                    }
                }

                //check if all routes in the connection all matched with at least one of the types given
                boolean typeMatch=true;
                for(boolean b:typeMatches){
                    if(!b){
                        typeMatch=false;
                        break;
                    }
                }
            
                //if any route did not match, remove the connection
                if(!typeMatch){
                    indirectConnections.remove(conn);
                }
            }
        }
        //if trainType is not a list, just a single type
        else if(trainType!=null && !trainType.isEmpty()){
            for(Connection conn: indirectConnections){
                //check if each route in the connection matches the type given
                //if the boolean array has any false value, it means at least one route did not match the type given
                boolean[] typeMatch = new boolean[conn.getRoutes().size()];

                for(int i=0; i<typeMatch.length; i++){
                    typeMatch[i]=false;
                    //check if the route matches the type given
                    if(conn.getRoutes().get(i).getTraintype().equalsIgnoreCase(trainType)){
                        typeMatch[i]=true;
                    }
                }

                boolean allMatch = true;
                for(boolean b: typeMatch){
                    if(!b){
                        allMatch=false;
                        break;
                    }
                }
                if(!allMatch){
                    indirectConnections.remove(conn);
                }
            }
        }

        //filter out the connections that do not match the max rates
        if(firstRate!=null && !firstRate.isEmpty()){
            for(Connection conn: indirectConnections){
                int rate = Integer.parseInt(firstRate);
                if(conn.getFirstClassPrice()>rate){
                    indirectConnections.remove(conn);
                }
            }
        }

        if(secondRate!=null && !secondRate.isEmpty()){
            for(Connection conn: indirectConnections){
                int rate = Integer.parseInt(secondRate);
                if(conn.getSecondClassPrice()>rate){
                    indirectConnections.remove(conn);
                }
            }
        }

        return indirectConnections;
    }

}
