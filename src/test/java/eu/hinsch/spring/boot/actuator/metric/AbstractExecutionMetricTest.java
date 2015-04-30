package eu.hinsch.spring.boot.actuator.metric;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.boot.logging.LogLevel;
import org.springframework.util.StopWatch;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by lh on 29/04/15.
 */
public class AbstractExecutionMetricTest {
    private static final String METRIC_NAME = "name";
    private static final String LOGGER_TEMPLATE = "Executing {} took {}ms";

    @Mock
    private Logger logger;

    @Mock
    private CounterService counterService;

    @Mock
    private GaugeService gaugeService;

    @Mock
    private StopWatch stopWatch;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(stopWatch.getLastTaskTimeMillis()).thenReturn(1L);
    }

    @Test
    public void shouldIgnoreMissingLogger() {
        // given
        AbstractExecutionMetric metric = new AbstractExecutionMetric(gaugeService, counterService, "name") {};

        // when
        metric.finish(stopWatch);

        // then
        verify(stopWatch).stop();
    }

    @Test
    public void shouldIgnoreLogLevelOff() {
        // given
        AbstractExecutionMetric metric = metricWithLogLevel(LogLevel.OFF);

        // when
        metric.finish(stopWatch);

        // then
        verifyNoMoreInteractions(logger);
    }

    @Test
    public void shouldLogTrace() {
        // given
        AbstractExecutionMetric metric = metricWithLogLevel(LogLevel.TRACE);

        // when
        metric.finish(stopWatch);

        // then
        verify(logger).trace(LOGGER_TEMPLATE, METRIC_NAME, 1L);
    }

    @Test
    public void shouldLogDebug() {
        // given
        AbstractExecutionMetric metric = metricWithLogLevel(LogLevel.DEBUG);

        // when
        metric.finish(stopWatch);

        // then
        verify(logger).debug(LOGGER_TEMPLATE, METRIC_NAME, 1L);
    }
    @Test
    public void shouldLogInfo() {
        // given
        AbstractExecutionMetric metric = metricWithLogLevel(LogLevel.INFO);

        // when
        metric.finish(stopWatch);

        // then
        verify(logger).info(LOGGER_TEMPLATE, METRIC_NAME, 1L);
    }
    @Test
    public void shouldLogWarn() {
        // given
        AbstractExecutionMetric metric = metricWithLogLevel(LogLevel.WARN);

        // when
        metric.finish(stopWatch);

        // then
        verify(logger).warn(LOGGER_TEMPLATE, METRIC_NAME, 1L);
    }

    @Test
    public void shouldLogError() {
        // given
        AbstractExecutionMetric metric = metricWithLogLevel(LogLevel.ERROR);

        // when
        metric.finish(stopWatch);

        // then
        verify(logger).error(LOGGER_TEMPLATE, METRIC_NAME, 1L);
    }

    @Test
    public void shouldThrowExceptionOnUnsupportedLogLevel() {
        // given
        AbstractExecutionMetric metric = metricWithLogLevel(LogLevel.FATAL);
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("unsupported loglevel: " + LogLevel.FATAL);

        // when
        metric.finish(stopWatch);

        // then -> exception
    }


    private AbstractExecutionMetric metricWithLogLevel(final LogLevel logLevel) {
        return new AbstractExecutionMetric(gaugeService, counterService, METRIC_NAME, logger, logLevel) {};
    }
}