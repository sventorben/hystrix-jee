package de.sven_torben.hystrix_jee;

import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.properties.HystrixProperty;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutors;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JeeHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {

  @Resource(mappedName = "java:comp/DefaultManagedThreadFactory")
  private ManagedThreadFactory mtf;

  public JeeHystrixConcurrencyStrategy() {}

  public JeeHystrixConcurrencyStrategy(String mappedName) throws NamingException {
    InitialContext initialContext = new InitialContext();
    this.mtf = Objects.requireNonNull((ManagedThreadFactory) initialContext.lookup(mappedName));
  }

  public JeeHystrixConcurrencyStrategy(ManagedThreadFactory mtf) {
    this.mtf = Objects.requireNonNull(mtf);
  }

  @Override
  public <T> Callable<T> wrapCallable(Callable<T> callable) {
    return super.wrapCallable(ManagedExecutors.managedTask(callable, null));
  }

  @Override
  public ThreadPoolExecutor getThreadPool(
      HystrixThreadPoolKey threadPoolKey, HystrixProperty<Integer> corePoolSize,
      HystrixProperty<Integer> maximumPoolSize, HystrixProperty<Integer> keepAliveTime,
      TimeUnit unit, BlockingQueue<Runnable> workQueue) {
    return new ThreadPoolExecutor(corePoolSize.get(), maximumPoolSize.get(), keepAliveTime.get(),
        unit, workQueue, mtf);
  }
}
