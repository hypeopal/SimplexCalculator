package com.scuse.controller;

import com.scuse.model.*;
import com.scuse.view.AppView;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.TextInputDialog;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
        view.getSolveButton().setOnAction(_ -> solve(primaryStage));

        for (Menu menu : view.getMenuBar().getMenus()) {
            for (MenuItem item : menu.getItems()) {
                switch (item.getText()) {
                    case "New":
                        item.setOnAction(_ -> handleNewAction(primaryStage));
                        break;
                    case "Open":
                        item.setOnAction(_ -> handleOpenAction(primaryStage));
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

    private void handleNewAction(Stage primaryStage) {
        clear();
        view.getVariablesTextField().clear();
        view.getConstraintsTextField().clear();
        resize(primaryStage);
    }

    private void handleOpenAction(Stage primaryStage) {
        clear();
        loadJsonFile();
        resize(primaryStage);
    }

    private void handleSaveAction() {
        extractCoefficient();
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Save File");
        dialog.setHeaderText("Save your model data");
        dialog.setContentText("Enter a file name:");
        dialog.getEditor().appendText("data.json");
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
        // 创建对话框
        Dialog<Void> feedbackDialog = new Dialog<>();
        feedbackDialog.setTitle("Feedback");
        feedbackDialog.setHeaderText("Submit Your Feedback");

        // 问题类型选择
        VBox feedbackContent = new VBox(10);
        feedbackContent.setPadding(new Insets(10));
        feedbackContent.setAlignment(Pos.CENTER_LEFT);

        Label issueTypeLabel = new Label("Select Issue Type:");
        CheckBox uiIssueCheckBox = new CheckBox("UI Issue");
        CheckBox functionalityIssueCheckBox = new CheckBox("Functionality Issue");
        CheckBox performanceIssueCheckBox = new CheckBox("Performance Issue");
        CheckBox otherIssueCheckBox = new CheckBox("Other");

        VBox issueTypeBox = new VBox(5, uiIssueCheckBox, functionalityIssueCheckBox, performanceIssueCheckBox, otherIssueCheckBox);

        // 问题详情输入框
        Label issueDetailsLabel = new Label("Issue Details:");
        TextArea issueDetailsTextArea = new TextArea();
        issueDetailsTextArea.setPromptText("Describe your issue here...");
        issueDetailsTextArea.setPrefHeight(150);

        feedbackContent.getChildren().addAll(issueTypeLabel, issueTypeBox, issueDetailsLabel, issueDetailsTextArea);

        // 添加按钮
        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        feedbackDialog.getDialogPane().getButtonTypes().addAll(submitButtonType, cancelButtonType);

        feedbackDialog.getDialogPane().setContent(feedbackContent);

        // 设置提交按钮操作
        feedbackDialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                // 收集用户反馈数据
                StringBuilder feedbackSummary = new StringBuilder("Feedback Submitted:\n");

                if (uiIssueCheckBox.isSelected()) feedbackSummary.append("- UI Issue\n");
                if (functionalityIssueCheckBox.isSelected()) feedbackSummary.append("- Functionality Issue\n");
                if (performanceIssueCheckBox.isSelected()) feedbackSummary.append("- Performance Issue\n");
                if (otherIssueCheckBox.isSelected()) feedbackSummary.append("- Other Issue\n");

                feedbackSummary.append("Details:\n").append(issueDetailsTextArea.getText().trim());

                // 模拟提交或保存反馈
                System.out.println(feedbackSummary);

                Alert alert = AppView.getAlertInstance(Alert.AlertType.INFORMATION, "Feedback Submitted", "Thank you for your feedback!", feedbackSummary.toString());
                alert.showAndWait();
            }
            return null;
        });

        // 显示对话框
        feedbackDialog.showAndWait();
    }

    private void handleAboutAction() {
        Alert aboutAlert = AppView.getAlertInstance(Alert.AlertType.INFORMATION, "About", "Simplex Calculator", "This is a Simplex Calculator GUI.");
        aboutAlert.showAndWait();
    }

    private void clear() {
        view.getConstraintsBox().getChildren().clear();
        view.getObjectiveBox().getChildren().clear();
        view.getResultsBox().getChildren().clear();
        mathModel.getConstraints().clear();
        mathModel.getObjectiveFunction().clear();
        mathModel.getLPQ().getSolutions().clear();
    }

    private void saveDataToJson(String fileName) throws IOException {
        // 创建 JSON 对象
        JSONObject data = new JSONObject();

        // 保存变量和方程个数
        data.put("numVariables", numVariables);
        data.put("numConstraints", numConstraints);

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

        // 保存解
        JSONArray solutions = new JSONArray();
        for (Solution solution : mathModel.getLPQ().getSolutions()) {
            JSONObject solutionObj = new JSONObject();
            solutionObj.put("Variables", solution.getVariableValues());
            solutionObj.put("objectiveValue", solution.getObjectiveValue());
            solutions.put(solutionObj);
        }
        data.put("solutions", solutions);

        // 将 JSON 数据写入文件
        try (FileWriter writer = new FileWriter(fileName.contains(".json") ? fileName : fileName + ".json")) {
            writer.write(data.toString(2)); // 格式化缩进为 2 个空格
        }
    }

    private void loadJsonFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open JSON File");
        fileChooser.setInitialDirectory(new java.io.File("."));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try (FileReader reader = new FileReader(file)) {
                // 解析 JSON 数据
                JSONTokener tokener = new JSONTokener(reader);
                JSONObject data = new JSONObject(tokener);

                // 解析变量和方程个数
                numVariables = data.getInt("numVariables");
                numConstraints = data.getInt("numConstraints");

                // 解析目标方程
                JSONObject objectiveFunctionObj = data.getJSONObject("objectiveFunction");
                List<Double> objCoefficients = new ArrayList<>();
                JSONArray coeffArray = objectiveFunctionObj.getJSONArray("coefficients");
                for (int i = 0; i < coeffArray.length(); i++) {
                    objCoefficients.add(coeffArray.getDouble(i));
                }
                mathModel.getObjectiveFunction().setCoefficients(objCoefficients);
                mathModel.getObjectiveFunction().setOptimizationType(objectiveFunctionObj.getString("optimization"));

                // 解析约束方程
                JSONArray constraintsArray = data.getJSONArray("constraints");
                mathModel.getConstraints().clear();
                for (int i = 0; i < constraintsArray.length(); i++) {
                    JSONObject constraintObj = constraintsArray.getJSONObject(i);
                    List<Double> constraintCoefficients = new ArrayList<>();
                    JSONArray constraintCoeffArray = constraintObj.getJSONArray("coefficients");
                    for (int j = 0; j < constraintCoeffArray.length(); j++) {
                        constraintCoefficients.add(constraintCoeffArray.getDouble(j));
                    }

                    ConstraintEquation constraint = new ConstraintEquation();
                    constraint.setCoefficients(constraintCoefficients);
                    constraint.setSign(constraintObj.getString("symbol"));
                    constraint.setConstant(constraintObj.getDouble("constant"));

                    mathModel.addConstraint(constraint);
                }

                // 解析解
                JSONArray solutionsArray = data.getJSONArray("solutions");
                mathModel.getLPQ().getSolutions().clear();
                for (int i = 0; i < solutionsArray.length(); i++) {
                    JSONObject solutionObj = solutionsArray.getJSONObject(i);
                    List<Double> variableValues = new ArrayList<>();
                    JSONArray variableValuesArray = solutionObj.getJSONArray("Variables");
                    for (int j = 0; j < variableValuesArray.length(); j++) {
                        variableValues.add(variableValuesArray.getDouble(j));
                    }
                    mathModel.getLPQ().addSolution(solutionObj.getDouble("objectiveValue"), variableValues);
                }

                // 更新视图
                updateViewFromModel();
                Alert alert = AppView.getAlertInstance(Alert.AlertType.INFORMATION, "Success", "Opened file", "Data loaded successfully.");
                alert.showAndWait();
            } catch (Exception e) {
                Alert alert = AppView.getAlertInstance(Alert.AlertType.ERROR, "Error", "Open file failed", "Failed to load data: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    private void updateViewFromModel() {
        view.getConstraintsBox().getChildren().clear();

        // 更新变量个数
        view.getVariablesTextField().setText(String.valueOf(numVariables));
        // 更新约束个数
        view.getConstraintsTextField().setText(String.valueOf(numConstraints));
        // 更新约束方程
        Label constraintLabel = new Label("Constraint Functions(Please input coefficients in order):");
        view.getConstraintsBox().getChildren().add(constraintLabel);
        for (ConstraintEquation constraint : mathModel.getConstraints()) {
            HBox equationBox = new HBox(10);
            equationBox.setPadding(new Insets(5));

            for (Double coefficient : constraint.getCoefficients()) {
                TextField coefficientField = new TextField(coefficient.toString());
                equationBox.getChildren().add(coefficientField);
            }

            ComboBox<String> symbolBox = new ComboBox<>();
            symbolBox.getItems().addAll("<=", ">=", "=");
            symbolBox.setValue(constraint.getSign());
            equationBox.getChildren().add(symbolBox);

            TextField constantField = new TextField(String.valueOf(constraint.getConstant()));
            equationBox.getChildren().add(constantField);

            view.getConstraintsBox().getChildren().add(equationBox);
        }

        // 更新目标方程
        Label objectiveLabel = new Label("Objective Function:");
        view.getObjectiveBox().getChildren().add(objectiveLabel);
        HBox objectiveFunctionInputBox = new HBox(10);
        for (Double coefficient : mathModel.getObjectiveFunction().getCoefficients()) {
            TextField coefficientField = new TextField(coefficient.toString());
            objectiveFunctionInputBox.getChildren().add(coefficientField);
        }
        view.getObjectiveBox().getChildren().add(objectiveFunctionInputBox);
        // 更新解集
        updateResults();

        // 更新优化类型
        view.getOptimizationChoiceBox().setValue(mathModel.getObjectiveFunction().getOptimizationType());
    }

    private void updateEquations(Stage primaryStage) {
        view.getResultsBox().getChildren().clear();
        mathModel.getLPQ().clear();
        try {
            numVariables = Integer.parseInt(view.getVariablesTextField().getText());
            numConstraints = Integer.parseInt(view.getConstraintsTextField().getText());
            // 更新约束方程部分
            ObservableList<Node> constraintsChildren = view.getConstraintsBox().getChildren();

            // 检查是否需要增加约束方程标题
            if (constraintsChildren.isEmpty()) {
                Label constraintLabel = new Label("Constraint Functions (Please input coefficients in order):");
                view.getConstraintsBox().getChildren().add(constraintLabel);
            }
            // 生成约束方程输入框
            int existingConstraints = constraintsChildren.size() - 1;
            if (existingConstraints < numConstraints) {
                for (int i = existingConstraints; i < numConstraints; i++) {
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

                    view.getConstraintsBox().getChildren().add(equationBox);
                }
            } else {
                // 减少约束方程
                view.getConstraintsBox().getChildren().subList(numConstraints + 1, existingConstraints + 1).clear();
            }

            HBox constraintsBox = (HBox) view.getConstraintsBox().getChildren().get(1);
            int existingVariables = constraintsBox.getChildren().size() - 2;
            if (existingVariables < numVariables) {
                for (int i = 1; i<= numConstraints; i++) {
                    HBox equationBox = (HBox) view.getConstraintsBox().getChildren().get(i);
                    for (int j = existingVariables; j < numVariables; j++) {
                        TextField coefficientField = new TextField();
                        coefficientField.setPromptText("a" + i + (j + 1));
                        equationBox.getChildren().add(j, coefficientField);
                    }
                }
            } else if (existingVariables > numVariables) {
                for (int i = 1; i <= numConstraints; i++) {
                    HBox equationBox = (HBox) view.getConstraintsBox().getChildren().get(i);
                    equationBox.getChildren().subList(numVariables, existingVariables).clear();
                }
            }

            // 生成目标函数输入框
            ObservableList<Node> objectiveChildren = view.getObjectiveBox().getChildren();
            if (objectiveChildren.isEmpty()) {
                Label objectiveFunctionLabel = new Label("Objective Function:");
                view.getObjectiveBox().getChildren().add(objectiveFunctionLabel);
                HBox objectiveFunctionInputBox = new HBox(10);
                for (int i = 0; i < numVariables; i++) {
                    TextField coefficientField = new TextField();
                    coefficientField.setPromptText("x" + (i + 1));
                    objectiveFunctionInputBox.getChildren().add(coefficientField);
                }
                view.getObjectiveBox().getChildren().add(objectiveFunctionInputBox);
            } else {
                HBox objectiveFunctionInputBox = (HBox) view.getObjectiveBox().getChildren().get(1);
//                int existingVariables = objectiveFunctionInputBox.getChildren().size();
                if (existingVariables > numVariables) {
                    // 减少变量输入框
                    objectiveFunctionInputBox.getChildren().subList(numVariables, existingVariables).clear();
                } else if (existingVariables < numVariables) {
                    for (int i = existingVariables; i < numVariables; i++) {
                        TextField coefficientField = new TextField();
                        coefficientField.setPromptText("x" + (i + 1));
                        objectiveFunctionInputBox.getChildren().add(coefficientField);
                    }
                }
            }

            resize(primaryStage);
        } catch (NumberFormatException e){
            Alert alert = AppView.getAlertInstance(Alert.AlertType.ERROR, "Error", "Input Error", "Invalid input: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void extractCoefficient() {
        mathModel.clear();
        try {
            // 提取约束方程的系数和常数项
            for (int i = 1; i <= numConstraints; i++) {
                StringBuilder constraintString = new StringBuilder();
                HBox constraintsBox = (HBox) view.getConstraintsBox().getChildren().get(i);
                ConstraintEquation constraint = new ConstraintEquation();

                // 获取约束方程中的系数
                for (int j = 0; j < numVariables; j++) {
                    TextField coefficientField = (TextField) constraintsBox.getChildren().get(j);
                    String coefficient = coefficientField.getText();
                    constraint.getCoefficients().add(Double.parseDouble(coefficient));
                    constraintString.append(coefficient).append(" * x").append(j + 1).append(" ");
                    if (j < numVariables - 1) {
                        constraintString.append("+ ");
                    }
                }

                Node node = constraintsBox.getChildren().get(numVariables);
                String sign;
                if (node instanceof ComboBox<?>) {
                    @SuppressWarnings("unchecked")
                    ComboBox<String> signComboBox = (ComboBox<String>) node;
                    sign = signComboBox.getValue();
                    constraint.setSign(sign);
                } else {
                    throw new ClassCastException("Expected ComboBox<String> but got " + node.getClass().getName());
                }

                // 获取常数项
                TextField constantField = (TextField) constraintsBox.getChildren().get(numVariables + 1);
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
                TextField coefficientField = (TextField) ((HBox) view.getObjectiveBox().getChildren().get(1)).getChildren().get(i);
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
            Alert alert = AppView.getAlertInstance(Alert.AlertType.ERROR, "Error", "Input Error", "Invalid input: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void updateResults() {
        view.getResultsBox().getChildren().clear();

        Label solutionLabel = new Label(mathModel.getLPQ().toString());
        solutionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        view.getResultsBox().getChildren().add(solutionLabel);
    }

    private void resize(Stage primaryStage) {
        // 动态调整窗口大小
        double newWidth = Math.max(600, 200 + numVariables * 82);
        double newHeight = Math.max(400, 330 + numConstraints * 60 + mathModel.getLPQ().getSolutions().size() * 60);
        primaryStage.setWidth(newWidth);
        primaryStage.setHeight(newHeight);
    }

    private void solve(Stage primaryStage) {
        extractCoefficient();
        mathModel.printFunction();
        mathModel.solve();
        mathModel.printSolution();
        updateResults();
        resize(primaryStage);
    }
}
