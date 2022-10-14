package com.example.demo.Controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.db.CommentsMapper;
import com.example.demo.db.CommentsVO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/Comments")
@RequiredArgsConstructor
public class CommentsController {

	private final CommentsMapper cm;

	@RequestMapping(value = "/sendComment", produces = "application/text;charset=utf8")
	public @ResponseBody String sendComment(HttpServletRequest req, HttpServletResponse resp) {

		resp.setHeader("Content-Type", "charset=utf-8");
		String result = "";

		HashMap<String, Object> hm;

		String commenter = req.getParameter("user_id");
		int postid = Integer.parseInt(req.getParameter("postid"));
		String comment = req.getParameter("comment");

		cm.insertComment(new CommentsVO(postid, commenter, comment));

		List<CommentsVO> l = cm.findCommentsByPostid(postid);
		Collections.reverse(l);

		JSONObject totalEntry = new JSONObject();
		JSONArray commentArray = new JSONArray();

		for (CommentsVO c : l) {
			hm = new HashMap<String, Object>();
			hm.put("commenter", c.getCommenter());
			hm.put("c_time", c.getC_time() + "");
			hm.put("c_contents", c.getC_contents());
			commentArray.add(new JSONObject(hm));
		}

		totalEntry.put("commentInfoArray", commentArray);
		result = totalEntry.toJSONString();

		return result;
	}

	@RequestMapping(value = "/deleteComment")
	public @ResponseBody String deleteComment(HttpServletRequest req, HttpServletResponse resp) {
		cm.deleteCommentByCommentid(Integer.parseInt(req.getParameter("commentid") + ""));
		return "";
	}
}
