package com.example.demo.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.example.demo.Service.ImageUtil;
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
@SessionAttributes(value = { "validTaglist", "visibility" })
// 사용자가 검색조건으로 클릭한 태그 리스트이다. 같은 검색조건일땐 그냥 아무동작하지 않기위해 저장해놓은것이다.

public class PinController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
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

			synchronized (this) {
				pins.transferTo(dest);
			}

			Thread.sleep(4000);

			List<Sys_tagVO> systemTagList;

			/* 구글 비전에서 시스탬 태그를 획득한다. */
			systemTagList = googleVisionAPI.getImageLabels(
					"classpath:static/pins/" + des);

			boolean visi;

			if (appliedTagList.get(appliedTagList.size() - 1).equals("true"))
				visi = true;
			else
				visi = false;

			pm.insertPin(new PinVO(des, ui,
					ImageUtil.getResolutionRatio(bufferedImage.getWidth(),
							bufferedImage.getHeight()),
					visi));

			if (appliedTagList.size() >= 3) {
				List<tagVO> l = new ArrayList<>();
				for (int idx = 1; idx < appliedTagList.size() - 1; idx++)
					l.add(new tagVO(des, appliedTagList.get(idx)));
				tm.insertTag(l);
			} else {
				List<String> l = new ArrayList<>();
				l.add(des);
				tm.insertTagNone(l);
			}

			/* 시스템 태그를 foreach insert 한다. */
			for (Sys_tagVO s : systemTagList)
				s.setPinName(des);

			stm.insertSystag(systemTagList);

			if (ext.equals("png")) {

				ImageUtil.pngToJpg(pinPath + des, thumbnailPath + des);

				Thread.sleep(2000);
				if (!new File(thumbnailPath + des).exists())
					Thread.sleep(2000);

				ImageUtil.compress(ImageUtil.changeExtension(thumbnailPath + des, "jpg"));
			} else {
				ImageUtil.compress(pinPath + des,
						ImageUtil.changeExtension(thumbnailPath + des, "jpg"));
			}

		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			logger.error("Generated IOException during the save " + des);
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (org.springframework.cloud.gcp.vision.CloudVisionException e) {
			logger.error("Generated cloud.gcp.vision.CloudVisionException during the save " + des);
			dest.delete();
			return "{ \"response\" : \"fail\"}";
		}
		return "{ \"response\" : \"success\" }";
	}

	@RequestMapping(value = "/movePinViewer") /* 16개씩만 로딩한다. */
	public ModelAndView movePinView(Model model, @SessionAttribute(value = "user_id") String ui,
			@RequestParam(value = "visibility") String visibility) {
		int cs = 0;

		/*
		 * ture : 전부 보여줘
		 * false : 퍼블릭만 보여줘
		 */
		List<PinInfoObject> l;
		if (visibility.equals("true")) {
			l = tm.getPininfoListByUploaderAll(ui);
			model.addAttribute("taglist", tm.findTagByUseridAll(ui));
		} else {
			l = tm.getPininfoListByUploaderOnlyPublic(ui);
			model.addAttribute("taglist", tm.findTagByUseridOnlyPublic(ui));
		}

		model.addAttribute("validTaglist", new ArrayList<Object>());

		model.addAttribute("allpinlist", l);
		model.addAttribute("allpinsize", l.size());

		if (l.size() <= 16)
			cs = l.size();
		else
			cs = 16;

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
				BufferedImage img = ImageIO.read(new File(thumbnailPath + jo.get("thumbnailName").toString()));
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

	@RequestMapping(value = "/search") /* 최대16개 로딩 */
	public @ResponseBody String changeSearchPredicate(Model model, @RequestBody String jsonTagRequest,
			@SessionAttribute(value = "user_id") String ui,
			@SessionAttribute(value = "validTaglist") List<Object> vtl) {
		JSONParser parser = new JSONParser();
		JSONObject responseJsonObject = new JSONObject();
		responseJsonObject.put("imageDataList", new JSONArray());

		int cs = 0;

		try {
			List<Object> l = Arrays
					.asList(((JSONArray) ((JSONObject) parser.parse(jsonTagRequest)).get("taglist")).toArray());
			String visibility = ((JSONObject) parser.parse(jsonTagRequest)).get("visibility") + "";

			List<String> ls = new ArrayList<>();
			for (Object o : l)
				ls.add(o.toString());

			model.addAttribute("validTaglist", l);

			List<PinInfoObject> piol;

			if (visibility.equals("true"))
				piol = tm.getPininfoListByUploaderAll(ui);
			else
				piol = tm.getPininfoListByUploaderOnlyPublic(ui);

			List<Object> validPiol = new ArrayList<>();

			for (PinInfoObject pio : piol) {
				if (Arrays.asList(pio.getTaglist().split(",")).containsAll(ls)) {
					validPiol.add(pio);
					if (cs++ <= 15) {
						BufferedImage img = ImageIO.read(new File(thumbnailPath + pio.getThumbnailName()));
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						ImageIO.write(img, "jpg", baos);
						byte[] bytes = baos.toByteArray();
						((JSONArray) responseJsonObject.get("imageDataList"))
								.add(Base64.getEncoder().encodeToString(bytes));
					}
				}
			}
			if (validPiol.size() <= 16)
				cs = validPiol.size();
			else
				cs = 16;
			responseJsonObject.put("allpinlist", validPiol);
			responseJsonObject.put("allpinsize", validPiol.size());
			responseJsonObject.put("current_pinsize", cs);
			responseJsonObject.put("current_pinlist", new ArrayList<>(validPiol.subList(0, cs)));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return responseJsonObject.toJSONString();
	}

	@RequestMapping(value = "/OriginPin")
	public @ResponseBody String getOriginPin(@SessionAttribute(value = "user_id") String ui,
			@RequestParam(value = "originPinName") String opn) {

		if (pm.checkPinByUserid(Map.of("pinName", opn, "user_id", ui)).isEmpty())
			return null;
		try {
			File f = new File(pinPath + opn);
			BufferedImage img = ImageUtil.checkImage(f, ImageUtil.getOrientation(f));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(img, ImageUtil.getExtentioString(opn), baos);
			byte[] bytes = baos.toByteArray();
			return Base64.getEncoder().encodeToString(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(value = "/deleteTagAll")
	public @ResponseBody String deleteTagAll(@SessionAttribute(value = "user_id") String ui,
			@RequestParam(value = "tagName") String tn) {
		if (tn.equalsIgnoreCase("none"))
			return "fail";
		long deleteRecordNum = tm.deleteTagByUseridAndTagName(Map.of("user_id", ui, "tagName", tn));
		out.println("삭제된 레코드 갯수 : " + deleteRecordNum);
		tm.insertTagNone(tm.selectNottingTagPinsNone(ui));
		return "success";
	}

	@RequestMapping(value = "/modifyTag")
	public @ResponseBody String modifyTagName(@SessionAttribute(value = "user_id") String ui,
			@RequestParam(value = "newTagname") String ntn, @RequestParam(value = "originalTagname") String otn) {
		if (otn.equalsIgnoreCase("none"))
			return "fail";
		long modifyRecordNum = tm.modifyTagName(Map.of("otn", otn, "ntn", ntn, "user_id", ui));
		out.println("수정된 레코드 갯수 : " + modifyRecordNum);
		return "success";
	}

	@RequestMapping(value = "/moveUploadView")
	public ModelAndView moveUploadView(Model model, @SessionAttribute(value = "user_id") String ui) {
		List<String> l = tm.findTagByUseridAll(ui);
		l.remove("none");
		model.addAttribute("tagList", l);
		return new ModelAndView("UploadPage");
	}

	
}