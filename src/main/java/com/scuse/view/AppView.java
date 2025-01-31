package com.scuse.view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AppView {
    private final VBox constraintsBox;
    private final VBox objectiveBox;
    private VBox resultsBox;
    private final MenuBar menuBar;
    private TextField variablesTextField;
    private TextField constraintsTextField;
    private ComboBox<String> optimizationChoiceBox;
    private Button updateButton;
    private Button solveButton;
    private static Alert alert;

    public AppView(Stage primaryStage) {
        primaryStage.setTitle("Simplex Calculator");

        // 创建菜单栏
        menuBar = createMenuBar();

        // 主界面布局
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(menuBar);

        VBox topBox = new VBox(10);
        topBox.setPadding(new Insets(10));

        HBox inputBox = createInputBox();
        topBox.getChildren().addAll(inputBox, updateButton);

        constraintsBox = new VBox(10);
        constraintsBox.setPadding(new Insets(10));
        topBox.getChildren().add(constraintsBox);

        objectiveBox = new VBox(10);
        objectiveBox.setPadding(new Insets(10));
        topBox.getChildren().add(objectiveBox);

        resultsBox = new VBox(10);
        resultsBox.setPadding(new Insets(10));
        resultsBox.setAlignment(Pos.CENTER);
        topBox.getChildren().add(resultsBox);
        mainLayout.setCenter(topBox);

        HBox bottomBox = createBottomBox();
        mainLayout.setBottom(bottomBox);

        Scene scene = new Scene(mainLayout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        fileMenu.getItems().addAll(new MenuItem("New"), new MenuItem("Open"), new MenuItem("Save"), new MenuItem("Exit"));
        Menu helpMenu = new Menu("Help");
        helpMenu.getItems().addAll(new MenuItem("Feedback"), new MenuItem("About"));
        menuBar.getMenus().addAll(fileMenu, helpMenu);
        return menuBar;
    }

    private HBox createInputBox() {
        HBox inputBox = new HBox(20);
        inputBox.setPadding(new Insets(10));

        HBox variablesBox = new HBox(10);
        Label variablesLabel = new Label("Number of Variables:");
        variablesTextField = new TextField();
        variablesTextField.setPromptText("Enter number of variables");
        variablesBox.getChildren().addAll(variablesLabel, variablesTextField);

        HBox constraintsBox = new HBox(10);
        Label constraintsLabel = new Label("Number of Constraints:");
        constraintsTextField = new TextField();
        constraintsTextField.setPromptText("Enter number of constraints");
        constraintsBox.getChildren().addAll(constraintsLabel, constraintsTextField);

        inputBox.getChildren().addAll(variablesBox, constraintsBox);

        updateButton = new Button("Update Equations");
        return inputBox;
    }

    private HBox createBottomBox() {
        HBox bottomBox = new HBox(10);
        bottomBox.setPadding(new Insets(10));
        bottomBox.setAlignment(Pos.CENTER);

        HBox optimizationBox = new HBox(10);
        Label optimizationLabel = new Label("Optimization:");
        optimizationChoiceBox = new ComboBox<>();
        optimizationChoiceBox.getItems().addAll("Maximize", "Minimize");
        optimizationChoiceBox.getSelectionModel().selectFirst();

        optimizationBox.getChildren().addAll(optimizationLabel, optimizationChoiceBox);
        solveButton = new Button("Solve");

        bottomBox.getChildren().addAll(optimizationBox, solveButton);
        return bottomBox;
    }

    public VBox getConstraintsBox() {
        return constraintsBox;
    }

    public TextField getVariablesTextField() {
        return variablesTextField;
    }

    public TextField getConstraintsTextField() {
        return constraintsTextField;
    }

    public ComboBox<String> getOptimizationChoiceBox() {
        return optimizationChoiceBox;
    }

    public Button getUpdateButton() {
        return updateButton;
    }

    public Button getSolveButton() {
        return solveButton;
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }

    public VBox getResultsBox() {
        return resultsBox;
    }

    public VBox getObjectiveBox() {
        return objectiveBox;
    }

    public static Alert getAlertInstance(Alert.AlertType alertType, String title, String header, String content) {
        if (alert == null) {
            alert = new Alert(alertType);
        }
        alert.setAlertType(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
    }
}
