package com.example.demo.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Service
@Data
public class PinInfoObject {
	private String pinName;
	private String taglist;
	private String uploadDate;
	private int resolutionRatio;

	public PinInfoObject(String pinName, String taglist, String uploadDate, int resolutionRatio) {
		this.pinName = pinName;
		this.taglist = taglist;
		this.uploadDate = uploadDate;
		this.resolutionRatio = resolutionRatio;
	}

	public List<String> getTagCollection() {
		try {
			return Arrays.asList(taglist.split(","));
		} catch (NullPointerException e) {
			return new ArrayList<String>();
		}
	}

	public String getThumbnailName() {
		return ImageUtil.changeExtension(pinName, "jpg");
	}

	@Override
	public String toString() {
		return "{\"pinName\":\"" + pinName + "\",\"taglist\":\"" + taglist + "\",\"uploadDate\":\"" + uploadDate
				+ "\",\"resolutionRatio\":" + resolutionRatio + ",\"thumbnailName\":\"" + getThumbnailName() + "\"}";
	}
}
