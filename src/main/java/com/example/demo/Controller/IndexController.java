package com.example.demo.Controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.Service.TimeConverter;
import com.example.demo.db.PostInfoDTO;
import com.example.demo.db.PostRepository;
import lombok.RequiredArgsConstructor;

/* 116.39.246.101 */
/* 223.130.195.200 */

@Controller
@RequiredArgsConstructor
public class IndexController {

	@Autowired
	final private PostRepository u;

	@RequestMapping(value = "/")
	public ModelAndView ex1(HttpServletRequest request) {

		ModelAndView mav = new ModelAndView("../static/index");
		List<PostInfoDTO> l = u.findAll();
		List<String> writenTimeList = new ArrayList<>();

		l.forEach(x -> {
			writenTimeList.add(TimeConverter.convertTime(x.getWrittentime()));
		});
		
		Collections.reverse(l);
		Collections.reverse(writenTimeList);

		mav.addObject("postlist", l);
		mav.addObject("wtl", writenTimeList);

		return mav;
	}
}
