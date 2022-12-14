package com.example.demo.Controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static java.lang.Integer.parseInt;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.Service.LikesService;
import com.example.demo.Service.PostService;
import com.example.demo.Service.TimeConverter;
import com.example.demo.db.CommentsMapper;
import com.example.demo.db.CommentsVO;
import com.example.demo.db.LikesId;
import com.example.demo.db.LikesRepository;
import com.example.demo.db.PostImageDTO;
import com.example.demo.db.PostImageMapper;
import com.example.demo.db.PostImageRepository;
import com.example.demo.db.PostInfoDTO;
import com.example.demo.db.PostInfoMapper;
import com.example.demo.db.PostRepository;

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
	final private PostRepository u;

	@RequestMapping(value = "/MoveBoard")
	public ModelAndView indexRequest(HttpSession s) {

		ModelAndView mav = new ModelAndView("BulletinBoard");
		List<PostInfoDTO> l = u.findAll();
		List<String> writenTimeList = new ArrayList<>();
		/* wordsAPI.getDictionaryInfomation("brand"); */

		l.forEach(x -> {
			writenTimeList.add(TimeConverter.convertTime(x.getWrittentime()));
		});

		Collections.reverse(l);
		Collections.reverse(writenTimeList);
		mav.addObject("postlist", l);
		mav.addObject("wtl", writenTimeList);
		s.setAttribute("PATH", "localhost");
		/* s.setAttribute("PATH", "116.39.246.101"); */
		return mav;
	}

	@RequestMapping(value = "/MoveWritePost")
	public ModelAndView moveWritePost(@SessionAttribute(value = "user_id") String ui) throws IOException {

		return new ModelAndView("PostWriter");
	}

	@RequestMapping(value = "/InsertPost")
	public ResponseEntity<?> insertPost(@RequestParam(value = "title") String title,
			@RequestParam(value = "contents") String contents, @RequestParam(value = "imagefile") MultipartFile f,
			@SessionAttribute("user_id") String ui) {
		int postid = 0, imageNum = 0;
		String des = "";

		if (!f.isEmpty()) {
			imageNum = 1;
			des = UUID.randomUUID() + "." + f.getContentType().split("/")[1];
			File dest = new File(
					"/Users/nicode./MainSpace/SpringBootDemo/demo/src/main/resources/static/upload/" + des);
			try {
				BufferedInputStream bis = new BufferedInputStream(f.getInputStream());
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dest));

				int bytesRead = 0;
				byte[] buffer = new byte[1024];
				while ((bytesRead = bis.read(buffer, 0, 1024)) != -1)
					bos.write(buffer, 0, bytesRead);
				bos.close();
				bis.close();
				Thread.sleep(1000);/* ????????? ?????? ???????????? ?????? ????????? ?????? ????????? ????????? sleep */

			} catch (IllegalStateException | IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}

		postid = ps.insertPost(new PostInfoDTO(title, ui, contents, imageNum));

		if (!f.isEmpty())
			pir.save(new PostImageDTO(postid, des));

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create("/Post/PostViewer/" + postid));
		return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
	}

	@RequestMapping(value = "/PostViewer/{pid}")
	public ModelAndView postViewer(HttpServletRequest req, @PathVariable(value = "pid") int pid, HttpSession s) {

		String ui = s.getAttribute("user_id") + "";

		ModelAndView mav = new ModelAndView("PostViewer");
		/* mav.addObject("user_id", ui); */

		ps.increasedHits(pid);

		PostInfoDTO p = ps.getPost(pid);
		p.setContents(p.getContents().replace("\r\n", "<br>"));
		mav.addObject("postinfo", p);

		if (lr.existsBylikesId(new LikesId(pid, ui)))
			mav.addObject("islikeAlready", 1);
		else
			mav.addObject("islikeAlready", 0);

		String l = pimb.findByPostid(pid);
		mav.addObject("image", l);

		List<CommentsVO> commentsList = cm.findCommentsByPostid(pid);
		for (CommentsVO c : commentsList)
			c.setC_contents(c.getC_contents().replace(System.getProperty("line.separator"), "<br>"));
		Collections.reverse(commentsList);
		mav.addObject("l", commentsList);

		return mav;
	}

	@RequestMapping(value = "/likes")
	public void dolike(@RequestBody Map<String, String> map, @SessionAttribute(value = "user_id") String ui) {
		ls.doLikes(parseInt(map.get("postid")), ui, parseInt(map.get("likeAlready")));
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