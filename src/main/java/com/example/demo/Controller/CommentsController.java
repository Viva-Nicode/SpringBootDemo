package com.example.demo.Controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.example.demo.db.CommentsMapper;
import com.example.demo.db.CommentsVO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/Comments")
@RequiredArgsConstructor
public class CommentsController {

	private final CommentsMapper cm;

	@RequestMapping(value = "/sendComment", produces = "application/text;charset=utf8")
	public @ResponseBody String sendComment(HttpServletRequest req, HttpServletResponse resp,
			@SessionAttribute(value = "user_id") String ui) throws Exception {

		if (req.getMethod().equals("GET"))
			throw new Exception();

		resp.setHeader("Content-Type", "charset=utf-8");
		String result = "";

		HashMap<String, Object> hm;

		int postid = Integer.parseInt(req.getParameter("postid"));
		String comment = req.getParameter("comment");

		cm.insertComment(new CommentsVO(postid, ui, comment));

		List<CommentsVO> l = cm.findCommentsByPostid(postid);
		Collections.reverse(l);

		JSONObject totalEntry = new JSONObject();
		JSONArray commentArray = new JSONArray();

		for (CommentsVO c : l) {
			hm = new HashMap<String, Object>();
			hm.put("commenter", c.getCommenter());
			hm.put("c_time", c.getC_time() + "");
			hm.put("c_contents", c.getC_contents().replace(System.getProperty("line.separator"), "<br>"));
			commentArray.add(new JSONObject(hm));
		}

		totalEntry.put("commentInfoArray", commentArray);
		result = totalEntry.toJSONString();

		return result;
	}

	@RequestMapping(value = "/deleteComment")
	public @ResponseBody String deleteComment(@RequestParam(value = "commentid") String commentid,
			@SessionAttribute(value = "user_id") String ui) {

		String result = cm.isMyselfComment(Map.of("commentid", commentid, "commenter", ui));
		if (result.equals("true")) {
			cm.deleteCommentByCommentid(Integer.parseInt(commentid));
			return "success";
		}
		return "fail";
	}
}
