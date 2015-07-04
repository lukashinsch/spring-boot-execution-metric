package eu.hinsch.spring.boot.actuator.metric;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.logging.LogLevel;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by lh on 25/04/15.
 */
public class ThrowingSupplierMetricTest extends AbstractMetricTest {

    protected ThrowingSupplierMetric<String> testExecutorWithLogger;
    protected ThrowingSupplierMetric<String> testExecutorWithoutLogger;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        testExecutorWithLogger = executionMetricFactory.throwingSupplierMetric("testExecutor", logger, LogLevel.INFO);
        testExecutorWithoutLogger = executionMetricFactory.throwingSupplierMetric("testExecutor");
    }

    @Test
    public void shouldMeasureRuntime() throws Throwable {
        // when
        String value = testExecutorWithLogger.measure(() -> getValue(50));

        // then
        assertThat(value, is("TEST50"));
        assertMeasurements(50.0, 50.0, 50.0, 50.0, 1);
        assertLogger(1);
    }

    @Test
    public void shouldMeasureMultipleRuntimes() throws Throwable {
        // when
        testExecutorWithLogger.measure(() -> getValue(50));
        testExecutorWithLogger.measure(() -> getValue(20));
        testExecutorWithLogger.measure(() -> getValue(30));
        testExecutorWithLogger.measure(() -> getValue(40));

        // then
        assertMeasurements(40.0, 35, 20.0, 50.0, 4);
        assertLogger(4);
    }

    @Test
    public void shouldMeasureRuntimeWithoutLogger() throws Throwable {
        // when
        String value = testExecutorWithoutLogger.measure(() -> getValue(50));

        // then
        assertThat(value, is("TEST50"));
        assertMeasurements(50.0, 50.0, 50.0, 50.0, 1);
    }

    private String getValue(int ms) {
        sleep(ms);
        return "TEST" + ms;
    }

}