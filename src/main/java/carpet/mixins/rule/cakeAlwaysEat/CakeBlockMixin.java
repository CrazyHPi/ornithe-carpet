package carpet.mixins.rule.cakeAlwaysEat;

import carpet.CarpetSettings;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.CakeBlock;
import net.minecraft.entity.living.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CakeBlock.class)
public abstract class CakeBlockMixin {
    @ModifyExpressionValue(method = "tryEatCake", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/living/player/PlayerEntity;canEat(Z)Z"))
    public boolean useAlwaysEat(boolean original) {
        return CarpetSettings.cakeAlwaysEat || original;
    }
}
