package com.app.utils;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.app.service.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

	private  JWTUtilities jwtUtilities;

	private final CustomUserDetailsService customUserDetailsService;
	
	public JWTAuthenticationFilter(CustomUserDetailsService customUserDetailsService) {
		this.customUserDetailsService=customUserDetailsService;
		this.jwtUtilities=new JWTUtilities();
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// Extract Authorization Header
		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		// Extract Bearer Token
		String token = authHeader.substring(7);

		// Check if the token is Valid
		if (token != null && jwtUtilities.isTokenValid(token)) {
			String username = jwtUtilities.extractUsername(token);

			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
						null, userDetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				// Set the user in security context
				SecurityContextHolder.getContext().setAuthentication(authToken);

			}
			// Continue filter chain
		}
		filterChain.doFilter(request, response);

	}

}
