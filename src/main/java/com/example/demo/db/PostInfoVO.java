package com.example.demo.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
mysql> desc PostInfo;
+-------------+---------------+------+-----+---------+-------------------+
| Field       | Type          | Null | Key | Default | Extra             |
+-------------+---------------+------+-----+---------+-------------------+
| postid      | int           | NO   | PRI | NULL    | auto_increment    |
| title       | varchar(30)   | NO   |     | NULL    |                   |
| writer      | varchar(20)   | NO   | MUL | NULL    |                   |
| hits        | int           | NO   |     | NULL    |                   |
| likes       | int           | NO   |     | 0       | DEFAULT_GENERATED |
| contents    | varchar(4096) | NO   |     | NULL    |                   |
| writtenTime | datetime      | YES  |     | now()   | DEFAULT_GENERATED |
| commentsNum | int           | YES  |     | 0       | DEFAULT_GENERATED |
| imageNum    | int           | YES  |     | 0       | DEFAULT_GENERATED |
+-------------+---------------+------+-----+---------+-------------------+
*/

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostInfoVO {
	private int postid;
	private String title;
	private String writer;
	private int hits;
	private int likes;
	private String contents;
	private String writtenTime;
	private int commentsNum;
	private int imageNum;
}
