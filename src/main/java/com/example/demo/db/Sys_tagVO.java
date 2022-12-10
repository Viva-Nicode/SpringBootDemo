package com.example.demo.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
mysql> desc sys_tag;
+--------------------+-------------+------+-----+---------+----------------+
| Field              | Type        | Null | Key | Default | Extra          |
+--------------------+-------------+------+-----+---------+----------------+
| tagid              | bigint      | NO   | PRI | NULL    | auto_increment |
| pinName            | char(42)    | YES  | MUL | NULL    |                |
| engSysTag          | varchar(64) | NO   |     | NULL    |                |
| korSysTag          | varchar(64) | NO   |     | NULL    |                |
| importanceFraction | float(10,9) | YES  |     | NULL    |                |
+--------------------+-------------+------+-----+---------+----------------+
*/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Sys_tagVO {
	private long tagid;
	private String pinName;
	private String engSysTag;
	private String korSysTag;
	private float importanceFraction;

	public Sys_tagVO(String pinName, String engSysTag, String korSysTag, float importanceFraction) {
		this.pinName = pinName;
		this.engSysTag = engSysTag;
		this.korSysTag = korSysTag;
		this.importanceFraction = importanceFraction;
	}

	public Sys_tagVO(String engSysTag, float importanceFraction) {
		this.engSysTag = engSysTag;
		this.importanceFraction = importanceFraction;
	}

	

}
