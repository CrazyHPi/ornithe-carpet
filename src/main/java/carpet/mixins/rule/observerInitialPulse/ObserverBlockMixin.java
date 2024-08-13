package carpet.mixins.rule.observerInitialPulse;

import carpet.CarpetSettings;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.ObserverBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ObserverBlock.class)
public abstract class ObserverBlockMixin extends FacingBlock {
    @Shadow
    @Final
    public static BooleanProperty POWERED;

    protected ObserverBlockMixin(Material material) {
        super(material);
    }

    @ModifyReturnValue(method = "getPlacementState", at = @At("RETURN"))
    public BlockState controlInitialPulse(BlockState original,
                                          World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
        return original.set(POWERED, !world.isClient && !CarpetSettings.observerInitialPulse);
    }
}
