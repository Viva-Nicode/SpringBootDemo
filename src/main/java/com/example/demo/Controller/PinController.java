package com.example.demo.Controller;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import static java.lang.System.out;

import java.awt.image.BufferedImage;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.Service.CompressImage;
import com.example.demo.Service.ConvertPngToJpg;
import com.example.demo.Service.GoogleVisionAPI;
import com.example.demo.Service.PinInfoObject;
import com.example.demo.db.PinMapper;
import com.example.demo.db.PinVO;
import com.example.demo.db.Sys_tagMapper;
import com.example.demo.db.Sys_tagVO;
import com.example.demo.db.TagMapper;
import com.example.demo.db.tagVO;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@RequestMapping("/Pin")
@RequiredArgsConstructor
@SessionAttributes(value = { "validTaglist" })

public class PinController {
	private final GoogleVisionAPI googleVisionAPI;
	private final PinMapper pm;
	private final TagMapper tm;
	private final Sys_tagMapper stm;
	private final String pinPath = "/Users/nicode./MainSpace/SpringBootDemo/demo/src/main/resources/static/pins/";
	private final String thumbnailPath = "/Users/nicode./MainSpace/SpringBootDemo/demo/src/main/resources/static/Thumbnail/";

	@RequestMapping(value = "/uploadPins")
	public @ResponseBody String uploadPins(@RequestParam(value = "pins") MultipartFile pins,
			@RequestParam(value = "appliedTagList") List<String> appliedTagList,
			@SessionAttribute(value = "user_id") String ui) {

		final String ext = pins.getContentType().split("/")[1];
		String des = UUID.randomUUID() + "." + ext;
		File dest = new File(pinPath + des);
		try {

			BufferedImage bufferedImage = ImageIO.read(pins.getInputStream());
			/* pin 테이블에 요청으로 넘어온 이미지 정보를 insert 한다. */
			pm.insertPin(new PinVO(des, ui,
					CompressImage.getResolutionRatio(bufferedImage.getWidth(), bufferedImage.getHeight())));

			/*
			 * 넘어온 태그가 존재한다면 이를 리스트로 만들어서 foreach insert 해준다.
			 * 태그가 하나도 없다면 동작하지 않는다.
			 */
			if (appliedTagList.size() >= 2) {
				List<tagVO> l = new ArrayList<>();
				for (int idx = 1; idx < appliedTagList.size(); idx++)
					l.add(new tagVO(des, appliedTagList.get(idx)));
				tm.insertTag(l);
			}

			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					try {

						synchronized (this) {
							pins.transferTo(dest);
						}
						List<Sys_tagVO> systemTagList;

						Thread.sleep(3000);
						while (!dest.exists())
							Thread.sleep(2500);

						/* 구글 비전에서 시스탬 태그를 획득한다. */
						systemTagList = googleVisionAPI.getImageLabels(
								"classpath:static/pins/" + des);

						/* 시스템 태그를 foreach insert 한다. */
						for (Sys_tagVO s : systemTagList)
							s.setPinName(des);
						stm.insertSystag(systemTagList);

						if (ext.equals("png")) {

							ConvertPngToJpg.pngToJpg(pinPath + des, thumbnailPath + des);

							Thread.sleep(2000);
							if (!new File(thumbnailPath + des).exists())
								Thread.sleep(2000);

							CompressImage.compress(ConvertPngToJpg.changeExtension(thumbnailPath + des, "jpg"));
						} else {
							CompressImage.compress(pinPath + des,
									ConvertPngToJpg.changeExtension(thumbnailPath + des, "jpg"));
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
		} catch (IOException e1) {
			e1.printStackTrace();
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

	@RequestMapping(value = "/movePinViewer") /* 20개씩만 로딩한다. */
	public ModelAndView movePinView(Model model, @SessionAttribute(value = "user_id") String ui) {
		int cs = 0;

		List<PinInfoObject> l = tm.getPininfoListByUploader(ui);

		for (PinInfoObject p : l)
			p.setPinName(ConvertPngToJpg.changeExtension(p.getPinName(), "jpg"));
		model.addAttribute("validTaglist", new ArrayList<Object>());
		model.addAttribute("allpinlist", l);
		model.addAttribute("allpinsize", l.size());
		model.addAttribute("taglist", tm.findTagByUserid(ui));

		if (l.size() <= 20)
			cs = l.size();
		else
			cs = 20;

		model.addAttribute("current_pinsize", cs);
		model.addAttribute("current_pinlist", new ArrayList<>(l.subList(0, cs)));
		return new ModelAndView("PinViewer");
	}

	@RequestMapping(value = "/morePins") /* 8개씩 로딩 */
	public @ResponseBody String morePinsRequest(@SessionAttribute("user_id") String ui,
			@RequestBody String jsonImageRequest) {
		JSONParser parser = new JSONParser();
		JSONObject responseJsonObject = new JSONObject();
		responseJsonObject.put("responseJsonArray", new JSONArray());

		try {
			JSONArray imageNameList = (JSONArray) ((JSONObject) parser.parse(jsonImageRequest)).get("imageNameArray");

			for (int idx = 0; idx < imageNameList.size(); idx++) {

				JSONObject jo = (JSONObject) imageNameList.get(idx);
				BufferedImage img = ImageIO.read(new File(thumbnailPath + jo.get("pinName").toString()));
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(img, "jpg", baos);
				byte[] bytes = baos.toByteArray();
				((JSONArray) responseJsonObject.get("responseJsonArray"))
						.add(Base64.getEncoder().encodeToString(bytes));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return responseJsonObject.toJSONString();
	}

	@RequestMapping(value = "/search")
	public @ResponseBody String changeSearchPredicate(Model model, @RequestBody String jsonTagRequest,
			@SessionAttribute(value = "user_id") String ui,
			@SessionAttribute(value = "validTaglist") List<Object> vtl) {
		JSONParser parser = new JSONParser();

		if (vtl.isEmpty())
			out.println("비어있음");

		for (Object tag : vtl)
			out.println(tag);

		try {
			JSONArray vaildTagList = (JSONArray) ((JSONObject) parser.parse(jsonTagRequest)).get("taglist");
			List<Object> l = Arrays.asList(vaildTagList.toArray());
			model.addAttribute("validTaglist", l);
			/*
			 * List<PinInfoObject> piol = pm.selectPinsByTags(l)
			 * for (PinInfoObject p : piol)
			 * p.setPinName(ConvertPngToJpg.changeExtension(p.getPinName(), "jpg"));
			 * if (piol.size() <= 20)
			 * cs = piol.size();
			 * else
			 * cs = 20;
			 * 커런트, 올 리스트 사이즈들 제이슨에 넣어서 응답
			 */

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}
}