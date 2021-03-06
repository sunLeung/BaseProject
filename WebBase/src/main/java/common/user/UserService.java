package common.user;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import common.config.Config;
import common.logger.Logger;
import common.logger.LoggerManger;
import common.utils.FileUtils;
import common.utils.JsonRespUtils;
import common.utils.JsonUtils;
import common.utils.StringUtils;

@Controller
@RequestMapping("user")
public class UserService {
	private static Logger log=LoggerManger.getLogger();
	public static Map<String,User> userContentByName=new ConcurrentHashMap<String,User>();
	public static Map<Integer,User> userContentById=new ConcurrentHashMap<Integer,User>();
	
	/**
	 * 初始化用户数据
	 */
	public static void initUserContent(){
		try {
			log.info("Star init userContent json data.");
			String filePath=Config.CONFIG_DIR + File.separator + "user.json";
			String jsonSrc=FileUtils.readFileToJSONString(filePath);
			User[] list=(User[])JsonUtils.objectFromJson(jsonSrc, User[].class);
			Map<String,User> tempContentByName=new ConcurrentHashMap<String,User>();
			Map<Integer,User> tempContentById=new ConcurrentHashMap<Integer,User>();
			for(User u:list){
				if(tempContentByName.containsKey(u.getName())){
					throw new IllegalArgumentException("Repeated username:"+u.getName());
				}
				if(tempContentById.containsKey(u.getId())){
					throw new IllegalArgumentException("Repeated userid:"+u.getId());
				}
				u.initAuthArray();
				tempContentByName.put(u.getName(), u);
				tempContentById.put(u.getId(), u);
			}
			userContentByName=tempContentByName;
			userContentById=tempContentById;
			log.info("Init userContent json data complete.");
		} catch (Exception e) {
			log.error(e.toString());
			e.printStackTrace();
		}
	}
	
	/**
	 * 回写用户数据
	 */
	public static void flushUserContent(){
		log.info("Star flush userContent json data.");
		try {
			String filePath=Config.CONFIG_DIR + File.separator + "user.json";
			Object[] users=(Object[])userContentByName.values().toArray();
			for(Object u:users){
				User us=(User)u;
				if(us.getId()==0){
					us.setAuth("all");
					us.setGroupid(0);
				}
			}
			String json=JsonUtils.jsonFromObject(users);
			FileUtils.writeStringToFile(filePath, json);
		} catch (Exception e) {
			log.error(e.toString());
			e.printStackTrace();
		}
		log.info("Flush userContent json data completed.");
	}
	
	
	/**授权*/
	public static Map<String,AuthMap> authContent=new HashMap<String, AuthMap>();
	public static List<AuthMap> authList=new ArrayList<AuthMap>();
	private static long authMapLastModify=0;
	
	/**
	 * 初始化授权表
	 */
	public static void initAuthContent(){
		try {
			log.info("Star init authContent json data.");
			String filePath=Config.CONFIG_DIR + File.separator + "auth-map.json";
			String jsonSrc=FileUtils.readFileToJSONString(filePath);
			AuthMap[] list=(AuthMap[])JsonUtils.objectFromJson(jsonSrc, AuthMap[].class);
			Map<String,AuthMap> tempContent=new ConcurrentHashMap<String, AuthMap>();
			for(AuthMap a:list){
				if(tempContent.containsKey(a.getUri())){
					throw new IllegalArgumentException("Repeated uri:"+a.getUri());
				}
				tempContent.put(a.getUri(),a);
				authList.add(a);
			}
			authContent=tempContent;
			log.info("Init authContent json data complete.");
		} catch (Exception e) {
			log.error(e.toString());
			e.printStackTrace();
		}
	}
	
	/**
	 * 监控权限文件
	 */
	public static void watchAuthContent(){
		String filePath=Config.CONFIG_DIR + File.separator + "auth-map.json";
		File f = new File(filePath);
		if(f.exists()){
			if(f.lastModified()!=authMapLastModify){
				authMapLastModify=f.lastModified();
				initAuthContent();
			}
		}else{
			throw new IllegalArgumentException("Can't find "+filePath);
		}
	}
	
