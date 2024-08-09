package carpet.commands;

import carpet.CarpetSettings;
import carpet.helpers.HopperCounter;
import net.minecraft.item.DyeColor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CounterCommand extends CarpetAbstractCommand {
    @Override
    public String getName() {
        return "counter";
    }

    @Override
    public String getUsage(CommandSource source) {
        return "Usage: counter <color> <reset/realtime>";
    }

    @Override
    public boolean canUse(MinecraftServer server, CommandSource source) {
        return canUseCommand(source, CarpetSettings.hopperCounters);
    }

    @Override
    public void run(MinecraftServer server, CommandSource source, String[] args) throws CommandException {
        if (args.length == 0) {
            msg(source, HopperCounter.formatAll(server, false));
            return;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "realtime":
                msg(source, HopperCounter.formatAll(server, true));
                return;
            case "reset":
                HopperCounter.resetAll(server);
                sendSuccess(source, this, "All counters restarted.");
                return;
        }
        HopperCounter counter = HopperCounter.getCounter(args[0]);
        if (counter == null) throw new IncorrectUsageException("Invalid color");
        if (args.length == 1) {
            msg(source, counter.format(server, false, false));
            return;
        }
        switch (args[1].toLowerCase(Locale.ROOT)) {
            case "realtime":
                msg(source, counter.format(server, true, false));
                return;
            case "reset":
                counter.reset(server);
                sendSuccess(source, this, String.format("%s counters restarted.", args[0]));
                return;
        }
        throw new IncorrectUsageException(getUsage(source));
    }

    @Override
    public List<String> getSuggestions(MinecraftServer server, CommandSource source, String[] args, @Nullable BlockPos pos) {
        if (args.length == 1) {
            List<String> lst = new ArrayList<String>();
            lst.add("reset");
            for (DyeColor clr : DyeColor.values()) {
                lst.add(clr.name().toLowerCase(Locale.ROOT));
            }
            lst.add("cactus");
            lst.add("all");
            lst.add("realtime");
            String[] stockArr = new String[lst.size()];
            stockArr = lst.toArray(stockArr);
            return suggestMatching(args, stockArr);
        }
        if (args.length == 2) {
            return suggestMatching(args, "reset", "realtime");
        }

        return Collections.emptyList();
    }
}
