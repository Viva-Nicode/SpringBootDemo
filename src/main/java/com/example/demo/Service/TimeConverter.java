package com.example.demo.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeConverter {

	public static String convertTime(final Timestamp sqlNow) {
		LocalDateTime now = LocalDateTime.now();

		String sourceNow = sqlNow.toString().substring(0, sqlNow.toString().length() - 2);
		String Now = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		if (sourceNow.split(" ")[0].equals(Now.split(" ")[0])) //년 월 일이 전부 같으면
			return sourceNow.split(" ")[1];
		else
			return sourceNow.split(" ")[0];
	}
}
