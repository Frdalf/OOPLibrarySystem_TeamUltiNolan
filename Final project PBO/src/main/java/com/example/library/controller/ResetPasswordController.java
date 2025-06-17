package com.example.library.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ResetPasswordController {

    @FXML private TextField verificationCodeField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button resetPasswordButton;
    @FXML private Button backToLoginButton;
    @FXML private Label statusLabel;

    private String email;
    private String verificationCode;

    public void initialize() {
        // Initialize any necessary setup
    }

    public void setEmailAndCode(String email, String code) {
        this.email = email;
        this.verificationCode = code;
    }

    @FXML
    private void handleResetPassword() {
        String enteredCode = verificationCodeField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validate verification code
        if (!enteredCode.equals(verificationCode)) {
            statusLabel.setText("Kode verifikasi tidak valid!");
            return;
        }

        // Validate password
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            statusLabel.setText("Password tidak boleh kosong!");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            statusLabel.setText("Password tidak cocok!");
            return;
        }

        // Implement password reset logic here
        boolean success = com.example.library.data.DataManager.updateMemberPassword(email, newPassword);

        if (success) {
            statusLabel.setText("Password berhasil direset!");
            statusLabel.getStyleClass().removeAll("status-label", "success");
            statusLabel.getStyleClass().addAll("status-label", "success");
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Password Anda berhasil direset!");
            handleBackToLogin();
        } else {
            statusLabel.setText("Gagal mereset password. Email tidak ditemukan.");
            showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal mereset password. Silakan coba lagi.");
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) backToLoginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error: Tidak dapat kembali ke halaman login");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Styling untuk dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("/css/alert.css").toExternalForm()
        );
        dialogPane.getStyleClass().add("custom-dialog");

        // Tambahkan class sesuai tipe alert
        switch (alertType) {
            case ERROR:
                dialogPane.getStyleClass().add("alert-error");
                break;
            case WARNING:
                dialogPane.getStyleClass().add("alert-warning");
                break;
            case INFORMATION:
                dialogPane.getStyleClass().add("alert-information");
                break;
            case CONFIRMATION:
                dialogPane.getStyleClass().add("alert-confirmation");
                break;
        }
        alert.showAndWait();
    }
} 