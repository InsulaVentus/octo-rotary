package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import scala.concurrent.ExecutionContextExecutor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Singleton
public class AnalyticsController extends Controller {

    private final ExecutionContextExecutor executor;

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("application");

    @Inject
    public AnalyticsController(ExecutionContextExecutor executor) {
        this.executor = executor;
    }

    public CompletionStage<Result> analytics() {

        String timestamps = request().queryString().get("timestamp")[0];

        logger.info(timestamps);

        CompletableFuture<Result> future = new CompletableFuture<>();
        future.complete(Results.ok("I come from the future: " + timestamps));
        return future;
    }
}
