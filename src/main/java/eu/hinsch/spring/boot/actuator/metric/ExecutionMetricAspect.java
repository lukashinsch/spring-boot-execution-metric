package eu.hinsch.spring.boot.actuator.metric;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.asList;

/**
 * Created by lh on 28/04/15.
 */
@Aspect
public class ExecutionMetricAspect {

    private Map<String, ThrowingSupplierMetric<Object>> store = new ConcurrentHashMap<>();
    private final ExecutionMetricFactory factory;

    @Autowired
    public ExecutionMetricAspect(GaugeService gaugeService, CounterService counterService) {
        factory = new ExecutionMetricFactory(counterService, gaugeService);
    }

    @Around("@annotation(eu.hinsch.spring.boot.actuator.metric.ExecutionMetric)")
    public Object measure(ProceedingJoinPoint joinPoint) throws Throwable {
        ExecutionMetric executionMetric = getExecutionMetric(joinPoint);
        Logger targetLogger = LoggerFactory.getLogger(getClassForLogger(joinPoint));
        String metricName = executionMetric.value();

        ThrowingSupplierMetric<Object> supplierMetric = store.computeIfAbsent(metricName,
                (name) -> factory.throwingSupplierMetric(name, targetLogger, executionMetric.loglevel()));

        return supplierMetric.measure(joinPoint::proceed);
    }

    private Class getClassForLogger(ProceedingJoinPoint joinPoint) throws Exception {
        Class<?> loggerClass = AopUtils.getTargetClass(joinPoint.getTarget());
        // TODO why is AopUtils.getTargetClass not working?
        if (loggerClass.getName().contains("$$EnhancerBySpringCGLIB$$")) {
            loggerClass = loggerClass.getSuperclass();
        }
        return loggerClass;
    }

    private ExecutionMetric getExecutionMetric(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        ExecutionMetric executionMetric = method.getAnnotation(ExecutionMetric.class);
        if (executionMetric == null && method.getDeclaringClass().isInterface()) {
            final String methodName = signature.getName();
            final Class<?> implementationClass = joinPoint.getTarget().getClass();
            final Method implementationMethod = implementationClass.getDeclaredMethod(methodName, method.getParameterTypes());
            executionMetric = implementationMethod.getAnnotation(ExecutionMetric.class);
        }
        return executionMetric;
    }
}
