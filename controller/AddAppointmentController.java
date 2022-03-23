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
import model.AppointmentsInterface;
import model.UsersDB;
import model.AppointmentsDB;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import static model.Alerts.emptyTextField;
import static model.UsersDB.currentUser;

/** This class initializes the Add Controller window */
public class AddAppointmentController implements Initializable {

    int count=0;
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

    /** List of times */
    private final ObservableList<String> times = FXCollections.observableArrayList("01:00", "01:30", "02:00", "02:30",
            "03:00", "03:30", "04:00", "04:30", "05:00", "05:30", "06:00", "06:30", "07:00", "07:30",
            "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30",
            "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00", "18:30",
            "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00", "22:30", "23:00", "24:00", "24:30");


    Stage stage;
    Parent scene;

    /** This method saves an appointment when the Save button is clicked.
     @param actionEvent Saves appointment and returns to Main Menu.
     */
    public void onActionSaveAddAppointmentButton(ActionEvent actionEvent) throws IOException {

        Integer.parseInt(appointmentIDText.getText());
        Integer.parseInt(userIDText.getText());
        int customerID = 0;
        String title = titleText.getText();
        String type = typeText.getText();
        String description = descriptionText.getText();
        String location = locationText.getText();
        int contactID = contactComboBox.getSelectionModel().getSelectedIndex() + 1;
        String startTime = startTimeComboBox.getValue();
        String endTime = endTimeComboBox.getValue();
        String startDate = null;
        String endDate = null;

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
            if (endDate == null)
                emptyTextField("End date");
        } else {
            startDate = startDateDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            endDate = endDateDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            startTime = startTimeComboBox.getValue();
            endTime = endTimeComboBox.getValue();
            AppointmentsInterface convertTime = (sTime,sDate) -> {
                LocalDate myDate= LocalDate.parse(sDate);
                LocalTime myTime = LocalTime.parse(sTime);
                LocalTime myTime1 = LocalTime.now();
                LocalDateTime myLocal = LocalDateTime.of(myDate,myTime);
                LocalDateTime myLocal1 = LocalDateTime.of(myDate,myTime1);
                ZoneId myZone = ZoneId.systemDefault();
                ZonedDateTime myZoneDateTime = ZonedDateTime.of(myLocal,myZone);
                ZoneId utcZoneId = ZoneId.of("UTC");
                ZonedDateTime utcZDT = ZonedDateTime.ofInstant(myZoneDateTime.toInstant(),utcZoneId);
                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String  datetimeString = dateFormat.format(utcZDT);
                System.out.println("Testing UTC time"+datetimeString);
                return  datetimeString;
            };
            String Start = convertTime.ConvertTimeZone(startTime,startDate);
            String End =convertTime.ConvertTimeZone(endTime,endDate);
            if (!AppointmentsDB.isInsideBusinessHours(startTime, endTime, startDate, endDate)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please choose within business hours: 8:00 a.m. to 10:00 p.m. EST", ButtonType.OK, ButtonType.CANCEL);
                alert.setHeaderText("Error");
                alert.showAndWait();
            } else {
            System.out.println(AppointmentsDB.checkTimeOverlap(Start,End,customerID));
                   if (!AppointmentsDB.checkTimeOverlap(Start, End, customerID)) {
                        try {
                            String insertStatement = "INSERT INTO appointments (Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
                            PreparedStatement ps = JDBC.getConnection().prepareStatement(insertStatement, Statement.RETURN_GENERATED_KEYS);
                            String Title = titleText.getText();
                            String Description = descriptionText.getText();
                            String Location = locationText.getText();
                            String Type = typeText.getText();
                            String Create_Date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
                            String Created_By = UsersDB.getCurrentUser();
                            String Last_Update = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
                            String Last_Updated_By = UsersDB.getCurrentUser();
                            String Customer_ID = customerIDText.getText();
                            String User_ID = userIDText.getText();
                            int Contact_ID = contactComboBox.getSelectionModel().getSelectedIndex() + 1;
                            ps.setString(1, Title);
                            ps.setString(2, Description);
                            ps.setString(3, Location);
                            ps.setString(4, Type);
                            ps.setString(5, Start);
                            ps.setString(6, End);
                            ps.setString(7, Create_Date);
                            ps.setString(8, Created_By);
                            ps.setString(9, Last_Update);
                            ps.setString(10, Last_Updated_By);
                            ps.setString(11, Customer_ID);
                            ps.setString(12, User_ID);
                            ps.setInt(13, Contact_ID);
                            ps.executeUpdate();

                            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                            scene = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
                            stage.setScene(new Scene(scene));
                            stage.show();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                } else {
                       if(AppointmentsDB.checkTimeOverlap(Start,End,customerID)) {
                           Alert alert = new Alert(Alert.AlertType.ERROR, "There are appointment conflicts:", ButtonType.OK, ButtonType.CANCEL);
                           alert.setHeaderText("Error");
                           alert.showAndWait();
                       }
                   }
            }
        }
        }


    /** This method closes Add Appointment window when the Close button is clicked.
     @param actionEvent Cancels Add Appointment and returns to Main Menu.
     */
    public void onActionCloseAddAppointmentButton(ActionEvent actionEvent) throws IOException {

        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /** Allows the user to add appointments. */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Integer count=0;
        String sql= "SELECT Appointment_ID FROM client_schedule.appointments ORDER BY Appointment_ID DESC LIMIT 1";
        try{
            preparedStatement = JDBC.getConnection().prepareStatement(sql);
            resultSet= preparedStatement.executeQuery();
            while(resultSet.next()) {
                count = Integer.valueOf(resultSet.getString("Appointment_ID"));
            }


            count++;
            appointmentIDText.setText(String.valueOf(count));

        }catch (Exception e){
            e.printStackTrace();
        }

        contactComboBox.setItems(DBQuery.getContacts());
        appointmentIDText.setEditable(false);
        userIDText.setText(currentUser.getUserID());
        userIDText.setEditable(false);
        startTimeComboBox.setItems(times);
        endTimeComboBox.setItems(times);

    }
}
