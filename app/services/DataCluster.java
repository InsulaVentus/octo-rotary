package services;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Offloadable;
import com.hazelcast.map.AbstractEntryProcessor;
import models.Events;
import play.inject.ApplicationLifecycle;
import play.libs.concurrent.HttpExecutionContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

@Singleton
public class DataCluster {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("application");

    private final ApplicationLifecycle appLifecycle;

    private final HttpExecutionContext executionContext;

    private final IMap<String, Events> analytics;

    @Inject
    public DataCluster(ApplicationLifecycle applicationLifecycle, HttpExecutionContext executionContext) {
        this.appLifecycle = applicationLifecycle;
        this.executionContext = executionContext;

        Config hazelcastConfig = new Config();
        hazelcastConfig.setProperty("hazelcast.logging.type", "slf4j");

        HazelcastInstance instance = Hazelcast.newHazelcastInstance(hazelcastConfig);
        analytics = instance.getMap("analytics");
    }

    public CompletionStage<String> addEvent(final String millisSinceEpoch, final String userName, final String event) {
        logger.debug(String.format("Add event: %s - %s - %s", millisSinceEpoch, userName, event));

        Supplier<String> addEventSupplier = () -> {
            if (analytics.putIfAbsent(millisSinceEpoch, new Events(userName, event)) != null) {
                return (String) analytics.executeOnKey(millisSinceEpoch, new EntryProcessor(userName, event));
            }
            return Thread.currentThread().getName();
        };

        return CompletableFuture.supplyAsync(addEventSupplier, executionContext.current());
    }

    private static class EntryProcessor extends AbstractEntryProcessor<String, Events> implements Offloadable {

        private final String userName;
        private final String event;

        EntryProcessor(final String userName, final String event) {
            this.userName = userName;
            this.event = event;
        }

        @Override
        public Object process(Map.Entry<String, Events> entry) {
            Events currentEvents = entry.getValue();
            currentEvents.add(userName, event);
            entry.setValue(currentEvents);

            return Thread.currentThread().getName();
        }

        @Override
        public String getExecutorName() {
            return OFFLOADABLE_EXECUTOR;
        }
    }
}
