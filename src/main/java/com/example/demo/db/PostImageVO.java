package com.example.demo.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
desc PostImage
+-----------+--------------+------+-----+---------+-------+
| Field     | Type         | Null | Key | Default | Extra |
+-----------+--------------+------+-----+---------+-------+
| postid    | int          | NO   | PRI | NULL    |       |
| imagename | varchar(100) | NO   | PRI | NULL    |       |
+-----------+--------------+------+-----+---------+-------+
*/

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostImageVO {
	private int postid;
	private String imagename;
}
