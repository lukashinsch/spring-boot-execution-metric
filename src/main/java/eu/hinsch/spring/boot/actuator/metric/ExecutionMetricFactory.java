package eu.hinsch.spring.boot.actuator.metric;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.stereotype.Component;

/**
 * Created by lh on 23/04/15.
 */
@Component
public class ExecutionMetricFactory {

    private final CounterService counterService;
    private final GaugeService gaugeService;

    @Autowired
    public ExecutionMetricFactory(CounterService counterService, GaugeService gaugeService) {
        this.counterService = counterService;
        this.gaugeService = gaugeService;
    }

    public <T> SupplierMetric<T> supplierMetric(String name, Logger logger) {
        return new SupplierMetric<>(gaugeService, counterService, name, logger);
    }

    public <T> SupplierMetric<T> supplierMetric(String name) {
        return new SupplierMetric<>(gaugeService, counterService, name);
    }

    public ExecutorMetric executorMetric(String name, Logger logger) {
        return new ExecutorMetric(gaugeService, counterService, name, logger);
    }

    public ExecutorMetric executorMetric(String name) {
        return new ExecutorMetric(gaugeService, counterService, name);
    }


}
