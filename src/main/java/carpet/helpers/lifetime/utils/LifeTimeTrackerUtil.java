package carpet.helpers.lifetime.utils;

import net.minecraft.entity.Entities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.XpOrbEntity;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.resource.Identifier;

import java.util.Optional;

public class LifeTimeTrackerUtil {
    public static boolean isTrackedEntity(Entity entity) {
        return entity instanceof MobEntity || entity instanceof ItemEntity || entity instanceof XpOrbEntity;
    }

    public static String getEntityTypeDescriptor(Class<? extends Entity> entityType) {
        if (PlayerEntity.class.isAssignableFrom(entityType)) {
            return "player";
        }
        Identifier identifier = Entities.getId(entityType);
        return identifier != null ? identifier.getPath() : entityType.getSimpleName();
    }

    public static Optional<Class<? extends Entity>> getEntityTypeFromName(String name) {
        Identifier identifier = new Identifier(name);
        return Optional.ofNullable(Entities.REGISTRY.get(identifier));
    }
}
