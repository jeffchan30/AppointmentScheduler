package model;

public class Users {

    private String username;
    private String userID;
    /** User class */

    public Users(){};

    /** This method gets the username*/

    public String getUsername() {
        return username;
    }

    /** This method sets the username*/


    public void setUsername(String username) {

        this.username = username;
    }

    /** This method gets user ID*/


    public String getUserID() {
        return userID;
    }

    /** This method sets user ID*/


    public void setUserID(String userID) {
        this.userID = userID;
    }
}
