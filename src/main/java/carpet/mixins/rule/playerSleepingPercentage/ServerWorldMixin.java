package carpet.mixins.rule.playerSleepingPercentage;

import carpet.CarpetSettings;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockableEventLoop;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldData;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.storage.WorldStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements BlockableEventLoop {
    @Shadow
    private boolean allPlayersSleeping;

    protected ServerWorldMixin(WorldStorage storage, WorldData data, Dimension dimension, Profiler profiler, boolean isClient) {
        super(storage, data, dimension, profiler, isClient);
    }

    @Definition(id = "players", field = "Lnet/minecraft/server/world/ServerWorld;players:Ljava/util/List;")
    @Definition(id = "size", method = "Ljava/util/List;size()I")
    @Expression("this.players.size() - ?")
    @ModifyExpressionValue(method = "updatePlayersSleepingStatus", at = @At("MIXINEXTRAS:EXPRESSION"))
    public int customPlayerSleepingPercentage(int original) {
        // ceil(original * percentage * 0.01)
        return (original * CarpetSettings.playersSleepingPercentage + 99) / 100;
    }

    @Inject(method = "canSkipNight", at = @At("HEAD"), cancellable = true)
    private void skipNight(CallbackInfoReturnable<Boolean> cir) {
        if (this.allPlayersSleeping && !this.isClient) {
            if (CarpetSettings.playersSleepingPercentage < 100) {
                float numberOfPlayers = 0;
                float sleepingPlayerCount = 0;

                for (PlayerEntity player : this.players) {
                    if (!player.isSpectator()) {
                        numberOfPlayers++;
                        if (player.isSleptEnough()) {
                            sleepingPlayerCount++;
                        }
                    }
                }

                cir.setReturnValue(CarpetSettings.playersSleepingPercentage <= (int) ((sleepingPlayerCount / numberOfPlayers) * 100));
            }
        }
    }
}

