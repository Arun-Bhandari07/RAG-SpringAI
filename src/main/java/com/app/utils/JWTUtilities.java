package com.app.utils;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

public class JWTUtilities {
	
	@Value("${jwt.secretKey}")
	private  String jwtSecretKey ;
	
	@Value("${jwt.expirationTime_InMinutes}")
	private int expirationTimeInMinutes;

	private static final Logger logger = LoggerFactory.getLogger(JWTUtilities.class);
	
	// Generate JWT key
	public String generateJwtToken(String username) {
		Instant issuedAt = Instant.now();
		Instant expireAt = issuedAt.plus(Duration.ofMinutes(expirationTimeInMinutes));
		Map<String,String> claims = new HashMap<>();
		claims.put("user", username);
		claims.put("role", "USER_ROLE");
		
		String jws = Jwts.builder()
				.subject(username)
				.issuedAt(Date.from(issuedAt))
				.expiration(Date.from(expireAt))
				.claims(claims)
				.signWith(getSigningKey())
				.compact();
		return jws;

	}
	
	// Extract payload
	public Claims extractAllClaims(String jwtToken) {
			return Jwts.parser()
					.verifyWith(getSigningKey())
					.build()
					.parseSignedClaims(jwtToken)
					.getPayload();
	
	}
	
	//Validate token
	public boolean isTokenValid(String token) {
		if(!isTokenExpired(token)) return true;
		return false;
	}
	
	//Check for Expiration 
	public Boolean isTokenExpired(String jwtToken) {
		Claims claims = extractAllClaims(jwtToken);
		if(!(claims==null)) {
			Date expDate = claims.getExpiration();
		if(expDate.before(Date.from(Instant.now()))) {
			return true;
		}
	}
		return false;
	}
	
	//Extract Username
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	
	//Extract specific claim from token
	public <T> T extractClaim(String token, Function<Claims,T> resolver  ) {
		Claims claims = extractAllClaims(token);
		return resolver.apply(claims);
	}
	

	//Get signing keys
	public SecretKey getSigningKey() {
		byte[] keyBytes = Decoders.BASE64URL.decode(jwtSecretKey);
		return 	Keys.hmacShaKeyFor(keyBytes);
	}
}