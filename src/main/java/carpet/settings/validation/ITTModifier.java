package carpet.settings.validation;

import carpet.api.settings.Validators;
import carpet.mixins.accessor.World_;
import carpet.CarpetServer;

public class ITTModifier extends Validators.SideEffectValidator<String> {
    @Override
    public String parseValue(String newValue) {
        return "none";
    }

    @Override
    public void performEffect(String newValue) {
        if ("none".equals(newValue)) return;
        int worldIndex = 0;
        if (newValue.startsWith("overworld_")) worldIndex = 0;
        else if (newValue.startsWith("nether_")) worldIndex = 1;
        else if (newValue.startsWith("end_")) worldIndex = 2;
        boolean value = false;
        if (newValue.endsWith("false")) value = false;
        else if (newValue.endsWith("true")) value = true;
        ((World_) CarpetServer.minecraftServer.worlds[worldIndex]).setDoTicksImmediately(value);
    }
}
