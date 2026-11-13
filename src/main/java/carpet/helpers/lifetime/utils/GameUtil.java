package carpet.helpers.lifetime.utils;

import carpet.CarpetServer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.mob.MobEntity;

public class GameUtil {
    public static long getGameTime() {
        return CarpetServer.minecraftServer.getWorld(0).getTime();
    }

    public static boolean isOnServerThread() {
        return CarpetServer.minecraftServer != null && CarpetServer.minecraftServer.isOnSameThread();
    }

    public static boolean countsTowardsMobcap(Entity entity) {
        if (entity instanceof MobEntity) {
            MobEntity mobEntity = (MobEntity) entity;
            return !mobEntity.isPersistent();
        }
        return false;
    }
}
