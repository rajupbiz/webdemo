package com.blob.security;


import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.blob.model.candidate.Candidate;
import com.blob.model.common.GMessage;
import com.blob.model.common.User;
import com.blob.service.candidate.CandidateService;
import com.blob.service.common.CommonService;
import com.blob.util.DateUtils;

@Service
public class SessionService {
	
	@Resource
	private CommonService commonService;

	@Resource
	private CandidateService candidateService;
	
	public void setLoginSessionData(HttpSession session, User user) throws Exception {
		System.out.println(" set session data ");
		String lastLoggedIn = null;
		session.setAttribute("USER", user);
		if(DateUtils.toLocalDate(DateUtils.now()).equals(DateUtils.toLocalDate(user.getLastLoggedIn()))){
			lastLoggedIn = "today";
		}else{
			lastLoggedIn = DateUtils.getYearMonthDayBetweenDates(DateUtils.toLocalDate(user.getLastLoggedIn()), DateUtils.toLocalDate(DateUtils.now()))+" before";
		}
		session.setAttribute("LAST_LOGGED_IN", lastLoggedIn);
	}
	
	public void setMenuChangeCommonAttribtesInSession(HttpSession session, String tab, User user){
		Candidate candidate = null;
		Integer noOfUnreadMessages = 0, noOfShortlistedProfiles = 0;
		candidate = commonService.getCandidateByUser(user);
		if(candidate != null){
			List<GMessage> messages = candidateService.getCandidateMessages(candidate);
			if(CollectionUtils.isNotEmpty(messages)){
				noOfUnreadMessages = messages.size();
			}
			if(CollectionUtils.isNotEmpty(candidate.getShortlistedCandidates())){
				noOfShortlistedProfiles = candidate.getShortlistedCandidates().size();
			}
		}
		session.setAttribute("noOfUnreadMessages", noOfUnreadMessages);
		session.setAttribute("noOfShortlistedProfiles", noOfShortlistedProfiles);
		session.setAttribute("TAB", tab);
	}
}
