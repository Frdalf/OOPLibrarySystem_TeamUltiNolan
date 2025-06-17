package com.example.library.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailService {
    private static final String EMAIL_FROM = "naufalmuamar46@gmail.com"; // Ganti dengan email Anda
    private static final String EMAIL_PASSWORD = "xxzjaudwpsrjygwi"; // Ganti dengan app password dari Gmail

    public static void sendVerificationCode(String toEmail, String verificationCode) throws MessagingException {
        // Setup mail server properties
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Create session
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
            }
        });

        try {
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Kode Verifikasi Reset Password");
            
            // Email content
            String emailContent = String.format(
                "Halo,\n\n" +
                "Anda telah meminta untuk mereset password akun perpustakaan Anda.\n" +
                "Kode verifikasi Anda adalah: %s\n\n" +
                "Kode ini akan kadaluarsa dalam 5 menit.\n" +
                "Jika Anda tidak meminta reset password, abaikan email ini.\n\n" +
                "Salam,\n" +
                "Tim Perpustakaan", 
                verificationCode
            );
            
            message.setText(emailContent);

            // Send message
            Transport.send(message);
            
        } catch (MessagingException e) {
            throw new MessagingException("Gagal mengirim email: " + e.getMessage());
        }
    }
} 