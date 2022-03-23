package model;

import helper.JDBC;
import java.sql.*;

/** Users database class */
public class UsersDB {

    public static Users currentUser;

    /** This method gets User ID */
    public static String getCurrentUser() {
        return currentUser.getUsername();
    }

    //Login Attempt
    /** This method checks too see if log in is password.
     @param Username Checks if username is valid.
     @param Password Checks if password is valid.
     */

   public static Boolean login(String Username, String Password) {

        try{
            Statement statement = JDBC.getConnection().createStatement();
            String loginCheck = "SELECT * FROM users WHERE User_Name='" + Username + "' AND Password='" + Password + "'";

            ResultSet rs = statement.executeQuery(loginCheck);

            if(rs.next()){
                currentUser = new Users();
                currentUser.setUsername(rs.getString("User_Name"));
                currentUser.setUserID(rs.getString("User_ID"));
                statement.close();

                return Boolean.TRUE;

            }
            else return Boolean.FALSE;
        } catch (Exception e){
            e.printStackTrace();
        }
        return true;

        }




}
