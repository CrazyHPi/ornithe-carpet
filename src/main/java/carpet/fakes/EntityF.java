package carpet.fakes;

import carpet.helpers.lifetime.removal.RemovalReason;
import carpet.helpers.lifetime.spawning.SpawningReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.Vec3d;

/**
 * Lifetime-tracking accessors added to {@link net.minecraft.entity.Entity}
 * by {@code carpet.mixins.rule.lifetime.EntityMixin}.
 * Adapted from carpetmod112 lifetime tracker.
 */
public interface EntityF {
    int getTrackId();

    long getLifeTime();

    Vec3d getSpawningPosition();

    Vec3d getRemovalPosition();

    void recordSpawning(SpawningReason reason);

    void recordRemoval(RemovalReason reason);

    DamageSource getDeathDamageSource();

    void setDeathDamageSource(DamageSource source);
}
