package carpet.mixins.rule.tntFuseLength;

import carpet.CarpetSettings;
import net.minecraft.entity.PrimedTntEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PrimedTntEntity.class)
public abstract class PrimedTntEntityMixin {
    @ModifyConstant(method = "<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/entity/living/LivingEntity;)V",
            constant = @Constant(intValue = 80))
    public int modifyTntFuseLength(int constant) {
        return CarpetSettings.tntFuseLength;
    }
}
