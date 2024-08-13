package carpet.mixins.lifecycle;

import carpet.CarpetServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.gen.WorldGeneratorType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Inject(method = "main", at = @At("HEAD"), require = 0)
    private static void initCarpetMod(String[] args, CallbackInfo ci) {
        CarpetServer.onGameStarted();
    }

    @Inject(method = "loadWorld", at = @At("HEAD"))
    private void onServerLoaded(String saveName, String name, long seed, WorldGeneratorType generatorType, String generatorOptions, CallbackInfo ci) {
        CarpetServer.onServerLoaded((MinecraftServer) (Object) this);
    }

    @Inject(method = "loadWorld", at = @At("RETURN"))
    private void onServerLoadedWorlds(String saveName, String name, long seed, WorldGeneratorType generatorType, String generatorOptions, CallbackInfo ci) {
        CarpetServer.onServerLoadedWorlds((MinecraftServer) (Object) this);
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;tickWorlds()V",
                    shift = At.Shift.BEFORE
            )
    )
    private void tick(CallbackInfo ci) {
        CarpetServer.tick((MinecraftServer) (Object) this);
    }

    @Inject(method = "stop", at = @At("HEAD"))
    private void onServerClosed(CallbackInfo ci) {
        CarpetServer.onServerClosed((MinecraftServer) (Object) this);
    }

    @Inject(method = "stop", at = @At("TAIL"))
    private void onServerDoneClosing(CallbackInfo ci) {
        CarpetServer.onServerDoneClosing((MinecraftServer) (Object) this);
    }
}
