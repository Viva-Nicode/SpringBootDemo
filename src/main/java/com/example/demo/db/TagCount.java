package com.example.demo.db;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TagCount {
	private String tagName;
	private long count;
}
