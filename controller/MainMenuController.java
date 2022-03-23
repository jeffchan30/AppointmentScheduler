package controller;

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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointments;
import model.Customers;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;

/** This class initializes the Main Menu window. */

public class MainMenuController implements Initializable {

    Stage stage;
    Parent scene;

    @FXML
    private TableView<Customers> customerTableView;
    @FXML
    private TableColumn<Customers, Integer> customerIDColumn;
    @FXML
    private TableColumn<Customers, String> customerNameColumn;
    @FXML
    private TableColumn<Customers, String> customerAddressColumn;
    @FXML
    private TableColumn<Customers, Integer> customerPostalCodeColumn;
    @FXML
    private TableColumn<Customers, Integer> customerPhoneNumberColumn;
    @FXML
    private TableColumn<Customers, String> customerStateProvinceColumn;
    @FXML
    private TableColumn<Customers, String> customerCountryColumn;

    @FXML
    private TableView<Appointments> appointmentTableView;
    @FXML
    private TableColumn<Appointments, Integer> apptIDColumn;
    @FXML
    private TableColumn<Appointments, String> titleColumn;
    @FXML
    private TableColumn<Appointments, String> descriptionColumn;
    @FXML
    private TableColumn<Appointments, String> locationColumn;
    @FXML
    private TableColumn<Appointments, String> contactColumn;
    @FXML
    private TableColumn<Appointments, String> typeColumn;
    @FXML
    private TableColumn<Appointments, String> startColumn;
    @FXML
    private TableColumn<Appointments, String> endColumn;
    @FXML
    private TableColumn<Appointments, Integer> appointmentCustomerIDColumn;
    @FXML
    private TableColumn<Appointments, Integer> userIDColumn;
    @FXML
    private RadioButton viewByMonthButton;
    @FXML
    private RadioButton viewAllButton;
    @FXML
    private RadioButton viewByWeekButton;
    @FXML
    private DatePicker mainMenuDatePicker;
    @FXML
    private Button deleteAppointmentButton;

    private boolean isViewByWeek;
    private boolean isViewByMonth;

    /** This method opens the Add Customer window.
     @param actionEvent Opens Add Customer window when the Add button is clicked.
     */
    @FXML
    public void onActionAddCustomer(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/AddCustomer.fxml/"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /** This method opens the Modify Customer window.
     @param event Opens Modify Customer window when the Modify button is clicked.
     */

    public void onActionModifyCustomer(ActionEvent event) throws IOException {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/ModifyCustomer.fxml"));
            loader.load();

            ModifyCustomerController MCustomerController = loader.getController();
            MCustomerController.sendCustomer(customerTableView.getSelectionModel().getSelectedItem());

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select the item to modify!", ButtonType.OK, ButtonType.CANCEL);
            alert.setHeaderText("Error");
            alert.showAndWait();
        }

    }

    /** This method opens the Add Appointment window.
     @param actionEvent Opens Add Appointment window when the Add button is clicked.
     */

    public void onActionAddAppointment(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/AddAppointment.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /** This method opens the Modify Appointment window.
     @param event Opens Modify Appointment window when the Add button is clicked.
     */

    public void onActionModifyAppointment(ActionEvent event) throws IOException {

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/ModifyAppointment.fxml"));
            loader.load();

            ModifyAppointmentController MAppointmentController = loader.getController();
            MAppointmentController.sendAppointment(appointmentTableView.getSelectionModel().getSelectedItem());

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select the item to modify!", ButtonType.OK, ButtonType.CANCEL);
            alert.setHeaderText("Error");
            alert.showAndWait();
        }
    }

    /** This method deletes an appointment from the Appointments Table.
     @param event Deletes an appointment when the Delete button is clicked.
     */

