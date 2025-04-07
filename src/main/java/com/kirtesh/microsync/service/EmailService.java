package com.kirtesh.microsync.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendOtpEmail(String email, String firstName, String lastName, String otp) throws MessagingException {
        String htmlContent = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "        .email-container {\n" +
                "            width: 100%;\n" +
                "            background-color: #f8fff5;\n" +
                "            padding: 20px;\n" +
                "            box-sizing: border-box;\n" +
                "        }\n" +
                "        .email-header {\n" +
                "            background-color: #34cc27;\n" +
                "            color: white;\n" +
                "            padding: 15px;\n" +
                "            text-align: center;\n" +
                "            font-size: 24px;\n" +
                "            font-weight: bold;\n" +
                "        }\n" +
                "        .email-body {\n" +
                "            background-color: #ffffff;\n" +
                "            padding: 20px;\n" +
                "            border-radius:  0 0 8px 8px;\n" +
                "            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\n" +
                "        }\n" +
                "        .otp {\n" +
                "            font-size: 18px;\n" +
                "            font-weight: bold;\n" +
                "            color: #333333;\n" +
                "        }\n" +
                "        .validity {\n" +
                "            font-size: 14px;\n" +
                "            color: #666666;\n" +
                "        }\n" +
                "       hr {\n" +
                "           border: 0;\n" +
                "           height: 1px;\n" +
                "           background: none;\n" +
                "           border-top: 1px dashed #57f04a;\n" +
                "       }\n" +
                "        .footer {\n" +
                "            text-align: center;\n" +
                "            font-style: italic;\n" +
                "            margin-top: 20px;\n" +
                "            font-size: 12px;\n" +
                "            color: #999999;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"email-container\">\n" +
                "        <div class=\"email-header\">\n" +
                "            Email Verification\n" +
                "        </div>\n" +
                "        <div class=\"email-body\">\n" +
                "            <p>Dear " + firstName + " " + lastName + ",</p>\n" +
                "            <p>Your OTP (One-Time Password) for email verification is:</p>\n" +
                "            <p class=\"otp\">" + otp + "</p>\n" +
                "            <p class=\"validity\">Please note, the OTP is valid for 10 minutes.</p>\n" +
                "            <p>Thank you for verifying your email address!</p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "  <hr>\n" +
                "            If you did not request this verification, please ignore this email.\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("MicroSync <no-reply@microsync.com>");
        helper.setTo(firstName + " " + lastName + " <" + email + ">");
        helper.setSubject("Email Verification");
        helper.setText(htmlContent, true);

        javaMailSender.send(message);
    }
}
