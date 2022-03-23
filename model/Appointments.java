package model;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/** This is the appointments class */
public class Appointments {

    private  String end;

    private  String start;

    private int appointmentID;

    private int userID;

    private int customerID;

    private String title;

    private String description;

    private String location;

    private String contact;

    private String type;

    private String startDate;

    private String startTime;

    private String endDate;

    private String endTime;

    /** List of all appointments */
    private static ObservableList<Appointments> allAppointments = FXCollections.observableArrayList();

    public static ObservableList<Appointments> getAllAppointments() {
        return allAppointments;
    }

    /** This method adds an appointment */

    public static void addAppointment(Appointments newAppointment){
        allAppointments.add(newAppointment);
    }

    /** Appointments constructor */


    public Appointments(int appointmentID, int userID, int customerID, String title, String description,
                        String location, String contact, String type, String startDate, String startTime, String endDate, String endTime){
        this.appointmentID = appointmentID;
        this.userID =userID;
        this.customerID = customerID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.type = type;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;

    }

    /** Appointments constructor */

    public Appointments(int appointmentID, int userID, int customerID, String title, String description,
                        String location, String contact, String type, String start, String end){
        this.appointmentID = appointmentID;
        this.userID =userID;
        this.customerID = customerID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.type = type;
        this.start = start;
        this.end= end;


    }
    /** This method deletes an appointment */

    public static boolean deleteAppointment(Appointments selectedAppointment){

        String sqlDeleteStatement= "DELETE FROM client_schedule.appointments WHERE Appointment_ID = ? ";

        try{
            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(sqlDeleteStatement);
            preparedStatement.setString(1,String.valueOf(selectedAppointment.getAppointmentID()));
            preparedStatement.executeUpdate();
            return  true;

        }catch (SQLException e){
            e.printStackTrace();
        }

        return false;
    }


    /** This method gets appointment ID */
    public int getAppointmentID() {
        return appointmentID;
    }

    /** This method sets appointment ID */
    public void setAppointmentID(int appointmentID) {
        this.appointmentID = appointmentID;
    }

    /** This method gets user ID */
    public int getUserID() {
        return userID;
    }

    /** This method sets user ID */
    public void setUserID(int userID) {
        this.userID = userID;
    }
    /** This method gets customer ID */
    public int getCustomerID() {
        return customerID;
    }
    /** This method sets customer ID */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    /** This method gets title*/
    public String getTitle() {
        return title;
    }

    /** This method sets title*/
    public void setTitle(String title) {
        this.title = title;
    }
    /** This method gets description */
    public String getDescription() {
        return description;
    }
    /** This method sets description*/
    public void setDescription(String description) {
        this.description = description;
    }
    /** This method gets location*/

    public String getLocation() {
        return location;
    }
    /** This method sets location */
    public void setLocation(String location) {
        this.location = location;
    }
    /** This method gets contact*/
    public String getContact() {
        return contact;
    }
    /** This method sets title*/
    public void setContact(String contact) {
        this.contact = contact;
    }

    /** This method gets type*/
    public String getType() {
        return type;
    }
    /** This method sets type*/
    public void setType(String type) {
        this.type = type;
    }

    /** This method gets start */
    public String getStart(){return  start;}
    /** This method gets end*/
    public String getEnd(){ return end;}
    /** This method sets start*/
    public void setStart(String start){this.start = start;}

}
