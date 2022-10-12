package com.example.demo.db;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserVO {
	
	private String ID;
	private String PW;
	private String email;
	private Date date;
	private String profile;

}
