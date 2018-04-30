package controllers;

import models.Events;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import services.DataCluster;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import static java.lang.String.format;

@Singleton
public class AnalyticsController extends Controller {

    private final HttpExecutionContext executionContext;

    private final DataCluster dataCluster;

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("application");

    @Inject
    public AnalyticsController(HttpExecutionContext executionContext, DataCluster dataCluster) {
        this.executionContext = executionContext;
        this.dataCluster = dataCluster;
    }

    public CompletionStage<Result> postAnalytics() {
        Map<String, String[]> queryString = request().queryString();
        logger.debug(request().toString());
        return dataCluster.addEvent(
                getMillisSinceEpoch(queryString),
                getUser(queryString),
                getEvent(queryString)
        ).thenApplyAsync(
                aVoid -> Results.ok(),
                executionContext.current()
        );
    }

    public CompletionStage<Result> getAnalytics() {
        logger.debug(request().toString());
        return dataCluster.getEvents(
                getMillisSinceEpoch(request().queryString())
        ).thenApplyAsync(
                optionalEvents -> optionalEvents
                        .map(events -> Results.ok(getPrintableSummary(events)))
                        .orElseGet(() -> Results.ok(getPrintableSummary(0, 0, 0))),
                executionContext.current()
        );
    }

    private static String getMillisSinceEpoch(final Map<String, String[]> queryString) {
        return queryString.get("timestamp")[0];
    }

    private static String getUser(final Map<String, String[]> queryString) {
        return queryString.get("user")[0];
    }

    private static String getEvent(final Map<String, String[]> queryString) {
        if (queryString.containsKey("click")) {
            return "click";
        } else {
            return "impression";
        }
    }

    private static String getPrintableSummary(final Events events) {
        return getPrintableSummary(events.getUniqueUsers(), events.getClicks(), events.getImpressions());
    }

    private static String getPrintableSummary(final int uniqueUsers, final int clicks, final int impressions) {
        return format(
                "unique_users,%d\nclicks,%d\nimpressions,%d\n",
                uniqueUsers,
                clicks,
                impressions
        );
    }
}
