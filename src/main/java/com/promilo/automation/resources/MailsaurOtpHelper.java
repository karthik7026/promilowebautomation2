package com.promilo.automation.resources;

import com.mailosaur.MailosaurClient;
import com.mailosaur.models.Message;
import com.mailosaur.models.SearchCriteria;

public class MailsaurOtpHelper {

    public static String getOtpFromMailosaur() throws Exception {
        String apiKey = "8soV0X7aKrwZMUbviyNA1ZEbv4a7mxTa"; // Secure this key in config/env
        String serverId = "ofuk8kzb";
        String testEmail = "rocket-milk@ofuk8kzb.mailosaur.net";

        MailosaurClient mailosaur = new MailosaurClient(apiKey);
        SearchCriteria criteria = new SearchCriteria().withSentTo(testEmail);

        System.out.println("⏳ Waiting for OTP from Mailosaur API...");
        Message message = mailosaur.messages().get(serverId, criteria, 30000); // 30 sec timeout

        String body = message.text().body();
        String otp = body.replaceAll("\\D+", ""); // Extract only digits

        System.out.println("✅ OTP fetched via API: " + otp);
        return otp;
    }
}
