package com.example.demo.Interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.demo.db.CommentsMapper;
import com.example.demo.db.PostInfoMapper;

@Component
public class PermissionInterceptor implements HandlerInterceptor {

	@Autowired
	private PostInfoMapper postInfoMapper;

	@Autowired
	private CommentsMapper commentMapper;

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler)
			throws Exception {
		String requestURI = req.getRequestURI();
		System.out.println(requestURI);
		return true;
	}
}