package carpet.mixins.command.playerCommand;

import carpet.fakes.PlayerManagerF;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Shadow
    private PlayerManager playerManager;

    @Inject(
            method = "stop",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerManager;saveAll()V"
            )
    )
    private void savingFakePlayers(CallbackInfo ci) {
        ((PlayerManagerF) playerManager).removeBotTeam();
        ((PlayerManagerF) playerManager).storeFakePlayerData();
    }
}
