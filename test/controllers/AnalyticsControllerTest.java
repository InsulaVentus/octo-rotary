package controllers;

import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;
import services.DataCluster;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static play.test.Helpers.*;

public class AnalyticsControllerTest extends WithApplication {

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    @Test
    public void testAnalytics() {
        DataCluster dataCluster = app.injector().instanceOf(DataCluster.class);
        dataCluster.addEvent("1522540800000", "sam", "click");

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/analytics?timestamp=1522540800000");

        Result result = route(app, request);
        assertThat(contentAsString(result), is(equalTo("unique_users,1\nclicks,1\nimpressions,0\n")));
    }
}
