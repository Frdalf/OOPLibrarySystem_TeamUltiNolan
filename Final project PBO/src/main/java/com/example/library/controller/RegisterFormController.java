package com.example.library.controller;

import com.example.library.MainApp;
import com.example.library.data.DataManager;
import com.example.library.model.Member;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

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
        String password = passwordField.getText().trim();

        if (memberId.isEmpty() || name.isEmpty() || major.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Registrasi Gagal", "Semua field harus diisi.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showAlert(Alert.AlertType.ERROR, "Registrasi Gagal", "Format email tidak valid.");
            return;
        }

        if (DataManager.isMemberIdExists(memberId)) {
            showAlert(Alert.AlertType.ERROR, "Registrasi Gagal", "ID Member sudah terdaftar.");
            return;
        }

        if (DataManager.isEmailExists(email)) {
            showAlert(Alert.AlertType.ERROR, "Registrasi Gagal", "Email sudah terdaftar.");
            return;
        }

        Member newMember = new Member(memberId, name, major, email, password);
        DataManager.registerMember(newMember);
        showAlert(Alert.AlertType.INFORMATION, "Registrasi Berhasil", "Member baru berhasil didaftarkan. Silakan login.");

        try {
            MainApp.showLoginView(); // Kembali ke halaman login setelah berhasil
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