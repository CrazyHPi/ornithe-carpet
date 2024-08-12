package carpet.mixins.rule.railPowerLimit;

import carpet.CarpetSettings;
import net.minecraft.block.PoweredRailBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PoweredRailBlock.class)
public abstract class PoweredRailBlockMixin {
	@ModifyConstant(
		method = "isPoweredByConnectedRails",
		constant = @Constant(
			intValue = 8
		)
	)
	private int powerLimit(int original) {
		return CarpetSettings.railPowerLimit - 1;
	}
}
