package de.sven_torben.hystrix_jee;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.strategy.properties.HystrixProperty;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.enterprise.concurrent.ManagedTask;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JeeHystrixConcurrencyStrategyTest {

  private InitialContext context = mock(InitialContext.class);

  @Rule
  public InitialContextRule initialContextRule = new InitialContextRule(context);

  private ManagedThreadFactory defaultManagedThreadFactory = mock(ManagedThreadFactory.class);
  private ManagedThreadFactory testFactory = mock(ManagedThreadFactory.class);

  @Before
  public void setUp() throws NamingException {
    given(context.lookup("java:comp/DefaultManagedThreadFactory"))
        .willReturn(defaultManagedThreadFactory);
  }

  @Test
  public void testDefaultThreadFactory() throws NamingException {
    JeeHystrixConcurrencyStrategy cut = new JeeHystrixConcurrencyStrategy();
    ThreadPoolExecutor threadPoolExecutor = getThreadPool(cut);
    assertThat(threadPoolExecutor.getThreadFactory(), sameInstance(defaultManagedThreadFactory));
  }

  @Test
  public void testJndiLookup() throws NamingException {
    given(context.lookup("testFactory")).willReturn(testFactory );
    JeeHystrixConcurrencyStrategy cut = new JeeHystrixConcurrencyStrategy("testFactory");
    ThreadPoolExecutor threadPoolExecutor = getThreadPool(cut);
    assertThat(threadPoolExecutor.getThreadFactory(), sameInstance(testFactory ));
  }

  @Test
  public void testThreadFactory() {
    JeeHystrixConcurrencyStrategy cut = new JeeHystrixConcurrencyStrategy(testFactory);
    ThreadPoolExecutor threadPoolExecutor = getThreadPool(cut);
    assertThat(threadPoolExecutor.getThreadFactory(), sameInstance(testFactory));
  }

  @Test
  public void testWrapCallable() throws NamingException {
    Callable<Object> callable = new JeeHystrixConcurrencyStrategy().wrapCallable(() -> {
      return null;
    });
    assertThat(callable, instanceOf(ManagedTask.class));
  }

  private ThreadPoolExecutor getThreadPool(JeeHystrixConcurrencyStrategy cut) {
    return cut.getThreadPool(
        HystrixThreadPoolKey.Factory.asKey("JeeHystrixConcurrencyStrategyTest"),
        HystrixProperty.Factory.asProperty(1),
        HystrixProperty.Factory.asProperty(1),
        HystrixProperty.Factory.asProperty(1), TimeUnit.SECONDS,
        new ArrayBlockingQueue<Runnable>(16));
  }
}
