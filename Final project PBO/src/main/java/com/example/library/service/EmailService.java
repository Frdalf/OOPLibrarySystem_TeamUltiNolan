package com.example.library.service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {
    private static final String EMAIL_FROM = "naufalmuamar46@gmail.com";
    private static final String EMAIL_PASSWORD = "xxzjaudwpsrjygwi";

    public static void sendVerificationCode(String toEmail, String verificationCode) {
        // Set up mail server properties
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
            message.setSubject("Kode Verifikasi Perpustakaan");
            message.setText("Kode verifikasi Anda adalah: " + verificationCode + "\n\n" +
                    "Kode ini akan kadaluarsa dalam 5 menit.\n" +
                    "Jika Anda tidak meminta kode ini, abaikan email ini.");

            // Send message
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Gagal mengirim email: " + e.getMessage());
        }
    }
} 