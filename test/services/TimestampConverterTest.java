package services;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static services.TimestampConverter.toHoursFromEpoch;

public class TimestampConverterTest {
    @Test
    public void theValueReturnedShouldIncreaseByOneForEachFullHourPassed() {
        final String aprilFirst_0000_2018_GMT_Millis = "1522540800000";
        final String aprilFirst_0000_2018_GMT_Hours = "422928";
        assertThat(toHoursFromEpoch(aprilFirst_0000_2018_GMT_Millis), is(aprilFirst_0000_2018_GMT_Hours));

        final String aprilFirst_0059_2018_GMT_Millis = "1522544399999";
        assertThat(toHoursFromEpoch(aprilFirst_0059_2018_GMT_Millis), is(aprilFirst_0000_2018_GMT_Hours));

        final String aprilFirst_0100_2018_GMT_Millis = "1522544400000";
        final String aprilFirst_0100_2018_GMT_Hours = "422929";
        assertThat(toHoursFromEpoch(aprilFirst_0100_2018_GMT_Millis), is(aprilFirst_0100_2018_GMT_Hours));
    }
}
