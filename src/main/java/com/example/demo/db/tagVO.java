package com.example.demo.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
mysql> desc tag;
+---------+-------------+------+-----+---------+----------------+
| Field   | Type        | Null | Key | Default | Extra          |
+---------+-------------+------+-----+---------+----------------+
| tagid   | bigint      | NO   | PRI | NULL    | auto_increment |
| pinName | char(42)    | YES  | MUL | NULL    |                |
| tagName | varchar(32) | NO   |     | NULL    |                |
+---------+-------------+------+-----+---------+----------------+
*/

@AllArgsConstructor
@NoArgsConstructor
@Data
public class tagVO {
	private long tagid;
	private String pinName;
	private String tagName;

	public tagVO(String pinName, String tagName) {
		this.pinName = pinName;
		this.tagName = tagName;
	}
}
