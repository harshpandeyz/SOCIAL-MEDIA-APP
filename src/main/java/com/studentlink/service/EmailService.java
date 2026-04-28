package com.studentlink.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {

        if (!isEducationalEmail(toEmail)) {
            throw new IllegalArgumentException("Only university email addresses are allowed");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("StudentLink Email Verification");
        message.setText(
                "Your StudentLink OTP is: " + otp +
                        "\n\nDo not share this with anyone."
        );

        try {
            mailSender.send(message);
            System.out.println("Mail sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.out.println("Failed to send email");
            e.printStackTrace();
            throw new RuntimeException("Email sending failed");
        }
    }

    public boolean isEducationalEmail(String email) {

        if (email == null || !email.contains("@")) {
            return false;
        }

        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();

        return domain.endsWith(".edu") ||
                domain.endsWith(".edu.in") ||
                domain.endsWith(".ac.in") ||
                domain.contains(".edu.");
    }
}