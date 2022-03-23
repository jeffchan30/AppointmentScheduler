package controller;

import helper.DBQuery;
import helper.JDBC;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.UsersDB;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import static model.Alerts.emptyTextField;

/** This class initializes the Add Customer window. */

public class AddCustomerController implements Initializable {

    @FXML
    private TextField customerIDText;

    @FXML
    private TextField customerNameText;

    @FXML
    private TextField addressText;

    @FXML
    private TextField postalCodeText;

    @FXML
    private TextField phoneNumberText;

    @FXML
    private ComboBox<String> stateProvinceComboBox;

    @FXML
    private ComboBox<String> countryComboBox;

    Stage stage;
    Parent scene;

    /** Shows states or provinces depending on the country selected */
   public void onCountrySelected(){

        Integer currentCountry = countryComboBox.getSelectionModel().getSelectedIndex();

        if(currentCountry == 0) {
            stateProvinceComboBox.setItems(DBQuery.getUsStateProvince());
            System.out.println("Got US states");
        } else if (currentCountry == 1){
            stateProvinceComboBox.setItems(DBQuery.getUkStateProvince());
            System.out.println("Got UK states");
        }else if(currentCountry == 2){
            stateProvinceComboBox.setItems(DBQuery.getCanadaStateProvince());
            System.out.println("Got Canada states");
        }

    }


    /** This method saves a customer when the Save button is clicked.
     @param actionEvent Saves customer and returns to Main Menu.
     */

    public void onActionSaveAddCustomerButton(ActionEvent actionEvent) throws SQLException, IOException {

            int customerID = Integer.parseInt(customerIDText.getText());
            String customerName = customerNameText.getText();
            String address = addressText.getText();
            String zipCode = postalCodeText.getText();
            String phoneNumber = phoneNumberText.getText();
            String stateProvince = stateProvinceComboBox.getSelectionModel().getSelectedItem();
            String country = countryComboBox.getSelectionModel().getSelectedItem();

            if(customerNameText.getText().isEmpty() ||  addressText.getText().isEmpty() || postalCodeText.getText().isEmpty() || phoneNumberText.getText().isEmpty()
                   || stateProvinceComboBox.getValue() == null || countryComboBox.getValue()== null) {
                if(customerName.isEmpty()) {
                    emptyTextField("Name");
                }
                if(address.isEmpty()) {
                    emptyTextField("Address");
                }
                if(zipCode.isEmpty()) {
                    emptyTextField("Postal Code");
                }
                if(phoneNumber.isEmpty()) {
                    emptyTextField("Phone Number");
                }
                if(stateProvince == null) {
                    emptyTextField("State or province");
                }
                if(country==null) {
                    emptyTextField("Country");
                }
            }else
            {

                String sqlDivionID = "SELECT Division_ID FROM client_schedule.first_level_divisions WHERE Division=?";
                int divisionID = 0;
                try {
                    PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(sqlDivionID);
                    preparedStatement.setString(1, stateProvinceComboBox.getSelectionModel().getSelectedItem());
                    ResultSet resultSet = preparedStatement.executeQuery();
                     while (resultSet.next()) {
                        divisionID = Integer.valueOf(resultSet.getInt("Division_ID"));

                       }

                    resultSet.next();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {

                    String insertStatement = "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) VALUES (?,?,?,?,?,?,?,?,?) ";

                    PreparedStatement ps = JDBC.getConnection().prepareStatement(insertStatement, Statement.RETURN_GENERATED_KEYS);

                    String Create_Date = "2021-09-17 00:00:00";
                    String Created_By = UsersDB.getCurrentUser();
                    String Last_Update = "2021-09-17 00:00:00";
                    String Last_Updated_By = UsersDB.getCurrentUser();

                    ps.setString(1, customerName);
                    ps.setString(2, address);
                    ps.setString(3, zipCode);
                    ps.setString(4, phoneNumber);
                    ps.setString(5, Create_Date);
                    ps.setString(6, Created_By);
                    ps.setString(7, Last_Update);
                    ps.setString(8, Last_Updated_By);
                    ps.setInt(9, divisionID);
                    ps.execute();

                    Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                    scene = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
                    stage.setScene(new Scene(scene));
                    stage.show();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
   }


    /** This method closes Add Customer window when the Close button is clicked.
     @param actionEvent Cancels Add Customer and returns to Main Menu.
     */
    public void onActionCloseAddCustomerButton(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /** Allows the user to add customers. Includes a lambda function for the Country Combo Box*/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Integer count=0;
        String sql= "SELECT Customer_ID FROM client_schedule.customers ORDER BY Customer_ID DESC LIMIT 1";
        try{
            preparedStatement = JDBC.getConnection().prepareStatement(sql);
            resultSet= preparedStatement.executeQuery();
            while(resultSet.next()) {
                count = Integer.valueOf(resultSet.getString("Customer_ID"));
            }


            count++;
            customerIDText.setText(String.valueOf(count));

            }catch (Exception e){
            e.printStackTrace();
            }


        countryComboBox.setItems(DBQuery.getCountries());

         countryComboBox.valueProperty().addListener(
         (ObservableValue<? extends String> observableValue, String s, String t1 ) -> {
                                                                    onCountrySelected();
         }
         );
        customerIDText.setEditable(false);

    }
}
