package carpet.fakes;

import carpet.helpers.lifetime.LifeTimeWorldTracker;

public interface ServerWorldF {
    void setLoginMinecartFix(boolean loginMinecartFix);

    LifeTimeWorldTracker getLifeTimeWorldTracker();
}
