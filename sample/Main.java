package sample;

import com.mysql.cj.x.protobuf.MysqlxPrepare;
import helper.DBQuery;
import helper.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.*;
import java.time.*;
import java.util.Locale;
import java.util.Scanner;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../view/LogIn.fxml"));
        primaryStage.setTitle("Appointment Management System");
        primaryStage.setScene(new Scene(root, Color.WHITE));
        primaryStage.show();
    }

/** Main method */
    public static void main(String[] args) throws SQLException {

        JDBC.openConnection();

        launch(args);

        JDBC.closeConnection();


    }
}
