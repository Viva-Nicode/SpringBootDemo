package com.example.demo.Controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.Service.TimeConverter;
import com.example.demo.db.PostInfoDTO;
import com.example.demo.db.PostRepository;

import lombok.RequiredArgsConstructor;

/* 223.130.195.200 */

@Controller
@RequiredArgsConstructor
public class IndexController {

	@RequestMapping(value = "/")
	public ModelAndView indexRequest(HttpSession s) {

		ModelAndView mav = new ModelAndView("../static/index");
		
		s.setAttribute("PATH", "localhost");
		/* s.setAttribute("PATH", "116.39.246.101"); */
		return mav;
	}
}
