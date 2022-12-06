package com.example.demo.Controller;

import java.sql.SQLIntegrityConstraintViolationException;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

	@ExceptionHandler(ServletRequestBindingException.class)
	public String e1(Model model) {
		model.addAttribute("errorMsg", "로그인해라 멍청아");
		return "ExceptionPage";
	}

	@ExceptionHandler(IndexOutOfBoundsException.class)
	public String e2(Model model) {
		model.addAttribute("errorMsg", "삭제되었거나 해서 여튼 없는글이다.");
		return "ExceptionPage";
	}

	@ExceptionHandler(Exception.class)
	public String e3(Model model, Exception e) {
		model.addAttribute("errorMsg", "핸들러가 정의되지 않은 에러.");
		e.printStackTrace();
		return "ExceptionPage";
	}

	@ExceptionHandler({ ConstraintViolationException.class, SQLIntegrityConstraintViolationException.class,
			DataIntegrityViolationException.class, EmptyResultDataAccessException.class})
	public String e4(Model model, Exception e) {
		model.addAttribute("errorMsg", "외래키 제약조건을 만족하지 못함. \nlikes나 comments같은 테이블에 없는 유저의 id가 insert되었을 가능성 있음");
		return "ExceptionPage";
	}
}
