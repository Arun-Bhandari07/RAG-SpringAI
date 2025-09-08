package com.app.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.entities.User;
import com.app.entities.VerificationToken;
import com.app.enums.TokenType;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken,Long>{

	Optional<VerificationToken> findByUserAndTokenType(User user,TokenType tokenType);
	
	Optional<VerificationToken> findByToken(String token);
	
}
