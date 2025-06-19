package com.example.library;

import java.io.IOException;

import com.example.library.controller.MemberController;
import com.example.library.data.DataManager;
import com.example.library.model.Member;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        try {
            Image icon = new Image(getClass().getResourceAsStream("/images/app_icon.jpeg"));
            primaryStage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Error loading application icon: " + e.getMessage());
        }
        showLoginView();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void showLoginView() throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/LoginView.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Login Perpustakaan");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setIconified(false);
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void showAdminView() throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/AdminView.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Dasbor Admin");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setIconified(false);
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void showMemberView(Member member) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/MemberView.fxml"));
        Parent root = loader.load();
        MemberController controller = loader.getController();
        controller.setCurrentMember(member);
        primaryStage.setTitle("Dasbor Member");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setIconified(false);
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void showRegistrationView() throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/RegisterFormView.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Registrasi Member Baru");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static void showForgotPasswordView() throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/ForgotPasswordView.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Lupa Password");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        DataManager.initializeDataFiles();
        launch(args);
    }
}