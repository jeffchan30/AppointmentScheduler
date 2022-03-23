package controller;

import helper.DBQuery;
import helper.JDBC;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Customers;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import static model.Alerts.emptyTextField;

/** This class initializes the Modify Customer window. */
public class ModifyCustomerController implements Initializable {

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

    /** Sends data from Add Customer Controller to Modify Customer Controller.
     @param customer Receives data from Add Customer table.
     */

    public void sendCustomer(Customers customer){

        customerIDText.setText(String.valueOf(customer.getCustomerID()));
        customerNameText.setText(customer.getCustomerName());
        addressText.setText(customer.getAddress());
        postalCodeText.setText(customer.getZipCode());
        phoneNumberText.setText(customer.getPhoneNumber());
        stateProvinceComboBox.setValue(customer.getStateProvince());
        countryComboBox.setValue(customer.getCountry());

    }


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



    public void onActionSaveModifyCustomerButton(ActionEvent actionEvent) throws IOException, SQLException {


            int customerID = Integer.parseInt(customerIDText.getText());
            String customerName = customerNameText.getText();
            String address = addressText.getText();
            String zipCode = postalCodeText.getText();
            String phoneNumber = phoneNumberText.getText();
            String stateProvince = stateProvinceComboBox.getSelectionModel().getSelectedItem();
            String country = countryComboBox.getSelectionModel().getSelectedItem();
        if(customerNameText.getText().isEmpty() || addressText.getText().isEmpty() || postalCodeText.getText().isEmpty() || phoneNumberText.getText().isEmpty()
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
        } else{
            String sqlDivionID= "SELECT Division_ID FROM client_schedule.first_level_divisions WHERE Division=?";
            int divisionID=0;
            try{
                PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(sqlDivionID);
                preparedStatement.setString(1,stateProvinceComboBox.getSelectionModel().getSelectedItem());
                ResultSet resultSet= preparedStatement.executeQuery();
                while(resultSet.next()) {
                    divisionID = Integer.valueOf(resultSet.getInt("Division_ID"));
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            int stateProvinceindex = divisionID;


            String updateStatement1= "UPDATE customers SET" + " Customer_Name= '" + customerName +"',Address= '"+address+"',Postal_Code= '"+zipCode+"',Phone='"+phoneNumber+"',Division_ID = '"+ stateProvinceindex + "'" + "WHERE Customer_ID= '"+customerID+"'";
            PreparedStatement ps = JDBC.getConnection().prepareStatement(updateStatement1, Statement.RETURN_GENERATED_KEYS);

            try{
                ps.execute(updateStatement1);
                Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                scene = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
                stage.setScene(new Scene(scene));
                stage.show();
            }catch(SQLException e){
                e.printStackTrace();
            }

        }


    }

    public void onActionCloseModifyCustomerButton(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /** Allows the user to modify customers. */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        countryComboBox.setItems(DBQuery.getCountries());

        countryComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                onCountrySelected();
            }
        });

        customerIDText.setEditable(false);



    }


}
