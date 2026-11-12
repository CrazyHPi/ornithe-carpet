package carpet.mixins.command.playerCommand;

import carpet.fakes.ServerWorldF;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldData;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.storage.WorldStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements ServerWorldF {
    protected ServerWorldMixin(WorldStorage storage, WorldData data, Dimension dimension, Profiler profiler, boolean isClient) {
        super(storage, data, dimension, profiler, isClient);
    }

    @Unique
    private boolean loginMinecartFix = false;

    @Definition(id = "entity", local = @Local(type = Entity.class, ordinal = 0, argsOnly = true))
    @Definition(id = "PlayerEntity", type = PlayerEntity.class)
    @Expression("entity instanceof PlayerEntity")
    @Inject(method = "canAddEntity", at = @At("MIXINEXTRAS:EXPRESSION"), cancellable = true)
    private void loginMinecartFix(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (loginMinecartFix) {
            entity.removed = true;
            cir.setReturnValue(true);
        }
    }

    @Override
    public void setLoginMinecartFix(boolean loginMinecartFix) {
        this.loginMinecartFix = loginMinecartFix;
    }
}
