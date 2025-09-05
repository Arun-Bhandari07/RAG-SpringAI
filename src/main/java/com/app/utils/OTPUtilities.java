package com.app.utils;

import io.netty.util.internal.ThreadLocalRandom;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OTPUtilities {

	public String generateOtp() {
		return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 100000));
	}
	
	
	
}
