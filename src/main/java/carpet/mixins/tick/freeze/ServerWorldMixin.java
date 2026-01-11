package carpet.mixins.tick.freeze;

import carpet.fakes.MinecraftServerF;
import carpet.fakes.WorldF;
import carpet.helpers.ServerTickRateManager;
import carpet.helpers.TickRateManager;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.PortalForcer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.NaturalSpawner;
import net.minecraft.world.World;
import net.minecraft.world.WorldData;
import net.minecraft.world.village.SavedVillageData;
import net.minecraft.world.village.VillageSiege;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements WorldF {
    @Shadow
    @Nullable
    public abstract MinecraftServer getServer();

    @Unique
    @Override
    public TickRateManager tickRateManager() {
        return ((MinecraftServerF) getServer()).getTickRateManager();
    }


    @WrapWithCondition(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;tick()V"
            )
    )
    public boolean wrapTickWeather(World instance) {
        return tickRateManager().runsNormally();
    }

    @WrapWithCondition(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/NaturalSpawner;tick(Lnet/minecraft/server/world/ServerWorld;ZZZ)I"
            )
    )
    public boolean wrapMobSpawning(NaturalSpawner instance, ServerWorld world, boolean spawnAnimals, boolean spawnMonsters, boolean spawnRareMobs) {
        return tickRateManager().runsNormally();
    }

    @WrapWithCondition(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/WorldData;setTime(J)V"
            )
    )
    public boolean wrapWorldTimeUpdate(WorldData instance, long time) {
        return tickRateManager().runsNormally();
    }

    @WrapWithCondition(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/WorldData;setTimeOfDay(J)V"
            )
    )
    public boolean wrapDayTimeUpdate(WorldData instance, long time) {
        return tickRateManager().runsNormally();
    }

    @WrapWithCondition(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ServerWorld;doScheduledTicks(Z)Z"
            )
    )
    public boolean wrapTileTicks(ServerWorld instance, boolean flush) {
        return tickRateManager().runsNormally();
    }

    @WrapWithCondition(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ServerWorld;tickChunks()V"
            )
    )
    public boolean wrapChunkTicks(ServerWorld instance) {
        return tickRateManager().runsNormally();
    }

    @WrapWithCondition(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/village/SavedVillageData;tick()V"
            )
    )
    public boolean wrapVillages(SavedVillageData instance) {
        return tickRateManager().runsNormally();
    }

    @WrapWithCondition(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/village/VillageSiege;tick()V"
            )
    )
    public boolean wrapVillageSieges(VillageSiege instance) {
        return tickRateManager().runsNormally();
    }

    @WrapWithCondition(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/PortalForcer;tick(J)V"
            )
    )
    public boolean wrapPortalRemoval(PortalForcer instance, long time) {
        return tickRateManager().runsNormally() && !((ServerTickRateManager) tickRateManager()).deeplyFrozen();
    }

    @WrapWithCondition(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ServerWorld;doBlockEvents()V"
            )
    )
    public boolean wrapBlockEvents(ServerWorld instance) {
        return tickRateManager().runsNormally();
    }
}
