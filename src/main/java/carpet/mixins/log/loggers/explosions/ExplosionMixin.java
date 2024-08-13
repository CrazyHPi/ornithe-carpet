package carpet.mixins.log.loggers.explosions;

import carpet.log.framework.LoggerRegistry;
import carpet.log.loggers.explosions.ExplosionLogHelper;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {
    @Shadow
    @Final
    private World world;

    private ExplosionLogHelper logHelper;

    @Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;DDDFZZ)V", at = @At("RETURN"))
    private void initLogger(World world, Entity source, double x, double y, double z, float power, boolean createFire, boolean destructive, CallbackInfo ci) {
        if (LoggerRegistry.__explosions) {
            logHelper = new ExplosionLogHelper(source, x, y, z, power, createFire);
        }
    }

    @Inject(method = "damageBlocks", at = @At("RETURN"))
    private void logExplosionDone(boolean createFire, CallbackInfo ci) {
        if (LoggerRegistry.__explosions && logHelper != null) {
            logHelper.onExplosionDone(world.getTime());
        }
    }
}
