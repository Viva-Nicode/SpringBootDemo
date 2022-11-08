package com.example.demo.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import static java.lang.System.out;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class wordsAPI {

	public static void getDictionaryInfomation(String word) {
		try {
			URL url = new URL("https://api.dictionaryapi.dev/api/v2/entries/en/" + word);
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

			JSONParser parser = new JSONParser();
			JSONArray ja = (((JSONArray) parser.parse(response))); // 가장 최상위인 제이슨 배열이다.

			for (int i = 0; i < ja.size(); i++) {
				JSONObject jo = (JSONObject) ja.get(i);
				JSONArray mean = (JSONArray) jo.get("meanings");
				for (int idx = 0; idx < mean.size(); idx++) {
					if (((JSONObject) mean.get(idx)).get("partOfSpeech").toString().equals("noun")
							|| ((JSONObject) mean.get(idx)).get("partOfSpeech").toString().equals("adjective")) {
						if (((JSONArray) ((JSONObject) mean.get(idx)).get("synonyms")).size() != 0) {
							out.println("======================================");
							out.println(((JSONObject) mean.get(idx)).get("synonyms"));
							out.println("======================================");
						} // 여기서 가져온 단어들은 완벽히 일치하는 경우에만 사용하는것으로 하자.
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}