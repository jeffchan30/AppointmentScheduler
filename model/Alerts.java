package model;

import helper.JDBC;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static model.UsersDB.currentUser;

/** This is the alerts class */
public class Alerts <A>{

    /** This method alerts the user if there is a blank text field */

    public static void emptyTextField(String customersInfo){

        String emptyText = String.valueOf(customersInfo);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("You have an empty field");
        alert.setContentText(customersInfo+ " cannot be empty.");
        alert.showAndWait();

    }

    /**  This method alerts the user when there is an appointment in fifteen minutes */

    public static void appointmentInFifteenAlert(String appointmentID, String start) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Appointment Alert!");
        alert.setHeaderText("You have an appointment ID of "+ appointmentID+ " at "+ start +"  within 15 minutes!");
        alert.showAndWait();
    }
    /**  This method alerts the user when there is no appointment in fifteen minutes */

    public static void noAppointment() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Attention!");
        alert.setHeaderText("You have no appointment soon!");
        alert.showAndWait();
    }

    /** This method determines whether there is an appointment in fifteen minutes*/

    public static boolean appointmentInFifteenMinutes() {
        Connection conn = JDBC.getConnection();
        try {
            ResultSet haveAnAppointment = conn.createStatement().executeQuery("SELECT Appointment_ID, Start FROM appointments WHERE User_ID ="+currentUser.getUserID()+" AND Start BETWEEN '"+ LocalDateTime.now(ZoneId.of("UTC"))+"' AND '"+LocalDateTime.now(ZoneId.of("UTC")).plusMinutes(15) +"'");

            if(haveAnAppointment.next()) {
                String appointmentID = haveAnAppointment.getString("Appointment_ID");
                String start = convertToLocalTime(haveAnAppointment.getString("Start"));
               // String end = convertToLocalTime(haveAnAppointment.getString("End"));
                appointmentInFifteenAlert(appointmentID, start);
            }
            else {
                noAppointment();
            }
            return true;
        }catch (SQLException | ParseException e) {
            e.printStackTrace();
           return false;
        }
    }
    public static String convertToLocalTime(String datetime) throws ParseException {

        String date = datetime.substring(0,10);
        String time = datetime.substring(11,19);

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        LocalTime localTime= LocalTime.parse(time);
        LocalTime AM = LocalTime.of(11,59,59);
        LocalTime PM = LocalTime.of(23,59,59);


        if(localTime.isAfter(AM) == true && localTime.isBefore(PM)== true) {
            time = time +" PM";
        }else{
            time = time + " AM";
        }

        String newDateTime = date +" "+ time;
        Date myLocalDate = formatter.parse(newDateTime);

        Instant current = myLocalDate.toInstant();
        // System.out.println(current);
        DateTimeFormatter formatTimeNow=DateTimeFormatter.ofPattern("hh:mm:ss a");
        LocalDateTime myLocal = LocalDateTime.ofInstant(current, ZoneId.systemDefault());

        // System.out.println(ZoneId.systemDefault());
        myLocal.format(formatTimeNow);
        //  System.out.println("my Local " + myLocal.format(formatTimeNow).toString());
        ZoneId myZone = ZoneId.systemDefault();

        ZoneId utcZoneId = ZoneId.of("UTC");
        ZonedDateTime myZoneDateTime = ZonedDateTime.of(myLocal, utcZoneId);
        ZonedDateTime utcZDT = ZonedDateTime.ofInstant(myZoneDateTime.toInstant(), utcZoneId);

        myZoneDateTime = ZonedDateTime.ofInstant(utcZDT.toInstant(), myZone);
        // myZoneDateTime = ZonedDateTime.ofInstant(myLocalDate.toInstant(), myZone);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String strDateTime = dateTimeFormatter.format(myZoneDateTime);
        // System.out.println("LOcal Time is"+strDateTime);
        return strDateTime;
    }
}
