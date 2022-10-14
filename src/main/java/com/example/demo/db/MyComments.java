package com.example.demo.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MyComments {
	private int commentid;
	private int postid;
	private String title;
	private String commenter;
	private String c_time;
	private String c_contents;
}
