package carpet.mixins.accessor;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(World.class)
public interface WorldA {
    @Accessor("doTicksImmediately")
    void setDoTicksImmediately(boolean doTicksImmediately);

    @Invoker("tickPlayers")
    void invokeTickPlayers();
}
