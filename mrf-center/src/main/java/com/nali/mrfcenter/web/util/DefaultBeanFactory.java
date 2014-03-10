package com.nali.mrfcenter.web.util;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.beans.factory.access.SingletonBeanFactoryLocator;
import org.springframework.context.ApplicationContext;

/**
 * <p>
 * DefaultBeanFactory
 * @version 1.0
 */
public class DefaultBeanFactory {

	private final static String APP_CTX = "default";
	private static String selector;
	private static ApplicationContext ac=null;
	
	public final static Object getBean(String beanName) {
		return getBean(beanName, APP_CTX);
	}

	public final static Object getBean(String beanName, String ctxName) {
		return getFactory(ctxName).getBean(beanName);
	}

	public final static boolean isSingleton(String beanName) {
		return isSingleton(beanName, APP_CTX);
	}

	public final static boolean isSingleton(String beanName, String ctxName) {
		return getFactory(ctxName).isSingleton(beanName);
	}

	protected final static BeanFactory getFactory(String ctxName) {
		BeanFactoryLocator beanFactoryLocator = selector == null ? SingletonBeanFactoryLocator
				.getInstance()
				: SingletonBeanFactoryLocator.getInstance(selector);
		BeanFactoryReference ref = beanFactoryLocator.useBeanFactory(ctxName);
		return ref.getFactory();
	}
	public static ApplicationContext getApplicationContext()
	{
		if(ac==null)
		{
			ac=(ApplicationContext)getFactory(APP_CTX);
		}
		return ac;
	}

	/**
	 * @param selector The selector to set.
	 */
	public static void setSelector(String selector) {
		DefaultBeanFactory.selector = selector;
	}
}
