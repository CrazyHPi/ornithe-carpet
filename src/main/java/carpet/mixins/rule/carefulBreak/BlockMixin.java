package carpet.mixins.rule.carefulBreak;

import carpet.CarpetSettings;
import carpet.helpers.CarefulBreakHelper;
import carpet.log.framework.LoggerRegistry;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class BlockMixin {

    @Inject(
            method = "dropItem",
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/world/World;addEntity(Lnet/minecraft/entity/Entity;)Z"
            )
    )
    private static void doCarefulBreakItemCollide(World world, BlockPos pos, ItemStack stack, CallbackInfo ci, @Local ItemEntity item) {
        if (CarpetSettings.carefulBreak) {
            ServerPlayerEntity player = CarefulBreakHelper.miningPlayer.get();
            if (player != null && player.isSneaking() && LoggerRegistry.getPlayerSubscriptions(player.getName()).containsKey("carefulBreak")) {
                item.setNoPickUpDelay();
                item.onPlayerCollision(player);
            }
        }
    }
}
