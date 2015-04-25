package eu.hinsch.spring.boot.actuator.metric;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by lh on 25/04/15.
 */
public class SupplierMetricTest extends AbstractMetricTest {

    protected SupplierMetric<String> testExecutor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        testExecutor = executionMetricFactory.supplierMetric("testExecutor", logger);
    }

    @Test
    public void shouldMeasureRuntime() {
        // when
        String value = testExecutor.measure(() -> getValue(50));

        // then
        assertThat(value, is("TEST50"));
        assertMeasurements(50.0, 50.0, 50.0, 50.0, 1);
    }

    @Test
    public void shouldMeasureMultipleRuntimes() {
        // when
        testExecutor.measure(() -> getValue(50));
        testExecutor.measure(() -> getValue(20));
        testExecutor.measure(() -> getValue(30));
        testExecutor.measure(() -> getValue(40));

        // then
        assertMeasurements(40.0, 35, 20.0, 50.0, 4);
    }

    private String getValue(int ms) {
        sleep(ms);
        return "TEST" + ms;
    }

}