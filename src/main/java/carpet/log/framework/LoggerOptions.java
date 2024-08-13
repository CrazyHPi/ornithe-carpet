package carpet.log.framework;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.stat.StatProgress;


public class LoggerOptions implements StatProgress {
    // for storing user's subscribed loggers
    public String logger;
    public String option;

    LoggerOptions() {
    }

    public LoggerOptions(String logger, String option) {
        this.logger = logger;
        this.option = option;
    }

    @Override
    public void add(JsonElement json) {
        JsonObject obj = (JsonObject) json;

        logger = obj.get("logger").getAsString();
        if (!obj.get("option").isJsonNull())
            option = obj.get("option").getAsString();
    }

    @Override
    public JsonElement toJson() {
        JsonObject entry = new JsonObject();

        entry.addProperty("logger", logger);
        entry.addProperty("option", option);

        return entry;
    }
}
