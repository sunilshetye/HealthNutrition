package com.health.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.TreeSet;
import com.health.service.FilesofBrouchureService;

import javax.servlet.http.HttpServletRequest;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
import com.google.common.collect.Lists;
import com.health.config.Auth;
import com.health.domain.security.Role;
import com.health.domain.security.UserRole;
import com.health.model.Brouchure;
import com.health.model.Carousel;
import com.health.model.Category;
import com.health.model.Comment;
import com.health.model.Consultant;
import com.health.model.FilesofBrouchure;
import com.health.model.ContributorAssignedMultiUserTutorial;
import com.health.model.ContributorAssignedTutorial;
import com.health.model.Event;
import com.health.model.FeedbackMasterTrainer;
import com.health.model.IndianLanguage;
import com.health.model.Language;
import com.health.model.LogManegement;
import com.health.model.OrganizationRole;
import com.health.model.PathofPromoVideo;
import com.health.model.PostQuestionaire;
import com.health.model.PromoVideo;
import com.health.model.Question;
import com.health.model.ResearchPaper;
import com.health.model.State;
import com.health.model.Testimonial;
import com.health.model.Topic;
import com.health.model.TopicCategoryMapping;
import com.health.model.TraineeInformation;
import com.health.model.TrainingInformation;
import com.health.model.TrainingTopic;
import com.health.model.Tutorial;
import com.health.model.User;
import com.health.model.UserIndianLanguageMapping;
import com.health.model.Version;
import com.health.repository.BrouchureRepository;
import com.health.repository.TopicCategoryMappingRepository;
import com.health.repository.TutorialRepository;
import com.health.service.BrouchureService;
import com.health.service.CarouselService;
import com.health.service.CategoryService;
import com.health.service.CityService;
import com.health.service.CommentService;
import com.health.service.ConsultantService;
import com.health.service.ContributorAssignedMultiUserTutorialService;
import com.health.service.ContributorAssignedTutorialService;
import com.health.service.DistrictService;
import com.health.service.EventService;
import com.health.service.FeedBackMasterTrainerService;
import com.health.service.IndianLanguageService;
import com.health.service.LanguageService;
import com.health.service.LogMangementService;
import com.health.service.OrganizationRoleService;
import com.health.service.PathofPromoVideoService;
import com.health.service.PostQuestionaireService;
import com.health.service.PromoVideoService;
import com.health.service.QuestionService;
import com.health.service.ResearchPaperService;
import com.health.service.RoleService;
import com.health.service.StateService;
import com.health.service.TestimonialService;
import com.health.service.TopicCategoryMappingService;
import com.health.service.TopicService;
import com.health.service.TraineeInformationService;
import com.health.service.TrainingInformationService;
import com.health.service.TrainingTopicService;
import com.health.service.TutorialService;
import com.health.service.UserIndianLanguageMappingService;
import com.health.service.UserRoleService;
import com.health.service.UserService;
import com.health.service.VersionService;
import com.health.utility.CommonData;
import com.health.repository.*;
import com.health.utility.MailConstructor;
import com.health.utility.SecurityUtility;
import com.health.utility.ServiceUtility;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Controller Class takes website request and process it accordingly
 * @author om prakash soni
 * @version 1.0
 *
 */
@Controller
public class HomeController {
	
	private static final Logger logger= LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	private VersionRepository verRepository;
	
	@Autowired
	private TopicCategoryMappingRepository tcmRepository;

	@Autowired
	private VersionService verService;
	
	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private MailConstructor mailConstructor;

	@Autowired
	private UserService userService;  

	@Autowired
	private LanguageService lanService;

	@Autowired
	private CategoryService catService;
	
	@Autowired
	private ResearchPaperService researchPaperService;


	@Autowired
	private RoleService roleService;

	@Autowired
	private TopicService topicService;

	@Autowired
	private TopicCategoryMappingService topicCatService;

	@Autowired
	private QuestionService questService;

	@Autowired
	private EventService eventservice;

	@Autowired
	private TestimonialService testService;
	
	@Autowired
	private FilesofBrouchureService filesofbrouchureService;

	@Autowired
	private ConsultantService consultService;

	@Autowired
	private Environment env;

	@Autowired
	private UserRoleService usrRoleService;
	
	@Autowired
	private UserRoleRepositary userRoleRepo;

	@Autowired
	private ContributorAssignedTutorialService conRepo;
	
	@Autowired
	private ContributorAssignedMultiUserTutorialService conMultiUser;

	@Autowired
	private TutorialService tutService;

	@Autowired
	private StateService stateService;

	@Autowired
	private TrainingInformationService trainingInfoService;

	@Autowired
	private DistrictService districtService;

	@Autowired
	private CityService cityService;

	@Autowired
	private TraineeInformationService traineeService;

	@Autowired
	private TrainingTopicService trainingTopicServ;

	@Autowired
	private FeedBackMasterTrainerService feedServ;

	@Autowired
	private CommentService comService;

	@Autowired
	private PostQuestionaireService postQuestionService;

	@Autowired
	private BrouchureService broService;
	
	@Autowired
	private PromoVideoService promoVideoService;
	
	@Autowired
	private PathofPromoVideoService pathofPromoVideoService;

	@Autowired
	private IndianLanguageService iLanService;

	@Autowired
	private UserIndianLanguageMappingService userIndianMappingService;

	@Autowired
	private CarouselService caroService;

	@Autowired
	private OrganizationRoleService organizationRoleService;
	
	@Autowired
	private CacheManager cacheManager;
	
	@Autowired
	private LogMangementService logMangementService;
	
	@Value("${scriptmanager_url}")
	private String scriptmanager_url;
	
	@Value("${scriptmanager_path}")
	private String scriptmanager_path;

    private static YouTube youtube;
    
	private static final String VIDEO_FILE_FORMAT = "video/*";

	private User getUser(Principal principal,UserService usrservice) {
		User usr=new User();
		if(principal!=null) {
			usr=usrservice.findByUsername(principal.getName());
		}
		return usr;
	}
	

	static void setCompStatus(Model model,List<Tutorial> tutorials) {
		
		if(tutorials.isEmpty()) {

			model.addAttribute("statusOutline", CommonData.ADD_CONTENT);
			model.addAttribute("statusScript", CommonData.ADD_CONTENT);
			model.addAttribute("statusSlide", CommonData.ADD_CONTENT);
			model.addAttribute("statusVideo", CommonData.ADD_CONTENT);
			model.addAttribute("statusKeyword", CommonData.ADD_CONTENT);
			model.addAttribute("statusPreReq", CommonData.ADD_CONTENT);
			model.addAttribute("statusGraphics", CommonData.ADD_CONTENT);
			model.addAttribute("tutorial", null);
		}else {
			for(Tutorial local:tutorials) {
				model.addAttribute("statusOutline", CommonData.tutorialStatus[local.getOutlineStatus()]);
				model.addAttribute("statusScript", CommonData.tutorialStatus[local.getScriptStatus()]);
				model.addAttribute("statusSlide", CommonData.tutorialStatus[local.getSlideStatus()]);
				model.addAttribute("statusVideo", CommonData.tutorialStatus[local.getVideoStatus()]);
				model.addAttribute("statusKeyword", CommonData.tutorialStatus[local.getKeywordStatus()]);
				model.addAttribute("statusPreReq", CommonData.tutorialStatus[local.getPreRequisticStatus()]);

//				model.addAttribute("tutorial", local);
				
				
				
			}

		}
	}
private void setVideoInfo(Model model, List<Tutorial> tutorials) {
	
	HashMap<String, Long> video_data = new HashMap<String, Long>();	
	if(!tutorials.isEmpty()) {
		for(Tutorial local:tutorials) {
			if(local.getVideo() != null) {

				IContainer container = IContainer.make();
				int result=10;
				result = container.open(env.getProperty("spring.applicationexternalPath.name")+local.getVideo(),IContainer.Type.READ,null);
				
					IStream stream = container.getStream(0);
					if(stream!=null) {
					IStreamCoder coder = stream.getStreamCoder();
					model.addAttribute("FileWidth", coder.getWidth());
					model.addAttribute("FileHeight", coder.getHeight());
					model.addAttribute("fileSizeInMB", container.getFileSize()/1000000);
					model.addAttribute("FileDurationInSecond", container.getDuration()/1000000);
					
					container.close();
				}
			}
		}
	}
	
}
	
static void setCompComment(Model model,List<Tutorial> tutorials,CommentService comService) {
	
	if(!tutorials.isEmpty()) {
		for(Tutorial local:tutorials) {
			List<Comment> comVideo = comService.getCommentBasedOnTutorialType(CommonData.VIDEO, local);
			List<Comment> comScript = comService.getCommentBasedOnTutorialType(CommonData.SCRIPT, local);
			List<Comment> comSlide = comService.getCommentBasedOnTutorialType(CommonData.SLIDE, local);

			List<Comment> comKeyword = comService.getCommentBasedOnTutorialType(CommonData.KEYWORD, local);
			List<Comment> comPreRequistic = comService.getCommentBasedOnTutorialType(CommonData.PRE_REQUISTIC, local);
			List<Comment> comOutline = comService.getCommentBasedOnTutorialType(CommonData.OUTLINE, local);

			model.addAttribute("comOutline", comOutline);
			model.addAttribute("comScript",comScript );
			model.addAttribute("comSlide",comSlide );
			model.addAttribute("comVideo", comVideo);
			model.addAttribute("comKeyword", comKeyword);
			model.addAttribute("comPreReq", comPreRequistic);
			
		}
	}
}

//1***********
static void setEngLangStatus(Model model,Language lan) {
	if(!lan.getLangName().equalsIgnoreCase("english")) {
		model.addAttribute("isEnglish", false);
	}else {
		model.addAttribute("isEnglish", true);
	}
}

private String setPreReqInfo(Tutorial tut) {
	String prefix = "Selected prerequisite : ";
	String pre_req = "";
	if(tut.getPreRequistic()!=null) {
		Tutorial local = tut.getPreRequistic();
		String catName = local.getConAssignedTutorial().getTopicCatId().getCat().getCatName();
		String topicName = local.getConAssignedTutorial().getTopicCatId().getTopic().getTopicName();
		pre_req = prefix + catName + " - " + topicName;
	}else {
		pre_req = prefix + "This tutorial has no prerequisite.";
	}
	return pre_req;
}

static String setScriptManagerUrl(Model model,String scriptmanager_url,String scriptmanager_path, Tutorial tutorial, Topic topic,Language lan, Category cat) {
	String topic_name = topic.getTopicName();
	topic_name	= topic_name.replaceAll(" ", "-");
	model.addAttribute("topic_name", topic_name);
	model.addAttribute("script_manager_view_url",scriptmanager_url+scriptmanager_path);
	model.addAttribute("sm_default_param", CommonData.SM_DEFAULT_PARAM);
	String tutorial_id="";
	if(tutorial!=null) {
		tutorial_id = Integer.toString(tutorial.getTutorialId());
	}
	String sm_url = scriptmanager_url+scriptmanager_path+Integer.toString(cat.getCategoryId())+"/"
			+tutorial_id+"/"+Integer.toString(lan.getLanId())+"/"+topic_name+"/"+"1";
	return sm_url;
}

private void getUsers() {
	
}


private List<Category> getCategories() {
	List<Tutorial> tutorials = tutService.findAllByStatus(true);
	Set<Category> catTemp = new HashSet<Category>();
	for(Tutorial temp :tutorials) {
		
		Category c = temp.getConAssignedTutorial().getTopicCatId().getCat();
		if(c.isStatus()) {
			catTemp.add(c);
		}
		
	}
	
	List<Category> catTempSorted =new ArrayList<Category>(catTemp);
	Collections.sort(catTempSorted);
	
	return catTempSorted;
}


private List<Topic> getTopics() {
	List<Tutorial> tutorials = tutService.findAllByStatus(true);
	Set<Topic> topicTemp = new HashSet<Topic>();
	for(Tutorial temp :tutorials) {
		
		Category c = temp.getConAssignedTutorial().getTopicCatId().getCat();
		if(c.isStatus()) {
			topicTemp.add(temp.getConAssignedTutorial().getTopicCatId().getTopic());
		}
	}
	List<Topic> topicTempSorted =new ArrayList<Topic>(topicTemp);
	Collections.sort(topicTempSorted);
	return topicTempSorted;
}

private List<Language> getLanguages() {
	List<Tutorial> tutorials = tutService.findAllByStatus(true);
	
	Set<Language> langTemp = new HashSet<Language>();
	for(Tutorial temp :tutorials) {
		Category c = temp.getConAssignedTutorial().getTopicCatId().getCat();
		if(c.isStatus()) {
			langTemp.add(temp.getConAssignedTutorial().getLan());
		}
		
	}
	List<Language> lanTempSorted =new ArrayList<Language>(langTemp);
	Collections.sort(lanTempSorted);
	
	return lanTempSorted;
}
	/**
	 * Index page Url
	 * @param model Model Object
	 * @return String object (Webpapge)
	 */

private void getModelData(Model model) {
	List<Category> catTempSorted = catService.getCategoriesForCache();
	List<Language> lanTempSorted = lanService.getLanguagesForCache();
	List<Topic> topicTemp = topicService.getTopicsForcache(); 
	
	model.addAttribute("categories", catTempSorted);
	model.addAttribute("languages", lanTempSorted);
	model.addAttribute("topics", topicTemp);
	model.addAttribute("languageCount",lanTempSorted.size());
}
	

	@RequestMapping("/")
	public String index(Model model) {

		List<Event> events=eventservice.findAllEventForCache(); //findAllEvent(); 
		List<Testimonial> testi= testService.findAllTestimonialByapprovedForCache();  //findAllTestimonialByapproved();
		List<Consultant> consults=consultService.findAllConsultHomeTrueForCache(); //findAllConsultHomeTrue();	
		List<Brouchure> brochures= broService.findAllBrouchuresForCache(); //findAllBrouchures(); 
		List<Carousel> carousel= caroService.findCarouselForCache(); //findCarousel();	
		//List<Category> category_objs = catService.findAllCategoryByOrderForCache(); // findAllCategoryByOrder();
		List<PromoVideo> promoVideos= promoVideoService.findAllByShowOnHomePage();
		List<ResearchPaper> researchPapers= researchPaperService.findAllByShowOnHomePage();
		
		List<Event> evnHome = new ArrayList<>();
		List<Testimonial> testHome = new ArrayList<>();
		List<Consultant> consulHome = new ArrayList<>();
		List<Category> categoryHome = new ArrayList<>();
		//List<Brouchure> brochureHome = new ArrayList<>();
		List<Version> versionHome= new ArrayList<>();
		List<Carousel> carouselHome = new ArrayList<>();
		List<PromoVideo> promoVideoHome= new ArrayList<>();
		List<ResearchPaper> researchPapersHome=new ArrayList<>();
		
		List<Category> catTempSorted = catService.getCategoriesForCache(); // getCategories(); 
		

		
		List<Version> allVersions=verService.findAll();
		List<Version> versions= new ArrayList<Version>();
		for(Brouchure bro: brochures) {
			for(Version ver: allVersions) {
				if(bro.getId()==ver.getBrouchure().getId() && bro.getPrimaryVersion()==ver.getBroVersion())
					versions.add(ver);
			}
		}
		Collections.sort(versions, Version.SortByBroVersionTime);
		//model.addAttribute("brouchures", brochures);
		//model.addAttribute("versions", versions);
	
		

		getModelData(model);



		int upperlimit = 0;

		for(Event local : events) {
			evnHome.add(local);
			if(++upperlimit > 3) {
				break;
			}
		}

		upperlimit = 0;

		for(Testimonial local : testi) {
			testHome.add(local);
			if(++upperlimit > 3) {
				break;
			}
		}

		upperlimit = 0;

		for(Consultant local : consults) {
			if(local.isOnHome()) {
				consulHome.add(local);
			}
			if(++upperlimit > 4) {
				break;
			}
		}
//		set upper limit for categories count
		upperlimit = 4 ;
		//categoryHome=(categories.size()>upperlimit) ? categories.subList(0, upperlimit):categories;
		categoryHome=(catTempSorted.size()>upperlimit) ? catTempSorted.subList(0, upperlimit):catTempSorted;
		//brochureHome=(brochures.size()>upperlimit) ? brochures.subList(0, upperlimit):brochures;
		versionHome=(versions.size()>upperlimit) ? versions.subList(0, upperlimit):versions;
		carouselHome=(carousel.size()>upperlimit) ? carousel.subList(0, upperlimit):carousel;
		promoVideoHome=(promoVideos.size()>1) ? promoVideos.subList(0, 1):promoVideos;
		researchPapersHome=(researchPapers.size()>upperlimit) ? researchPapers.subList(0, upperlimit):researchPapers;

		if(!consulHome.isEmpty()) {
			model.addAttribute("listOfConsultant", consulHome);
		}
		
		if(!researchPapersHome.isEmpty()) {
			model.addAttribute("listOfResearchPapers", researchPapersHome);
		}


		if(!testHome.isEmpty()) {
			model.addAttribute("listofTestimonial", testHome);
		}

		if(!categoryHome.isEmpty()) {
			model.addAttribute("listofCategories", categoryHome);
		}

		if(!evnHome.isEmpty()) {
			Collections.sort(evnHome, Event.SortByEventAddedTimeInDesc);
			model.addAttribute("events", evnHome);
		}
		
		if(!promoVideoHome.isEmpty()) {
			model.addAttribute("listofPromoVideos", promoVideoHome);
			model.addAttribute("PromoVideoLanguages", promoVideoHome.get(0).findAlllanguages());
			model.addAttribute("PromoVideos", promoVideoHome.get(0).getVideoFiles());
			System.out.println("PromoVideo Test" + promoVideoHome);
		}
		

		if(!versionHome.isEmpty()) {
			Collections.sort(versionHome, Version.SortByBroVersionTime);
			model.addAttribute("listofVesrsions", versionHome);
		}
		
		
		List<Tutorial> finalTutorials = tutService.getFinalTutorialsForCache(); //getFinalTutorials();
		


		
		
		model.addAttribute("videoCount", finalTutorials.size());
		model.addAttribute("consultantCount", consults.size());


		
		

		if(!carouselHome.isEmpty()) {
			model.addAttribute("carousel", carouselHome.get(0));
			model.addAttribute("carouselList", carouselHome.subList(1, carouselHome.size()));
			//model.addAttribute("carouselList", carouselHome.size());
		}
		
		

		return "index";
	}
	


	/*
	 * A controller to clear all caches
	 * Author: Alok Kumar
	 */
	
	@RequestMapping(value = "/clearAllCaches", method = RequestMethod.GET)
	public String ClearAllCache(Principal principal,Model model ) {
		
		for(String name:cacheManager.getCacheNames()){
			cacheManager.getCache(name).clear(); // clear cache by name
			}
		
		User usr=new User();
		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
		model.addAttribute("success_msg",CommonData.All_Caches_Clear_MSG); 
		return "ClearAllCaches";
	}
	



	/**
	 * Redirects to Tutorials Page
	 * @param req HttpServletRequest object
	 * @param cat String object
	 * @param topic String object
	 * @param lan String object
	 * @param principal principal object
	 * @param model model object
	 * @param page int value
	 * @return String object (Webpapge)
	 */
	@RequestMapping(value = "/tutorials", method = RequestMethod.GET)
	public String viewCoursesAvailable(HttpServletRequest req,
			@RequestParam(name = "categoryName") int cat,
			@RequestParam(name = "topic") int topic,
			@RequestParam(name = "lan") int lan,
			
			@RequestParam(name ="page",defaultValue = "0") int page , Principal principal,Model model) {

		getModelData(model);
		
		model.addAttribute("category", cat);
		model.addAttribute("language", lan);
		model.addAttribute("topic", topic);

		Category localCat = null;
		Language localLan = null;
		Topic localTopic = null;
		TopicCategoryMapping localTopicCat = null;
		List<TopicCategoryMapping> localTopicCatList = null;
		List<ContributorAssignedTutorial> conAssigTutorialList =null;
		ContributorAssignedTutorial conAssigTutorial = null;

		Page<Tutorial> tut = null;
		List<Tutorial> tutToView = new ArrayList<Tutorial>();
		List<Tutorial> tutToView1 = new ArrayList<Tutorial>();

		User usr=new User();
		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
		if(cat!=0) {
			localCat = catService.findByid(cat);
		}
			if(topic!=0) {
			localTopic = topicService.findById(topic);
		}		
			if(lan!=0) {
			localLan= lanService.getById(lan);
		}

		if(localCat != null && localTopic != null) {
			localTopicCat = topicCatService.findAllByCategoryAndTopic(localCat, localTopic);
		}else if (localCat != null) {
			localTopicCatList = topicCatService.findAllByCategory(localCat);
		}else if (localTopic != null) {
			localTopicCatList = topicCatService.findAllByTopicwithCategoryTrue(localTopic);
		}

		if(localTopicCat != null) {

			if(localLan != null) {
				conAssigTutorial = conRepo.findByTopicCatAndLanViewPart(localTopicCat, localLan);
			}else {
				conAssigTutorialList = conRepo.findByTopicCat(localTopicCat);
			}
		}else if(localTopicCatList != null) {

			if(localLan != null) {
				conAssigTutorialList = conRepo.findAllByTopicCatAndLanViewPartwithCategoryTrue(localTopicCatList, localLan);
			}else {
				conAssigTutorialList = conRepo.findAllByTopicCat(localTopicCatList);
			}
		}else {
			if(localLan != null) {
				conAssigTutorialList = conRepo.findAllByLanWithcategoryTrue(localLan);
			}
		}

		Pageable pageable = PageRequest.of(page, 10);

		if(conAssigTutorial != null) {
			tut = tutService.findAllByconAssignedTutorialPagination(conAssigTutorial,pageable);
			
		} else if(conAssigTutorialList != null) {
			tut =tutService.findAllByconAssignedTutorialListPagination(conAssigTutorialList, pageable);
			
		}else {
			tut = tutService.findAllPaginationWithEnabledCategoryandTrueTutorial(pageable);
	
		}

		for(Tutorial temp :tut) {
			if(temp.isStatus()) {
				tutToView.add(temp);
			}
		}

		for(Tutorial temp :tutToView) {
			Category c = temp.getConAssignedTutorial().getTopicCatId().getCat();
			if(c.isStatus()) {
				tutToView1.add(temp);
			}
		}
		
		if(localCat==null) {
			Collections.sort(tutToView1, Tutorial.UserVisitComp);
		}
		else {
			Collections.sort(tutToView1, Tutorial.SortByOrderValue);
		}
		
		if(localCat==null) {
			System.out.println("********Checking Sort by userVisit in Tutorial*********");
			for(Tutorial tv: tutToView1) {
				System.out.println(tv.getUserVisit() + " " + tv.getConAssignedTutorial().getTopicCatId().getTopic().getTopicName());
			}
		}else {
			System.out.println("********Checking Sort by orderValue in Tutorial*********");
			for(Tutorial tv: tutToView1) {
				System.out.println(tv.getConAssignedTutorial().getTopicCatId().getOrder() + " " + tv.getConAssignedTutorial().getTopicCatId().getTopic().getTopicName());
			}
		}
		
		 // sorting based on order value
		
		int totalPages = tut.getTotalPages();
		int firstPage = page + 1 > 2 ? page + 1 - 2 : 1;
		int lastPage= page + 1 < totalPages - 5 ? page + 1 + 5 : totalPages;

		model.addAttribute("tutorials", tutToView1);
		model.addAttribute("currentPage",page);
		model.addAttribute("firstPage", firstPage);
		model.addAttribute("lastPage", lastPage);
		model.addAttribute("totalPages",totalPages);

		return "tutorialList";   // add view name (filename)
	}
	
	
	/*
	 * Function to Search Tutorial By Outline Query
	 * Author: Alok Kumar
	 */
	
	@RequestMapping(value = "/tutorialsSearch", method = RequestMethod.GET)
	public String viewCoursesAvailableBySearch(HttpServletRequest req,
			@RequestParam(name = "query") String query,
			@RequestParam(name ="page",defaultValue = "0") int page , Principal principal,Model model) {
		
		getModelData(model);
		

		Page<Tutorial> tut = null;
		List<Tutorial> tutToView = new ArrayList<Tutorial>();
		List<Tutorial> tutToView1= new ArrayList<Tutorial>();
		

		User usr=new User();
		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
		
        
		Pageable pageable = PageRequest.of(page, 20);

		if(query != null) {
			tut = tutService.SearchOutlineByCombinationOfWords(query, pageable);
			
			if(tut.isEmpty()) {
				return "redirect:/";
			}
			
		}else {
			tut = tutService.findAllPagination(pageable);
	
		}

		for(Tutorial temp :tut) {
			if(temp.isStatus()) {
				tutToView.add(temp);
			}
		}
		
		for(Tutorial temp :tutToView) {
			
			Category c = temp.getConAssignedTutorial().getTopicCatId().getCat();
			if(c.isStatus()) {
				tutToView1.add(temp);
			}
			System.out.println(temp.getTutorialId() +"***********");
		}
		
		//Collections.sort(tutToView1); 
		Collections.sort(tutToView1, Tutorial.UserVisitComp);
		System.out.println("********Checking Sort by userVisit in Tutorial*********");
		for(Tutorial tv: tutToView1) {
			System.out.println(tv.getUserVisit() + " " + tv.getConAssignedTutorial().getTopicCatId().getTopic().getTopicName());
		}
		
		model.addAttribute("tutorials", tutToView1);
		model.addAttribute("currentPage",page);
		model.addAttribute("totalPages",tut.getTotalPages());
		model.addAttribute("query", query);
		

		//return "tutorialList";  
		return "TutorialListforOutlineSearching";   // add view name (filename)
	}


	
	

	

	/**
	 * redirects to tutorial specific page
	 * @param req HttpServletRequest object
	 * @param id integer value
	 * @param principal Principal object
	 * @param model Model object
	 * @return String object (Webpapge)
	 */
	@RequestMapping(value = "/tutorialView/{catName}/{topicName}/{language}", method = RequestMethod.GET)
	public String viewTutorial(HttpServletRequest req,@PathVariable(name = "catName") String cat,
			@PathVariable (name = "topicName") String topic,
			@PathVariable (name = "language") String lan,Principal principal,Model model) {
		
		Category catName = catService.findBycategoryname(cat);
		if(!catName.isStatus()) {
			catName=null;
		}
		
		Topic topicName = topicService.findBytopicName(topic);
		Language lanName = lanService.getByLanName(lan);
		TopicCategoryMapping topicCatMap = topicCatService.findAllByCategoryAndTopic(catName, topicName);
		ContributorAssignedTutorial conTut = conRepo.findByTopicCatAndLanViewPart(topicCatMap, lanName);
		
		if(catName == null || topicName == null || lanName == null || topicCatMap == null || conTut == null) {
			System.out.println("Problem1");
			return "redirect:/";
		}
		
		
		
		
			 Tutorial tutorial = tutService.findAllByContributorAssignedTutorialEnabled(conTut).get(0);
			 List<Tutorial> relatedTutorial = new ArrayList<>();
			 
			 if(tutorial == null || tutorial.isStatus() == false) {
				 System.out.println("Problem2");
				 return "redirect:/";
			 }
			 
			 tutorial.setUserVisit(tutorial.getUserVisit()+1);
			 tutService.save(tutorial);
			 
			 model.addAttribute("tutorial", tutorial);
			 
			 
			 if(!tutorial.getConAssignedTutorial().getLan().getLangName().equalsIgnoreCase("english")){
				 model.addAttribute("relatedContent", tutorial.getRelatedVideo());
			 }

			 Category category = catService.findByid(tutorial.getConAssignedTutorial().getTopicCatId().getCat().getCategoryId());
			 List<TopicCategoryMapping> topicCatMapping = topicCatService.findAllByCategory(category);
			 List<ContributorAssignedTutorial> contriAssignedTut = conRepo.findAllByTopicCat(topicCatMapping);
			 List<Tutorial> tutorials = tutService.findAllByContributorAssignedTutorialList1(contriAssignedTut);
			 
			 for(Tutorial x: tutorials) {
				 if(x==tutorial) {
					 continue;
				 }
				 Category cat1 = x.getConAssignedTutorial().getTopicCatId().getCat();
					
						 if(x.getConAssignedTutorial().getLan().getLangName().equalsIgnoreCase(tutorial.getConAssignedTutorial().getLan().getLangName())) {
							 relatedTutorial.add(x);
						 }
					
				 
				
			 }
			
			 Collections.sort(relatedTutorial, Tutorial.UserVisitComp);
			 //Collections.sort(relatedTutorial);
			 
			 model.addAttribute("tutorials", relatedTutorial);

				Set<String> catTemp = new HashSet<String>();
				Set<String> topicTemp = new HashSet<String>();
				Set<String> lanTemp = new HashSet<String>();

				List<Tutorial> tutorialse = tutService.findAllByStatus(true);
				for(Tutorial temp :tutorialse) {
					ContributorAssignedTutorial conAssignedTutorial = temp.getConAssignedTutorial();
					catTemp.add(conAssignedTutorial.getTopicCatId().getCat().getCatName());
					lanTemp.add(conAssignedTutorial.getLan().getLangName());
					topicTemp.add(conAssignedTutorial.getTopicCatId().getTopic().getTopicName());
				}
				
//				List<String> catTempSorted =new ArrayList<String>(catTemp);
//				Collections.sort(catTempSorted);
//				
//				List<String> lanTempSorted =new ArrayList<String>(lanTemp);
//				Collections.sort(lanTempSorted);

				getModelData(model);
//				model.addAttribute("topics", topicTemp);
				//String sm_url = scriptmanager_url + scriptmanager_path + String.valueOf(category.getCategoryId())+"/"+String.valueOf(tutorial.getTutorialId())+"/"+String.valueOf(lanName.getLanId())+"/"+String.valueOf(tutorial.getTopicName())+"/1";
				String sm_url = scriptmanager_url + scriptmanager_path + String.valueOf(category.getCategoryId())+"/"+String.valueOf(tutorial.getTutorialId())+"/"+String.valueOf(lanName.getLanId())+"/"+String.valueOf(tutorial.getConAssignedTutorial().getTopicCatId().getTopic().getTopicName())+"/1";
				model.addAttribute("sm_url", sm_url);
			return "tutorial";
	}
	
	/**
	 * Redirects to Login Page
	 * @param model Model object
	 * @return String object (Webpapge)
	 */
	@RequestMapping("/login")									// in use
	public String loginGet(Model model) {
		model.addAttribute("classActiveLogin", true);
		return "signup";
	}

	/**
	 * Redirects to ShowEvent Page
	 * @param model Model object
	 * @return String object (Webpapge)
	 */
	@RequestMapping(value = "/showEvent",method = RequestMethod.GET)
	public String showEventGet(Model model) {
	
		List<Event> events=eventservice.findAll();
		
		
		model.addAttribute("Events", events);
		return "events";
	}

