package com.example.demo.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static java.lang.System.out;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.example.demo.db.Sys_tagVO;

public class PapagoTranslationAPI {

	public static List<Sys_tagVO> getTranslationTagList(Map<String, Float> m) {

		String data = "source=en&target=ko&text=";

		List<Sys_tagVO> systemTagList = new ArrayList<>();

		for (Entry<String, Float> elem : m.entrySet()) {
			data += elem.getKey() + "/";
			systemTagList.add(new Sys_tagVO(elem.getKey(), elem.getValue()));
		}

		data = data.substring(0, data.length() - 1);

		try {
			URL url = new URL("https://openapi.naver.com/v1/papago/n2mt");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			connection.setRequestProperty("X-Naver-Client-Id", "9IrQiPVL2LcgX1T2MUmx");
			connection.setRequestProperty("X-Naver-Client-Secret", "F7_k5qp8eJ");

			DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
			outputStream.writeBytes(data);
			outputStream.flush();
			outputStream.close();

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer stringBuffer = new StringBuffer();
			String inputLine;

			while ((inputLine = bufferedReader.readLine()) != null)
				stringBuffer.append(inputLine);

			bufferedReader.close();

			String response = stringBuffer.toString();

			JSONParser parser = new JSONParser();
			JSONObject jo = (JSONObject) parser.parse(response);

			out.println(((JSONObject) jo.get("message")).get("result"));

			List<String> list = Arrays.asList(((JSONObject) ((JSONObject) jo.get("message")).get("result"))
					.get("translatedText").toString().split("/")).stream()
					.map(String::trim).collect(Collectors.toList());

			if (list.size() == systemTagList.size()) {
				for (int idx = 0; idx < systemTagList.size(); idx++)
					systemTagList.get(idx).setKorSysTag(list.get(idx));
			}
			return systemTagList;

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		return null;
	}
}