	/**
	 * 用户登陆
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping(value ="login",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Integer> login(HttpServletRequest request, HttpServletResponse response,
				HttpSession session) {
		Map<String,Integer> result=new HashMap<String, Integer>();
		String name=request.getParameter("name");
		String password=request.getParameter("password");
		if(StringUtils.isBlank(name)||StringUtils.isBlank(password)){
			result.put("code", 1);
			return result;
		}
		
		User user=userContentByName.get(name);
		if(user!=null&&password.equals(user.getPassword())){
			session.setAttribute("user", user);
			result.put("code", 0);
		}else{
			result.put("code", 1);
		}
		return result;
    }
	
	/**
	 * 用户注销
	 * @param request
	 * @param response
	 * @param session
	 */
	@RequestMapping(value ="logout")
	public void logout(HttpServletRequest request,HttpServletResponse response,HttpSession session){
		try {
			session.removeAttribute("user");
			response.sendRedirect(Config.WEB_BASE+"/login.jsp");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**导航*/
	public static List<Navigation> navigationContent=new ArrayList<Navigation>();
	
	/**
	 * 初始化授权表
	 */
	public static void initNavigationContent(){
		try {
			log.info("Star init navigationContent json data.");
			String filePath=Config.CONFIG_DIR + File.separator + "navigation.json";
			String jsonSrc=FileUtils.readFileToJSONString(filePath);
			Navigation[] list=(Navigation[])JsonUtils.objectFromJson(jsonSrc, Navigation[].class);
			navigationContent.addAll(Arrays.asList(list));
			log.info("Init navigationContent json data complete.");
		} catch (Exception e) {
			log.error(e.toString());
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取主页导航栏数据
	 * @param session
	 * @return
	 */
	public static List<Navigation> getNavigation(HttpSession session){
		List<Navigation> result=new ArrayList<Navigation>();
		User user=(User)session.getAttribute("user");
		if(user!=null){
			for(Navigation navi:navigationContent){
				String uri=navi.getUri();
				AuthMap authMap=authContent.get(uri);
				if(authMap==null){
					result.add(navi);
					continue;
				}
				if(authMap!=null&&ArrayUtils.contains(user.authArray(), authMap.getAuthCode())){
					result.add(navi);
					continue;
				}
			}
		}
		return result;
	}
	
	/**用户组*/
	public static List<Group> groupContent=new ArrayList<Group>();
	
	/**
	 * 初始化用户组
	 */
	public static void initGroupContent(){
		try {
			log.info("Star init groupContent json data.");
			String filePath=Config.CONFIG_DIR + File.separator + "group.json";
			String jsonSrc=FileUtils.readFileToJSONString(filePath);
			Group[] list=(Group[])JsonUtils.objectFromJson(jsonSrc, Group[].class);
			groupContent.addAll(Arrays.asList(list));
			log.info("Init groupContent json data complete.");
		} catch (Exception e) {
			log.error(e.toString());
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value ="create",method = RequestMethod.POST)
	@ResponseBody
	public String createUser(
			@RequestParam(value = "username") String username,
			@RequestParam(value = "password") String password,
			@RequestParam(value = "repassword") String repassword,
			@RequestParam(value = "groupid") String groupid,
			@RequestParam(value = "power") String power) {
		if(StringUtils.isBlack(username,password,repassword,groupid)){
			return JsonRespUtils.fail("必要数据不能为空");
		}
		if(!password.equals(repassword)){
			return JsonRespUtils.fail("两次密码不一样");
		}
		if(userContentByName.containsKey(username)){
			return JsonRespUtils.fail("用户已存在");
		}
		User user=new User();
		user.setId(userContentByName.size());
		user.setName(username);
		user.setPassword(repassword);
		user.setGroupid(Integer.valueOf(groupid));
		user.setAuth(power);
		user.initAuthArray();
		UserService.userContentByName.put(username, user);
		UserService.userContentById.put(user.getId(), user);
		UserService.flushUserContent();
		return JsonRespUtils.success("创建成功");
	}
	
	@RequestMapping(value ="look",method = RequestMethod.POST)
	@ResponseBody
	public String lookUser(
			@RequestParam(value = "userid") String userid) {
		if(StringUtils.isBlack(userid)){
			return JsonRespUtils.fail("无效访问");
		}
		User user=userContentById.get(Integer.valueOf(userid.trim()));
		
		if(user!=null){
			Map<String,Object> result=new HashMap<String, Object>();
			result.put("name", user.getName());
			result.put("password", user.getPassword());
			result.put("group", getGroupName(user.getGroupid()));
			List<String> power=new ArrayList<String>();
			if(user.authArray()!=null)
			for(int p:user.authArray()){
				power.add(getAuthName(p));
			}
			result.put("power", power);
			return JsonRespUtils.success(result);
		}
		return JsonRespUtils.fail("获取失败");
	}
	
	@RequestMapping(value ="pre-update",method = RequestMethod.POST)
	@ResponseBody
	public String preUpdate(
			@RequestParam(value = "userid") String userid) {
		if(StringUtils.isBlack(userid)){
			return JsonRespUtils.fail("无效访问");
		}
		User user=userContentById.get(Integer.valueOf(userid.trim()));
		
		if(user!=null){
			Map<String,Object> result=new HashMap<String, Object>();
			result.put("id", user.getId());
			result.put("name", user.getName());
			result.put("password", user.getPassword());
			result.put("group", user.getGroupid());
			List<Integer> power=new ArrayList<Integer>();
			if(user.authArray()!=null)
				for(int p:user.authArray()){
					power.add(p);
				}
			result.put("power", power);
			return JsonRespUtils.success(result);
		}
		return JsonRespUtils.fail("获取失败");
	}
	
	@RequestMapping(value ="update",method = RequestMethod.POST)
	@ResponseBody
	public String updateUser(
			@RequestParam(value = "id") String id,
			@RequestParam(value = "pwd") String pwd,
			@RequestParam(value = "repwd") String repwd,
			@RequestParam(value = "gid") String gid,
			@RequestParam(value = "power") String power) {
		if(StringUtils.isBlack(pwd,repwd,gid)){
			return JsonRespUtils.fail("必要数据不能为空");
		}
		if(!pwd.equals(repwd)){
			return JsonRespUtils.fail("两次密码不一样");
		}
		User user=userContentById.get(Integer.valueOf(id));
		if(user!=null){
			user.setAuth(power);
			user.setGroupid(Integer.valueOf(gid));
			user.setPassword(pwd);
			user.initAuthArray();
			updateUser(user);
			return JsonRespUtils.success("更新成功");
		}
		return JsonRespUtils.fail("更新失败");
	}
	
	@RequestMapping(value ="delete",method = RequestMethod.POST)
	@ResponseBody
	public String deleteUser(
			@RequestParam(value = "id") String id) {
		User user=userContentById.get(Integer.valueOf(id));
		if(user!=null){
			deleteUser(user);
			return JsonRespUtils.success("删除成功");
		}
		return JsonRespUtils.fail("删除失败");
	}
	
	public static void updateUser(User user){
		userContentById.put(user.getId(), user);
		userContentByName.put(user.getName(), user);
		UserService.flushUserContent();
	}
	
	public static void deleteUser(User user){
		int id=user.getId();
		String name=user.getName();
		userContentById.remove(id);
		userContentByName.remove(name);
		UserService.flushUserContent();
	}
	
	/**
	 * 获取用户组名
	 * @param groupid
	 * @return
	 */
	public static String getGroupName(int groupid){
		for(Group g:groupContent){
			if(g.getId()==groupid){
				return g.getName();
			}
		}
		return "";
	}
	
	/**
	 * 获取权限名字
	 * @param authCode
	 * @return
	 */
	public static String getAuthName(int authCode){
		for(AuthMap a:authList){
			if(a.getAuthCode()==authCode){
				return a.getName();
			}
		}
		return "";
	}
	
	private static Comparator<User> userSorter=new Comparator<User>() {
		
		@Override
		public int compare(User o1, User o2) {
			return o1.getId()-o2.getId();
		}
	};
	
	/**
	 * 获取用户列表
	 * @return
	 */
	public static List<User> getUserList(User user,String searchKey){
		List<User> list=new ArrayList<User>();
		if(StringUtils.isBlank(searchKey)){
			list.addAll(userContentByName.values());
		}else{
			for(Entry<String, User> entry:userContentByName.entrySet()){
				String name=entry.getKey();
				if(name.equalsIgnoreCase(searchKey.trim())||name.contains(searchKey.trim())){
					list.add(entry.getValue());
				}
			}
		}
		if(user.getId()!=0){
			for(int i=0;i<list.size();i++){
				User l=list.get(i);
				if(l.getId()==0){
					list.remove(i);
				}
			}
		}
		
		Collections.sort(list, userSorter);
		return list;
	}
}
