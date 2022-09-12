package com.example.demo.db;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
desc comments
+------------+--------------+------+-----+---------+-------------------+
| Field      | Type         | Null | Key | Default | Extra             |
+------------+--------------+------+-----+---------+-------------------+
| postid     | int          | NO   | MUL | NULL    |                   |
| commenter  | varchar(20)  | NO   | PRI | NULL    |                   |
| c_time     | datetime     | NO   | PRI | now()   | DEFAULT_GENERATED |
| c_contents | varchar(100) | NO   |     | NULL    |                   |
+------------+--------------+------+-----+---------+-------------------+
*/

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentsVO {

	private int postid;
	private String commenter;
	private Timestamp c_time;
	private String c_contents;

	public CommentsVO(String commenter, Timestamp c_time, String c_contents) {
		this.commenter = commenter;
		this.c_time = c_time;
		this.c_contents = c_contents;
	}

	public CommentsVO(int postid, String commenter, String c_contents) {
		this.postid = postid;
		this.commenter = commenter;
		this.c_contents = c_contents;
	}
}
