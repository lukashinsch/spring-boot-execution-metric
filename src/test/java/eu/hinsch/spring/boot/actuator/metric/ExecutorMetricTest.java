package eu.hinsch.spring.boot.actuator.metric;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

/**
 * Created by lh on 25/04/15.
 */
public class ExecutorMetricTest extends AbstractMetricTest {

    protected ExecutorMetric testExecutor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        testExecutor = executionMetricFactory.executorMetric("testExecutor", logger);
    }

    @Test
    public void shouldMeasureRuntime() {
        // when
        testExecutor.measure(() -> sleep(50));

        // then
        assertMeasurements(50.0, 50.0, 50.0, 50.0, 1);
    }

    @Test
    public void shouldMeasureMultipleRuntimes() {
        // when
        testExecutor.measure(() -> sleep(50));
        testExecutor.measure(() -> sleep(20));
        testExecutor.measure(() -> sleep(30));
        testExecutor.measure(() -> sleep(40));

        // then
        assertMeasurements(40.0, 35, 20.0, 50.0, 4);

    }

}