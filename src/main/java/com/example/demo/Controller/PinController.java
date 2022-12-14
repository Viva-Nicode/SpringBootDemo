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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;

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
import com.example.demo.Service.PinTagsModifyProcesser;
import com.example.demo.Service.Tagger;
import com.example.demo.db.PinMapper;
import com.example.demo.db.PinVO;
import com.example.demo.db.Sys_tagMapper;
import com.example.demo.db.Sys_tagVO;
import com.example.demo.db.TagMapper;
import com.example.demo.db.TagVO;

import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@RequestMapping("/Pin")
@RequiredArgsConstructor
@SessionAttributes(value = { "visibility" })

public class PinController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final GoogleVisionAPI googleVisionAPI;
	private final PinMapper pm;
	private final TagMapper tm;
	private final Sys_tagMapper stm;
	private final String pinPath = "/Users/nicode./MainSpace/SpringBootDemo/demo/src/main/resources/static/pins/";
	private final String thumbnailPath = "/Users/nicode./MainSpace/SpringBootDemo/demo/src/main/resources/static/Thumbnail/";
	private final String trashPath = "/Users/nicode./MainSpace/SpringBootDemo/demo/src/main/resources/static/deleted/";
	private final Tagger tagger;

	@RequestMapping(value = "/uploadPins")
	public @ResponseBody String uploadPins(@RequestParam(value = "pins") MultipartFile pins,
			@RequestParam(value = "appliedTagList") List<String> appliedTagList,
			@SessionAttribute(value = "user_id") String ui) {

		final String ext = pins.getContentType().split("/")[1];
		final String uuidPinName = UUID.randomUUID() + "." + ext;
		File dest = new File(pinPath + uuidPinName);
		boolean visi;
		
		try {
			synchronized (this) {
				BufferedInputStream bis = new BufferedInputStream(pins.getInputStream());
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dest));

				int bytesRead = 0;
				byte[] buffer = new byte[1024];
				while ((bytesRead = bis.read(buffer, 0, 1024)) != -1)
					bos.write(buffer, 0, bytesRead);
				bos.close();
				bis.close();
			}
			Thread.sleep(1000);
			BufferedImage newbi = ImageUtil.checkImage(dest, ImageUtil.getOrientation(dest));

			/* appliedTagList ????????? ???????????? ????????? ????????? ????????? ????????????. */
			if (appliedTagList.get(appliedTagList.size() - 1).equals("true"))
				visi = true;
			else
				visi = false;

			/* ?????? ???????????? ????????? ????????? ????????????. */
			List<Sys_tagVO> systemTagList;

			systemTagList = googleVisionAPI.getImageLabels(
					"classpath:static/pins/" + uuidPinName);

			/* db??? pin ???????????? insert??????. */
			pm.insertPin(new PinVO(uuidPinName, ui,
					ImageUtil.getResolutionRatio(newbi.getWidth(),
							newbi.getHeight()),
					visi));

			/* ????????? ????????? insert ??????. */
			for (Sys_tagVO s : systemTagList)
				s.setPinName(uuidPinName);
			stm.insertSystag(systemTagList);

			tagger.doAutoTagging(appliedTagList, systemTagList, uuidPinName);

			/* ???????????? ????????????. */
			/* ???????????? ??????????????? ???????????? ??????. */
			if (ext.equals("png")) {

				ImageUtil.pngToJpg(pinPath + uuidPinName, thumbnailPath + uuidPinName);

				Thread.sleep(3000);
				if (!new File(thumbnailPath + uuidPinName).exists())
					Thread.sleep(2000);

				ImageUtil.compress(ImageUtil.changeExtension(thumbnailPath + uuidPinName, "jpg"));
			} else {
				ImageUtil.compress(pinPath + uuidPinName,
						ImageUtil.changeExtension(thumbnailPath + uuidPinName, "jpg"));
			}

		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			logger.error("Generated IOException during the save " + uuidPinName);
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (org.springframework.cloud.gcp.vision.CloudVisionException e) {
			e.printStackTrace();
			logger.error("Generated cloud.gcp.vision.CloudVisionException during the save " + uuidPinName);
			dest.delete();
			return "{ \"response\" : \"fail\"}";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "{ \"response\" : \"success\" }";
	}

	@RequestMapping(value = "/movePinViewer") /* 16????????? ????????????. */
	public ModelAndView movePinView(Model model, @SessionAttribute(value = "user_id") String ui,
			@RequestParam(value = "visibility") String visibility) {
		int cs = 0;

		List<PinInfoObject> l;
		List<String> taglist;
		if (visibility.equals("true")) {
			l = tm.getPininfoListByUploaderAll(ui);
			taglist = tm.findTagByUseridAll(ui);
			model.addAttribute("taglist", taglist);
		} else {
			l = tm.getPininfoListByUploaderOnlyPublic(ui);
			taglist = tm.findTagByUseridOnlyPublic(ui);
			model.addAttribute("taglist", taglist);
		}

		List<Integer> listForTagColor = new ArrayList<Integer>();
		int n = 1;
		for (int idx = 0; idx < taglist.size(); idx++) {
			listForTagColor.add(n++);
			if (n == 5)
				n = 1;
		}

		model.addAttribute("listForTagColor", listForTagColor);
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

	@RequestMapping(value = "/morePins") /* 16?????? ?????? */
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

	@RequestMapping(value = "/search") /* ??????16??? ?????? */
	public @ResponseBody String changeSearchPredicate(Model model, @RequestBody String jsonTagRequest,
			@SessionAttribute(value = "user_id") String ui) {
		JSONParser parser = new JSONParser();
		JSONObject responseJsonObject = new JSONObject();
		responseJsonObject.put("imageDataList", new JSONArray());

		int cs = 0;

		try {
			List<Object> l = Arrays
					.asList(((JSONArray) ((JSONObject) parser.parse(jsonTagRequest)).get("taglist")).toArray());
			String visibility = ((JSONObject) parser.parse(jsonTagRequest)).get("visibility") + "";
			String searchMod = ((JSONObject) parser.parse(jsonTagRequest)).get("SearchMod") + "";

			List<String> ls = new ArrayList<>();
			for (Object o : l)
				ls.add(o.toString());

			List<PinInfoObject> piol;

			if (visibility.equals("true"))
				piol = tm.getPininfoListByUploaderAll(ui);
			else
				piol = tm.getPininfoListByUploaderOnlyPublic(ui);

			List<PinInfoObject> validPiol = new ArrayList<>(); // ?????? ????????? ??????????????? ???????????? ?????? ????????? ?????? ??????

			for (PinInfoObject pio : piol) {
				List<String> temp = Arrays.asList(pio.getTaglist().split(","));
				if (searchMod.equals("false")) {
					if (temp.containsAll(ls)) {
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
				} else if (searchMod.equals("true")) {
					for (String t : ls) {
						if (temp.contains(t)) {
							validPiol.add(pio);
							if (cs++ <= 15) {
								BufferedImage img = ImageIO.read(new File(thumbnailPath + pio.getThumbnailName()));
								ByteArrayOutputStream baos = new ByteArrayOutputStream();
								ImageIO.write(img, "jpg", baos);
								byte[] bytes = baos.toByteArray();
								((JSONArray) responseJsonObject.get("imageDataList"))
										.add(Base64.getEncoder().encodeToString(bytes));
							}
							break;
						}
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
		out.println("????????? ????????? ?????? : " + deleteRecordNum);
		List<String> l = tm.selectNottingTagPinsNone(ui);
		if (l.size() > 0)
			tm.insertTagNone(l);

		return "success";
	}

	@RequestMapping(value = "/modifyTag")
	public @ResponseBody String modifyTagName(@SessionAttribute(value = "user_id") String ui,
			@RequestParam(value = "newTagname") String ntn, @RequestParam(value = "originalTagname") String otn) {
		if (otn.equalsIgnoreCase("none"))
			return "fail";
		long modifyRecordNum = tm.modifyTagName(Map.of("otn", otn, "ntn", ntn, "user_id", ui));
		out.println("????????? ????????? ?????? : " + modifyRecordNum);
		return "success";
	}

	@RequestMapping(value = "/moveUploadView")
	public ModelAndView moveUploadView(Model model, @SessionAttribute(value = "user_id") String ui) {
		List<String> l = tm.findTagByUseridAll(ui);
		l.remove("none");
		model.addAttribute("tagList", l);
		return new ModelAndView("UploadPage");
	}

	@RequestMapping(value = "/modifyPin")
	public @ResponseBody String modifyPinTags(@SessionAttribute(value = "user_id") String ui,
			@RequestBody String request) {
		JSONParser jsonParser = new JSONParser();

		try {
			/* ?????? ????????? ?????? ?????? */
			JSONObject jo = (JSONObject) jsonParser.parse(request);
			Map<String, List<String>> m = new HashMap<>();
			m.put("modifyPinlist", new ArrayList<String>());
			m.put("commonTagList", new ArrayList<String>());
			m.put("complementTaglist", new ArrayList<String>());
			JSONArray ja = ((JSONArray) jo.get("modifyPinlist"));
			for (int i = 0; i < ja.size(); i++)
				m.get("modifyPinlist").add(ja.get(i).toString());
			ja = ((JSONArray) jo.get("commonTagList"));
			for (int i = 0; i < ja.size(); i++)
				m.get("commonTagList").add(ja.get(i).toString());
			ja = ((JSONArray) jo.get("complementTaglist"));
			for (int i = 0; i < ja.size(); i++)
				m.get("complementTaglist").add(ja.get(i).toString());
			/* ?????? ??? */

			/*
			 * out.println("modifyPinlist");
			 * m.get("modifyPinlist").forEach((String s) -> {
			 * out.println(s);
			 * });
			 * out.println("commonTagList");
			 * m.get("commonTagList").forEach((String s) -> {
			 * out.println(s);
			 * });
			 * out.println("complementTaglist");
			 * m.get("complementTaglist").forEach((String s) -> {
			 * out.println(s);
			 * });
			 */

			List<String> uploaderCheckList = tm.checkPinHostUser(m.get("modifyPinlist"));

			for (int idx = 0; idx < uploaderCheckList.size(); idx++) {
				if (!uploaderCheckList.get(idx).equals(ui)) {
					logger.error(uploaderCheckList.get(idx) + "is overlap pinanem " + ui);
					return "pinName overlap";
				}
			}

			List<TagVO> modifyPinsTaglist = tm.getModifyPinTaglist(m.get("modifyPinlist"));
			Map<String, List<TagVO>> rm = PinTagsModifyProcesser.processingModification(m, modifyPinsTaglist);

			/*
			 * out.println("insert");
			 * for (tagVO t : rm.get("insertQueryList"))
			 * out.println(t.getPinName() + " " + t.getTagName());
			 * 
			 * out.println("delete");
			 * for (tagVO t : rm.get("deleteQueryList"))
			 * out.println(t.getTagName() + " " + String.valueOf(t.getTagid()));
			 */

			if (rm.get("insertQueryList").size() > 0) {
				tm.insertTag(rm.get("insertQueryList"));
				tm.deleteNone(rm.get("insertQueryList"));
			}
			if (rm.get("deleteQueryList").size() > 0) {
				tm.deleteTag(rm.get("deleteQueryList"));
				tm.insertTagNone(tm.selectNottingTagPinsNone(ui));
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	@RequestMapping(value = "/deletePin")
	public @ResponseBody String deletePin(@SessionAttribute(value = "user_id") String ui,
			@RequestParam(value = "deletepinlist[]") List<String> s) {
		List<String> uploaderList = tm.checkPinHostUser(s);
		try {
			BufferedInputStream bis;
			BufferedOutputStream bos;

			for (String uploader : uploaderList) {
				if (!uploader.equals(ui))
					return "fail";
			}
			pm.deletePins(s);
			for (String pinName : s) {
				bis = new BufferedInputStream(new FileInputStream(new File(pinPath + pinName)));
				bos = new BufferedOutputStream(new FileOutputStream(new File(trashPath + pinName)));
				int bytesRead = 0;
				byte[] buffer = new byte[1024];
				while ((bytesRead = bis.read(buffer, 0, 1024)) != -1)
					bos.write(buffer, 0, bytesRead);
				bos.close();
				bis.close();
				new File(pinPath + pinName).delete();
				new File(thumbnailPath + ImageUtil.changeExtension(pinName, "jpg")).delete();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "";
	}

	@RequestMapping(value = "/Main")
	public ModelAndView moveMain(HttpSession s) {
		ModelAndView mav = new ModelAndView("Main");
		return mav;
	}
}