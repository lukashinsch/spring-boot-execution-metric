package eu.hinsch.spring.boot.actuator.metric;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

/**
 * Created by lh on 25/04/15.
 */
public class ExecutorMetricTest extends AbstractMetricTest {

    protected ExecutorMetric testExecutorWithLogger;
    protected ExecutorMetric testExecutorWithoutLogger;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        testExecutorWithLogger = executionMetricFactory.executorMetric("testExecutor", logger);
        testExecutorWithoutLogger = executionMetricFactory.executorMetric("testExecutor");
    }

    @Test
    public void shouldMeasureRuntime() {
        // when
        testExecutorWithLogger.measure(() -> sleep(50));

        // then
        assertMeasurements(50.0, 50.0, 50.0, 50.0, 1);
        assertLogger(1);
    }

    @Test
    public void shouldMeasureMultipleRuntimes() {
        // when
        testExecutorWithLogger.measure(() -> sleep(50));
        testExecutorWithLogger.measure(() -> sleep(20));
        testExecutorWithLogger.measure(() -> sleep(30));
        testExecutorWithLogger.measure(() -> sleep(40));

        // then
        assertMeasurements(40.0, 35, 20.0, 50.0, 4);
        assertLogger(4);
    }

    @Test
    public void shouldMeasureRuntimeWithoutLogger() {
        // when
        testExecutorWithoutLogger.measure(() -> sleep(50));

        // then
        assertMeasurements(50.0, 50.0, 50.0, 50.0, 1);
    }

}