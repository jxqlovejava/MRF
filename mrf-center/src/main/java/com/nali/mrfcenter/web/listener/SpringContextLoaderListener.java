/**
 * Created on 2008-03-03
 * 
 * @author bing.xie
 * 
 */

package com.nali.mrfcenter.web.listener;

/**
 * 
 * @author will
 * 
 */
import javax.servlet.ServletContextEvent;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.nali.mrfcenter.web.util.SpringContextUtil;

public class SpringContextLoaderListener extends org.springframework.web.context.ContextLoaderListener {

	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		
		// Set application context for web layer
		SpringContextUtil.setApplicationContext(WebApplicationContextUtils
				.getWebApplicationContext(event.getServletContext()));
	}
}