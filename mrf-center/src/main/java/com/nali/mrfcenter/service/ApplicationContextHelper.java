package com.nali.mrfcenter.service;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service("applicationContextHelper")
public class ApplicationContextHelper implements ApplicationContextAware {

	private static ApplicationContext appContext;  
	
	@Override
	public void setApplicationContext(ApplicationContext appContext)
			throws BeansException {
		ApplicationContextHelper.appContext = appContext;
	}
	
	public static Object getBean(String beanName) {  
        return appContext.getBean(beanName);  
    }

}
