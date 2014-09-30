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
		// Create the 'root' Spring application context
		AnnotationConfigWebApplicationContext springMvcContext = new AnnotationConfigWebApplicationContext();
		springMvcContext.register(SpringMVCConfig.class);
		
		springMvcContext.setServletContext(servletContext);

		// Register and map the dispatcher servlet
		ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(springMvcContext));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("*.do");
		System.out.println("[System INFO] WebAppInitializer completed.");
	}

}
