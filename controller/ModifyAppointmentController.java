package controller;

import helper.DBQuery;
import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Appointments;
import model.AppointmentsDB;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import static model.Alerts.emptyTextField;

/** This class initializes the Modify Appointment window. */
public class ModifyAppointmentController implements Initializable {

    @FXML
    private TextField appointmentIDText;


    @FXML
    private TextField userIDText;

    @FXML
    private TextField customerIDText;

    @FXML
    private TextField titleText;

    @FXML
    private TextField descriptionText;

    @FXML
    private TextField locationText;

    @FXML
    private ComboBox<String> contactComboBox;

    @FXML
    private TextField typeText;

    @FXML
    private DatePicker startDateDatePicker;

    @FXML
    private DatePicker endDateDatePicker;

    @FXML
    private ComboBox<String> startTimeComboBox;

    @FXML
    private ComboBox<String> endTimeComboBox;

    /** Displays al the times */

    private final ObservableList<String> times = FXCollections.observableArrayList("01:00", "01:30", "02:00", "02:30",
            "03:00", "03:30", "04:00", "04:30", "05:00", "05:30", "06:00", "06:30", "07:00", "07:30",
            "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30",
            "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00", "18:30",
            "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00", "22:30", "23:00", "24:00", "24:30");

    Stage stage;
    Parent scene;

    public ModifyAppointmentController() {
    }

    /** Sends data from Add Appointment Controller to Modify Appointment Controller.
     @param appointment Receives data from Add Appointment table.
     */

    public void sendAppointment(Appointments appointment){

        appointmentIDText.setText(String.valueOf(appointment.getAppointmentID()));
        userIDText.setText(String.valueOf(appointment.getUserID()));
        customerIDText.setText(String.valueOf(appointment.getCustomerID()));
        titleText.setText(appointment.getTitle());
        descriptionText.setText(appointment.getDescription());
        locationText.setText(appointment.getLocation());
        contactComboBox.setValue(appointment.getContact());

        typeText.setText(String.valueOf(appointment.getType()));
        String startDate = appointment.getStart().substring(0,10);
        String endDate =appointment.getEnd().substring(0,10);
        startDateDatePicker.setValue(LocalDate.parse(startDate));
        endDateDatePicker.setValue(LocalDate.parse(endDate));
        String startTime = appointment.getStart().substring(11,19);
        String endTime = appointment.getEnd().substring(11,19);
        startTimeComboBox.setValue(startTime);
        endTimeComboBox.setValue(endTime);

    }

