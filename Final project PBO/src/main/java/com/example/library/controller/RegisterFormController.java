package com.example.library.controller;

import java.io.IOException;

import com.example.library.MainApp;
import com.example.library.data.DataManager;
import com.example.library.model.Member;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterFormController {

    @FXML private TextField memberIdField;
    @FXML private TextField nameField;
    @FXML private TextField majorField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        statusLabel.setText("");
    }

    @FXML
    private void handleRegisterMember() {
        String memberId = memberIdField.getText().trim();
        String name = nameField.getText().trim();
        String major = majorField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (memberId.isEmpty() || name.isEmpty() || major.isEmpty() || email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Semua field harus diisi!");
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }

        if (password.length() < 6) {
            statusLabel.setText("Password minimal harus 6 karakter!");
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            statusLabel.setText("Format email tidak valid!");
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }

        if (DataManager.isMemberIdExists(memberId)) {
            statusLabel.setText("ID Member sudah terdaftar!");
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }

        if (DataManager.isEmailExists(email)) {
            statusLabel.setText("Email sudah terdaftar!");
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }

        Member newMember = new Member(memberId, name, major, email, password);
        DataManager.registerMember(newMember);
        statusLabel.setText("Pendaftaran berhasil! Silakan login.");
        statusLabel.setStyle("-fx-text-fill: #27ae60;");

        try {
            MainApp.showLoginView();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Kesalahan Sistem", "Gagal kembali ke halaman login.");
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            MainApp.showLoginView();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Kesalahan Sistem", "Gagal kembali ke halaman login.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}