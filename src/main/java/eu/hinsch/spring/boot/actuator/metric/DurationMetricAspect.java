package eu.hinsch.spring.boot.actuator.metric;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lh on 28/04/15.
 */
@Aspect
public class DurationMetricAspect {

    private Map<String, SupplierMetric<Object>> store = new ConcurrentHashMap<>();
    private final ExecutionMetricFactory factory;

    @Autowired
    public DurationMetricAspect(GaugeService gaugeService, CounterService counterService) {
        factory = new ExecutionMetricFactory(counterService, gaugeService);
    }

    @Around("@annotation(eu.hinsch.spring.boot.actuator.metric.DurationMetric)")
    public Object measure(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DurationMetric durationMetric = method.getAnnotation(DurationMetric.class);

        Logger targetLogger = LoggerFactory.getLogger(method.getDeclaringClass());
        String metricName = durationMetric.value();

        SupplierMetric<Object> supplierMetric = store.computeIfAbsent(metricName,
                (name) -> factory.supplierMetric(name, targetLogger, durationMetric.loglevel()));

        return supplierMetric.measure(() -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable throwable) {
                throw new RuntimeException("error invoking method", throwable);
            }
        });

    }
}