    /** This method saves an appointment when the Save button is clicked.
     @param actionEvent Saves appointment and returns to Main Menu.
     */
    public void onActionSaveModifyAppointmentButton(ActionEvent actionEvent) throws IOException {
        int duplicateTimeCount =0;
        int appointmentID = Integer.parseInt(appointmentIDText.getText());
        String userID = userIDText.getText();
        int customerID = Integer.parseInt(customerIDText.getText());
        //int contactID = contactComboBox.
        String title = titleText.getText();
        String type = typeText.getText();
        String description = descriptionText.getText();
        String location = locationText.getText();
        int contactID = contactComboBox.getSelectionModel().getSelectedIndex() + 1;
        String startTime = startTimeComboBox.getValue();
        String endTime = endTimeComboBox.getValue();
        String startDate = startDateDatePicker.getValue().toString();
        String endDate = endDateDatePicker.getValue().toString();

        if (customerIDText.getText().isEmpty() || titleText.getText().isEmpty() || typeText.getText().isEmpty()
                || descriptionText.getText().isEmpty() || locationText.getText().isEmpty() || contactComboBox.getValue() == null
                || startTimeComboBox.getValue() == null || endTimeComboBox.getValue() == null || startDateDatePicker.getValue() == null
                || endDateDatePicker.getValue() == null) {
            if (customerIDText.getText().isEmpty()) {
                emptyTextField("Customer ID");
            }
            if (title.isEmpty()) {
                emptyTextField("Title");
            }
            if (type.isEmpty()) {
                emptyTextField("Type");
            }
            if (description.isEmpty()) {
                emptyTextField("Description");
            }
            if (location.isEmpty()) {
                emptyTextField("Location");
            }
            if (contactComboBox.getValue() == null) {
                emptyTextField("Contact");
            }
            if (startTime == null) {
                emptyTextField("Start time");
            }
            if (endTime == null) {
                emptyTextField("End time");
            }
            if (startDate == null) {
                emptyTextField("Start date");
            }
            if (endDate == null) {
                emptyTextField("End date");

            }
        }else {
                startDate = startDateDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                endDate = endDateDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                startTime = startTimeComboBox.getValue();
                endTime = endTimeComboBox.getValue();
                String Start = AppointmentsDB.ConvertTimeZone(startTime, startDate);
                String End = AppointmentsDB.ConvertTimeZone(endTime, endDate);
                if (!AppointmentsDB.isInsideBusinessHours(startTime, endTime, startDate, endDate)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Please choose within business hours: 8:00 a.m. to 10:00 p.m. EST", ButtonType.OK, ButtonType.CANCEL);
                    alert.setHeaderText("Error");
                    alert.showAndWait();
                } else {
                        if(!AppointmentsDB.checkTimeOverlap(Start, End, customerID)){
                                try {
                                    String sqlupdateStatement = "UPDATE appointments SET Title=?,Description=?,Location=?,Type=?,Start=?,End=?,Contact_ID=? WHERE Appointment_ID=?";

                                    PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(sqlupdateStatement);
                                    preparedStatement.setString(1, title);
                                    preparedStatement.setString(2, description);
                                    preparedStatement.setString(3, location);
                                    preparedStatement.setString(4, type);
                                    preparedStatement.setString(5, Start);
                                    preparedStatement.setString(6, End);
                                    preparedStatement.setInt(7, contactID);
                                    preparedStatement.setInt(8, appointmentID);

                                    preparedStatement.executeUpdate();

                                    Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                                    scene = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
                                    stage.setScene(new Scene(scene));
                                    stage.show();

                                } catch (SQLException e) {

                                    e.printStackTrace();

                                }
                        } else {
                            duplicateTimeCount++;
                            System.out.println(customerIDText.getText().toString());
                            System.out.println(AppointmentsDB.checkTimeOverlapForUpdate(Start, End, customerID));
                            System.out.println(AppointmentsDB.checkTimeOverlap(Start,End, customerID));
                                    if ((customerID == AppointmentsDB.checkTimeOverlapForUpdate(Start, End, customerID))) {
                                        try {
                                            String sqlupdateStatement = "UPDATE appointments SET Title=?,Description=?,Location=?,Type=?,Start=?,End=?,Contact_ID=? WHERE Appointment_ID=?";

                                            PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(sqlupdateStatement);
                                            preparedStatement.setString(1, title);
                                            preparedStatement.setString(2, description);
                                            preparedStatement.setString(3, location);
                                            preparedStatement.setString(4, type);
                                            preparedStatement.setString(5, Start);
                                            preparedStatement.setString(6, End);
                                            preparedStatement.setInt(7, contactID);
                                            preparedStatement.setInt(8, appointmentID);

                                            preparedStatement.executeUpdate();

                                            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                                            scene = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
                                            stage.setScene(new Scene(scene));
                                            stage.show();

                                        } catch (SQLException e) {

                                            e.printStackTrace();

                                        }
                                    }else{
                                    Alert alert = new Alert(Alert.AlertType.ERROR, "There are appointment conflicts:", ButtonType.OK, ButtonType.CANCEL);
                                    alert.setHeaderText("Error");
                                    alert.showAndWait();
                                }
                        }

                }

            }
        }


    /** This method cancels an appointment when the Close button is clicked.
     @param actionEvent Cancels Appointment and returns to Main Menu.
     */

    public void onActionCloseModifyAppointmentButton(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /** This method initializes the Modify Appointment window. */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        contactComboBox.setItems(DBQuery.getContacts());
        startTimeComboBox.setItems(times);
        endTimeComboBox.setItems(times);
        userIDText.setEditable(false);
        customerIDText.setEditable(false);

    }
}
