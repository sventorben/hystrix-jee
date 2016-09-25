package de.sven_torben.hystrix_jee;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

public class MockInitialContextFactory implements InitialContextFactory {

  private static final ThreadLocal<Context> CURRENT_CONTEXT = new ThreadLocal<>();

  @Override
  public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
    return CURRENT_CONTEXT.get();
  }

  public static void setCurrentContext(Context context) {
    CURRENT_CONTEXT.set(context);
  }

  public static void clearCurrentContext() {
    CURRENT_CONTEXT.remove();
  }
}
