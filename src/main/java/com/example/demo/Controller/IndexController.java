package com.example.demo.Controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;

/* 223.130.195.200 */

@Controller
@RequiredArgsConstructor
public class IndexController {

	@RequestMapping(value = "/")
	public ModelAndView indexRequest(HttpSession s) {
		ModelAndView mav;

		/* s.setAttribute("PATH", "116.39.246.101"); */
		s.setAttribute("PATH", "localhost");
		String ui = s.getAttribute("user_id") + "";
		if (ui.equals("null")) {
			mav = new ModelAndView("../static/index");
		} else {
			mav = new ModelAndView("redirect:/Pin/Main");
		}
		return mav;
	}
}
