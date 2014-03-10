package com.nali.mrfcenter.web.util;

import org.springframework.context.ApplicationContext;

/**
 * Can only be used in web layer, JSP and Servelt for example.
 * @author will
 *
 */
public class SpringContextUtil {

    private static ApplicationContext context;

    public static void setApplicationContext(ApplicationContext acx) {
        context = acx;
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    public static Object getBean(String name) {
        return context.getBean(name);
    }

}