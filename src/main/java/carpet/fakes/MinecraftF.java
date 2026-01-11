package carpet.fakes;

import carpet.helpers.TickRateManager;

import java.util.Optional;

public interface MinecraftF {
    Optional<TickRateManager> getTickRateManager();
}
