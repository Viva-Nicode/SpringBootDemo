package com.example.demo.db;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@Getter
@Setter
public class TagVO {
	private long tagid;
	private String pinName;
	private String tagName;

	public TagVO(String pinName, String tagName) {
		this.pinName = pinName;
		this.tagName = tagName;
	}

	public TagVO(String tagName, long tagid) {
		this.tagName = tagName;
		this.tagid = tagid;
	}

	@Override
	public int hashCode() {
		return this.tagName.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		TagVO other = (TagVO) obj;
		if (!tagName.equalsIgnoreCase(other.tagName))
			return false;
		return true;
	}
}
