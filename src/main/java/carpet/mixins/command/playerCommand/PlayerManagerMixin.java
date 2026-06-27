package carpet.mixins.command.playerCommand;

import carpet.CarpetServer;
import carpet.CarpetSettings;
import carpet.fakes.PlayerManagerF;
import carpet.patches.ServerPlayNetworkHandlerFake;
import carpet.patches.ServerPlayerEntityFake;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.network.handler.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin implements PlayerManagerF {
    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    @Final
    private List<ServerPlayerEntity> players;

    @Override
    public void removeBotTeam() {
        for (ServerPlayerEntity player : this.players) {
            if (player instanceof ServerPlayerEntityFake) {
                ServerPlayerEntityFake.removePlayerFromTeams((ServerPlayerEntityFake) player);
            }
        }
    }

    @Override
    public void storeFakePlayerData() {
        ArrayList<String> list = new ArrayList<>();
        if(!CarpetSettings.reloadFakePlayers){
            CarpetServer.saveBots(server, list);
            return;
        }
        for (ServerPlayerEntity p : players) {
            if(p instanceof ServerPlayerEntityFake){
                list.add(ServerPlayerEntityFake.getInfo(p));
            }
        }
        CarpetServer.saveBots(server, list);
    }

    @ModifyExpressionValue(
            method = "onLogin",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/server/network/handler/ServerPlayNetworkHandler"
            )
    )
    private ServerPlayNetworkHandler replaceNetworkHandler(ServerPlayNetworkHandler original,
                                                           @Local(argsOnly = true) Connection connection,
                                                           @Local(argsOnly = true) ServerPlayerEntity player) {
        if (player instanceof ServerPlayerEntityFake) {
            return new ServerPlayNetworkHandlerFake(this.server, connection, player);
        }

        return original;
    }

    @Inject(
            method = "onLogin",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/entity/living/player/ServerPlayerEntity;setWorld(Lnet/minecraft/world/World;)V"
            )
    )
    private void resetToSetPosition(Connection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if (player instanceof ServerPlayerEntityFake) {
            ((ServerPlayerEntityFake) player).resetToSetPosition();
        }
    }

    // weird way to continue in a loop
    // aka kick out fake players from iterator
    @ModifyExpressionValue(
            method = "createForLogin",
            at = @At(
                    value = "INVOKE",
                    target = "java/util/List.iterator()Ljava/util/Iterator;"
            )
    )
    private Iterator<ServerPlayerEntity> killDupeFakePlayer(Iterator<ServerPlayerEntity> original) {
        List<ServerPlayerEntity> result = new ArrayList<>();
        while (original.hasNext()) {
            ServerPlayerEntity player = original.next();
            if (player instanceof ServerPlayerEntityFake) {
                player.discard();
            } else {
                result.add(player);
            }
        }

        return result.iterator();
    }
}
