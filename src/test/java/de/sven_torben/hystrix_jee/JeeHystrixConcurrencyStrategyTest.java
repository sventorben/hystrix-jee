package de.sven_torben.hystrix_jee;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.strategy.properties.HystrixProperty;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.enterprise.concurrent.ManagedTask;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@RunWith(MockitoJUnitRunner.class)
public class JeeHystrixConcurrencyStrategyTest {

  InitialContext context = mock(InitialContext.class);

  @Rule
  public InitialContextRule initialContextRule = new InitialContextRule(context);

  @Mock
  ManagedThreadFactory expectedThreadFactory;

  @InjectMocks
  JeeHystrixConcurrencyStrategy cut = new JeeHystrixConcurrencyStrategy();

  @Before
  public void setUp() throws NamingException {
    given(context.lookup("testFactory")).willReturn(expectedThreadFactory);
  }

  @Test
  public void testDefaultThreadFactory() {
    ThreadPoolExecutor threadPoolExecutor = getThreadPool(cut);
    assertThat(threadPoolExecutor.getThreadFactory(), sameInstance(expectedThreadFactory));
  }

  @Test
  public void testJndiLookup() throws NamingException {
    JeeHystrixConcurrencyStrategy cut = new JeeHystrixConcurrencyStrategy("testFactory");
    verify(context).lookup("testFactory");
    ThreadPoolExecutor threadPoolExecutor = getThreadPool(cut);
    assertThat(threadPoolExecutor.getThreadFactory(), sameInstance(expectedThreadFactory));
  }

  @Test
  public void testThreadFactory() {
    JeeHystrixConcurrencyStrategy cut = new JeeHystrixConcurrencyStrategy(expectedThreadFactory);
    ThreadPoolExecutor threadPoolExecutor = getThreadPool(cut);
    assertThat(threadPoolExecutor.getThreadFactory(), sameInstance(expectedThreadFactory));
  }

  @Test
  public void testWrapCallable() {
    Callable<Object> callable = cut.wrapCallable(() -> {
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
