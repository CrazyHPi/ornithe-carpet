package carpet.mixins.log.loggers.items;

import carpet.log.framework.LoggerRegistry;
import carpet.log.loggers.items.ItemLogHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    private ItemLogHelper logHelper;

    public ItemEntityMixin(World world) {
        super(world);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/World;DDD)V", at = @At("RETURN"))
    private void initLogger(World world, double x, double y, double z, CallbackInfo ci) {
        if (LoggerRegistry.__items) {
            logHelper = new ItemLogHelper("items");
        }
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/World;isClient:Z",
                    ordinal = 3,
                    shift = At.Shift.BEFORE
            )
    )
    private void log(CallbackInfo ci) {
        if (LoggerRegistry.__items && logHelper != null) {
            logHelper.onTick(x, y, z, velocityX, velocityY, velocityZ);
        }
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/ItemEntity;remove()V",
                    ordinal = 1
            )
    )
    private void logItemDead(CallbackInfo ci) {
        if (LoggerRegistry.__items && logHelper != null) {
            logHelper.onFinish("Despawn Timer");
        }
    }

    @Inject(
            method = "damage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/ItemEntity;remove()V"
            )
    )
    private void logItemDamaged(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (LoggerRegistry.__items && logHelper != null) {
            logHelper.onFinish(source.getName());
        }
    }
}
