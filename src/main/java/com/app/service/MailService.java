package com.app.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.app.enums.TokenType;
import com.app.exception.EmailSendException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

	@Value("${app.frontend_url}")
	private String frontendUrl;

	@Value("${spring.mail.username}")
	private String orgMail;

	private final JavaMailSender javaMailSender;

	private final TokenVerificationService tokenService;

	
	public void sendVerificationEmail(String email) {
		String token = tokenService.generateAndSaveVerificationToken(email, TokenType.SIGNUP_EMAIL_VERIFICATION);
	    String link = frontendUrl + "/verify-email?token=" + token;
	    String body = verificationEmailTemplate(link);
	    sendEmail(email, "Email Verification", body, false);
	}

	public void sendForgotPasswordEmail(String email) {
		String token = tokenService.generateAndSaveVerificationToken(email, TokenType.FORGOT_PASSWORD);
	    String link = frontendUrl + "/forgot-password?token=" + token;
	    String body = forgotPasswordEmailTemplate(link);
	    sendEmail(email, "Password Reset", body, false);
		
	}

	@Async
	private void sendEmail(String to, String subject, String body, boolean isMultipart) {
	    try {
	        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
	        MimeMessageHelper helper;
	        helper = new MimeMessageHelper(mimeMessage, isMultipart);
	        helper.setFrom(orgMail);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(body, true);
			javaMailSender.send(mimeMessage);
			 log.info("Email sent to {} with subject '{}'", to, subject);
	    } catch (MessagingException ex) {
	        log.error("Failed to send email to {} with subject '{}': {}", to, subject, ex.getMessage(), ex);
	        throw new EmailSendException("Could not send email", ex);
	    }
	}
	
	
	public String verificationEmailTemplate(String emailVerificationLink) {
	    return buildEmailTemplate(
	        "Verify Your Email",
	        "Use the link below to verify your email address:",
	        "Click this to verify your email",
	        emailVerificationLink,
	        false
	    );
	}

	public String forgotPasswordEmailTemplate(String passwordResetLink) {
	    return buildEmailTemplate(
	        "Password Reset Request",
	        "We received a request to reset your password. Click the button below to set a new one:",
	        "Reset Password",
	        passwordResetLink,
	        true
	    );
	}
	
	private String buildEmailTemplate(String title, String message, String linkText, String linkUrl, boolean styledButton) {
	    String linkHtml = styledButton
	        ? "<a href=\"%s\" class=\"btn\">%s</a>".formatted(linkUrl.strip(), linkText)
	        : """
	            <div style="font-size:24px; font-weight:bold; text-align:center; background:#f0f0f0; padding:10px; border-radius:5px; margin:20px 0;">
	                <a href="%s" style="color:#1a73e8; text-decoration:none;">%s</a>
	            </div>
	          """.formatted(linkUrl.strip(), linkText);

	    return """
	        <!DOCTYPE html>
	        <html>
	        <head>
	          <meta charset="UTF-8">
	          <title>%s</title>
	          <style>
	            body { font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px; }
	            .container { max-width: 500px; margin: auto; background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
	            .btn { display: inline-block; padding: 10px 20px; background-color: #007bff; color: #fff; text-decoration: none; border-radius: 5px; }
	            .footer { font-size: 12px; color: #888; margin-top: 20px; }
	          </style>
	        </head>
	        <body>
	          <div class="container">
	            <h2>%s</h2>
	            <p style="font-size:16px; color:#555;">Hi there,</p>
	            <p style="font-size:16px; color:#555;">%s</p>
	            %s
	            <p style="font-size:14px; color:#888;">If you didn’t request this, feel free to ignore it.</p>
	            <div class="footer">
	              &copy; %d DocuChat-AI. All rights reserved.
	            </div>
	          </div>
	        </body>
	        </html>
	        """.formatted(
	            title,
	            title,
	            message,
	            linkHtml,
	            LocalDateTime.now().getYear()
	        );
	}

}
