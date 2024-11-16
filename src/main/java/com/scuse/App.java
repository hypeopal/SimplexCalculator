package com.scuse;

import javafx.application.Application;
import javafx.stage.Stage;
import com.scuse.controller.AppController;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        new AppController(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}