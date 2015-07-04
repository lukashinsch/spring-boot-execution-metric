package eu.hinsch.spring.boot.actuator.metric;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.boot.logging.LogLevel;

/**
 * Created by lh on 23/04/15.
 */
public class ExecutionMetricFactory {

    private final CounterService counterService;
    private final GaugeService gaugeService;

    @Autowired
    public ExecutionMetricFactory(CounterService counterService, GaugeService gaugeService) {
        this.counterService = counterService;
        this.gaugeService = gaugeService;
    }

    public <T> SupplierMetric<T> supplierMetric(String name, Logger logger, LogLevel logLevel) {
        return new SupplierMetric<>(gaugeService, counterService, name, logger, logLevel);
    }

    public <T> SupplierMetric<T> supplierMetric(String name) {
        return new SupplierMetric<>(gaugeService, counterService, name);
    }

    public <T> ThrowingSupplierMetric<T> throwingSupplierMetric(String name, Logger logger, LogLevel logLevel) {
        return new ThrowingSupplierMetric<>(gaugeService, counterService, name, logger, logLevel);
    }

    public <T> ThrowingSupplierMetric<T> throwingSupplierMetric(String name) {
        return new ThrowingSupplierMetric<>(gaugeService, counterService, name);
    }

    public ExecutorMetric executorMetric(String name, Logger logger, LogLevel logLevel) {
        return new ExecutorMetric(gaugeService, counterService, name, logger, logLevel);
    }

    public ExecutorMetric executorMetric(String name) {
        return new ExecutorMetric(gaugeService, counterService, name);
    }


}
