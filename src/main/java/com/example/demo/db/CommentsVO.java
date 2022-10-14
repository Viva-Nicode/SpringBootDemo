package com.example.demo.db;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
mysql> desc comments;
+------------+--------------+------+-----+---------+-------------------+
| Field      | Type         | Null | Key | Default | Extra             |
+------------+--------------+------+-----+---------+-------------------+
| commentid  | int          | NO   | PRI | NULL    | auto_increment    |
| postid     | int          | NO   | MUL | NULL    |                   |
| commenter  | varchar(20)  | NO   | MUL | NULL    |                   |
| c_time     | datetime     | NO   |     | now()   | DEFAULT_GENERATED |
| c_contents | varchar(100) | NO   |     | NULL    |                   |
+------------+--------------+------+-----+---------+-------------------+
*/

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentsVO {

	private int commentid;
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

/*
CREATE TABLE comments(
commentid int NOT NULL AUTO_INCREMENT primary key,
postid int NOT NULL,
commenter varchar(20) NOT NULL,
c_time datetime NOT NULL DEFAULT (now()),
c_contents varchar(100) NOT NULL,
CONSTRAINT comments_ibfk_1 FOREIGN KEY (postid) REFERENCES PostInfo (postid) ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT comments_ibfk_2 FOREIGN KEY (commenter) REFERENCES User (ID) ON DELETE CASCADE ON UPDATE CASCADE)
*/
