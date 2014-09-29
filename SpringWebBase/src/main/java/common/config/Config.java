package common.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;

import common.logger.Logger;
import common.logger.LoggerConfig;
import common.logger.LoggerManger;
import common.utils.FileUtils;
import common.utils.JsonUtils;

/**
 * 
 * @Description 项目配置
 * @author liangyx
 * @date 2013-7-1
 * @version V1.0
 */
public class Config {
	private static Logger log=LoggerManger.getLogger();
	
	public static String ROOT_DIR=System.getProperty("user.dir");
	/**配置文件根目录*/
	public static String CONFIG_DIR="config";
	/**守护线程运行间隔*/
	public static int WATCH_SECOND=10;
	/**日志配置文件*/
	public static String LOGGER_CONFIG="logger.xx";
	/**数据库配置*/
	public static String DB_CONFIG="c3p0-config.xml";
	/**外部配置文件数据容器*/
	public static Map<String,Object> CONFIG_DATA=new HashMap<String, Object>();
	
	/**
	 * 初始化配置
	 */
	public static void init(ServletContextEvent sce){
		ROOT_DIR=sce.getServletContext().getRealPath("")+File.separator;
		CONFIG_DIR=ROOT_DIR+"WEB-INF"+File.separator+CONFIG_DIR+File.separator;
		LOGGER_CONFIG=CONFIG_DIR+LOGGER_CONFIG;
		DB_CONFIG=CONFIG_DIR+DB_CONFIG;
		initConfigContent();
		
		//设置日志参数
		LoggerConfig.DEFAULT_LOG_PATH=ROOT_DIR+"logs";
		LoggerConfig.DEFAULT_PATTERN="[%level %time] %msg";
	}
	
	public static void initConfigContent(){
		try {
			log.info("Star init config json data.");
			String filePath=Config.CONFIG_DIR + File.separator + "config.json";
			String jsonSrc=FileUtils.readFileToJSONString(filePath);
			CONFIG_DATA=(Map<String,Object>)JsonUtils.objectFromJson(jsonSrc, Map.class);
			log.info("Init config json data complete.");
		} catch (Exception e) {
			log.error(e.toString());
			e.printStackTrace();
		}
	}
}
