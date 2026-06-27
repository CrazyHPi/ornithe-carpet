package carpet.mixins.accessor;

import net.minecraft.network.packet.c2s.play.SelectSlotC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SelectSlotC2SPacket.class)
public interface SelectSlotC2SPacketA {

    @Accessor("slot")
    void setSlot(int slot);

}
