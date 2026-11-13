package carpet.mixins.rule.lifetime;

import carpet.fakes.EntityF;
import carpet.fakes.ServerWorldF;
import carpet.helpers.lifetime.LifeTimeTracker;
import carpet.helpers.lifetime.filter.EntityFilterManager;
import carpet.helpers.lifetime.removal.DeathRemovalReason;
import carpet.helpers.lifetime.removal.LiteralRemovalReason;
import carpet.helpers.lifetime.removal.RemovalReason;
import carpet.helpers.lifetime.removal.TransDimensionRemovalReason;
import carpet.helpers.lifetime.spawning.MobDropSpawningReason;
import carpet.helpers.lifetime.spawning.SpawningReason;
import carpet.helpers.lifetime.spawning.TransDimensionSpawningReason;
import carpet.helpers.lifetime.utils.GameUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityF {
    @Shadow
    public World world;

    @Unique
    private long spawnTime;
    @Unique
    private boolean doLifeTimeTracking;
    @Unique
    private boolean recordedSpawning;
    @Unique
    private boolean recordedRemoval;
    @Unique
    private Vec3d spawningPos;
    @Unique
    private Vec3d removalPos;
    @Unique
    private DamageSource deathDamageSource;
    @Unique
    private int trackId;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initLifeTimeTracker(World worldIn, CallbackInfo ci) {
        this.doLifeTimeTracking = false;
        this.recordedSpawning = false;
        this.recordedRemoval = false;
        if (this.world instanceof ServerWorld) {
            this.spawnTime = ((ServerWorldF) this.world).getLifeTimeWorldTracker().getSpawnStageCounter();
            this.trackId = LifeTimeTracker.getInstance().getCurrentTrackId();
            this.doLifeTimeTracking = LifeTimeTracker.getInstance().willTrackEntity((Entity) (Object) this);
        } else {
            this.trackId = -1;
            this.doLifeTimeTracking = false;
        }
        this.deathDamageSource = null;
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void recordRemovalOnRemove(CallbackInfo ci) {
        DamageSource source = this.getDeathDamageSource();
        if (source != null) {
            this.recordRemoval(new DeathRemovalReason(source));
        } else {
            this.recordRemoval(LiteralRemovalReason.OTHER);
        }
    }

    @Inject(method = "tickVoid", at = @At("HEAD"))
    private void recordVoidRemoval(CallbackInfo ci) {
        this.recordRemoval(LiteralRemovalReason.VOID);
    }

    @WrapOperation(
            method = "dropItem(Lnet/minecraft/item/ItemStack;F)Lnet/minecraft/entity/ItemEntity;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addEntity(Lnet/minecraft/entity/Entity;)Z")
    )
    private boolean recordItemDropSpawning(World world, Entity entity, Operation<Boolean> original) {
        ((EntityF) entity).recordSpawning(new MobDropSpawningReason(((Entity) (Object) this).getClass()));
        return original.call(world, entity);
    }

    @WrapOperation(
            method = "teleportToDimension",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;addEntity(Lnet/minecraft/entity/Entity;)Z")
    )
    private boolean recordTransDimension(ServerWorld serverWorld2, Entity entity, Operation<Boolean> original) {
        this.recordRemoval(new TransDimensionRemovalReason(serverWorld2.dimension.getType()));
        ((EntityF) entity).recordSpawning(new TransDimensionSpawningReason(this.world.dimension.getType()));
        return original.call(serverWorld2, entity);
    }

    @Override
    public int getTrackId() {
        return this.trackId;
    }

    @Override
    public long getLifeTime() {
        return ((ServerWorldF) this.world).getLifeTimeWorldTracker().getSpawnStageCounter() - this.spawnTime;
    }

    @Override
    public Vec3d getSpawningPosition() {
        return this.spawningPos;
    }

    @Override
    public Vec3d getRemovalPosition() {
        return this.removalPos;
    }

    @Override
    public void recordSpawning(SpawningReason reason) {
        if (this.doLifeTimeTracking && !this.recordedSpawning && this.world instanceof ServerWorld && EntityFilterManager.getInstance().test((Entity) (Object) this)) {
            if (((Entity) (Object) this) instanceof MobEntity && !GameUtil.countsTowardsMobcap((Entity) (Object) this)) {
                return;
            }
            this.recordedSpawning = true;
            this.spawningPos = ((Entity) (Object) this).getSourcePos();
            ((ServerWorldF) this.world).getLifeTimeWorldTracker().onEntitySpawn((Entity) (Object) this, reason);
        }
    }

    @Override
    public void recordRemoval(RemovalReason reason) {
        if (this.doLifeTimeTracking && this.recordedSpawning && this.spawningPos != null && !this.recordedRemoval) {
            this.recordedRemoval = true;
            this.removalPos = ((Entity) (Object) this).getSourcePos();
            ((ServerWorldF) this.world).getLifeTimeWorldTracker().onEntityRemove((Entity) (Object) this, reason);
        }
    }

    @Override
    public DamageSource getDeathDamageSource() {
        return this.deathDamageSource;
    }

    @Override
    public void setDeathDamageSource(DamageSource source) {
        this.deathDamageSource = source;
    }
}
