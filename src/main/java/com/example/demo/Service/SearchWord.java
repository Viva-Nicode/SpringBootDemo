package com.example.demo.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import static java.lang.System.out;

@Service
public class SearchWord {
	public static void searchRequest() {
		try {
			URL url = new URL("https://www.bing.com/search?q=\"banana\"+AND+\"apple\"");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("GET");

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer stringBuffer = new StringBuffer();
			String inputLine;

			while ((inputLine = bufferedReader.readLine()) != null) {
				stringBuffer.append(inputLine);
			}

			bufferedReader.close();

			String response = stringBuffer.toString();

 			Document doc = Jsoup.parse(response);

			Elements div = doc.select("#b_tween > span.sb_count");
			out.println(div.html());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void searchRequest2(String searchWord) {
		try {
			URL url = new URL("http://www.google.com/search?q=" + searchWord);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("GET");

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer stringBuffer = new StringBuffer();
			String inputLine;

			while ((inputLine = bufferedReader.readLine()) != null) {
				stringBuffer.append(inputLine);
			}

			bufferedReader.close();

			String response = stringBuffer.toString();

 			Document doc = Jsoup.parse(response);

			Elements div = doc.select("#result-stats");
			out.println(div.html());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
