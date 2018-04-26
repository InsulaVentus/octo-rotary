package services;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import play.inject.ApplicationLifecycle;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DataCluster {

    private final ApplicationLifecycle appLifecycle;
    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("application");

    @Inject
    public DataCluster(ApplicationLifecycle applicationLifecycle) {
        this.appLifecycle = applicationLifecycle;
        logger.info("Initializing cluster");

        Config hazelcastConfig = new Config();
        hazelcastConfig.setProperty("hazelcast.logging.type", "slf4j");

        HazelcastInstance instance = Hazelcast.newHazelcastInstance(hazelcastConfig);
        IMap<Object, Object> hmm = instance.getMap("hmm");
//        hmm.lock
    }
}
