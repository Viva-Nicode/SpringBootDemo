package com.example.demo.Controller;

import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
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

		System.out.println("index request");
		ModelAndView mav = new ModelAndView("../static/index");
		ServletContext application = request.getServletContext();
		application.setAttribute("ip", "localhost");

		List<PostInfoDTO> l = u.findAll();
		if (l.isEmpty()) {
			System.out.println("list is empty");
		}

		mav.addObject("postlist", l);
		return mav;
	}
}
