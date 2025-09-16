package com.app.configurations;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.app.entities.User;
import com.app.enums.RegistrationType;
import com.app.enums.UserRole;
import com.app.repositories.UserRepository;

@Configuration
public class StartupInitializerConfig {

	@Bean
	public CommandLineRunner adminInitializer(UserRepository userRepo, PasswordEncoder passwordEncoder) {
		return args->{
			if(userRepo.count()==0) {
				User user = new User();
				user.setName("admin");
				user.setEmail("test001@gmail.com");
				user.setCreated_at(LocalDateTime.now());
				user.setEmailVerified(true);
				user.setPassword(passwordEncoder.encode("admin"));
				user.setRegType(RegistrationType.LOCAL);
				user.setRole(UserRole.ROLE_ADMIN);
				userRepo.save(user);
			}
		};
	}
}
