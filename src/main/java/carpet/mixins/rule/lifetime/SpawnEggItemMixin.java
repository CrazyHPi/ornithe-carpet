package carpet.mixins.rule.lifetime;

import carpet.fakes.EntityF;
import carpet.helpers.lifetime.spawning.LiteralSpawningReason;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SpawnEggItem.class)
public abstract class SpawnEggItemMixin {
    @WrapOperation(
            method = "spawnEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addEntity(Lnet/minecraft/entity/Entity;)Z")
    )
    private static boolean recordItemSpawning(World world, Entity entity, Operation<Boolean> original) {
        ((EntityF) entity).recordSpawning(LiteralSpawningReason.ITEM);
        return original.call(world, entity);
    }
}
