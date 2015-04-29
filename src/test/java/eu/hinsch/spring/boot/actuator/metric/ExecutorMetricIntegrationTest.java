package eu.hinsch.spring.boot.actuator.metric;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Created by lh on 25/04/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ExecutorMetricIntegrationTest.TestConfig.class)
@WebAppConfiguration
@DirtiesContext
public class ExecutorMetricIntegrationTest {

    private ExecutorMetric executorMetric;
    private MockMvc mockMvc;

    @Configuration
    @EnableAutoConfiguration
    static class TestConfig {
        @Bean
        public ExecutionMetricFactory executionMetricFactory(CounterService gaugeService, GaugeService counterService) {
            return new ExecutionMetricFactory(gaugeService, counterService);
        }
    }

    @Autowired
    private ExecutionMetricFactory executionMetricFactory;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() {
        executorMetric = executionMetricFactory.executorMetric("test");
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void shouldExposeMeasurementAsMetrics() throws Exception {
        // when
        executorMetric.measure(() -> sleep(50));
        executorMetric.measure(() -> sleep(100));

        // then
        mockMvc.perform(get("/metrics"))
                .andExpect(jsonPath("$.['gauge.test.last']").value(greaterThanOrEqualTo(100.0)))
                .andExpect(jsonPath("$.['gauge.test.average']").value(both(greaterThanOrEqualTo(75.0)).and(lessThan(100.0))))
                .andExpect(jsonPath("$.['gauge.test.min']").value(both(greaterThanOrEqualTo(50.0)).and(lessThan(75.0))))
                .andExpect(jsonPath("$.['gauge.test.max']").value(greaterThanOrEqualTo(100.0)))
                .andExpect(jsonPath("$.['counter.test']").value(2));
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {}
    }
}
