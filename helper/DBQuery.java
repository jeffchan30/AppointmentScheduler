package helper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

/** DBQuery class */

public class DBQuery {

    private static String query;
    private static Statement stmnt;
    private static ResultSet result;

    static ObservableList<String> usStateProvince = FXCollections.observableArrayList();
    static ObservableList<String> canadaStateProvince = FXCollections.observableArrayList();
    static ObservableList<String> ukStateProvince = FXCollections.observableArrayList();
    static ObservableList<String> countries = FXCollections.observableArrayList();
    static ObservableList<String> contacts = FXCollections.observableArrayList();

    private static PreparedStatement statement;

    public static void setPreparedStatement(Connection conn, String sqlStatement) throws SQLException {

        statement = conn.prepareStatement(sqlStatement);

    }

    public static PreparedStatement getPreparedStatement(){

        return statement;
    }
    /** Gets U.S. states */
    public static ObservableList<String> getUsStateProvince() {
        try {
            Connection conn = JDBC.getConnection();
            ResultSet usStateProvinceList = conn.createStatement().executeQuery("SELECT Division FROM first_level_divisions LIMIT 51");
            while (usStateProvinceList.next()) {
                usStateProvince.add(usStateProvinceList.getString("Division"));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return usStateProvince;

    }

    /** Gets Canadian provinces */
    public static ObservableList<String> getCanadaStateProvince() {
        try {
            Connection conn = JDBC.getConnection();
            ResultSet canadaStateProvinceList = conn.createStatement().executeQuery("SELECT Division FROM first_level_divisions LIMIT 51,13");
            while (canadaStateProvinceList.next()) {
                canadaStateProvince.add(canadaStateProvinceList.getString("Division"));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return canadaStateProvince;

    }

    /** Gets UK regions */

    public static ObservableList<String> getUkStateProvince() {
        try {
            Connection conn = JDBC.getConnection();
            ResultSet ukStateProvinceList = conn.createStatement().executeQuery("SELECT Division FROM first_level_divisions LIMIT 64,4");
            while (ukStateProvinceList.next()) {
                ukStateProvince.add(ukStateProvinceList.getString("Division"));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return ukStateProvince;

    }

    /** Gets countries */

    public static ObservableList<String> getCountries(){
        try {
            countries.removeAll(countries);
            Connection conn = JDBC.getConnection();
            ResultSet countriesList = conn.createStatement().executeQuery("SELECT Country FROM countries");
            while (countriesList.next()) {
                countries.add(countriesList.getString("Country"));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return countries;


    }

    /** Gets contacts */

    public static ObservableList<String> getContacts(){
        try {
            contacts.removeAll(contacts);
            Connection conn = JDBC.getConnection();
            ResultSet contactsList = conn.createStatement().executeQuery("SELECT Contact_Name FROM contacts");
            while (contactsList.next()) {
                contacts.add(contactsList.getString("Contact_Name"));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return contacts;
    }
}
