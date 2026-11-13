package carpet.mixins.rule.lifetime;

import carpet.fakes.EntityF;
import carpet.helpers.lifetime.spawning.LiteralSpawningReason;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.SummonCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SummonCommand.class)
public abstract class SummonCommandMixin {
    @WrapOperation(
            method = "run",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;refreshPositionAndAngles(DDDFF)V")
    )
    private void recordSummon(Entity entity, double x, double y, double z, float yaw, float pitch, Operation<Void> original) {
        original.call(entity, x, y, z, yaw, pitch);
        ((EntityF) entity).recordSpawning(LiteralSpawningReason.COMMAND);
    }
}
