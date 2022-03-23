package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.UsersDB;
import static model.Alerts.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

/** Log In Controller Class */
public class LogInController {

    @FXML
    TextField usernameText;

    @FXML
    TextField passwordText;

    @FXML
    Label loginLabel;

    @FXML
    Label usernameLabel;

    @FXML
    Label passwordLabel;

    @FXML
    Label locationLabel;

    @FXML
    Button startButton;

    @FXML
    Button exitButton;

    private String alertTitle;
    private String alertHeader;
    private String alertContext;


    /** This method starts the application when user is logged on.
     @param actionEvent Tests to see if user is valid and records on a text file.
     */
    public void onActionStartButton(ActionEvent actionEvent) throws IOException {
        Parent scene;

        String usernameInput = usernameText.getText();
        String passwordInput = passwordText.getText();

        boolean validUser = UsersDB.login(usernameInput, passwordInput);

        if(validUser == true){

            String file = "src/helper/login_activity.txt";
            FileWriter fileWriter = new FileWriter(file, true) ;
            PrintWriter fileOutput = new PrintWriter(fileWriter);
            fileOutput.println(usernameInput + " successfully logged in on " + LocalDateTime.now());
            System.out.println((usernameInput + " successfully logged in on " + LocalDateTime.now()));
            fileOutput.close();
            appointmentInFifteenMinutes();

            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml/"));
            stage.setScene(new Scene(scene));
            stage.show();
        }
        else {
            String file = "src/helper/login_activity.txt";
            FileWriter fileWriter = new FileWriter(file, true) ;
            PrintWriter fileOutput = new PrintWriter(fileWriter);
            fileOutput.println("Unsuccessful login at " + LocalDateTime.now());
            System.out.println("Unsuccessful login at " + LocalDateTime.now());
            fileOutput.close();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(alertTitle);
            alert.setHeaderText(alertHeader);
            alert.setContentText(alertContext);
            alert.showAndWait();
        }
    }
    /** This method converts to local time */


    /** This method closes the application. */
    public void onActionExitButton(ActionEvent actionEvent) {
        Stage stage = (Stage)exitButton.getScene().getWindow();
        stage.close();

    }

    /** Sets the log-in screen to English or French */
    public void initialize(){

        ZoneId location = ZoneId.systemDefault();
        String locationString = location.getId();
        locationLabel.setText(locationString);
        ResourceBundle language = ResourceBundle.getBundle("helper/Language", Locale.getDefault());
        loginLabel.setText(language.getString("loginLabel"));
        usernameLabel.setText(language.getString("usernameLabel"));
        passwordLabel.setText(language.getString("passwordLabel"));
        startButton.setText(language.getString("startButton"));
        exitButton.setText(language.getString("exitButton"));
        alertTitle = (language.getString("alertTitle"));
        alertHeader = (language.getString("alertHeader"));
        alertContext = (language.getString("alertContext"));
    }
}
