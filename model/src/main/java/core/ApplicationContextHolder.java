package core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Holder for the Spring Application Context. This is used so that the application classes can extract
 * Spring wired beans such as datasources and DAOs without having to be fully Spring wired themselves.
 */
public class ApplicationContextHolder implements ApplicationContextAware {

    /** Static reference to the application context. */
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    /**
     * Retrieve a bean by class. This can be used when the bean container only contains a single instance
     * of the desired class.
     * @param beanClass Class of the bean to retrieve from the application context.
     * @return Instance of the class from the application context.
     */
    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

    /**
     * Retrieve a bean by name. This can be used when the bean container contains multiple beans of the same
     * class.
     * @param beanName ID of the bean to retrieve.
     * @return Instance of the class from the application context.
     */
    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }
}
