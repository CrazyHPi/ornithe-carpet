package carpet.mixins.rule.lifetime;

import carpet.fakes.ServerWorldF;
import carpet.helpers.lifetime.LifeTimeWorldTracker;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements ServerWorldF {
    @Unique
    private LifeTimeWorldTracker lifetimeTracker;

    @Override
    public LifeTimeWorldTracker getLifeTimeWorldTracker() {
        if (this.lifetimeTracker == null) {
            this.lifetimeTracker = new LifeTimeWorldTracker((ServerWorld) (Object) this);
        }
        return this.lifetimeTracker;
    }
}
