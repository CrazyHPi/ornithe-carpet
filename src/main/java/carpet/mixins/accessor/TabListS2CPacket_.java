package carpet.mixins.accessor;

import net.minecraft.network.packet.s2c.play.TabListS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TabListS2CPacket.class)
public interface TabListS2CPacket_ {
    @Accessor("header")
    void setHeader(Text text);

    @Accessor("footer")
    void setFooter(Text text);
}
