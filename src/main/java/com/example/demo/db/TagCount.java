package com.example.demo.db;

import lombok.Data;

@Data
public class TagCount {
	private String tagName;
	private long count;
	private int forhtmlid;

	public TagCount(String tagName, long count) {
		this.tagName = tagName;
		this.count = count;
		this.forhtmlid = 0;
	}
}
