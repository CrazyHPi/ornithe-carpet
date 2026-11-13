package carpet.mixins.rule.tpAcrossDimensions;

import carpet.CarpetSettings;
import carpet.helpers.TeleportHelper;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.TpCommand;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TpCommand.class)
public abstract class TpCommandMixin {
    @Inject(
            method = "run",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/Entity;world:Lnet/minecraft/world/World;",
                    ordinal = 0,
                    opcode = Opcodes.GETFIELD
            )
    )
    private void tpAcrossDimensions(MinecraftServer server, CommandSource source, String[] args, CallbackInfo ci,
                                    @Local(ordinal = 0) Entity entity, @Local(ordinal = 1) Entity entity2) {
        if (CarpetSettings.tpAcrossDimensions && entity2.world != entity.world) {
            if (entity2 instanceof ServerPlayerEntity && entity instanceof ServerPlayerEntity) {
                TeleportHelper.changeDimensions((ServerPlayerEntity) entity, (ServerPlayerEntity) entity2);
            }
        }
    }
}
