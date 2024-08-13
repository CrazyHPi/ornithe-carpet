package carpet.mixins.log.loggers.weather;

import carpet.log.framework.LoggerRegistry;
import carpet.utils.Messenger;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.minecraft.world.WorldData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public abstract class WorldMixin {
    @Shadow
    protected WorldData data;

    @Shadow
    @Nullable
    public abstract MinecraftServer getServer();

    @Inject(
            method = "tickWeather",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/WorldData;setThunderTime(I)V",
                    shift = At.Shift.AFTER
            ),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/WorldData;setThunderTime(I)V",
                            ordinal = 1
                    ),
                    to = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/WorldData;setThunderTime(I)V",
                            ordinal = 2,
                            shift = At.Shift.AFTER
                    )
            )
    )
    private void logThunder(CallbackInfo ci) {
        if (LoggerRegistry.__weather) {
            LoggerRegistry.getLogger("weather").log(() -> new Text[]{
                    Messenger.s("Thunder is set to: " + this.data.isThundering() + " time: " + this.data.getThunderTime() + " Server time: " + getServer().getTicks())
            });
        }
    }

    @Inject(
            method = "tickWeather",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/WorldData;setRainTime(I)V",
                    shift = At.Shift.AFTER
            ),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/WorldData;setRainTime(I)V",
                            ordinal = 1
                    ),
                    to = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/WorldData;setRainTime(I)V",
                            ordinal = 2,
                            shift = At.Shift.AFTER
                    )
            )
    )
    private void logRain(CallbackInfo ci) {
        if (LoggerRegistry.__weather) {
            LoggerRegistry.getLogger("weather").log(() -> new Text[]{
                    Messenger.s("Rain is set to: " + this.data.isRaining() + " time: " + this.data.getRainTime() + " Server time: " + getServer().getTicks())
            });
        }
    }
}
