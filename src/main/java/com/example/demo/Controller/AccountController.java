package com.example.demo.Controller;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static java.lang.System.out;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.Service.AccountService;
import com.example.demo.Service.Tagger;
import com.example.demo.db.CommentsMapper;
import com.example.demo.db.MyComments;
import com.example.demo.db.PostInfoMapper;
import com.example.demo.db.PostInfoVO;
import com.example.demo.db.TagCount;
import com.example.demo.db.TagMapper;
import com.example.demo.db.UserMapper;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/Account")
@RequiredArgsConstructor
@CrossOrigin
@SessionAttributes(value = { "user_id", "commentList", "IwroteitList", "likepostlist" })
public class AccountController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final AccountService ac;
	private final UserMapper um;
	private final PostInfoMapper pm;
	private final CommentsMapper cm;
	private final TagMapper tm;
	private final Tagger representingKeywordSessionBinder;

	@RequestMapping(value = "/moveSignupPage")
	public ModelAndView moveSignupPage() {
		return new ModelAndView("Signup");
	}

	@RequestMapping(value = "/moveUserInfoSetting")
	public String moveUserSetting(Model model, @SessionAttribute(value = "user_id") String ui) {

		model.addAttribute("email", um.findUserByID(ui).get(0).getEmail());

		List<MyComments> l = cm.findByCommenter(ui);

		for (MyComments c : l) {
			c.setC_contents(c.getC_contents().replace(System.getProperty("line.separator"), "<br>").replace("[", "[[")
					.replace("]", "]]").replace("{", "{{").replace("}", "}}").replace(",", ",,").replace("'", "&quot")
					.replace("\"", "&quot"));
		}

		Collections.reverse(l);
		model.addAttribute("commentList", l);

		List<PostInfoVO> postlist = pm.findByUserid(ui);
		for (PostInfoVO elem : postlist) {
			elem.setWrittenTime(elem.getWrittenTime().replace(":", "."));
			elem.setContents("");
		}
		model.addAttribute("IwroteitList", postlist);

		List<PostInfoVO> likepostlist = pm.getLikepostByUserid(ui);

		for (PostInfoVO elem : likepostlist) {
			elem.setWrittenTime(elem.getWrittenTime().replace(":", "."));
			elem.setContents("");
		}

		model.addAttribute("likepostlist", likepostlist);
		List<TagCount> taglist = tm.getTagCountList(ui);

		for (int idx = 0; idx < taglist.size(); idx++) {
			taglist.get(idx).setForhtmlid(idx);
		}

		model.addAttribute("tagCountList", taglist);

		return "UserInfoSetting";
	}

	@RequestMapping(value = "/checkSignupOverlap")
	public @ResponseBody String checkSignupOverlap(@RequestBody Map<String, String> map) {
		return ac.checkedOverlapUserInfo(map.get("user_id"), map.get("email"), map.get("user_pw")) + "";
	}

	@RequestMapping(value = "/checkEmail") /* email은 {email : email}같이 ajax로 보낸 데이터이다. */
	public @ResponseBody String checkOverlapEmail(@ModelAttribute("email") String email) {
		return ac.checkedOverlapEmail(email) + "";
	}

	@RequestMapping(value = "/profile")
	public String uploadprofile(@RequestParam(value = "email") String email,
			@RequestParam(value = "imagefile") MultipartFile f, @SessionAttribute("user_id") String ui, HttpSession s,
			Model model) {
		Map<String, String> m = new HashMap<>();
		/* redirect:/Account/moveUserInfoSetting */
		final String savePath = "/Users/nicode./MainSpace/SpringBootDemo/demo/src/main/resources/static/profile/";

		m.put("user_id", ui);

		if (!um.findUserByID(ui).get(0).getEmail().equals(email)) {
			m.put("newEmail", email);
			um.updateUserEmail(m);
		}

		if (f.isEmpty()) {
			m.put("profileImageName", null);
			um.insertUserProfileImage(m);
			s.setAttribute("profileImageName", null);
		} else {
			File dest = new File(savePath + f.getOriginalFilename());
			try {
				f.transferTo(dest);
				m.put("profileImageName", f.getOriginalFilename());
				um.insertUserProfileImage(m);
				s.setAttribute("profileImageName", f.getOriginalFilename());
			} catch (IllegalStateException | IOException e) {
				e.printStackTrace();
			}
		}

		model.addAttribute("email", email);
		model.addAttribute("reroad", 1);

		return "UserinfoSetting";
	}

	@PostMapping(value = "/login")
	@ResponseBody
	public String login(HttpServletRequest req) {
		HttpSession s = req.getSession();

		int result = ac.signin(req.getParameter("user_id"), req.getParameter("user_pw"));

		if (result == 0) {
			logger.info(req.getParameter("user_id") + " is login");
			s.setAttribute("user_id", req.getParameter("user_id"));
			s.setAttribute("profileImageName", um.findUserByID(req.getParameter("user_id")).get(0).getProfile());
			representingKeywordSessionBinder.bindingRepresentKeywordsAtSession(req.getParameter("user_id"));
			s.setAttribute("tagger", representingKeywordSessionBinder);
			return result + "";
		} else {
			return result + "";
		}
	}

	@RequestMapping(value = "/logout")
	public ResponseEntity<?> logout(HttpServletRequest req, SessionStatus session, HttpSession s) {
		session.setComplete();
		s.invalidate();

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create("/"));
		return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
	}
}
