package service.gamemanager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import common.config.Config;
import common.utils.FileUtils;
import common.utils.JsonRespUtils;
import common.utils.SSHUtils;
import common.utils.StringUtils;

@Controller
@RequestMapping("gamemanager")
public class GameManagerService {

//	@RequestMapping(value = "upload", method = RequestMethod.POST)
//	@ResponseBody
//	public String handleFileUpload(
//			@RequestParam("file") CommonsMultipartFile file) {
//		String path = (String) Config.CONFIG_DATA.get("gameManagerUploadPath");
//		if (StringUtils.isBlack(path)) {
//			return JsonRespUtils
//					.fail("Config.json gameManagerUploadPath is null");
//		}
//		if (!file.isEmpty()) {
//			try {
//				file.transferTo(new File(path + File.separator
//						+ file.getOriginalFilename()));
//			} catch (IllegalStateException | IOException e) {
//				e.printStackTrace();
//				return JsonRespUtils.exception(e.toString());
//			}
//		}
//		return JsonRespUtils.fail("upload fail.");
//	}

	/**
	 * 上传资源文件
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "upload", method = RequestMethod.POST)
	@ResponseBody
	public String upload(MultipartHttpServletRequest request,
			HttpServletResponse response) {
		try {
			Iterator<String> itr = request.getFileNames();
			MultipartFile mpf = null;
			List<Map<String,Object>> result=new ArrayList<Map<String,Object>>();
			String path = (String) Config.CONFIG_DATA.get("gameManagerUploadPath");
			if (StringUtils.isBlack(path)) {
				return JsonRespUtils.fail("Config.json gameManagerUploadPath is null");
			}
			File dir=new File(path);
			if(!dir.exists()){
				dir.mkdirs();
			}
			while (itr.hasNext()) {
				Map<String,Object> map=new HashMap<String, Object>();
				mpf = request.getFile(itr.next());
				map.put("fileName", mpf.getOriginalFilename());
				map.put("fileSize", mpf.getSize() / 1024 + " Kb");
				map.put("fileType", mpf.getContentType());
				File file=new File(path + File.separator+ mpf.getOriginalFilename());
				FileCopyUtils.copy(mpf.getBytes(), new FileOutputStream(file));
				map.put("fileMD5", FileUtils.getFileMD5String(file));
				result.add(map);
			}
			return JsonRespUtils.success(result);
		} catch (Exception e) {
			e.printStackTrace();
			return JsonRespUtils.exception(e.toString());
		}
	}
	
	@RequestMapping(value = "start-server", method = RequestMethod.GET)
	@ResponseBody
	public String startServer() {
		String os=System.getProperties().getProperty("os.name");
		System.out.println("os:"+os);
		String gameServerPath=(String) Config.CONFIG_DATA.get("gameServerPath");
		if (StringUtils.isBlack(gameServerPath)) {
			return JsonRespUtils.fail("Config.json gameServerPath is null");
		}
		if(StringUtils.containsIgnoreCase(os,"linux")){
			SSHUtils.startGameServer(gameServerPath+"/auto.sh","now desktop is");
		}
		return JsonRespUtils.success("正在开服...");
	}
	
	@RequestMapping(value = "stop-server", method = RequestMethod.GET)
	@ResponseBody
	public String stopServer() {
		String os=System.getProperties().getProperty("os.name");
		System.out.println("os:"+os);
		String str="";
		String gameServerPath=(String) Config.CONFIG_DATA.get("gameServerPath");
		if (StringUtils.isBlack(gameServerPath)) {
			return JsonRespUtils.fail("Config.json gameServerPath is null");
		}
		if(StringUtils.containsIgnoreCase(os,"linux")){
			str=SSHUtils.runLinuxCMD(gameServerPath+"/stop.sh");
		}
		return JsonRespUtils.success("关服成功");
	}
	
	@RequestMapping(value = "server-state", method = RequestMethod.GET)
	@ResponseBody
	public static Map<String,Object> getServerState(){
		String os=System.getProperties().getProperty("os.name");
		System.out.println("os:"+os);
		String ps="";
		String netstat="";
		Integer gameserverport=(Integer)Config.CONFIG_DATA.get("gameServerPort");
		Map<String,Object> result=new HashMap<String, Object>();
		if (gameserverport==null) {
			result.put("code", 1);
			result.put("msg", "Config.json gameServerPort is null");
			return result;
		}
		if(StringUtils.containsIgnoreCase(os,"linux")){
			ps=SSHUtils.runLinuxCMD("ps -ef | grep java");
			netstat=SSHUtils.runLinuxCMD("netstat -an | grep "+gameserverport);
		}
		if(StringUtils.isBlank(ps)){
			result.put("code", 2);
			result.put("msg", "检测失败");
		}else{
			if(ps.contains("GameServer")){
				if(netstat.contains(""+gameserverport)){
					result.put("code", 0);
					result.put("msg", "运行中...");
				}else{
					result.put("code", 3);
					result.put("msg", "正在开服...");
				}
			}else{
				result.put("code", 4);
				result.put("msg", "已停服");
			}
		}
		return result;
	}
	
}
