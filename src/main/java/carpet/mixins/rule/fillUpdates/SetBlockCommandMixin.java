package carpet.mixins.rule.fillUpdates;

import carpet.CarpetSettings;
import carpet.utils.MixinGlobals;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.SetBlockCommand;
import net.minecraft.server.command.source.CommandSource;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SetBlockCommand.class)
public abstract class SetBlockCommandMixin {
    @WrapMethod(method = "run")
    public void pushPopYeetUpdateFlags(MinecraftServer server, CommandSource source, String[] args, Operation<Void> original) {
        if (CarpetSettings.fillUpdates) {
            original.call(server, source, args);
            return;
        }
        MixinGlobals.pushYeetUpdateFlags();
        try {
            original.call(server, source, args);
        } finally {
            MixinGlobals.restoreYeetUpdateFlags();
        }
    }
}
