package controllers;

import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import services.DataCluster;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.CompletionStage;

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

    public CompletionStage<Result> analytics() {
        Map<String, String[]> queryString = request().queryString();
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : queryString.keySet()) {
            stringBuilder.append(String.format("[%s -> %s] ", key, queryString.get(key)[0]));
        }
        logger.debug(stringBuilder.toString());

        return dataCluster.addEvent(getHoursSinceEpoch(queryString), getUser(queryString), getEvent(queryString)).thenApplyAsync(Results::ok, executionContext.current());
    }

    private static String getHoursSinceEpoch(final Map<String, String[]> queryString) {
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
}
