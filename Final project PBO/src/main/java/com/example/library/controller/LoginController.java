package com.example.library.controller;

import java.io.IOException;
import java.util.Optional;

import com.example.library.MainApp;
import com.example.library.data.DataManager;
import com.example.library.model.Member;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class LoginController {

    @FXML private TextField adminUsernameField;
    @FXML private PasswordField adminPasswordField;
    @FXML private Button loginButton;
    @FXML private Hyperlink registerButton;
    @FXML private Label statusLabel;
    @FXML private ImageView logoImageView;
    @FXML private StackPane rootPane;

    // Username dan password admin (hardcoded untuk contoh)
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";

    @FXML
    public void initialize() {
        statusLabel.setText("");
        adminPasswordField.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                handleLogin();
            }
        });
        
        // Load the logo image
        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/images/login_bg.jpg"));
            logoImageView.setImage(logoImage);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading logo image: " + e.getMessage());
        }
        // Bind image size to rootPane
        logoImageView.fitWidthProperty().bind(rootPane.widthProperty());
        logoImageView.fitHeightProperty().bind(rootPane.heightProperty());
    }

    @FXML
    private void handleLogin() {
        String username = adminUsernameField.getText();
        String password = adminPasswordField.getText();
        statusLabel.setText("");

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Tidak Lengkap", "ID Anggota / Username dan Password tidak boleh kosong.");
            return;
        }

        try {
            // Cek login admin
            if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
                MainApp.showAdminView();
                return;
            }
            // Jika bukan admin, cek sebagai anggota
            Optional<Member> memberOpt = DataManager.findMemberById(username);
            if (memberOpt.isPresent() && memberOpt.get().getPassword().equals(password)) {
                MainApp.showMemberView(memberOpt.get());
            } else if (memberOpt.isPresent()) {
                showAlert(Alert.AlertType.ERROR, "Login Gagal", "Password salah.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Gagal", "ID Anggota tidak ditemukan. Silakan daftar jika belum punya akun.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Kesalahan Sistem", "Gagal memuat halaman: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegister() {
        try {
            MainApp.showRegistrationView();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Kesalahan Sistem", "Gagal membuka halaman registrasi.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleForgotPassword() {
        try {
            com.example.library.MainApp.showForgotPasswordView();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Kesalahan Sistem", "Gagal membuka halaman lupa password.");
        }
    }
}