    @FXML
    public void onActionDeleteAppointment(ActionEvent event) throws IOException {

        try {
            Appointments selectedAppointment = appointmentTableView.getSelectionModel().getSelectedItem();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to delete Appointment ID: " + (selectedAppointment.getAppointmentID()) + " Type: " + (selectedAppointment.getType()) + "?", ButtonType.OK, ButtonType.CANCEL);
            alert.setTitle("Appointments");
            alert.setHeaderText("Delete");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (Appointments.deleteAppointment(selectedAppointment)) {
                    displayAppointment();
                }
            }
        } catch (Exception e) {

            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select the item to delete!", ButtonType.OK, ButtonType.CANCEL);
            alert.setHeaderText("Error");
            alert.showAndWait();

        }

    }


    /** Displays customers */
    public void displayCustomer() {
        Connection conn;
        ObservableList<Customers> customers = FXCollections.observableArrayList();
        ObservableList<Appointments> appointments = FXCollections.observableArrayList();

        try {
            customers.clear();
            appointments.clear();
            conn = JDBC.getConnection();
            ResultSet rsCustomers = conn.createStatement().executeQuery("SELECT customers.Customer_ID, customers.Customer_Name, customers.Address, customers.Postal_Code, customers.Phone, first_level_divisions.Division, countries.country FROM customers INNER JOIN first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID INNER JOIN countries ON first_level_divisions.Country_ID = countries.Country_ID ORDER BY Customer_ID");

            while (rsCustomers.next()) {
                customers.add(new Customers(rsCustomers.getInt("Customer_ID"), rsCustomers.getString("Customer_Name"), rsCustomers.getString("Address"), rsCustomers.getString("Postal_Code"), rsCustomers.getString("Phone"), rsCustomers.getString("Division"), rsCustomers.getString("country")));
            }

            customerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
            customerAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
            customerPostalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("zipCode"));
            customerPhoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
            customerStateProvinceColumn.setCellValueFactory(new PropertyValueFactory<>("stateProvince"));
            customerCountryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));

            customerTableView.setItems(customers);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** This method deletes a customer from the Customer Table.
     @param actionEvent Deletes a part when the Delete button is clicked.
     */

    @FXML
    public void onActionDeleteCustomer(ActionEvent actionEvent) {

        Connection con;
        con = JDBC.connection;
        try {
            Customers selectedCustomer = customerTableView.getSelectionModel().getSelectedItem();
            int customer_ID = selectedCustomer.getCustomerID();
            ResultSet resultSet = con.createStatement().executeQuery("SELECT client_schedule.customers.Customer_ID FROM client_schedule.customers INNER JOIN appointments ON customers.Customer_ID = appointments.Customer_ID\n" +
                    "AND client_schedule.customers.Customer_ID =" + customer_ID);
            if (resultSet.next()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "There is an appointment for this customer, are you sure you want to delete!", ButtonType.YES, ButtonType.NO);
                alert.setHeaderText("Warning!");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.YES) {
                    if (Customers.deleteCustomer(selectedCustomer)) {
                        displayCustomer();
                        displayAppointment();
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to delete this customer?", ButtonType.OK, ButtonType.CANCEL);
                alert.setTitle("Customers");
                alert.setHeaderText("Delete");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    if (Customers.deleteCustomer(selectedCustomer)) {
                        displayCustomer();
                    }
                }
            }
        } catch (Exception e) {

            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select the item to delete!", ButtonType.OK, ButtonType.CANCEL);
            alert.setHeaderText("Error");
            alert.showAndWait();
        }
    }

    /** This method initializes the main menu. */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        displayAppointment();
        displayCustomer();
        viewByMonthButton.setSelected(false);
        viewByWeekButton.setSelected(false);
        viewAllButton.setSelected(true);

    }

    /** This method displays appointments. */
    public void displayAppointment() {
        Connection conn;

        ObservableList<Appointments> appointments = FXCollections.observableArrayList();
        try {
            appointments.clear();
            conn = JDBC.getConnection();

            ResultSet rsAppointments = conn.createStatement().executeQuery("SELECT appointments.Appointment_ID, appointments.Title, appointments.Description, appointments.Location, appointments.Type, contacts.Contact_Name, appointments.Start, appointments.End, customers.Customer_ID, users.User_ID FROM appointments INNER JOIN customers ON appointments.Customer_ID = customers.Customer_ID INNER JOIN contacts ON contacts.Contact_ID = appointments.Contact_ID INNER JOIN users ON users.User_ID = appointments.User_ID ORDER BY Appointment_ID");

            while (rsAppointments.next()) {
                System.out.println("Start from Database"+ rsAppointments.getString("Start"));
                String start = convertToLocalTime(rsAppointments.getString("Start"));
                String end = convertToLocalTime(rsAppointments.getString("End"));

                appointments.add(new Appointments(rsAppointments.getInt("Appointment_ID"),
                        rsAppointments.getInt("User_ID"),
                        rsAppointments.getInt("Customer_ID"),
                        rsAppointments.getString("Title"),
                        rsAppointments.getString("Description"),
                        rsAppointments.getString("Location"),
                        rsAppointments.getString("Contact_Name"),
                        rsAppointments.getString("Type"),
                        start,
                        end));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        apptIDColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        startColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        endColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
        appointmentCustomerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        userIDColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));
        appointmentTableView.getItems().clear();
        appointmentTableView.setItems(appointments);
    }

    /** This method displays appointments by month */

    public void displayAppointmentByMonthFilter() {

        isViewByWeek = false;
        isViewByMonth = true;

        appointmentTableView.getItems().clear();

        String datePicked = mainMenuDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String monthPicked = datePicked.toString().substring(5, 7);
        String yearPicked = datePicked.toString().substring(0, 4);
        Connection conn;
        ObservableList<Appointments> appointments = FXCollections.observableArrayList();
        try {
            appointments.clear();
            conn = JDBC.getConnection();
            ResultSet rsAppointments = conn.createStatement().executeQuery("SELECT Appointment_ID, Title, Description,contacts.Contact_Name, Location, Type, Start, End, appointments.Customer_ID, appointments.User_ID FROM customers INNER JOIN appointments ON customers.Customer_ID= appointments.Customer_ID INNER JOIN contacts ON contacts.Contact_ID = appointments.Contact_ID INNER JOIN users ON users.User_ID = appointments.User_ID WHERE MONTH(Start) ='"+monthPicked+"' AND YEAR(Start) = '"+yearPicked+"' ORDER BY Start;");
            while (rsAppointments.next()) {
                String start = convertToLocalTime(rsAppointments.getString("Start"));
                String end = convertToLocalTime(rsAppointments.getString("End"));

                appointments.add(new Appointments(rsAppointments.getInt("Appointment_ID"),
                        rsAppointments.getInt("User_ID"),
                        rsAppointments.getInt("Customer_ID"),
                        rsAppointments.getString("Title"),
                        rsAppointments.getString("Description"),
                        rsAppointments.getString("Location"),
                        rsAppointments.getString("Contact_Name"),
                        rsAppointments.getString("Type"),
                        start,
                        end));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        apptIDColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        startColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        endColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
        appointmentCustomerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        userIDColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));
        appointmentTableView.getItems().clear();
        appointmentTableView.setItems(appointments);
    }

    /** This method displays appointments by week */
    public void displayAppointmentByWeek(){
        isViewByWeek = true;
        isViewByMonth = false;
        appointmentTableView.getItems().clear();
        viewByWeekButton.setSelected(true);
        LocalDate datePicked = mainMenuDatePicker.getValue();
        String weekPicked = datePicked.toString().substring(8,10);
        String yearPicked = datePicked.toString().substring(0,4);
        WeekFields weekFields = WeekFields.of(Locale.US);
        int weekNumber = datePicked.get(weekFields.weekOfWeekBasedYear());
        String weekString = Integer.toString(weekNumber);
        Connection conn;

        ObservableList<Appointments> appointments = FXCollections.observableArrayList();
        try {
            appointments.clear();
            conn = JDBC.getConnection();
            ResultSet rsAppointments = conn.createStatement().executeQuery("SELECT Appointment_ID, Title, Description,contacts.Contact_Name, Location, Type, Start, End, appointments.Customer_ID, appointments.User_ID FROM customers INNER JOIN appointments ON customers.Customer_ID= appointments.Customer_ID INNER JOIN contacts ON contacts.Contact_ID = appointments.Contact_ID INNER JOIN users ON users.User_ID = appointments.User_ID WHERE WEEK(Start) + 1 ='"+weekString+"' AND YEAR(Start) = '"+yearPicked+"' ORDER BY Start;");
            while (rsAppointments.next()) {

                String start = convertToLocalTime(rsAppointments.getString("Start"));
                String end = convertToLocalTime(rsAppointments.getString("End"));

                appointments.add(new Appointments(rsAppointments.getInt("Appointment_ID"),
                        rsAppointments.getInt("User_ID"),
                        rsAppointments.getInt("Customer_ID"),
                        rsAppointments.getString("Title"),
                        rsAppointments.getString("Description"),
                        rsAppointments.getString("Location"),
                        rsAppointments.getString("Contact_Name"),
                        rsAppointments.getString("Type"),
                        start,
                        end));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        apptIDColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        startColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        endColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
        appointmentCustomerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        userIDColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));
        appointmentTableView.getItems().clear();
        appointmentTableView.setItems(appointments);
    }

    /** This method displays all of the appointments */
    public void displayAllAppointments(){
        isViewByWeek = false;
        isViewByMonth = false;
        displayAppointment();
    }

    /** This method converts to local time */

    public String convertToLocalTime(String datetime) throws ParseException {

        String date = datetime.substring(0,10);
        String time = datetime.substring(11,19);
        //System.out.println(" testing time " + time);
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        LocalTime localTime= LocalTime.parse(time);
        LocalTime AM = LocalTime.of(11,59,59);
        LocalTime PM = LocalTime.of(23,59,59);

       // System.out.println("Time from Database"+ datetime+ "change localtime format"+localTime.toString());
       // System.out.println(localTime.isAfter(AM) );
       // System.out.println(localTime.isBefore(PM) );
        if(localTime.isAfter(AM) == true && localTime.isBefore(PM)== true) {
            time = time +" PM";
        }else{
            time = time + " AM";
        }

        String newDateTime = date +" "+ time;
        Date myLocalDate = formatter.parse(newDateTime);
       // System.out.println(" my LocalDate test" + myLocalDate);
       // System.out.println(" am pm" + newDateTime);
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

    /** This method changes the appointment view based on date picked */
    public void onActionDatePicked(ActionEvent event) {
        if(isViewByWeek){
            displayAppointmentByWeek();
        }else if (isViewByMonth){
            displayAppointmentByMonthFilter();
        }else displayAllAppointments();
    }

    /** This method goes to the Reports view */
    public void onActionReports(ActionEvent event) throws IOException, SQLException {

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/Reports.fxml"));
        scene = loader.load();
        ReportsController controller = loader.getController();
        controller.appointmentTypesReport();
        stage.setScene(new Scene(scene));
        stage.show();
    }
}