	/**
	 * Redirects to Consultant Page
	 * @param model Model object
	 * @return String object (Webpapge)
	 */
	@RequestMapping(value = "/showConsultant",method = RequestMethod.GET)
	public String showConsultantGet(Model model) {

		List<Consultant> consults = consultService.findAll();
		model.addAttribute("listConsultant", consults);
		
		HashMap<Integer, String> map = new HashMap<>();
		
		User user = userService.findByEmail("bellatonyp@gmail.com");
		Set<UserRole> roles = user.getUserRoles();
		Set<Category> categorys = user.getCategories();
		for(Consultant c:consults) {
			String s="";
			if(c.isOnHome()) {
				Set<UserRole> userRoles = c.getUser().getUserRoles();
				for(UserRole ur:userRoles) {
					if(ur.getRole().getName().equals(CommonData.domainReviewerRole)) {
						s= s+ ur.getCategory().getCatName()+" , ";
						
					}
					
				}
				if(s.length()==0) {
					continue;
				}
				
				map.put(c.getConsultantId(),s.substring(0, s.length()-2));
			}
			
			
			
			
		}
		//map.put(100,"teststring");
		

		model.addAttribute("map", map);
		return "Consultants";
	}
	@RequestMapping(value = "/showLanguages",method = RequestMethod.GET)
	public String showLanguagesGet(Model model) {

		HashMap<String, Integer> map = new HashMap<>();
		//List<Language> langs = lanService.getAllLanguages();
		List<Language> langs = getLanguages();
		for(Language lang:langs) {
			List<ContributorAssignedTutorial> con = conRepo.findAllByLan(lang);
			List<Tutorial> tutorials=tutService.findAllByContributorAssignedTutorialList1(con);
			List<Tutorial> finalTutorials=new ArrayList<>();
			
			for(Tutorial temp :tutorials) {
				
				Category c = temp.getConAssignedTutorial().getTopicCatId().getCat();
				if(c.isStatus()) {
					finalTutorials.add(temp);
				}
			}
			
			map.put(lang.getLangName(), finalTutorials.size());
		}
		model.addAttribute("map", map);
		
//		List<Consultant> consults = consultService.findAll();
//		model.addAttribute("listConsultant", consults);
//		
//		HashMap<Integer, String> map = new HashMap<>();
//		User user = userService.findByEmail("bellatonyp@gmail.com");
//		Set<UserRole> roles = user.getUserRoles();
//		Set<Category> categorys = user.getCategories();
//		for(Consultant c:consults) {
//			String s="";
//			Set<UserRole> userRoles = c.getUser().getUserRoles();
//			for(UserRole ur:userRoles) {
//				if(ur.getRole().getName().equals(CommonData.domainReviewerRole)) {
//					s= s+ ur.getCategory().getCatName()+" , ";
//				}
//			}
//			map.put(c.getConsultantId(),s.substring(0, s.length()-2));
//			
//		}
//		map.put(100,"teststring");
//
//		model.addAttribute("map", map);
		return "languages";
	}
	
	/**
	 * Redirects to Testimonail Page
	 * @param model Model object
	 * @return String object (Webpapge)
	 */
	@RequestMapping(value = "/showTestimonial",method = RequestMethod.GET)
	public String showTestimonialGet(Model model) {

		List<Testimonial> testi = testService.findByApproved(true);
		model.addAttribute("Testimonials", testi);
		return "signup";
	}

	/**
	 * redirects to Forget Password Post method
	 * @param request HttpServletRequest object
	 * @param email String object
	 * @param model model object
	 * @return String object (Webpapge)
	 */
	@RequestMapping(value = "/forgetPassword",method = RequestMethod.POST)
	public String forgetPasswordPost(HttpServletRequest request, @ModelAttribute("email") String email, Model model) {

		model.addAttribute("classActiveForgetPassword", true);
		
		User usr = userService.findByEmail(email);
		
		if (usr == null) {
			model.addAttribute("emailNotExist", true);
			return "signup";
		}
		
		try {
			String token = UUID.randomUUID().toString();
			usr.setToken(token);
			userService.save(usr);
			
			String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
			SimpleMailMessage newEmail = mailConstructor.constructResetTokenEmail(appUrl, request.getLocale(), token, usr);

			mailSender.send(newEmail);

			model.addAttribute("forgetPasswordEmailSent", true);
		} catch (MailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			model.addAttribute("error", true);
		}
		
		return "signup";
	}

	/**
	 * Redirects to Forget Password Page
	 * @param model Model object
	 * @return String object (Webpapge)
	 */
	@RequestMapping("/forgetPassword")									// in use
	public String forgetPasswordGet(Model model) {

		model.addAttribute("classActiveForgetPassword", true);
		return "signup";
	}
	
	/**
	 * Url to reset password of the user
	 * @param mv ModelAndView Object
	 * @param token String object
	 * @param principal Princiapl Object
	 * @return String object (Webpapge)
	 */
	@RequestMapping(value = "/reset", method = RequestMethod.GET)
	public ModelAndView resetPasswordGet(ModelAndView mv, @RequestParam("token") String token,Principal principal) {
		
		if(principal != null) {
			User localUser=userService.findByUsername(principal.getName());
			
			mv.addObject("LoggedUser",localUser);
			
			mv.setViewName("accessDeniedPage");
			return mv;
		}

		User usr = userService.findBytoken(token);
		if (usr == null) {
			mv.setViewName("redirect:/");
			return mv;
		}

		mv.addObject("resetToken", usr.getToken());
		mv.setViewName("resetPassword");
		return mv;

	}
	
	/**
	 * redirects to forget password page
	 * @param mv ModelAndView object
	 * @param req HttpServletRequest object
	 * @param principal HttpServletRequest object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
	public ModelAndView resetPasswordPost(ModelAndView mv, HttpServletRequest req,Principal principal) {

		String newPassword = req.getParameter("Password");
		String confNewPassword = req.getParameter("Confirm");
		String token = req.getParameter("token");
		
		if(principal != null) {
			User localUser=userService.findByUsername(principal.getName());
			
			mv.addObject("LoggedUser",localUser);
			
			mv.setViewName("redirect:/");
			return mv;
		}


		User usr = userService.findBytoken(token);
		if (usr == null) {
			mv.addObject("Error", "Invalid Request");
			return mv;
		}

		if (!newPassword.contentEquals(confNewPassword)) {
			mv.addObject("Error", "Both password doesn't match");
			return mv;
		}
		
		if(newPassword.length()<6) {
			mv.addObject("Error", "Password must be atleast 6 character");
			return mv;
		}
		
		usr.setPassword(SecurityUtility.passwordEncoder().encode(newPassword));
		usr.setToken(null);
		userService.save(usr);

		mv.addObject("Success", "Password updated Successfully");
		mv.setViewName("resetPassword");
		return mv;

	}
	
	/**
	 * redirects to category page
	 * @param model Model object
	 * @return String object(webpage)
	 */
	@RequestMapping(value = "/categories",method = RequestMethod.GET)
	public String showCategoriesGet(Model model) {

		//List<Category> categories=catService.findAll();
		List<Category> categories=getCategories();
		model.addAttribute("categories", categories);
		return "categories";
	}
	
	
	@RequestMapping(value = "/researchPapers",method = RequestMethod.GET)
	public String showResearchPapersGet(Model model) {

		//List<Category> categories=catService.findAll();
		List<ResearchPaper> researchPapers= researchPaperService.findAllByShowOnHomePage();
		model.addAttribute("researchPapers", researchPapers);
		return "researchPapers";
	}


	/**************************** USER REGISTRATION *************************************************/

	/**
	 * Url to add user into system
	 * @param request HttpServletRequest object
	 * @param username String object
	 * @param firstName String object
	 * @param lastName String object
	 * @param userEmail String object
	 * @param password String object
	 * @param address String object
	 * @param phone String object
	 * @param gender String object
	 * @param model Model Object
	 * @return String object(Webpage)
	 * @throws Exception
	 */
	@RequestMapping(value = "/newUser", method = RequestMethod.POST)    // in use
	public String newUserPost(
			HttpServletRequest request,
			@ModelAttribute("username") String username, @ModelAttribute("firstName") String firstName,
			@ModelAttribute("lastName") String lastName, @ModelAttribute("email") String userEmail,
			@ModelAttribute("password") String password, @ModelAttribute("address") String address,
			@ModelAttribute("phone") String phone,@ModelAttribute("gender") String gender,
			Model model) throws Exception {

		long phoneLongValue;
		model.addAttribute("classActiveNewAccount", true);
		model.addAttribute("email", userEmail);
		model.addAttribute("username", username);

		if (userService.findByUsername(username) != null) {
			model.addAttribute("usernameExists", true);
			return "signup";
		}

		if (userService.findByEmail(userEmail) != null) {
			model.addAttribute("emailExists", true);
			return "signup";
		}

		if(!ServiceUtility.checkEmailValidity(userEmail)) {   // need to accommodate

			model.addAttribute("emailWrong", true);
			return "signup";
		}

		if(phone.length()>10) {								// need to accommodate

			model.addAttribute("phoneWrong", true);
			return "signup";

		}else {
			phoneLongValue=Long.parseLong(phone);

		}
		User user = new User();
		user.setId(userService.getNewId());
		user.setUsername(username);
		user.setEmail(userEmail);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setAddress(address);
		user.setGender(gender);
		user.setPhone(phoneLongValue);
		user.setPassword(SecurityUtility.passwordEncoder().encode(password));
		user.setDateAdded(ServiceUtility.getCurrentTime());
		user.setEmailVerificationCode("");

		userService.save(user);
		model.addAttribute("emailSent", "true");

		return "signup";

	}

	/**
	 * Redirects to adduser page
	 * @param model Model Object
	 * @return String object (webpage)
	 */
	@RequestMapping("/newUser")										// in use
	public String newUserGet (Model model) {

		model.addAttribute("classActiveNewAccount", true);
		return "signup";


	}

	/************************** END ****************************************************/

	/**************************** DASHBAORD PAGE FOR ALL USER *****************************************/

	/**
	 * redirects to user Dashboard page 
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/dashBoard",method = RequestMethod.GET)
	public String dashBoardGet (Model model,Principal principal) {

		User usr=new User();
		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		List<UserRole> userRoles= usrRoleService.findAllByUser(usr);
		List<UserRole> pendingUserRoles= usrRoleService.findAllByUser(usr);
		List<Integer> roleIds = new ArrayList<Integer>();

		for(int i=0; i<userRoles.size();i++) {
			if(!userRoles.get(i).getStatus()) {
//				roleIds.add(userRoles.get(i).getRole().getRoleId());
				pendingUserRoles.add(userRoles.get(i));
			}
		}
		model.addAttribute("roleIds", roleIds);
		model.addAttribute("userRoles", pendingUserRoles);
		return "roleAdminDetail";
	}

	/****************************************** ADD CATEGORY *************************************************/
	
	/**
	 * redirects to add category page
	 * @param model model Object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/addCategory",method = RequestMethod.GET)
	public String addCategoryGet(Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		List<Category> categories = catService.findAll();
		model.addAttribute("categories", categories);

		return "addCategory";

	}

	/**
	 * Url to add category to object
	 * @param model Model object
	 * @param principal Principal object
	 * @param req HttpServletRequest object
	 * @param files MultipartFile object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/addCategory",method = RequestMethod.POST)
	public String addCategoryPost(Model model,Principal principal,HttpServletRequest req,
								  @RequestParam("categoryImage") MultipartFile files) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
		List<Category> categoriesTemp = catService.findAll();
		model.addAttribute("categories", categoriesTemp);

		String categoryName = req.getParameter("categoryname");
		String categoryDesc = req.getParameter("categoryDesc");

		if(categoryName == null) {
			model.addAttribute("error_msg", "Please Try Again");
			return "addCategory";
		}
		
		if(categoryDesc == null) {
			model.addAttribute("error_msg", "Please Try Again");
			return "addCategory";
		}

		if(catService.findBycategoryname(categoryName)!=null) {
			model.addAttribute("error_msg", CommonData.RECORD_EXISTS);
			return "addCategory";
		}

		if(!ServiceUtility.checkFileExtensionImage(files)) {
			model.addAttribute("error_msg", CommonData.JPG_PNG_EXT);
			return "addCategory";
		}

		int newCatId=catService.getNewCatId();
		Category cat=new Category();
		cat.setCategoryId(newCatId);
		cat.setCatName(categoryName);
		cat.setDateAdded(ServiceUtility.getCurrentTime());
		cat.setPosterPath("null");
		cat.setDescription(categoryDesc);
		cat.setUser(usr);

		Set<Category> categories=new HashSet<Category>();
		categories.add(cat);

		try {
			userService.addUserToCategory(usr, categories);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			model.addAttribute("error_msg", CommonData.RECORD_ERROR);
			return "addCategory";
		}

		try {
				ServiceUtility.createFolder(env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadDirectoryCategory+newCatId);
				String pathtoUploadPoster=ServiceUtility.uploadFile(files, env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadDirectoryCategory+newCatId);
				int indexToStart=pathtoUploadPoster.indexOf("Media");

				String document=pathtoUploadPoster.substring(indexToStart, pathtoUploadPoster.length());

				Category local=catService.findBycategoryname(categoryName);
				local.setPosterPath(document);

				catService.save(local);



		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			model.addAttribute("error_msg", CommonData.RECORD_ERROR);
			return "addCategory";
		}

		categoriesTemp = catService.findAll();
		model.addAttribute("categories", categoriesTemp);
		model.addAttribute("success_msg", CommonData.RECORD_SAVE_SUCCESS_MSG);
		return "addCategory";

	}

	/************************************END**********************************************/
	/************************************ADD ORGANIZATIONAL ROLE**********************************************/

	/**
	 * redirect to add organization role page
	 * @param model Model object
	 * @param principal principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/addOrganizationRole",method = RequestMethod.GET)
	public String addOrganizationRoleGet(Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		List<OrganizationRole> orgRoles=organizationRoleService.findAll();

		model.addAttribute("orgRoles", orgRoles);

		return "addOrganizationRole";

	}

	/**
	 * add organization role object to database
	 * @param model Model object
	 * @param principal principal object
	 * @param req HttpServletRequest
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/addOrganizationRole",method = RequestMethod.POST)
	public String addOrganizationRolePost(Model model,Principal principal,HttpServletRequest req) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		List<OrganizationRole> orgRoles=organizationRoleService.findAll();

		model.addAttribute("orgRoles", orgRoles);

		String orgRoleName=req.getParameter("role");

		if(orgRoleName==null) {

			model.addAttribute("error_msg", CommonData.RECORD_ERROR);
			return "addOrganizationRole";
		}

		if(organizationRoleService.getByRole(orgRoleName)!=null) {

			model.addAttribute("error_msg", CommonData.RECORD_EXISTS);
			return "addOrganizationRole";
		}

		String roleName = orgRoleName.substring(0, 1).toUpperCase() + orgRoleName.substring(1).toLowerCase();
		OrganizationRole orgRole = new OrganizationRole();
		orgRole.setDateAdded(ServiceUtility.getCurrentTime());
		orgRole.setRoleId(organizationRoleService.getnewRoleId());
		orgRole.setRole(roleName);
		organizationRoleService.save(orgRole);

		Set<OrganizationRole> roles=new HashSet<OrganizationRole>();
		roles.add(orgRole);

		orgRoles=organizationRoleService.findAll();

		model.addAttribute("orgRoles", orgRoles);
		model.addAttribute("success_msg", CommonData.RECORD_SAVE_SUCCESS_MSG);

		return "addOrganizationRole";

	}
	
	/**
	 * redirect to edit organization role page given id
	 * @param id int value
	 * @param model Model object
	 * @param principal principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/organization_role/edit/{name}", method = RequestMethod.GET)
	public String editOrganizationRoleGet(@PathVariable(name = "name") String orgname,Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		OrganizationRole role = organizationRoleService.getByRole(orgname);

		model.addAttribute("role",role);

		return "updateOrganizationalRole";
	}

	/**
	 * update organization role object to database
	 * @param model Model object
	 * @param principal principal object
	 * @param req HttpServletRequest
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/update_organization_role",method = RequestMethod.POST)
	public String updateOrganizationRolePost(Model model,Principal principal,HttpServletRequest req) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		String roleName=req.getParameter("role");
		String lanIdInString = req.getParameter("roleId");
		int roleId = Integer.parseInt(lanIdInString);
		OrganizationRole role = organizationRoleService.getById(roleId);
//		Language lan = lanService.getById(lanId);

		if(role == null) {
			model.addAttribute("error_msg", CommonData.RECORD_ERROR);
			model.addAttribute("role",role);
			return "updateOrganizationalRole";  //  accomodate view part
		}

		if(roleName==null) {

			model.addAttribute("error_msg", CommonData.RECORD_ERROR);
			model.addAttribute("role",role);
			return "updateOrganizationalRole";  //  accomodate view part
		}

		if(lanService.getByLanName(roleName)!=null) {

			model.addAttribute("error_msg", CommonData.RECORD_EXISTS);
			model.addAttribute("role",role);
			return "updateOrganizationalRole";   //  accomodate view part
		}

		String role_formatted = roleName.substring(0, 1).toUpperCase() + roleName.substring(1).toLowerCase();

		role.setRole(role_formatted);
		try {
			organizationRoleService.save(role);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			model.addAttribute("language",role);
			model.addAttribute("error_msg", CommonData.RECORD_ERROR);
			return "updateOrganizationalRole";  //  accomodate view part
		}

//		role = lanService.getById(lanId);
		role = organizationRoleService.getById(roleId);
		model.addAttribute("role",role);
		model.addAttribute("success_msg", CommonData.RECORD_SAVE_SUCCESS_MSG);

		return "updateOrganizationalRole";  //  accomodate view part

	}


	/************************************END**********************************************/

	/************************************ADD LANGUAGE**********************************************/

	/**
	 * redirect to add language page
	 * @param model Model object
	 * @param principal principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/addLanguage",method = RequestMethod.GET)
	public String addLanguageGet(Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		List<Language> languages=lanService.getAllLanguages();

		model.addAttribute("languages", languages);

		return "addlanguage";

	}

	/**
	 * add language object to database
	 * @param model Model object
	 * @param principal principal object
	 * @param req HttpServletRequest
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/addLanguage",method = RequestMethod.POST)
	public String addLanguagePost(Model model,Principal principal,HttpServletRequest req) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		List<Language> languagesTemp=lanService.getAllLanguages();

		model.addAttribute("languages", languagesTemp);

		String languagename=req.getParameter("languageName");

		if(languagename==null) {

			model.addAttribute("error_msg", CommonData.RECORD_ERROR);
			return "addlanguage";
		}

		if(lanService.getByLanName(languagename)!=null) {

			model.addAttribute("error_msg", CommonData.RECORD_EXISTS);
			return "addlanguage";
		}

		String language_formatted = languagename.substring(0, 1).toUpperCase() + languagename.substring(1).toLowerCase();
		Language newLan=new Language();
		newLan.setLanId(lanService.getnewLanId());
		newLan.setLangName(language_formatted);
		newLan.setDateAdded(ServiceUtility.getCurrentTime());
		newLan.setUser(usr);

		Set<Language> languages=new HashSet<Language>();
		languages.add(newLan);

		try {
			userService.addUserToLanguage(usr, languages);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			model.addAttribute("error_msg", CommonData.RECORD_ERROR);
			return "addlanguage";
		}

		languagesTemp=lanService.getAllLanguages();

		model.addAttribute("languages", languagesTemp);

		model.addAttribute("success_msg", CommonData.RECORD_SAVE_SUCCESS_MSG);

		return "addlanguage";

	}

	/************************************END**********************************************/

	/******************************ADD CAROUSEL ******************************************/
	
	/**
	 * redirect to add carousel page
	 * @param model Model object
	 * @param principal principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/addCarousel",method = RequestMethod.GET)
	public String addCarouselGet(Model model,Principal principal) {
		User usr=new User();
		if(principal!=null) {
			usr=userService.findByUsername(principal.getName());
		}
		model.addAttribute("userInfo", usr);

		List<Carousel> cara = caroService.findAll();

		model.addAttribute("carousels", cara);

		return "addCarousel";
	}

	/**
	 * Add Carousel object
	 * @param model Model object
	 * @param principal principal object
	 * @param file MultipartFile
	 * @param name String object
	 * @param desc String object
 	 * @return String object
	 */
	@RequestMapping(value = "/addCarousel",method = RequestMethod.POST)
	public String addCarouselPost(Model model,Principal principal,
								  @RequestParam("file") MultipartFile file,
								  @RequestParam(value = "eventName") String name,
								  @RequestParam(name = "eventDesc") String desc
								  ) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		List<Carousel> cara = caroService.findAll();

		model.addAttribute("carousels", cara);
		
		if(name == null) {  // throw error
			model.addAttribute("error_msg","Please Try Again");
			return "addCarousel";
		}
		
		if(desc == null) {  // throw error
			model.addAttribute("error_msg","Please Try Again");
			return "addCarousel";
		}

		if(!ServiceUtility.checkFileExtensionImage(file)) {  // throw error
			model.addAttribute("error_msg",CommonData.JPG_PNG_EXT);
			return "addCarousel";
		}

		Carousel caraTemp = new Carousel();
		caraTemp.setId(caroService.getNewId());
		caraTemp.setDescription(desc);
		caraTemp.setEventName(name);

		try {

			caroService.save(caraTemp);

			ServiceUtility.createFolder(env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadCarousel+caraTemp.getId());
			String pathtoUploadPoster=ServiceUtility.uploadFile(file, env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadCarousel+caraTemp.getId());
			int indexToStart=pathtoUploadPoster.indexOf("Media");

			String document=pathtoUploadPoster.substring(indexToStart, pathtoUploadPoster.length());

			caraTemp.setPosterPath(document);

			caroService.save(caraTemp);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				model.addAttribute("error_msg",CommonData.RECORD_ERROR);
				caroService.delete(caraTemp);
				return "addCarousel";
			}

	
		cara = caroService.findAll();
		model.addAttribute("carousels", cara);
		model.addAttribute("success_msg",CommonData.RECORD_SAVE_SUCCESS_MSG);

