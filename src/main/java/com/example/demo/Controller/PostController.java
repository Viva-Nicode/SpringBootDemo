package com.example.demo.Controller;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.Service.DetectProperties;
import com.example.demo.Service.ImageLabel;
import com.example.demo.Service.LikesService;
import com.example.demo.Service.PostService;
import com.example.demo.db.CommentsMapper;
import com.example.demo.db.CommentsVO;
import com.example.demo.db.LikesId;
import com.example.demo.db.LikesRepository;
import com.example.demo.db.PostImageDTO;
import com.example.demo.db.PostImageMapper;
import com.example.demo.db.PostImageRepository;
import com.example.demo.db.PostInfoDTO;
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

	private final ImageLabel il;

	@RequestMapping(value = "/MoveWritePost")
	public ModelAndView moveWritePost(HttpServletRequest req) throws IOException {

		il.getImageLabels("classpath:static/upload/456.jpg");
		DetectProperties.detectProperties("/Users/nicode./MainSpace/SpringBootDemo/demo/src/main/resources/static/456.jpg");

		HttpSession s = req.getSession();
		String user_id = s.getAttribute("user_id") + "";
		if (user_id.equals("null")) {
			return new ModelAndView("redirect:/");
		}
		return new ModelAndView("PostWriter");
	}

	@RequestMapping(value = "/InsertPost")
	public ResponseEntity<?> insertPost(HttpServletRequest req) {

		int count = 0;
		ArrayList<String> nameList = new ArrayList<>();
		String savePath = "/Users/nicode./MainSpace/SpringBootDemo/demo/src/main/resources/static/upload/";

		try {
			MultipartRequest MPR = new MultipartRequest(req, savePath, 1024 * 1024 * 10, "utf-8",
					new DefaultFileRenamePolicy());
			String title = MPR.getParameter("title");
			String contents = MPR.getParameter("contents");

			HttpSession s = req.getSession();

			Enumeration files = MPR.getFileNames();

			while (files.hasMoreElements()) {
				count++;
				String str = (String) files.nextElement();
				String fileName = MPR.getFilesystemName(str);
				nameList.add(fileName);
			}

			if (nameList.get(0) == null)
				count = 0;

			int postid = ps.insertPost(new PostInfoDTO(title, s.getAttribute("user_id") + "", contents, count));

			for (int idx = 0; idx < nameList.size(); idx++) {
				if (nameList.get(idx) != null)
					pir.save(new PostImageDTO(postid, nameList.get(idx)));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create("/"));
		return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
	}

	@RequestMapping(value = "/PostViewer")
	public ModelAndView postViewer(HttpServletRequest req) {
		HttpSession s = req.getSession();

		ModelAndView mav = new ModelAndView("PostViewer");
		String user_id = s.getAttribute("user_id") + "";
		mav.addObject("user_id", user_id);

		int postid = Integer.parseInt(req.getParameter("postid"));
		mav.addObject("postid", postid);

		ps.increasedHits(postid);

		PostInfoDTO p = ps.getPost(postid);

		String contents = p.getContents();
		mav.addObject("contents", contents.replace("\r\n", "<br>"));
		mav.addObject("title", p.getTitle());
		mav.addObject("likes", p.getLikes());
		mav.addObject("hits", p.getHits());
		mav.addObject("writer", p.getWriter());
		mav.addObject("wTime", p.getWrittentime().toString().substring(0, p.getWrittentime().toString().length() - 2));
		if (lr.existsBylikesId(new LikesId(postid, user_id)))
			mav.addObject("islikeAlready", 1);
		else
			mav.addObject("islikeAlready", 0);

		List<String> l = pimb.findByPostid(postid);
		mav.addObject("imageList", l);
		List<CommentsVO> ll = cm.findCommentsByPostid(postid);
		Collections.reverse(ll);
		mav.addObject("l", ll);

		return mav;
	}

	@RequestMapping(value = "/likes")
	public void dolike(HttpServletRequest req) {

		ls.doLikes(Integer.parseInt(req.getParameter("postid")), req.getParameter("user_id"),
				Integer.parseInt(req.getParameter("likeAlready")));
	}

}
