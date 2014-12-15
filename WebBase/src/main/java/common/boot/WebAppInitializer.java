package common.boot;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import common.config.SpringMVCConfig;

public class WebAppInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext)throws ServletException {
		initSpring(servletContext);
		System.out.println("[System INFO] WebAppInitializer completed.");
	}

	/**
	 * 初始化spring
	 * @param servletContext
	 */
	private static void initSpring(ServletContext servletContext){
		AnnotationConfigWebApplicationContext springMvcContext = new AnnotationConfigWebApplicationContext();
		springMvcContext.register(SpringMVCConfig.class);

		// Register and map the dispatcher servlet
		ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(springMvcContext));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("*.do");
		
		System.out.println("[System INFO] Init spring completed.");
	}
	
}
