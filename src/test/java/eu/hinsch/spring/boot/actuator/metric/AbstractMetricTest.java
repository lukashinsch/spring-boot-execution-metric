package eu.hinsch.spring.boot.actuator.metric;

import org.junit.Before;
import org.mockito.*;
import org.slf4j.Logger;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;

import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by lh on 25/04/15.
 */
public abstract class AbstractMetricTest {
    protected  static final long MAX_OVERHEAD = 10L;

    @Mock
    private GaugeService gaugeService;

    @Mock
    private CounterService counterService;

    @Mock
    protected Logger logger;

    @InjectMocks
    protected ExecutionMetricFactory executionMetricFactory;

    @Captor
    private ArgumentCaptor<Double> lastCaptor;

    @Captor
    private ArgumentCaptor<Double> averageCaptor;

    @Captor
    private ArgumentCaptor<Double> minCaptor;

    @Captor
    private ArgumentCaptor<Double> maxCaptor;

    @Captor
    private ArgumentCaptor<Long> logCaptor;

    protected void assertMeasurements(double last, double average, double min, double max, int invocations) {
        verify(counterService, times(invocations)).increment("testExecutor");

        verify(gaugeService, times(invocations)).submit(eq("testExecutor.last"), lastCaptor.capture());
        assertThat(lastCaptor.getValue(), both(greaterThanOrEqualTo(last)).and(lessThan(last + MAX_OVERHEAD)));

        verify(gaugeService, times(invocations)).submit(eq("testExecutor.average"), averageCaptor.capture());
        assertThat(averageCaptor.getValue(), both(greaterThanOrEqualTo(average)).and(lessThan(average + MAX_OVERHEAD)));

        verify(gaugeService, times(invocations)).submit(eq("testExecutor.min"), minCaptor.capture());
        assertThat(minCaptor.getValue(), both(greaterThanOrEqualTo(min)).and(lessThan(min + MAX_OVERHEAD)));

        verify(gaugeService, times(invocations)).submit(eq("testExecutor.max"), maxCaptor.capture());
        assertThat(maxCaptor.getValue(), both(greaterThanOrEqualTo(max)).and(lessThan(max + MAX_OVERHEAD)));
    }

    protected void assertLogger(int invocations) {
        verify(logger, times(invocations)).debug(eq("Executing {} took {}ms"), eq("testExecutor"), anyLong());
    }

    protected void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
