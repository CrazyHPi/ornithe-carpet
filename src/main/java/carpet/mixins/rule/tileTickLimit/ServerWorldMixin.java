package carpet.mixins.rule.tileTickLimit;

import carpet.CarpetSettings;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
	@ModifyConstant(method = "doScheduledTicks", constant = @Constant(intValue = 65536), expect = 2)
	public int modifyTileTickLimit(int constant) {
		return CarpetSettings.tileTickLimit;
	}
}
