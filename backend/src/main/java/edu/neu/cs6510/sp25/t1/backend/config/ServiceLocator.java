package edu.neu.cs6510.sp25.t1.backend.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Service locator to retrieve beans from the Spring context.
 * This is used to break circular dependencies between services.
 */
@Component
public class ServiceLocator implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    /**
     * Get a bean by type.
     *
     * @param beanClass The class of the bean to retrieve
     * @param <T> The type of the bean
     * @return The bean instance
     */
    public static <T> T getBean(Class<T> beanClass) {
        return applicationContext.getBean(beanClass);
    }

}