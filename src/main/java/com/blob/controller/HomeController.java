package com.blob.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.blob.dao.UserDao;
import com.blob.model.Candidate;
import com.blob.model.User;
import com.blob.security.SigninSignoutService;
import com.blob.service.CandidateService;
import com.blob.service.UIService;
import com.blob.security.SessionService;

@Controller
public class HomeController extends BaseController {

	@Resource
	private SigninSignoutService loginLogoutService;

	@Resource
	private SessionService sessionService;
	
	@Resource
	private UserDao userDao;
	
	@Resource
	private UIService uiService;
	
	@Resource
	private CandidateService candidateService;
	
	@RequestMapping("/vHome")
	public ModelAndView home(){
		Model m = new ExtendedModelMap();
		
		System.out.println(" \n\n\n home ............. ");
		User user = getLoggedInUser();
		Candidate c = candidateService.getCandidateByUser(user);
		m.addAttribute("dashboard", uiService.getDashboardInfoForUI(c));
		sessionService.setMenuChangeCommonAttribtesInSession(request.getSession(), "home", user);
		return new ModelAndView("/home", m.asMap());
	}
}
