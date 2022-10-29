package com.example.demo.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
mysql> desc Pin;
+------------+-------------+------+-----+---------+-------------------+
| Field      | Type        | Null | Key | Default | Extra             |
+------------+-------------+------+-----+---------+-------------------+
| pinName    | char(42)    | NO   | PRI | NULL    |                   |
| uploader   | varchar(20) | NO   | MUL | NULL    |                   |
| uploadDate | datetime    | YES  |     | now()   | DEFAULT_GENERATED |
| visibility | tinyint(1)  | NO   |     | 0       |                   |
+------------+-------------+------+-----+---------+-------------------+
*/

@AllArgsConstructor
@NoArgsConstructor
@Data

public class PinVO {
	private String pinName;
	private String uploader;
	private String uploadDate;
	private Boolean visibility;

	public PinVO(String pinName, String uploader) {
		this.pinName = pinName;
		this.uploader = uploader;
	}
}
