package de.sven_torben.hystrix_jee;


import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import javax.naming.Context;

class InitialContextRule implements TestRule {

  private final Context context;

  InitialContextRule(Context context) {
    this.context = context;
  }

  @Override
  public Statement apply(Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        String factory = System.getProperty(Context.INITIAL_CONTEXT_FACTORY);
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
            MockInitialContextFactory.class.getName());
        MockInitialContextFactory.setCurrentContext(context);
        try {
          base.evaluate();
        } finally {
          if (factory == null) {
            System.clearProperty(Context.INITIAL_CONTEXT_FACTORY);
          } else {
            System.setProperty(Context.INITIAL_CONTEXT_FACTORY, factory);
          }
          MockInitialContextFactory.clearCurrentContext();
        }
      }
    };
  }
}
