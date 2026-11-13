package carpet.mixins.tick.freeze;

import carpet.fakes.WorldF;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.entity.Entity;
import net.minecraft.util.Tickable;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(World.class)
public abstract class WorldMixin implements WorldF {

    // World.globalEntities, for weather entities(seems only contain lightning bolt)
    // lightning bolt
    @WrapWithCondition(
            method = "tickEntities",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/Entity;ticks:I",
                    opcode = 181 /* PUTFIELD */
            )
    )
    public boolean freezeLightningBoltTime(Entity instance, int value) {
        return this.tickRateManager().shouldEntityTick(instance);
    }

    // lightning bolt
    @WrapWithCondition(
            method = "tickEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;tick()V"
            )
    )
    public boolean freezeLightningBoltTick(Entity instance) {
        return this.tickRateManager().shouldEntityTick(instance);
    }

    // World.entities, for all loaded entities
    // loaded entity
    @WrapWithCondition(
            method = "tickEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;tickEntity(Lnet/minecraft/entity/Entity;)V"
            )
    )
    public boolean freezeEntityUpdate(World instance, Entity entity) {
        return this.tickRateManager().shouldEntityTick(entity);
    }

    // World.tickingBlockEntities
    // block entity
    @WrapWithCondition(
            method = "tickEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/Tickable;tick()V"
            )
    )
    public boolean freezeBlockEntityTick(Tickable instance) {
        return this.tickRateManager().runsNormally();
    }
}
