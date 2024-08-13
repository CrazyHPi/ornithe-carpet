package carpet.mixins.log.loggers.mobcaps;

import carpet.utils.SpawnReporter;
import net.minecraft.entity.living.mob.MobCategory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.NaturalSpawner;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(NaturalSpawner.class)
public abstract class NaturalSpawnerMixin {
    @Shadow
    @Final
    private static int MOB_CAPACITY_CHUNK_AREA;

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/living/mob/MobCategory;isPeaceful()Z",
                    ordinal = 0
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void logMobCap(ServerWorld world, boolean spawnAnimals, boolean spawnMonsters, boolean spawnRareMobs, CallbackInfoReturnable<Integer> cir, int i, int o, BlockPos blockPos, MobCategory[] var8, int var9, int var10, MobCategory mobCategory) {
        int dim = world.dimension.getType().getId();
        int count = world.getEntityCount(mobCategory.getType());
        int cap = mobCategory.getCap() * i / MOB_CAPACITY_CHUNK_AREA;
        SpawnReporter.mobcaps.get(dim).put(mobCategory, new Pair<>(count, cap));
    }
}
