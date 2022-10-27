package com.example.demo.Controller;

import org.springframework.cloud.gcp.vision.CloudVisionException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import static java.lang.System.out;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.example.demo.Service.ImageLabel;
import com.example.demo.Service.PapagoTranslationAPI;

@Controller
@RequestMapping("/Pin")
@RequiredArgsConstructor
public class PinController {
	private final ImageLabel il;

	@RequestMapping(value = "/uploadPins") // 이미지들과 태그들을 요청으로 받아서 db에 저장
	public @ResponseBody String uploadPins(@RequestParam(value = "pins") MultipartFile pins,
			@RequestParam(value = "appliedTagList") List<String> appliedTagList) {

		String des = UUID.randomUUID() + "." + pins.getContentType().split("/")[1];
		File dest = new File(
				"/Users/nicode./MainSpace/SpringBootDemo/demo/src/main/resources/static/pins/" + des);
		try {
			pins.transferTo(dest);
			Thread.sleep(2500);
			if (!dest.exists())
				Thread.sleep(2500);
			Map<String, String> tagMap;

			tagMap = PapagoTranslationAPI
					.getTranslationTagList(il.getImageLabels(
							"classpath:static/pins/" + des));
			/*
			 * synchronized (this) {
			 * out.println("===========================================================");
			 * for (Entry<String, String> elem : tagMap.entrySet())
			 * out.println(elem.getKey() + " " + elem.getValue());
			 * }
			 */

		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "";
	}

	@RequestMapping(value = "/moveUploadView")
	public ModelAndView moveUploadView(Model model, @SessionAttribute(value = "user_id") String ui) {
		return new ModelAndView("UploadPage");
	}

	// 모든 이미지들을 대상으로 api이용해서 시스템 태그 생성
	@RequestMapping(value = "cretaeSystemTag")
	public void createSystemTag() {

	}
}
