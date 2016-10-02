package com.blob.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.blob.dao.CandidateAstroDetailDao;
import com.blob.dao.CandidateDao;
import com.blob.dao.CandidateFamilyDao;
import com.blob.dao.CandidatePersonalDetailDao;
import com.blob.model.Candidate;
import com.blob.model.CandidateAddress;
import com.blob.model.CandidateAstroDetail;
import com.blob.model.CandidateContact;
import com.blob.model.CandidateFamily;
import com.blob.model.CandidatePersonalDetail;
import com.blob.model.User;
import com.blob.model.ui.ContactInfo;
import com.blob.model.ui.FamilyInfo;
import com.blob.model.ui.PersonalInfo;
import com.blob.security.SessionService;
import com.blob.service.CandidateService;
import com.blob.service.ProfileService;
import com.blob.service.UIService;


@Controller
public class ProfileController extends BaseController {

	@Resource
	private SessionService sessionService;
	
	@Resource
	private CandidateService candidateService;
	
	@Resource
	private UIService uiService;
	
	@Resource
	private CandidateDao candidateDao;
	
	@Resource
	private CandidatePersonalDetailDao candidatePersonalDetailDao;
	
	@Resource
	private CandidateAstroDetailDao candidateAstroDetailDao;
	
	@Resource
	private CandidateFamilyDao candidateFamilyDao;
	
	@Resource
	private ProfileService profileService;
	
	@RequestMapping(value="/vUpdateProfile", method=RequestMethod.GET)
	public ModelAndView vUpdateProfile(){

		Model m = new ExtendedModelMap();
		Boolean isNewCandidate = false;
		User user = getLoggedInUser();
		Candidate c = candidateService.getCandidateByUser(user);
		if(c == null){
			isNewCandidate = true;
			c = new Candidate();
			c.setCandidatePersonalDetail(new CandidatePersonalDetail());
		}
		m.addAttribute("candidate", c);
		m.addAttribute("personalInfo", uiService.getPersonalInfoSectionForUI(c));
		m.addAttribute("familyInfo", uiService.getFamilyInfoSectionForUI(c));
		m.addAttribute("contactInfo", uiService.getContactInfoSectionForUI(c));
		m.addAttribute("contactInfo", uiService.getContactInfoSectionForUI(c));
		m.addAttribute("isNewCandidate", isNewCandidate);
		sessionService.setMenuChangeCommonAttribtesInSession(request.getSession(), "update_profile", user);
		return new ModelAndView("/update-profile", m.asMap());
	}
	
	@RequestMapping(value="/ePersonalInfo", method=RequestMethod.GET)
	public ModelAndView ePersonalInfo(){

		Model m = new ExtendedModelMap();
		User user = getLoggedInUser();
		Candidate c = candidateService.getCandidateByUser(user);
		m.addAttribute("personalInfo", uiService.getPersonalInfoSectionForUI(c));
		return new ModelAndView("fragments/f-personal-info :: personalInfoEdit", m.asMap());
	}
	
	@RequestMapping(value="/vPersonalInfo", method=RequestMethod.GET)
	public ModelAndView vPersonalInfo(){

		Model m = new ExtendedModelMap();
		Boolean isNewCandidate = false;
		User user = getLoggedInUser();
		Candidate c = candidateService.getCandidateByUser(user);
		if(c == null){
			isNewCandidate = true;
			c = new Candidate();
			c.setCandidatePersonalDetail(new CandidatePersonalDetail());
		}
		//m.addAttribute("candidate", c);
		m.addAttribute("personalInfo", uiService.getPersonalInfoSectionForUI(c));
		m.addAttribute("isNewCandidate", isNewCandidate);
		return new ModelAndView("fragments/f-personal-info :: personalInfoView", m.asMap());
	}
	
	@RequestMapping(value="/sPersonalInfo", method=RequestMethod.POST)
	public ModelAndView sPersonalInfo(@ModelAttribute("personalInfo") PersonalInfo personalInfo, BindingResult result, Model model){

		CandidatePersonalDetail personalDet = uiService.getCandidatePersonalInfoFromUI(personalInfo);
		CandidateAstroDetail astroDet = uiService.getCandidateAstroDetailFromUI(personalInfo);
		Model m = new ExtendedModelMap();
		User user = getLoggedInUser();
		Candidate c = null;
		if(!candidateService.isCandidate(user)){
			// new candidate
			c = candidateService.registerAsCandidate(user);
		}else{
			c = candidateService.getCandidateByUser(user);
			personalDet.setId(c.getCandidatePersonalDetail().getId());
			astroDet.setId(c.getCandidateAstroDetail().getId());
		}
		astroDet.setCandidate(c);
		personalDet.setCandidate(c);
		personalDet = candidatePersonalDetailDao.save(personalDet);
		astroDet = candidateAstroDetailDao.save(astroDet);
		c.setCandidatePersonalDetail(personalDet);
		c.setCandidateAstroDetail(astroDet);
		m.addAttribute("personalInfo", uiService.getPersonalInfoSectionForUI(c));
		return new ModelAndView("fragments/f-personal-info :: personalInfoView", m.asMap());
	}
	
