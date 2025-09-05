package com.app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.app.entities.User;
import com.app.exception.EmailSendException;
import com.app.repositories.OTPVerificationRepository;
import com.app.utils.OTPUtilities;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
	
	@Value("${spring.mail.username}")
	private String orgMail;
	
	private final JavaMailSender javaMailSender;
	
	private final OTPVerificationRepository otpRepo;

	public void sendVerificationEmail(User user) {
		String subject = "Email Verification";
		String from = orgMail;
		String to = user.getEmail();
		String verificationCode = OTPUtilities.generateOtp();
		
		String content = verificationEmailTemplate(verificationCode); 
		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
			
			helper.setFrom(from);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(content,true);
			
			javaMailSender.send(mimeMessage);
		}catch(MessagingException ex) {
			log.warn("Error when sending verification mail to {} with code {} message:{}, {}",to,verificationCode,ex.getMessage(),ex);
			throw new EmailSendException("Couldnot send verification mail",ex);
		}
		
	}
	
	public void sendPasswordResetEmail() {
		//TODO
	}
	
	public String verificationEmailTemplate(String verificationCode) {
		return """
				<!DOCTYPE html>
				<html>
				<body style="font-family:Arial, sans-serif; background:#f9f9f9; padding:20px;">
				<div style="max-width:500px; margin:auto; background:#fff; padding:20px; border-radius:8px; box-shadow:0 2px 5px rgba(0,0,0,0.1);">
    	 <h2 style="text-align:center; color:#333;">Verify Your Email</h2>
	      <p style="font-size:16px; color:#555;">Hi there,</p>
	      <p style="font-size:16px; color:#555;">Use the code below to verify your email address:</p>
     	<div style="font-size:24px; font-weight:bold; text-align:center; background:#f0f0f0; padding:10px; border-radius:5px; margin:20px 0;">
				%s
				</div>
	      <p style="font-size:14px; color:#888;">If you didn’t request this, feel free to ignore it.</p>
	      <p style="font-size:14px; color:#888;">Cheers,<br>Your App Team</p>
				</div>
				</body>
				</html>
				""".formatted(verificationCode.strip());
	}
}
