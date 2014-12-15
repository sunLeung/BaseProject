package common.logger;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoggerConfig {
	/**************************日志等级定义********************************/
	public static final int INFO=0;
	public static final int DEBUG=1;
	public static final int ERROR=2;
	
	/**************************日志输出定义********************************/
	public static final int APPENDER_CONTROLLER=0;
	public static final int APPENDER_FILE=1;
	
	/**************************日志参数配置********************************/
	public static long LAST_MODIFY=0;
	public static final String SYSTEM_LOGGER_NAME="syslog";
	
	/**日志回写时间频率(秒)*/
	public static int DEFAULT_FLUSH_TIME=30; 
	public static String DEFAULT_LOG_PATH=System.getProperty("user.dir")+File.separator+"logs";
	public static String DEFAULT_PATTERN="[%level %time %class %method %line] %msg";
	public static int[] DEFAULT_LEVELS=new int[]{INFO,DEBUG,ERROR};
	public static int[] DEFAULT_APPENDERS=new int[]{APPENDER_CONTROLLER,APPENDER_FILE};
	
	public static Map<String,Object> configFile=new ConcurrentHashMap<String,Object>();
	
	public static int getFLUSH_TIME(){
		Integer time=(Integer)configFile.get("FLUSH_TIME");
		time=time==null?DEFAULT_FLUSH_TIME:time;
		return time;
	}
	public static String getLOG_PATH(){
		String path=(String)configFile.get("LOG_PATH");
		path=path==null?DEFAULT_LOG_PATH:path;
		return path;
	}
	public static String getPATTERN(){
		String pattern=(String)configFile.get("PATTERN");
		pattern=pattern==null?DEFAULT_PATTERN:pattern;
		return pattern;
	}
	public static int[] getLEVELS(){
		int[] lv=(int[])configFile.get("LEVELS");
		lv=lv==null?DEFAULT_LEVELS:lv;
		return lv;
	}
	public static int[] getAPPENDERS(){
		int[] apds=(int[])configFile.get("APPENDERS");
		apds=apds==null?DEFAULT_LEVELS:apds;
		return apds;
	}
}
