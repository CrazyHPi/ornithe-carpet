package carpet.utils;

import carpet.CarpetSettings;
import carpet.helpers.HopperCounter;
import net.minecraft.block.Blocks;
import net.minecraft.block.ColoredBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.client.world.color.BlockColor;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WoolTool {
    public static final EnumProperty<DyeColor> COLOR = EnumProperty.<DyeColor>of("color", DyeColor.class);

//    public static void carpetPlacedAction(DyeColor color, PlayerEntity placer, BlockPos pos, World worldIn) {
//        if (!CarpetSettings.carpets) {
//            return;
//        }
//        switch (color) {
//            case PINK:
//                if (CarpetSettings.commandSpawn)
//                    Messenger.send(placer, SpawnReporter.report(pos, worldIn));
//
//                break;
//            case BLACK:
//                if (CarpetSettings.commandSpawn)
//                    Messenger.send(placer, SpawnReporter.show_mobcaps(pos, worldIn));
//                break;
//            case BROWN:
//                if (CarpetSettings.commandDistance) {
//                    DistanceCalculator.report_distance(placer, pos);
//                }
//                break;
//            case GRAY:
//                if (CarpetSettings.commandBlockInfo)
//                    Messenger.send(placer, BlockInfo.blockInfo(pos.down(), worldIn));
//                break;
//            case YELLOW:
//                if (CarpetSettings.commandEntityInfo)
//                    EntityInfo.issue_entity_info(placer);
//                break;
//            case GREEN:
//                if (CarpetSettings.hopperCounters == CarpetSettings.HopperCounters.wool) {
//                    DyeColor under = getWoolColorAtPosition(worldIn, pos.down());
//                    if (under == null) return;
//                    Messenger.send(placer, HopperCounter.COUNTERS.get(under.getName()).format(worldIn.getMinecraftServer(), false, false));
//                } else if (CarpetSettings.hopperCounters == CarpetSettings.HopperCounters.all) {
//                    Messenger.send(placer, HopperCounter.COUNTERS.get("all").format(worldIn.getMinecraftServer(), false, false));
//                }
//                break;
//            case RED:
//                if (CarpetSettings.hopperCounters == CarpetSettings.HopperCounters.wool) {
//                    DyeColor under = getWoolColorAtPosition(worldIn, pos.down());
//                    if (under == null) return;
//                    HopperCounter.COUNTERS.get(under.getName()).reset(worldIn.getMinecraftServer());
//                    Messenger.s(placer, String.format("%s counter reset", under.toString()));
//                } else if (CarpetSettings.hopperCounters == CarpetSettings.HopperCounters.all) {
//                    HopperCounter.COUNTERS.get("all").reset(worldIn.getMinecraftServer());
//                    Messenger.s(placer, "Reset hopper counters");
//                }
//                break;
//        }
//    }

    public static DyeColor getWoolColorAtPosition(World worldIn, BlockPos pos) {
        BlockState state = worldIn.getBlockState(pos);
        if (state.getBlock() != Blocks.WOOL) return null;
        return state.get(ColoredBlock.COLOR);
    }
}