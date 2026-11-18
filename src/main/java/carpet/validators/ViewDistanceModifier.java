package carpet.validators;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Validator;
import carpet.utils.Messenger;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.dedicated.DedicatedServer;
import org.jetbrains.annotations.Nullable;

public class ViewDistanceModifier extends Validator<Integer> {

    @Override
    public Integer validate(@Nullable CommandSource source, CarpetRule<Integer> changingRule, Integer newValue, String userInput) {
        if (changingRule.value().equals(newValue) || source == null) {
            return newValue;
        }
        if (newValue != 0 && (newValue < 2 || newValue > 32)) {
            Messenger.m(source, "r view distance has to be 0 or between 2 and 32");
            return null;
        }

        MinecraftServer server = source.getServer();
        if (server != null && server.isDedicated()) {
            int vd = (newValue >= 2) ? newValue: ((DedicatedServer) server).getIntProperty("view-distance", 10);
            if (vd != server.getPlayerManager().getViewDistance()) {
                server.getPlayerManager().updateViewDistance(vd);
            }
            return newValue;
        } else {
            Messenger.m(source, "r view distance can only be changed on a server");
            return 0;
        }
    }

    @Override
    public String description() {
        return "You must choose a value from 0 (use server settings) to 32";
    }
}
