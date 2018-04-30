import com.google.inject.AbstractModule;
import services.DataCluster;

import java.time.Clock;

public class Module extends AbstractModule {
    @Override
    public void configure() {
        bind(Clock.class).toInstance(Clock.systemDefaultZone());
        bind(DataCluster.class).asEagerSingleton();
    }
}
