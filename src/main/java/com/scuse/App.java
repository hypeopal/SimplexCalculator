package com.scuse;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class App extends Application {
    private int numVariables = 0;
    private int numConstraints = 0;

    private TextField variablesTextField;
    private TextField constraintsTextField;
    private VBox equationsBox;

    private ComboBox<String> optimizationChoiceBox;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Simplex Calculator");

        // 创建菜单栏
        MenuBar menuBar = createMenuBar();

        // 主界面布局
        BorderPane mainLayout = new BorderPane();

        // 将菜单栏放到顶部
        mainLayout.setTop(menuBar);

        VBox topBox = new VBox(10);
        topBox.setPadding(new Insets(10));

        // 输入框
        HBox inputBox = new HBox(20);
        inputBox.setPadding(new Insets(10));
        // 设置水平和垂直居中
        inputBox.setAlignment(Pos.CENTER);

        // 变量数输入
        HBox variablesBox = new HBox(10);
        Label variablesLabel = new Label("Number of Variables:");
        variablesTextField = new TextField();
        variablesTextField.setPromptText("Enter number of variables");
        variablesBox.getChildren().addAll(variablesLabel, variablesTextField);

        // 约束方程数输入
        HBox constraintsBox = new HBox(10);
        Label constraintsLabel = new Label("Number of Constraints:");
        constraintsTextField = new TextField();
        constraintsTextField.setPromptText("Enter number of constraints");
        constraintsBox.getChildren().addAll(constraintsLabel, constraintsTextField);

        // 将变量数和约束方程数输入框放入同一行
        inputBox.getChildren().addAll(variablesBox, constraintsBox);

        // 更新方程输入框
        Button updateButton = new Button("Update Equations");
        updateButton.setOnAction(e -> updateEquations(primaryStage));

        topBox.getChildren().addAll(inputBox, updateButton);

        // 方程输入框区
        equationsBox = new VBox(10);
        equationsBox.setPadding(new Insets(10));

        topBox.getChildren().add(equationsBox);

        mainLayout.setCenter(topBox);

        // 底部添加选择框和求解按钮
        HBox bottomBox = new HBox(10);
        bottomBox.setPadding(new Insets(10));
        bottomBox.setAlignment(Pos.CENTER);

        // 选择框，选择最大值还是最小值
        HBox optimizationBox = new HBox(10);
        Label optimizationLabel = new Label("Optimization:");
        optimizationChoiceBox = new ComboBox<>();
        optimizationChoiceBox.getItems().addAll("Maximize", "Minimize");
        optimizationChoiceBox.getSelectionModel().selectFirst(); // 默认选择最大值
        optimizationBox.getChildren().addAll(optimizationLabel, optimizationChoiceBox);

        // 求解按钮
        Button solveButton = new Button("Solve");
        solveButton.setOnAction(e -> solve());

        // 将选择框和求解按钮加入底部布局
        bottomBox.getChildren().addAll(optimizationBox, solveButton);

        mainLayout.setBottom(bottomBox);  // 将底部内容添加到主界面的底部

        Scene scene = new Scene(mainLayout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void extractCoefficient() {
        try {
            // 提取约束方程的系数和常数项
            for (int i = 1; i <= numConstraints; i++) {
                StringBuilder constraint = new StringBuilder();
                HBox equationBox = (HBox) equationsBox.getChildren().get(i);

                // 获取约束方程中的系数
                for (int j = 0; j < numVariables; j++) {
                    TextField coefficientField = (TextField) equationBox.getChildren().get(j);
                    String coefficient = coefficientField.getText();
                    constraint.append(coefficient).append(" * x").append(j + 1).append(" ");
                    if (j < numVariables - 1) {
                        constraint.append("+ ");
                    }
                }

                ComboBox<String> signComboBox = (ComboBox<String>) equationBox.getChildren().get(numVariables);
                String sign = signComboBox.getValue();
                // 获取常数项
                TextField constantField = (TextField) equationBox.getChildren().get(numVariables + 1);
                String constant = constantField.getText();
                constraint.append(sign).append(" ").append(constant);

                // 打印约束方程
                System.out.println("Constraint " + (i + 1) + ": " + constraint.toString());
            }

            // 提取目标函数的系数
            StringBuilder objectiveFunction = new StringBuilder();
            for (int i = 0; i < numVariables; i++) {
                TextField coefficientField = (TextField) ((HBox) equationsBox.getChildren().get(numConstraints + 2)).getChildren().get(i);
                String coefficient = coefficientField.getText();
                objectiveFunction.append(coefficient).append(" * x").append(i + 1).append(" ");
                if (i < numVariables - 1) {
                    objectiveFunction.append("+ ");
                }
            }

            // 打印目标函数
            System.out.println("Objective Function: " + objectiveFunction.toString());

            // 打印最大化/最小化目标
            String optimizationType = optimizationChoiceBox.getValue();
            System.out.println("Optimization Type: " + optimizationType);

        } catch (Exception e) {
            // 错误处理
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error in input data. Please check your inputs.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void solve() {
        extractCoefficient();
    }

    private MenuBar createMenuBar() {
        // 创建菜单栏
        MenuBar menuBar = new MenuBar();

        // 创建文件菜单
        Menu fileMenu = new Menu("File");
        MenuItem newItem = new MenuItem("New");
        MenuItem openItem = new MenuItem("Open");
        MenuItem saveItem = new MenuItem("Save");
        // 绑定点击事件
        newItem.setOnAction(e -> handleNewAction());
        openItem.setOnAction(e -> handleOpenAction());
        saveItem.setOnAction(e -> handleSaveAction());

        fileMenu.getItems().addAll(newItem, openItem, saveItem);

        // 创建帮助菜单
        Menu helpMenu = new Menu("Help");
        MenuItem feedbackItem = new MenuItem("Feedback");
        MenuItem aboutItem = new MenuItem("About");

        feedbackItem.setOnAction(e -> handleFeedbackAction());
        aboutItem.setOnAction(e -> handleAboutAction());
        helpMenu.getItems().addAll(feedbackItem, aboutItem);

        // 将菜单添加到菜单栏中
        menuBar.getMenus().addAll(fileMenu, helpMenu);

        return menuBar;
    }

    private void handleNewAction() {

    }

    private void handleOpenAction() {

    }

    private void handleSaveAction() {

    }

    private void handleFeedbackAction() {

    }

    private void handleAboutAction() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "This is a Simplex Solver GUI.", ButtonType.OK);
        alert.showAndWait();
    }

    private void updateEquations(Stage primaryStage) {
        // 清空之前的方程框
        equationsBox.getChildren().clear();

        try {
            numVariables = Integer.parseInt(variablesTextField.getText());
            numConstraints = Integer.parseInt(constraintsTextField.getText());

            Label constraintLabel = new Label("Constraint Functions:");
            equationsBox.getChildren().add(constraintLabel);
            // 生成约束方程输入框
            for (int i = 0; i < numConstraints; i++) {
                HBox equationBox = new HBox(10);
                equationBox.setPadding(new Insets(5));

                // 为每个约束方程生成输入框
                for (int j = 0; j < numVariables; j++) {
                    TextField coefficientField = new TextField();
                    coefficientField.setPromptText("a" + (i + 1) + (j + 1));
                    equationBox.getChildren().add(coefficientField);
                }

                // 符号选择框
                ComboBox<String> signComboBox = new ComboBox<>();
                signComboBox.getItems().addAll("<", "<=", "=", ">=", ">");
                signComboBox.getSelectionModel().selectFirst(); // 默认选择 "<="
                equationBox.getChildren().add(signComboBox);

                // 常数项输入框
                TextField constantField = new TextField();
                constantField.setPromptText("b" + (i + 1));
                equationBox.getChildren().add(constantField);

                equationsBox.getChildren().add(equationBox);
            }

            // 生成目标函数输入框
            Label objectiveFunctionLabel = new Label("Objective Function:");
            equationsBox.getChildren().add(objectiveFunctionLabel);
            HBox objectiveFunctionInputBox = new HBox(10);
            for (int i = 0; i < numVariables; i++) {
                TextField coefficientField = new TextField();
                coefficientField.setPromptText("x" + (i + 1));
                objectiveFunctionInputBox.getChildren().add(coefficientField);
            }
            equationsBox.getChildren().add(objectiveFunctionInputBox);

            // 动态调整窗口大小
            double newWidth = Math.max(600, 200 + numVariables * 82);
            double newHeight = Math.max(400, 265 + numConstraints * 60);
            primaryStage.setWidth(newWidth);
            primaryStage.setHeight(newHeight);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter valid numbers for variables and constraints.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
