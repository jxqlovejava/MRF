package com.nali.mrfcenter.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminController {
	
	@RequestMapping("/admin")
	public ModelAndView admin() {
		System.out.println("AdminController.admin()");
		
		String message = "hello, admin";
		ModelAndView mav = new ModelAndView("admin");
		mav.addObject("message", message);
		return mav;
	}

}
