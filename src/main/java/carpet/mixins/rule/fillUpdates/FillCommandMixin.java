package carpet.mixins.rule.fillUpdates;

import carpet.utils.MixinGlobals;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.FillCommand;
import net.minecraft.server.command.source.CommandSource;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FillCommand.class)
public abstract class FillCommandMixin {
    @WrapMethod(method = "run")
    public void pushPopYeetUpdateFlags(MinecraftServer server, CommandSource source, String[] args, Operation<Void> original) {
        MixinGlobals.pushYeetUpdateFlags();
        try {
            original.call(server, source, args);
        } finally {
            MixinGlobals.restoreYeetUpdateFlags();
        }
    }
}
