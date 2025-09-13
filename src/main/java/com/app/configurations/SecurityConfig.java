package com.app.configurations;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.app.exception.CustomAccessDeniedException;
import com.app.exception.CustomAuthenticationEntryPoint;
import com.app.service.CustomUserDetailsService;
import com.app.utils.JWTAuthenticationFilter;
import com.app.utils.OAuth2SuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
	
	@Value("${app.frontend_url}")
	private String frontendUrl;
	
	private final CustomUserDetailsService customUserDetailsService;
	
	private final OAuth2SuccessHandler oauth2SuccessHandler;
	
	private final JWTAuthenticationFilter jwtAuthenticationFilter;
	
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	
	private final CustomAccessDeniedException customAccessDeniedException;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.exceptionHandling(ex-> ex
					.authenticationEntryPoint( customAuthenticationEntryPoint)   
					.accessDeniedHandler(customAccessDeniedException))
				.csrf(csrf->csrf.disable())
				.cors(cors->cors.configurationSource(corsConfigurationSource()))
				.authorizeHttpRequests(auth->auth
						.requestMatchers("/public/**","/v1/auth/**").permitAll()
						.requestMatchers("/upload","/users/**","api/v1/ask").permitAll()
						.requestMatchers("/admin/**").hasRole("ADMIN")
						.requestMatchers("/private").authenticated()
						.anyRequest().authenticated())
				.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
				.authenticationProvider(authenticationProvider())
				.oauth2Login(oauth2->oauth2.successHandler(oauth2SuccessHandler))
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
			
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of(frontendUrl));
		config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setAllowCredentials(true);
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;	
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) 
			throws Exception
	{
		return authConfig.getAuthenticationManager();
	}
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	

	
}
