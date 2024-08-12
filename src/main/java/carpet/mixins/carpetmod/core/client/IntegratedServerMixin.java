package carpet.mixins.carpetmod.core.client;

import carpet.CarpetServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.gen.WorldGeneratorType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin {
    @Inject(method = "loadWorld", at = @At("HEAD"))
    private void onServerLoaded(String saveName, String name, long seed, WorldGeneratorType generatorType, String generatorOptions, CallbackInfo ci) {
        CarpetServer.onServerLoaded((MinecraftServer) (Object) this);
    }

    @Inject(method = "loadWorld", at = @At("RETURN"))
    private void onServerLoadedWorlds(String saveName, String name, long seed, WorldGeneratorType generatorType, String generatorOptions, CallbackInfo ci) {
        CarpetServer.onServerLoadedWorlds((MinecraftServer) (Object) this);
    }
}
