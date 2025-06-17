// src/main/java/com/example/library/controller/ForgotPasswordController.java
package com.example.library.controller;

import java.io.IOException;
import java.util.Random;

import javax.mail.MessagingException;

import com.example.library.service.EmailService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.application.Platform;

public class ForgotPasswordController {
    @FXML private TextField emailField;
    @FXML private Button sendCodeButton;
    @FXML private Button backToLoginButton;

    private String tempEmail;
    private String tempVerificationCode;

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Generate 6-digit code
        return String.valueOf(code);
    }

    @FXML
    private void handleSendCode() {
        String email = emailField.getText().trim();
        
        if (email.isEmpty()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Email tidak boleh kosong!");
            alert.showAndWait();
            return;
        }

        // TODO: Validate if email exists in database
        // For now, we'll just generate a code and proceed
        
        String verificationCode = generateVerificationCode();
        
        try {
            // Send verification code via email
            EmailService.sendVerificationCode(email, verificationCode);
            tempEmail = email;
            tempVerificationCode = verificationCode;
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Sukses");
            alert.setHeaderText(null);
            alert.setContentText("Kode verifikasi telah dikirim ke email Anda!");
            alert.showAndWait();
            // Setelah user klik OK, pindah ke halaman reset password
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ResetPasswordView.fxml"));
            Parent root = loader.load();
            ResetPasswordController controller = loader.getController();
            controller.setEmailAndCode(tempEmail, tempVerificationCode);
            Scene scene = new Scene(root);
            Stage stage = (Stage) sendCodeButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (MessagingException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Gagal mengirim email: " + e.getMessage());
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Tidak dapat membuka halaman reset password");
            alert.showAndWait();
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
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Tidak dapat kembali ke halaman login");
            alert.showAndWait();
        }
    }
}