// src/main/java/com/example/library/controller/ForgotPasswordController.java
package com.example.library.controller;

import com.example.library.MainApp;
import com.example.library.data.DataManager;
import com.example.library.model.Member;
import com.example.library.service.EmailService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class ForgotPasswordController {

    @FXML private TextField memberIdField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button resetPasswordButton;
    @FXML private Label statusLabel;
    @FXML private TextField verificationCodeField;
    @FXML private Button sendCodeButton; // Tombol untuk mengirim kode
    @FXML private VBox passwordResetPane; // Pane yang berisi field password baru dan konfirmasi
    @FXML private VBox sendCodePane; // Pane untuk input ID/Email dan tombol kirim kode

    // Peta untuk menyimpan kode verifikasi yang dihasilkan (contoh sederhana, di dunia nyata akan lebih aman)
    private static Map<String, String> verificationCodes = new HashMap<>();
    private static String currentMemberIdentifier; // Untuk menyimpan ID/Email saat kode dikirim

    @FXML
    public void initialize() {
        statusLabel.setText("");
        passwordResetPane.setVisible(false); // Sembunyikan panel reset password di awal
        sendCodePane.setVisible(true); // Tampilkan panel kirim kode di awal
    }

    @FXML
    private void handleSendCode() {
        String email = memberIdField.getText().trim();
        if (email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Email tidak boleh kosong");
            return;
        }

        // Generate verification code
        String generatedCode = generateVerificationCode(); // Dapatkan kode yang dihasilkan
        try {
            // Send verification code via email
            EmailService.sendVerificationCode(email, generatedCode);
            showAlert(Alert.AlertType.INFORMATION, "Kode Terkirim", 
                "Kode verifikasi telah dikirim ke email yang terhubung dengan akun Anda. Silakan cek email Anda.");
            
            // Simpan kode verifikasi ke peta statis
            currentMemberIdentifier = email; // Simpan email sebagai identifier
            verificationCodes.put(currentMemberIdentifier, generatedCode);

            // Enable verification code field and verify button
            verificationCodeField.setDisable(false);
            sendCodeButton.setDisable(true);

            // Show password reset pane and hide send code pane
            sendCodePane.setVisible(false);
            passwordResetPane.setVisible(true);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", 
                "Gagal mengirim kode verifikasi: " + e.getMessage());
        }
    }

    @FXML
    private void handleResetPassword() {
        String code = verificationCodeField.getText().trim();
        String newPass = newPasswordField.getText();
        String confirmPass = confirmPasswordField.getText();

        if (code.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Tidak Lengkap", "Semua field harus diisi.");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            showAlert(Alert.AlertType.ERROR, "Password Tidak Cocok", "Password baru dan konfirmasi password tidak cocok.");
            return;
        }
        if (newPass.length() < 6) {
            showAlert(Alert.AlertType.ERROR, "Password Lemah", "Password minimal 6 karakter.");
            return;
        }

        // Verifikasi kode
        if (currentMemberIdentifier != null && verificationCodes.containsKey(currentMemberIdentifier) && verificationCodes.get(currentMemberIdentifier).equals(code)) {
            Optional<Member> memberOpt;
            if (currentMemberIdentifier.contains("@")) {
                memberOpt = DataManager.findMemberByEmail(currentMemberIdentifier);
            } else {
                memberOpt = DataManager.findMemberById(currentMemberIdentifier);
            }

            if (memberOpt.isPresent()) {
                Member memberToUpdate = memberOpt.get();
                memberToUpdate.setPassword(newPass); // Update password
                DataManager.updateMember(memberToUpdate); // Simpan perubahan

                verificationCodes.remove(currentMemberIdentifier); // Hapus kode setelah digunakan
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Password berhasil direset. Silakan login dengan password baru Anda.");
                handleBackToLogin();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal memperbarui password. Member tidak ditemukan.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Kode Tidak Valid", "Kode verifikasi salah atau sudah kadaluarsa.");
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

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6 digit angka
        return String.valueOf(code);
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