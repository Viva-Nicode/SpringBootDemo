package com.example.demo.Controller;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.Service.AccountService;
import com.example.demo.db.CommentsMapper;
import com.example.demo.db.MyComments;
import com.example.demo.db.PostInfoMapper;
import com.example.demo.db.UserMapper;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/Account")
@RequiredArgsConstructor
@SessionAttributes(value = "user_id")
public class AccountController {

	private final AccountService ac;
	private final UserMapper um;
	private final PostInfoMapper pm;
	private final CommentsMapper cm;

	@RequestMapping(value = "/moveSignupPage")
	public ModelAndView moveSignupPage() {
		return new ModelAndView("Signup");
	}

	/*
	 * 알게된 사실
	 * 1. 아래와 같이 매개변수로 모델을 넣어주고 값 바인딩하면 알아서 view로 전달된다.
	 */
	@RequestMapping(value = "/moveUserInfoSetting")
	public String moveUserSetting(@SessionAttribute(value = "user_id") String ui, Model model) {
		model.addAttribute("email", um.findUserByID(ui).get(0).getEmail());
		model.addAttribute("IwroteitList", pm.findByUserid(ui));
		List<MyComments> l = cm.findByCommenter(ui);

		for (MyComments c : l) {
			c.setC_contents(c.getC_contents().replace(System.getProperty("line.separator"), "<br>").replace("[", "[[")
					.replace("]", "]]").replace("{", "{{").replace("}", "}}").replace(",", ",,").replace("'", "&quot")
					.replace("\"", "&quot"));
		}
		Collections.reverse(l);

		model.addAttribute("commentList", l);
		return "UserInfoSetting";
	}

	@RequestMapping(value = "/checkSignupOverlap")
	public @ResponseBody String checkSignupOverlap(@RequestBody Map<String, String> map) {
		return ac.checkedOverlapUserInfo(map.get("user_id"), map.get("email"), map.get("user_pw")) + "";
	}

	@RequestMapping(value = "/checkEmail")/* email은 {email : email}같이 ajax로 보낸 데이터이다. */
	public @ResponseBody String checkOverlapEmail(@ModelAttribute("email") String email) {
		return ac.checkedOverlapEmail(email) + "";
	}

	@RequestMapping(value = "/profile")
	public ModelAndView uploadprofile(HttpServletRequest req) {
		Map<String, String> m = new HashMap<>();
		ModelAndView mav = new ModelAndView("UserInfoSetting");
		final String savePath = "/Users/nicode./MainSpace/SpringBootDemo/demo/src/main/resources/static/profile/";

		HttpSession s = req.getSession();
		try {
			MultipartRequest MPR = new MultipartRequest(req, savePath, 1024 * 1024 * 10, "utf-8",
					new DefaultFileRenamePolicy());

			String email = MPR.getParameter("email");
			String imageName = MPR.getFilesystemName("imagefile"); // null or image name

			m.put("user_id", s.getAttribute("user_id") + "");

			if (!um.findUserByID(s.getAttribute("user_id") + "").get(0).getEmail().equals(email)) {
				m.put("newEmail", email);
				um.updateUserEmail(m);
			}
			mav.addObject("email", email);
			mav.addObject("reroad", 1);
			m.put("profileImageName", imageName);
			um.insertUserProfileImage(m);
			s.setAttribute("profile", imageName);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return mav;
	}

	@PostMapping(value = "/login")
	@ResponseBody
	public String login(HttpServletRequest req, HttpServletResponse resp) {
		HttpSession s = req.getSession();

		int result = ac.signin(req.getParameter("user_id"), req.getParameter("user_pw"));

		if (result == 0) {
			s.setAttribute("user_id", req.getParameter("user_id"));
			s.setAttribute("profile", um.findUserByID(req.getParameter("user_id")).get(0).getProfile());
			return result + "";
		} else {
			return result + "";
		}
	}

	@RequestMapping(value = "/logout")
	public ResponseEntity<?> logout(HttpServletRequest req, SessionStatus session) {
		session.setComplete();

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create("/"));
		return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
	}
}
