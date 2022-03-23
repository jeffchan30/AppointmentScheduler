package controller;

import helper.JDBC;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/** This is the Reports Controller class */
public class ReportsController {

    Parent scene;

        @FXML
        TextArea monthText;
        @FXML
        TextArea typeText;
        @FXML
        TextArea apptTotalText;
        @FXML
        TextArea apptTypesText1;
        @FXML
        TextArea schedulesContact;
        @FXML
        TextArea schedulesDescription;
        @FXML
        TextArea schedulesTitle;
        @FXML
        TextArea schedulesType;
        @FXML
        TextArea schedulesAppointmentID;
        @FXML
        TextArea schedulesStart;
        @FXML
        TextArea schedulesEnd;
        @FXML
        TextArea schedulesCustomerID;
        @FXML
        TextArea divisionText;
        @FXML
        TextArea numberOfAppointmentsText;

        /** This method shows appointments by type */

    public void appointmentTypesReport() throws SQLException {
        String query = "Select date_format(Start, '%M') as Month, Type, count(*) as TotalAppointments FROM client_schedule.appointments group by Type, month(Start);";

        Statement statement = JDBC.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        StringBuffer month = new StringBuffer();
        StringBuffer type = new StringBuffer();
        StringBuffer appointmentTotal = new StringBuffer();
        while (resultSet.next()) {

            month.append(String.format("%s\n", resultSet.getString("Month")));
            type.append(String.format("%s\n",resultSet.getString("Type")));
            appointmentTotal.append(String.format("%s\n",resultSet.getString("TotalAppointments")));

        }
        statement.close();

        monthText.setText(month.toString());
        typeText.setText(type.toString());
        apptTotalText.setText(appointmentTotal.toString());
    }

    /** This method shows the consultants' schedules */

    public void schedulesReport() throws SQLException {


        String query = "SELECT appointments.Last_Updated_By, appointments.Description, contacts.Contact_Name, appointments.Title, appointments.Type, appointments.Appointment_ID, Start, End, appointments.Customer_ID" +
                "                FROM appointments" +
                "                JOIN contacts on contacts.Contact_ID = appointments.Contact_ID" +
                "                WHERE Start>=NOW()" +
                "                GROUP BY appointments.Last_Updated_By, month(Start),Start" +
                "                ORDER BY Contact_Name, Start";


        Statement statement = JDBC.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        StringBuffer contact = new StringBuffer();
        StringBuffer description = new StringBuffer();
        StringBuffer title = new StringBuffer();
        StringBuffer type = new StringBuffer();
        StringBuffer appointmentID = new StringBuffer();
        StringBuffer start = new StringBuffer();
        StringBuffer end = new StringBuffer();
        StringBuffer customerID= new StringBuffer();
        String startString = null;
        String endString = null;

        while (resultSet.next()) {

            contact.append(String.format("%s\n", resultSet.getString("Contact_Name")));
            description.append(String.format("%s\n", resultSet.getString("Description")));
            title.append(String.format("%s\n", resultSet.getString("Title")));
            type.append(String.format("%s\n", resultSet.getString("Type")));
            appointmentID.append(String.format("%s\n", resultSet.getString("Appointment_ID")));
            try{
                startString = convertToLocalTime(resultSet.getString("Start"));
                endString = convertToLocalTime(resultSet.getString("End"));
            }catch(ParseException e){
                e.printStackTrace();
            }
            start.append(String.format("%s\n", startString));
            end.append(String.format("%s\n", endString));
            customerID.append(String.format("%s\n", resultSet.getString("Customer_ID")));

        }

        statement.close();
        schedulesContact.setText(contact.toString());
        schedulesDescription.setText(description.toString());
        schedulesTitle.setText(title.toString());
        schedulesType.setText(type.toString());
        schedulesAppointmentID.setText(appointmentID.toString());

        schedulesStart.setText(start.toString());
        schedulesEnd.setText(end.toString());
        schedulesCustomerID.setText(customerID.toString());

    }

    /** Convert to Local Time */
    public String convertToLocalTime(String datetime) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        DateFormat dateOnly = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat timeOnly = new SimpleDateFormat("hh:mm:ss");
        String date = dateOnly.format(dateFormat.parse(datetime));
        String time = timeOnly.format(dateFormat.parse(datetime));
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        LocalTime localTime= LocalTime.parse(time);
        LocalTime AM = LocalTime.of(11,59,59);
        LocalTime PM = LocalTime.of(23,59,59);
        if(localTime.isAfter(AM) == true || localTime.isBefore(PM)== true) {
            time = time +" PM";
        }
        String newDateTime = date + " "+ time;
        Date myLocalDate = formatter.parse(newDateTime);

        Instant current = myLocalDate.toInstant();
        LocalDateTime myLocal = LocalDateTime.ofInstant(current, ZoneId.systemDefault());
        ZoneId myZone = ZoneId.systemDefault();
        ZoneId utcZoneId = ZoneId.of("UTC");

        ZonedDateTime myZoneDateTime = ZonedDateTime.of(myLocal, utcZoneId);
        ZonedDateTime utcZDT = ZonedDateTime.ofInstant(myZoneDateTime.toInstant(), utcZoneId);
        myZoneDateTime = ZonedDateTime.ofInstant(utcZDT.toInstant(), myZone);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String strDateTime = dateTimeFormatter.format(myZoneDateTime);
        return strDateTime;
    }

    /** This method shows appointments by state and province */

    public void divisionAppointments() throws SQLException {
        String query = "Select Division, Appointment_ID, count(*) as TotalAppointmentsByDivision FROM client_schedule.appointments as a INNER JOIN client_schedule.customers as c ON a.Customer_ID = c.Customer_ID INNER JOIN client_schedule.first_level_divisions as f ON c.Division_ID=f.Division_ID  GROUP BY Division";

        Statement statement = JDBC.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        StringBuffer division = new StringBuffer();
        StringBuffer totalAppointmentsByDivision = new StringBuffer();


        while (resultSet.next()) {
            division.append(String.format("%s\n", resultSet.getString("Division")));
            totalAppointmentsByDivision.append(String.format("%s\n", resultSet.getString("totalAppointmentsByDivision")));
        }
        statement.close();
        divisionText.setText(division.toString());
        numberOfAppointmentsText.setText(totalAppointmentsByDivision.toString());
    }


    /** This method returns to the Main Menu when Back button is pressed */
    public void reportsBackButton(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

}
