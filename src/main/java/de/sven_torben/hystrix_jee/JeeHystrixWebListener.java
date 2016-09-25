package de.sven_torben.hystrix_jee;

import com.netflix.config.ConcurrentCompositeConfiguration;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicConfiguration;
import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.strategy.HystrixPlugins;
import org.apache.commons.configuration.AbstractConfiguration;

import java.util.concurrent.TimeUnit;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class JeeHystrixWebListener implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {
    HystrixPlugins.getInstance().registerConcurrencyStrategy(new JeeHystrixConcurrencyStrategy());
  }

  @Override
  public void contextDestroyed(ServletContextEvent servletContextEvent) {
    Hystrix.reset(1, TimeUnit.SECONDS);

    AbstractConfiguration configInstance = ConfigurationManager.getConfigInstance();
    if (configInstance instanceof DynamicConfiguration) {
      ((DynamicConfiguration) configInstance).stopLoading();
    } else if (configInstance instanceof ConcurrentCompositeConfiguration) {
      ConcurrentCompositeConfiguration config = ((ConcurrentCompositeConfiguration) configInstance);
      config.getConfigurations().stream()
          .filter(c -> c instanceof  DynamicConfiguration)
          .map(c -> (DynamicConfiguration) c)
          .forEach(DynamicConfiguration::stopLoading);
    }
  }
}
