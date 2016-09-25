package de.sven_torben.hystrix_jee;

import com.netflix.config.ConcurrentCompositeConfiguration;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicConfiguration;
import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.strategy.HystrixPlugins;
import org.apache.commons.configuration.AbstractConfiguration;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class JeeHystrixWebListener implements ServletContextListener {

  private static final String JNDI_INIT_PARAM =
      "de.sven_torben.hystrix_jee.ManagedThreadFactory.jndi";

  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {
    try {
      JeeHystrixConcurrencyStrategy strategy =
          getConcurrencyStrategy(servletContextEvent.getServletContext());
      HystrixPlugins.getInstance().registerConcurrencyStrategy(strategy);
    } catch (NamingException e) {
      throw new IllegalStateException(e);
    }
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

  private static JeeHystrixConcurrencyStrategy getConcurrencyStrategy(ServletContext servletContext)
      throws NamingException {
    Optional<String> jndiName = getJndiName(servletContext);
    if (jndiName.isPresent()) {
      return new JeeHystrixConcurrencyStrategy(jndiName.get());
    }
    return new JeeHystrixConcurrencyStrategy();
  }

  private static Optional<String> getJndiName(ServletContext servletContext) {
    return Optional.ofNullable(servletContext.getInitParameter(JNDI_INIT_PARAM));
  }
}
