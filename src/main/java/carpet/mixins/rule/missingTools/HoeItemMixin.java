package carpet.mixins.rule.missingTools;

import carpet.CarpetSettings;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Set;

@Mixin(HoeItem.class)
public abstract class HoeItemMixin extends Item {

    @Shadow
    protected Tier tier;
    @Unique
    private static final Set<Block> EFFECTIVE_BLOCKS = ImmutableSet.of(
            Blocks.NETHER_WART_BLOCK,
            Blocks.HAY,
            Blocks.SPONGE,
            Blocks.LEAVES,
            Blocks.LEAVES2
    );


    // HoeItem is not a ToolItem, but we can still override these from Item

    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        if (CarpetSettings.missingTools && EFFECTIVE_BLOCKS.contains(state.getBlock())) {
            return this.tier.getMiningSpeed();
        }
        return 1.0F;
    }

    @Override
    public boolean mineBlock(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity entity) {
        // damage hoe when used on effective block
        if (CarpetSettings.missingTools && !world.isClient && state.getMiningSpeed(world, pos) != 0.0F) {
            stack.takeDamageAndBreak(1, entity);
            return true;
        }
        return false;
    }

}
