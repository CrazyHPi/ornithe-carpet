package carpet.mixins.tick.freeze;

import carpet.fakes.MinecraftServerF;
import carpet.helpers.ServerTickRateManager;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements MinecraftServerF {

    @Unique
    private ServerTickRateManager serverTickRateManager;

    @Inject(
            method = "<init>",
            at = @At(
                    "RETURN"
            )
    )
    private void onInit(CallbackInfo ci) {
        serverTickRateManager = new ServerTickRateManager((MinecraftServer) (Object) this);
    }

    @Unique
    @Override
    public ServerTickRateManager getTickRateManager() {
        return serverTickRateManager;
    }

    @Unique
    public ServerTickRateManager tickRateManager() {
        return this.getTickRateManager();
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;tickWorlds()V",
                    shift = At.Shift.BEFORE,
                    ordinal = 0
            )
    )
    private void onTick(CallbackInfo ci) {
        tickRateManager().tick();
    }


    @WrapWithCondition(
            method = "tick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/server/MinecraftServer;ticks:I", opcode = 181 /* PUTFIELD */
            )
    )
    public boolean wrapServerTickUpdate(MinecraftServer instance, int value) {
        return tickRateManager().runsNormally();
    }

    @WrapWithCondition(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;saveWorlds(Z)V"
            )
    )
    public boolean wrapAutosave(MinecraftServer instance, boolean silent) {
        return tickRateManager().runsNormally();
    }
}
