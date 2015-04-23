package eu.hinsch.spring.boot.actuator.metric;

import org.slf4j.Logger;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.util.StopWatch;

/**
 * Created by lh on 23/04/15.
 */
public abstract class AbstractExecutionMetric {

    private final GaugeService gaugeService;
    private final CounterService counterService;
    protected final String name;
    private final Logger logger;

    private long count;
    private double average;
    private long max;
    private long min;

    public AbstractExecutionMetric(GaugeService gaugeService, CounterService counterService, String name) {
        this(gaugeService, counterService, name, null);
    }

    public AbstractExecutionMetric(GaugeService gaugeService, CounterService counterService, String name, Logger logger) {
        this.gaugeService = gaugeService;
        this.counterService = counterService;
        this.name = name;
        this.logger = logger;
    }

    protected synchronized void submit(final long duration) {
        updateValues(duration);
        gaugeService.submit(name + ".last", duration);
        gaugeService.submit(name + ".average", Double.valueOf(average).longValue());
        gaugeService.submit(name + ".max", max);
        gaugeService.submit(name + ".min", min);
        counterService.increment(name);
    }

    // TODO polish...
    private void updateValues(final long duration) {
        average = (average * count + duration) / (count + 1);
        count++;
        max = Math.max(max, duration);

        // TODO this will ignore measurement of 0 as min
        min = min > 0 ? Math.min(min, duration) : duration;
    }

    protected void finish(final StopWatch stopWatch) {
        stopWatch.stop();
        final long duration = stopWatch.getLastTaskTimeMillis();
        if (logger != null) {
            logger.debug("Executing {} took {}ms", name, duration);
        }
        submit(duration);
    }

    protected StopWatch start() {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        return stopWatch;
    }
}
