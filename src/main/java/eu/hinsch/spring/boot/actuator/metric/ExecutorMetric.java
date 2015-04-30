package eu.hinsch.spring.boot.actuator.metric;

import org.slf4j.Logger;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.boot.logging.LogLevel;
import org.springframework.util.StopWatch;

/**
 * Created by lh on 23/04/15.
 */
public class ExecutorMetric extends AbstractExecutionMetric {

    public ExecutorMetric(final GaugeService gaugeService,
                          final CounterService counterService, final String name) {
        super(gaugeService, counterService, name);
    }

    public ExecutorMetric(GaugeService gaugeService,
                          CounterService counterService,
                          String name,
                          Logger logger,
                          LogLevel logLevel) {
        super(gaugeService, counterService, name, logger, logLevel);
    }

    public void measure(Runnable runnable) {
        final StopWatch stopWatch = start();
        runnable.run();
        finish(stopWatch);
    }
}
