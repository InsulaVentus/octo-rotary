package controllers;

import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;
import services.DataCluster;

import static java.lang.String.format;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static play.test.Helpers.*;

public class AnalyticsControllerIntegrationTest extends WithApplication {

    private static final String APRIL_FIRST_2018_0000$00_GMT_MILLIS = "1522540800000";
    private static final String APRIL_FIRST_2018_0000$05_GMT_MILLIS = "1522540805000";
    private static final String APRIL_FIRST_2018_0100$00_GMT_MILLIS = "1522544400000";
    private static final String APRIL_FIRST_2018_0100$01_GMT_MILLIS = "1522544401000";

    private static final String POST_URI = "/analytics?timestamp=%s&user=%s&%s";
    private static final String GET_URI = "/analytics?timestamp=%s";

    private static final String USER_SAM = "Sam";
    private static final String USER_FRODO = "Frodo";

    private static final String EVENT_IMPRESSION = "impression";
    private static final String EVENT_CLICK = "click";

    private static final String EXPECTED_RESPONSE = "unique_users,%d\nclicks,%d\nimpressions,%d\n";

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    @Test
    public void shouldReturnEmptyResultWhenNoEventsExistForAGivenHour() {
        app.injector().instanceOf(DataCluster.class);
        Result result = route(app, getEvents(APRIL_FIRST_2018_0000$00_GMT_MILLIS));
        assertCorrectnessOfResponse(contentAsString(result), 0, 0, 0);
    }

    @Test
    public void shouldBeAbleToStoreAndBeRetrieveEventsOccurringWithinTheSameHour() {
        app.injector().instanceOf(DataCluster.class);

        // Sam views a page:
        route(app, postEvent(APRIL_FIRST_2018_0000$00_GMT_MILLIS, USER_SAM, EVENT_IMPRESSION));
        // Five seconds later he clicks a couple of buttons:
        route(app, postEvent(APRIL_FIRST_2018_0000$05_GMT_MILLIS, USER_SAM, EVENT_CLICK));
        route(app, postEvent(APRIL_FIRST_2018_0000$05_GMT_MILLIS, USER_SAM, EVENT_CLICK));
        // And an hour later he views a different page:
        route(app, postEvent(APRIL_FIRST_2018_0100$00_GMT_MILLIS, USER_SAM, EVENT_IMPRESSION));

        // Frodo views a page:
        route(app, postEvent(APRIL_FIRST_2018_0100$00_GMT_MILLIS, USER_FRODO, EVENT_IMPRESSION));
        // And clicks a button a second later:
        route(app, postEvent(APRIL_FIRST_2018_0100$01_GMT_MILLIS, USER_FRODO, EVENT_CLICK));

        Result resultFirstHour = route(app, getEvents(APRIL_FIRST_2018_0000$05_GMT_MILLIS));
        final int expectedUniqueUsersFirstHour = 1;
        final int expectedClicksFirstHour = 2;
        final int expectedImpressionsFirstHour = 1;
        assertCorrectnessOfResponse(
                contentAsString(resultFirstHour),
                expectedUniqueUsersFirstHour,
                expectedClicksFirstHour,
                expectedImpressionsFirstHour
        );

        Result resultSecondHour = route(app, getEvents(APRIL_FIRST_2018_0100$01_GMT_MILLIS));
        final int expectedUniqueUsersSecondHour = 2;
        final int expectedClicksSecondHour = 1;
        final int expectedImpressionsSecondHour = 2;
        assertCorrectnessOfResponse(
                contentAsString(resultSecondHour),
                expectedUniqueUsersSecondHour,
                expectedClicksSecondHour,
                expectedImpressionsSecondHour
        );
    }

    private static Http.RequestBuilder getEvents(final String timestamp) {
        return new Http.RequestBuilder()
                .method(GET)
                .uri(format(GET_URI, timestamp));
    }

    private static Http.RequestBuilder postEvent(final String timestamp, final String user, final String event) {
        return new Http.RequestBuilder()
                .method(POST)
                .uri(format(POST_URI, timestamp, user, event));
    }

    private static void assertCorrectnessOfResponse(String actualResponse, int expectedUniqueUsers, int expectedClicks, int expectedImpressions) {
        assertThat(actualResponse, is(format(EXPECTED_RESPONSE, expectedUniqueUsers, expectedClicks, expectedImpressions)));
    }
}
