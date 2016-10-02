package com.blob.security;

import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.blob.model.Candidate;
import com.blob.model.User;
import com.blob.util.DateUtils;

@Service
public class SessionService {

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
		if(user != null && CollectionUtils.isNotEmpty(user.getCandidates())){
			candidate = user.getCandidates().get(0);
			// TODO: to get no of unread messages instead of all
			if(CollectionUtils.isNotEmpty(candidate.getCandidateMessages())){
				noOfUnreadMessages = candidate.getCandidateMessages().size();
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
