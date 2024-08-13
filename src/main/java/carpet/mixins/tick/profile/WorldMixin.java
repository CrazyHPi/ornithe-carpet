package carpet.mixins.tick.profile;

import carpet.tick.TickContext;
import carpet.tick.TypedProfiler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(World.class)
public abstract class WorldMixin implements WorldView {
    @Shadow
    @Final
    public boolean isClient;

    @WrapOperation(method = "neighborChanged", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/state/BlockState;neighborChanged(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;)V"))
    public void profileNeighborUpdates(
            BlockState instance, World world, BlockPos blockPos,
            Block neighborBlock, BlockPos neighborPos, Operation<Void> original) {
        if (TickContext.profilingNeighborUpdates && !isClient) {
            TypedProfiler<Block> profiler = TickContext.INSTANCE.updateProfiler;
            profiler.swap(instance.getBlock());
            original.call(instance, world, blockPos, neighborBlock, neighborPos);
            profiler.swap(null);
        } else original.call(instance, world, blockPos, neighborBlock, neighborPos);
    }

    @WrapOperation(method = "tickEntities", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;tick()V"))
    public void profileGlobalEntities(Entity instance, Operation<Void> original) {
        if (TickContext.profilingEntities && !isClient) {
            TypedProfiler<Class<? extends Entity>> profiler = TickContext.INSTANCE.entityProfiler;
            profiler.swap(instance.getClass());
            original.call(instance);
            profiler.swap(null);
        } else original.call(instance);
    }

    @WrapOperation(method = "tickEntities", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;updateEntity(Lnet/minecraft/entity/Entity;)V"))
    public void profileEntities(World instance, Entity entity, Operation<Void> original) {
        if (TickContext.profilingEntities && !isClient) {
            TypedProfiler<Class<? extends Entity>> profiler = TickContext.INSTANCE.entityProfiler;
            profiler.swap(entity.getClass());
            original.call(instance, entity);
            profiler.swap(null);
        } else original.call(instance, entity);
    }

    @WrapOperation(method = "tickEntities", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/util/Tickable;tick()V"))
    public void profileBlockEntities(Tickable instance, Operation<Void> original) {
        if (TickContext.profilingBlockEntities && !isClient) {
            TypedProfiler<Class<? extends BlockEntity>> profiler = TickContext.INSTANCE.blockEntityProfiler;
            BlockEntity blockEntity = (BlockEntity) instance;
            profiler.swap(blockEntity.getClass());
            original.call(instance);
            profiler.swap(null);
        } else original.call(instance);
    }
}
