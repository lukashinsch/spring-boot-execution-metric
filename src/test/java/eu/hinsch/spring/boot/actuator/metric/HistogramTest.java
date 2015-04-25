package eu.hinsch.spring.boot.actuator.metric;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Created by lh on 25/04/15.
 */
public class HistogramTest {

    private Histogram histogram;

    @Before
    public void setUp() throws Exception {
        histogram = new Histogram();
    }

    @Test
    public void shouldRetainFirstValue() {
        // when
        histogram.addValue(10);

        // then
        assertThat(histogram.getAverage(), is(10.0));
        assertThat(histogram.getMax(), is(10L));
        assertThat(histogram.getMin(), is(10L));
    }

    @Test
    public void shouldCalculateNewMinValue() {
        // given
        histogram.addValue(10);

        // when
        histogram.addValue(5);
        assertThat(histogram.getMin(), is(5L));
    }

    @Test
    public void shouldNotChangeMaxValueWhenSmallerValueIsAdded() {
        // given
        histogram.addValue(10);

        // when
        histogram.addValue(5);

        // then
        assertThat(histogram.getMax(), is(10L));
    }

    @Test
    public void shouldCalculateNewMaxValue() {
        // given
        histogram.addValue(10);

        // when
        histogram.addValue(20);

        // then
        assertThat(histogram.getMax(), is(20L));
    }

    @Test
    public void shouldNoChangeMinValueWhenLargerValueIsAdded() {
        // given
        histogram.addValue(10);

        // when
        histogram.addValue(20);

        // then
        assertThat(histogram.getMin(), is(10L));
    }

    @Test
    public void shouldCalculateNewAverage() {
        // given
        histogram.addValue(10);

        // when
        histogram.addValue(20);

        // then
        assertThat(histogram.getAverage(), is(15.0));
    }
}