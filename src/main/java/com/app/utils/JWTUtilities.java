package com.app.utils;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.app.entities.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTUtilities {
	
	@Value("${jwt.secretKey}")
	private  String jwtSecretKey;
	
	@Value("${jwt.expirationTime_InMinutes}")
	private int expirationTimeInMinutes;
	
	public String generateJwtToken(User user) {
		Instant issuedAt = Instant.now();
		Instant expireAt = issuedAt.plus(Duration.ofMinutes(expirationTimeInMinutes));
		
		Map<String,String> claims = new HashMap<>();
		claims.put("name", user.getName());
		claims.put("role", "USER_ROLE");
		
		String jws = Jwts.builder()
				.subject(user.getEmail())
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
		byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
		return 	Keys.hmacShaKeyFor(keyBytes);
	}
}