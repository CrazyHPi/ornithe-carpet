package carpet.helpers.lifetime;

import carpet.fakes.EntityF;
import carpet.fakes.ServerWorldF;
import carpet.helpers.lifetime.utils.LifeTimeTrackerUtil;
import carpet.helpers.lifetime.utils.SpecificDetailMode;
import carpet.helpers.lifetime.utils.TextUtil;
import carpet.utils.Messenger;
import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class LifeTimeTracker extends AbstractTracker {
    private static boolean attachedServer = false;
    private static final LifeTimeTracker INSTANCE = new LifeTimeTracker();

    private int currentTrackId = 0;

    private final Map<ServerWorld, LifeTimeWorldTracker> trackers = Maps.newHashMap();

    public LifeTimeTracker() {
        super("LifeTime");
    }

    public static LifeTimeTracker getInstance() {
        return INSTANCE;
    }

    public LifeTimeWorldTracker getTracker(World world) {
        return world instanceof ServerWorld ? this.trackers.get(world) : null;
    }

    public static void attachServer(MinecraftServer minecraftServer) {
        attachedServer = true;
        INSTANCE.trackers.clear();
        for (ServerWorld world : minecraftServer.worlds) {
            INSTANCE.trackers.put(world, ((ServerWorldF) world).getLifeTimeWorldTracker());
        }
    }

    public static boolean isActivated() {
        return attachedServer && INSTANCE.isTracking();
    }

    public boolean willTrackEntity(Entity entity) {
        return isActivated() &&
                ((EntityF) entity).getTrackId() == this.getCurrentTrackId() &&
                LifeTimeTrackerUtil.isTrackedEntity(entity);
    }

    public Stream<String> getAvailableEntityType() {
        if (!isActivated()) {
            return Stream.empty();
        }
        return this.trackers.values().stream().
                flatMap(
                        tracker -> tracker.getDataMap().keySet().
                                stream().map(LifeTimeTrackerUtil::getEntityTypeDescriptor)
                ).
                distinct();
    }

    public int getCurrentTrackId() {
        return this.currentTrackId;
    }

    @Override
    protected void initTracker() {
        this.currentTrackId++;
        this.trackers.values().forEach(LifeTimeWorldTracker::initTracker);
    }

    @Override
    protected void printTrackingResult(CommandSource source, boolean realtime) {
        try {
            long ticks = this.sendTrackedTime(source, realtime);
            int count = this.trackers.values().stream().
                    mapToInt(tracker -> tracker.print(source, ticks, null, null)).
                    sum();
            if (count == 0) {
                Messenger.m(source, Messenger.s("No result yet"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendUnknownEntity(CommandSource source, String entityTypeString) {
        Messenger.m(source, "r " + String.format("Unknown entity type \"%s\"", entityTypeString));
    }

    private void printTrackingResultSpecificInner(CommandSource source, String entityTypeString, String detailModeString, boolean realtime) {
        Optional<Class<? extends Entity>> entityTypeOptional = LifeTimeTrackerUtil.getEntityTypeFromName(entityTypeString);
        if (entityTypeOptional.isPresent()) {
            SpecificDetailMode detailMode = null;
            if (detailModeString != null) {
                try {
                    detailMode = SpecificDetailMode.fromString(detailModeString);
                } catch (IllegalArgumentException e) {
                    Messenger.m(source, "r " + String.format("Invalid statistic detail \"%s\"", detailModeString));
                    return;
                }
            }

            long ticks = this.sendTrackedTime(source, realtime);
            Class<? extends Entity> entityType = entityTypeOptional.get();
            Messenger.m(source, Messenger.s("Life time result for " + LifeTimeTrackerUtil.getEntityTypeDescriptor(entityType)));
            SpecificDetailMode finalDetailMode = detailMode;
            int count = this.trackers.values().stream().
                    mapToInt(tracker -> tracker.print(source, ticks, entityType, finalDetailMode)).
                    sum();
            if (count == 0) {
                Messenger.m(source, Messenger.s("No result yet"));
            }
        } else {
            this.sendUnknownEntity(source, entityTypeString);
        }
    }

    public int printTrackingResultSpecific(CommandSource source, String entityTypeString, String detailModeString, boolean realtime) {
        return this.doWhenTracking(source, () -> this.printTrackingResultSpecificInner(source, entityTypeString, detailModeString, realtime));
    }

    public int showHelp(CommandSource source) {
        String docLink = "https://github.com/TISUnion/TISCarpet113/blob/TIS-Server/docs/Features.md#lifetime";
        source.sendMessage(Messenger.c(
                String.format("wb %s\n", this.getTranslatedNameFull()),
                "w A tracker to track lifetime and spawn / removal reasons from all newly spawned and removed entities\n",
                "w Complete doc ",
                TextUtil.getFancyText(
                        null,
                        Messenger.s("here", "ut"),
                        Messenger.s(docLink, "t"),
                        new ClickEvent(ClickEvent.Action.OPEN_URL, docLink)
                )
        ));
        return 1;
    }
}
