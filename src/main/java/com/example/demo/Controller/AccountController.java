package com.example.demo.Controller;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.Service.AccountService;
import com.example.demo.db.UserMapper;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/Account")
@RequiredArgsConstructor
public class AccountController {

	@Autowired
	AccountService ac;

	@Autowired
	UserMapper um;

	@RequestMapping(value = "/moveSignupPage")
	public ModelAndView moveSignupPage() {
		return new ModelAndView("Signup");
	}

	@RequestMapping(value = "/moveUserInfoSetting")
	public ModelAndView moveUserSetting(HttpServletRequest req) {
		ModelAndView mav = new ModelAndView("UserInfoSetting");
		mav.addObject("email", um.findUserByID(req.getSession().getAttribute("user_id") + "").get(0).getEmail());
		return mav;
	}

	@RequestMapping(value = "/checkSignupOverlap")
	public @ResponseBody String checkSignupOverlap(HttpServletRequest req) {
		return ac.checkedOverlapUserInfo(req.getParameter("user_id"), req.getParameter("email"),
				req.getParameter("user_pw")) + "";
	}

	@RequestMapping(value = "/checkEmail")
	public @ResponseBody String checkOverlapEmail(HttpServletRequest req) {
		return ac.checkedOverlapEmail(req.getParameter("email")) + "";
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
	public @ResponseBody String login(HttpServletRequest req, HttpServletResponse resp) {
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
	public ResponseEntity<?> logout(HttpServletRequest req) {
		HttpSession s = req.getSession();
		s.removeAttribute("user_id");
		s.removeAttribute("profile");

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create("/"));
		return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
	}
}
