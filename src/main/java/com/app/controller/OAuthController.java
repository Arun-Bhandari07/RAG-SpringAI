package com.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OAuthController {
	
	@GetMapping("login/oauth")
	public String redirectPage() {
		return "index";
	}
	//TODO
}