	@RequestMapping(value="/eFamilyInfo", method=RequestMethod.GET)
	public ModelAndView eFamilyInfo(){

		Model m = new ExtendedModelMap();
		User user = getLoggedInUser();
		Candidate c = candidateService.getCandidateByUser(user);
		m.addAttribute("familyInfo", uiService.getFamilyInfoSectionForUI(c));
		return new ModelAndView("fragments/f-family-info :: familyInfoEdit", m.asMap());
	}
	
	@RequestMapping(value="/vFamilyInfo", method=RequestMethod.GET)
	public ModelAndView vFamilyInfo(){

		Model m = new ExtendedModelMap();
		User user = getLoggedInUser();
		Candidate c = candidateService.getCandidateByUser(user);
		m.addAttribute("familyInfo", uiService.getFamilyInfoSectionForUI(c));
		return new ModelAndView("fragments/f-family-info :: familyInfoView", m.asMap());
	}
	
	@RequestMapping(value="/sFamilyInfo", method=RequestMethod.POST)
	public ModelAndView sFamilyInfo(@ModelAttribute("familyInfo") FamilyInfo familyInfo, BindingResult result, Model model){

		Model m = new ExtendedModelMap();
		User user = getLoggedInUser();
		Candidate c = candidateService.getCandidateByUser(user);
		CandidateFamily cf = uiService.getFamilyInfoFromUI(familyInfo);
		if(c.getCandidateFamily() != null){
			cf.setId(c.getCandidateFamily().getId());
		}
		cf.setCandidate(c);
		cf = candidateFamilyDao.save(cf);
		c.setCandidateFamily(cf);
		m.addAttribute("familyInfo", uiService.getFamilyInfoSectionForUI(c));
		return new ModelAndView("fragments/f-family-info :: familyInfoView", m.asMap());
	}
	
	@RequestMapping(value="/eContactInfo", method=RequestMethod.GET)
	public ModelAndView eContactInfo(){

		Model m = new ExtendedModelMap();
		User user = getLoggedInUser();
		Candidate c = candidateService.getCandidateByUser(user);
		m.addAttribute("contactInfo", uiService.getContactInfoSectionForUI(c));
		return new ModelAndView("fragments/f-contact-info :: contactInfoEdit", m.asMap());
	}
	
	@RequestMapping(value="/vContactInfo", method=RequestMethod.GET)
	public ModelAndView vContactInfo(){

		Model m = new ExtendedModelMap();
		User user = getLoggedInUser();
		Candidate c = candidateService.getCandidateByUser(user);
		m.addAttribute("contactInfo", uiService.getContactInfoSectionForUI(c));
		return new ModelAndView("fragments/f-contact-info :: contactInfoView", m.asMap());
	}
	
	@RequestMapping(value="/sContactInfo", method=RequestMethod.POST)
	public ModelAndView sContactInfo(@ModelAttribute("contactInfo") ContactInfo contactInfo, BindingResult result, Model model){

		Model m = new ExtendedModelMap();
		User user = getLoggedInUser();
		Candidate c = candidateService.getCandidateByUser(user);
		List<CandidateContact> contacts = uiService.getContactsInfoFromUI(contactInfo);
		List<CandidateAddress> addresses = uiService.getAddressesInfoFromUI(contactInfo);
		CandidatePersonalDetail pd = c.getCandidatePersonalDetail();
		pd.setNativePlace(contactInfo.getNativePlace());
		pd = candidatePersonalDetailDao.save(pd);
		contacts = profileService.saveCandidateContacts(contacts, c);
		addresses = profileService.saveCandidateAddress(addresses, c);
		if(contacts != null && !contacts.isEmpty()){
			c.setCandidateContacts(contacts);
		}
		if(addresses != null && !addresses.isEmpty()){
			c.setCandidateAddresses(addresses);
		}
		c.setCandidatePersonalDetail(pd);
		m.addAttribute("contactInfo", uiService.getContactInfoSectionForUI(c));
		return new ModelAndView("fragments/f-contact-info :: contactInfoView", m.asMap());
	}
	
	
	@RequestMapping(value="/vPreviewProfile", method=RequestMethod.GET)
	public ModelAndView vPreviewProfile(){

		User user = getLoggedInUser();
		sessionService.setMenuChangeCommonAttribtesInSession(request.getSession(), "preview_profile", user);
		Model m = new ExtendedModelMap();
		return new ModelAndView("/preview-profile", m.asMap());
	}
	
}
