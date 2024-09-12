package com.example.assignment_01;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AnalyserApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(AnalyserApplication.class.getResource("system-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1300, 700);
        stage.setTitle("Pill and Capsule Analyser");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}