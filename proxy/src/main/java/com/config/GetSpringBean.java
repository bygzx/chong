package com.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author eric
 * @date 2019/7/11 10:37
 **/
@Component
@Lazy(false)
public class GetSpringBean implements ApplicationContextAware {

    private static ApplicationContext context;

    public static Object getBean(String name) {
        return context.getBean(name);
    }

    public static <T> T getBean(Class<T> c) {

        return context.getBean(c);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        if(applicationContext!=null){
            context = applicationContext;
        }
    }
}
