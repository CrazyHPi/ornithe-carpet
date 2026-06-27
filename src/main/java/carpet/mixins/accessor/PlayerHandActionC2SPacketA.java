package carpet.mixins.accessor;

import net.minecraft.network.packet.c2s.play.PlayerHandActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerHandActionC2SPacket.class)
public interface PlayerHandActionC2SPacketA {

    @Accessor("pos")
    void setPos(BlockPos pos);

    @Accessor("face")
    void setFace(Direction face);

    @Accessor("action")
    void setAction(PlayerHandActionC2SPacket.Action action);
}
