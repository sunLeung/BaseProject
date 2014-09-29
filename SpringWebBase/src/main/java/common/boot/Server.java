package common.boot;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import common.config.Config;
import common.logger.LoggerManger;
import common.user.UserService;
import common.utils.TimerManagerUtils;

public class Server implements ServletContextListener{

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		//回写日志
		LoggerManger.stopFileWriter();
		//清理定时器
		TimerManagerUtils.destroyed();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		//初始化配置
		initConfig(arg0);
		LoggerManger.initLoggerConfig(Config.LOGGER_CONFIG);
		//初始化权限表
		UserService.initAuthContent();
		//初始化用户数据
		UserService.initUserContent();
		//初始化主页导航
		UserService.initNavigationContent();
		//初始化用户组数据
		UserService.initGroupContent();
	}

	private void initConfig(ServletContextEvent sce){
		Config.init(sce);
	}
}
