package eu.hinsch.spring.boot.actuator.metric;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.test.OutputCapture;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Created by lh on 29/04/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ExecutionMetricAspectIntegrationTest.TestConfig.class)
@DirtiesContext
public class ExecutionMetricAspectIntegrationTest {

    @Configuration
    @EnableAutoConfiguration
    static class TestConfig {
        @Bean
        public TestBean testBean() {
            return new TestBean();
        }

        @Bean
        @Primary
        public CounterService counterService() {
            return Mockito.mock(CounterService.class);
        }

    }

    public interface TestInterface {
        void interfaceMethod();
    }

    public static class TestBean { // implements TestInterface { // TODO why is this not working???

        @ExecutionMetric("class-method")
        public void classMethod() {}

        @ExecutionMetric(value = "logged-method", loglevel = LogLevel.INFO)
        public void loggerMethod() {}

//        @Override
//        @ExecutionMetric("interface-method")
//        public void interfaceMethod() {}
    }

    @Autowired
    private TestBean testBean;

    @Autowired
    private CounterService counterService;

    @Rule
    public OutputCapture output = new OutputCapture();

    @Test
    public void shouldMeasureClassMethod() {
        // when
        testBean.classMethod();

        // then
        verify(counterService).increment("class-method");
    }

    @Test
    public void shouldLogMessage() {
        // when
        testBean.loggerMethod();

        // then
        assertThat(output.toString(), containsString("IntegrationTest$TestBean : Executing logged-method took"));
    }


//    @Test
//    public void shouldMeasureInterfaceMethod() {
//        // when
//        testBean.interfaceMethod();
//
//        // then
//        verify(counterService).increment("interface-method");
//    }
}
