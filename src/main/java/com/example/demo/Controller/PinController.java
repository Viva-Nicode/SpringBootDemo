package com.example.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import static java.lang.System.out;
import org.springframework.web.bind.annotation.RequestParam;
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
			pm.insertPin(new PinVO(des, ui));

			if (appliedTagList.size() >= 2) {
				for (int idx = 1; idx < appliedTagList.size(); idx++)
					tm.insertTag(new tagVO(des, appliedTagList.get(idx)));
			}

			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						pins.transferTo(dest);
						List<Sys_tagVO> systemTagList;
						Thread.sleep(3000);

						while (!dest.exists())
							Thread.sleep(2500);

						systemTagList = PapagoTranslationAPI
								.getTranslationTagList(googleVisionAPI.getImageLabels(
										"classpath:static/pins/" + des));

						for (Sys_tagVO s : systemTagList) {
							s.setPinName(des);
							stm.insertSystag(s);
						}

					} catch (IllegalStateException | IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});

			t.start();

		} catch (IllegalStateException e) {
			out.println("Generated Exception.");
		} catch (org.springframework.cloud.gcp.vision.CloudVisionException e) {
			out.println("Generated Exception.");
		}
		return "";
	}

	@RequestMapping(value = "/deleteTagAll")
	public @ResponseBody String deleteTagAll(@SessionAttribute(value = "user_id") String ui,
			@RequestParam(value = "tagName") String tn) {
		long deleteRecordNum = tm.deleteTagByUseridAndTagName(Map.of("user_id", ui, "tagName", tn));
		out.println("삭제된 레코드 갯수 : " + deleteRecordNum);
		return "success";
	}

	@RequestMapping(value = "/modifyTag")
	public @ResponseBody String modifyTagName(@SessionAttribute(value = "user_id") String ui,
			@RequestParam(value = "newTagname") String ntn, @RequestParam(value = "originalTagname") String otn) {
		long modifyRecordNum = tm.modifyTagName(Map.of("otn", otn, "ntn", ntn, "user_id", ui));
		out.println("수정된 레코드 갯수 : " + modifyRecordNum);
		return "success";
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