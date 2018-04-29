package services;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Offloadable;
import com.hazelcast.map.AbstractEntryProcessor;
import models.Events;
import play.libs.concurrent.HttpExecutionContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static services.TimestampConverter.toHoursFromEpoch;

@Singleton
public class DataCluster {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("application");

    private final HttpExecutionContext executionContext;

    private final IMap<String, Events> analytics;

    @Inject
    public DataCluster(HttpExecutionContext executionContext) {
        this.executionContext = executionContext;

        Config hazelcastConfig = new Config();
        hazelcastConfig.setProperty("hazelcast.logging.type", "slf4j");

        HazelcastInstance instance = Hazelcast.newHazelcastInstance(hazelcastConfig);
        analytics = instance.getMap("analytics");
    }

    public CompletionStage<Void> addEvent(final String millisSinceEpoch, final String userName, final String event) {
        logger.debug(String.format("Add event: %s - %s - %s", millisSinceEpoch, userName, event));
        return CompletableFuture.runAsync(
                () -> {
                    if (analytics.putIfAbsent(toHoursFromEpoch(millisSinceEpoch), new Events(userName, event)) != null) {
                        analytics.executeOnKey(toHoursFromEpoch(millisSinceEpoch), new EntryProcessor(userName, event));
                    }
                },
                executionContext.current()
        );
    }

    public CompletionStage<Optional<Events>> getEvents(final String millisSinceEpoch) {
        return CompletableFuture.supplyAsync(
                () -> Optional.ofNullable(analytics.get(toHoursFromEpoch(millisSinceEpoch))),
                executionContext.current()
        );
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
            currentEvents.addEvent(userName, event);
            entry.setValue(currentEvents);

            return Thread.currentThread().getName();
        }

        @Override
        public String getExecutorName() {
            return OFFLOADABLE_EXECUTOR;
        }
    }
}
