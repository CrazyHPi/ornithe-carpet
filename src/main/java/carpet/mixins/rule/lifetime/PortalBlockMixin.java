package carpet.mixins.rule.lifetime;

import carpet.fakes.EntityF;
import carpet.helpers.lifetime.spawning.LiteralSpawningReason;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.PortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.resource.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PortalBlock.class)
public abstract class PortalBlockMixin {
    @WrapOperation(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/SpawnEggItem;spawnEntity(Lnet/minecraft/world/World;Lnet/minecraft/resource/Identifier;DDD)Lnet/minecraft/entity/Entity;"
            )
    )
    private Entity recordPigmanSpawning(World world, Identifier type, double x, double y, double z, Operation<Entity> original) {
        Entity entity = original.call(world, type, x, y, z);
        if (entity != null) {
            ((EntityF) entity).recordSpawning(LiteralSpawningReason.PORTAL_PIGMAN);
        }
        return entity;
    }
}
