package carpet.mixins.rule.creativeNoClip;

import carpet.CarpetSettings;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.piston.PistonMoveBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity {
    @Shadow
    public PlayerAbilities abilities;

    @Shadow
    public abstract boolean isCreative();

    public PlayerEntityMixin(World world) {
        super(world);
    }

    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/living/player/PlayerEntity;isSpectator()Z"
            )
    )
    public boolean noClip(boolean original) {
        return original || (CarpetSettings.creativeNoClip &&
                this.isCreative() && this.abilities.flying);
    }

    @Inject(method = "updatePlayerPose", at = @At(value = "HEAD"), cancellable = true)
    public void noPose(CallbackInfo ci) {
        if (CarpetSettings.creativeNoClip && abilities.creativeMode && abilities.flying) {
            ci.cancel();
        }
    }

    @Intrinsic
    @Override
    public void move(MoverType moverType, double x, double y, double z) {
        if (moverType == MoverType.SELF || !(CarpetSettings.creativeNoClip && this.isCreative() && this.abilities.flying)) {
            super.move(moverType, x, y, z);
        }
    }

    // somehow piston+slime will bounce creativeNoClip players, what did you do carpet client
    @Intrinsic
    @Override
    public PistonMoveBehavior getPistonMoveBehavior() {
        return (CarpetSettings.creativeNoClip && this.isCreative() && this.abilities.flying)
                ? PistonMoveBehavior.IGNORE : super.getPistonMoveBehavior();
    }
}
