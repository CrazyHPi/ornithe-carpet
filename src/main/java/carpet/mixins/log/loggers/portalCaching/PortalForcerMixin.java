package carpet.mixins.log.loggers.portalCaching;

import carpet.log.framework.LoggerRegistry;
import carpet.log.loggers.portalCaching.PortalCaching;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.server.world.PortalForcer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;

@Mixin(PortalForcer.class)
public abstract class PortalForcerMixin {
    @Shadow
    @Final
    private Long2ObjectMap<PortalForcer.PortalPos> portalCache;

    @Shadow
    @Final
    private ServerWorld world;


    /**
     * @author Crazy_H
     * @reason carpetmod112 portal caching and logger
     */
    @Overwrite
    public void tick(long time) {
        if (time % 100L == 0L) {
            long l = time - 300L;
            ObjectIterator<PortalForcer.PortalPos> objectIterator = this.portalCache.values().iterator();
            ArrayList<Vec3d> uncachings = new ArrayList<>();

            while (objectIterator.hasNext()) {
                PortalForcer.PortalPos portalPos = (PortalForcer.PortalPos) objectIterator.next();
                if (portalPos == null || portalPos.lastUseTime < l) {
                    uncachings.add(new Vec3d(portalPos.getX(), portalPos.getY(), portalPos.getZ()));
                    objectIterator.remove();
                }
            }

            // Log portal uncaching CARPET-XCOM
            if (LoggerRegistry.__portalCaching) {
                PortalCaching.portalCachingCleared(world, portalCache.size(), uncachings);
            }
        }
    }
}
