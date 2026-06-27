package carpet.mixins.command.playerCommand;

import carpet.CarpetSettings;
import carpet.helpers.PlayerEntityActionPack;
import carpet.fakes.ServerPlayerEntityF;
import carpet.patches.ServerPlayerEntityFake;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements ServerPlayerEntityF {
    @Final
    @Shadow
    public MinecraftServer server;

    @Unique
    public PlayerEntityActionPack actionPack;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initActionPack(CallbackInfo ci) {
        actionPack = new PlayerEntityActionPack((ServerPlayerEntity) (Object) this);
    }

    @Override
    public PlayerEntityActionPack getActionPack() {
        return actionPack;
    }

    @Override
    public void setActionPack(PlayerEntityActionPack actionPack) {
        this.actionPack = actionPack;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickPlayerAction(CallbackInfo ci) {
        actionPack.onUpdate();
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "incrementStat", at = @At("HEAD"), cancellable = true)
    private void fakePlayerStat(CallbackInfo ci) {
        if (!CarpetSettings.fakePlayerStats && ((ServerPlayerEntity) (Object) this) instanceof ServerPlayerEntityFake) {
            ci.cancel();
        }
    }
}
