package carpet.mixins.carpetmod.core;

import carpet.CarpetServer;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public abstract class MainMixin {
    @Inject(method = "main", at = @At("HEAD"))
    private static void initCarpetMod(String[] args, CallbackInfo ci) {
        CarpetServer.onGameStarted();
    }
}
