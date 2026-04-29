package com.studentlink.service;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Emailv31;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${mailjet.api.key}")
    private String apiKey;

    @Value("${mailjet.secret.key}")
    private String secretKey;

    public void sendOtpEmail(String toEmail, String otp) {

        if (!isEducationalEmail(toEmail)) {
            throw new IllegalArgumentException("Only university email addresses are allowed");
        }

        try {
            MailjetClient client = new MailjetClient(apiKey, secretKey);
            MailjetRequest request = new MailjetRequest(Emailv31.resource)
                    .property(Emailv31.MESSAGES, new JSONArray()
                            .put(new JSONObject()
                                    .put(Emailv31.Message.FROM, new JSONObject()
                                            .put("Email", "your_verified_mail@domain.com")
                                            .put("Name", "StudentLink"))
                                    .put(Emailv31.Message.TO, new JSONArray()
                                            .put(new JSONObject()
                                                    .put("Email", toEmail)))
                                    .put(Emailv31.Message.SUBJECT, "StudentLink Email Verification")
                                    .put(Emailv31.Message.TEXTPART,
                                            "Your StudentLink OTP is: " + otp + "\n\nDo not share this with anyone.")
                            ));

            MailjetResponse response = client.post(request);

            if (response.getStatus() == 200) {
                System.out.println("Mail sent successfully to: " + toEmail);
            } else {
                System.out.println("Mailjet error: " + response.getStatus());
                throw new RuntimeException("Email sending failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Email sending failed");
        }
    }

    public boolean isEducationalEmail(String email) {
        if (email == null || !email.contains("@")) return false;

        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();

        return domain.endsWith(".edu") ||
                domain.endsWith(".edu.in") ||
                domain.endsWith(".ac.in") ||
                domain.contains(".edu.");
    }
}