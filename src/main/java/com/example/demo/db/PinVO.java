package com.example.demo.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
mysql> desc pin;
+-----------------+-------------+------+-----+---------+-------------------+
| Field           | Type        | Null | Key | Default | Extra             |
+-----------------+-------------+------+-----+---------+-------------------+
| pinName         | char(42)    | NO   | PRI | NULL    |                   |
| uploader        | varchar(20) | NO   | MUL | NULL    |                   |
| uploadDate      | datetime    | YES  |     | now()   | DEFAULT_GENERATED |
| visibility      | tinyint(1)  | NO   |     | 0       |                   |
| resolutionRatio | int         | NO   |     | NULL    |                   |
+-----------------+-------------+------+-----+---------+-------------------+

*/

@AllArgsConstructor
@NoArgsConstructor
@Data

public class PinVO {
	private String pinName;
	private String uploader;
	private String uploadDate;
	private Boolean visibility;
	private int resolutionRatio;

	public PinVO(String pinName, String uploader, int resolutionRatio, Boolean visi) {
		this.pinName = pinName;
		this.uploader = uploader;
		this.resolutionRatio = resolutionRatio;
		this.visibility = visi;
	}
}