		return "addCarousel";


	}
	/************************************END**********************************************/
	
	/*****************************ADD Research Paper *************************************/
	
	@RequestMapping(value = "/addResearchPaper",method = RequestMethod.GET)
	public String addResearchPaperGet(Model model,Principal principal) {
		User usr=new User();
		if(principal!=null) {
			usr=userService.findByUsername(principal.getName());
		}
		model.addAttribute("userInfo", usr);

		List<ResearchPaper> researchPapers = researchPaperService.findAll();
		for(ResearchPaper temp : researchPapers) {
			makeThumbnailofResearchPaper(temp);
		}

		model.addAttribute("researchPapers", researchPapers);

		return "addResearchPaper";
	}
	
	
	@RequestMapping(value = "/addResearchPaper",method = RequestMethod.POST)
	public String addResearchPaperPost(Model model,Principal principal,
								  @RequestParam("researchFile") MultipartFile researchFile,
								  @RequestParam(value = "title") String title,
								  @RequestParam(name = "researchPaperDesc") String researchPaperDesc
								  ) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
		boolean viewSection= false;
		model.addAttribute("viewSection", viewSection);

		List<ResearchPaper> researchPapers = researchPaperService.findAll();

		model.addAttribute("researchPapers", researchPapers);

		
		if(title == null) {  // throw error
			model.addAttribute("error_msg","Please Try Again");
			return "addResearchPaper";
		}
		
		if(researchPaperDesc == null) {  // throw error
			model.addAttribute("error_msg","Please Try Again");
			return "addResearchPaper";
		}

		if(!ServiceUtility.checkFileExtensiononeFilePDF(researchFile)) {  // throw error
			model.addAttribute("error_msg", "Only PDf file is required");
			return "addResearchPaper";
		}
		
		

		ResearchPaper researchPaperTemp = new ResearchPaper();
		researchPaperTemp.setId(researchPaperService.getNewId());
		researchPaperTemp.setDescription(researchPaperDesc);
		researchPaperTemp.setTitle(title);
		researchPaperTemp.setDateAdded(ServiceUtility.getCurrentTime());

		try {

			

			ServiceUtility.createFolder(env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadResearchPaper+researchPaperTemp.getId());
			String pathtoUploadPoster=ServiceUtility.uploadFile(researchFile, env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadResearchPaper+researchPaperTemp.getId());
			int indexToStart=pathtoUploadPoster.indexOf("Media");

			String document=pathtoUploadPoster.substring(indexToStart, pathtoUploadPoster.length());

			researchPaperTemp.setResearchPaperPath(document);

			researchPaperService.save(researchPaperTemp);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				viewSection= false;
				model.addAttribute("viewSection", viewSection);
				model.addAttribute("error_msg",CommonData.RECORD_ERROR);
				researchPaperService.delete(researchPaperTemp);
				return "addResearchPaper";
			}

		viewSection= false;
		model.addAttribute("viewSection", viewSection);
		researchPapers = researchPaperService.findAll();
		model.addAttribute("researchPapers", researchPapers);
		model.addAttribute("success_msg",CommonData.RECORD_SAVE_SUCCESS_MSG);

		return "addResearchPaper";


	}

	
	/************************************************************************************/
	
	/**************************************ADD PROMOVIDEO*********************************/
	
	@RequestMapping(value = "/addPromoVideo",method = RequestMethod.GET)
	public String addPromoVideoGet(Model model,Principal principal) {
		User usr=new User();
		if(principal!=null) {
			usr=userService.findByUsername(principal.getName());
		}
		model.addAttribute("userInfo", usr);
		
		List<Language> lans = lanService.getAllLanguages();
		model.addAttribute("languages", lans);
		
		List<PromoVideo> promovideos = promoVideoService.findAll();
		List<PathofPromoVideo> pathofPromoVideos= pathofPromoVideoService.findAll();
		logger.info("promovideos={} {}", promovideos, promovideos.isEmpty());
		model.addAttribute("promoVideos", promovideos);
		model.addAttribute("pathofPromoVideos",pathofPromoVideos);
		
		
		return "addPromoVideo";
	}
	
	
	
	
	@RequestMapping(value = "/addPromoVideo",method = RequestMethod.POST)
	public String addPromoVideoPost(Model model,Principal principal,
								  @RequestParam("promoVideo") List<MultipartFile> promoVideos,
								  @RequestParam(name = "languageName") List<Integer> languageIds,
								  @RequestParam(name = "title") String title) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		
		model.addAttribute("userInfo", usr);
		System.out.println(languageIds);
		
		boolean viewSection= false;
		model.addAttribute("viewSection", viewSection);
		
		List<Language> languages=lanService.getAllLanguages();
		model.addAttribute("languages", languages);
		List<PromoVideo> promoVideosList = promoVideoService.findAll();
		List<PathofPromoVideo> pathofPromoVideos=pathofPromoVideoService.findAll();
		
		model.addAttribute("promoVideos", promoVideosList);
		model.addAttribute("pathofPromoVideos",pathofPromoVideos);
		
		for(MultipartFile uniquefile: promoVideos) {
			if(!uniquefile.isEmpty()) {
				
				if(!ServiceUtility.checkFileExtensionVideo(uniquefile)) { // throw error on extension
					model.addAttribute("error_msg",CommonData.VIDEO_FILE_EXTENSION_ERROR);
					return addPromoVideoGet(model, principal);
				}
				
				if(!ServiceUtility.checkVideoSizePromoVideo(uniquefile)) {
					model.addAttribute("error_msg","File size must be less than 1 GB");
					return addPromoVideoGet(model, principal);
				}
				
				
				
			}
			
		}
		

	
		if(title == null) {  // throw error
		model.addAttribute("error_msg","Please Try again");
		return  addPromoVideoGet(model, principal);
		}
		
		
	   boolean filesError=false;
	   boolean duplicatLanguage=false;
	   Language lan=lanService.getById(languageIds.get(0));
		
		
		
		int newPromoVideoId= promoVideoService.getNewId();
		PromoVideo promoVideoTemp = new PromoVideo();
		promoVideoTemp.setPromoId(newPromoVideoId);
		promoVideoTemp.setTitle(title);
		promoVideoTemp.setDateAdded(ServiceUtility.getCurrentTime());
		
		try {
			List<PathofPromoVideo> pathofPromoVideoList=new ArrayList<>();
			
			String document1="";
			
			int newPathOfPromoId= pathofPromoVideoService.getNewId();
			List<String>addedLanguages= new ArrayList<>();
			for(int i=0; i<languageIds.size(); i++) {
				document1="";
				
				if(languageIds.get(i)==0){
					break;
				}
				if(!promoVideos.get(i).isEmpty()) 
				{
				
				String langName=lanService.getById(languageIds.get(i)).getLangName();
				PathofPromoVideo pathofPromoVideo= new PathofPromoVideo();
				
			
				if(!promoVideos.get(i).isEmpty()) {
				ServiceUtility.createFolder(env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadPromoVideo + newPromoVideoId  + "/" + langName );
				String pathtoUploadPoster1=ServiceUtility.uploadVideoFile(promoVideos.get(i), env.getProperty("spring.applicationexternalPath.name")+ CommonData.uploadPromoVideo + newPromoVideoId  + "/" + langName );
				int indexToStart1=pathtoUploadPoster1.indexOf("Media");
				 document1=pathtoUploadPoster1.substring(indexToStart1, pathtoUploadPoster1.length());
				}
			
			
			
			for(String testLan: addedLanguages){
				if(testLan==langName) {
					duplicatLanguage=true;
				}
				
			}
			
			addedLanguages.add(langName);
			
			pathofPromoVideoList.add(new PathofPromoVideo(newPathOfPromoId, ServiceUtility.getCurrentTime(), document1, promoVideoTemp, lanService.getById(languageIds.get(i))));
			newPathOfPromoId +=1;
			
			
			}
				else {
					filesError=true;
				}
			}

			if(filesError==false && duplicatLanguage==false) {
				
				try {
					promoVideoService.save(promoVideoTemp);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					model.addAttribute("error_msg",CommonData.RECORD_ERROR);
					System.out.println("AlokSP Error2");
					return  addPromoVideoGet(model, principal);
				}
				
				
				
				pathofPromoVideoService.saveAll(pathofPromoVideoList);
			}	
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		viewSection= false;
		model.addAttribute("viewSection", viewSection);
		
		model.addAttribute("error_msg",CommonData.RECORD_ERROR);
		
		return  addPromoVideoGet(model, principal);
		
	}
		
		if(filesError==true || duplicatLanguage==true) {
			
			viewSection= false;
			
			model.addAttribute("viewSection", viewSection);
			
			if(filesError==true) {
				model.addAttribute("error_msg", "Video Files should not be null for selected language");
			}else {
				model.addAttribute("error_msg", "Duplicate Languages are not allowed");
			}
			
		} else {
			viewSection= false;
			model.addAttribute("viewSection", viewSection);
			
			model.addAttribute("success_msg", CommonData.RECORD_SAVE_SUCCESS_MSG);
		}
		return addPromoVideoGet(model, principal);
	}

	
	
	/***************************************END********************************************/
	
	
	

	/******************************ADD BROUCHURE ******************************************/
	
	/**
	 * redirect to add brochure page
	 * @param model Model object
	 * @param principal principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/addBrochure",method = RequestMethod.GET)
	public String addBrochureGet(Model model,Principal principal) {
		User usr=new User();
		if(principal!=null) {
			usr=userService.findByUsername(principal.getName());
		}
		model.addAttribute("userInfo", usr);
		List<Category> category = catService.findAll();
		model.addAttribute("categories", category);
		List<Language> lans = lanService.getAllLanguages();
		model.addAttribute("languages", lans);
		List<Brouchure> brouchures = broService.findAll();
		
		
		List<Version> versions= new ArrayList<Version>();
		for(Brouchure bro: brouchures) {
			Version ver = verService.findByBrouchureAndPrimaryVersion(bro, bro.getPrimaryVersion());
			versions.add(ver);
			/*for(Version ver: allVersions) {
				if(bro.getId()==ver.getBrouchure().getId() && bro.getPrimaryVersion()==ver.getBroVersion())
					versions.add(ver);
			}*/
		}
		Collections.sort(versions, Version.SortByBroVersionTime);
		for(Version ver: versions) {
			System.out.println(ver.getDateAdded());
		}
		
		
		List<FilesofBrouchure> filesofbrochures= filesofbrouchureService.findAll();
		
		for(FilesofBrouchure temp : filesofbrochures) {
			makeThumbnail(temp);
		}
		
		model.addAttribute("brouchures", brouchures);
		model.addAttribute("versions", versions);
		model.addAttribute("filesofbrouchureService",filesofbrouchureService);
		
		
		return "addBrochure";
	}

	/**
	 * Add brochure to the system
	 * @param model Model object
	 * @param principal principal object
	 * @param brochure MultipartFile
	 * @param categoryId int value
	 * @param topicId int value
	 * @param languageId int value
	 * @return String object
	 */
	@RequestMapping(value = "/addBrochure",method = RequestMethod.POST)
	public String addBrochurePost(Model model,Principal principal,
								  @RequestParam("brouchure") List<MultipartFile> brochures,
								  @RequestParam("brouchurePrint") List<MultipartFile> brochurePrints,
								  @RequestParam(value = "categoryName") int categoryId,
								  @RequestParam(name = "inputTopicName") int topicId,
								  @RequestParam(name = "languageName") List<Integer> languageIds,
								  @RequestParam(value = "primaryVersion") int primaryVersion,
								  @RequestParam(name = "title") String title) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		
		model.addAttribute("userInfo", usr);
		System.out.println(languageIds);
		
		boolean viewSection= false;
		model.addAttribute("viewSection", viewSection);
		
		List<Language> languages=lanService.getAllLanguages();
		List<Category> categories=catService.findAll();
		model.addAttribute("categories", categories);
		model.addAttribute("languages", languages);
		List<Brouchure> brouchures = broService.findAll();
		
		List<Version> versions= new ArrayList<>();
		for(Brouchure bro: brouchures) {
			Version ver= verService.findByBrouchureAndPrimaryVersion(bro, bro.getPrimaryVersion());
			versions.add(ver);
		}
		Collections.sort(versions, Version.SortByBroVersionTime);
		model.addAttribute("brouchures", brouchures);
		model.addAttribute("versions", versions);
		model.addAttribute("filesofbrouchureService",filesofbrouchureService);
		
		for(MultipartFile uniquefile: brochures) {
			if(!uniquefile.isEmpty()) {
				if(!ServiceUtility.checkFileExtensionImage(uniquefile) && !ServiceUtility.checkFileExtensiononeFilePDF(uniquefile)){  // throw error
					model.addAttribute("error_msg","Only image and pdf files are supported");
					return  addBrochureGet(model, principal);
				}
			}
			
		}
		
		for(MultipartFile uniquePrintfile: brochurePrints) {
			if(!uniquePrintfile.isEmpty()) {
				if(!ServiceUtility.checkFileExtensionImage(uniquePrintfile) && !ServiceUtility.checkFileExtensiononeFilePDF(uniquePrintfile)){  // throw error
					model.addAttribute("error_msg","Only image and pdf files are supported");
					return  addBrochureGet(model, principal);
				}
			}
		}
		
		
		
		
		Category cat=catService.findByid(categoryId);
		
		Topic topic=topicService.findById(topicId);
		
		/*if(cat == null) {  // throw error
			model.addAttribute("error_msg","Please Try again");
			return "addBrochure";
		}
		
		if(topic == null) {  // throw error
			model.addAttribute("error_msg","Please Try again");
			return "addBrochure";
		}
		*/
		//int versionInt = Integer.parseInt(version);
		String versionStr=Integer.toString(primaryVersion);
		
		if(versionStr == null) {  // throw error
		model.addAttribute("error_msg","Please Try again");
		return  addBrochureGet(model, principal);
		}
	
		if(title == null) {  // throw error
		model.addAttribute("error_msg","Please Try again");
		return  addBrochureGet(model, principal);
		}
		
		
	   boolean filesError=false;
	   boolean duplicatLanguage=false;
	   Language lan=lanService.getById(languageIds.get(0));
		
		
		int newBroId=broService.getNewId();
		int newVerid= verService.getNewId();
		Brouchure brochureTemp = new Brouchure();
		brochureTemp.setId(newBroId);
		brochureTemp.setLan(lan);
		brochureTemp.setTitle(title);
		brochureTemp.setPrimaryVersion(primaryVersion);
		
		if(cat!=null) {
			brochureTemp.setCatId(cat);
		}
		
		
		if(cat !=null && topic !=null) 
		
		{
			TopicCategoryMapping topicCat=topicCatService.findAllByCategoryAndTopic(cat, topic);
			brochureTemp.setTopicCatId(topicCat);
		}
		
		

		


		Version version= new Version();
		
		try {
			List<FilesofBrouchure> filesofbrochureList=new ArrayList<>();
			
			String document1="";
			String printDocument="";
			int newbroFileId= filesofbrouchureService.getNewId();
			List<String>addedLanguages= new ArrayList<>();
			for(int i=0; i<languageIds.size(); i++) {
				document1="";
				printDocument="";
				
				if(languageIds.get(i)==0){
					break;
				}
				if(!brochures.get(i).isEmpty()) 
				{
				
				String langName=lanService.getById(languageIds.get(i)).getLangName();
				FilesofBrouchure filesOfbrouchure= new FilesofBrouchure();
				
			
				if(!brochures.get(i).isEmpty()) {
				ServiceUtility.createFolder(env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadBrouchure+newBroId + "/" + primaryVersion + "/" + "web" + "/" + langName );
				String pathtoUploadPoster1=ServiceUtility.uploadFile(brochures.get(i), env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadBrouchure+newBroId +"/" + primaryVersion + "/" + "web" + "/" + langName );
				int indexToStart1=pathtoUploadPoster1.indexOf("Media");
				 document1=pathtoUploadPoster1.substring(indexToStart1, pathtoUploadPoster1.length());
				}
			
			
			if(!brochurePrints.get(i).isEmpty()) {
			ServiceUtility.createFolder(env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadBrouchure+newBroId+"/" +primaryVersion+ "/" + "printdoc" + "/" + langName);
			String pathtoUploadPoster2=ServiceUtility.uploadFile(brochurePrints.get(i), env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadBrouchure+newBroId+"/" +primaryVersion+ "/" + "printdoc" + "/" + langName);
			int indexToStart2=pathtoUploadPoster2.indexOf("Media");
			printDocument=pathtoUploadPoster2.substring(indexToStart2, pathtoUploadPoster2.length());
			
			
			}
			
			
			
			for(String testLan: addedLanguages){
				if(testLan==langName) {
					duplicatLanguage=true;
				}
				
			}
			
			addedLanguages.add(langName);
			
			filesofbrochureList.add(new FilesofBrouchure(newbroFileId, ServiceUtility.getCurrentTime(), document1, printDocument,  version, lanService.getById(languageIds.get(i))));
			newbroFileId +=1;
			
			
			}
				else {
					filesError=true;
				}
			}

			if(filesError==false && duplicatLanguage==false) {
				
				try {
					broService.save(brochureTemp);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					model.addAttribute("error_msg",CommonData.RECORD_ERROR);
					System.out.println("AlokSP Error2");
					return  addBrochureGet(model, principal);
				}
				
				version.setVerId(newVerid);
				version.setVersionPosterPath(document1);
				version.setVersionPrintPosterPath(printDocument);
				version.setBrouchure(brochureTemp);
				version.setBroVersion(primaryVersion);
				version.setDateAdded(ServiceUtility.getCurrentTime());
				verService.save(version);
				
				filesofbrouchureService.saveAll(filesofbrochureList);
			}	
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		viewSection= false;
		model.addAttribute("viewSection", viewSection);
		
		model.addAttribute("error_msg",CommonData.RECORD_ERROR);
		verService.delete(version);
		System.out.println(" AlokSP  Error4");
		/*brouchures = broService.findAll();
		allVersions= verService.findAll();
		versions= new ArrayList<>();
		for(Brouchure bro: brouchures) {
			for(Version ver: allVersions) {
				if(bro.getId()==ver.getBrouchure().getId() && bro.getPrimaryVersion()==ver.getBroVersion())
					versions.add(ver);
			}
		}
		Collections.sort(versions, Version.SortByBroVersionTime);
		model.addAttribute("brouchures", brouchures);
		model.addAttribute("versions", versions);
		*/
		return  addBrochureGet(model, principal);
		
	}
		
		if(filesError==true || duplicatLanguage==true) {
			viewSection= false;
			model.addAttribute("viewSection", viewSection);
			if(filesError==true) {
				model.addAttribute("error_msg", "Web Files should not be null for selected language");
			}else {
				model.addAttribute("error_msg", "Duplicate Languages are not allowed");
			}
			
		} else {
			viewSection= false;
			model.addAttribute("viewSection", viewSection);
			
			model.addAttribute("success_msg",CommonData.RECORD_SAVE_SUCCESS_MSG);
		}
		return addBrochureGet(model, principal);
	}

	/********************************END****************************************************/

	/************************************ADD TOPIC**********************************************/

	/**
	 * redirect to add topic page
	 * @param model Model object
	 * @param principal principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/addTopic",method = RequestMethod.GET)
	public String addTopicGet(Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}
		List<TopicCategoryMapping> tcm=topicCatService.findAll();
		
		TopicCategoryMapping tcp=new TopicCategoryMapping();
		

		model.addAttribute("userInfo", usr);

		List<Category> category = catService.findAll();
		Collections.sort(category);
		
		List<Category> getcats= getCategories();
		
		List<Category>newCategories1=new ArrayList<>();
		List<Category>newCategories2=new ArrayList<>();
		
		for(Category cat1: category) {
			for(Category cat2: getcats)
				if(cat1==cat2)
					continue;
			
			if(cat1.isStatus()) {
				newCategories1.add(cat1);
			}
			else {
				newCategories2.add(cat1);		
		}
			
		}
		newCategories1.addAll(newCategories2);
		getcats.addAll(newCategories1);

		model.addAttribute("categories",getcats);

		List<Topic> topics = topicService.findAll();

		model.addAttribute("topics", topics);
		model.addAttribute("tcm", tcm);
		return "addTopic";

	}

	/**
	 * add topic into database
	 * @param model Model object
	 * @param principal Principal object
	 * @param categoryId int value
	 * @param topicName String object
	 * @param orderValue int value
	 * @return String object
	 */
	@RequestMapping(value = "/addTopic",method = RequestMethod.POST)
	public String addTopicPost(Model model,Principal principal,
							   @RequestParam(name = "topicId") int topicId,
							   @RequestParam(name = "categoryName") int categoryId,
							   @RequestParam(name = "topicName") String topicName,
							   @RequestParam(name = "orderValue") int orderValue) {

		User usr=new User();

		if(principal!=null) {
			usr=userService.findByUsername(principal.getName());
		}

		Category cat =catService.findByid(categoryId);

		if(cat == null) {
			model.addAttribute("error_msg", "Category Doesn't Exist");
			return addTopicGet(model,principal);
		}
		
		Topic topicTemp;
				
		
		
		if(topicId==-1) {
			topicTemp = topicService.findBytopicName(topicName);
		}
		else {
			topicTemp = topicService.findById(topicId);
		}

		

		if(topicTemp!=null) {

			if(topicCatService.findAllByCategoryAndTopic(cat, topicTemp)==null) {

				TopicCategoryMapping localTopicMap=new TopicCategoryMapping(topicCatService.getNewId(), true, cat, topicTemp,orderValue);
				topicCatService.save(localTopicMap);
				model.addAttribute("success_msg", CommonData.RECORD_SAVE_SUCCESS_MSG);
				return addTopicGet(model,principal);

			}else {

				model.addAttribute("error_msg", CommonData.RECORD_ERROR);
				return addTopicGet(model,principal);
			}
		}

		Topic local=new Topic();
		local.setTopicId(topicService.getNewTopicId());
		local.setTopicName(topicName);
		local.setDateAdded(ServiceUtility.getCurrentTime());
		local.setUser(usr);

		Set<Topic> topics=new HashSet<Topic>();
		topics.add(local);

		try {
			userService.addUserToTopic(usr, topics);
			TopicCategoryMapping localTopicMap=new TopicCategoryMapping(topicCatService.getNewId(), true, cat, local,orderValue);
			topicCatService.save(localTopicMap);


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			model.addAttribute("error_msg", CommonData.RECORD_ERROR);
			return addTopicGet(model,principal);
		}
		
		

		model.addAttribute("success_msg", CommonData.RECORD_SAVE_SUCCESS_MSG);
		
		
		
		return addTopicGet(model,principal);

	}

	/**
	 * redirects to 
 page of topic given id
	 * @param model Model object
	 * @param principal principal object
	 * @param id int value
	 * @return String object (webpage)
	 */
	/*@RequestMapping(value = "/topic/edit/{topicName}", method = RequestMethod.GET)
	public String editTopicGet(@PathVariable(name = "topicName") String topicTemp,Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		Topic topic=topicService.findBytopicName(topicTemp);
		
		if(topic == null) {
			return "redirect:/addTopic";
		}

		model.addAttribute("topic",topic);

		return "updateTopic";  // need to accomdate view part
	}
	*/
	
	@RequestMapping(value = "/topic/edit/{topicCatId}", method = RequestMethod.GET)
	public String editTopicGet(@PathVariable(name = "topicCatId") int topicCatId, Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
		
		TopicCategoryMapping tcp= tcmRepository.findById(topicCatId).get();
		
		Topic topic=tcp.getTopic();
		
		
		if(topic == null) {
			return "redirect:/addTopic";
		}

		model.addAttribute("topic",topic);
		model.addAttribute("topicCatMap",tcp);
		
		
		

		return "updateTopic";  // need to accomdate view part
	}

	/**
	 * update topic object 
	 * @param model Model object
	 * @param principal principal object
	 * @param req HttpServletRequest
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/updateTopic",method = RequestMethod.POST)
	public String updateTopicPost(Model model,Principal principal,HttpServletRequest req) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		String topicname=req.getParameter("topicName");
		String topicIdInString = req.getParameter("TopicId");
		int topicId = Integer.parseInt(topicIdInString);
		
		String orderVal=req.getParameter("orderValue");
		int orderValue = Integer.parseInt(orderVal);
		String topicCatIdInString = req.getParameter("TopicCatId");
		int topicCatId = Integer.parseInt(topicCatIdInString);
		
		TopicCategoryMapping tcp= tcmRepository.findById(topicCatId).get();
		Topic topic = topicService.findById(topicId);

		if(topic == null || tcp == null) {
			model.addAttribute("error_msg", CommonData.RECORD_ERROR);
			model.addAttribute("topicCatMap",tcp);
			model.addAttribute("topic",topic);
			return "updateTopic";  //  accomodate view part
		}

		if(topicname==null ) {

			model.addAttribute("error_msg", CommonData.RECORD_ERROR);
			model.addAttribute("topicCatMap",tcp);
			model.addAttribute("topic",topic);
			return "updateTopic";  //  accomodate view part
		}
		
		
		  if(topicService.findBytopicName(topicname)!=null &&  topicService.findBytopicName(topicname)!= topic) {
			model.addAttribute("topicCatMap",tcp);
			model.addAttribute("error_msg", CommonData.RECORD_EXISTS);
			model.addAttribute("topic",topic);
			return "updateTopic";   //  accomodate view part
		}
		 
		
		
		

		topic.setTopicName(topicname);
		tcp.setOrder(orderValue);

		try {
			topicService.save(topic);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			
			//model.addAttribute("error_msg", CommonData.RECORD_ERROR);
			
			
		}
		try {
			
			topicCatService.save(tcp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			
			//model.addAttribute("error_msg", CommonData.RECORD_ERROR);
			
			
		}

		topic = topicService.findById(topicId);
		tcp= tcmRepository.findById(topicCatId).get();
		model.addAttribute("topic",topic);
		model.addAttribute("topicCatMap",tcp);

		model.addAttribute("success_msg", CommonData.RECORD_SAVE_SUCCESS_MSG);

		return "updateTopic";  //  accomodate view part

	}


	/************************************END**********************************************/

	/************************************ADD ROLE**********************************************/

	/**
	 * redirect to add role page
	 * @param model Model object
	 * @param principal principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/addRole",method = RequestMethod.GET)
	public String addRoleGet(Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		List<Role> roles = roleService.findAll();

		model.addAttribute("roles", roles);

		return "addNewRole";

	}

	/**
	 * add role object into database
	 * @param model Model object
	 * @param principal principal object
	 * @param req HttpServletRequest
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/addRole",method = RequestMethod.POST)
	public String addRolePost(Model model,Principal principal,HttpServletRequest req) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		List<Role> roles = roleService.findAll();

		model.addAttribute("roles", roles);

		String roleName = req.getParameter("roleName");
		
		if(roleName == null) {

			model.addAttribute("error_msg", "Please Try Again");
			return "addNewRole";
		}

		if(roleService.findByname(roleName)!=null) {

//			model.addAttribute("msg1", true);
			model.addAttribute("error_msg", CommonData.RECORD_EXISTS);
			return "addNewRole";
		}

		Role newRole=new Role();
		newRole.setRoleId(roleService.getNewRoleId());
		newRole.setName(roleName);

		try {
			roleService.save(newRole);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			model.addAttribute("error_msg", CommonData.RECORD_ERROR);
			return "addNewRole";
		}

		roles = roleService.findAll();

		model.addAttribute("roles", roles);
		model.addAttribute("success_msg", CommonData.RECORD_SAVE_SUCCESS_MSG);
		return "addNewRole";

	}



	/************************************END**********************************************/

	/************************************ADD QUESTION**********************************************/

	/**
	 * redirects to upload question page
	 * @param model Model object
	 * @param principal principal object
	 * @return Strig object (webpage)
	 */
	@RequestMapping(value = "/uploadQuestion",method = RequestMethod.GET)
	public String addQuestionGet(Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		List<Question> questions = questService.findAll();
		model.addAttribute("questions", questions);

		List<Language> languages=lanService.getAllLanguages();

		List<Category> categories=catService.findAll();

		model.addAttribute("categories", categories);

		model.addAttribute("languages", languages);

		return "uploadQuestion";


	}

	/**
	 * upload question object into database
	 * @param model Model object
	 * @param principal principal object
	 * @param quesPdf MultipartFile
	 * @param categoryId int value
	 * @param topicId int value
	 * @param languageId int value
	 * @return string object
	 */
	@RequestMapping(value = "/uploadQuestion",method = RequestMethod.POST)
	public String addQuestionPost(Model model,Principal principal,
								  @RequestParam("questionName") MultipartFile quesPdf,
								  @RequestParam(value = "categoryName") int categoryId,
								  @RequestParam(name = "inputTopicName") int topicId,
								  @RequestParam(name = "languageyName") int languageId) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		List<Question> questionsTemp = questService.findAll();
		model.addAttribute("questions", questionsTemp);

		List<Language> languages=lanService.getAllLanguages();

		List<Category> categories=catService.findAll();

		model.addAttribute("categories", categories);

		model.addAttribute("languages", languages);

		if(!ServiceUtility.checkFileExtensiononeFilePDF(quesPdf)) {  // throw error

			model.addAttribute("error_msg",CommonData.RECORD_ERROR);
			return "uploadQuestion";
		}
		
		if(!ServiceUtility.checkScriptSlideProfileQuestion(quesPdf)) {
			
			model.addAttribute("error_msg","File Size must be less than 20MB");
			return "uploadQuestion";
		}

		Category cat=catService.findByid(categoryId);
		Topic topic=topicService.findById(topicId);
		
		if(cat == null) {  // throw error

			model.addAttribute("error_msg",CommonData.RECORD_ERROR);
			return "uploadQuestion";
		}
		
		if(topic == null) {  // throw error

			model.addAttribute("error_msg",CommonData.RECORD_ERROR);
			return "uploadQuestion";
		}
		
		TopicCategoryMapping topicCat=topicCatService.findAllByCategoryAndTopic(cat, topic);
		Language lan=lanService.getById(languageId);

		Question quesTemp = questService.getQuestionBasedOnTopicCatAndLan(topicCat, lan);

		if(quesTemp != null) {

			model.addAttribute("error_msg",CommonData.QUESTION_EXIST);
			return "uploadQuestion";
		}
		int newQuestionId=questService.getNewId();
		Question question=new Question();
		question.setQuestionId(newQuestionId);
		question.setDateAdded(ServiceUtility.getCurrentTime());
		question.setLan(lan);
		question.setQuestionPath("null");
		question.setTopicCatId(topicCat);
		question.setUser(usr);

		Set<Question> questions=new HashSet<Question>();
		questions.add(question);

		try {
			userService.addUserToQuestion(usr, questions);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			model.addAttribute("error_msg",CommonData.RECORD_ERROR);
			return "uploadQuestion";
		}

		try {
				ServiceUtility.createFolder(env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadDirectoryQuestion+newQuestionId);
				String pathtoUploadPoster=ServiceUtility.uploadFile(quesPdf, env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadDirectoryQuestion+newQuestionId);
				int indexToStart=pathtoUploadPoster.indexOf("Media");

				String document=pathtoUploadPoster.substring(indexToStart, pathtoUploadPoster.length());

				Question temp=questService.findById(newQuestionId);

				temp.setQuestionPath(document);

				questService.save(temp);




		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			model.addAttribute("error_msg",CommonData.RECORD_ERROR);
			return "uploadQuestion";
		}

		questionsTemp = questService.findAll();
		model.addAttribute("questions", questionsTemp);

		model.addAttribute("success_msg",CommonData.RECORD_SAVE_SUCCESS_MSG);

		return "uploadQuestion";


	}

	/**
	 * redirect to edit question page given id
	 * @param id int value
	 * @param model Model object
	 * @param principal principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/question/edit/{catName}/{topicName}/{language}", method = RequestMethod.GET)
	public String editQuestionGet(@PathVariable(name = "catName") String cat,
			@PathVariable (name = "topicName") String topic,
			@PathVariable (name = "language") String lan,Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
		
		Category catName = catService.findBycategoryname(cat);
		Topic topicName = topicService.findBytopicName(topic);
		Language lanName = lanService.getByLanName(lan);
		TopicCategoryMapping topicCatMap = topicCatService.findAllByCategoryAndTopic(catName, topicName);
		
		Question ques = questService.getQuestionBasedOnTopicCatAndLan(topicCatMap, lanName);
		
		if(catName == null || topicName == null || lanName == null || topicCatMap == null || ques == null) {
			return "redirect:/uploadQuestion";
		}

		
		model.addAttribute("question",ques);

		return "updateQuestion"; // question edit page
	}

	/**
	 * update question object
	 * @param req HttpServletRequest
	 * @param model Model object
	 * @param principal principal object
	 * @param quesPdf MultipartFile object
	 * @return String object
	 */
	@RequestMapping(value = "/updateQuestion",method = RequestMethod.POST)
	public String updateQuestionPost(HttpServletRequest req,Model model,Principal principal,
								  @RequestParam("questionName") MultipartFile quesPdf) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		String quesIdInString=req.getParameter("id");
		int idQues =  Integer.parseInt(quesIdInString);
		Question ques = questService.findById(idQues);
		
		if(ques == null) {  // throw error

			model.addAttribute("error_msg",CommonData.RECORD_ERROR);
			model.addAttribute("question",ques);
			return "updateQuestion"; // accomodate error
		}

		if(!ServiceUtility.checkFileExtensiononeFilePDF(quesPdf)) {  // throw error

			model.addAttribute("error_msg",CommonData.RECORD_ERROR);
			model.addAttribute("question",ques);
			return "updateQuestion"; // accomodate error
		}

		if(!ServiceUtility.checkScriptSlideProfileQuestion(quesPdf)) {
			
			model.addAttribute("error_msg","File Size must be less than 20MB");
			model.addAttribute("question",ques);
			return "updateQuestion";
		}

		try {

				String pathtoUploadPoster=ServiceUtility.uploadFile(quesPdf, env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadDirectoryQuestion+ques.getQuestionId());
				int indexToStart=pathtoUploadPoster.indexOf("Media");

				String document=pathtoUploadPoster.substring(indexToStart, pathtoUploadPoster.length());

				ques.setQuestionPath(document);

				questService.save(ques);



		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			model.addAttribute("error_msg",CommonData.RECORD_ERROR);
			model.addAttribute("question",ques);
			return "updateQuestion";   // accomodate view part
		}

		ques = questService.findById(idQues);
		model.addAttribute("question",ques);

		model.addAttribute("success_msg",CommonData.RECORD_SAVE_SUCCESS_MSG);

		return "updateQuestion";     // accomodate view part


	}

	/************************************END**********************************************/

	/************************************ADD CONSULTANT**********************************************/

	/**
	 *  redirect to add consultant page
	 * @param model Model object
	 * @param principal principal object
	 * @return String object (Webpage)
	 */ 
	@RequestMapping(value = "/addConsultant",method = RequestMethod.GET)
	public String addConsultantGet(Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		List<Consultant> consultants = consultService.findAll();
		List<Category> cat=catService.findAll();
		List<Language> lans=lanService.getAllLanguages();

		model.addAttribute("categories", cat);
		model.addAttribute("consultants", consultants);
		model.addAttribute("languages", lans);

		return "addConsultant";

	}

	/**
	 * Add consultant to the system
	 * @param model Model object
	 * @param principal principal object
	 * @param name String object
	 * @param catId int value
	 * @param lanId int value
	 * @param email String object
	 * @return String object
	 */
	@RequestMapping(value = "/addConsultant",method = RequestMethod.POST)
	public String addConsultantPost(Model model,Principal principal,
									@RequestParam("nameConsaltant") String name,
									@RequestParam("lastname") String lastname,
									@RequestParam("categoryName") int catId,
									@RequestParam("lanName") int lanId,
									@RequestParam("email") String email,
									@RequestParam("desc") String desc,
									@RequestParam("photo") MultipartFile photo){

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		List<Consultant> consultants = consultService.findAll();
		List<Category> cat=catService.findAll();
		List<Language> lans=lanService.getAllLanguages();

		model.addAttribute("categories", cat);
		model.addAttribute("consultants", consultants);
		model.addAttribute("languages", lans);

		if(!ServiceUtility.checkEmailValidity(email)) {  // throw email wromng error

			model.addAttribute("error_msg", CommonData.NOT_VALID_EMAIL_ERROR);
			return "addConsultant";
		}

		Category cats = catService.findByid(catId);
		Language lan =lanService.getById(lanId);
		
		if(cats == null){  // throw email wromng error

			model.addAttribute("error_msg", "Cat is null, Please Try Again");
			return "addConsultant";
		}
		
		if(lan == null) {  // throw email wromng error

			model.addAttribute("error_msg", " Language is null, Please Try Again");
			return "addConsultant";
		}
		
		if(!photo.isEmpty()) {
			if(!ServiceUtility.checkFileExtensionImage(photo) ) {
				model.addAttribute("error_msg",CommonData.VIDEO_CONSENT_FILE_EXTENSION_ERROR);
				return "addConsultant";
			}
			
		}

		boolean flagforExistingUser=false;
		User userTemp = userService.findByUsername(email);
		
		if(userTemp == null) {
			userTemp = new User();
			userTemp.setId(userService.getNewId());
			userTemp.setFirstName(name);
			userTemp.setLastName(lastname);
			userTemp.setEmail(email);
			userTemp.setUsername(email);
			userTemp.setDateAdded(ServiceUtility.getCurrentTime());
			userTemp.setPassword(SecurityUtility.passwordEncoder().encode(CommonData.COMMON_PASSWORD));
			userTemp.setEmailVerificationCode("");
		} else {
			if(consultService.findByUser(userTemp)!=null) {
				model.addAttribute("error_msg", " Email is already assigned to consultant");
				return "addConsultant";
			}
				
			flagforExistingUser=true;
		}
		
		int newConsultid=consultService.getNewConsultantId();
		Consultant consultant=new Consultant();
		consultant.setConsultantId(newConsultid);
		consultant.setDescription(desc);
		consultant.setDateAdded(ServiceUtility.getCurrentTime());
		try {
			if(!photo.isEmpty()) {
				String photoFolder = env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadDirectoryConsultant+newConsultid;
				ServiceUtility.createFolder(photoFolder);
				String pathtoUploadPhoto = ServiceUtility.uploadFile(photo, photoFolder);
				int indexToStart=pathtoUploadPhoto.indexOf("Media");
				String cons_photo=pathtoUploadPhoto.substring(indexToStart, pathtoUploadPhoto.length());
				userTemp.setProfilePic(cons_photo);
			}
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		userService.save(userTemp);
		
		consultant.setUser(userTemp);
		
		Set<Consultant> consults=new HashSet<Consultant>();
		consults.add(consultant);
		
		try {
			
			userService.addUserToConsultant(usr, consults);
			Role role = roleService.findByname(CommonData.domainReviewerRole);
			
			UserRole usrRole= new UserRole();
			usrRole.setUserRoleId(usrRoleService.getNewUsrRoletId());
			usrRole.setCat(cats);
			usrRole.setLan(lan);
			usrRole.setUser(userTemp);
			usrRole.setRole(role);
			usrRole.setStatus(true);
			usrRole.setCreated(ServiceUtility.getCurrentTime());

			usrRoleService.save(usrRole);
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			model.addAttribute("error_msg",CommonData.RECORD_ERROR);
			System.out.println("Problem 1");
			return "addConsultant";    // throw a error
		}
		
		if(flagforExistingUser==true) {
			SimpleMailMessage msg1 = mailConstructor.domainRoleMailSendforExistingUser(userTemp);
			mailSender.send(msg1);
		} else {
			SimpleMailMessage msg2 = mailConstructor.domainRoleMailSend(userTemp);
			mailSender.send(msg2);
		}
		
		consultants = consultService.findAll();
		model.addAttribute("consultants", consultants);
		model.addAttribute("success_msg",CommonData.RECORD_SAVE_SUCCESS_MSG);
		return "addConsultant";
	}

	@RequestMapping(value = "/consultant/edit/{id}", method = RequestMethod.GET)
	public String editConsultant(@PathVariable int id,Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
		
		Consultant consultant = consultService.findById(id);
//		Testimonial test=testService.findById(id);
		
		if(consultant == null) {

			return "redirect:/addConsultant";
		}

//		if(consultant.getUser().getId() != usr.getId()) {
//
//			return "redirect:/addConsultant";
//		}

		model.addAttribute("consultant", consultant);

		return "updateConsultant";
	}

	@RequestMapping(value = "/updateConsultant", method = RequestMethod.POST)
	public String updateConnsultant(HttpServletRequest req,Model model,Principal principal,@RequestParam("photo") MultipartFile file) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
		String consultant_id=req.getParameter("consultant_id");
		String name=req.getParameter("name");
		String lastname=req.getParameter("lastname");
		String desc=req.getParameter("desc");

		Consultant consultant=consultService.findById(Integer.parseInt(consultant_id));

		if(consultant==null) {
			// accommodate error message
			model.addAttribute("error_msg", CommonData.CONSULTANT_ERROR);
			return "addConsultant";
		}

		if(!file.isEmpty()) {
		try {

			String pathSampleVideo = null;;
			try {
				pathSampleVideo = ServiceUtility.uploadVideoFile(file, env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadDirectoryConsultant);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			    ServiceUtility.createFolder(env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadDirectoryConsultant+consultant.getConsultantId());
				String pathtoUploadPoster=ServiceUtility.uploadVideoFile(file, env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadDirectoryConsultant+consultant.getConsultantId());
				int indexToStart=pathtoUploadPoster.indexOf("Media");

				String document=pathtoUploadPoster.substring(indexToStart, pathtoUploadPoster.length());
				consultant.getUser().setFirstName(name);
				consultant.getUser().setLastName(lastname);
				consultant.setDescription(desc);
				consultant.getUser().setProfilePic(document);
				consultService.save(consultant);

				


		}catch (Exception e) {
			// TODO: handle exception

			e.printStackTrace();
			model.addAttribute("error_msg", CommonData.RECORD_ERROR);
			model.addAttribute("consultant", consultant);
			return "updateConsultant";    // throw a error
		}
		}else {

			consultant.getUser().setFirstName(name);
			consultant.getUser().setLastName(lastname);
			consultant.setDescription(desc);
			
			consultService.save(consultant);		}
			model.addAttribute("consultant", consultant);

		model.addAttribute("success_msg", CommonData.RECORD_SAVE_SUCCESS_MSG);

		return "updateConsultant";
	}

	/************************************END**********************************************/

	/************************************ADD EVENT**********************************************/
	/**
	 * redirect to add page
	 * @param model Model object
	 * @param principal principal object
	 * @return String object
	 */
	@RequestMapping(value = "/addEvent",method = RequestMethod.GET)
	public String addEventGet(Model model,Principal principal) {
		User usr=new User();
		if(principal!=null) {
			usr=userService.findByUsername(principal.getName());
		}
		model.addAttribute("userInfo", usr);
		//List<Event> events = eventservice.findByUser(usr);
		List<Event> events = eventservice.findAll();
		model.addAttribute("events", events);
		List<State> states = stateService.findAll();
		model.addAttribute("states", states);
		List<Category> categories = catService.findAll();
		model.addAttribute("categories", categories);
		List<Language> lans = lanService.getAllLanguages();
		model.addAttribute("lans", lans);

		return "addEvent";
	}

	/**
	 *  Add Event object into database
	 * @param model Model object
	 * @param principal principal object
	 * @param req  HttpServletRequest object
	 * @param files MultipartFile
	 * @param topicId list of integer value
	 * @param catName int value
	 * @return String (webpage)
	 */
	@RequestMapping(value = "/addEvent",method = RequestMethod.POST)
	public String addEventPost(Model model,Principal principal,HttpServletRequest req,
						@RequestParam("Image") MultipartFile files,@RequestParam(value="inputTopic") int[] topicId,
						@RequestParam(value="categoryName") int catName) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		List<Event> eventsTemp = eventservice.findAll();
		model.addAttribute("events", eventsTemp);
		
		List<State> states = stateService.findAll();
		model.addAttribute("states", states);
		List<Category> categories = catService.findAll();
		model.addAttribute("categories", categories);
		List<Language> lans = lanService.getAllLanguages();
		model.addAttribute("lans", lans);

		String eventName = req.getParameter("eventname");
		String desc = req.getParameter("description");
		String venueName = req.getParameter("venuename");
		String contactPerson = req.getParameter("contactperson");
		String contactNumber = req.getParameter("contactnumber");
		String email = req.getParameter("email");
		String startDateTemp=req.getParameter("date");
		String endDateTemp=req.getParameter("endDate");

		String pinCode = req.getParameter("pinCode");
		String stateName = req.getParameter("stateName");
		String districtName = req.getParameter("districtName");
		String cityName = req.getParameter("cityName");
		String addressInformationName = req.getParameter("addressInformationName");
		String language = req.getParameter("language");

		Date startDate;
		Date endDate;
		int newEventid;
		
		if(stateService.findById(Integer.parseInt(stateName))==null){
			model.addAttribute("error_msg", "Please Select State");
			return "addEvent";
		}
		
		if(districtService.findById(Integer.parseInt(districtName))==null){
			model.addAttribute("error_msg", "Please Select District");
			return "addEvent";
		}
		
		
		
		if(lanService.getByLanName(language)==null){
			model.addAttribute("error_msg", "Please Select language");
			return "addEvent";
		}
		
		if(catService.findByid(catName)==null){
			model.addAttribute("error_msg", "Please Select Category");
			return "addEvent";
		}

		try {
			startDate=ServiceUtility.convertStringToDate(startDateTemp);
			endDate=ServiceUtility.convertStringToDate(endDateTemp);
			if(!files.isEmpty()) {
				if(!ServiceUtility.checkFileExtensionImage(files)) {
					model.addAttribute("error_msg", CommonData.JPG_PNG_EXT);
					return "addEvent";
				}
			}
			

			if(endDate.before(startDate)) {      // throws error if end date is previous to start date
				model.addAttribute("error_msg",CommonData.EVENT_CHECK_DATE);
				return "addEvent";
			}
			if(!email.isEmpty()) {
				if(!ServiceUtility.checkEmailValidity(email)) { // throw error on wrong email
					model.addAttribute("error_msg",CommonData.EVENT_CHECK_EMAIL);
					return "addEvent";
				}
			}
			
			if(!contactNumber.isEmpty()) {
				if(contactNumber.length() != 10) {        // throw error on wrong phone number
					model.addAttribute("error_msg",CommonData.EVENT_CHECK_CONTACT);
					return "addEvent";
				}
			}
			
			long contact=0;
			if(!contactNumber.isEmpty()) {
				contact=Long.parseLong(contactNumber);
			}
			

			newEventid=eventservice.getNewEventId();
			Event event=new Event();

			
				ServiceUtility.createFolder(env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadDirectoryEvent+newEventid);
				if(!files.isEmpty()) {
					String pathtoUploadPoster=ServiceUtility.uploadFile(files, env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadDirectoryEvent+newEventid);
					int indexToStart=pathtoUploadPoster.indexOf("Media");
					String document=pathtoUploadPoster.substring(indexToStart, pathtoUploadPoster.length());
					event.setPosterPath(document);
				}
				
			
			
			event.setEventId(newEventid);
			event.setContactPerson(contactPerson);
			event.setDateAdded(ServiceUtility.getCurrentTime());
			event.setEmail(email);
			event.setDescription(desc);
			event.setEndDate(endDate);
			event.setStartDate(startDate);
			event.setUser(usr);
			event.setContactNumber(contact);
			event.setEventName(eventName);
			event.setLocation(venueName);
			event.setAddress(addressInformationName);
			event.setState(stateService.findById(Integer.parseInt(stateName)));
			event.setDistrict(districtService.findById(Integer.parseInt(districtName)));
			if(cityService.findById(Integer.parseInt(cityName))!=null) {
				event.setCity(cityService.findById(Integer.parseInt(cityName)));
			}
			
			event.setPincode(Integer.parseInt(pinCode));
			event.setLan(lanService.getByLanName(language));
			Set<TrainingTopic> trainingTopicTemp = new HashSet<>();
			Category cat=catService.findByid(catName);

			try {
//				trainingInfoService.save(trainingData);
				int trainingTopicId=trainingTopicServ.getNewId();
				for(int topicID : topicId) {
					Topic topicTemp=topicService.findById(topicID);
					TopicCategoryMapping topicCatMap=topicCatService.findAllByCategoryAndTopic(cat, topicTemp);
					TrainingTopic trainingTemp=new TrainingTopic(trainingTopicId++, topicCatMap, event);
					trainingTopicTemp.add(trainingTemp);

				}

				event.setTrainingTopicId(trainingTopicTemp);

			Set<Event> events=new HashSet<Event>();
			events.add(event);

			userService.addUserToEvent(usr, events);


		}catch (Exception e){

		}
		}catch (Exception e) {
			// TODO: handle exception
			model.addAttribute("error_msg",CommonData.RECORD_ERROR);
			e.printStackTrace();
			return "addEvent";
		}finally {

		}

		eventsTemp = eventservice.findAll();
		model.addAttribute("events", eventsTemp);
		model.addAttribute("success_msg",CommonData.RECORD_SAVE_SUCCESS_MSG);
		return "addEvent";


	}


	/************************************END**********************************************/

	/************************************ADD TESTIMONIAL**********************************************/

	/**
	 * redirects page to add testimonial page
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object(webpage)
	 */
	@RequestMapping(value = "/addTestimonial",method = RequestMethod.GET)
	public String addTestimonialGet(Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		List<Testimonial> testimonials = testService.findAll();
		List<TrainingInformation> trainings = trainingInfoService.findAll();
		List<Event> events = eventservice.findAll();
		model.addAttribute("testimonials", testimonials);
		model.addAttribute("trainings", trainings);
		model.addAttribute("events", events);

		return "addTestimonial";


	}

	/**
	 * Add testimonial into database
	 * @param model Model object
	 * @param principal Principal object
	 * @param file MultipartFile
	 * @param consent MultipartFile
	 * @param name String 
	 * @param desc String
	 * @param trainingId String
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/addTestimonial",method = RequestMethod.POST)
	public String addTestimonialPost(Model model,Principal principal,
									@RequestParam("uploadTestimonial") MultipartFile file,
									@RequestParam("consent") MultipartFile consent,
									@RequestParam("testimonialName") String name,
									@RequestParam("description") String desc,
									@RequestParam(value ="trainingName", required = false ) String trainingId) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		List<Testimonial> testimonials = testService.findAll();
		List<TrainingInformation> trainings = trainingInfoService.findAll();
		model.addAttribute("testimonials", testimonials);
		model.addAttribute("trainings", trainings);
		
		if(!consent.isEmpty()) {
			if(!ServiceUtility.checkFileExtensionImage(consent) && !ServiceUtility.checkFileExtensiononeFilePDF(consent)) {
				model.addAttribute("error_msg",CommonData.VIDEO_CONSENT_FILE_EXTENSION_ERROR);
				return "addTestimonial";
			}
		}
		

		if(!file.isEmpty()) {
		if(!ServiceUtility.checkFileExtensionVideo(file)) { // throw error on extension
			model.addAttribute("error_msg",CommonData.VIDEO_FILE_EXTENSION_ERROR);
			return "addTestimonial";
		}
		
		if(!ServiceUtility.checkVideoSizeTestimonial(file)) {
			model.addAttribute("error_msg","File size must be less than 20MB");
			return "addTestimonial";
		}

		String pathSampleVideo = null;;
		try {
			pathSampleVideo = ServiceUtility.uploadVideoFile(file, env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadDirectoryTestimonial);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		IContainer container = IContainer.make();
		int result=10;
		result = container.open(pathSampleVideo,IContainer.Type.READ,null);

		try {
			if(result<0) {

				model.addAttribute("error_msg",CommonData.RECORD_ERROR);
				return "addTestimonial";

			}else {
					if(container.getDuration()>CommonData.videoDuration) {

						model.addAttribute("error_msg",CommonData.VIDEO_DURATION_ERROR);
						Path deletePreviousPath=Paths.get(pathSampleVideo);
						Files.delete(deletePreviousPath);
						return "addTestimonial";
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			model.addAttribute("error_msg",CommonData.RECORD_ERROR);
			return "addTestimonial";
		}

		int newTestiId=testService.getNewTestimonialId();
		Testimonial test=new Testimonial();
		test.setDateAdded(ServiceUtility.getCurrentTime());
		test.setDescription(desc);
		test.setName(name);
		test.setUser(usr);
		test.setTestimonialId(newTestiId);
		test.setFilePath("null");

		if(trainingId != null) {
			TrainingInformation train = trainingInfoService.getById(Integer.parseInt(trainingId));
			test.setTraineeInfos(train);
			test.setApproved(false);
		}

		Set<Testimonial> testi=new HashSet<Testimonial>();
		testi.add(test);

		try {
			userService.addUserToTestimonial(usr, testi);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			model.addAttribute("error_msg",CommonData.RECORD_ERROR);
			return "addTestimonial";
		}

		try {
				ServiceUtility.createFolder(env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadDirectoryTestimonial+newTestiId);
				String pathtoUploadPoster=ServiceUtility.uploadVideoFile(file, env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadDirectoryTestimonial+newTestiId);
				int indexToStart=pathtoUploadPoster.indexOf("Media");

				String document=pathtoUploadPoster.substring(indexToStart, pathtoUploadPoster.length());

				Testimonial temp=testService.findById(newTestiId);

				temp.setFilePath(document);
				
				if(!consent.isEmpty()) {
					pathtoUploadPoster=ServiceUtility.uploadFile(consent, env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadDirectoryTestimonial+newTestiId);
					indexToStart=pathtoUploadPoster.indexOf("Media");

					document=pathtoUploadPoster.substring(indexToStart, pathtoUploadPoster.length());
					
					temp.setConsentLetter(document);
				}
				

				testService.save(temp);



		}catch (Exception e) {
			// TODO: handle exception

			e.printStackTrace();
			model.addAttribute("error_msg",CommonData.RECORD_ERROR);
			return "addTestimonial";    // throw a error
		}
		}else {

			Testimonial test=new Testimonial();
			test.setDateAdded(ServiceUtility.getCurrentTime());
			test.setDescription(desc);
			test.setName(name);
			test.setUser(usr);
			test.setTestimonialId(testService.getNewTestimonialId());
			test.setFilePath("null");

			if(trainingId != null) {
				TrainingInformation train = trainingInfoService.getById(Integer.parseInt(trainingId));
				test.setTraineeInfos(train);
				test.setApproved(false);
			}

			Set<Testimonial> testi=new HashSet<Testimonial>();
			testi.add(test);

			userService.addUserToTestimonial(usr, testi);
			
			try {
				ServiceUtility.createFolder(env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadDirectoryTestimonial+test.getTestimonialId());
				if(!consent.isEmpty()) {
					String pathtoUploadPoster=ServiceUtility.uploadFile(consent, env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadDirectoryTestimonial+test.getTestimonialId());
					int indexToStart=pathtoUploadPoster.indexOf("Media");

					String document=pathtoUploadPoster.substring(indexToStart, pathtoUploadPoster.length());
					
					test.setConsentLetter(document);
				}
				

				testService.save(test);


		}catch (Exception e) {
			// TODO: handle exception

			e.printStackTrace();
			model.addAttribute("error_msg",CommonData.RECORD_ERROR);
			return "addTestimonial";    // throw a error
		}
			
			
		}

		testimonials = testService.findAll();
		model.addAttribute("testimonials", testimonials);
		model.addAttribute("success_msg",CommonData.RECORD_SAVE_SUCCESS_MSG);
		return "addTestimonial";


	}

	/************************************END**********************************************/

	/************************************UPDATE SECTION AND VIEW OF CATEGORY**********************************************/

	/**
	 * redirects to add category page
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/category", method = RequestMethod.GET)
	public String viewCategoryGet(Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		List<Category> cat=catService.findAll();

		model.addAttribute("categories", cat);

		return "category";
	}

	/**
	 * redirects to edit category page
	 * @param id int value
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/category/edit/{catName}", method = RequestMethod.GET)
	public String editCategoryGet(@PathVariable(name = "catName") String catName,Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		Category cat=catService.findBycategoryname(catName);
		
		if(cat == null) {
			return "redirect:/category";
		}

		model.addAttribute("category",cat);

		return "updateCategory";
	}

	/**
	 * update category object
	 * @param model Model object
	 * @param principal Principal object
	 * @param req HttpServletRequest object
	 * @param file MultipartFile object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/updateCategory", method = RequestMethod.POST)
	public String updateCategoryGet(Model model,Principal principal,HttpServletRequest req,
			@RequestParam("categoryImage") MultipartFile file) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		String catId=req.getParameter("id");
		String catName=req.getParameter("categoryname");
		String categoryDesc = req.getParameter("categoryDesc");


		Category cat=catService.findByid(Integer.parseInt(catId));

		if(cat==null) {
			 // accommodate  error message
			model.addAttribute("category",cat);
			model.addAttribute("error_msg","Category doesn't exist");
			return "updateCategory";
		}

		List<Category> cats=catService.findAll();
		for(Category x : cats) {
			if(x.getCategoryId()!=cat.getCategoryId()) {
				if(catName.equalsIgnoreCase(x.getCatName())) {
					// accommodate  error message
					model.addAttribute("category",cat);
					model.addAttribute("error_msg","Category Name Already Exist");
					return "updateCategory";
				}
				}
		}

		cat.setCatName(catName);
		cat.setDescription(categoryDesc);

		if(!file.isEmpty()) {
			try {
					ServiceUtility.createFolder(env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadDirectoryCategory+cat.getCategoryId());
					String pathtoUploadPoster=ServiceUtility.uploadFile(file, env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadDirectoryCategory+cat.getCategoryId());

					int indexToStart=pathtoUploadPoster.indexOf("Media");

					String document=pathtoUploadPoster.substring(indexToStart, pathtoUploadPoster.length());

					cat.setPosterPath(document);

					catService.save(cat);


			}catch (Exception e) {
				// TODO: handle exception

				e.printStackTrace();
				model.addAttribute("category",cat);
				model.addAttribute("error_msg",CommonData.RECORD_ERROR);
				return "updateCategory";  // throw a error
			}
			}else {

				catService.save(cat);
			}

		cat=catService.findByid(Integer.parseInt(catId));
		model.addAttribute("category",cat);

		model.addAttribute("success_msg",CommonData.RECORD_UPDATE_SUCCESS_MSG);   // need to accommodate

		return "updateCategory";
	}


	/************************************END**********************************************/

	/************************************UPDATE AND VIEW SECTION OF EVENT**********************************************/

	/**
	 * redirects to event details in homepage
	 * @param id int value
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/eventDetails/{id}", method = RequestMethod.GET)
	public String eventGet(@PathVariable int id,Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		Event event= eventservice.findById(id);
		
		if(event == null) {
			return "redirect:/event";
		}
		
		model.addAttribute("event", event);

		return "event";
	}

	/**
	 * redirects to add event page
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/event", method = RequestMethod.GET)
	public String viewEventGet(Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		List<Event> event=eventservice.findAll();
		model.addAttribute("events", event);

		return "event";
	}

	/**
	 * redirects to edit event page given id
	 * @param id int value
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/event/edit/{id}", method = RequestMethod.GET)
	public String editEventGet(@PathVariable int id,Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		Event event= eventservice.findById(id);
		
		if(event == null) {

			return "redirect:/addEvent";
		}

		/*if(event.getUser().getId() != usr.getId()) {

			return "redirect:/addEvent";
		}*/
		
		model.addAttribute("events", event);

		return "updateEvent";
	}

	/**
	 * update event object 
	 * @param req HttpServletRequest object
	 * @param model Model object
	 * @param principal Principal object
	 * @param files MultipartFile object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/updateEvent", method = RequestMethod.POST)
	public String updateEventGet(HttpServletRequest req,Model model,Principal principal,
			@RequestParam("Image") MultipartFile files) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		String eventId=req.getParameter("eventId");
		String eventName = req.getParameter("eventname");
		String desc = req.getParameter("description");
		String venueName = req.getParameter("venuename");
		String contactPerson = req.getParameter("contactperson");
		String contactNumber = req.getParameter("contactnumber");
		String email = req.getParameter("email");
		String startDateTemp=req.getParameter("date");
		String endDateTemp=req.getParameter("endDate");
		Date startDate;
		Date endDate;

		Event event= eventservice.findById(Integer.parseInt(eventId));

		model.addAttribute("events", event);

		if(event==null) {
			model.addAttribute("error_msg","Event doesn't exist");
			return "updateEvent";
		}

		try {
			startDate=ServiceUtility.convertStringToDate(startDateTemp);
			endDate=ServiceUtility.convertStringToDate(endDateTemp);

			if(endDate.before(startDate)) {      // throws error if end date is previous to start date

				model.addAttribute("error_msg","End date must be after Start date");
				return "updateEvent";
			}
			/*if(!contactNumber.isEmpty()) {
				if(contactNumber.length() != 10) {        // throw error on wrong phone number

					model.addAttribute("error_msg","Contact number must be 10 digit");
					return "updateEvent";
				}
			}
			*/
			if(!email.isEmpty()) {
				if(!ServiceUtility.checkEmailValidity(email)) {    // throw error on wrong email

					model.addAttribute("error_msg",CommonData.NOT_VALID_EMAIL_ERROR);
					return "updateEvent";
				}
			}

			

			if(!files.isEmpty()) {
				if(!ServiceUtility.checkFileExtensionImage(files)) { // throw error on extension
					model.addAttribute("error_msg",CommonData.JPG_PNG_EXT);
					return "updateEvent";
			}
			}
			long contact=0;
			if(!contactNumber.isEmpty()) {
				contact=Long.parseLong(contactNumber);
			}
			

			event.setContactPerson(contactPerson);
			event.setEmail(email);
			event.setDescription(desc);
			event.setEndDate(endDate);
			event.setStartDate(startDate);
			event.setContactNumber(contact);
			event.setEventName(eventName);
			event.setLocation(venueName);

			if(!files.isEmpty()) {
				String pathtoUploadPoster=ServiceUtility.uploadFile(files, env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadDirectoryEvent+event.getEventId());
				int indexToStart=pathtoUploadPoster.indexOf("Media");

				String document=pathtoUploadPoster.substring(indexToStart, pathtoUploadPoster.length());

				event.setPosterPath(document);

			}

			eventservice.save(event);

		}catch (Exception e) {
			// TODO: handle exception
			model.addAttribute("error_msg",CommonData.RECORD_ERROR);
			model.addAttribute("events", event);
			return "updateEvent";        // need to add some error message
		}


		model.addAttribute("success_msg",CommonData.RECORD_UPDATE_SUCCESS_MSG);
		model.addAttribute("events", event);

		return "updateEvent";
	}


	/************************************END**********************************************/
	
	
	
	
	/************************************Edit Section of PromoVideo **********************/
	
	@RequestMapping(value = "/promoVideo/edit/{id}", method = RequestMethod.GET)
	public String promoVideoGet(@PathVariable int id,Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		//Event event= eventservice.findById(id);
		PromoVideo promoVideo=promoVideoService.findById(id);
		
		
		if(promoVideo == null) {

			return "redirect:/addPromoVideo";
		}
		
		List<Language> languages=lanService.getAllLanguages();
		model.addAttribute("languages", languages);
	
		List<PathofPromoVideo> pathofPromoVideoList= pathofPromoVideoService.findByPromoVideo(promoVideo);
		
		model.addAttribute("pathofPromoVideoList", pathofPromoVideoList);
		model.addAttribute("promoVideo", promoVideo);
		
		List<PromoVideo> promoVideos = promoVideoService.findAll();
		
		model.addAttribute("promoVideos",promoVideos);
		
		return "updatePromoVideo";
	}

	
	
	
	@RequestMapping(value = "/updatePromoVideo", method = RequestMethod.POST)
	public String updatePromoVideoGet(HttpServletRequest req,Model model,Principal principal, @RequestParam(name = "languageName") List<Integer> languageIds,
			 @RequestParam("promoVideo") List<MultipartFile> promoVideoFiles) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
		
		
		String title= req.getParameter("title");
		String promoVideoId=req.getParameter("promoVideoId");
		int promoVideoIdInt= Integer.parseInt(promoVideoId);
		
		
		
		List<Language> languages=lanService.getAllLanguages();
		model.addAttribute("languages", languages);

		PromoVideo promoVideo= promoVideoService.findById(Integer.parseInt(promoVideoId));
		
		if(promoVideo==null) {
			model.addAttribute("error_msg","Brouchure doesn't exist");
			return "updatePromoVideo";
		}
		
		
		List<PathofPromoVideo> pathofPromoVideoList = pathofPromoVideoService.findByPromoVideo(promoVideo);
		model.addAttribute("pathofPromoVideoList", pathofPromoVideoList);
		model.addAttribute("promoVideo", promoVideo);
		
		
		if(title.isEmpty()){
			model.addAttribute("error_msg","Title doesn't exist with empty");
			return "updatePromoVideo";
		}
		
		
		boolean fileError=false;
		boolean duplicatLanguage =false;
		

		try {
			
			for(MultipartFile uniquefile: promoVideoFiles) {
				if(!uniquefile.isEmpty()) {
					
					if(!ServiceUtility.checkFileExtensionVideo(uniquefile)) { // throw error on extension
						model.addAttribute("error_msg",CommonData.VIDEO_FILE_EXTENSION_ERROR);
						return  "updatePromoVideo";
					}
					
					if(!ServiceUtility.checkVideoSizePromoVideo(uniquefile)) {
						model.addAttribute("error_msg","File size must be less than 1 GB");
						return "updatePromoVideo";
					}
					
					
					
				}
				
			}
			
					 
					String document1="";
					int newpathofPromoVideoId= pathofPromoVideoService.getNewId();
					List<PathofPromoVideo> pathofPromoVideoList1= new ArrayList<>();
					List<String> addedlanguages= new ArrayList<>();
					for(int i=0; i<languageIds.size(); i++) {
						document1="";
						
						if(languageIds.get(i)==0){
							break;
						}
						
						
						Language language = lanService.getById(languageIds.get(i));
						String langName=language.getLangName();	
						
						PathofPromoVideo pathofPromoVideo1 = pathofPromoVideoService.findByLanguageandPromoVideo(language, promoVideo);
						
					
					
					if(!promoVideoFiles.get(i).isEmpty()) {
						ServiceUtility.createFolder(env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadPromoVideo + promoVideoIdInt  + "/" + langName );
						String pathtoUploadPoster1=ServiceUtility.uploadVideoFile(promoVideoFiles.get(i), env.getProperty("spring.applicationexternalPath.name")+ CommonData.uploadPromoVideo + promoVideoIdInt + "/" + langName );
						int indexToStart1=pathtoUploadPoster1.indexOf("Media");
						document1=pathtoUploadPoster1.substring(indexToStart1, pathtoUploadPoster1.length());
					
					
					}
					
					
					for(String testlan: addedlanguages) {
						if(testlan==langName) {
							duplicatLanguage=true;
						}
					}
					addedlanguages.add(langName);
					
					if(pathofPromoVideo1 !=null) {
						pathofPromoVideo1.setLan(language);
						
						if(!promoVideoFiles.get(i).isEmpty()) {
							pathofPromoVideo1.setVideoPath(document1);
						}
						
						pathofPromoVideoService.save(pathofPromoVideo1);
						
						
					}
					
					else {
						
						if(!promoVideoFiles.get(i).isEmpty()) {
							pathofPromoVideoList1.add(new PathofPromoVideo(newpathofPromoVideoId, ServiceUtility.getCurrentTime(), document1,  promoVideo, language));
							newpathofPromoVideoId = newpathofPromoVideoId + 1;
							
						}
						else {
							 fileError=true;
						}
						
					}
					
					
					
				} 
					if(fileError==false && duplicatLanguage==false) {
					
					promoVideo.setTitle(title);
					promoVideoService.save(promoVideo);
					
					pathofPromoVideoService.saveAll(pathofPromoVideoList1);
				}
					
			

			} 
			
		   catch (Exception e) {
			// TODO: handle exception
			model.addAttribute("error_msg",CommonData.RECORD_ERROR);
			
			pathofPromoVideoList = pathofPromoVideoService.findByPromoVideo(promoVideo);
			model.addAttribute("pathofPromoVideoList", pathofPromoVideoList);
			model.addAttribute("promoVideo", promoVideo);
			
			return "updatePromoVideo";        // need to add some error message
		}
		
		if(fileError==true) {
			model.addAttribute("error_msg", "PromoVideo  file is required for new Language");
			return "updatePromoVideo"; 	
			
		}
		
		
		if(duplicatLanguage==true) {
			model.addAttribute("error_msg", "Duplicate Languages are not allowed");
			return "updatePromoVideo"; 	
			
		}
		
		

		model.addAttribute("success_msg",CommonData.RECORD_UPDATE_SUCCESS_MSG);
		pathofPromoVideoList = pathofPromoVideoService.findByPromoVideo(promoVideo);
		model.addAttribute("pathofPromoVideoList", pathofPromoVideoList);
		model.addAttribute("promoVideo", promoVideo);
		
		return "updatePromoVideo";
	}

	

	
	
	/*************************************END**********************************************/
	
	
	
	
	
	/***************************************Edit Section of Brochure*******************************************/

	/*
	 * Author: Alok Kumar
	 */

	@RequestMapping(value = "/brochure/edit/{id}", method = RequestMethod.GET)
	public String BrochureGet(@PathVariable int id,Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		//Event event= eventservice.findById(id);
		Brouchure brochure=broService.findById(id);
		
		
		if(brochure == null) {

			return "redirect:/addBrochure";
		}
		
		List<Language> languages=lanService.getAllLanguages();
		List<Category> categories=catService.findAll();
		model.addAttribute("categories", categories);
		model.addAttribute("languages", languages);
	
		Language langByBrochure= brochure.getLan();
	/* 
	    TopicCategoryMapping tcm=brouchure.getTopicCatId();
		Category catBrouchure=tcm.getCat();
		Topic topicBrouchure=tcm.getTopic();
		model.addAttribute("catBrouchure", catBrouchure);
		model.addAttribute("topicBrouchure", topicBrouchure);
		
	*/	
	
		Set<Version> verSet= brochure.getVersions();
		
		Version version= null;
		
		for(Version ver: verSet) {
			if(brochure.getPrimaryVersion()==ver.getBroVersion()) {
				version=ver;
				break;	
			}
		}
		
		List<Version> listofVersions= new ArrayList<>(verSet);
		
		Collections.sort(listofVersions, Version.SortByBroVersionTime);
		
		List<Version> newlistofVersion= new ArrayList<>();
		int temp=0;
		for(Version ver: listofVersions) {
			newlistofVersion.add(ver);
			temp++;
			if(temp==3) {
				break;
			}
				
		}
		
		List<FilesofBrouchure> newfilesList= new ArrayList<>();
		for(Version ver1: newlistofVersion) {
			for(Language lan: languages) {
				FilesofBrouchure filesBro= filesofbrouchureService.findByLanguageandVersion(lan, ver1);
				if(filesBro!=null) {
					newfilesList.add(filesBro);
				}
			}
		}
		
		model.addAttribute("newfilesList", newfilesList);
		
		System.out.println(version);
		List<FilesofBrouchure> filesOfBroList = filesofbrouchureService.findByVersion(version);
		System.out.println(filesOfBroList);
		model.addAttribute("filesOfBroList", filesOfBroList);
		model.addAttribute("listofVersions", newlistofVersion);
		model.addAttribute("version", version);
		model.addAttribute("brouchure", brochure);
		model.addAttribute("langByBrouchure",langByBrochure);
		
		List<Brouchure> brouchures = broService.findAll();
		List<Version> versions= new ArrayList<Version>();
		for(Brouchure bro: brouchures) {
			Version ver= verService.findByBrouchureAndPrimaryVersion(bro, bro.getPrimaryVersion());
			versions.add(ver);
		}
		Collections.sort(versions, Version.SortByBroVersionTime);
		for(Version ver: versions) {
			System.out.println(ver.getDateAdded());
		}
		model.addAttribute("brouchures", brouchures);
		model.addAttribute("versions", versions);
		
		
		return "updateBrochure";
	}

	
	/*
	 * Author: Alok Kumar
	 * 
	 */
	@RequestMapping(value = "/updateBrochure", method = RequestMethod.POST)
	public String updatBrochureGet(HttpServletRequest req,Model model,Principal principal, @RequestParam(name = "languageName") List<Integer> languageIds,
			@RequestParam("brouchure") List<MultipartFile> brochures, @RequestParam("brouchurePrint") List<MultipartFile> brochurePrints) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
		
		
		String title= req.getParameter("title");
		String brochureId=req.getParameter("brochureId");
		
		String overwrite=req.getParameter("overwrite");
		int overwriteValue=0;
		if(overwrite!=null)
			overwriteValue = Integer.parseInt(overwrite);
		/*
		String cat = req.getParameter("categoryName");
		String topic = req.getParameter("inputTopic");
		String lang = req.getParameter("languageyName");
		*/
		
		List<Language> languages=lanService.getAllLanguages();
		model.addAttribute("languages", languages);

		Brouchure brouchure= broService.findById(Integer.parseInt(brochureId));
		Version version=verRepository.findByBrouchureAndBroVersion(brouchure, brouchure.getPrimaryVersion());
		
		
		List<FilesofBrouchure> filesOfBroList = filesofbrouchureService.findByVersion(version);
		model.addAttribute("filesOfBroList", filesOfBroList);
		model.addAttribute("version", version);
		
		model.addAttribute("brouchure", brouchure);
		
		
		


		if(brouchure==null) {
			model.addAttribute("error_msg","Brouchure doesn't exist");
			return "updateBrochure";
		}
		
		Set<Version> verSet= brouchure.getVersions();
		List<Version> listofVersions= new ArrayList<>(verSet);
		Collections.sort(listofVersions, Version.SortByBroVersionTime);
		List<Version> newlistofVesrion= new ArrayList<>();
		int temp=0;
		for(Version ver: listofVersions) {
			newlistofVesrion.add(ver);
			temp++;
			if(temp==3)
				break;
		}
		
		List<FilesofBrouchure> newfilesList= new ArrayList<>();
		for(Version ver1: newlistofVesrion) {
			for(Language lan: languages) {
				FilesofBrouchure filesBro= filesofbrouchureService.findByLanguageandVersion(lan, ver1);
				if(filesBro!=null) {
					newfilesList.add(filesBro);
				}
			}
		}
		
		model.addAttribute("newfilesList", newfilesList);
		
		model.addAttribute("listofVersions", newlistofVesrion);
		
		if(title.isEmpty()){
			model.addAttribute("error_msg","Title doesn't exist with empty");
			return "updateBrochure";
		}
		
		
		int newVerId= verService.getNewId();
		int versionValue=brouchure.getPrimaryVersion();
		int newVersionValue=versionValue+1;
		boolean filesError=false;
		boolean duplicatLanguage =false;
		boolean duplicatelangforOverride= false;
		boolean webfileErrorforOverride= false;
		
		

		try {
			
			for(MultipartFile uniquefile: brochures) {
				if(!uniquefile.isEmpty()) {
					if(!ServiceUtility.checkFileExtensionImage(uniquefile) && !ServiceUtility.checkFileExtensiononeFilePDF(uniquefile)){  // throw error
						model.addAttribute("error_msg","Only image and pdf files are supported");
						return "addBrochure";
					}
				}
				
			}
			
			for(MultipartFile uniquePrintfile: brochurePrints) {
				if(!uniquePrintfile.isEmpty()) {
					if(!ServiceUtility.checkFileExtensionImage(uniquePrintfile) && !ServiceUtility.checkFileExtensiononeFilePDF(uniquePrintfile)){  // throw error
						model.addAttribute("error_msg","Only image and pdf files are supported");
						return "addBrochure";
					}
				}
			}

			
	
				if(overwriteValue !=0) {
					 
					String document1="";
					String printDocument="";
					int newbroFileId= filesofbrouchureService.getNewId();
					List<FilesofBrouchure> filesBroList= new ArrayList<>();
					List<String> addedlanguagesforOverride= new ArrayList<>();
					for(int i=0; i<languageIds.size(); i++) {
						document1="";
						printDocument="";
						
						if(languageIds.get(i)==0){
							break;
						}
						
						
						Language language = lanService.getById(languageIds.get(i));
						String langName=language.getLangName();	
						
						System.out.println(langName + " " + language + " " + version);

						FilesofBrouchure fileBro = filesofbrouchureService.findByLanguageandVersion(language, version);
						
					
					
					if(!brochures.get(i).isEmpty()) {
					ServiceUtility.createFolder(env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadBrouchure+ brochureId + "/" + versionValue + "/" + "web" + "/" + langName );
					String pathtoUploadPoster1=ServiceUtility.uploadFile(brochures.get(i), env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadBrouchure+ brochureId +"/" + versionValue + "/" + "web" + "/" + langName );
					int indexToStart1=pathtoUploadPoster1.indexOf("Media");
					document1=pathtoUploadPoster1.substring(indexToStart1, pathtoUploadPoster1.length());
					
					
					}
					
					if(!brochurePrints.get(i).isEmpty()) {
						ServiceUtility.createFolder(env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadBrouchure+brochureId+"/" + versionValue + "/" + "printdoc" + "/" + langName);
						String pathtoUploadPoster2=ServiceUtility.uploadFile(brochurePrints.get(i), env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadBrouchure+ brochureId +"/" +versionValue+ "/" + "printdoc" + "/" + langName);
						int indexToStart2=pathtoUploadPoster2.indexOf("Media");
						printDocument=pathtoUploadPoster2.substring(indexToStart2, pathtoUploadPoster2.length());
						
						
					}
					
					for(String testlan: addedlanguagesforOverride) {
						if(testlan==langName) {
							duplicatelangforOverride=true;
						}
					}
					addedlanguagesforOverride.add(langName);
					
					if(fileBro !=null) {
						fileBro.setLan(language);
						System.out.println(fileBro.getLan().getLangName() + "Alok Kumar Check BrochureFile");
						
						
						if(!brochures.get(i).isEmpty()) {
							fileBro.setWebPath(document1);
						}
						
						if(!brochurePrints.get(i).isEmpty()) {
							fileBro.setPrintPath(printDocument);
						}
						
						fileBro.setThumbnailPath(null);
						filesofbrouchureService.save(fileBro);
						//filesBroList.add(fileBro);
						
						
					}
					
					else {
						
						if(!brochures.get(i).isEmpty()) {
							filesBroList.add(new FilesofBrouchure(newbroFileId, ServiceUtility.getCurrentTime(), document1, printDocument,  version, language));
							newbroFileId = newbroFileId + 1;
							
						}
						else {
							 webfileErrorforOverride=true;
						}
						
					}
					
					
					
				} 
					if(webfileErrorforOverride==false && duplicatelangforOverride==false) {
					version.setVersionPosterPath(document1);
					version.setVersionPrintPosterPath(printDocument);
					verService.save(version);
					brouchure.setTitle(title);
					broService.save(brouchure);
					
					filesofbrouchureService.saveAll(filesBroList);
				}
					
					
					
					
				}
					
					else {
						
						String document2="";
						String printDocument2="";
						int newbroFileId= filesofbrouchureService.getNewId();
						List<FilesofBrouchure> filesBroList1= new ArrayList<>();
						Version newVer= new Version();
						List<String> addedLanguages= new ArrayList<>();
						for(int i=0; i<languageIds.size(); i++) {
							document2="";
							printDocument2="";
							
							if(languageIds.get(i)==0){
								break;
							}
							
							
							String langName=lanService.getById(languageIds.get(i)).getLangName();
						
						
						if(!brochures.get(i).isEmpty()) {
						
						ServiceUtility.createFolder(env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadBrouchure+ brochureId + "/" + newVersionValue + "/" + "web" + "/" + langName );
						String pathtoUploadPoster3=ServiceUtility.uploadFile(brochures.get(i), env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadBrouchure+ brochureId +"/" + newVersionValue + "/" + "web" + "/" + langName );
						int indexToStart3=pathtoUploadPoster3.indexOf("Media");
						document2=pathtoUploadPoster3.substring(indexToStart3, pathtoUploadPoster3.length());
						
						
						if(!brochurePrints.get(i).isEmpty()) {
						ServiceUtility.createFolder(env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadBrouchure+brochureId+"/" + newVersionValue+ "/" + "printdoc" + "/" + langName);
						String pathtoUploadPoster4=ServiceUtility.uploadFile(brochurePrints.get(i), env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadBrouchure+ brochureId +"/" + newVersionValue + "/" + "printdoc" + "/" + langName);	
						int indexToStart4=pathtoUploadPoster4.indexOf("Media");
						printDocument2=pathtoUploadPoster4.substring(indexToStart4, pathtoUploadPoster4.length());
						
						}
						 
						for(String testlan: addedLanguages) {
							if(testlan==langName) {
								duplicatLanguage=true;
							}
						}
						addedLanguages.add(langName);
						
						filesBroList1.add(new FilesofBrouchure(newbroFileId, ServiceUtility.getCurrentTime(), document2, printDocument2,  newVer, lanService.getById(languageIds.get(i))));
						newbroFileId +=1;
						
						}
						
						else {
							filesError=true;
						}
						
						}
						
						if(filesError==false && duplicatLanguage == false) {
							
							System.out.println("Alok Kumar test");
							
							brouchure.setTitle(title);
							brouchure.setPrimaryVersion(version.getBroVersion()+1);
							broService.save(brouchure);
							
							
							newVer.setVerId(newVerId);
							newVer.setBrouchure(brouchure);
							newVer.setDateAdded(ServiceUtility.getCurrentTime());
							newVer.setBroVersion(version.getBroVersion()+1);
							newVer.setVersionPosterPath(document2);
							newVer.setVersionPrintPosterPath(printDocument2);
							verService.save(newVer);
							
							filesofbrouchureService.saveAll(filesBroList1);
							
							brouchure= broService.findById(Integer.parseInt(brochureId));
							verSet= brouchure.getVersions();
							listofVersions= new ArrayList<>(verSet);
							model.addAttribute("listofVersions", listofVersions);
							
						}
						
						
						
					}

			
			

			} catch (Exception e) {
			// TODO: handle exception
			model.addAttribute("error_msg",CommonData.RECORD_ERROR);
			System.out.println("Check path error");
			
			
			version=verRepository.findByBrouchureAndBroVersion(brouchure, brouchure.getPrimaryVersion());
			filesOfBroList = filesofbrouchureService.findByVersion(version);
			model.addAttribute("filesOfBroList", filesOfBroList);
			model.addAttribute("version", version);
			model.addAttribute("brouchure", brouchure);
		
			verSet= brouchure.getVersions();
			listofVersions= new ArrayList<>(verSet);
			Collections.sort(listofVersions, Version.SortByBroVersionTime);
			newlistofVesrion= new ArrayList<>();
			temp=0;
			for(Version ver: listofVersions) {
				newlistofVesrion.add(ver);
				temp++;
				if(temp==3)
					break;
			}
			
			newfilesList= new ArrayList<>();
			for(Version ver1: newlistofVesrion) {
				for(Language lan: languages) {
					FilesofBrouchure filesBro= filesofbrouchureService.findByLanguageandVersion(lan, ver1);
					if(filesBro!=null) {
						newfilesList.add(filesBro);
					}
				}
			}
			
			model.addAttribute("newfilesList", newfilesList);
			
			model.addAttribute("listofVersions", newlistofVesrion);
			return "updateBrochure";        // need to add some error message
		}
		
		if(webfileErrorforOverride==true) {
			model.addAttribute("error_msg", "brouchure web file is required for new Language");
			return "updateBrochure"; 	
			
		}
		
		if(duplicatLanguage==true) {
			model.addAttribute("error_msg", "Duplicate Languages are not allowed");
			return "updateBrochure"; 	
			
		}
		
		if(duplicatelangforOverride==true) {
			model.addAttribute("error_msg", "Duplicate Languages are not allowed");
			return "updateBrochure"; 	
			
		}
		
		
		if(filesError==true) {
			model.addAttribute("error_msg", "brouchure web file is required for new version");
			return "updateBrochure"; 	
			
		}
		
		

		model.addAttribute("success_msg",CommonData.RECORD_UPDATE_SUCCESS_MSG);
		brouchure= broService.findById(Integer.parseInt(brochureId));
		version=verRepository.findByBrouchureAndBroVersion(brouchure, brouchure.getPrimaryVersion());
		filesOfBroList = filesofbrouchureService.findByVersion(version);
		model.addAttribute("filesOfBroList", filesOfBroList);
		model.addAttribute("version", version);
		model.addAttribute("brouchure", brouchure);
		verSet= brouchure.getVersions();
		listofVersions= new ArrayList<>(verSet);
		Collections.sort(listofVersions, Version.SortByBroVersionTime);
		newlistofVesrion= new ArrayList<>();
		temp=0;
		for(Version ver: listofVersions) {
			newlistofVesrion.add(ver);
			temp++;
			if(temp==3)
				break;
		}
		newfilesList= new ArrayList<>();
		for(Version ver1: newlistofVesrion) {
			for(Language lan: languages) {
				FilesofBrouchure filesBro= filesofbrouchureService.findByLanguageandVersion(lan, ver1);
				if(filesBro!=null) {
					newfilesList.add(filesBro);
				}
			}
		}
		
		model.addAttribute("newfilesList", newfilesList);
		
		model.addAttribute("listofVersions", newlistofVesrion);
		for(Version ver: listofVersions)
		System.out.println(ver);
		List<Brouchure> brouchures = broService.findAll();
		List<Version> versions= new ArrayList<Version>();
		for(Brouchure bro: brouchures) {
			Version ver= verService.findByBrouchureAndPrimaryVersion(bro, bro.getPrimaryVersion());
			versions.add(ver);
		}
		Collections.sort(versions, Version.SortByBroVersionTime);
		for(Version ver: versions) {
			System.out.println(ver.getDateAdded());
		}
		model.addAttribute("brouchures", brouchures);
		model.addAttribute("versions", versions);

		return "updateBrochure";
	}

	
	
	
	
	/************************************END******************************************************************/
	
	
	/******************************************Edit Section of Research Paper ********************************/
	
	@RequestMapping(value = "/researchPaper/edit/{id}", method = RequestMethod.GET)
	public String editResearchPaerGet(@PathVariable int id,Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		
		ResearchPaper researchPaper=researchPaperService.findById(id);
		
		if(researchPaper == null) {

			return "redirect:/addResearchPaper";
		}

		/*if(carousel.getUser().getId() != usr.getId()) {

			return "redirect:/addResearchPaper";
		}*/
		model.addAttribute("researchPaper", researchPaper);

		return "updateResearchPaper";
	}
	
	
	@RequestMapping(value = "/updateResearchPaper", method = RequestMethod.POST)
	public String updateResearchPaperPost(HttpServletRequest req,Model model,Principal principal,
			@RequestParam("researchFile") MultipartFile researchFile) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		String rseacrhPaperId=req.getParameter("researchPaperId");
		String title = req.getParameter("title");
		String desc = req.getParameter("description");
		
		

		
		ResearchPaper researchPaper=researchPaperService.findById(Integer.parseInt(rseacrhPaperId));
		
		if(researchPaper==null) {
			model.addAttribute("error_msg","ResearchPaper doesn't exist");
			return "updateResearchPaper";
		}

		
		model.addAttribute("researchPaper", researchPaper);

		
		try {
			

			if(!researchFile.isEmpty()) {
				if(!ServiceUtility.checkFileExtensiononeFilePDF(researchFile)) { // throw error on extension
					model.addAttribute("error_msg", "Only pdf file is required");
					return "updateResearchPaper";
			}
			}


			

			researchPaper.setTitle(title);
			researchPaper.setDescription(desc);
			
			if(!researchFile.isEmpty()) {
				ServiceUtility.createFolder(env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadResearchPaper+researchPaper.getId());
				String pathtoUploadPoster=ServiceUtility.uploadFile(researchFile, env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadResearchPaper+researchPaper.getId());
				int indexToStart=pathtoUploadPoster.indexOf("Media");

				String document=pathtoUploadPoster.substring(indexToStart, pathtoUploadPoster.length());

				researchPaper.setResearchPaperPath(document);
				researchPaper.setThumbnailPath(null);

			}
			
			

			researchPaperService.save(researchPaper);

		}catch (Exception e) {
			// TODO: handle exception
			model.addAttribute("error_msg",CommonData.RECORD_ERROR);
			model.addAttribute("researchPaper", researchPaper);
			return "updateResearchPaper";        // need to add some error message
		}
		
		


		model.addAttribute("success_msg",CommonData.RECORD_UPDATE_SUCCESS_MSG);
		model.addAttribute("researchPaper", researchPaper);

		return "updateResearchPaper";
	}
	
	
	


	
	/***********************************************End********************************************************/
	
	/**************************************Edit section of Carousel ************************************************/
	
	/*
	 * Author: Alok Kumar
	 */
	
	@RequestMapping(value = "/carousel/edit/{id}", method = RequestMethod.GET)
	public String editCarouselGet(@PathVariable int id,Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		
		Carousel carousel=caroService.findById(id);
		
		if(carousel == null) {

			return "redirect:/addEvent";
		}

		/*if(carousel.getUser().getId() != usr.getId()) {

			return "redirect:/addEvent";
		}*/
		model.addAttribute("carousels", carousel);

		return "updateCarousel";
	}
	
	/*
	 * Author:Alok Kumar 
	 */
	
	@RequestMapping(value = "/updateCarousel", method = RequestMethod.POST)
	public String updateCaroUselPost(HttpServletRequest req,Model model,Principal principal,
			@RequestParam("Image") MultipartFile files) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		String carouselId=req.getParameter("carouselId");
		String eventName = req.getParameter("eventname");
		String desc = req.getParameter("description");
		
		

		
		Carousel carousel=caroService.findById(Integer.parseInt(carouselId));

		model.addAttribute("carousels", carousel);

		if(carousel==null) {
			model.addAttribute("error_msg","Event doesn't exist");
			return "updateCarousel";
		}

		try {
			

			if(!files.isEmpty()) {
				if(!ServiceUtility.checkFileExtensionImage(files)) { // throw error on extension
					model.addAttribute("error_msg",CommonData.JPG_PNG_EXT);
					return "updateCarousel";
			}
			}


			

			carousel.setEventName(eventName);
			carousel.setDescription(desc);
			
			if(!files.isEmpty()) {
				String pathtoUploadPoster=ServiceUtility.uploadFile(files, env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadCarousel+carousel.getId());
				int indexToStart=pathtoUploadPoster.indexOf("Media");

				String document=pathtoUploadPoster.substring(indexToStart, pathtoUploadPoster.length());

				carousel.setPosterPath(document);

			}
			
			

			caroService.save(carousel);

		}catch (Exception e) {
			// TODO: handle exception
			model.addAttribute("error_msg",CommonData.RECORD_ERROR);
			model.addAttribute("carousels", carousel);
			return "updateCarousel";        // need to add some error message
		}
		
		


		model.addAttribute("success_msg",CommonData.RECORD_UPDATE_SUCCESS_MSG);
		model.addAttribute("carousels", carousel);

		return "updateCarousel";
	}
	
	
	
	/************************************************END***********************************************************/
	

	/************************************VIEW SECTION OF LANGAUAGE**********************************************/


	/**
	 * redirects to add language page
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/language", method = RequestMethod.GET)
	public String viewLanguageGet(Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
		List<Language> lan=lanService.getAllLanguages();
		model.addAttribute("lan", lan);

		return "language";
	}

	/**
	 * redirects to edit language page given id
	 * @param id int value
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/language/edit/{lanName}", method = RequestMethod.GET)
	public String editLanguageGet(@PathVariable(name = "lanName") String lanTemp,Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		Language lan=lanService.getByLanName(lanTemp);
		
		if(lan == null) {
			return "redirect:/addLanguage";
		}

		model.addAttribute("language",lan);

		return "updateLanguage";  // need to accomdate view part
	}
	
	/**
	 * update language object
	 * @param model Model object
	 * @param principal Principal object
	 * @param req HttpServletRequest object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/updateLanguage",method = RequestMethod.POST)
	public String updateLanguagePost(Model model,Principal principal,HttpServletRequest req) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		String languagename=req.getParameter("languageName");
		String lanIdInString = req.getParameter("lanId");
		int lanId = Integer.parseInt(lanIdInString);

		Language lan = lanService.getById(lanId);

		if(lan == null) {
			model.addAttribute("error_msg", CommonData.RECORD_ERROR);
			model.addAttribute("language",lan);
			return "updateLanguage";  //  accomodate view part
		}

		if(languagename==null) {

			model.addAttribute("error_msg", CommonData.RECORD_ERROR);
			model.addAttribute("language",lan);
			return "updateLanguage";  //  accomodate view part
		}

		if(lanService.getByLanName(languagename)!=null) {

			model.addAttribute("error_msg", CommonData.RECORD_EXISTS);
			model.addAttribute("language",lan);
			return "updateLanguage";   //  accomodate view part
		}

		String language_formatted = languagename.substring(0, 1).toUpperCase() + languagename.substring(1).toLowerCase();
		lan.setLangName(language_formatted);

		try {
			lanService.save(lan);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			model.addAttribute("language",lan);
			model.addAttribute("error_msg", CommonData.RECORD_ERROR);
			return "updateLanguage";  //  accomodate view part
		}

		lan = lanService.getById(lanId);
		model.addAttribute("language",lan);
		model.addAttribute("success_msg", CommonData.RECORD_SAVE_SUCCESS_MSG);

		return "updateLanguage";  //  accomodate view part

	}

	/************************************END**********************************************/

	/*********************************** VIEW SECTION OF DOMAIN REVIEWER ************************************/

	/**
	 * redirects to domain reviewer page
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/domainReviewer", method = RequestMethod.GET)
	public String viewDomaineGet(Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		Role domain=roleService.findByname(CommonData.domainReviewerRole);

		List<UserRole> domains = usrRoleService.findAllByRole(domain);

		model.addAttribute("domains", domains);

		return "viewDomainReviewer";
	}

	/************************************END**********************************************/

	/*********************************** VIEW SECTION OF QUALITY REVIEWER ************************************/

	/**
	 * redirects to quality reviewer page
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/qualityReviewer", method = RequestMethod.GET)
	public String viewQualityeGet(Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
		Role quality=roleService.findByname(CommonData.qualityReviewerRole);

		List<UserRole> qualities = usrRoleService.findAllByRole(quality);

		model.addAttribute("qualities", qualities);

		return "viewQualityReviewer";
	}

	/************************************END**********************************************/

	/*********************************** VIEW SECTION OF MASTER TRAINER ************************************/

	/**
	 * redirects to master trainer page
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/masterTrainer", method = RequestMethod.GET)
	public String viewMasterTrainerGet(Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
		Role master=roleService.findByname(CommonData.masterTrainerRole);

		List<UserRole> masters = usrRoleService.findAllByRole(master);
		model.addAttribute("masters", masters);

		return "viewMasterTrainer";
	}

	/************************************END**********************************************/

	/*********************************** VIEW SECTION OF QUESTIONNAIRE ************************************/

	@RequestMapping(value = "/downloadQuestion", method = RequestMethod.GET)
	public String PostQuestionaireGet(Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
		List<PostQuestionaire> postQuestionnaires = postQuestionService.findAll();

		model.addAttribute("postQuestionnaires", postQuestionnaires);

		return "viewQuestionnaire";
	}

	/************************************END**********************************************/
	/************************************BROCHURE**********************************************/



	/************************************END**********************************************/
	/************************************UPDATE AND VIEW SECTION OF TESTIMONIAL**********************************************/

	/**
	 * redirects to testimonial page on homepage
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/testimonialList", method = RequestMethod.GET)
	public String viewtestimonialListGet(Model model,Principal principal) {
		List<Testimonial> test= new ArrayList<>();
		List<Testimonial> test1=testService.findAll();
		for(Testimonial temp : test1) {
			if(temp.isApproved()) {
				test.add(temp);
			}
		}
		
		model.addAttribute("testimonials", test);

		return "testimonialList";
	}

	/**
	 * redirects to testimonial page
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/testimonial", method = RequestMethod.GET)
	public String viewtestimonialGet(Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
		List<Testimonial> test=testService.findAll();
		model.addAttribute("testimonials", test);

		return "testimonial";
	}

	/**
	 * redirects to edit testimonial page given testimonial id
	 * @param id int value
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/testimonial/edit/{id}", method = RequestMethod.GET)
	public String edittestimonialGet(@PathVariable int id,Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		Testimonial test=testService.findById(id);
		
		if(test == null) {

			return "redirect:/addTestimonial";
		}

		/*
		 * if(test.getUser().getId() != usr.getId()) {

			return "redirect:/addTestimonial";
		}
		*/
		
		model.addAttribute("testimonials", test);

		return "updateTestimonial";
	}

	/**
	 * Update testimonial object in database
	 * @param req HttpServletRequest object
	 * @param model Model object
	 * @param principal Principal object
	 * @param file MultipartFile object
	 * @return String object(webpage)
	 */
	@RequestMapping(value = "/updateTestimonial", method = RequestMethod.POST)
	public String updatetestimonialGet(HttpServletRequest req,Model model,Principal principal,@RequestParam("TestiVideo") MultipartFile file) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
		String testiId=req.getParameter("testimonialId");
		String name=req.getParameter("testimonialName");
		String desc=req.getParameter("desc");

		Testimonial test=testService.findById(Integer.parseInt(testiId));

		if(test==null) {
			// accommodate error message
			model.addAttribute("error_msg", CommonData.TESTIMONIAL_NOT_ERROR);
			return "updateTestimonial";
		}

		if(!file.isEmpty()) {
		try {

			String pathSampleVideo = null;;
			try {
				pathSampleVideo = ServiceUtility.uploadVideoFile(file, env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadDirectoryTestimonial);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			IContainer container = IContainer.make();
			int result=10;
			result = container.open(pathSampleVideo,IContainer.Type.READ,null);

			try {
				if(result<0) {

					model.addAttribute("error_msg",CommonData.RECORD_ERROR);
					return "updateTestimonial";

				}else {
						if(container.getDuration()>CommonData.videoDuration) {

							model.addAttribute("error_msg",CommonData.VIDEO_DURATION_ERROR);
							Path deletePreviousPath=Paths.get(pathSampleVideo);
							Files.delete(deletePreviousPath);
							return "updateTestimonial";
					}
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				model.addAttribute("error_msg",CommonData.RECORD_ERROR);
				return "updateTestimonial";
			}

			    ServiceUtility.createFolder(env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadDirectoryTestimonial+test.getTestimonialId());
				String pathtoUploadPoster=ServiceUtility.uploadVideoFile(file, env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadDirectoryTestimonial+test.getTestimonialId());
				int indexToStart=pathtoUploadPoster.indexOf("Media");

				String document=pathtoUploadPoster.substring(indexToStart, pathtoUploadPoster.length());
				test.setName(name);
				test.setDescription(desc);
				test.setFilePath(document);

				testService.save(test);


		}catch (Exception e) {
			// TODO: handle exception

			e.printStackTrace();
			model.addAttribute("error_msg", CommonData.RECORD_ERROR);
			return "updateTestimonial";    // throw a error
		}
		}else {

			test.setName(name);
			test.setDescription(desc);

			testService.save(test);
		}


		model.addAttribute("success_msg", CommonData.RECORD_SAVE_SUCCESS_MSG);

		return "updateTestimonial";
	}


	/************************************END**********************************************/

	/************************************ROLE MANGAEMENT OPERATION**********************************************/

	/**
	 * redirects to add contributor page
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/addContributorRole", method = RequestMethod.GET)
	public String addContributorGet(Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		List<Language> languages=lanService.getAllLanguages();

		model.addAttribute("languages", languages);
		return "addContributorRole";
	}

	/**
	 * add contributor role into system
	 * @param model Model object
	 * @param principal Principal object
	 * @param req HttpServletRequest
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/addContributorRole", method = RequestMethod.POST)
	public String addContributorPost(Model model,Principal principal,HttpServletRequest req) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		String lanName=req.getParameter("selectedLan");
		
		if(lanName == null) {
			
			return "redirect:/addContributorRole";
		}

		Language lan=lanService.getByLanName(lanName);
		
		if(lan == null) {
			
			return "redirect:/addContributorRole";
		}

		Role role=roleService.findByname(CommonData.contributorRole);
		List<UserRole> userRoles = usrRoleService.findByLanUser(lan, usr, role);
		if(!userRoles.isEmpty()) {
			// throw error
			//model.addAttribute("msgSuccefull", CommonData.ADMIN_ADDED_SUCCESS_MSG);
			List<Language> languages=lanService.getAllLanguages();
			List<Category> categories=catService.findAll();

			model.addAttribute("categories", categories);

			model.addAttribute("languages", languages);

			model.addAttribute("error_msg", CommonData.CONTRIBUTOR_ERROR);

			return "addContributorRole";
		}

		UserRole usrRole=new UserRole();
		usrRole.setCreated(ServiceUtility.getCurrentTime());
		usrRole.setUser(usr);
		usrRole.setRole(role);
		usrRole.setLanguage(lan);
		usrRole.setUserRoleId(usrRoleService.getNewUsrRoletId());

		try {
			usrRoleService.save(usrRole);
			model.addAttribute("success_msg", CommonData.CONTRIBUTOR_ADDED_SUCCESS_MSG);
		} catch (Exception e) {
			model.addAttribute("error_msg", CommonData.CONTRIBUTOR_ERROR_MSG);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<Language> languages=lanService.getAllLanguages();

		model.addAttribute("languages", languages);

		return "addContributorRole";
	}
	
	/**
	 * redirects to add contributor page
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/addExternalContributorRole", method = RequestMethod.GET)
	public String addExternalContributorGet(Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		List<Language> languages=lanService.getAllLanguages();

		model.addAttribute("languages", languages);
		return "addExternalContributorRole";
	}

	/**
	 * add contributor role into system
	 * @param model Model object
	 * @param principal Principal object
	 * @param req HttpServletRequest
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/addExternalContributorRole", method = RequestMethod.POST)
	public String addExternalContributorPost(Model model,Principal principal,HttpServletRequest req) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		String lanName=req.getParameter("selectedLan");
		
		if(lanName == null) {
			
			return "redirect:/addExternalContributorRole";
		}

		Language lan=lanService.getByLanName(lanName);
		
		if(lan == null) {
			
			return "redirect:/addExternalContributorRole";
		}

		Role role=roleService.findByname(CommonData.externalContributorRole);
		List<UserRole> userRoles = usrRoleService.findByLanUser(lan, usr, role);
		if(!userRoles.isEmpty()) {
			
			// throw error
			//model.addAttribute("msgSuccefull", CommonData.ADMIN_ADDED_SUCCESS_MSG);
			List<Language> languages=lanService.getAllLanguages();
			List<Category> categories=catService.findAll();

			model.addAttribute("categories", categories);

			model.addAttribute("languages", languages);

			model.addAttribute("error_msg", CommonData.CONTRIBUTOR_ERROR);

			return "addExternalContributorRole";
		}

		UserRole usrRole=new UserRole();
		usrRole.setCreated(ServiceUtility.getCurrentTime());
		usrRole.setUser(usr);
		usrRole.setRole(role);
		usrRole.setLanguage(lan);
		usrRole.setUserRoleId(usrRoleService.getNewUsrRoletId());

		try {
			usrRoleService.save(usrRole);
			model.addAttribute("success_msg", CommonData.CONTRIBUTOR_ADDED_SUCCESS_MSG);
		} catch (Exception e) {
			model.addAttribute("error_msg", CommonData.CONTRIBUTOR_ERROR_MSG);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<Language> languages=lanService.getAllLanguages();

		model.addAttribute("languages", languages);

		return "addExternalContributorRole";
	}

	/**
	 * redirects to add admin role page
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/addAdminRole", method = RequestMethod.GET)
	public String addAdminPost(Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		List<Language> languages=lanService.getAllLanguages();
		List<Category> categories=catService.findAll();

		model.addAttribute("categories", categories);
		model.addAttribute("languages", languages);


		return "addAdminRole";
	}

	/**
	 * add admin role into system
	 * @param model Model object
	 * @param principal Principal object
	 * @param req HttpServletRequest
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/addAdminRole", method = RequestMethod.POST)
	public String addAdminPost(Model model,Principal principal,HttpServletRequest req) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		String lanName=req.getParameter("selectedLan");
		String catName=req.getParameter("catSelected");

		Category cat=catService.findBycategoryname(catName);

		Language lan=lanService.getByLanName(lanName);
		
		if(cat == null) {

			List<Language> languages=lanService.getAllLanguages();
			List<Category> categories=catService.findAll();

			model.addAttribute("categories", categories);

			model.addAttribute("languages", languages);
			model.addAttribute("error_msg", "Please Try Again");

			return "addAdminRole";
		}
		
		if(lan == null) {

			List<Language> languages=lanService.getAllLanguages();
			List<Category> categories=catService.findAll();

			model.addAttribute("categories", categories);

			model.addAttribute("languages", languages);
			model.addAttribute("error_msg", "Please Try Again");

			return "addAdminRole";
		}

		Role role=roleService.findByname(CommonData.adminReviewerRole);

		if(usrRoleService.findByLanCatUser(lan, cat, usr, role)!=null) {

			List<Language> languages=lanService.getAllLanguages();
			List<Category> categories=catService.findAll();

			model.addAttribute("categories", categories);

			model.addAttribute("languages", languages);
			model.addAttribute("error_msg", CommonData.DUPLICATE_ROLE_ERROR);

			return "addAdminRole";
		}

		UserRole usrRole=new UserRole();
		usrRole.setCreated(ServiceUtility.getCurrentTime());
		usrRole.setUser(usr);
		usrRole.setRole(role);
		usrRole.setLanguage(lan);
		usrRole.setCategory(cat);
		usrRole.setUserRoleId(usrRoleService.getNewUsrRoletId());

		try {
			usrRoleService.save(usrRole);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			model.addAttribute("error_msg", CommonData.ROLE_ERROR_MSG);
			e.printStackTrace();
												// accommodate error message
		}

		List<Language> languages=lanService.getAllLanguages();
		List<Category> categories=catService.findAll();

		model.addAttribute("categories", categories);

		model.addAttribute("languages", languages);

		model.addAttribute("success_msg", CommonData.ADMIN_ADDED_SUCCESS_MSG);
		return "addAdminRole";
	}

	/**
	 * redirects to add domain role page
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/addDomainRole", method = RequestMethod.GET)
	public String addDomainGet(Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		List<Language> languages=lanService.getAllLanguages();
		List<Category> categories=catService.findAll();

		model.addAttribute("categories", categories);

		model.addAttribute("languages", languages);

		return "addDomainRole";
	}

	/**
	 * add domain role into system
	 * @param model Model object
	 * @param principal Principal object
	 * @param req HttpServletRequest
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/addDomainRole", method = RequestMethod.POST)
	public String addDomainPost(Model model,Principal principal,HttpServletRequest req) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		String lanName=req.getParameter("selectedLan");
		String catName=req.getParameter("catSelected");

		Category cat=catService.findBycategoryname(catName);

		Language lan=lanService.getByLanName(lanName);
		
		if(cat == null) {

			// throw error
			//model.addAttribute("msgSuccefull", CommonData.ADMIN_ADDED_SUCCESS_MSG);
			List<Language> languages=lanService.getAllLanguages();
			List<Category> categories=catService.findAll();

			model.addAttribute("categories", categories);

			model.addAttribute("languages", languages);
			model.addAttribute("error_msg", "Please try Again");

			return "addDomainRole";
		}
		
		if(lan == null) {

			// throw error
			//model.addAttribute("msgSuccefull", CommonData.ADMIN_ADDED_SUCCESS_MSG);
			List<Language> languages=lanService.getAllLanguages();
			List<Category> categories=catService.findAll();

			model.addAttribute("categories", categories);

			model.addAttribute("languages", languages);
			model.addAttribute("error_msg", "Please try Again");

			return "addDomainRole";
		}
		
		Role role=roleService.findByname(CommonData.domainReviewerRole);

		if(usrRoleService.findByLanCatUser(lan, cat, usr, role)!=null) {

			// throw error
			//model.addAttribute("msgSuccefull", CommonData.ADMIN_ADDED_SUCCESS_MSG);
			List<Language> languages=lanService.getAllLanguages();
			List<Category> categories=catService.findAll();

			model.addAttribute("categories", categories);

			model.addAttribute("languages", languages);
			model.addAttribute("error_msg", CommonData.DUPLICATE_ROLE_ERROR);

			return "addDomainRole";
		}
		
		int newConsultid=consultService.getNewConsultantId();
		Consultant local=new Consultant();
		local.setConsultantId(newConsultid);
		local.setDescription("null");
		local.setDateAdded(ServiceUtility.getCurrentTime());
		local.setUser(usr);

		Set<Consultant> consults=new HashSet<Consultant>();
		consults.add(local);


		UserRole usrRole=new UserRole();
		usrRole.setCreated(ServiceUtility.getCurrentTime());
		usrRole.setUser(usr);
		usrRole.setRole(role);
		usrRole.setCategory(cat);
		usrRole.setLanguage(lan);
		usrRole.setUserRoleId(usrRoleService.getNewUsrRoletId());

		try {
			usrRoleService.save(usrRole);
			userService.addUserToConsultant(usr, consults);
			model.addAttribute("success_msg", "Request Submitted Sucessfully");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			model.addAttribute("error_msg", CommonData.ROLE_REQUEST_ERROR);
			e.printStackTrace();
			return "addDomainRole";									// accommodate error message
		}

		List<Language> languages=lanService.getAllLanguages();
		List<Category> categories=catService.findAll();

		model.addAttribute("categories", categories);

		model.addAttribute("languages", languages);

		return "addDomainRole";
	}
	
	/**
	 * redirects to add quality page
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/addQualityRole", method = RequestMethod.GET)
	public String addQualityGet(Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		List<Language> languages=lanService.getAllLanguages();
		List<Category> categories=catService.findAll();

		model.addAttribute("categories", categories);

		model.addAttribute("languages", languages);

		return "addQualityRole";
	}

	/**
	 * add quality role into system
	 * @param model Model object
	 * @param principal Principal object
	 * @param req HttpServletRequest
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/addQualityRole", method = RequestMethod.POST)
	public String addQualityPost(Model model,Principal principal,HttpServletRequest req) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		String lanName=req.getParameter("selectedLan");
		String catName=req.getParameter("catSelected");

		Category cat=catService.findBycategoryname(catName);

		Language lan=lanService.getByLanName(lanName);
		
		if(cat == null) {

			// throw error
			//model.addAttribute("msgSuccefull", CommonData.ADMIN_ADDED_SUCCESS_MSG);
			List<Language> languages=lanService.getAllLanguages();
			List<Category> categories=catService.findAll();

			model.addAttribute("categories", categories);

			model.addAttribute("languages", languages);
			model.addAttribute("error_msg", "Please try Again");

			return "addQualityRole";
		}
		
		if(lan == null) {

			// throw error
			//model.addAttribute("msgSuccefull", CommonData.ADMIN_ADDED_SUCCESS_MSG);
			List<Language> languages=lanService.getAllLanguages();
			List<Category> categories=catService.findAll();

			model.addAttribute("categories", categories);

			model.addAttribute("languages", languages);
			model.addAttribute("error_msg", "Please try Again");

			return "addQualityRole";
		}
		
		Role role=roleService.findByname(CommonData.qualityReviewerRole);

		if(usrRoleService.findByLanCatUser(lan, cat, usr, role)!=null) {

			// throw error
			//model.addAttribute("msgSuccefull", CommonData.ADMIN_ADDED_SUCCESS_MSG);
			List<Language> languages=lanService.getAllLanguages();
			List<Category> categories=catService.findAll();

			model.addAttribute("categories", categories);

			model.addAttribute("languages", languages);
			model.addAttribute("error_msg", CommonData.DUPLICATE_ROLE_ERROR);

			return "addQualityRole";
		}

		UserRole usrRole=new UserRole();
		usrRole.setCreated(ServiceUtility.getCurrentTime());
		usrRole.setUser(usr);
		usrRole.setRole(role);
		usrRole.setCategory(cat);
		usrRole.setLanguage(lan);
		usrRole.setUserRoleId(usrRoleService.getNewUsrRoletId());

		try {
			usrRoleService.save(usrRole);
			model.addAttribute("success_msg", CommonData.QUALITY_ADDED_SUCCESS_MSG);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			model.addAttribute("error_msg", CommonData.ROLE_REQUEST_ERROR);
			e.printStackTrace();
												// accommodate error message
		}

		List<Language> languages=lanService.getAllLanguages();
		List<Category> categories=catService.findAll();

		model.addAttribute("categories", categories);

		model.addAttribute("languages", languages);

		return "addQualityRole";
	}

	/**
	 * redirects to add master trainer page
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/addMasterTrainerRole", method = RequestMethod.GET)
	public String addMasterTrainerGet(Model model,Principal principal) {

		User usr=new User();
		List<IndianLanguage> languages = iLanService.findAll();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());

		}

		model.addAttribute("userInfo", usr);
		model.addAttribute("languages", languages);

		if(usr.getProfilePic() == null) {
			model.addAttribute("error_msg", CommonData.ADD_PROFILE_PIC_CONSTRAINT);
			return "profileView";
		}

		Role role = roleService.findByname(CommonData.masterTrainerRole);

		if(!usrRoleService.findByRoleUser(usr, role).isEmpty()) {

			model.addAttribute("success_msg", "Request Already submitted for role");
			model.addAttribute("alredaySubmittedFlag", true);
			return "addMasterTrainerRole";
		}
		List<OrganizationRole> org_roles = organizationRoleService.findAll();
		
		model.addAttribute("org_roles", org_roles);

		model.addAttribute("alredaySubmittedFlag", false);
		return "addMasterTrainerRole";
	}

	/**
	 * add master trainer into system
	 * @param model Model object
	 * @param principal Principal object
	 * @param req HttpServletRequest
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/addMasterTrainerRole", method = RequestMethod.POST)
	public String addMasterTrainerPost(Model model,Principal principal,HttpServletRequest req) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		String name=req.getParameter("name");
		String age=req.getParameter("age");
		String mobileNumber=req.getParameter("phone");
		String address=req.getParameter("address");
		String organization=(req.getParameter("org"));
		String exp=req.getParameter("experience");
		String aadhar=req.getParameter("aadharNumber");
		String lang=req.getParameter("languages");
		String roleOrg=req.getParameter("newRole");
		

		List<OrganizationRole> org_roles = organizationRoleService.findAll();
		
		model.addAttribute("org_roles", org_roles);

		int userIndianMappingId=userIndianMappingService.getNewId();

		Set<UserIndianLanguageMapping> userIndianMapping = new HashSet<UserIndianLanguageMapping>();

		if(aadhar.length()!=12) {
			 // throw error
			model.addAttribute("error_msg", "Invalid aadhar number");
			return "addMasterTrainerRole";
		}

		if(mobileNumber.length()!=10) {

			// throw error
			model.addAttribute("error_msg", "Invalid phone number");
			return "addMasterTrainerRole";
		}


		String[] lan = lang.split("&");
		for(String x : lan) {

			String[] y = x.split("_");
			for(int z=1 ; z<y.length ; z++) {

				IndianLanguage temp = iLanService.findByName(y[0]);

				UserIndianLanguageMapping tempUser = new UserIndianLanguageMapping();
				tempUser.setId(userIndianMappingId++);
				tempUser.setUser(usr);
				tempUser.setIndianlan(temp);

				for(char xx : y[z].toCharArray()) {

					if(xx=='r') {
						tempUser.setRead(true);
					}
//					else if(xx=='w') {
//						tempUser.setWrite(true);
//						System.out.println(xx);
//					}
					else if(xx=='s') {
						tempUser.setSpeak(true);
					}
				}

				userIndianMapping.add(tempUser);
			}

		}

		Role role=roleService.findByname(CommonData.masterTrainerRole);

		List<UserRole> userRoles = usrRoleService.findByRoleUser(usr, role);
		if(!userRoles.isEmpty()) {
			// throw error
			model.addAttribute("error_msg", "Error in submitting request");
			return "addMasterTrainerRole";
		}

		usr.setAadharNumber(Long.parseLong(aadhar));
		usr.setExperience(Integer.parseInt(exp));
		usr.setAddress(address);
		usr.setFirstName(name);
		usr.setOrganization(organization);
		usr.setAge(Integer.parseInt(age));
		usr.setPhone(Long.parseLong(mobileNumber));

		List<OrganizationRole> orgRolesList = organizationRoleService.findAll();
		boolean isRoleExist = orgRolesList.stream().anyMatch(o -> o.getRole().equals(roleOrg));
		OrganizationRole r;
		if(isRoleExist) {
			 r = organizationRoleService.getByRole(roleOrg);
		}else {
			r = new OrganizationRole();
			r.setDateAdded(ServiceUtility.getCurrentTime());
			r.setRole(roleOrg);
			r.setRoleId(organizationRoleService.getnewRoleId());
			organizationRoleService.save(r);
		}
		usr.setOrgRolev(r);
		userService.save(usr);
		try {

			userService.addUserToUserIndianMapping(usr, userIndianMapping);

			UserRole usrRole=new UserRole();
			usrRole.setCreated(ServiceUtility.getCurrentTime());
			usrRole.setUser(usr);
			usrRole.setRole(role);
			usrRole.setUserRoleId(usrRoleService.getNewUsrRoletId());

			usrRoleService.save(usrRole);

			model.addAttribute("success_msg", "Request submitted for role successfully");

		}catch (Exception e) {
			// TODO: handle exception

			model.addAttribute("error_msg", "Error in submitting request");
			// throw error
		}
		List<IndianLanguage> languages = iLanService.findAll();

		model.addAttribute("userInfo", usr);
		model.addAttribute("success_msg", "Request submitted for role successfully");

		return "addMasterTrainerRole";
	}


	/************************************END****************************************************************/

	/*********************************** Approve Role ************************************************/

	/**
	 * redirects to approve role page
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/approveRole", method = RequestMethod.GET)
	public String approveRoleGet(Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		Role contributor=roleService.findByname(CommonData.contributorRole);
		Role admin=roleService.findByname(CommonData.adminReviewerRole);
		Role master=roleService.findByname(CommonData.masterTrainerRole);
		Role quality=roleService.findByname(CommonData.qualityReviewerRole);
		Role domain=roleService.findByname(CommonData.domainReviewerRole);
		Role external=roleService.findByname(CommonData.externalContributorRole);

		List<UserRole> adminReviewer = usrRoleService.findAllByRoleAndStatusAndRevoked(admin,false,false);
		List<UserRole> masterTrainer = usrRoleService.findAllByRoleAndStatusAndRevoked(master,false,false);
		List<UserRole> qualityReviewer = usrRoleService.findAllByRoleAndStatusAndRevoked(quality,false,false);
		List<UserRole> contributorReviewer = usrRoleService.findAllByRoleAndStatusAndRevoked(contributor,false,false);
		List<UserRole> domainReviewer = usrRoleService.findAllByRoleAndStatusAndRevoked(domain,false,false);
		List<UserRole> externalUser= usrRoleService.findAllByRoleAndStatusAndRevoked(external,false,false);
		
		int countAdminreviewer= adminReviewer.size();
		int countMasterTrainer= masterTrainer.size();
		int countQualityReviewer= qualityReviewer.size();
		int countContributorReviewer= contributorReviewer.size();
		int countDomainReviwer= domainReviewer.size();
		int countExternalUser= externalUser.size();
		
		model.addAttribute("countAdminreviewer", countAdminreviewer);
		model.addAttribute("countMasterTrainer", countMasterTrainer);
		model.addAttribute("countQualityReviewer", countQualityReviewer);
		model.addAttribute("countContributorReviewer", countContributorReviewer);
		model.addAttribute("countDomainReviwer", countDomainReviwer);
		model.addAttribute("countExternalUser", countExternalUser);
		

		model.addAttribute("userInfoAdmin", adminReviewer);
		model.addAttribute("userInfoQuality", qualityReviewer);
		model.addAttribute("userInfoContributor", contributorReviewer);
		model.addAttribute("userInfoMaster", masterTrainer);
		model.addAttribute("userInfoDomain", domainReviewer);
		model.addAttribute("userInfoExternal", externalUser);


		return "approveRole";
	}




	/******************************************END **************************************************/


	/*************************************** ASSIGN CONTRINUTOR (NOT IN USE FOR NOW) ****************************************/
	
	/**
	 * redirects to assign contributor edit page
	 * @param id int value
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/assignContributor/edit/{id}", method = RequestMethod.GET)
	public String editAssignContributor(@PathVariable Long id,Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}
		Role role=roleService.findByname(CommonData.contributorRole);
		List<UserRole> userRoles= usrRoleService.findAllByRoleAndStatusAndRevoked(role, true,false);

		model.addAttribute("userByContributors", userRoles);
		model.addAttribute("userInfo", usr);

		return "updateAssignContributor";
	}

	
	/**
	 * redirects to assign contributor page
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/assignTutorialToContributor", method = RequestMethod.GET)
	public String assignTutorialToContributoreGet(Model model,Principal principal) {

		model.addAttribute("userInfo", getUser(principal, userService));

		Role role=roleService.findByname(CommonData.contributorRole);
		Role role1=roleService.findByname(CommonData.externalContributorRole);

		List<ContributorAssignedTutorial> userRoles = conRepo.findAll();
		
		List<UserRole> userRolesTemp= usrRoleService.findAllByRoleAndStatusAndRevoked(role, true,false);
		userRolesTemp.addAll(usrRoleService.findAllByRoleAndStatusAndRevoked(role1, true,false));
		Collections.sort(userRolesTemp);
		LinkedHashSet<User> userRolesUniqueTemp = new LinkedHashSet<>();
		for(UserRole x : userRolesTemp) {
			userRolesUniqueTemp.add(x.getUser());
		}
		model.addAttribute("userByContributors", userRolesUniqueTemp);
		model.addAttribute("userByContributorsAssigned", userRoles);
		
		

		return "assignContributorList";
	}

	/**
	 * Assign tutorial to contributor
	 * @param model Model object
	 * @param principal Principal object
	 * @param contributorName String object
	 * @param lanName String object
	 * @param catName String object
	 * @param topics list of String object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/assignTutorialToContributor", method = RequestMethod.POST)
	public String assignTutorialToContributorePost(Model model,Principal principal,
												@RequestParam(name = "contributorName") String contributorName,
												@RequestParam(name = "languageName") String lanName,
												@RequestParam(name = "contributorCategory") String catName,
												@RequestParam(name = "inputTopic") String[] topics) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		Role role=roleService.findByname(CommonData.contributorRole);
		Role role1=roleService.findByname(CommonData.externalContributorRole);

		List<ContributorAssignedTutorial> userRoles = conRepo.findAll();

		List<UserRole> userRolesTemp= usrRoleService.findAllByRoleAndStatusAndRevoked(role, true,false);
		userRolesTemp.addAll(usrRoleService.findAllByRoleAndStatusAndRevoked(role1, true,false));
		Collections.sort(userRolesTemp);
		LinkedHashSet<User> userRolesUniqueTemp = new LinkedHashSet<>();
		for(UserRole x : userRolesTemp) {
			userRolesUniqueTemp.add(x.getUser());
		}
		
		for(UserRole x : userRolesTemp) {
			userRolesUniqueTemp.add(x.getUser());
		}
		
		model.addAttribute("userByContributorsAssigned", userRoles);
		
		model.addAttribute("userByContributors", userRolesUniqueTemp);


		Language lan=lanService.getByLanName(lanName);
		Category cat=catService.findByid(Integer.parseInt(catName));
		User userAssigned=userService.findByUsername(contributorName);
		Set<ContributorAssignedTutorial> conTutorials=new HashSet<ContributorAssignedTutorial>();
		int conNewId=conRepo.getNewId();
		int conMutliUserNewId = conMultiUser.getNewId();

		for(String topic:topics) {

			Topic localtopic=topicService.findById(Integer.parseInt(topic));
			if(localtopic != null) {

				TopicCategoryMapping topicCat=topicCatService.findAllByCategoryAndTopic(cat, localtopic);

				ContributorAssignedTutorial x=conRepo.findByTopicCatAndLanViewPart(topicCat, lan);

				if(x == null) {

					ContributorAssignedTutorial temp=new ContributorAssignedTutorial(conNewId++, ServiceUtility.getCurrentTime(), topicCat, lan);
					conRepo.save(temp);
					ContributorAssignedMultiUserTutorial multiUser = new ContributorAssignedMultiUserTutorial(conMutliUserNewId++, ServiceUtility.getCurrentTime(), userAssigned, temp);
					conMultiUser.save(multiUser);
					
					//conTutorials.add(temp);

				}else {
					// throw error for repeated task
					
					//model.addAttribute("error_msg", CommonData.CONTRIBUTOR_ERROR_MSG);
					
					ContributorAssignedMultiUserTutorial multiUser = new ContributorAssignedMultiUserTutorial(conMutliUserNewId++, ServiceUtility.getCurrentTime(), userAssigned, x);
					conMultiUser.save(multiUser);
					
					// return "assignContributorList";
				}



			}else {
				// throw error as topic doesn't exist
				model.addAttribute("error_msg", CommonData.CONTRIBUTOR_TOPIC_ERROR);
				return "assignContributorList";
			}
		}


		//userService.addUserToContributorTutorial(userAssigned, conTutorials);
		
		userRoles = conRepo.findAll();

		userRolesTemp= usrRoleService.findAllByRoleAndStatusAndRevoked(role, true,false);
		
		for(UserRole x : userRolesTemp) {
			userRolesUniqueTemp.add(x.getUser());
		}

		model.addAttribute("userByContributorsAssigned", userRoles);
		
		model.addAttribute("userByContributors", userRolesUniqueTemp);


		model.addAttribute("success_msg", CommonData.CONTRIBUTOR_ASSIGNED_TUTORIAL);

		return "assignContributorList";
	}




	/********************************************END*************************************************/


	/*********************************** CONTRIBUTOR ROLE OPERATION *************************************/
	/**
	 * redirects page to uplaod tutorial under contributor role
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object(webpage)
	 */
	@RequestMapping(value = "/uploadTutorial", method = RequestMethod.GET)
	public String uploadTutorialGet(Model model,Principal principal) {
		User usr = getUser(principal, userService);
		model.addAttribute("userInfo", usr);

		List<String> catName = new ArrayList<String>();
		List<ContributorAssignedMultiUserTutorial> con=conMultiUser.getAllByuser(usr);

		for(ContributorAssignedMultiUserTutorial temp:con) {
			ContributorAssignedTutorial conTemp = conRepo.findById(temp.getConAssignedTutorial().getId());
			catName.add(conTemp.getTopicCatId().getCat().getCatName());
		}
		HashSet<String> uniqueCatName=new HashSet<String>(catName);    
		List<String> categories = new ArrayList<String>(uniqueCatName);
		Collections.sort(categories);
		model.addAttribute("contributorTutorial", categories);
		return "uploadTutorialPre";
	}


	private List<Category> getPreReqCategories(Language lan){
		List<Category> categories = new ArrayList<Category>();
		List<Category> preReqCategories = new ArrayList<Category>();
		Set<Category> preReqCatSet = new HashSet<Category>();
//		get all category
		categories = catService.findAll();
//		for each category; 
		for(Category category : categories) {
			if(category.isStatus()) {
				List<TopicCategoryMapping> tcm = topicCatService.findAllByCategory(category);
				List<ContributorAssignedTutorial> con_t = new ArrayList<ContributorAssignedTutorial>();
				if(!tcm.isEmpty()) {
					if(lan==null) {
						con_t = conRepo.findAllByTopicCat(tcm);
					}else {
						con_t = conRepo.findAllByTopicCatAndLanViewPart(tcm, lan);
					}
					for(ContributorAssignedTutorial c : con_t) {
						List<Tutorial> tut = tutService.findAllByContributorAssignedTutorial(c);
						if(!tut.isEmpty()) {
							if(tut.get(0).isStatus()) {
								preReqCatSet.add(category);
							}
						}
						
					}
				}
				
			}
		}
		for(Category c : preReqCatSet) {
			preReqCategories.add(c);
		}
		return preReqCategories;
	}
	/**
	 * load tutorial component page to add various component for the tutorial
	 * @param model Model object
	 * @param principal Principal object
	 * @param categoryName String object
	 * @param topicId int value
	 * @param langName String object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/uploadTutorial", method = RequestMethod.POST)
	public String uploadTutorialPost(Model model,Principal principal,
										@RequestParam(value="categoryName") String categoryName,
										@RequestParam(name="inputTopic") int topicId,
										@RequestParam(name="inputLanguage") String langName) {

		model.addAttribute("userInfo", getUser(principal, userService));
		Language lan=lanService.getByLanName(langName);
		model.addAttribute("language", lan);
		List<Category> categories = getPreReqCategories(lan);
		model.addAttribute("categories", categories);
		Category cat=catService.findBycategoryname(categoryName);
		model.addAttribute("category", cat);
		Topic topic=topicService.findById(topicId);
		model.addAttribute("topic", topic);
		
		TopicCategoryMapping topicCat=topicCatService.findAllByCategoryAndTopic(cat, topic);
		Tutorial tutorial = null;
		
		ContributorAssignedTutorial conTutorial=conRepo.findByTopicCatAndLanViewPart( topicCat, lan);
		List<Tutorial> tutorials=tutService.findAllByContributorAssignedTutorial(conTutorial);
		setCompStatus(model,tutorials);
		setEngLangStatus(model, lan);
		
		if(!lan.getLangName().equalsIgnoreCase("english")) {
			
			boolean goAhead = false;
			List<Tutorial> tutTemp = null ;
			List<ContributorAssignedTutorial> tempCon = conRepo.findByTopicCat(topicCat);
			List<TopicCategoryMapping> tcm_list = topicCatService.findAllByTopic(topic);
			
			for(TopicCategoryMapping tc : tcm_list) {
				List<ContributorAssignedTutorial> ct = conRepo.findByTopicCat(tc);
				for(ContributorAssignedTutorial x : ct) {
					if(x.getLan().getLangName().equalsIgnoreCase("english")) {
						goAhead =true;
						tutTemp = tutService.findAllByContributorAssignedTutorial(x);
						break;
					}
				}
			}
			
			if(goAhead == false) {
				model.addAttribute("error_msg", "Please add English version of tutorial first.");
				model.addAttribute("disable", true);
				return "uploadTutorialPost";
			}else {
				
				if(tutTemp.isEmpty()) {
					model.addAttribute("error_msg", "Please add English version of tutorial first.");
					model.addAttribute("disable", true);
					return "uploadTutorialPost";
					
				}else {
					for(Tutorial x : tutTemp) {
						if(!x.isStatus()) {
							model.addAttribute("error_msg", "Please publish English version of tutorial first.");
							model.addAttribute("disable", true);
							return "uploadTutorialPost";
						}
					}
				}
			}
		}
		if(!tutorials.isEmpty()) {
//			set video details
			setVideoInfo(model,tutorials);
			setCompComment(model, tutorials, comService);
			for(Tutorial local:tutorials) {
				tutorial = local;
				String sm_url = setScriptManagerUrl(model, scriptmanager_url, scriptmanager_path, tutorial, topic, lan, cat);
				model.addAttribute("sm_url", sm_url);
				model.addAttribute("tutorial", local);
				if(local.getPreRequisticStatus()!=0 ) {
					model.addAttribute("pre_req", setPreReqInfo(local));
				}
			}
		}
		return "uploadTutorialPost";
	}

	/**
	 * List all the tutorial for contributor  to review
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "listTutorialForContributorReview", method = RequestMethod.GET)
	public String listContributorReviewTutorialGet(Model model,Principal principal) {
		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
		
		Set<Tutorial> tutorials = new HashSet<Tutorial>();

		List<ContributorAssignedMultiUserTutorial> conTutorials=conMultiUser.getAllByuser(usr);
		
		for(ContributorAssignedMultiUserTutorial conMultiTemp : conTutorials) {
			
			ContributorAssignedTutorial conTemp = conRepo.findById(conMultiTemp.getConAssignedTutorial().getId());
			
			tutorials.addAll(tutService.findAllByContributorAssignedTutorial(conTemp));
		}

		//List<Tutorial> tutorials =  tutService.findAllByContributorAssignedTutorialList(conTutorials);

		model.addAttribute("tutorial", tutorials);

		return "listTutorialContributorReviwer";

	}

	/**
	 * redirect to contributor review page given tutorial id
	 * @param id int value
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "Contributor/review/{catName}/{topicName}/{language}", method = RequestMethod.GET)
	public String listContributorReviewTutorialGet(@PathVariable(name = "catName") String cat,
			@PathVariable (name = "topicName") String topic,
			@PathVariable (name = "language") String lan,Model model,Principal principal) {
		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}
		
		List<Category> categories = catService.findAll();
		model.addAttribute("categories", categories);
		model.addAttribute("userInfo", usr);
		
		Category catName = catService.findBycategoryname(cat);
		Topic topicName = topicService.findBytopicName(topic);
		Language lanName = lanService.getByLanName(lan);
		TopicCategoryMapping topicCatMap = topicCatService.findAllByCategoryAndTopic(catName, topicName);
		ContributorAssignedTutorial conTut = conRepo.findByTopicCatAndLanViewPart(topicCatMap, lanName);
		
		if(catName == null || topicName == null || lanName == null || topicCatMap == null || conTut == null) {
			return "redirect:/listTutorialForContributorReview";
		}
		
		Tutorial tutorial=tutService.findAllByContributorAssignedTutorial(conTut).get(0);

		if(tutorial == null) {
			// throw a error
			model.addAttribute("error_msg", CommonData.STATUS_ERROR);
			model.addAttribute("tutorialNotExist", "Bad request");   //  throw proper error
			return "redirect:/listTutorialForContributorReview";

		}

//		if(tutorial.getConAssignedTutorial().getUser().getId() != usr.getId()) {
//
//			return "redirect:/listTutorialForContributorReview";
//		}

		model.addAttribute("statusOutline", CommonData.tutorialStatus[tutorial.getOutlineStatus()]);
		model.addAttribute("statusScript", CommonData.tutorialStatus[tutorial.getScriptStatus()]);
		model.addAttribute("statusSlide", CommonData.tutorialStatus[tutorial.getSlideStatus()]);
		model.addAttribute("statusVideo", CommonData.tutorialStatus[tutorial.getVideoStatus()]);
		model.addAttribute("statusKeyword", CommonData.tutorialStatus[tutorial.getKeywordStatus()]);
		model.addAttribute("statusPreReq", CommonData.tutorialStatus[tutorial.getPreRequisticStatus()]);

		model.addAttribute("tutorial", tutorial);
		

		if(tutorial.getVideo() != null) {

		IContainer container = IContainer.make();
		int result=10;
		result = container.open(env.getProperty("spring.applicationexternalPath.name")+tutorial.getVideo(),IContainer.Type.READ,null);

		IStream stream = container.getStream(0);
		IStreamCoder coder = stream.getStreamCoder();
		
		model.addAttribute("FileWidth", coder.getWidth());
		model.addAttribute("FileHeight", coder.getHeight());
		

		model.addAttribute("fileSizeInMB", container.getFileSize()/1000000);
		model.addAttribute("FileDurationInSecond", container.getDuration()/1000000);
		
		container.close();

		}


		List<Comment> comVideo = comService.getCommentBasedOnTutorialType(CommonData.VIDEO, tutorial);
		List<Comment> comScript = comService.getCommentBasedOnTutorialType(CommonData.SCRIPT, tutorial);
		List<Comment> comSlide = comService.getCommentBasedOnTutorialType(CommonData.SLIDE, tutorial);

		List<Comment> comKeyword = comService.getCommentBasedOnTutorialType(CommonData.KEYWORD, tutorial);
		List<Comment> comPreRequistic = comService.getCommentBasedOnTutorialType(CommonData.PRE_REQUISTIC, tutorial);
		List<Comment> comOutline = comService.getCommentBasedOnTutorialType(CommonData.OUTLINE, tutorial);

		model.addAttribute("comOutline", comOutline);
		model.addAttribute("comScript",comScript );
		model.addAttribute("comSlide",comSlide );
		model.addAttribute("comVideo", comVideo);
		model.addAttribute("comKeyword", comKeyword);
		model.addAttribute("comPreReq", comPreRequistic);

		model.addAttribute("category", tutorial.getConAssignedTutorial().getTopicCatId().getCat());
		model.addAttribute("topic", tutorial.getConAssignedTutorial().getTopicCatId().getTopic());
		model.addAttribute("language", tutorial.getConAssignedTutorial().getLan());
		
		if(!tutorial.getConAssignedTutorial().getLan().getLangName().equalsIgnoreCase("english")) {
			model.addAttribute("otherLan", false);
		}else {
			model.addAttribute("otherLan", true);
		}

		return "uploadTutorialPost";



	}




	/****************************************END********************************************************/

/********************************** operation at Admin End *****************************************/
	
	/**
	 * List all the tutorial for Admin reviewer to review
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "listTutorialForAdminReview", method = RequestMethod.GET)
	public String listAdminReviewTutorialGet(Model model,Principal principal) {
		User usr = getUser(principal, userService);
		model.addAttribute("userInfo", usr);
		HashSet<Tutorial> toReview = new HashSet<>();
		HashSet<Tutorial> reviewed = new HashSet<>();
		Role role=roleService.findByname(CommonData.adminReviewerRole);

		List<UserRole> userRoles=usrRoleService.findByRoleUser(usr, role);
		List<TopicCategoryMapping> localMap=topicCatService.findAllByCategoryBasedOnUserRoles(userRoles);

		List<ContributorAssignedTutorial> conTutorials=conRepo.findByTopicCatLan(localMap, userRoles);

		List<Tutorial> tutorials =  tutService.findAllByContributorAssignedTutorialList(conTutorials);
		for(Tutorial temp:tutorials) {
			int videoStatus = temp.getVideoStatus();
			if(videoStatus == CommonData.ADMIN_STATUS) {
				toReview.add(temp);
				System.out.println(temp);
			}else if(videoStatus > CommonData.ADMIN_STATUS) {
				reviewed.add(temp);
			}
			
			if(temp.getTutorialId()==654)
				System.out.println("VideoStatus: " + videoStatus);
		}

		model.addAttribute("tutorialToReview", toReview);
		model.addAttribute("tutorialReviewed", reviewed);
		return "listTutorialAdminReviwer";
	}

	/**
	 * redirect to admin review page given tutorial id
	 * @param id int value
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "adminreview/review/{tutorialId}", method = RequestMethod.GET)
	public String listAdminReviewTutorialGet(@PathVariable int  tutorialId, Model model,Principal principal) {
		User usr = getUser(principal, userService);
		model.addAttribute("userInfo", usr);
		
		
		Tutorial tutorial1=tutService.getById(tutorialId);
		if(tutorial1 == null) {
			System.out.println(" problem 2");	
			return "redirect:/listTutorialForAdminReview";
		}
		
		Category catName = tutorial1.getConAssignedTutorial().getTopicCatId().getCat();
		Topic topicName = tutorial1.getConAssignedTutorial().getTopicCatId().getTopic();
		Language lanName = tutorial1.getConAssignedTutorial().getLan();
		TopicCategoryMapping topicCatMap = topicCatService.findAllByCategoryAndTopic(catName, topicName);
		ContributorAssignedTutorial conTut = conRepo.findByTopicCatAndLanViewPart(topicCatMap, lanName);
		
		if(catName == null || topicName == null || lanName == null || topicCatMap == null || conTut == null) {
			System.out.println(" problem 1");			return "redirect:/listTutorialForAdminReview";
		}
		List<Tutorial> tutorials = tutService.findAllByContributorAssignedTutorial(conTut);
		System.out.println(tutorials);
		
	

		if(tutorial1.getVideoStatus() != CommonData.ADMIN_STATUS) {
			
			return "redirect:/listTutorialForAdminReview";
		}

		model.addAttribute("category", tutorial1.getConAssignedTutorial().getTopicCatId().getCat().getCatName());
		model.addAttribute("topic", tutorial1.getConAssignedTutorial().getTopicCatId().getTopic().getTopicName());
		model.addAttribute("language", tutorial1.getConAssignedTutorial().getLan().getLangName());
		model.addAttribute("tutorial", tutorial1);
		
		List<Comment> comVideo = comService.getCommentBasedOnTutorialType(CommonData.VIDEO, tutorial1);
		model.addAttribute("comVideo", comVideo);
		setVideoInfo(model, tutorials);
		
		
		return "addContentAdminReview";



	}



	/***********************************END ***************************************************************/

	/*************************** OPERATION AT DOMAIN REVIEWER END ***********************************/
	
	/**
	 * List all the tutorial for domain reviewer to review
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "listTutorialForDomainReview", method = RequestMethod.GET)
	public String listDomainReviewTutorialGet(Model model,Principal principal) {
		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
		HashSet<Tutorial> toReview = new HashSet<>();
		HashSet<Tutorial> published = new HashSet<>();
		Role role=roleService.findByname(CommonData.domainReviewerRole);

		List<UserRole> userRoles=usrRoleService.findByRoleUser(usr, role);
		List<TopicCategoryMapping> localMap=topicCatService.findAllByCategoryBasedOnUserRoles(userRoles);

		List<ContributorAssignedTutorial> conTutorials=conRepo.findByTopicCatLan(localMap, userRoles);

		List<Tutorial> tutorials =  tutService.findAllByContributorAssignedTutorialList(conTutorials);
		for(Tutorial temp:tutorials) {

			if(temp.getOutlineStatus() > CommonData.DOMAIN_STATUS && temp.getScriptStatus() > CommonData.DOMAIN_STATUS &&
					temp.getSlideStatus() > CommonData.DOMAIN_STATUS && temp.getKeywordStatus() > CommonData.DOMAIN_STATUS &&
					temp.getVideoStatus() > CommonData.DOMAIN_STATUS &&
					temp.getPreRequisticStatus() > CommonData.DOMAIN_STATUS) {

				published.add(temp);
			}else {
				if(temp.getOutlineStatus() == CommonData.DOMAIN_STATUS || temp.getScriptStatus() == CommonData.DOMAIN_STATUS ||
						temp.getSlideStatus() == CommonData.DOMAIN_STATUS || temp.getKeywordStatus() == CommonData.DOMAIN_STATUS ||
						temp.getVideoStatus() == CommonData.DOMAIN_STATUS ||
						temp.getPreRequisticStatus() == CommonData.DOMAIN_STATUS) {

				
				toReview.add(temp);
			}

		}
		}

		model.addAttribute("tutorialToReview", toReview);
		model.addAttribute("tutorialReviewed", published);
		return "listTutorialDomainReviewer";
	}

	/**
	 * redirect to domain review page given tutorial id
	 * @param id int value
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "domainreview/review/{tutorialId}", method = RequestMethod.GET)
	public String listDomainReviewTutorialGet(@PathVariable int tutorialId, Model model,Principal principal) {
		User usr = getUser(principal, userService);
		model.addAttribute("userInfo", usr);
		
		Tutorial tutorial1= tutService.getById(tutorialId);

		if(tutorial1 == null) {
			return "redirect:/listTutorialForDomainReview";
		}
		
		Category category = tutorial1.getConAssignedTutorial().getTopicCatId().getCat();
		Topic topic =  tutorial1.getConAssignedTutorial().getTopicCatId().getTopic();
		Language language = tutorial1.getConAssignedTutorial().getLan();
		TopicCategoryMapping topicCatMap = topicCatService.findAllByCategoryAndTopic(category, topic);
		ContributorAssignedTutorial conTut = conRepo.findByTopicCatAndLanViewPart(topicCatMap, language);
		List<Tutorial> tutorials=tutService.findAllByContributorAssignedTutorial(conTut);
		
		model.addAttribute("category", tutorial1.getConAssignedTutorial().getTopicCatId().getCat().getCatName());
		model.addAttribute("topic", tutorial1.getConAssignedTutorial().getTopicCatId().getTopic().getTopicName());
		model.addAttribute("language", tutorial1.getConAssignedTutorial().getLan().getLangName());
		
		if(category == null || topic == null || language == null || topicCatMap == null || conTut == null) {
			return "redirect:/listTutorialForDomainReview";
		}
		
		

		setCompComment(model, tutorials, comService);
		setCompStatus(model, tutorials);
		setVideoInfo(model, tutorials);
		model.addAttribute("tutorial", tutorial1);
		setEngLangStatus(model, language);
		String sm_url = setScriptManagerUrl(model, scriptmanager_url, scriptmanager_path, tutorial1, topic, language, category);
		model.addAttribute("sm_url", sm_url);
		
		return "addContentDomainReview";



	}

	/*********************************** END *******************************************************/

	/*************************** OPERATION AT QUALITY REVIEWER END ***********************************/

	/**
	 * List all the tutorial for quality review
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "listTutorialForQualityReview", method = RequestMethod.GET)
	public String listQualityReviewTutorialGet(Model model,Principal principal) {
		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
		HashSet<Tutorial> toReview = new HashSet<>();
		HashSet<Tutorial> published = new HashSet<>();
		Role role=roleService.findByname(CommonData.qualityReviewerRole);

		List<UserRole> userRoles=usrRoleService.findByRoleUser(usr, role);
		List<TopicCategoryMapping> localMap=topicCatService.findAllByCategoryBasedOnUserRoles(userRoles);

		List<ContributorAssignedTutorial> conTutorials=conRepo.findByTopicCatLan(localMap, userRoles);

		List<Tutorial> tutorials =  tutService.findAllByContributorAssignedTutorialList(conTutorials);
		for(Tutorial temp:tutorials) {

			if(temp.getOutlineStatus() == CommonData.PUBLISH_STATUS && temp.getScriptStatus() == CommonData.PUBLISH_STATUS &&
					temp.getSlideStatus() == CommonData.PUBLISH_STATUS && temp.getKeywordStatus() == CommonData.PUBLISH_STATUS &&
					temp.getVideoStatus() == CommonData.PUBLISH_STATUS  &&
					temp.getPreRequisticStatus() == CommonData.PUBLISH_STATUS) {

				published.add(temp);
			}
			
			else {
				if(temp.getOutlineStatus() > CommonData.DOMAIN_STATUS || temp.getScriptStatus() > CommonData.DOMAIN_STATUS ||
						temp.getSlideStatus() > CommonData.DOMAIN_STATUS || temp.getKeywordStatus() > CommonData.DOMAIN_STATUS ||
						temp.getVideoStatus() > CommonData.DOMAIN_STATUS  ||
						temp.getPreRequisticStatus() > CommonData.DOMAIN_STATUS) {
					
					toReview.add(temp);
				}
				
				
			}

		}

		model.addAttribute("tutorialToReview", toReview);
		model.addAttribute("tutorialReviewed", published);

		return "listTutorialQualityReviewer";


	}

	/**
	 * redirects page to view all the tutorial to be published
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "tutorialToPublish", method = RequestMethod.GET)
	public String tutorialToPublishGet(Model model,Principal principal) {
		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
		HashSet<Tutorial> published = new HashSet<>();
		Role role=roleService.findByname(CommonData.qualityReviewerRole);

		List<UserRole> userRoles=usrRoleService.findByRoleUser(usr, role);
		List<TopicCategoryMapping> localMap=topicCatService.findAllByCategoryBasedOnUserRoles(userRoles);

		List<ContributorAssignedTutorial> conTutorials=conRepo.findByTopicCatLan(localMap, userRoles);

		List<Tutorial> tutorials =  tutService.findAllByContributorAssignedTutorialList(conTutorials);
		for(Tutorial temp:tutorials) {
			
			
			
			
			if(temp.getOutlineStatus() >= CommonData.WAITING_PUBLISH_STATUS && temp.getScriptStatus() >= CommonData.WAITING_PUBLISH_STATUS &&
					temp.getSlideStatus() >= CommonData.WAITING_PUBLISH_STATUS && temp.getKeywordStatus() >= CommonData.WAITING_PUBLISH_STATUS &&
					temp.getVideoStatus() >= CommonData.WAITING_PUBLISH_STATUS &&
					temp.getPreRequisticStatus() >= CommonData.WAITING_PUBLISH_STATUS) {
				
				published.add(temp);
			}

		}

		model.addAttribute("tutorial", published);

		return "listTutorialPublishQualityReviwer";


	}
	//getStreamCoder()

	/**
	 * publish the tutorial under quality role
	 * @param id int value
	 * @param model Model object
	 * @param principal principal object
	 * @param redirectAttributes RedirectAttributes object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "publish/{id}", method = RequestMethod.GET)
	public String publishTutorialGet(@PathVariable int id,Model model,Principal principal,RedirectAttributes redirectAttributes) {
		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
		Tutorial tutorial=tutService.getById(id);

		if(tutorial == null) {
			// throw a error
			model.addAttribute("tutorialNotExist", "Bad request");   //  throw proper error
			return "redirect:/tutorialToPublish";

		}

		tutorial.setKeywordStatus(CommonData.PUBLISH_STATUS);
		tutorial.setOutlineStatus(CommonData.PUBLISH_STATUS);
		tutorial.setSlideStatus(CommonData.PUBLISH_STATUS);
		tutorial.setScriptStatus(CommonData.PUBLISH_STATUS);
		tutorial.setPreRequisticStatus(CommonData.PUBLISH_STATUS);
		tutorial.setVideoStatus(CommonData.PUBLISH_STATUS);
		tutorial.setStatus(true);

		tutService.save(tutorial);
		model.addAttribute("success_msg", CommonData.PUBLISHED_SUCCESS);
		
		HashSet<Tutorial> published = new HashSet<>();
		Role role=roleService.findByname(CommonData.qualityReviewerRole);

		List<UserRole> userRoles=usrRoleService.findByRoleUser(usr, role);
		List<TopicCategoryMapping> localMap=topicCatService.findAllByCategoryBasedOnUserRoles(userRoles);

		List<ContributorAssignedTutorial> conTutorials=conRepo.findByTopicCatLan(localMap, userRoles);

		List<Tutorial> tutorials =  tutService.findAllByContributorAssignedTutorialList(conTutorials);
		for(Tutorial temp:tutorials) {

			if(temp.getOutlineStatus() >= CommonData.WAITING_PUBLISH_STATUS && temp.getScriptStatus() >= CommonData.WAITING_PUBLISH_STATUS &&
					temp.getSlideStatus() >= CommonData.WAITING_PUBLISH_STATUS && temp.getKeywordStatus() >= CommonData.WAITING_PUBLISH_STATUS &&
					temp.getVideoStatus() >= CommonData.WAITING_PUBLISH_STATUS &&
					temp.getPreRequisticStatus() >= CommonData.WAITING_PUBLISH_STATUS) {

				published.add(temp);
			}

		}

		model.addAttribute("tutorial", published);
		
	
        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.upload");

        try {
            
            Credential credential = Auth.authorize(scopes, "uploadvideo");

           
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential).build();


            
            Video videoObjectDefiningMetadata = new Video();

            VideoStatus status = new VideoStatus();
            status.setPrivacyStatus("public");
            videoObjectDefiningMetadata.setStatus(status);

            VideoSnippet snippet = new VideoSnippet();

            Calendar cal = Calendar.getInstance();
            snippet.setTitle("sample" + cal.getTime());
            snippet.setDescription(
                    "Video uploaded via YouTube Data API V3 using the Java library " + "on " + cal.getTime());
            
            List<String> tags = new ArrayList<String>();
            tags.add("test");
            tags.add("example");
            tags.add("java");
            tags.add("YouTube Data API V3");
            tags.add("erase me");
            snippet.setTags(tags);

            videoObjectDefiningMetadata.setSnippet(snippet);
            
            InputStream sam = new FileInputStream(env.getProperty("spring.applicationexternalPath.name")+tutorial.getVideo());

            InputStreamContent mediaContent = new InputStreamContent(VIDEO_FILE_FORMAT,sam);

            YouTube.Videos.Insert videoInsert = youtube.videos()
                    .insert("snippet,statistics,status", videoObjectDefiningMetadata, mediaContent);

            MediaHttpUploader uploader = videoInsert.getMediaHttpUploader();

            uploader.setDirectUploadEnabled(false);

            MediaHttpUploaderProgressListener progressListener = new MediaHttpUploaderProgressListener() {
                public void progressChanged(MediaHttpUploader uploader) throws IOException {
                    switch (uploader.getUploadState()) {
                        case INITIATION_STARTED:
                            System.out.println("Initiation Started");
                            break;
                        case INITIATION_COMPLETE:
                            System.out.println("Initiation Completed");
                            break;
                        case MEDIA_IN_PROGRESS:
                            System.out.println("Upload in progress");
                            System.out.println("Upload percentage: " + uploader.getProgress());
                            break;
                        case MEDIA_COMPLETE:
                            System.out.println("Upload Completed!");
                            break;
                        case NOT_STARTED:
                            System.out.println("Upload Not Started!");
                            break;
                    }
                }
            };
            uploader.setProgressListener(progressListener);

           
            Video returnedVideo = videoInsert.execute();

            System.out.println("\n================== Returned Video ==================\n");
            System.out.println("  - Id: " + returnedVideo.getId());
            System.out.println("  - Title: " + returnedVideo.getSnippet().getTitle());
            System.out.println("  - Tags: " + returnedVideo.getSnippet().getTags());
            System.out.println("  - Privacy Status: " + returnedVideo.getStatus().getPrivacyStatus());
            System.out.println("  - Video Count: " + returnedVideo.getStatistics().getViewCount());

        } catch (GoogleJsonResponseException e) {
            System.err.println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
        } catch (Throwable t) {
            System.err.println("Throwable: " + t.getMessage());
            t.printStackTrace();
        }
        
        
        return "listTutorialPublishQualityReviwer";
		
		
	}

	/**
	 * redirects to quality review page given tutorial id
	 * @param id int value
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object(webpage)
	 */
	@RequestMapping(value = "qualityreview/review/{tutorialId}", method = RequestMethod.GET)
	public String listQualityReviewTutorialGet(@PathVariable int tutorialId, Model model, Principal principal) {
		
		User usr = getUser(principal, userService);
		model.addAttribute("userInfo", usr);
		
		Tutorial tutorial1= tutService.getById(tutorialId);
		
		Category category = tutorial1.getConAssignedTutorial().getTopicCatId().getCat();
		Topic topic = tutorial1.getConAssignedTutorial().getTopicCatId().getTopic();
		Language language = tutorial1.getConAssignedTutorial().getLan();
		TopicCategoryMapping topicCatMap = topicCatService.findAllByCategoryAndTopic(category, topic);
		ContributorAssignedTutorial conTut = conRepo.findByTopicCatAndLanViewPart(topicCatMap, language);
		
		if(category == null || topic == null || language == null || topicCatMap == null || conTut == null) {
			return "redirect:/listTutorialForQualityReview";
		}
		
		List<Tutorial> tutorials =tutService.findAllByContributorAssignedTutorial(conTut);
		if(tutorials == null) {
			return "redirect:/listTutorialForQualityReview";
		}

		
		model.addAttribute("category", tutorial1.getConAssignedTutorial().getTopicCatId().getCat().getCatName());
		model.addAttribute("topic", tutorial1.getConAssignedTutorial().getTopicCatId().getTopic().getTopicName());
		model.addAttribute("language", tutorial1.getConAssignedTutorial().getLan().getLangName());
		
		model.addAttribute("tutorial", tutorial1);
		setCompComment(model, tutorials, comService);
		setCompStatus(model, tutorials);
		setVideoInfo(model, tutorials);
		setEngLangStatus(model, language);
		String sm_url = setScriptManagerUrl(model, scriptmanager_url, scriptmanager_path, tutorial1, topic, language, category);
		model.addAttribute("sm_url", sm_url);
		
		return "addContentQualityReview";



	}

	/*********************************** END *******************************************************/

	/************************* OPERATION AT MASTER TRAINER ******************************************/

	@RequestMapping(value = "/trainerProfile", method = RequestMethod.GET)
	public String profileMasterTrainerGet(Model model,Principal principal) {
		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		return "traineeView";



	}

	/**
	 * redirects to master trainer operation page
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object(webpage)
	 */
	@RequestMapping(value = "/masterTrainerOperation", method = RequestMethod.GET)
	public String MasterTrainerGet(Model model,Principal principal) {
		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		List<Event> events = eventservice.findByUser(usr);

		List<Category> cat=catService.findAll();

		List<State> states=stateService.findAll();

		List<Language> lan=lanService.getAllLanguages();

		model.addAttribute("categories", cat);

		model.addAttribute("states", states);
		model.addAttribute("lans", lan);

		model.addAttribute("events", events);

		return "masterTrainerOperation";

	}

	/**
	 * redirects page to view master trainer details
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object(webpage)
	 */
	@RequestMapping(value = "/details", method = RequestMethod.GET)
	public String MasterTrainerDetailsGet(Model model,Principal principal) {
		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		List<PostQuestionaire> postQuestionnaire = postQuestionService.findByUser(usr);
		List<FeedbackMasterTrainer> feedbackMasterTrainer = feedServ.findByUser(usr);
		List<TrainingInformation> trainingInfo = trainingInfoService.findByUser(usr);
		model.addAttribute("postQuestionnaire", postQuestionnaire);
		model.addAttribute("feedbackMasterTrainer", feedbackMasterTrainer);
		model.addAttribute("trainingInfo", trainingInfo);

		return "masterTrainerViewDetails";

	}

	/**
	 * Download question 
	 * @param model Model object
	 * @param principal Principal object
	 * @param catName int value
	 * @param topicId int value
	 * @param lanName String object
	 * @return String object(webpage)
	 */
	@RequestMapping(value = "/downloadQuestion", method = RequestMethod.POST)
	public String downloadQuestionPost(Model model,Principal principal,
										@RequestParam(value="catMasterId") int catName,
										@RequestParam(value="lanMasterTrId") int topicId,
										@RequestParam(value="dwnByLanguageId") String lanName) {
		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		Category cat=catService.findByid(catName);
		Topic topic=topicService.findById(topicId);
		TopicCategoryMapping topicCat=topicCatService.findAllByCategoryAndTopic(cat, topic);
		Language lan = lanService.getByLanName(lanName);
		Question questions = questService.getQuestionBasedOnTopicCatAndLan(topicCat, lan);
		model.addAttribute("post", "true");
		model.addAttribute("Questions", questions);

		List<Category> cats=catService.findAll();

		List<State> states=stateService.findAll();

		List<Language> lans=lanService.getAllLanguages();

		model.addAttribute("categories", cats);

		model.addAttribute("states", states);
		model.addAttribute("lans", lans);
		model.addAttribute("question", "question");

		return "masterTrainerOperation";

	}

	/**
	 * redirects page to view all the trainee
	 * @param model Model object
	 * @param principal Principal Object
	 * @return String object(webpage)
	 */
	@RequestMapping(value = "/viewTrainee", method = RequestMethod.GET)
	public String downloadQuestionPost(Model model,Principal principal) {
		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
		List<TraineeInformation> trainees = traineeService.findAll();
		List<Category> categories=catService.findAll();

		model.addAttribute("TraineesData", trainees);
		model.addAttribute("categories", categories);

		return "traineeView";

	}

	/**
	 * redirects to edit trainee information given trainee id
	 * @param id int value
	 * @param model model object
	 * @param principal principal object
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/trainee/edit/{id}", method = RequestMethod.GET)
	public String editTraineeGet(@PathVariable int id,Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		TraineeInformation trainee=traineeService.findById(id);
		
		if(trainee == null) {

			 return "redirect:/viewTrainee";
		}

		if(trainee.getTraineeInfos().getUser().getId() != usr.getId()) {

			 return "redirect:/viewTrainee";
		}

		model.addAttribute("trainee",trainee);

		return "editTrainee";  // need to accomdate view part
	}

	/**
	 * update trainee information 
	 * @param model Model object
	 * @param principal Principal object
	 * @param req HttpServletRequest object
	 * @return String object (Webpage)
	 */
	@RequestMapping(value = "/updateTrainee", method = RequestMethod.POST)
	public String editTraineeGet(Model model,Principal principal,HttpServletRequest req) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		String Id=req.getParameter("traineeId");
		String name = req.getParameter("name");
		String age = req.getParameter("age");
		String email = req.getParameter("email");
		String gender = req.getParameter("gender");
		String aadhar = req.getParameter("aadhar");
		String org = req.getParameter("org");
		String phone=req.getParameter("contactnumber");

		TraineeInformation trainee=traineeService.findById(Integer.parseInt(Id));

		model.addAttribute("trainee",trainee);

		if(aadhar.length()!=12) {
			 // throw error
			model.addAttribute("error_msg", "Invalid aadhar number");
			return "editTrainee";
		}

		if(phone.length()!=10) {

			// throw error
			model.addAttribute("error_msg", "Invalid phone number");
			return "editTrainee";
		}

		if(!ServiceUtility.checkEmailValidity(email)) {   // need to accommodate

			model.addAttribute("error_msg", "E-mail Wrong");
			return "editTrainee";
		}

		trainee.setAadhar(Long.parseLong(aadhar));
		trainee.setAge(Integer.parseInt(age));
		trainee.setEmail(email);
		trainee.setGender(gender);
		trainee.setOrganization(org);
		trainee.setPhone(Long.parseLong(phone));
		trainee.setName(name);

		traineeService.save(trainee);

		model.addAttribute("trainee",trainee);
		model.addAttribute("success_msg",CommonData.RECORD_UPDATE_SUCCESS_MSG);

		return "editTrainee";  // need to accomdate view part
	}

	/**
	 * Add training information into object
	 * @param model Model object
	 * @param principal Principal object
	 * @param trainingImage MultipartFile object 
	 * @param traineeInfo MultipartFile object
	 * @param eventId int value
	 * @param trainingInformation String object
	 * @param totaltrainee int value
	 * @return String object (webpage)
	 */
	@RequestMapping(value = "/addTrainingInfo", method = RequestMethod.POST)
	public String addTrainingInfoPost(Model model,Principal principal,
			@RequestParam("ParticipantsPhoto") MultipartFile trainingImage,
			@RequestParam("traineeInformation") MultipartFile traineeInfo,
			@RequestParam(value="event") int eventId,
			@RequestParam(value="traningInfo") String trainingInformation,
			@RequestParam(value = "totalPar") int totaltrainee) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		Event event = eventservice.findById(eventId);

