package eu.hinsch.spring.boot.actuator.metric;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Random;

@SpringBootApplication
@Controller("/test")
public class SpringBootExecutionMetricTestApplication {

    private final Random random = new Random();

    public static void main(String[] args) {
        SpringApplication.run(SpringBootExecutionMetricTestApplication.class, args);
    }

    @Autowired
    private TestInterface test;

    @RequestMapping
    @ResponseBody
    @ExecutionMetric(value = "test-metric", loglevel = LogLevel.WARN)
    public String aopTest(@RequestParam("param") String param) {
        test.someMethod();
        return param + callSomeExternalSystem();
    }

    @Component
    public static class TestClass implements TestInterface {
        @Override
        @ExecutionMetric(value = "some-method", loglevel = LogLevel.WARN)
        public void someMethod() {
        }
    }

    public interface TestInterface { void someMethod(); }

    private void simulateActivity() {
        try {
            Thread.sleep((long)(random.nextDouble() * 1000));
        } catch (InterruptedException e) {
            // ignored
        }
    }

    private String callSomeExternalSystem() {
        simulateActivity();
        return String.valueOf(random.nextDouble());

    }
}
