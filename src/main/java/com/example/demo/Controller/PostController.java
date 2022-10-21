package com.example.demo.Controller;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import static java.lang.Integer.parseInt;
import static java.lang.System.out;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.Service.DetectProperties;
import com.example.demo.Service.ImageLabel;
import com.example.demo.Service.LikesService;
import com.example.demo.Service.PapagoTranslationAPI;
import com.example.demo.Service.PostService;
import com.example.demo.db.CommentsMapper;
import com.example.demo.db.CommentsVO;
import com.example.demo.db.LikesId;
import com.example.demo.db.LikesRepository;
import com.example.demo.db.PostImageDTO;
import com.example.demo.db.PostImageMapper;
import com.example.demo.db.PostImageRepository;
import com.example.demo.db.PostInfoDTO;
import com.example.demo.db.PostInfoMapper;
import com.google.cloud.vision.v1.ColorInfo;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/Post")
@RequiredArgsConstructor
public class PostController {

	private final PostService ps;
	private final PostImageRepository pir;
	private final LikesRepository lr;
	private final LikesService ls;
	private final PostImageMapper pimb;
	private final CommentsMapper cm;
	private final PostInfoMapper pm;

	private final ImageLabel il;

	@RequestMapping(value = "/MoveWritePost")
	public ModelAndView moveWritePost(@SessionAttribute(value = "user_id") String ui) throws IOException {

		/*
		 * Map<String, String> tagMap = PapagoTranslationAPI
		 * .getTranslationTagList(il.getImageLabels(
		 * "classpath:static/upload/hot_air_balloon1.jpeg"));
		 * 
		 * for (Entry<String, String> elem : tagMap.entrySet())
		 * System.out.println(elem.getKey() + " " + elem.getValue());
		 * 
		 * List<ColorInfo> colorList = DetectProperties.detectProperties(
		 * "/Users/nicode./MainSpace/SpringBootDemo/demo/src/main/resources/static/upload/hot_air_balloon1.jpeg"
		 * );
		 * 
		 * for (ColorInfo c : colorList) {
		 * System.out.println(c.getPixelFraction());
		 * System.out.println(c.getColor().getRed());
		 * System.out.println(c.getColor().getGreen());
		 * System.out.println(c.getColor().getBlue());
		 * }
		 */
		return new ModelAndView("PostWriter");
	}

	@RequestMapping(value = "/InsertPost")
	public ResponseEntity<?> insertPost(HttpServletRequest req) {

		int count = 0, postid = 0;
		ArrayList<String> nameList = new ArrayList<>();
		String savePath = "/Users/nicode./MainSpace/SpringBootDemo/demo/src/main/resources/static/upload/";

		try {
			MultipartRequest MPR = new MultipartRequest(req, savePath, 1024 * 1024 * 10, "utf-8",
					new DefaultFileRenamePolicy());
			String title = MPR.getParameter("title");
			String contents = MPR.getParameter("contents");

			HttpSession s = req.getSession();

			Enumeration<?> files = MPR.getFileNames();

			while (files.hasMoreElements()) {
				count++;
				String str = (String) files.nextElement();
				String fileName = MPR.getFilesystemName(str);
				nameList.add(fileName);
			}

			if (nameList.get(0) == null)
				count = 0;

			postid = ps.insertPost(new PostInfoDTO(title, s.getAttribute("user_id") + "", contents, count));

			for (int idx = 0; idx < nameList.size(); idx++) {
				if (nameList.get(idx) != null)
					pir.save(new PostImageDTO(postid, nameList.get(idx)));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create("/Post/PostViewer/" + postid));
		return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
	}

	@RequestMapping(value = "/PostViewer/{pid}")
	public ModelAndView postViewer(HttpServletRequest req, @PathVariable(value = "pid") int pid, HttpSession s) {

		String ui = s.getAttribute("user_id") + "";

		ModelAndView mav = new ModelAndView("PostViewer");
		mav.addObject("user_id", ui);

		ps.increasedHits(pid);

		PostInfoDTO p = ps.getPost(pid);
		p.setContents(p.getContents().replace("\r\n", "<br>"));
		mav.addObject("postinfo", p);

		if (lr.existsBylikesId(new LikesId(pid, ui)))
			mav.addObject("islikeAlready", 1);
		else
			mav.addObject("islikeAlready", 0);

		List<String> l = pimb.findByPostid(pid);
		mav.addObject("imageList", l);

		List<CommentsVO> commentsList = cm.findCommentsByPostid(pid);
		for (CommentsVO c : commentsList)
			c.setC_contents(c.getC_contents().replace(System.getProperty("line.separator"), "<br>"));
		Collections.reverse(commentsList);
		mav.addObject("l", commentsList);

		return mav;
	}

	@RequestMapping(value = "/likes")
	public void dolike(@RequestBody Map<String, String> map) {
		ls.doLikes(parseInt(map.get("postid")), map.get("user_id"), parseInt(map.get("likeAlready")));
	}

	@RequestMapping(value = "/deletePost")
	public @ResponseBody String deletePost(@RequestParam(value = "postid") int postid,
			@SessionAttribute(value = "user_id") String ui) {

		String result = pm.isMyself(Map.of("postid", postid + "", "writer", ui));
		if (result.equals("true")) {
			pm.deletePost(postid);
			return "success";
		}
		return "fail";
	}
}