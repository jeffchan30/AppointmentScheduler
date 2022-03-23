package model;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Customers {

    private int customerID;

    private String customerName;

    private String address;

    private String zipCode;

    private String phoneNumber;

    private String stateProvince;

    private String country;

   // private int divisionID;

    private static ObservableList<Customers> allCustomers = FXCollections.observableArrayList();

    public static ObservableList<Customers> getAllCustomers() {
        return allCustomers;
    }

    public static void addCustomer(Customers newCustomer){
        allCustomers.add(newCustomer);
    }



    public Customers(int customerID, String customerName, String address, String zipCode, String phoneNumber, String stateProvince, String country){
        this.customerID = customerID;
        this.customerName = customerName;
        this.address = address;
        this.zipCode = zipCode;
        this.phoneNumber = phoneNumber;
        this.stateProvince = stateProvince;
        this.country = country;
        //this.divisionID = divisionID;

}




    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

  /*  public int getDivisionID() {
        return divisionID;
    }

    public void setDivisionID(int divisionID) {
        this.divisionID = divisionID;
    } */

    public static void updateCustomer(int id, Customers newCustomer){
        int index = 0;

        for (Customers customer : Customers.getAllCustomers()) {

            if (customer.getCustomerID() == id) {
                Customers.getAllCustomers().set(index, newCustomer);

            }
            index++;
        }

    }

    public static boolean deleteCustomer(Customers selectedCustomer){
        Connection con;
        con = JDBC.connection;
        int customer_ID= selectedCustomer.getCustomerID();
      try{

                    String sqlDeleteStatement = "DELETE FROM client_schedule.customers WHERE Customer_ID=?";

                    PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(sqlDeleteStatement);
                    preparedStatement.setInt(1, customer_ID);
                    preparedStatement.executeUpdate();
                    return true;

        }catch (SQLException e){

            e.printStackTrace();

        }

        return false;
    }


}
