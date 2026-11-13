package carpet.commands;

import carpet.CarpetSettings;
import carpet.helpers.lifetime.LifeTimeTracker;
import carpet.helpers.lifetime.filter.EntityFilterManager;
import carpet.helpers.lifetime.utils.SpecificDetailMode;
import carpet.utils.Messenger;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entities;
import net.minecraft.entity.Entity;
import net.minecraft.resource.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.TargetSelector;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LifeTimeCommand extends CarpetAbstractCommand {
    private static final String NAME = "lifetime";
    private static final LifeTimeTracker tracker = LifeTimeTracker.getInstance();

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getUsage(CommandSource sender) {
        return "/lifetime tracking [start|stop|restart|realtime]\n" +
                "/lifetime filter <entity_type> [set <selector>|clear]\n" +
                "/lifetime <entity_type> [life_time|spawning|removal [realtime]]";
    }

    @Override
    public boolean canUse(MinecraftServer server, CommandSource source) {
        return canUseCommand(source, CarpetSettings.commandLifeTime);
    }

    @Override
    public void run(MinecraftServer server, CommandSource sender, String[] args) throws CommandException {
        if (args.length == 0) {
            Messenger.m(sender, getUsage(sender));
        } else {
            switch (args[0]) {
                case "tracking":
                    this.executeTracking(server, sender, args);
                    break;
                case "filter":
                    this.setFilter(server, sender, args);
                    break;
                case "help":
                    tracker.showHelp(sender);
                    break;
                default:
                    this.executeDisplayResult(server, sender, args);
                    break;
            }
        }
    }

    private void executeTracking(MinecraftServer server, CommandSource sender, String[] args) throws CommandException {
        if (args.length == 1) {  // lifetime tracking
            tracker.reportTracking(sender, false);
            return;
        }
        switch (args[1]) {
            case "start":
                tracker.startTracking(sender, true);
                break;
            case "stop":
                tracker.stopTracking(sender, true);
                break;
            case "restart":
                tracker.restartTracking(sender);
                break;
            case "realtime":
                tracker.reportTracking(sender, true);
                break;
            default:
                throw new IncorrectUsageException("Unknown command: " + args[1]);
        }
    }

    private void setFilter(MinecraftServer server, CommandSource sender, String[] args) throws CommandException {
        if (args.length == 1) {  // lifetime filter
            EntityFilterManager.getInstance().displayAllFilters(sender);
            return;
        }
        String entityTypeString = args[1];
        Class<? extends Entity> entityType = null;
        if (!entityTypeString.equals("global")) {
            entityType = Entities.REGISTRY.get(new Identifier(entityTypeString));
            if (entityType == null) {
                throw new IncorrectUsageException("Unknown entity type: " + entityTypeString);
            }
        }
        if (args.length < 3) {  // lifetime filter <entity_type>
            EntityFilterManager.getInstance().displayFilter(sender, entityType);
            return;
        }
        switch (args[2]) {
            case "set":
                if (args.length < 4) {
                    throw new IncorrectUsageException("Entity selector is required");
                }
                String selectorString = args[3];
                if (TargetSelector.isValid(selectorString)) {
                    EntityFilterManager.getInstance().setEntityFilter(sender, entityType, selectorString);
                } else {
                    throw new IncorrectUsageException("Invalid entity selector");
                }
                break;
            case "clear":
                EntityFilterManager.getInstance().setEntityFilter(sender, entityType, null);
                break;
            default:
                throw new IncorrectUsageException("Unknown command: " + args[2]);
        }
    }

    private void executeDisplayResult(MinecraftServer server, CommandSource sender, String[] args) {
        String entityTypeInput = args[0];
        if (args.length == 1) {  // lifetime creeper
            tracker.printTrackingResultSpecific(sender, entityTypeInput, null, false);
        } else {
            if (args[1].equals("realtime")) {  // lifetime creeper realtime
                tracker.printTrackingResultSpecific(sender, entityTypeInput, null, true);
            } else if (args.length == 2) {  // lifetime creeper xxx
                tracker.printTrackingResultSpecific(sender, entityTypeInput, args[1], false);
            } else if (args.length == 3 && args[2].equals("realtime")) {  // lifetime creeper xxx realtime
                tracker.printTrackingResultSpecific(sender, entityTypeInput, args[1], true);
            }
        }
    }

    @Override
    public List<String> getSuggestions(MinecraftServer server, CommandSource sender, String[] args, @Nullable BlockPos pos) {
        if (!CarpetSettings.commandLifeTime) {
            return Collections.emptyList();
        }
        Set<String> entityTypes = tracker.getAvailableEntityType().collect(Collectors.toSet());
        if (args.length == 1) {
            List<String> suggestions = Lists.newArrayList(entityTypes);
            suggestions.add("tracking");
            suggestions.add("filter");
            suggestions.add("help");
            return suggestMatching(args, suggestions);
        } else if (args.length == 2 && args[0].equals("tracking")) {
            return suggestMatching(args, "start", "stop", "restart", "realtime");
        } else if (args.length >= 2 && args[0].equals("filter")) {
            if (args.length == 2) {
                List<String> suggestions = Entities.getKeys().stream().map(Identifier::getPath).collect(Collectors.toList());
                suggestions.add("global");
                return suggestMatching(args, suggestions);
            } else if (args.length == 3) {
                return suggestMatching(args, "set", "clear");
            }
        } else if (args.length >= 2 && entityTypes.contains(args[0])) {
            List<String> detailSuggestions = Lists.newArrayList(SpecificDetailMode.getSuggestion());
            if (args.length == 2) {
                detailSuggestions.add("realtime");
                return suggestMatching(args, detailSuggestions);
            } else if (detailSuggestions.contains(args[1])) {
                return suggestMatching(args, "realtime");
            }
        }
        return Collections.emptyList();
    }
}
