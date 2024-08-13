package carpet.mixins.log.loggers.projectiles;

import carpet.log.framework.LoggerRegistry;
import carpet.log.loggers.projectiles.TrajectoryLogHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.thrown.ThrownEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownEntity.class)
public abstract class ThrownEntityMixin extends Entity {
    private TrajectoryLogHelper logHelper;

    public ThrownEntityMixin(World world) {
        super(world);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/World;)V", at = @At("RETURN"))
    private void addLogger(World world, CallbackInfo ci) {
        if (LoggerRegistry.__projectiles) {
            logHelper = new TrajectoryLogHelper("projectiles");
        }
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/thrown/ThrownEntity;setPosition(DDD)V"
            )
    )
    private void tickCheck(CallbackInfo ci) {
        if (LoggerRegistry.__projectiles && logHelper != null) {
            logHelper.onTick(x, y, z, velocityX, velocityY, velocityZ);
        }
    }

    @Override
    public void remove() {
        super.remove();
        if (LoggerRegistry.__projectiles && logHelper != null) {
            logHelper.onFinish();
        }
    }
}
