package eu.hinsch.spring.boot.actuator.metric;

import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by lh on 28/04/15.
 */
@Configuration
public class ExecutorMetricAutoConfiguration {
    @Bean
    public DurationMetricAspect durationMetricAspect(GaugeService gaugeService, CounterService counterService) {
        return new DurationMetricAspect(gaugeService, counterService);
    }
}
