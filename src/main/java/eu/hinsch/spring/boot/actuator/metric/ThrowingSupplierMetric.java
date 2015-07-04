package eu.hinsch.spring.boot.actuator.metric;

import org.slf4j.Logger;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.boot.logging.LogLevel;
import org.springframework.util.StopWatch;

/**
 * Created by lh on 23/04/15.
 */
public class ThrowingSupplierMetric<T> extends AbstractExecutionMetric {

    public ThrowingSupplierMetric(final GaugeService gaugeService,
                                  final CounterService counterService, final String name) {
        super(gaugeService, counterService, name);
    }

    public ThrowingSupplierMetric(GaugeService gaugeService,
                                  CounterService counterService,
                                  String name,
                                  Logger logger,
                                  LogLevel logLevel) {
        super(gaugeService, counterService, name, logger, logLevel);
    }

    public T measure(ThrowingSupplier<T> supplier) throws Throwable {
        final StopWatch stopWatch = start();
        final T result = supplier.get();
        finish(stopWatch);
        return result;
    }

    public interface ThrowingSupplier<T> {
        T get() throws Throwable;
    }
}
