// Controller/AppController.java
package com.scuse.controller;

import com.scuse.model.ConstraintEquation;
import com.scuse.model.MathModel;
import com.scuse.model.ObjectiveFunction;
import com.scuse.view.AppView;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import javafx.scene.control.TextInputDialog;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AppController {
    private final AppView view;
    private final MathModel mathModel;
    private int numVariables;
    private int numConstraints;

    public AppController(Stage primaryStage) {
        view = new AppView(primaryStage);
        mathModel = new MathModel();

        view.getUpdateButton().setOnAction(_ -> updateEquations(primaryStage));
        view.getSolveButton().setOnAction(_ -> solve());

        for (Menu menu : view.getMenuBar().getMenus()) {
            for (MenuItem item : menu.getItems()) {
                switch (item.getText()) {
                    case "New":
                        item.setOnAction(_ -> handleNewAction());
                        break;
                    case "Open":
                        item.setOnAction(_ -> handleOpenAction());
                        break;
                    case "Save":
                        item.setOnAction(_ -> handleSaveAction());
                        break;
                    case "Feedback":
                        item.setOnAction(_ -> handleFeedbackAction());
                        break;
                    case "About":
                        item.setOnAction(_ -> handleAboutAction());
                        break;
                    case "Exit":
                        item.setOnAction(_ -> primaryStage.close());
                        break;
                }
            }
        }
    }

    private void handleNewAction() {
        clear();
        view.getVariablesTextField().clear();
        view.getConstraintsTextField().clear();
    }

    private void handleOpenAction() {

    }

    private void handleSaveAction() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Save File");
        dialog.setHeaderText("Save your model data");
        dialog.setContentText("Enter a file name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(fileName -> {
            try {
                saveDataToJson(fileName);
                System.out.println("Data saved successfully to " + fileName);
            } catch (IOException e) {
                System.err.println("Failed to save file: " + e.getMessage());
            }
        });
    }

    private void handleFeedbackAction() {

    }

    private void handleAboutAction() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "This is a Simplex Solver GUI.", ButtonType.OK);
        alert.showAndWait();
    }

    private void clear() {
        view.getEquationsBox().getChildren().clear();
        mathModel.getConstraints().clear();
        mathModel.getObjectiveFunction().clear();
    }

    private void saveDataToJson(String fileName) throws IOException {
        // 创建 JSON 对象
        JSONObject data = new JSONObject();

        // 保存目标方程
        JSONObject objectiveFunction = new JSONObject();
        List<Double> objCoefficients = mathModel.getObjectiveFunction().getCoefficients();
        objectiveFunction.put("coefficients", objCoefficients);
        objectiveFunction.put("optimization", mathModel.getObjectiveFunction().getOptimizationType());
        data.put("objectiveFunction", objectiveFunction);

        // 保存约束方程
        JSONArray constraints = new JSONArray();
        for (ConstraintEquation constraint : mathModel.getConstraints()) {
            JSONObject constraintObj = new JSONObject();
            constraintObj.put("coefficients", constraint.getCoefficients());
            constraintObj.put("symbol", constraint.getSign());
            constraintObj.put("constant", constraint.getConstant());
            constraints.put(constraintObj);
        }
        data.put("constraints", constraints);

        // 将 JSON 数据写入文件
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(data.toString(4)); // 格式化缩进为 4 个空格
        }
    }

    private void updateEquations(Stage primaryStage) {
        clear();

        try {
            numVariables = Integer.parseInt(view.getVariablesTextField().getText());
            numConstraints = Integer.parseInt(view.getConstraintsTextField().getText());
            Label constraintLabel = new Label("Constraint Functions:");
            view.getEquationsBox().getChildren().add(constraintLabel);
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

                view.getEquationsBox().getChildren().add(equationBox);
            }

            // 生成目标函数输入框
            Label objectiveFunctionLabel = new Label("Objective Function:");
            view.getEquationsBox().getChildren().add(objectiveFunctionLabel);
            HBox objectiveFunctionInputBox = new HBox(10);
            for (int i = 0; i < numVariables; i++) {
                TextField coefficientField = new TextField();
                coefficientField.setPromptText("x" + (i + 1));
                objectiveFunctionInputBox.getChildren().add(coefficientField);
            }
            view.getEquationsBox().getChildren().add(objectiveFunctionInputBox);

            // 动态调整窗口大小
            double newWidth = Math.max(600, 200 + numVariables * 82);
            double newHeight = Math.max(400, 265 + numConstraints * 60);
            primaryStage.setWidth(newWidth);
            primaryStage.setHeight(newHeight);
        } catch (NumberFormatException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter valid numbers for variables and constraints.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void extractCoefficient() {
        try {
            // 提取约束方程的系数和常数项
            for (int i = 1; i <= numConstraints; i++) {
                StringBuilder constraintString = new StringBuilder();
                HBox equationBox = (HBox) view.getEquationsBox().getChildren().get(i);
                ConstraintEquation constraint = new ConstraintEquation();

                // 获取约束方程中的系数
                for (int j = 0; j < numVariables; j++) {
                    TextField coefficientField = (TextField) equationBox.getChildren().get(j);
                    String coefficient = coefficientField.getText();
                    constraint.getCoefficients().add(Double.parseDouble(coefficient));
                    constraintString.append(coefficient).append(" * x").append(j + 1).append(" ");
                    if (j < numVariables - 1) {
                        constraintString.append("+ ");
                    }
                }

                ComboBox<String> signComboBox = (ComboBox<String>) equationBox.getChildren().get(numVariables);
                String sign = signComboBox.getValue();
                constraint.setSign(sign);
                // 获取常数项
                TextField constantField = (TextField) equationBox.getChildren().get(numVariables + 1);
                String constant = constantField.getText();
                constraint.setConstant(Double.parseDouble(constant));
                constraintString.append(sign).append(" ").append(constant);

                // 打印约束方程
                System.out.println("Constraint " + (i + 1) + ": " + constraintString);
                mathModel.addConstraint(constraint);
            }

            // 提取目标函数的系数
            StringBuilder objectiveFunctionString = new StringBuilder();
            ObjectiveFunction objectiveFunction = new ObjectiveFunction();
            for (int i = 0; i < numVariables; i++) {
                TextField coefficientField = (TextField) ((HBox) view.getEquationsBox().getChildren().get(numConstraints + 2)).getChildren().get(i);
                String coefficient = coefficientField.getText();
                objectiveFunction.getCoefficients().add(Double.parseDouble(coefficient));
                objectiveFunctionString.append(coefficient).append(" * x").append(i + 1).append(" ");
                if (i < numVariables - 1) {
                    objectiveFunctionString.append("+ ");
                }
            }

            // 打印目标函数
            System.out.println("Objective Function: " + objectiveFunctionString);

            // 打印最大化/最小化目标
            String optimizationType = view.getOptimizationChoiceBox().getValue();
            objectiveFunction.setOptimizationType(optimizationType);
            System.out.println("Optimization Type: " + optimizationType);

            mathModel.setObjectiveFunction(objectiveFunction);
        } catch (Exception e) {
            // 错误处理
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error in input data. Please check your inputs.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void solve() {
        extractCoefficient();
        mathModel.print();
    }
}
