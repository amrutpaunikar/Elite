package com.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;



@Service
public class EmailService {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    public void sendOtp(String toEmail, String otp) {

        // 1️⃣ Configure API Client
        ApiClient client = Configuration.getDefaultApiClient();
        ApiKeyAuth auth = (ApiKeyAuth) client.getAuthentication("api-key");
        auth.setApiKey(apiKey);
        TransactionalEmailsApi apiInstance = new TransactionalEmailsApi(client);

        // 2️⃣ Build Email
        SendSmtpEmailSender sender = new SendSmtpEmailSender()
                .email(senderEmail)
                .name(senderName);

        SendSmtpEmailTo to = new SendSmtpEmailTo()
                .email(toEmail);

        SendSmtpEmail email = new SendSmtpEmail()
                .sender(sender)
                .to(Collections.singletonList(to))
                .subject("Your OTP for Password Reset")
                .htmlContent("<h3>Your OTP is: <strong>" + otp + "</strong></h3>"
                        + "<p>This OTP is valid for 5 minutes.</p>");

        // 3️⃣ Send Email
        try {
            apiInstance.sendTransacEmail(email);
            System.out.println("OTP email sent!");
        } catch (ApiException e) {
            System.err.println("Error sending email: " + e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }
}
