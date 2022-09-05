package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginUnit {
	private String email;
	private String password;

	public LoginUnit(String email, String password) {
		this.email = email;
		this.password = password;
	}
}