//		Set<TrainingTopic> trainingTopicTemp = new HashSet<>();

		if(!ServiceUtility.checkFileExtensiononeFileCSV(traineeInfo)) {

			// throw error on output
			model.addAttribute("error_msg",CommonData.CSV_ERROR);
			return "masterTrainerOperation";
		}

		if(!ServiceUtility.checkFileExtensionZip(trainingImage)) {

			// throw error on output
			model.addAttribute("error_msg",CommonData.ZIP_ERROR);
			return "masterTrainerOperation";
		}

		int newTrainingdata=trainingInfoService.getNewId();
		TrainingInformation trainingData=new TrainingInformation();
		trainingData.setDateAdded(ServiceUtility.getCurrentTime());
		trainingData.setTrainingId(newTrainingdata);
		trainingData.setTotalParticipant(totaltrainee);
		trainingData.setUser(usr);
		trainingData.setEvent(event);

		try {
			trainingInfoService.save(trainingData);
			int trainingTopicId=trainingTopicServ.getNewId();
			trainingInfoService.save(trainingData);

				ServiceUtility.createFolder(env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadDirectoryMasterTrainer+newTrainingdata);
				String pathtoUploadPoster=ServiceUtility.uploadFile(trainingImage, env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadDirectoryMasterTrainer+newTrainingdata);
				int indexToStart=pathtoUploadPoster.indexOf("Media");

				String document=pathtoUploadPoster.substring(indexToStart, pathtoUploadPoster.length());

				trainingData.setPosterPath(document);

				byte[] bytes = traineeInfo.getBytes();
	            String completeData = new String(bytes);
	            System.out.println(completeData);
	            String[] rows = completeData.split("\n");
	            System.out.println("data"+rows.length);

	            Set<TraineeInformation> trainees=new HashSet<TraineeInformation>();
	            int newTraineeId=traineeService.getNewId();

	            for(int i=0;i<rows.length;i++) {
	            	String[] columns = rows[i].split(",");
	            	TraineeInformation temp=new TraineeInformation(newTraineeId++, columns[0], columns[1], Long.parseLong(columns[2]),Integer.parseInt(columns[3]), Long.parseLong(columns[4]), columns[5], columns[6], trainingData);
	            	trainees.add(temp);
	            }

	            trainingInfoService.addTrainee(trainingData, trainees);





		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			 // throw a error
			model.addAttribute("error_msg",CommonData.EVENT_ERROR);
			return "masterTrainerOperation";
		}


		model.addAttribute("success_msg",CommonData.EVENT_SUCCESS);
		return "masterTrainerOperation";

	}

	/**
	 * upload feedback given by master trainer
	 * @param model Model object
	 * @param principal Principal object
	 * @param catId int value
	 * @param trainingTitle int value
	 * @param feedbackFile MultipartFile object
	 * @param desc String object
	 * @return String object (Webpage)
	 */
	@RequestMapping(value = "/uploadfeedback", method = RequestMethod.POST)
	public String uploadFeedbackPost(Model model,Principal principal,
								@RequestParam(value = "catMasId") int catId,
								@RequestParam(value = "feedbackmasterId") int trainingTitle,
								@RequestParam(value = "feedbackForm") MultipartFile feedbackFile,
								@RequestParam(value = "traningInformation") String desc) {
		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
		List<Category> cats=catService.findAll();

		List<State> states=stateService.findAll();

		List<Language> lan=lanService.getAllLanguages();

		model.addAttribute("categories", cats);

		model.addAttribute("states", states);
		model.addAttribute("lans", lan);

		if(!ServiceUtility.checkFileExtensionZip(feedbackFile)) {

			model.addAttribute("error_msg",CommonData.ZIP_ERROR);								// Accommodate error message
			return "masterTrainerOperation";
		}

		TrainingInformation trainingInfo = trainingInfoService.getById(trainingTitle);

		FeedbackMasterTrainer feed = new FeedbackMasterTrainer(feedServ.getNewId(), desc, ServiceUtility.getCurrentTime(), null, trainingInfo, usr);
		try {
			feedServ.save(feed);

				ServiceUtility.createFolder(env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadDirectoryMasterTrainerFeedback+feed.getId());
				String pathtoUploadPoster=ServiceUtility.uploadFile(feedbackFile, env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadDirectoryMasterTrainerFeedback+feed.getId());
				int indexToStart=pathtoUploadPoster.indexOf("Media");

				String document=pathtoUploadPoster.substring(indexToStart, pathtoUploadPoster.length());

				feed.setPath(document);
				feedServ.save(feed);


		} catch (Exception e) {
			// TODO Auto-generated catch block
			model.addAttribute("error_msg",CommonData.EVENT_ERROR);
			e.printStackTrace();
		}


		model.addAttribute("success_msg",CommonData.EVENT_SUCCESS);
		return "masterTrainerOperation";

	}


	/**
	 * upload post questionnaire under master trainer role
	 * @param model Model object
	 * @param principal Principal object
	 * @param catId int value
	 * @param trainingTitle int value
	 * @param postQuestions MultipartFile object
	 * @return String object(Webpage)
	 */
	@RequestMapping(value = "/uploadPostQuestionaire", method = RequestMethod.POST)
	public String uploadQuestionPost(Model model,Principal principal,
								@RequestParam(value = "catMasPostId") int catId,
								@RequestParam(value = "postTraining") int trainingTitle,
								@RequestParam(value = "postQuestions") MultipartFile postQuestions) {
		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
		List<Category> cats=catService.findAll();

		List<State> states=stateService.findAll();

		List<Language> lan=lanService.getAllLanguages();

		model.addAttribute("categories", cats);

		model.addAttribute("states", states);
		model.addAttribute("lans", lan);

		if(!ServiceUtility.checkFileExtensionZip(postQuestions)) {

			model.addAttribute("error_msg",CommonData.ZIP_ERROR);								// Accommodate error message
			return "masterTrainerOperation";
		}

		TrainingInformation trainingInfo = trainingInfoService.getById(trainingTitle);

		PostQuestionaire feed = new PostQuestionaire(postQuestionService.getNewCatId(), null, ServiceUtility.getCurrentTime(), trainingInfo, usr);
		try {
			postQuestionService.save(feed);

				ServiceUtility.createFolder(env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadPostQuestion+feed.getId());
				String pathtoUploadPoster=ServiceUtility.uploadFile(postQuestions, env.getProperty("spring.applicationexternalPath.name")+CommonData.uploadPostQuestion+feed.getId());
				int indexToStart=pathtoUploadPoster.indexOf("Media");

				String document=pathtoUploadPoster.substring(indexToStart, pathtoUploadPoster.length());

				feed.setQuestionPath(document);
				postQuestionService.save(feed);


		} catch (Exception e) {
			// TODO Auto-generated catch block
			model.addAttribute("error_msg",CommonData.EVENT_ERROR);
			e.printStackTrace();
			return "masterTrainerOperation";
		}


		model.addAttribute("success_msg",CommonData.EVENT_SUCCESS);
		return "masterTrainerOperation";

	}


	/************************ DOMAIN ROLE CONSULTANT MAPPING *************************************/

	/**
	 * redirects to admin role request page
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object(Webpage)
	 */
	@RequestMapping(value = "/assignRoleToAdmin" , method = RequestMethod.GET)
	public String assignRoleToDomainGet(Model model,Principal principal) {
		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		List<Category> categories = catService.findAll();
		List<Language> languages = lanService.getAllLanguages();

		model.addAttribute("categories", categories);

		model.addAttribute("languages", languages);

		return "assignRoleToDomain"; // add html page

	}

	/**
	 * Assign role to admin given category name and language name
	 * @param model Model object
	 * @param principal Principal object
	 * @param cat String object
	 * @param lan String object
	 * @return String object(Webpage)
	 */
	@RequestMapping(value = "/assignRoleToAdmin" , method = RequestMethod.POST)
	public String assignRoleToDomainPost(Model model,Principal principal,
									@RequestParam(value = "category") String cat,
									@RequestParam(value = "language") String lan) {
		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		List<Category> categories = catService.findAll();
		List<Language> languages = lanService.getAllLanguages();

		model.addAttribute("categories", categories);
		model.addAttribute("languages", languages);

		Category category = catService.findBycategoryname(cat);
		Language language = lanService.getByLanName(lan);
		
		if(language == null) {
			model.addAttribute("error_msg", "Please select language");

			return "assignRoleToDomain";
		}
		
		if(category == null) {
			model.addAttribute("error_msg", "Please select Category");

			return "assignRoleToDomain";
		}
		
		Role role = roleService.findByname(CommonData.adminReviewerRole);

		if(usrRoleService.findByLanCatUser(language, category, usr, role)!=null) {
			model.addAttribute("error_msg", CommonData.DUPLICATE_ROLE_ERROR);
			return "assignRoleToDomain";
		}

		UserRole usrRole=new UserRole();
		usrRole.setCreated(ServiceUtility.getCurrentTime());
		usrRole.setUser(usr);
		usrRole.setRole(role);
		usrRole.setLanguage(language);
		usrRole.setCategory(category);
		usrRole.setUserRoleId(usrRoleService.getNewUsrRoletId());

		try {
			usrRoleService.save(usrRole);

//			SimpleMailMessage newEmail = mailConstructor.domainRoleMailSend(usrTemp);
//
//			mailSender.send(newEmail);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			model.addAttribute("error_msg", CommonData.ROLE_ERROR_MSG);
			e.printStackTrace();
			return "assignRoleToDomain";				// accommodate error message
		}


		model.addAttribute("success_msg", CommonData.ADMIN_REVIEWER_REQ);


		return "assignRoleToDomain"; // add html page

	}


	/****************************** END ***********************************************************/

	/**
	 * redirects to profile page
	 * @param model Model object
	 * @param principal Principal object
	 * @return String object(webpage)
	 */
	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	public String profileUserGet(Model model,Principal principal) {
		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		return "profileView";

	}

	/**
	 * Revoke role from user interface
	 * @param usrRoleId long value
	 * @param principal Principal object
	 * @param model Model object
	 * @return String object (Webpage)
	 */
	@GetMapping("/revokeRole/{usrRoleId}")
	public String revokeRoleByRole(@PathVariable long usrRoleId,Principal principal,Model model){

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		UserRole usrRole = usrRoleService.findById(usrRoleId);

		if(usrRole.getUser().getId() == usr.getId()) {
			usrRole.setStatus(false);
			usrRole.setRevoked(true);
			usrRoleService.save(usrRole);

			model.addAttribute("success_msg", "Role revoked Successfully");
		}else {
			model.addAttribute("error_msg", "Wrong operation");
		}

		model.addAttribute("userInfo", usr);

		return "revokeRole";
	}

	/************************ PROFILE UPDATE SECTION *****************************************/
	
	/**
	 * update profile data on user object
	 * @param req HttpServletRequest object
	 * @param model Model object
	 * @param principal Principal object
	 * @param firstName String object
	 * @param lastName String object
	 * @param phone String object
	 * @param address String object
	 * @param dob String object
	 * @param desc String object
	 * @return String object(Webpage)
	 */
	@RequestMapping(value = "/profile", method = RequestMethod.POST)
	public String updateuserdataPost(HttpServletRequest req,Model model,Principal principal,
			 						@ModelAttribute("firstName") String firstName,
			 						@ModelAttribute("lastName") String lastName,
			 						@ModelAttribute("phone") String phone,
			 						@ModelAttribute("address") String address,
			 						@ModelAttribute("birthday") String dob,
			 						@ModelAttribute("description") String desc) {
		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}
		model.addAttribute("userInfo", usr);

		long phoneLongValue ;
		if(phone.length()>10) {								// need to accommodate

			model.addAttribute("error_msg", "Entered Mobile Number is not of 10 digit");
			return "profileView";

		}else {
			phoneLongValue = Long.parseLong(phone);

		}


		try {
			usr.setFirstName(firstName);
			usr.setLastName(lastName);
			usr.setAddress(address);
			usr.setPhone(phoneLongValue);
			usr.setDob(ServiceUtility.convertStringToDate(dob));

			userService.save(usr);

			if(!desc.isEmpty()) {
				Consultant consult = consultService.findByUser(usr);
				consult.setDescription(desc);
				consultService.save(consult);
			}

			model.addAttribute("success_msg", "Data Updated Successfully");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			model.addAttribute("error_msg", "Please Try Later");
		}

		usr=userService.findByUsername(principal.getName());
		model.addAttribute("userInfo", usr);
		return "profileView";

	}

	/**
	 * redirects to revoke role page under Contributor role
	 * @param principal Principal Object
	 * @param model Model object
	 * @return String object(webpage)
	 */
	@GetMapping("/revokeRoleContributor")
	public String revokeRoleByContributor(Principal principal,Model model){

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		Role role = roleService.findByname(CommonData.contributorRole);

		List<UserRole> userRoles = usrRoleService.findAllByRoleUserStatus(role, usr, true);

		model.addAttribute("userRoles", userRoles);

		return "revokeRole";
	}

	/**
	 * redirects to revoke role page under Admin role
	 * @param principal Principal Object
	 * @param model Model object
	 * @return String object(webpage)
	 */
	@GetMapping("/revokeRoleDomain")
	public String revokeRoleByDomain(Principal principal,Model model){

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		Role role = roleService.findByname(CommonData.domainReviewerRole);

		List<UserRole> userRoles = usrRoleService.findAllByRoleUserStatus(role, usr, true);

		model.addAttribute("userRoles", userRoles);

		return "revokeRole";
	}

	/**
	 * redirects to revoke role page under Quality role
	 * @param principal Principal Object
	 * @param model Model object
	 * @return String object(webpage)
	 */
	@GetMapping("/revokeRoleQuality")
	public String revokeRoleBYQuality(Principal principal,Model model){

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		Role role = roleService.findByname(CommonData.qualityReviewerRole);

		List<UserRole> userRoles = usrRoleService.findAllByRoleUserStatus(role, usr, true);

		model.addAttribute("userRoles", userRoles);

		return "revokeRole";
	}

	/**
	 * redirects to revoke role page under master role
	 * @param principal Principal Object
	 * @param model Model object
	 * @return String object(webpage)
	 */
	@GetMapping("/revokeRoleMaster")
	public String revokeRoleMaster(Principal principal,Model model){

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		Role role = roleService.findByname(CommonData.masterTrainerRole);

		List<UserRole> userRoles = usrRoleService.findAllByRoleUserStatus(role, usr, true);

		model.addAttribute("userRoles", userRoles);

		return "revokeRole";
	}

	/**
	 * redirects to brochure page
	 * @param principal Principal Object
	 * @param model Model object
	 * @return String object(webpage)
	 */
	
	

	/* to generate img from 1st page of pdf */
	
    private static final String IMAGE_FORMAT = "png";

    public String generateImageFromPdfAndSave(String pdfFilePath, String outputFolderPath) throws IOException {
    	logger.info(pdfFilePath + " " + outputFolderPath);
        String pathName = env.getProperty("spring.applicationexternalPath.name");
		try (PDDocument document = PDDocument.load(new File(pathName + pdfFilePath))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage image = pdfRenderer.renderImageWithDPI(0, 15); // Render the first page with 300 DPI
            
            logger.info("Image Width and Height:{} {}", image.getWidth(), image.getHeight());
            
            if(image.getHeight()>200) {
            	int newDPI = (int) Math.ceil(15.0 * 200 / image.getHeight());
            	if (newDPI > 2 && newDPI < 15) {
            		image = pdfRenderer.renderImageWithDPI(0, newDPI);
            		logger.info("After setting dpi {}, Width and Height of Image: {} {}", newDPI, image.getWidth(), image.getHeight());
            	}
            }
            
            // Save the image to the output folder
            String fileName = outputFolderPath + "/" + "thumbnail.png";
            File outputFile = new File(pathName, fileName);
            ImageIO.write(image, "png", outputFile);

            // Convert the byte array to a Base64-encoded string
           // String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            return fileName;
        }
    }
    
    
	
	
	
	@GetMapping("/brochures")
	public String brochure(Principal principal,Model model){

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		/*List<Category> cat = catService.findAll();

		Map<Category, List<TopicCategoryMapping>> dataToSend = new HashMap<Category, List<TopicCategoryMapping>>();

		for(Category temp : cat) {
			List<TopicCategoryMapping> tempTopic = topicCatService.findAllByCategory(temp);
			dataToSend.put(temp, tempTopic);

		}
		
		model.addAttribute("brochuresData", dataToSend);
		
		*/
		
		List<Brouchure> brouchures= broService.findAllBrouchuresForCache();
		List<Language> languages= lanService.getAllLanguages();
		List<Version> allVersions=verService.findAll();
		List<Version> versions= new ArrayList<Version>();
		for(Brouchure bro: brouchures) {
				Version ver = verService.findByBrouchureAndPrimaryVersion(bro, bro.getPrimaryVersion());
				versions.add(ver);
					
		}
		Collections.sort(versions, Version.SortByBroVersionTime);
		
		List<FilesofBrouchure> filesList= new ArrayList<>();
		for(Version ver1: versions) {
			for(Language lan: languages) {
				FilesofBrouchure filesBro= filesofbrouchureService.findByLanguageandVersion(lan, ver1);
				if(filesBro!=null) {
					filesList.add(filesBro);
				}
			}
		}
		
		
		for(FilesofBrouchure temp: filesList) {
			
			 makeThumbnail(temp);
			
		}
		
		
		model.addAttribute("filesList", filesList);
		model.addAttribute("brouchures", brouchures);
		model.addAttribute("versions", versions);
		model.addAttribute("languages", languages);

		return "brochures";  // view name
	}


	private void makeThumbnail(FilesofBrouchure temp) {
		try {
			 
			 boolean checkPdf=temp.getWebPath().toLowerCase().endsWith(".pdf");
			 
			 if(checkPdf==true && temp.getThumbnailPath()==null) {
				 int brochureId=temp.getVersion().getBrouchure().getId();
				 int versionValue= temp.getVersion().getBroVersion();
				 String langName= temp.getLan().getLangName();
				 String pdfpath=temp.getWebPath();
				 	
				
				 String str=CommonData.uploadBrouchure+ brochureId + "/" + versionValue + "/" + "web" + "/" + langName ;
				 ServiceUtility.createFolder(str);
				 String document1= generateImageFromPdfAndSave(pdfpath, str);
				 temp.setThumbnailPath(document1);
				 filesofbrouchureService.save(temp);
					
			 }
			 
			 
	  } catch (IOException e) {
			 
		    e.printStackTrace();
	  }
	}
	
	
	
	private void makeThumbnailofResearchPaper(ResearchPaper temp) {
		try {
			 
			 boolean checkPdf=temp.getResearchPaperPath().toLowerCase().endsWith(".pdf");
			 
			 if(checkPdf==true && temp.getThumbnailPath()==null) {
				 int researchPaperId=temp.getId();
				 
				 String pdfpath=temp.getResearchPaperPath();
				 	
				
				 String str=CommonData.uploadResearchPaper+researchPaperId ;
				 ServiceUtility.createFolder(str);
				 String document1= generateImageFromPdfAndSave(pdfpath, str);
				 temp.setThumbnailPath(document1);
				 researchPaperService.save(temp);
					
			 }
			 
			 
	  } catch (IOException e) {
			 
		    e.printStackTrace();
	  }
	}


	/**
	 * redirects page to edit the training data given training id
	 * @param id  int value
	 * @param principal Principal Object
	 * @param model Model object
	 * @return String object(webpage)
	 */
	@RequestMapping(value = "/training/edit/{id}", method = RequestMethod.GET)
	public String editTrainingGet(@PathVariable int id,Model model,Principal principal) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);

		TrainingInformation training=trainingInfoService.getById(id);

		if(training.getUser().getId() != usr.getId()) {

			 return "redirect:/viewTrainee";
		}

		List<State> states=stateService.findAll();

		model.addAttribute("states",states);

		model.addAttribute("training",training);

		return "updateTraining";  // need to accomdate view part
	}

	/**
	 * update training object
	 * @param model model object
	 * @param principal Principal Object
	 * @param state int value
	 * @param district int value
	 * @param city int value
	 * @param totaltrainee int value
	 * @param address int value
	 * @param pinCode int value
	 * @param trainingId int value
	 * @return String object(Webpage)
	 */
	@RequestMapping(value = "/updateTraining", method = RequestMethod.POST)
	public String updateTrainingPost(Model model,Principal principal,
			@RequestParam(value="stateName") int state,
			@RequestParam(value="districtName") int district,
			@RequestParam(value="cityName") int city,
			@RequestParam(value = "totalPar") int totaltrainee,
			@RequestParam(value="addressInformationName") String address,
			@RequestParam(value="pinCode") int pinCode,
			@RequestParam(value="trainingId") int trainingId) {

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		List<State> states=stateService.findAll();

		model.addAttribute("states",states);

		model.addAttribute("userInfo", usr);

		TrainingInformation trainingData=trainingInfoService.getById(trainingId);

		model.addAttribute("training",trainingData);

		trainingData.setTotalParticipant(totaltrainee);

		trainingData.setAddress(address);

		try {
			trainingInfoService.save(trainingData);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			 // throw a error
			model.addAttribute("error_msg",CommonData.EVENT_ERROR);
			return "updateTraining";
		}


		model.addAttribute("success_msg",CommonData.EVENT_SUCCESS);
		model.addAttribute("training",trainingData);
		return "updateTraining";

	}
	
	/**
	 * Redirects page to list all the tutorial under super admin interface
	 * @param principal Principal Object
	 * @param model Model object
	 * @return String object(webpage)
	 */
	@GetMapping("/tutorialStatus")
	public String tutorialStatus(Principal principal,Model model){

		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
		List<Tutorial> published = new ArrayList<Tutorial>();
		List<Tutorial> tutorials =  tutService.findAll();
		for(Tutorial temp:tutorials) {

			if(temp.getOutlineStatus() == CommonData.PUBLISH_STATUS && temp.getScriptStatus() == CommonData.PUBLISH_STATUS &&
					temp.getSlideStatus() == CommonData.PUBLISH_STATUS && temp.getKeywordStatus() == CommonData.PUBLISH_STATUS &&
					temp.getVideoStatus() == CommonData.PUBLISH_STATUS &&
					temp.getPreRequisticStatus() == CommonData.PUBLISH_STATUS) {

				published.add(temp);
			}

		}

		model.addAttribute("tutorial", published);

		return "listTutorialSuperAdmin";
	}
	
	/**
	 * publish or unpublish tutorial from the system under super admin role
	 * @param id tutorial id int value
	 * @param principal Principal Object
	 * @param model Model object
	 * @return String object(webpage)
	 */
	@RequestMapping(value = "/tutorialStatus/{id}", method = RequestMethod.GET)
	public String publishTutorial(@PathVariable int id,Principal principal,Model model) {
		
		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
		List<Tutorial> published = new ArrayList<Tutorial>();
		List<Tutorial> tutorials ;
		
		
		Tutorial tut = tutService.getById(id);
		if(tut.isStatus()) {
			tut.setStatus(false);
			model.addAttribute("success_msg", "Tutorial unpublished Successfully");
		}else {
			tut.setStatus(true);
			model.addAttribute("success_msg", "Tutorial published Successfully");
		}
		
		try {
			
			tutService.save(tut);
			
		}catch (Exception e) {
			// TODO: handle exception
			
			tutorials =  tutService.findAll();
			for(Tutorial temp:tutorials) {

				if(temp.getOutlineStatus() == CommonData.PUBLISH_STATUS && temp.getScriptStatus() == CommonData.PUBLISH_STATUS &&
						temp.getSlideStatus() == CommonData.PUBLISH_STATUS && temp.getKeywordStatus() == CommonData.PUBLISH_STATUS &&
						temp.getVideoStatus() == CommonData.PUBLISH_STATUS &&
						temp.getPreRequisticStatus() == CommonData.PUBLISH_STATUS) {

					published.add(temp);
				}

			}
			model.addAttribute("tutorial", published);
			model.addAttribute("error_msg", "Please Try again.");
			return "listTutorialSuperAdmin";
		}
		
		
		tutorials =  tutService.findAll();
		for(Tutorial temp:tutorials) {

			if(temp.getOutlineStatus() == CommonData.PUBLISH_STATUS && temp.getScriptStatus() == CommonData.PUBLISH_STATUS &&
					temp.getSlideStatus() == CommonData.PUBLISH_STATUS && temp.getKeywordStatus() == CommonData.PUBLISH_STATUS &&
					temp.getVideoStatus() == CommonData.PUBLISH_STATUS &&
					temp.getPreRequisticStatus() == CommonData.PUBLISH_STATUS) {

				published.add(temp);
			}

		}

		model.addAttribute("tutorial", published);

		return "listTutorialSuperAdmin";
		
	}
	
	/**
	 * Redirects to upload time script for the tutorial
	 * @param principal Principal Object
	 * @param model Model object
	 * @return String object(webpage)
	 */
	@RequestMapping(value = "/uploadTimescript", method = RequestMethod.GET)
	public String uploadTimescriptGet(Principal principal,Model model) {
		
		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
		
		Set<Tutorial> tutorials = new HashSet<Tutorial>();
		Set<Tutorial> published = new HashSet<Tutorial>();

		List<ContributorAssignedMultiUserTutorial> conTutorials=conMultiUser.getAllByuser(usr);
		
		for(ContributorAssignedMultiUserTutorial conMultiTemp : conTutorials) {
			
			ContributorAssignedTutorial conTemp = conRepo.findById(conMultiTemp.getConAssignedTutorial().getId());
			
			tutorials.addAll(tutService.findAllByContributorAssignedTutorial(conTemp));
		}
		
		for(Tutorial x : tutorials) {
			if(x.isStatus()) {
				published.add(x);
			}
		}

		model.addAttribute("tutorial", published);


		return "uploadTimescript";
	}
	
	/**
	 * Url to show entire user in a system on super admin interface
	 * @param principal Principal object
	 * @param model Model object
	 * @return String object(webpage)
	 */
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public String usersGet(Principal principal,Model model) {
		
		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
	
		List<User> allUser= userService.findAll();
		model.addAttribute("users", allUser);
		model.addAttribute("usrRoleService", usrRoleService);
		
		Set<UserRole> ur;
		for(User user: allUser) {
			System.out.println("*******************************************");
			System.out.println(user.getUsername() + " " + user.getFullName());
			List<UserRole> ur1=  usrRoleService.findAllByUser(user);
			//ur=user.getUserRoles();
			for(UserRole temp: ur1) {
				if(temp.getStatus()) {
					String str1=new String();
					String str2=new String();
					if(temp.getCategory()==null ) {
						str1="NA";
					}else {
						str1=temp.getCategory().getCatName();
					}
					if( temp.getLanguage()==null) {
						str2="NA";
					}else {
						str2=temp.getLanguage().getLangName();
					}
					System.out.println(str1 + " " + str2+ " " + temp.getRole().getName());
				}
			}
			
		}
		
		
		
		
		
		return "showUsers";
	}

	@RequestMapping(value = "/statistics",method = RequestMethod.GET)
	public String statistics(Principal principal, Model model,
			@RequestParam(name ="categoryId",defaultValue = "0") int categoryId,
			@RequestParam(name ="languageId",defaultValue = "0") int languageId) {
		
		User usr=new User();
		if(principal!=null) {
			usr=userService.findByUsername(principal.getName());
		}
		model.addAttribute("userInfo", usr);
		//List<Category> cat = catService.findAll();
		List<Category> cat = getCategories();
		
		//List<Category> categories = catService.findAllByOrder();
		//List<Language> lan =lanService.getAllLanguages();
		List<Language> lan =getLanguages();
		
		List<ContributorAssignedTutorial> contributor_Role = conRepo.findAll();
		 
		Collections.sort(cat);
		Collections.sort(lan);
		LinkedHashMap<Integer,String> catMap = new LinkedHashMap<>();
		LinkedHashMap<Integer,String> lanMap = new LinkedHashMap<>();
		for(Category c: cat) {
			
				catMap.put(c.getCategoryId(), c.getCatName());
			
			
		}
		for(Language l: lan) {
			lanMap.put(l.getLanId(), l.getLangName());
		}
		
		List<Tutorial> tutorilaListForCount=tutService.findAllByStatus(true);
		List<Tutorial> finaltutoriallistforcount=new ArrayList<>();
		for(Tutorial temp :tutorilaListForCount) {
			
			Category c = temp.getConAssignedTutorial().getTopicCatId().getCat();
			if(c.isStatus()) {
				finaltutoriallistforcount.add(temp);
			}
		}
		
		model.addAttribute("catMap", catMap);
		model.addAttribute("lanMap", lanMap);

		List<Tutorial> tutorials;
		List<Tutorial> finalTutorials=new ArrayList<>();
		if(categoryId!=0 & languageId!=0) {
			Language language = lanService.getById(languageId);
			Category category = catService.findByid(categoryId);
			List<TopicCategoryMapping> topicCategoryMappings = topicCatService.findAllByCategory(category);
			List<ContributorAssignedTutorial> contributorAssignedTutorials = conRepo.findAllByTopicCatAndLanViewPart(topicCategoryMappings, language);
			tutorials = tutService.findAllByContributorAssignedTutorialList(contributorAssignedTutorials);
			
			for(Tutorial temp :tutorials) {
				
			  Category c = temp.getConAssignedTutorial().getTopicCatId().getCat();
				if(c.isStatus()) {
					finalTutorials.add(temp);
				}
			}
			
			model.addAttribute("cat_value", category.getCategoryId());
			model.addAttribute("lan_value", language.getLanId());
		}else if(categoryId!=0) {
			Category category = catService.findByid(categoryId);
			List<TopicCategoryMapping> topicCategoryMappings = topicCatService.findAllByCategory(category);
			if(!topicCategoryMappings.isEmpty()){
				List<ContributorAssignedTutorial> contributorAssignedTutorials = conRepo.findAllByTopicCat(topicCategoryMappings);
				tutorials = tutService.findAllByContributorAssignedTutorialList(contributorAssignedTutorials);
				for(Tutorial temp :tutorials) {
					
					  Category c = temp.getConAssignedTutorial().getTopicCatId().getCat();
						if(c.isStatus()) {
							finalTutorials.add(temp);
						}
					}
			}else {
				tutorials = null;
				finalTutorials=null;
			}
			
			model.addAttribute("cat_value", category.getCategoryId());
			model.addAttribute("lan_value", 0);
		}else if(languageId!=0){
			Language language = lanService.getById(languageId);
			List<ContributorAssignedTutorial> contributorAssignedTutorials = conRepo.findAllByLan(language);
			tutorials = tutService.findAllByContributorAssignedTutorialList(contributorAssignedTutorials);
			
			for(Tutorial temp :tutorials) {
				
				  Category c = temp.getConAssignedTutorial().getTopicCatId().getCat();
					if(c.isStatus()) {
						finalTutorials.add(temp);
					}
				}
			
			model.addAttribute("cat_value", 0);
			model.addAttribute("lan_value", language.getLanId());
		}else {
			tutorials = tutService.findAllByStatus(true);
			for(Tutorial temp :tutorials) {
				
				  Category c = temp.getConAssignedTutorial().getTopicCatId().getCat();
					if(c.isStatus()) {
						finalTutorials.add(temp);
					}
				}
			model.addAttribute("cat_value", 0);
			model.addAttribute("lan_value", 0);
		}
		
		model.addAttribute("categories", cat);
		model.addAttribute("languages", lan);
		model.addAttribute("catTotal", cat.size());
		model.addAttribute("lanTotal", lan.size());
		model.addAttribute("tutTotal",  finaltutoriallistforcount.size());
		model.addAttribute("tutorials",finalTutorials);
		model.addAttribute("contributor_Role", contributor_Role);
		
		HashMap<String,Integer> cat_count = new HashMap<>();
		HashMap<ContributorAssignedTutorial,Integer> contriRole_count = new HashMap<>();
		
		model.addAttribute("cat_count",cat_count);
		model.addAttribute("contriRole_count",contriRole_count);
				
		return "statistics";
	}
	
	@RequestMapping(value = "/cdContentInfo",method = RequestMethod.GET)
	public String cdContentInfoGet(Principal principal, Model model) {
		
		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
		
		List<Category> cat = catService.findAll();
		List<Language> lan =lanService.getAllLanguages();
		Collections.sort(cat);
		Collections.sort(lan);
		
		model.addAttribute("categories", cat);
		model.addAttribute("languages", lan);
		
		return "cdContent";
	}
	
	@RequestMapping(value = "/cdContentInfo",method = RequestMethod.POST)
	public String cdContentInfoPost(@RequestParam(name = "categoryName") String category,
			@RequestParam(name = "lan") String language,Principal principal, Model model) {
		
		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}

		model.addAttribute("userInfo", usr);
		
		long totalSize = 0;
		int totalNumberOfVideo=0;
		
		List<Category> cat = catService.findAll();
		List<Language> lan =lanService.getAllLanguages();
		Collections.sort(cat);
		Collections.sort(lan);
		
		model.addAttribute("categories", cat);
		model.addAttribute("languages", lan);
		
		Category catTemp = catService.findBycategoryname(category);
		Language lanTemp = lanService.getByLanName(language);
		
		if(catTemp == null || lanTemp == null ) {
			System.out.println("vik");
			return "redirect:/cdContentInfo";
		}
		
		model.addAttribute("categoryName", category);
		model.addAttribute("lanName", language);
		
		List<Tutorial> tutorials = tutService.findAll();
		
		for(Tutorial x : tutorials) {
			if(x.getConAssignedTutorial().getLan().getLangName().equalsIgnoreCase(lanTemp.getLangName()) &&
					x.getConAssignedTutorial().getTopicCatId().getCat().getCatName().equalsIgnoreCase(catTemp.getCatName())){
				
				Path path = Paths.get(env.getProperty("spring.applicationexternalPath.name")+x.getVideo());
				
				try {
					totalSize += Files.size(path);
					totalNumberOfVideo+=1;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		System.out.println(totalSize);
		model.addAttribute("value", totalSize/1024/1024);
		model.addAttribute("totalVideo", totalNumberOfVideo);
		if(totalSize > 0 && totalNumberOfVideo > 0) {
			if(principal == null) {
				model.addAttribute("rate", "500");
			}else {
				model.addAttribute("rate", "Free");
			}
		}
		
		return "cdContent";
	}
	
	@RequestMapping(value = "/unpublishTopic",method = RequestMethod.GET)
	public String unpublishTopic(Model model,Principal principal) {

		User usr = getUser(principal, userService);

		model.addAttribute("userInfo", usr);
		List<Category> categories = catService.findAll();
		List<Category> filtered_categories = new ArrayList<Category>();
		for(Category cat: categories) {
			if(!cat.getTopicCategoryMap().isEmpty()) {
				filtered_categories.add(cat);
			}
		}
		
		//alok code start
		List<Topic> tops=topicService.findAll();
		List<String> topics=new ArrayList<String>();
		for(Topic t: tops) {
			topics.add(t.getTopicName());
			
		}
		
		//alok code ends
		
		List<Language> langs = lanService.getAllLanguages();
		List<String> langauges=new ArrayList<String>();
		for(Language temp:langs) {
				langauges.add(temp.getLangName());
		}
		List<LogManegement> lms = logMangementService.getLogsWithSuperUser();
		Set<Tutorial> tutorials = new HashSet<Tutorial>();
		for(LogManegement l:lms) {
			if(!l.getTutorialInfos().isStatus()) {
				tutorials.add(l.getTutorialInfos());
				Tutorial t = l.getTutorialInfos();
				
			}
		}
		
		model.addAttribute("langauges", langauges);
		model.addAttribute("categories", filtered_categories);
		model.addAttribute("topics",topics);
		model.addAttribute("tutorials", tutorials);
		
		model.addAttribute("method", "get");
		return "unpublishTopic";

	} 
	
	@RequestMapping(value = "/unpublishTopic",method = RequestMethod.POST)
	public String unpublishTopicPost(HttpServletRequest request, 
			@ModelAttribute("category") Integer categoryId, 
			@ModelAttribute("topic") Integer topicId, 
			@ModelAttribute("language") String language, 
			Model model , Principal principal) {

		model.addAttribute("classActiveForgetPassword", true);
		Category cat = catService.findByid(categoryId);
		Topic topic = topicService.findById(topicId);
		model.addAttribute("topicname", topic.getTopicName());
		
		Language lan = lanService.getByLanName(language);
		model.addAttribute("cat", cat.getCategoryId());
		model.addAttribute("topic", topic.getTopicId());
		model.addAttribute("lan", lan.getLangName());
		model.addAttribute("catname", cat.getCatName());
		TopicCategoryMapping tcm = topicCatService.findAllByCategoryAndTopic(cat, topic);
		ContributorAssignedTutorial con = conRepo.findByTopicCatAndLanViewPart(tcm, lan);
		List<Tutorial> tut= tutService.findAllByContributorAssignedTutorial(con);
		Tutorial t = tut.get(0);
		model.addAttribute("tut", tut);
		model.addAttribute("tutorial_id", t.getTutorialId());
		
		List<Category> categories_lst = catService.findAll();
		List<String> categories = new ArrayList<String>();;
		HashMap<Integer,String> map = new HashMap<>();
		for(Category c: categories_lst) {
			map.put(c.getCategoryId(),c.getCatName());
		}
		
		List<Language> langs = lanService.getAllLanguages();
		List<String> langauges=new ArrayList<String>();
		for(Language temp:langs) {
				langauges.add(temp.getLangName());
		}
		
		HashMap<Integer,String> topicName=new HashMap<>();

		

		List<TopicCategoryMapping> local = topicCatService.findAllByCategory(cat) ;

		for(TopicCategoryMapping temp : local) {

			topicName.put(temp.getTopic().getTopicId(), temp.getTopic().getTopicName());
			
		}
		/*
		//alok code starts
		List<TopicCategoryMapping> local = topicCatService.findAllByCategory(cat) ;
		List<ContributorAssignedTutorial> cat_list = conService.findAllByTopicCat(local);
		
		//To find Topics
		List<Tutorial> tutorials = tutService.findAllByconAssignedTutorialAndStatus(cat_list);
		
		for(Tutorial t1: tutorials) {
			topicName.put(t1.getConAssignedTutorial().getTopicCatId().getTopic().getTopicId(),t1.getConAssignedTutorial().getTopicCatId().getTopic().getTopicName());
		}
		
		*/
		//alok code ends
		
		model.addAttribute("topics", topicName);
		model.addAttribute("langauges", langauges);
		model.addAttribute("categories", map);
		model.addAttribute("method", "post");
		
			model.addAttribute("status", "can_unpublish");
		
		if(t.isStatus()) {
			model.addAttribute("status", "can_unpublish");
		}else {
			model.addAttribute("status", "already_unpublished");
		}
		User usr=new User();

		if(principal!=null) {

			usr=userService.findByUsername(principal.getName());
		}
		model.addAttribute("userInfo", usr);
		
		
		
		return "unpublishTopic";
	}
	
	
}
