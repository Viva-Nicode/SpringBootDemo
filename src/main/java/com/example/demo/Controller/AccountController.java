package com.example.demo.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;

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
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.Service.AccountService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/Account")
@RequiredArgsConstructor
public class AccountController {

	@Autowired
	AccountService ac;

	@RequestMapping(value = "/moveSignupPage.do")
	public ModelAndView moveSignupPage() {
		return new ModelAndView("Signup");
	}

	@RequestMapping(value = "/checkSignupOverlap.do")
	public void checkSignupOverlap(HttpServletRequest req, HttpServletResponse resp) {
		try {
			resp.getWriter().print(ac.checkedOverlapUserInfo(req.getParameter("user_id"), req.getParameter("email"),
					req.getParameter("user_name"), req.getParameter("user_pw")) + "");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@PostMapping(value = "/login.do")
	public void login(HttpServletRequest req, HttpServletResponse resp) {

		HttpSession s = req.getSession();
		try {
			PrintWriter pr = resp.getWriter();

			int result = ac.signin(req.getParameter("user_id"), req.getParameter("user_pw"));
			if (result == 0) {
				s.setAttribute("user_id", req.getParameter("user_id"));
				pr.print(result + "");
			} else {
				pr.print(result + "");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/logout.do")
	public ResponseEntity<?>  logout(HttpServletRequest req) {
		HttpSession s = req.getSession();
		s.removeAttribute("user_id");

		HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/"));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
	}
}
