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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ForgotPasswordController {
    @FXML private TextField emailField;
    @FXML private Button sendCodeButton;
    @FXML private Button backToLoginButton;
    @FXML private Label statusLabel;

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Generate 6-digit code
        return String.valueOf(code);
    }

    @FXML
    private void handleSendCode() {
        String email = emailField.getText().trim();
        
        if (email.isEmpty()) {
            statusLabel.setText("Email tidak boleh kosong!");
            return;
        }

        // TODO: Validate if email exists in database
        // For now, we'll just generate a code and proceed
        
        String verificationCode = generateVerificationCode();
        
        try {
            // Send verification code via email
            EmailService.sendVerificationCode(email, verificationCode);
            statusLabel.setText("Kode verifikasi telah dikirim ke email Anda!");
            
            // Navigate to reset password view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ResetPasswordView.fxml"));
            Parent root = loader.load();
            
            ResetPasswordController controller = loader.getController();
            controller.setEmailAndCode(email, verificationCode);
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) sendCodeButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (MessagingException e) {
            statusLabel.setText("Error: Gagal mengirim email - " + e.getMessage());
        } catch (IOException e) {
            statusLabel.setText("Error: Tidak dapat membuka halaman reset password");
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
}