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
    private Histogram histogram;

    public AbstractExecutionMetric(GaugeService gaugeService, CounterService counterService, String name) {
        this(gaugeService, counterService, name, null);
    }

    public AbstractExecutionMetric(GaugeService gaugeService, CounterService counterService, String name, Logger logger) {
        this.gaugeService = gaugeService;
        this.counterService = counterService;
        this.name = name;
        this.logger = logger;
        this.histogram = new Histogram();
    }

    protected StopWatch start() {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        return stopWatch;
    }


    protected void finish(final StopWatch stopWatch) {
        stopWatch.stop();
        final long duration = stopWatch.getLastTaskTimeMillis();
        if (logger != null) {
            logger.debug("Executing {} took {}ms", name, duration);
        }
        submit(duration);
    }

    protected synchronized void submit(final long duration) {
        histogram.addValue(duration);
        gaugeService.submit(name + ".last", duration);
        gaugeService.submit(name + ".average", Double.valueOf(histogram.getAverage()).longValue());
        gaugeService.submit(name + ".max", histogram.getMax());
        gaugeService.submit(name + ".min", histogram.getMin());
        counterService.increment(name);
    }
}
