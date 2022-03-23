package model;

import helper.JDBC;

import java.sql.Connection;
import java.sql.ResultSet;
import java.time.*;
import java.time.format.DateTimeFormatter;

/** Appointments database class */
public class AppointmentsDB {
    public static Appointments appointments;

    /** This method gets title*/

    public String getTitle() {return appointments.getTitle();}

    /** This method determines whether appointment is inside business hours */

    public static boolean isInsideBusinessHours(String startTime,String endTime,String startDate,String endDate){

        // Calling Convert Eastern Time Methods
        String start = ConvertEstTime(startTime,startDate);
        String end = ConvertEstTime(endTime,endDate);
        //LocalDate myDate= LocalDate.parse(date);
        LocalTime myStartTime = LocalTime.parse(start.substring(11,16));
        LocalTime myEndTime = LocalTime.parse(end.substring(11,16));
        //LocalDate myDate = LocalDate.parse(date);

        LocalTime openingTime = LocalTime.of(7,59,59);
        LocalTime closingTime = LocalTime.of(22,01,00);

        Boolean isValidStartTime = myStartTime.isAfter(openingTime);
        Boolean isValidEndTime = myEndTime.isBefore(closingTime);

        if(isValidStartTime == true && isValidEndTime== true) {
            System.out.println(" Inside Business hours" + "Start Time" + isValidStartTime + "End Time" + isValidEndTime);
            return true;
        }else{

            return  false;
        }
    }

    /** This method converts time to Eastern time */

    public static String ConvertEstTime (String time, String date){

        LocalDate myDate= LocalDate.parse(date);
        LocalTime myTime = LocalTime.parse(time);

        LocalDateTime myLocal = LocalDateTime.of(myDate,myTime);

        ZoneId myZone = ZoneId.systemDefault();
        ZonedDateTime myZoneDateTime = ZonedDateTime.of(myLocal,myZone);

        ZoneId estZoneId = ZoneId.of("America/New_York");
        ZonedDateTime estZDT = ZonedDateTime.ofInstant(myZoneDateTime.toInstant(),estZoneId);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a");
        String datetimeString = dateFormat.format(estZDT);
        return datetimeString;

    }

    /** This method determines whether time is overlapping */

    public static boolean checkTimeOverlap(String start, String end,int customerId) {
        int customerID = customerId;
        boolean isOverLap = false;
        System.out.println(start);
        System.out.println(end);
        Connection conn;
        conn = JDBC.getConnection();
        try {


            ResultSet rsCheckOverlap = conn.createStatement().executeQuery(String.format("SELECT Start, End, Customer_Name,c.Customer_ID  FROM appointments a INNER JOIN customers c ON a.Customer_ID=c.Customer_ID " +
                    "WHERE ('%s' >= Start AND '%s' <= End) " +
                    "OR ('%s' <= Start AND '%s' >= End) " +
                    "OR ('%s' <= Start AND '%s' >= Start) " +
                    "OR ('%s' <= End AND '%s' >= End)",
                    start, start, end, end, start, end, start, end));

           if(rsCheckOverlap.next()){
            isOverLap = true;
           }


        } catch (Exception e) {
            e.printStackTrace();
            isOverLap = false;
        }
        return  isOverLap;
    }
    /** This method determines whether time is overlapping for update appointment */

    public static int checkTimeOverlapForUpdate(String start, String end,int customerId) {
        int customerID = customerId;
        int returnID =0;

        try {
            Connection conn;
            conn = JDBC.getConnection();

            ResultSet rsCheckOverlap = conn.createStatement().executeQuery((String.format("SELECT Start, End, Customer_Name,c.Customer_ID  FROM appointments a INNER JOIN customers c ON a.Customer_ID=c.Customer_ID " +
                            "WHERE ('%s' >= Start AND '%s' <= End) " +
                            "OR ('%s' <= Start AND '%s' >= End) " +
                            "OR ('%s' <= Start AND '%s' >= Start) " +
                            "OR ('%s' <= End AND '%s' >= End)",
                    start, start, end, end, start, end, start, end)));

            //rsCheckOverlap.next();
            if(rsCheckOverlap.next()){
            returnID= rsCheckOverlap.getInt("Customer_ID");}else {
                returnID = 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            //return 0;
        }
        return  returnID;
    }


    /** This method converts time zone */

    public static String ConvertTimeZone(String time,String date) {

        LocalDate myDate= LocalDate.parse(date);
        LocalTime myTime = LocalTime.parse(time);
        LocalTime myTime1 = LocalTime.now();


        LocalDateTime myLocal = LocalDateTime.of(myDate,myTime);
        LocalDateTime myLocal1 = LocalDateTime.of(myDate,myTime1);

        ZoneId myZone = ZoneId.systemDefault();
        ZonedDateTime myZoneDateTime = ZonedDateTime.of(myLocal,myZone);

        ZoneId utcZoneId = ZoneId.of("UTC");
        ZonedDateTime utcZDT = ZonedDateTime.ofInstant(myZoneDateTime.toInstant(),utcZoneId);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String  datetimeString = dateFormat.format(utcZDT);
        return  datetimeString;


    }


}
