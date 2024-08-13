package carpet.commands.framework;

import carpet.utils.algebraic.StructuredAdtMatcher;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.AbstractCommand;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StructuredCommand<D extends StructuredCommandData> extends AbstractCommand {
    private final StructuredAdtMatcher<D> matcher;
    private final String name;

    protected StructuredCommand(Class<D> dataClass, String name) {
        this.matcher = new StructuredAdtMatcher<>(dataClass);
        this.name = name;
    }

    protected D parseData(String[] args) {
        D data = matcher.clear().append(Arrays.asList(args)).matchedValue();
        return data;
    }

    protected D parsePartialData(String[] args) {
        D data = matcher.clear().append(Arrays.asList(args)).nonFailureValue();
        return data;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getUsage(CommandSource source) {
        return getName();
    }

    @Override
    public List<String> getSuggestions(MinecraftServer server, CommandSource source, String[] args, @Nullable BlockPos pos) {
        D data = parsePartialData(args);
        if (data == null) return Collections.emptyList();
        return data.getSuggestions(this, args.length, server, source, pos);
    }

    @Override
    public boolean hasTargetSelectorAt(String[] args, int index) {
        D data = parseData(args);
        if (data == null) return false;
        return index == data.getTargetSelector();
    }

    @Override
    public void run(MinecraftServer server, CommandSource source, String[] args) throws CommandException {
        D data = parseData(args);
        if (data == null) throw new IncorrectUsageException("Command data parse failed");
        data.run(this, server, source);
    }
}
