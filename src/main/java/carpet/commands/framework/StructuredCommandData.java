package carpet.commands.framework;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.Command;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public interface StructuredCommandData<T extends Command> {
    void run(T command, MinecraftServer server, CommandSource source) throws CommandException;

    default List<String> getSuggestions(T command, int slot, MinecraftServer server,
                                        CommandSource source, @Nullable BlockPos pos) {
        return Collections.emptyList();
    }

    default int getTargetSelector() {
        return -1;
    }
}
