package carpet.mixins.accessor;

import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerManager.class)
public interface PlayerManager_ {
	@Accessor
	boolean getAllowCommands();
}
