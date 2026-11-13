package carpet.mixins.rule.lifetime;

import carpet.fakes.EntityF;
import carpet.helpers.lifetime.removal.LiteralRemovalReason;
import carpet.helpers.lifetime.removal.MobPickupRemovalReason;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.living.mob.MobEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {
    @Inject(
            method = "checkDespawn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/living/mob/MobEntity;remove()V", ordinal = 0)
    )
    private void recordDespawnImmediately(CallbackInfo ci) {
        ((EntityF) this).recordRemoval(LiteralRemovalReason.DESPAWN_IMMEDIATELY);
    }

    @Inject(
            method = "checkDespawn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/living/mob/MobEntity;remove()V", ordinal = 1)
    )
    private void recordDespawnRandomly(CallbackInfo ci) {
        ((EntityF) this).recordRemoval(LiteralRemovalReason.DESPAWN_RANDOMLY);
    }

    @Inject(
            method = "updateInventory",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/living/mob/MobEntity;persistent:Z", opcode = Opcodes.PUTFIELD)
    )
    private void recordMobPickup(ItemEntity item, CallbackInfo ci) {
        ((EntityF) this).recordRemoval(LiteralRemovalReason.PERSISTENT);
        ((EntityF) item).recordRemoval(new MobPickupRemovalReason(((Entity) (Object) this).getClass()));
    }

    @Inject(method = "setPersistent", at = @At("HEAD"))
    private void recordPersistent(CallbackInfo ci) {
        ((EntityF) this).recordRemoval(LiteralRemovalReason.PERSISTENT);
    }
}
