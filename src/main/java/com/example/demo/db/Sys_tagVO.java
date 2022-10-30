package com.example.demo.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
mysql> desc sys_tag;
+-----------+-------------+------+-----+---------+----------------+
| Field     | Type        | Null | Key | Default | Extra          |
+-----------+-------------+------+-----+---------+----------------+
| tagid     | bigint      | NO   | PRI | NULL    | auto_increment |
| pinName   | char(42)    | YES  | MUL | NULL    |                |
| engSysTag | varchar(64) | NO   |     | NULL    |                |
| korSysTag | varchar(64) | NO   |     | NULL    |                |
+-----------+-------------+------+-----+---------+----------------+
*/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Sys_tagVO {
	long tagid;
	String pinName;
	String engSysTag;
	String korSysTag;

	public Sys_tagVO(String pinName, String engSysTag, String korSysTag) {
		this.pinName = pinName;
		this.engSysTag = engSysTag;
		this.korSysTag = korSysTag;
	}
}
