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
import com.example.demo.Service.GoogleVisionAPI;
import com.example.demo.Service.PapagoTranslationAPI;
import com.example.demo.db.PinMapper;
import com.example.demo.db.PinVO;
import com.example.demo.db.Sys_tagMapper;
import com.example.demo.db.Sys_tagVO;
import com.example.demo.db.TagMapper;
import com.example.demo.db.tagVO;

@Controller
@RequestMapping("/Pin")
@RequiredArgsConstructor
public class PinController {
	private final GoogleVisionAPI googleVisionAPI;
	private final PinMapper pm;
	private final TagMapper tm;
	private final Sys_tagMapper stm;

	@RequestMapping(value = "/uploadPins")
	public @ResponseBody String uploadPins(@RequestParam(value = "pins") MultipartFile pins,
			@RequestParam(value = "appliedTagList") List<String> appliedTagList,
			@SessionAttribute(value = "user_id") String ui) {

		out.println("tag List length : " + appliedTagList.size());
		for (String tag : appliedTagList)
			out.println(tag);

		String des = UUID.randomUUID() + "." + pins.getContentType().split("/")[1];
		File dest = new File(
				"/Users/nicode./MainSpace/SpringBootDemo/demo/src/main/resources/static/pins/" + des);
		try {
			pins.transferTo(dest);
			pm.insertPin(new PinVO(des, ui));
			if (appliedTagList.size() >= 2) {
				for (int idx = 1; idx < appliedTagList.size(); idx++)
					tm.insertTag(new tagVO(des, appliedTagList.get(idx)));
			}
			Thread.sleep(2500);
			if (!dest.exists())
				Thread.sleep(2500);
			Map<String, String> tagMap;

			tagMap = PapagoTranslationAPI
					.getTranslationTagList(googleVisionAPI.getImageLabels(
							"classpath:static/pins/" + des));

			for (Entry<String, String> elem : tagMap.entrySet())
				stm.insertSystag(new Sys_tagVO(des, elem.getKey(), elem.getValue()));

		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "";
	}

	@RequestMapping(value = "/moveUploadView")
	public ModelAndView moveUploadView(Model model, @SessionAttribute(value = "user_id") String ui) {
		model.addAttribute("tagList", tm.findTagByUserid(ui));
		return new ModelAndView("UploadPage");
	}

	@RequestMapping(value = "/movePinViewer")
	public ModelAndView movePinView(Model model, @SessionAttribute(value = "user_id") String ui) {
		return new ModelAndView("PinViewer");
	}
}