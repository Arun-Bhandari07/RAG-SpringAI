package com.app.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.entities.User;
import com.app.enums.RegistrationType;

@Repository
public interface UserRepository extends JpaRepository<User,Integer>{
		
		Optional<User> findByEmail(String email);
		
		Optional<User> findByRegTypeAndRegId(RegistrationType regType, String regId);
		

		boolean existsByEmail(String email);
		
		boolean existsByRegTypeAndRegId(String regType, String regId);
}

