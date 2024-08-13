package carpet.mixins.log.loggers.tnt;

import carpet.log.framework.LoggerRegistry;
import carpet.log.loggers.tnt.TntLogHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.PrimedTntEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PrimedTntEntity.class)
public abstract class PrimedTntEntityMixin extends Entity {
    private TntLogHelper logHelper;

    public PrimedTntEntityMixin(World world) {
        super(world);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/World;)V", at = @At("RETURN"))
    private void initTNTLogger(World world, CallbackInfo ci) {
        if (LoggerRegistry.__tnt && logHelper == null) {
            logHelper = new TntLogHelper();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void primeLogger(CallbackInfo ci) {
        if (LoggerRegistry.__tnt && logHelper != null && !logHelper.initialized) {
            Vec3d angle = new Vec3d(velocityX, velocityY, velocityZ);
            logHelper.onPrimed(x, y, z, angle);
        }
    }

    @Inject(method = "explode", at = @At("RETURN"))
    private void explodeLogger(CallbackInfo ci) {
        if (LoggerRegistry.__tnt && logHelper != null) {
            logHelper.onExploded(x, y, z, world.getTime());
        }
    }
}
