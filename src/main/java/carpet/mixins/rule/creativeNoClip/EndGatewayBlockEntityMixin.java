package carpet.mixins.rule.creativeNoClip;

import carpet.CarpetSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFilter;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(EndGatewayBlockEntity.class)
public abstract class EndGatewayBlockEntityMixin extends EndPortalBlockEntity {

    @WrapOperation(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;getEntities(Ljava/lang/Class;Lnet/minecraft/util/math/Box;)Ljava/util/List;"
        )
    )
    public List<Entity> creativeNoClipTeleport(World instance, Class<? extends Entity> type, Box bounds, Operation<List<Entity>> original) {

        if (CarpetSettings.creativeNoClip) {
            return instance.getEntities(Entity.class, new Box(this.getPos()),
                input -> EntityFilter.NOT_SPECTATOR.apply(input)
                    && !(input instanceof PlayerEntity
                    && ((PlayerEntity) input).isCreative()
                    && ((PlayerEntity) input).abilities.flying
                )
            );
        } else {
            return original.call(instance, type, bounds);
        }
    }
}
