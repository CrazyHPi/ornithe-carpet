package carpet.mixins.rule.pushLimit;

import carpet.CarpetSettings;
import net.minecraft.block.piston.PistonMoveStructureResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = PistonMoveStructureResolver.class, priority = 420)

public class PistonMoveStructureResolverMixin {
    @ModifyConstant(method = "addColumn", constant = @Constant(intValue = 12), expect = 3)
    private int pushLimit(int constant) {
        return CarpetSettings.pushLimit;
    }
}
