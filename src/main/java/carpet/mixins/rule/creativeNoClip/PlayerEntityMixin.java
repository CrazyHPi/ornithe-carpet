package carpet.mixins.rule.creativeNoClip;

import carpet.CarpetSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity {
    @Shadow
    public abstract boolean isCreative();

    @Shadow
    public PlayerAbilities abilities;

    public PlayerEntityMixin(World world) {
        super(world);
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/living/player/PlayerEntity;isSpectator()Z"
            )
    )
    private boolean noClip(PlayerEntity instance) {
        return instance.isSpectator() || (CarpetSettings.creativeNoClip && instance.isCreative() && instance.abilities.flying);
    }

    @Override
    public void move(MoverType moverType, double x, double y, double z) {
        if (moverType == MoverType.SELF || !(CarpetSettings.creativeNoClip && this.isCreative() && this.abilities.flying)) {
            super.move(moverType, x, y, z);
        }
    }
}
